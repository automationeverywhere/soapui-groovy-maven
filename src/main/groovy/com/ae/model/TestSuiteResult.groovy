package com.ae.model

class TestSuiteResult {

    String testSuiteName
    List<TestCaseResult> testCaseResultList = new ArrayList<>()
    boolean isTestSuitePassed = true

    String getTestSuiteName() {
        return testSuiteName
    }

    void setTestSuiteName(String testSuiteName) {
        this.testSuiteName = testSuiteName
    }

    List<TestCaseResult> getTestCaseResultList() {
        return testCaseResultList
    }

    void setTestCaseResultList(List<TestCaseResult> testCaseResultList) {
        this.testCaseResultList = testCaseResultList
    }

    boolean getIsTestSuitePassed() {
        return isTestSuitePassed
    }

    void setIsTestSuitePassed(boolean isTestSuitePassed) {
        this.isTestSuitePassed = isTestSuitePassed
    }

    TestSuiteResult(String testSuiteName, List<TestCaseResult> testCaseResultList, boolean isTestSuitePassed) {
        this.testSuiteName = testSuiteName
        this.testCaseResultList = testCaseResultList
        this.isTestSuitePassed = isTestSuitePassed
    }

    TestSuiteResult(String testSuiteName) {
        this.testSuiteName = testSuiteName
    }

    void updateIsTestSuitePassed(){
        for(testCase in testCaseResultList){
            if(!testCase.getIsTestCasePassed()){
                setIsTestSuitePassed(false)
                break;
            }
        }
    }

    void addTestCaseResultToTestSuiteResult(TestCaseResult testCaseResult){
        this.testCaseResultList.add(testCaseResult)
        updateIsTestSuitePassed()
    }

    void addTestStepResultToTestSuiteResult(String testCaseName,int iteration,String testStepName, String testStepResult, String comments,String responseTime){
        TestCaseResult testCaseResult = getTestCaseResultObjectByTestCaseName(testCaseName)
        testCaseResult.addTestStepResultToTestCaseResult(iteration,testStepName,testStepResult,comments,responseTime)
        updateIsTestSuitePassed()
    }

    TestCaseResult getTestCaseResultObjectByTestCaseName(String testCaseName) {
        boolean testCaseResultExists = false
        for(testCaseObj in this.testCaseResultList){
            if(testCaseObj.getTestCaseName().equalsIgnoreCase(testCaseName)){
                testCaseResultExists = true
                return testCaseObj
            }
        }
        if(!testCaseResultExists){
            TestCaseResult testCaseResult = new TestCaseResult(testCaseName)
            this.testCaseResultList.add(testCaseResult)
            return testCaseResult
        }
    }
}
