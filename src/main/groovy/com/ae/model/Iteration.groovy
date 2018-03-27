package com.ae.model

class Iteration {
    int iteration = 0
    boolean execute = false
    Map<String, String> parameters = new HashMap<>()

    int getIteration() {
        return iteration
    }

    void setIteration(int iteration) {
        this.iteration = iteration
    }

    boolean getExecute() {
        return execute
    }

    void setExecute(boolean execute) {
        this.execute = execute
    }

    Map<String, String> getParameters() {
        return parameters
    }

    void setParameters(Map<String, String> parameters) {
        this.parameters = parameters
    }

    Iteration(int iteration, boolean execute, Map<String, String> parameters) {
        this.iteration = iteration
        this.execute = execute
        this.parameters = parameters
    }

}
