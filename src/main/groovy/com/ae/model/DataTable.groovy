package com.ae.model

class DataTable {

    List<TestSuite> testSuites = new ArrayList<>()

    List<TestSuite> getTestSuites() {
        return testSuites
    }

    void setTestSuites(List<TestSuite> testSuites) {
        this.testSuites = testSuites
    }

    DataTable(List<TestSuite> testSuites) {
        this.testSuites = testSuites
    }
}
