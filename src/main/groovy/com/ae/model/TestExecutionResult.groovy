package com.ae.model

class TestExecutionResult {

    String projectName = ""
    List<TestSuiteResult> testSuiteResultList = new ArrayList<>()
    boolean isTestExecutionPassed = true

    String getProjectName() {
        return projectName
    }

    void setProjectName(String projectName) {
        this.projectName = projectName
    }

    List<TestSuiteResult> getTestSuiteResultList() {
        return testSuiteResultList
    }

    void setTestSuiteResultList(List<TestSuiteResult> testSuiteResultList) {
        this.testSuiteResultList = testSuiteResultList
    }

    boolean getIsTestExecutionPassed() {
        return isTestExecutionPassed
    }

    void setIsTestExecutionPassed(boolean isTestExecutionPassed) {
        this.isTestExecutionPassed = isTestExecutionPassed
    }

    TestExecutionResult(String projectName) {
        this.projectName = projectName
    }

    TestExecutionResult(String projectName, List<TestSuiteResult> testSuiteResultList, boolean isTestExecutionPassed) {
        this.projectName = projectName
        this.testSuiteResultList = testSuiteResultList
        this.isTestExecutionPassed = isTestExecutionPassed
    }

    void updateIsTestExecutionPassed(){
        for(testSuite in testSuiteResultList){
            if(!testSuite.getIsTestSuitePassed()){
                setIsTestExecutionPassed(false)
                break;
            }
        }
    }

    void addTestSuiteResultToTestExecutionResult(TestSuiteResult testSuiteResult){
        this.testSuiteResultList.add(testSuiteResult)
        updateIsTestExecutionPassed()
    }

    void addTestStepResultToTestExecutionResult(String testSuiteName,String testCaseName, int iteration, String testStepName, String testStepResult, String comments, String responseTime){
        TestSuiteResult testSuiteResult = getTestSuiteObjectByTestSuiteName(testSuiteName)
        testSuiteResult.addTestStepResultToTestSuiteResult(testCaseName,iteration,testStepName,testStepResult,comments,responseTime)
        updateIsTestExecutionPassed()
    }

    TestSuiteResult getTestSuiteObjectByTestSuiteName(String testSuiteName) {
        boolean testSuiteExists = false
        for(testSuiteObj in this.testSuiteResultList){
            if(testSuiteObj.getTestSuiteName().equalsIgnoreCase(testSuiteName)){
               testSuiteExists = true
                return testSuiteObj
            }
        }
        if(!testSuiteExists){
            TestSuiteResult testSuiteResult = new TestSuiteResult(testSuiteName)
            this.testSuiteResultList.add(testSuiteResult)
            return testSuiteResult
        }

    }
}
