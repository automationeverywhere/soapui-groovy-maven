package com.ae.model

class IterationResult {

    int iterationNumber = 0
    List<TestStepResult> testStepResultList = new ArrayList<>()
    boolean isIterationPassed = true

    int getIterationNumber() {
        return iterationNumber
    }

    void setIterationNumber(int iterationNumber) {
        this.iterationNumber = iterationNumber
    }

    List<TestStepResult> getTestStepResultList() {
        return testStepResultList
    }

    void setTestStepResultList(List<TestStepResult> testStepResultList) {
        this.testStepResultList = testStepResultList
    }

    boolean getIsIterationPassed() {
        return isIterationPassed
    }

    void setIsIterationPassed(boolean isIterationPassed) {
        this.isIterationPassed = isIterationPassed
    }

    IterationResult(int iterationNumber) {
        this.iterationNumber = iterationNumber
    }

    IterationResult(int iterationNumber, List<TestStepResult> testStepResultList, boolean isIterationPassed) {
        this.iterationNumber = iterationNumber
        this.testStepResultList = testStepResultList
        this.isIterationPassed = isIterationPassed
    }

    void updateIsIterationPassed(){
        for(testStep in testStepResultList){
            if(!testStep.getIsTestStepPassed()){
                setIsIterationPassed(false)
                break;
            }
        }
    }

    void addTestStepResultToIterationResult(TestStepResult testStepResult){
        this.testStepResultList.add(testStepResult)
        updateIsIterationPassed()
    }

    void addTestStepResultToIterationResult(String testStepName, String testStepResult,String comments, String responseTime){
        this.testStepResultList.add(new TestStepResult(testStepName,testStepResult,comments,responseTime))
        updateIsIterationPassed()
    }
}
