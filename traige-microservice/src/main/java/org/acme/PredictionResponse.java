package org.acme;

public class PredictionResponse {
    public int getPredictedTriageLevel() {
        return predictedTriageLevel;
    }

    public void setPredictedTriageLevel(int predictedTriageLevel) {
        this.predictedTriageLevel = predictedTriageLevel;
    }

    public double[] getProbabilities() {
        return probabilities;
    }

    public void setProbabilities(double[] probabilities) {
        this.probabilities = probabilities;
    }

    public int predictedTriageLevel;
    public double[] probabilities;

    public PredictionResponse(int level, double[] probs) {
        this.predictedTriageLevel = level;
        this.probabilities = probs;
    }
}
