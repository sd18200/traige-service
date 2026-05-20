package org.acme;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class BedrockModelResponse {

    public String getGeneration() {
        return generation;
    }

    public void setGeneration(String generation) {
        this.generation = generation;
    }

    public String getPrompt_token_count() {
        return prompt_token_count;
    }

    public void setPrompt_token_count(String prompt_token_count) {
        this.prompt_token_count = prompt_token_count;
    }

    public String getGeneration_token_count() {
        return generation_token_count;
    }

    public void setGeneration_token_count(String generation_token_count) {
        this.generation_token_count = generation_token_count;
    }

    public String getStop_reason() {
        return stop_reason;
    }

    public void setStop_reason(String stop_reason) {
        this.stop_reason = stop_reason;
    }

    String generation;
    String  prompt_token_count;
    String  generation_token_count;
    String   stop_reason;
}
