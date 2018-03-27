package com.ae.model

class TestCaseResult {

    String testCaseName = ""
    List<IterationResult> iterationResultList = new ArrayList<>()
    boolean isTestCasePassed = true

    String getTestCaseName() {
        return testCaseName
    }

    void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName
    }

    List<IterationResult> getIterationResultList() {
        return iterationResultList
    }

    void setIterationResultList(List<IterationResult> iterationResultList) {
        this.iterationResultList = iterationResultList
    }

    boolean getIsTestCasePassed() {
        return isTestCasePassed
    }

    void setIsTestCasePassed(boolean isTestCasePassed) {
        this.isTestCasePassed = isTestCasePassed
    }

    TestCaseResult(String testCaseName) {
        this.testCaseName = testCaseName
    }

    TestCaseResult(String testCaseName, List<IterationResult> iterationResultList, boolean isTestCasePassed) {
        this.testCaseName = testCaseName
        this.iterationResultList = iterationResultList
        this.isTestCasePassed = isTestCasePassed
    }

    void updateIsTestCasePassed(){
        for(iteration in iterationResultList){
            if(!iteration.getIsIterationPassed()){
                setIsTestCasePassed(false)
                break;
            }
        }
    }

    void addIterationResultToTestCaseResult(IterationResult iterationResult){
        this.iterationResultList.add(iterationResult)
        updateIsTestCasePassed()
    }

    void addTestStepResultToTestCaseResult(int iteration, String testStepName, String testStepResult, String comments,String responseTime){
        IterationResult iterationResult = getIterarationResultObjectByIterationNumber(iteration)
        iterationResult.addTestStepResultToIterationResult(testStepName,testStepResult,comments,responseTime)
        updateIsTestCasePassed()
    }

    IterationResult getIterarationResultObjectByIterationNumber(int iteration){
        boolean iterationResultExists = false
        for(iterationObj in this.iterationResultList){
            if(iterationObj.getIterationNumber()==iteration){
                iterationResultExists = true
                return iterationObj
            }
        }
        if(!iterationResultExists){
            IterationResult iterationResult = new IterationResult(iteration)
            this.iterationResultList.add(iterationResult)
            return iterationResult
        }
    }

}
