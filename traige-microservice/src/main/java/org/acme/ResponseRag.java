package org.acme;

import lombok.Data;


public class ResponseRag {


    public String getGeneration() {
        return generation;
    }

    public void setGeneration(String generation) {
        this.generation = generation;
    }

    String generation;
}
