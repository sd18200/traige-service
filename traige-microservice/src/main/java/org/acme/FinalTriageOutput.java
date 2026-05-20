package org.acme;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "logisticRegressionResult",
        "bayesianNetResult",
        "llmTriageResult"
})
public class FinalTriageOutput {
    public FinalTriageRequest.LogisticRegressionTriageResult getLogisticRegressionResult() {
        return logisticRegressionResult;
    }

    public void setLogisticRegressionResult(FinalTriageRequest.LogisticRegressionTriageResult logisticRegressionResult) {
        this.logisticRegressionResult = logisticRegressionResult;
    }

    public FinalTriageRequest.BayesianNetTriageResult getBayesianNetResult() {
        return bayesianNetResult;
    }

    public void setBayesianNetResult(FinalTriageRequest.BayesianNetTriageResult bayesianNetResult) {
        this.bayesianNetResult = bayesianNetResult;
    }

    public LLMTriageOutput getLlmTriageResult() {
        return llmTriageResult;
    }

    public void setLlmTriageResult(LLMTriageOutput llmTriageResult) {
        this.llmTriageResult = llmTriageResult;
    }

    FinalTriageRequest.LogisticRegressionTriageResult logisticRegressionResult;
    FinalTriageRequest.BayesianNetTriageResult bayesianNetResult;
    LLMTriageOutput llmTriageResult;
}
