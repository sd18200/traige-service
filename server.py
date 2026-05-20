import os
os.environ["OPENBLAS_NUM_THREADS"] = "1" # Mitigate potential BLAS threading issues

import json
import numpy as np
from pgmpy.models import DiscreteBayesianNetwork
from pgmpy.factors.discrete import TabularCPD
from pgmpy.inference import VariableElimination
from flask import Flask, request, jsonify

# --- Configuration ---
MODEL_FILE = "learned_bn_model.json"
SERVER_HOST = '0.0.0.0' # Listen on all network interfaces
SERVER_PORT = 5000      # Port for the API

# --- Model Loading Function ---
def load_model_from_json(filepath):
    """Loads a pgmpy DiscreteBayesianNetwork model from a JSON file."""
    try:
        with open(filepath, 'r') as f:
            model_dict = json.load(f)
    except FileNotFoundError:
        print(f"Error: Model file not found at {filepath}")
        raise
    except json.JSONDecodeError:
        print(f"Error: Could not decode JSON from {filepath}")
        raise

    try:
        model = DiscreteBayesianNetwork(ebunch=model_dict['edges'])

        cpds = []
        for cpd_dict in model_dict['cpds']:
            variable = cpd_dict['variable']
            # Ensure evidence is None if empty list, required by TabularCPD
            evidence = cpd_dict['evidence'] if cpd_dict['evidence'] else None
            # Load values as numpy array first
            values_flat = np.array(cpd_dict['values'])
            # Ensure state names are loaded correctly (pgmpy expects lists of strings/ints)
            state_names = {var: list(names) for var, names in cpd_dict['state_names'].items()}

            variable_card = len(state_names[variable])
            evidence_card = [len(state_names[e]) for e in evidence] if evidence else []

            # --- Start Change ---
            # Calculate the expected shape for the CPD values
            # Shape should be (variable_card, product of evidence_card)
            num_evidence_states = np.prod(evidence_card) if evidence else 1
            expected_shape = (variable_card, int(num_evidence_states)) # Ensure num_evidence_states is int

            # Reshape the flattened values array
            # Use Fortran order ('F') for reshaping as pgmpy stores values column-major
            values_reshaped = values_flat.reshape(expected_shape, order='F')
            # --- End Change ---


            # Create the CPD object using the reshaped values
            cpd = TabularCPD(variable=variable,
                             variable_card=variable_card,
                             values=values_reshaped, # Use the reshaped array
                             evidence=evidence,
                             evidence_card=evidence_card,
                             state_names=state_names)
            cpds.append(cpd)

        model.add_cpds(*cpds)

        # Validate the model structure and CPDs
        model.check_model()
        print(f"✅ Model loaded and validated successfully from {filepath}")
        return model

    except Exception as e:
        print(f"❌ Error constructing pgmpy model from loaded data: {e}")
        # Print more details for debugging if needed
        import traceback
        traceback.print_exc()
        raise


try:
    bayesian_model = load_model_from_json(MODEL_FILE)
    inference_engine = VariableElimination(bayesian_model)
    MODEL_NODES = set(bayesian_model.nodes())
except Exception:
    bayesian_model = None
    inference_engine = None
    MODEL_NODES = set()
    print("🚨 Model loading failed. Prediction endpoint will not be available.")

app = Flask(__name__)

@app.route('/predict', methods=['POST'])
def predict():
    """REST endpoint to perform triage level prediction."""
    if not inference_engine:
        return jsonify({"error": "Model not loaded. Prediction service unavailable."}), 503

    if not request.is_json:
        return jsonify({"error": "Request must be JSON"}), 400

    evidence_dict = request.get_json()

    if not isinstance(evidence_dict, dict):
        return jsonify({"error": "JSON payload must be an object/dictionary"}), 400

    # --- Validate keys ---
    invalid_keys = set(evidence_dict.keys()) - MODEL_NODES
    if invalid_keys:
        valid_keys_list = sorted(list(MODEL_NODES))
        error_message = f"Invalid evidence key(s): {list(invalid_keys)}. Valid keys include: {valid_keys_list}"
        return jsonify({"error": error_message}), 400

    try:
        # Perform inference using the globally loaded engine
        result_factor = inference_engine.query(variables=["triage_level"], evidence=evidence_dict)

        # --- Start Change: Map probabilities to descriptive keys ---
        # Define the mapping from state name (as string) to desired key
        triage_key_mapping = {
            "0": "lowTriageProb",
            "1": "medTriageProb",
            "2": "UrgentTriageProb"
            # Add more mappings if your triage_level has more states
        }

        # Create the probability dictionary using the mapping
        prediction_probs_mapped = {}
        most_likely_state = None
        max_prob = -1.0

        for state, prob in zip(result_factor.state_names['triage_level'], result_factor.values):
            state_str = str(state) # Ensure state is a string for mapping lookup
            descriptive_key = triage_key_mapping.get(state_str, f"unknownState_{state_str}") # Use mapping, fallback if state not in map
            prediction_probs_mapped[descriptive_key] = prob

            # Also find the state with the highest probability to determine predicted_level
            if prob > max_prob:
                max_prob = prob
                most_likely_state = state_str # Store the original state name ("0", "1", "2")

        # --- End Change ---

        return jsonify({
            "evidence_received": evidence_dict,
            "triage_level_probabilities": prediction_probs_mapped, # Use the mapped dictionary
            "predicted_triage_level": most_likely_state # Return the original state name ("0", "1", or "2")
        })

    except ValueError as ve:
         # Catch potential errors from pgmpy if evidence is inconsistent or invalid state names/values
         print(f"⚠️ Inference ValueError: {ve}")
         # Provide a more specific error if possible (pgmpy errors can be verbose)
         error_msg = f"Inconsistent evidence or invalid value provided: {ve}"
         # Check for common state name/value errors
         if "State names" in str(ve) or "value" in str(ve):
             error_msg = f"Invalid value provided for one or more evidence variables. Please check input. Details: {ve}"
         return jsonify({"error": error_msg}), 400
    except Exception as e:
        # Log the error for server-side debugging
        print(f"❌ Unexpected error during inference: {e}")
        import traceback
        traceback.print_exc()
        # Return a generic server error to the client
        return jsonify({"error": "An internal error occurred during prediction."}), 500

@app.route('/health', methods=['GET'])
def health_check():
    """Basic health check endpoint."""
    if inference_engine:
        return jsonify({"status": "UP", "model_loaded": True}), 200
    else:
        return jsonify({"status": "DOWN", "model_loaded": False}), 503

# --- Main Execution ---
if __name__ == '__main__':
    if inference_engine: # Only start the server if the model loaded successfully
        print(f"🚀 Starting Flask server on http://{SERVER_HOST}:{SERVER_PORT}")
        # Use debug=False for production environments
        app.run(host=SERVER_HOST, port=SERVER_PORT, debug=False)
    else:
        print("❌ Server not started due to model loading failure.")