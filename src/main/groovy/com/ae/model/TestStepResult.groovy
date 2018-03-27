package com.ae.model

class TestStepResult {
    String testStepName = ""
    String testStepResult = ""
    String comments = ""
    String responseTime = ""
    boolean isTestStepPassed = true

    String getTestStepName() {
        return testStepName
    }

    void setTestStepName(String testStepName) {
        this.testStepName = testStepName
    }

    String getTestStepResult() {
        return testStepResult
    }

    void setTestStepResult(String testStepResult) {
        this.testStepResult = testStepResult
    }

    String getComments() {
        return comments
    }

    void setComments(String comments) {
        this.comments = comments
    }

    String getResponseTime() {
        return responseTime
    }

    void setResponseTime(String responseTime) {
        this.responseTime = responseTime
    }

    boolean getIsTestStepPassed() {
        return isTestStepPassed
    }

    void setIsTestStepPassed(boolean isTestStepPassed) {
        this.isTestStepPassed = isTestStepPassed
    }

    TestStepResult(String testStepName, String testStepResult, String comments, String responseTime) {
        this.testStepName = testStepName
        this.testStepResult = testStepResult
        this.comments = comments
        this.responseTime = responseTime
        updateIsTestStepPassed(testStepResult)
    }

    void updateIsTestStepPassed(String testStepResult){
        if(!testStepResult.toString().equalsIgnoreCase("OK")){
            this.setIsTestStepPassed(false)
        }
    }
}
