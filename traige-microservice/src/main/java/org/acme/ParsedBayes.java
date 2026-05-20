package org.acme;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ParsedBayes {

    @JsonProperty("heart_rate")
    private Integer heartRate;

    @JsonProperty("respiratory_rate")
    private Integer respiratoryRate;

    @JsonProperty("systolic_bp")
    private Integer systolicBp;

    @JsonProperty("diastolic_bp")
    private Integer diastolicBp;

    @JsonProperty("bodyTemperature")
    private Float bodyTemperature;

    @JsonProperty("pain_score")
    private Integer painScore;

    @JsonProperty("bodyHeight")
    private Float bodyHeight;

    @JsonProperty("bodyWeight")
    private Float bodyWeight;

    @JsonProperty("bodyMassIndex")
    private Float bodyMassIndex;

    @JsonProperty("symptoms")
    private List<String> symptoms;

    @JsonProperty("risk_conditions")
    private List<String> riskConditions;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("gender")
    private String gender; // "male", "female", or null

    @JsonProperty("encounter_context")
    private String encounterContext; // "emergency", "inpatient", etc.

    @JsonProperty("condition_context")
    private String conditionContext; // "mental_health", "mixed", etc.

    // --- Getters and Setters ---

    public Integer getHeartRate() { return heartRate; }
    public void setHeartRate(Integer heartRate) { this.heartRate = heartRate; }

    public Integer getRespiratoryRate() { return respiratoryRate; }
    public void setRespiratoryRate(Integer respiratoryRate) { this.respiratoryRate = respiratoryRate; }

    public Integer getSystolicBp() { return systolicBp; }
    public void setSystolicBp(Integer systolicBp) { this.systolicBp = systolicBp; }

    public Integer getDiastolicBp() { return diastolicBp; }
    public void setDiastolicBp(Integer diastolicBp) { this.diastolicBp = diastolicBp; }

    public Float getBodyTemperature() { return bodyTemperature; }
    public void setBodyTemperature(Float bodyTemperature) { this.bodyTemperature = bodyTemperature; }

    public Integer getPainScore() { return painScore; }
    public void setPainScore(Integer painScore) { this.painScore = painScore; }

    public Float getBodyHeight() { return bodyHeight; }
    public void setBodyHeight(Float bodyHeight) { this.bodyHeight = bodyHeight; }

    public Float getBodyWeight() { return bodyWeight; }
    public void setBodyWeight(Float bodyWeight) { this.bodyWeight = bodyWeight; }

    public Float getBodyMassIndex() { return bodyMassIndex; }
    public void setBodyMassIndex(Float bodyMassIndex) { this.bodyMassIndex = bodyMassIndex; }

    public List<String> getSymptoms() { return symptoms; }
    public void setSymptoms(List<String> symptoms) { this.symptoms = symptoms; }

    public List<String> getRiskConditions() { return riskConditions; }
    public void setRiskConditions(List<String> riskConditions) { this.riskConditions = riskConditions; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getEncounterContext() { return encounterContext; }
    public void setEncounterContext(String encounterContext) { this.encounterContext = encounterContext; }

    public String getConditionContext() { return conditionContext; }
    public void setConditionContext(String conditionContext) { this.conditionContext = conditionContext; }
}
