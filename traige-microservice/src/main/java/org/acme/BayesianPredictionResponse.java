package org.acme;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class BayesianPredictionResponse {


    @JsonProperty("evidence_received")
    private Map<String, String> evidenceReceived;


    @JsonProperty("predicted_triage_level")
    private String predictedTriageLevel;


    @JsonProperty("triage_level_probabilities")
    private Map<String, Double> triageLevelProbabilities;

    public BayesianPredictionResponse() {
    }



    public Map<String, String> getEvidenceReceived() {
        return evidenceReceived;
    }

    public void setEvidenceReceived(Map<String, String> evidenceReceived) {
        this.evidenceReceived = evidenceReceived;
    }

    public String getPredictedTriageLevel() {
        return predictedTriageLevel;
    }

    public void setPredictedTriageLevel(String predictedTriageLevel) {
        this.predictedTriageLevel = predictedTriageLevel;
    }

    public Map<String, Double> getTriageLevelProbabilities() {
        return triageLevelProbabilities;
    }

    public void setTriageLevelProbabilities(Map<String, Double> triageLevelProbabilities) {
        this.triageLevelProbabilities = triageLevelProbabilities;
    }

    // Optional: toString() for debugging
    @Override
    public String toString() {
        return "PredictionResponse{" +
                "evidenceReceived=" + evidenceReceived +
                ", predictedTriageLevel='" + predictedTriageLevel + '\'' +
                ", triageLevelProbabilities=" + triageLevelProbabilities +
                '}';
    }
}