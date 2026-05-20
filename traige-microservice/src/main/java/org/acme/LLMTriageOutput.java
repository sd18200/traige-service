package org.acme;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "triage_level",
        "justification",
        "supporting_evidence"
})
public class LLMTriageOutput {
    public String getTriage_level() {
        return triage_level;
    }

    public void setTriage_level(String triage_level) {
        this.triage_level = triage_level;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getSupporting_evidence() {
        return supporting_evidence;
    }

    public void setSupporting_evidence(String supporting_evidence) {
        this.supporting_evidence = supporting_evidence;
    }

    String triage_level;
    String justification;
    String supporting_evidence;
}
