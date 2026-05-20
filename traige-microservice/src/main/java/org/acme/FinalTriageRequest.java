package org.acme;

public class FinalTriageRequest {

    public LogisticRegressionTriageResult getLogisticRegressionResult() {
        return logisticRegressionResult;
    }

    public void setLogisticRegressionResult(LogisticRegressionTriageResult logisticRegressionResult) {
        this.logisticRegressionResult = logisticRegressionResult;
    }

    public BayesianNetTriageResult getBayesianNetResult() {
        return bayesianNetResult;
    }

    public void setBayesianNetResult(BayesianNetTriageResult bayesianNetResult) {
        this.bayesianNetResult = bayesianNetResult;
    }

    private LogisticRegressionTriageResult logisticRegressionResult;
    private BayesianNetTriageResult bayesianNetResult;


    public static class LogisticRegressionTriageResult {
        public String getFinalLogisticTriage() {
            return finalLogisticTriage;
        }

        public void setFinalLogisticTriage(String finalLogisticTriage) {
            this.finalLogisticTriage = finalLogisticTriage;
        }

        public String getUrgentLRTriageProbabllity() {
            return urgentLRTriageProbabllity;
        }

        public void setUrgentLRTriageProbabllity(String urgentLRTriageProbabllity) {
            this.urgentLRTriageProbabllity = urgentLRTriageProbabllity;
        }

        public String getMediumLRTriageProbabllity() {
            return mediumLRTriageProbabllity;
        }

        public void setMediumLRTriageProbabllity(String mediumLRTriageProbabllity) {
            this.mediumLRTriageProbabllity = mediumLRTriageProbabllity;
        }

        public String getLowLRTriageProbabllity() {
            return lowLRTriageProbabllity;
        }

        public void setLowLRTriageProbabllity(String lowLRTriageProbabllity) {
            this.lowLRTriageProbabllity = lowLRTriageProbabllity;
        }

        private String finalLogisticTriage;
        private String urgentLRTriageProbabllity;
        private String mediumLRTriageProbabllity;
        private String lowLRTriageProbabllity;
    }


    public static class BayesianNetTriageResult {
        private String finalBayesianNetTriage;

        public String getFinalBayesianNetTriage() {
            return finalBayesianNetTriage;
        }

        public void setFinalBayesianNetTriage(String finalBayesianNetTriage) {
            this.finalBayesianNetTriage = finalBayesianNetTriage;
        }

        public String getUrgentBNTriageProbabllity() {
            return urgentBNTriageProbabllity;
        }

        public void setUrgentBNTriageProbabllity(String urgentBNTriageProbabllity) {
            this.urgentBNTriageProbabllity = urgentBNTriageProbabllity;
        }

        public String getMediumBNTriageProbabllity() {
            return mediumBNTriageProbabllity;
        }

        public void setMediumBNTriageProbabllity(String mediumBNTriageProbabllity) {
            this.mediumBNTriageProbabllity = mediumBNTriageProbabllity;
        }

        public String getLowBNTriageProbabllity() {
            return lowBNTriageProbabllity;
        }

        public void setLowBNTriageProbabllity(String lowBNTriageProbabllity) {
            this.lowBNTriageProbabllity = lowBNTriageProbabllity;
        }

        private String urgentBNTriageProbabllity;
        private String mediumBNTriageProbabllity;
        private String lowBNTriageProbabllity;
    }

}
