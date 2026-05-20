package org.acme;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BayesianRequest {

    @JsonProperty("has_hypertension")
    private String hasHypertension;

    @JsonProperty("condition_context")
    private String conditionContext;

    @JsonProperty("has_stroke")
    private String hasStroke;

    @JsonProperty("has_sepsis_disorder")
    private String hasSepsisDisorder;

    @JsonProperty("has_cardiac_arrest")
    private String hasCardiacArrest;

    @JsonProperty("has_history_of_cardiac_arrest_situation")
    private String hasHistoryOfCardiacArrestSituation;

    @JsonProperty("Systolic Blood Pressure")
    private String systolicBloodPressure;

    @JsonProperty("age")
    private String age;

    @JsonProperty("has_history_of_myocardial_infarction_situation")
    private String hasHistoryOfMyocardialInfarctionSituation;

    @JsonProperty("has_myocardial_infarction")
    private String hasMyocardialInfarction;

    @JsonProperty("has_stress_finding")
    private String hasStressFinding;

    @JsonProperty("Body Mass Index")
    private String bodyMassIndex;

    @JsonProperty("Body Weight")
    private String bodyWeight;

    @JsonProperty("has_coronary_heart_disease")
    private String hasCoronaryHeartDisease;

    @JsonProperty("has_respiratory_distress_finding")
    private String hasRespiratoryDistressFinding;

    @JsonProperty("has_pneumonia_disorder")
    private String hasPneumoniaDisorder;

    @JsonProperty("has_hypoxemia_disorder")
    private String hasHypoxemiaDisorder;

    @JsonProperty("Body Height")
    private String bodyHeight;

    @JsonProperty("has_childhood_asthma")
    private String hasChildhoodAsthma;

    @JsonProperty("has_reports_of_violence_in_the_environment_finding")
    private String hasReportsOfViolenceInTheEnvironmentFinding;

    @JsonProperty("has_diabetic_renal_disease_disorder")
    private String hasDiabeticRenalDiseaseDisorder;

    @JsonProperty("has_pneumonia")
    private String hasPneumonia;

    @JsonProperty("has_prediabetes")
    private String hasPrediabetes;

    @JsonProperty("has_chronic_kidney_disease_stage_1_disorder")
    private String hasChronicKidneyDiseaseStage1Disorder;

    @JsonProperty("has_septic_shock_disorder")
    private String hasSepticShockDisorder;

    @JsonProperty("has_acute_respiratory_failure_disorder")
    private String hasAcuteRespiratoryFailureDisorder;

    @JsonProperty("has_alzheimer's_disease_disorder") // Note: Apostrophe might cause issues depending on JSON library strictness. Test carefully.
    private String hasAlzheimersDiseaseDisorder;

    @JsonProperty("has_suspected_covid-19")
    private String hasSuspectedCovid19;

    @JsonProperty("has_anemia_disorder")
    private String hasAnemiaDisorder;

    @JsonProperty("has_hypertriglyceridemia_disorder")
    private String hasHypertriglyceridemiaDisorder;

    @JsonProperty("has_diabetes")
    private String hasDiabetes;

    @JsonProperty("has_metabolic_syndrome_x_disorder")
    private String hasMetabolicSyndromeXDisorder;

    @JsonProperty("has_covid-19")
    private String hasCovid19;

    @JsonProperty("Respiratory rate")
    private String respiratoryRate;

    @JsonProperty("has_osteoporosis_disorder")
    private String hasOsteoporosisDisorder;

    @JsonProperty("has_sepsis_caused_by_virus_disorder")
    private String hasSepsisCausedByVirusDisorder;

    @JsonProperty("has_acute_pulmonary_embolism_disorder")
    private String hasAcutePulmonaryEmbolismDisorder;

    @JsonProperty("has_pathological_fracture_due_to_osteoporosis_disorder")
    private String hasPathologicalFractureDueToOsteoporosisDisorder;

    @JsonProperty("has_otitis_media")
    private String hasOtitisMedia;

    @JsonProperty("has_fever_finding")
    private String hasFeverFinding;

    @JsonProperty("Diastolic Blood Pressure")
    private String diastolicBloodPressure;

    @JsonProperty("ENCOUNTERCLASS_urgentcare")
    private String encounterclassUrgentcare; // Renamed for Java convention

    @JsonProperty("has_neoplasm_of_prostate")
    private String hasNeoplasmOfProstate;

    @JsonProperty("Heart rate")
    private String heartRate;

    @JsonProperty("has_acute_respiratory_distress_syndrome_disorder")
    private String hasAcuteRespiratoryDistressSyndromeDisorder;

    @JsonProperty("has_carcinoma_in_situ_of_prostate_disorder")
    private String hasCarcinomaInSituOfProstateDisorder;

    @JsonProperty("has_cough_finding")
    private String hasCoughFinding;

    @JsonProperty("has_neuropathy_due_to_type_2_diabetes_mellitus_disorder")
    private String hasNeuropathyDueToType2DiabetesMellitusDisorder;

    @JsonProperty("has_sore_throat_symptom_finding")
    private String hasSoreThroatSymptomFinding;

    @JsonProperty("has_hyperglycemia_disorder")
    private String hasHyperglycemiaDisorder;

    @JsonProperty("triage_level")
    private String triageLevel;



    public BayesianRequest() {
    }

    public String getHasHypertension() { return hasHypertension; }
    public void setHasHypertension(String hasHypertension) { this.hasHypertension = hasHypertension; }

    public String getConditionContext() { return conditionContext; }
    public void setConditionContext(String conditionContext) { this.conditionContext = conditionContext; }

    public String getHasStroke() { return hasStroke; }
    public void setHasStroke(String hasStroke) { this.hasStroke = hasStroke; }

    public String getHasSepsisDisorder() { return hasSepsisDisorder; }
    public void setHasSepsisDisorder(String hasSepsisDisorder) { this.hasSepsisDisorder = hasSepsisDisorder; }

    public String getHasCardiacArrest() { return hasCardiacArrest; }
    public void setHasCardiacArrest(String hasCardiacArrest) { this.hasCardiacArrest = hasCardiacArrest; }

    public String getHasHistoryOfCardiacArrestSituation() { return hasHistoryOfCardiacArrestSituation; }
    public void setHasHistoryOfCardiacArrestSituation(String hasHistoryOfCardiacArrestSituation) { this.hasHistoryOfCardiacArrestSituation = hasHistoryOfCardiacArrestSituation; }

    public String getSystolicBloodPressure() { return systolicBloodPressure; }
    public void setSystolicBloodPressure(String systolicBloodPressure) { this.systolicBloodPressure = systolicBloodPressure; }

    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }

    public String getHasHistoryOfMyocardialInfarctionSituation() { return hasHistoryOfMyocardialInfarctionSituation; }
    public void setHasHistoryOfMyocardialInfarctionSituation(String hasHistoryOfMyocardialInfarctionSituation) { this.hasHistoryOfMyocardialInfarctionSituation = hasHistoryOfMyocardialInfarctionSituation; }

    public String getHasMyocardialInfarction() { return hasMyocardialInfarction; }
    public void setHasMyocardialInfarction(String hasMyocardialInfarction) { this.hasMyocardialInfarction = hasMyocardialInfarction; }

    public String getHasStressFinding() { return hasStressFinding; }
    public void setHasStressFinding(String hasStressFinding) { this.hasStressFinding = hasStressFinding; }

    public String getBodyMassIndex() { return bodyMassIndex; }
    public void setBodyMassIndex(String bodyMassIndex) { this.bodyMassIndex = bodyMassIndex; }

    public String getBodyWeight() { return bodyWeight; }
    public void setBodyWeight(String bodyWeight) { this.bodyWeight = bodyWeight; }

    public String getHasCoronaryHeartDisease() { return hasCoronaryHeartDisease; }
    public void setHasCoronaryHeartDisease(String hasCoronaryHeartDisease) { this.hasCoronaryHeartDisease = hasCoronaryHeartDisease; }

    public String getHasRespiratoryDistressFinding() { return hasRespiratoryDistressFinding; }
    public void setHasRespiratoryDistressFinding(String hasRespiratoryDistressFinding) { this.hasRespiratoryDistressFinding = hasRespiratoryDistressFinding; }

    public String getHasPneumoniaDisorder() { return hasPneumoniaDisorder; }
    public void setHasPneumoniaDisorder(String hasPneumoniaDisorder) { this.hasPneumoniaDisorder = hasPneumoniaDisorder; }

    public String getHasHypoxemiaDisorder() { return hasHypoxemiaDisorder; }
    public void setHasHypoxemiaDisorder(String hasHypoxemiaDisorder) { this.hasHypoxemiaDisorder = hasHypoxemiaDisorder; }

    public String getBodyHeight() { return bodyHeight; }
    public void setBodyHeight(String bodyHeight) { this.bodyHeight = bodyHeight; }

    public String getHasChildhoodAsthma() { return hasChildhoodAsthma; }
    public void setHasChildhoodAsthma(String hasChildhoodAsthma) { this.hasChildhoodAsthma = hasChildhoodAsthma; }

    public String getHasReportsOfViolenceInTheEnvironmentFinding() { return hasReportsOfViolenceInTheEnvironmentFinding; }
    public void setHasReportsOfViolenceInTheEnvironmentFinding(String hasReportsOfViolenceInTheEnvironmentFinding) { this.hasReportsOfViolenceInTheEnvironmentFinding = hasReportsOfViolenceInTheEnvironmentFinding; }

    public String getHasDiabeticRenalDiseaseDisorder() { return hasDiabeticRenalDiseaseDisorder; }
    public void setHasDiabeticRenalDiseaseDisorder(String hasDiabeticRenalDiseaseDisorder) { this.hasDiabeticRenalDiseaseDisorder = hasDiabeticRenalDiseaseDisorder; }

    public String getHasPneumonia() { return hasPneumonia; }
    public void setHasPneumonia(String hasPneumonia) { this.hasPneumonia = hasPneumonia; }

    public String getHasPrediabetes() { return hasPrediabetes; }
    public void setHasPrediabetes(String hasPrediabetes) { this.hasPrediabetes = hasPrediabetes; }

    public String getHasChronicKidneyDiseaseStage1Disorder() { return hasChronicKidneyDiseaseStage1Disorder; }
    public void setHasChronicKidneyDiseaseStage1Disorder(String hasChronicKidneyDiseaseStage1Disorder) { this.hasChronicKidneyDiseaseStage1Disorder = hasChronicKidneyDiseaseStage1Disorder; }

    public String getHasSepticShockDisorder() { return hasSepticShockDisorder; }
    public void setHasSepticShockDisorder(String hasSepticShockDisorder) { this.hasSepticShockDisorder = hasSepticShockDisorder; }

    public String getHasAcuteRespiratoryFailureDisorder() { return hasAcuteRespiratoryFailureDisorder; }
    public void setHasAcuteRespiratoryFailureDisorder(String hasAcuteRespiratoryFailureDisorder) { this.hasAcuteRespiratoryFailureDisorder = hasAcuteRespiratoryFailureDisorder; }

    public String getHasAlzheimersDiseaseDisorder() { return hasAlzheimersDiseaseDisorder; }
    public void setHasAlzheimersDiseaseDisorder(String hasAlzheimersDiseaseDisorder) { this.hasAlzheimersDiseaseDisorder = hasAlzheimersDiseaseDisorder; }

    public String getHasSuspectedCovid19() { return hasSuspectedCovid19; }
    public void setHasSuspectedCovid19(String hasSuspectedCovid19) { this.hasSuspectedCovid19 = hasSuspectedCovid19; }

    public String getHasAnemiaDisorder() { return hasAnemiaDisorder; }
    public void setHasAnemiaDisorder(String hasAnemiaDisorder) { this.hasAnemiaDisorder = hasAnemiaDisorder; }

    public String getHasHypertriglyceridemiaDisorder() { return hasHypertriglyceridemiaDisorder; }
    public void setHasHypertriglyceridemiaDisorder(String hasHypertriglyceridemiaDisorder) { this.hasHypertriglyceridemiaDisorder = hasHypertriglyceridemiaDisorder; }

    public String getHasDiabetes() { return hasDiabetes; }
    public void setHasDiabetes(String hasDiabetes) { this.hasDiabetes = hasDiabetes; }

    public String getHasMetabolicSyndromeXDisorder() { return hasMetabolicSyndromeXDisorder; }
    public void setHasMetabolicSyndromeXDisorder(String hasMetabolicSyndromeXDisorder) { this.hasMetabolicSyndromeXDisorder = hasMetabolicSyndromeXDisorder; }

    public String getHasCovid19() { return hasCovid19; }
    public void setHasCovid19(String hasCovid19) { this.hasCovid19 = hasCovid19; }

    public String getRespiratoryRate() { return respiratoryRate; }
    public void setRespiratoryRate(String respiratoryRate) { this.respiratoryRate = respiratoryRate; }

    public String getHasOsteoporosisDisorder() { return hasOsteoporosisDisorder; }
    public void setHasOsteoporosisDisorder(String hasOsteoporosisDisorder) { this.hasOsteoporosisDisorder = hasOsteoporosisDisorder; }

    public String getHasSepsisCausedByVirusDisorder() { return hasSepsisCausedByVirusDisorder; }
    public void setHasSepsisCausedByVirusDisorder(String hasSepsisCausedByVirusDisorder) { this.hasSepsisCausedByVirusDisorder = hasSepsisCausedByVirusDisorder; }

    public String getHasAcutePulmonaryEmbolismDisorder() { return hasAcutePulmonaryEmbolismDisorder; }
    public void setHasAcutePulmonaryEmbolismDisorder(String hasAcutePulmonaryEmbolismDisorder) { this.hasAcutePulmonaryEmbolismDisorder = hasAcutePulmonaryEmbolismDisorder; }

    public String getHasPathologicalFractureDueToOsteoporosisDisorder() { return hasPathologicalFractureDueToOsteoporosisDisorder; }
    public void setHasPathologicalFractureDueToOsteoporosisDisorder(String hasPathologicalFractureDueToOsteoporosisDisorder) { this.hasPathologicalFractureDueToOsteoporosisDisorder = hasPathologicalFractureDueToOsteoporosisDisorder; }

    public String getHasOtitisMedia() { return hasOtitisMedia; }
    public void setHasOtitisMedia(String hasOtitisMedia) { this.hasOtitisMedia = hasOtitisMedia; }

    public String getHasFeverFinding() { return hasFeverFinding; }
    public void setHasFeverFinding(String hasFeverFinding) { this.hasFeverFinding = hasFeverFinding; }

    public String getDiastolicBloodPressure() { return diastolicBloodPressure; }
    public void setDiastolicBloodPressure(String diastolicBloodPressure) { this.diastolicBloodPressure = diastolicBloodPressure; }

    public String getEncounterclassUrgentcare() { return encounterclassUrgentcare; }
    public void setEncounterclassUrgentcare(String encounterclassUrgentcare) { this.encounterclassUrgentcare = encounterclassUrgentcare; }

    public String getHasNeoplasmOfProstate() { return hasNeoplasmOfProstate; }
    public void setHasNeoplasmOfProstate(String hasNeoplasmOfProstate) { this.hasNeoplasmOfProstate = hasNeoplasmOfProstate; }

    public String getHeartRate() { return heartRate; }
    public void setHeartRate(String heartRate) { this.heartRate = heartRate; }

    public String getHasAcuteRespiratoryDistressSyndromeDisorder() { return hasAcuteRespiratoryDistressSyndromeDisorder; }
    public void setHasAcuteRespiratoryDistressSyndromeDisorder(String hasAcuteRespiratoryDistressSyndromeDisorder) { this.hasAcuteRespiratoryDistressSyndromeDisorder = hasAcuteRespiratoryDistressSyndromeDisorder; }

    public String getHasCarcinomaInSituOfProstateDisorder() { return hasCarcinomaInSituOfProstateDisorder; }
    public void setHasCarcinomaInSituOfProstateDisorder(String hasCarcinomaInSituOfProstateDisorder) { this.hasCarcinomaInSituOfProstateDisorder = hasCarcinomaInSituOfProstateDisorder; }

    public String getHasCoughFinding() { return hasCoughFinding; }
    public void setHasCoughFinding(String hasCoughFinding) { this.hasCoughFinding = hasCoughFinding; }

    public String getHasNeuropathyDueToType2DiabetesMellitusDisorder() { return hasNeuropathyDueToType2DiabetesMellitusDisorder; }
    public void setHasNeuropathyDueToType2DiabetesMellitusDisorder(String hasNeuropathyDueToType2DiabetesMellitusDisorder) { this.hasNeuropathyDueToType2DiabetesMellitusDisorder = hasNeuropathyDueToType2DiabetesMellitusDisorder; }

    public String getHasSoreThroatSymptomFinding() { return hasSoreThroatSymptomFinding; }
    public void setHasSoreThroatSymptomFinding(String hasSoreThroatSymptomFinding) { this.hasSoreThroatSymptomFinding = hasSoreThroatSymptomFinding; }

    public String getHasHyperglycemiaDisorder() { return hasHyperglycemiaDisorder; }
    public void setHasHyperglycemiaDisorder(String hasHyperglycemiaDisorder) { this.hasHyperglycemiaDisorder = hasHyperglycemiaDisorder; }

    public String getTriageLevel() { return triageLevel; }
    public void setTriageLevel(String triageLevel) { this.triageLevel = triageLevel; }

}