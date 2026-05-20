package org.acme;

import cc.kave.repackaged.jayes.BayesNet;
import cc.kave.repackaged.jayes.BayesNode;
import cc.kave.repackaged.jayes.inference.IBayesInferer;
import cc.kave.repackaged.jayes.inference.junctionTree.JunctionTreeAlgorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;



@ApplicationScoped
public class TriageBayesianNetworkService {

    public static final List<String> knownConditionsBayes = List.of(
            "has_hypertension",
            "has_stroke",
            "has_sepsis_disorder",
            "has_pneumonia",
            "has_dyspnea_finding",
            "has_diabetes",
            "has_myocardial_infarction",
            "has_chronic_congestive_heart_failure_disorder",
            "has_fever_finding",
            "has_cardiac_arrest",
            "has_history_of_cardiac_arrest_situation",
            "has_history_of_myocardial_infarction_situation",
            "has_stress_finding",
            "has_not_in_labor_force_finding",
            "has_full-time_employment_finding",
            "has_victim_of_intimate_partner_abuse_finding",
            "has_part-time_employment_finding",
            "has_coronary_heart_disease",
            "has_limited_social_contact_finding",
            "has_hypoxemia_disorder",
            "has_pneumonia_disorder",
            "has_respiratory_distress_finding",
            "has_social_isolation_finding",
            "has_childhood_asthma",
            "has_reports_of_violence_in_the_environment_finding",
            "has_has_a_criminal_record_finding",
            "has_diabetic_renal_disease_disorder",
            "has_prediabetes",
            "has_chronic_kidney_disease_stage_1_disorder"
    );



    private final Map<String, BayesNode> nodeMap = new HashMap<>();
    private IBayesInferer inferer;



    public TriageBayesianNetworkService() {
        try {
            loadNetwork("learned_bn_model.json");

            System.out.println("Bayesian network loaded successfully with " + nodeMap.size() + " nodes.");

        } catch (Exception e) {
            System.err.println("Failed to load Bayesian Network: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadNetwork(String jsonPath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream in = getClass().getClassLoader().getResourceAsStream(jsonPath);
        if (in == null) {
            throw new FileNotFoundException("Could not find " + jsonPath + " in resources");
        }

        JsonNode root = mapper.readTree(in);
        BayesNet bayesNet = new BayesNet();

        // 1. Create all nodes and assign outcomes
        for (JsonNode cpd : root.get("cpds")) {
            String varName = cpd.get("variable").asText();
            JsonNode stateNames = cpd.get("state_names").get(varName);

            BayesNode node = bayesNet.createNode(varName);
            List<String> outcomes = new ArrayList<>();
            for (JsonNode state : stateNames) {
                outcomes.add(state.asText());
            }
            node.addOutcomes(outcomes.toArray(new String[0]));
            nodeMap.put(varName, node);
        }

        // 2. Add all edges
        for (JsonNode edge : root.get("edges")) {
            BayesNode parent = nodeMap.get(edge.get(0).asText());
            BayesNode child = nodeMap.get(edge.get(1).asText());
            List<BayesNode> parents = new ArrayList<>(child.getParents());
            parents.add(parent);
            child.setParents(parents);
        }

        // 3. Set all CPDs (flatten the 2D/ND arrays from JSON)
        for (JsonNode cpdNode : root.get("cpds")) {
            String varName = cpdNode.get("variable").asText();
            BayesNode node = nodeMap.get(varName);

            List<Double> flatProbabilities = new ArrayList<>();
            flattenProbabilities(cpdNode.get("values"), flatProbabilities);
            double[] probs = flatProbabilities.stream().mapToDouble(Double::doubleValue).toArray();
            node.setProbabilities(probs);
        }

        // 4. Initialize inference engine
        inferer = new JunctionTreeAlgorithm();
        inferer.setNetwork(bayesNet);

        // 5. Show priors for triage_level
        BayesNode triageNode = nodeMap.get("triage_level");
        double[] beliefs = inferer.getBeliefs(triageNode);
        List<String> outcomes = triageNode.getOutcomes();

        System.out.println("🔍 Prior beliefs over triage_level:");
        for (int i = 0; i < beliefs.length; i++) {
            System.out.printf("Class %s: %.4f%n", outcomes.get(i), beliefs[i]);
        }
    }



    private double[] flattenNestedArray(ArrayNode nestedArray) {
        List<Double> flatList = new ArrayList<>();
        flatten(nestedArray, flatList);
        return flatList.stream().mapToDouble(Double::doubleValue).toArray();
    }

    private void flatten(JsonNode node, List<Double> result) {
        if (node.isNumber()) {
            result.add(node.asDouble());
        } else {
            for (JsonNode child : node) {
                flatten(child, result);
            }
        }
    }



    private void flattenProbabilities(JsonNode node, List<Double> result) {
        if (node.isArray() && node.get(0).isArray()) {
            for (JsonNode child : node) {
                flattenProbabilities(child, result);
            }
        } else if (node.isArray()) {
            for (JsonNode val : node) {
                result.add(val.asDouble());
            }
        } else {
            result.add(node.asDouble());
        }
    }



    public Map<String, Double> predictWithBayesianNet(Map<String, String> rawEvidence, String queryNode) {
        Map<BayesNode, String> evidence = new HashMap<>();

        for (Map.Entry<String, String> entry : rawEvidence.entrySet()) {
            String variableName = entry.getKey();
            String stateValue = entry.getValue();

            BayesNode node = nodeMap.get(variableName);
            if (node == null) {
                System.out.println("⚠️ No node found for variable: " + variableName);
                continue;
            }

            // Ensure state values are strings and exist in the node outcomes
            if (node.getOutcomes().contains(stateValue)) {
                evidence.put(node, stateValue);
            } else {
                System.out.printf("⚠️ Skipped evidence: %s=%s not in outcomes %s%n",
                        variableName, stateValue, node.getOutcomes());
            }
        }

        System.out.println("Final evidence passed to inference engine: " + evidence);

        BayesNode query = nodeMap.get(queryNode);
        if (query == null) {
            throw new IllegalArgumentException("Query node '" + queryNode + "' not found.");
        }

        inferer.setEvidence(evidence);
        double[] beliefs = inferer.getBeliefs(query);

        Map<String, Double> result = new LinkedHashMap<>();
        List<String> outcomes = query.getOutcomes();
        for (int i = 0; i < outcomes.size(); i++) {
            result.put(outcomes.get(i), beliefs[i]);
        }

        return result;
    }



    Map<String, Integer> conditionContextMap = Map.of(
            "cardiac_related", 0,
            "diabetes_related", 1,
            "mental_health", 2,
            "mixed", 3,
            "none", 4,
            "unknown", 5
    );


    private boolean isOrdinal(String feature) {
        return Set.of(
                "Heart rate", "Systolic Blood Pressure", "Diastolic Blood Pressure",
                "Respiratory rate", "Pain severity - 0-10 verbal numeric rating [Score] - Reported"
        ).contains(feature);
    }

    private int discretize(int value, String feature) {
        // Customize thresholds if you used something different in Python
        switch (feature) {
            case "Heart rate":
            case "Respiratory rate":
            case "Pain severity - 0-10 verbal numeric rating [Score] - Reported":
                if (value < 90) return 0;       // low
                else if (value < 110) return 1; // medium
                else return 2;                  // high
            case "Systolic Blood Pressure":
            case "Diastolic Blood Pressure":
                if (value < 100) return 0;
                else if (value < 130) return 1;
                else return 2;
            default:
                return 1;
        }
    }




}