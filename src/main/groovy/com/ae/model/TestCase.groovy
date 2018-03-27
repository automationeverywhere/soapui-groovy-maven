package com.ae.model

class TestCase {
    String testCaseName = "";
    int maxIteration
    List<Iteration> iterations = new ArrayList<>()

    String getTestCaseName() {
        return testCaseName
    }

    void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName
    }

    int getMaxIteration() {
        return maxIteration
    }

    void setMaxIteration(int maxIteration) {
        this.maxIteration = maxIteration
    }

    List<Iteration> getIterations() {
        return iterations
    }

    void setIterations(List<Iteration> iterations) {
        this.iterations = iterations
    }

    TestCase(String testCaseName, int maxIteration, List<Iteration> iterations) {
        this.testCaseName = testCaseName
        this.maxIteration = maxIteration
        this.iterations = iterations
    }
}
