import os
os.environ["OPENBLAS_NUM_THREADS"] = "1"  # Prevent memory issues with multithreaded BLAS

import pandas as pd
import numpy as np
import json
import networkx as nx
from sklearn.preprocessing import LabelEncoder
from sklearn.feature_selection import mutual_info_classif
from pgmpy.models import DiscreteBayesianNetwork
from pgmpy.estimators import HillClimbSearch, BIC, BayesianEstimator
from pgmpy.inference import VariableElimination

# Step 1: Load and preprocess
df = pd.read_csv("Discretized_Dataset_with_Context.csv").dropna()

ordinal_mapping = {'low': 0, 'medium': 1, 'high': 2}
ordinal_columns = [
    'Heart rate', 'Systolic Blood Pressure', 'Diastolic Blood Pressure',
    'Respiratory rate', 'Body temperature',
    'Pain severity - 0-10 verbal numeric rating [Score] - Reported', 'age'
]

for col in ordinal_columns:
    if col in df.columns:
        df[col] = df[col].map(ordinal_mapping)

# Label encode strings
label_encoders = {}
for col in df.select_dtypes(include="object").columns:
    le = LabelEncoder()
    df[col] = le.fit_transform(df[col])
    label_encoders[col] = le

# Step 2: Feature selection
X = df.drop(columns=["triage_level"])
y = df["triage_level"]
mi = mutual_info_classif(X, y, discrete_features=True)
mi_df = pd.DataFrame({"Feature": X.columns, "MI": mi}).sort_values(by="MI", ascending=False)

# Exclude dominant variables
exclude_vars = {
    "has_not_in_labor_force_finding",
    "has_full-time_employment_finding",
    "has_part-time_employment_finding",
    "has_only_received_primary_school_education_finding",
    "has_received_certificate_of_high_school_equivalency_finding",
    "has_received_higher_education_finding",
    "has_served_in_armed_forces_finding",
    "has_housing_unsatisfactory_finding",
    "has_transport_problems_finding",
    "has_lack_of_access_to_transportation_finding",
    "has_social_isolation_finding",
    "has_limited_social_contact_finding",
    "has_refugee_person",
    "has_has_a_criminal_record_finding",
    "has_risk_activity_involvement_finding",
    "has_unemployed_finding",
    "has_male_infertility",
    "has_victim_of_intimate_partner_abuse_finding",
    "has_misuses_drugs_finding",
    "has_unhealthy_alcohol_drinking_behavior_finding"
}
mi_df = mi_df[~mi_df["Feature"].isin(exclude_vars)]

# Keep top 50 features + always include vital/contextual features
always_include = [
    'Heart rate', 'Systolic Blood Pressure', 'condition_context'
]
selected_features = list(mi_df["Feature"].head(50).unique())
for f in always_include:
    if f not in selected_features:
        selected_features.append(f)

selected_columns = selected_features + ["triage_level"]
df = df[selected_columns]
selected_columns = selected_features + ["triage_level"]
print("Selected features for modeling:")
print(selected_columns)

# Step 3: Structure learning
hc = HillClimbSearch(df)
model_structure = hc.estimate(scoring_method=BIC(df), max_indegree=3)

# Add manual edges if needed
for parent in always_include:
    if (parent, 'triage_level') not in model_structure.edges():
        model_structure.add_edge(parent, 'triage_level')

# Ensure DAG
while not nx.is_directed_acyclic_graph(model_structure):
    cycle = list(nx.simple_cycles(model_structure))[0]
    model_structure.remove_edge(cycle[0], cycle[1])

# Step 4: Fit model
model = DiscreteBayesianNetwork(model_structure.edges())
model.fit(df, estimator=BayesianEstimator, prior_type="BDeu", equivalent_sample_size=50)

# Step 5: Inference & export
infer = VariableElimination(model)

def run_inference(name, evidence_dict):
    if all(k in model.nodes() for k in evidence_dict):
        result = infer.query(variables=["triage_level"], evidence=evidence_dict)
        print(f"\n{name} Evidence: {evidence_dict}")
        print(result)
    else:
        print(f"\n{name} evidence skipped: invalid keys")

# Staged testing
run_inference("Low", {'Heart rate': 1, 'Systolic Blood Pressure': 1, 'condition_context': 4,'has_myocardial_infarction':0})
run_inference("Moderate", {'Heart rate': 1, 'Systolic Blood Pressure': 1, 'condition_context': 2,'has_cardiac_arrest':1}) # Note: 'has_' might be a typo or incomplete feature name
run_inference("High", {'Heart rate': 2, 'Systolic Blood Pressure': 0, 'condition_context': 0,"has_cardiac_arrest":1})

# Step 6: Export model
export_model = {
    "nodes": list(model.nodes()),
    "edges": list(model.edges()),
    "cpds": [
        {
            "variable": cpd.variable,
            "values": cpd.values.tolist(),
            "evidence": list(cpd.variables[1:]),
            "state_names": {k: list(map(str, v)) for k, v in cpd.state_names.items()}
        } for cpd in model.get_cpds()
    ]
}
with open("learned_bn_model.json", "w") as f:
    json.dump(export_model, f, indent=2)

print("\nModel trained and exported successfully.")
print("\nTop MI Features Used:")
print(mi_df.head(12))