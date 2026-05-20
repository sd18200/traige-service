package org.acme;

import java.util.List;

public class ParsedTriageData {

    private Integer heart_rate;
    private Integer respiratory_rate;
    private Integer systolic_bp;
    private Integer diastolic_bp;
    private Integer pain_score;
    private List<String> symptoms;
    private List<String> risk_conditions;
    private Integer age;
    private String encounter_context;

    public Double getBodyHeight() {
        return bodyHeight;
    }

    public void setBodyHeight(Double bodyHeight) {
        this.bodyHeight = bodyHeight;
    }

    public Double getBodyWeight() {
        return bodyWeight;
    }

    public void setBodyWeight(Double bodyWeight) {
        this.bodyWeight = bodyWeight;
    }

    public Double getBodyTemperature() {
        return bodyTemperature;
    }

    public void setBodyTemperature(Double bodyTemperature) {
        this.bodyTemperature = bodyTemperature;
    }

    public Double getBodyMassIndex() {
        return bodyMassIndex;
    }

    public void setBodyMassIndex(Double bodyMassIndex) {
        this.bodyMassIndex = bodyMassIndex;
    }

    private Double bodyHeight;
    private Double bodyWeight;
    private Double bodyTemperature;
    private Double bodyMassIndex;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    private String gender;

    // Getters and Setters

    public Integer getHeart_rate() {
        return heart_rate;
    }

    public void setHeart_rate(Integer heart_rate) {
        this.heart_rate = heart_rate;
    }

    public Integer getRespiratory_rate() {
        return respiratory_rate;
    }

    public void setRespiratory_rate(Integer respiratory_rate) {
        this.respiratory_rate = respiratory_rate;
    }

    public Integer getSystolic_bp() {
        return systolic_bp;
    }

    public void setSystolic_bp(Integer systolic_bp) {
        this.systolic_bp = systolic_bp;
    }

    public Integer getDiastolic_bp() {
        return diastolic_bp;
    }

    public void setDiastolic_bp(Integer diastolic_bp) {
        this.diastolic_bp = diastolic_bp;
    }

    public Integer getPain_score() {
        return pain_score;
    }

    public void setPain_score(Integer pain_score) {
        this.pain_score = pain_score;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    public List<String> getRisk_conditions() {
        return risk_conditions;
    }

    public void setRisk_conditions(List<String> risk_conditions) {
        this.risk_conditions = risk_conditions;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEncounter_context() {
        return encounter_context;
    }

    public void setEncounter_context(String encounter_context) {
        this.encounter_context = encounter_context;
    }

    @Override
    public String toString() {
        return "ParsedTriageData{" +
                "heart_rate=" + heart_rate +
                ", respiratory_rate=" + respiratory_rate +
                ", systolic_bp=" + systolic_bp +
                ", diastolic_bp=" + diastolic_bp +
                ", pain_score=" + pain_score +
                ", symptoms=" + symptoms +
                ", risk_conditions=" + risk_conditions +
                ", age=" + age +
                ", encounter_context='" + encounter_context + '\'' +
                '}';
    }

    public double[] toFeatureVector() {
        return new double[]{
                age != null ? age : 0,
                heart_rate != null ? heart_rate : 0,
                systolic_bp != null ? systolic_bp : 0,
                diastolic_bp != null ? diastolic_bp : 0,
                respiratory_rate != null ? respiratory_rate : 0,
                pain_score != null ? pain_score : 0,
                bodyHeight != null ? bodyHeight : 0,
                bodyWeight != null ? bodyWeight : 0,
                bodyTemperature != null ? bodyTemperature : 0,
                bodyMassIndex != null ? bodyMassIndex : 0
        };
    }


}
