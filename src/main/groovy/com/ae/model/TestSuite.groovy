package com.ae.model

class TestSuite {
    String testSuiteName = ""
    List<TestCase> testCases = new ArrayList<>()

    String getTestSuiteName() {
        return testSuiteName
    }

    void setTestSuiteName(String testSuiteName) {
        this.testSuiteName = testSuiteName
    }

    List<TestCase> getTestCases() {
        return testCases
    }

    void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases
    }

    TestSuite(String testSuiteName, List<TestCase> testCases) {
        this.testSuiteName = testSuiteName
        this.testCases = testCases
    }
}
