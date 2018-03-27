package com.ae.framework

import com.ae.model.TestExecutionResult
import groovy.json.JsonSlurper
import org.apache.log4j.Logger

class FrameworkUtils implements Constants {

    private final static Logger logger = Logger.getLogger(FrameworkUtils.class);

    /**
     *
     * @param project
     * @return
     */
    static def projectSetUp(project) {
        if (project.getContext().executionLevel == null) {
            project.getContext().executionLevel = "project"
        }
        loadProperties(project)
        setDataTable(project)
        setReportObjects(project)
        setCustomDataVariable(project)
//        DBUtils.getOracleDBObject(project)
        logger.debug("Project Setup Completed")
    }

    /**
     *
     * @param project
     * @return
     */
    static def projectTearDown(project) {
        if (project.getContext().executionLevel.toString().equals("project")) {
            ReportUtils.generateReport(project)
            project.getContext().removeProperty("executionLevel")
            project.getContext().removeProperty("appProperties")
//            DBUtils.closeDBConnection(project.getContext().oracleObj)
//            project.getContext().removeProperty("oracleObj")
            project.getContext().removeProperty("report")
            project.getContext().removeProperty("datatable")
            project.getContext().removeProperty("customdata")
        }
    }

    /**
     *
     * @param testSuite
     * @return
     */
    static def testSuiteSetup(testSuite) {
        loadProperties(testSuite.project)
        setDataTable(testSuite.project)
        setCustomDataVariable(testSuite.project)
        if (testSuite.project.getContext().executionLevel == null || !testSuite.project.getContext().executionLevel.toString().equals("project")) {
            testSuite.project.getContext().executionLevel = "testsuite"
            setReportObjects(testSuite.project)
//            DBUtils.getOracleDBObject(testSuite.project)
        }
    }

    /**
     *
     * @param testSuite
     * @return
     */
    static def testSuiteTearDown(testSuite) {
        if (testSuite.project.getContext().executionLevel.toString().equals("testsuite")) {
            ReportUtils.generateReport(testSuite.project)
            testSuite.project.getContext().removeProperty("executionLevel")
            testSuite.project.getContext().removeProperty("appProperties")
//            DBUtils.closeDBConnection(testSuite.project.getContext().oracleObj)
//            testSuite.project.getContext().removeProperty("oracleObj")
            testSuite.project.getContext().removeProperty("report")
            testSuite.project.getContext().removeProperty("datatable")
            testSuite.project.getContext().removeProperty("customdata")
        }
    }

    /**
     *
     * @param testRunner
     * @return
     */
    static def testCaseSetup(testRunner) {
        loadProperties(testRunner.testCase.testSuite.project)
        setDataTable(testRunner.testCase.testSuite.project)
        setCustomDataVariable(testRunner.testCase.testSuite.project)
        if (testRunner.testCase.testSuite.project.getContext().executionLevel == null ||
                (!testRunner.testCase.testSuite.project.getContext().executionLevel.toString().equals("project")
                        && !testRunner.testCase.testSuite.project.getContext().executionLevel.toString().equals("testsuite"))) {
            testRunner.testCase.testSuite.project.getContext().executionLevel = "testcase"
            setReportObjects(testRunner.testCase.testSuite.project)
//            DBUtils.getOracleDBObject(testRunner.testCase.testSuite.project)
        }
        updateEndpoints(testRunner.testCase)
    }

    /**
     *
     * @param testRunner
     * @return
     */
    static def testCaseTearDown(testRunner) {
        if (testRunner.testCase.testSuite.project.getContext().executionLevel.toString().equals("testcase")) {
            ReportUtils.generateReport(testRunner.testCase.testSuite.project)
            testRunner.testCase.testSuite.project.getContext().removeProperty("executionLevel")
            testRunner.testCase.testSuite.project.getContext().removeProperty("appProperties")
//            DBUtils.closeDBConnection(testRunner.testCase.testSuite.project.getContext().oracleObj)
//            testRunner.testCase.testSuite.project.getContext().removeProperty("oracleObj")
            testRunner.testCase.testSuite.project.getContext().removeProperty("report")
            testRunner.testCase.testSuite.project.getContext().removeProperty("datatable")
            testRunner.testCase.testSuite.project.getContext().removeProperty("customdata")
        }
    }

    /**
     *
     * @param project
     * @return
     */
    static def setDataTable(project) {
        if (project.getContext().datatable == null) {
            project.getContext().datatable = new groovy.json.JsonBuilder(CommonUtils.parseExcelData(project.getContext().appProperties.dataTablePath, project))
        }
    }

    /**
     *
     * @param project
     * @return
     */
    static def setCustomDataVariable(project) {
        if (project.getContext().customdata == null) {
            project.getContext().customdata = [:]
        }
    }

    /**
     *
     * @param project
     * @return
     */
    static def setReportObjects(project) {
        def testDirPath = "${RESULT_BASE_LOCATION}${CommonUtils.getDateFormat()}"
        if (project.getContext().resultsPath == null || project.getContext().executionLevel.toString().equals("project") || project.getContext().executionLevel.toString().equals("testsuite") || project.getContext().executionLevel.toString().equals("testcase")) {
            new File(testDirPath).mkdirs()
            project.getContext().resultsPath = testDirPath
        }
        if (project.getContext().report == null || project.getContext().executionLevel.toString().equals("project") || project.getContext().executionLevel.toString().equals("testsuite") || project.getContext().executionLevel.toString().equals("testcase")) {
            project.getContext().report = new TestExecutionResult(project.name);
        }
    }

    /**
     *
     * @param testRunner
     * @param testCase
     */
    static def updatePropertiesAndExecute(testRunner, testCase) {
        def jsonSlurper = new JsonSlurper()
        def dataTable = jsonSlurper.parseText(testRunner.testCase.testSuite.project.getContext().datatable.toPrettyString())
        def itrCount = getTestCaseIterationCount(dataTable, testCase.testSuite.name, testCase.name).toInteger()
        for (int itr = 1; itr <= itrCount; itr++) {
            if (getTestCaseExecutionFlag(dataTable, testCase.testSuite.name, testCase.name, itr)) {
                def params = getTestCaseParameters(dataTable, testCase.testSuite.name, testCase.name, itr)
                for (int stepCount = 0; stepCount < testCase.getTestStepCount(); stepCount++) {
                    if (testCase.getTestStepAt(stepCount).config.type.toString().equals("properties")) {
                        if (params) {
                            testCase.getTestStepAt(stepCount).getProperties().each {
                                if (params.containsKey(it.key)) {
                                    testCase.getTestStepAt(stepCount).setPropertyValue(it.key, params.get(it.key))
                                }
                            }
                        }
                    }
                }
                for (int stepCount = 0; stepCount < testCase.getTestStepCount(); stepCount++) {
                    testCase.testSuite.project.getContext().comments = [:]
                    def result = testRunner.runTestStepByName(testCase.getTestStepAt(stepCount).name)
                    def comments = getCustomData(testCase, testCase.getTestStepAt(stepCount).name)
                    if (testCase.getTestStepAt(stepCount).config.type.toString().equals("properties")
                            && testCase.testSuite.project.getContext().appProperties.capturePOT.toString().equals("true")) {
                        printProperties(testCase, stepCount, testCase.getTestStepAt(stepCount).name, itr)
                    }
                    if (testCase.getTestStepAt(stepCount).config.type.toString().contains("request")) {
                        comments += "\n${getTestComments(testCase, testCase.getTestStepAt(stepCount).name)}"
                    }
                    if (testCase.getTestStepAt(stepCount).config.type.toString().contains("request")) {
                        if (result != null) {
                            testCase.testSuite.project.getContext().report.addTestStepResultToTestExecutionResult(testCase.testSuite.name, testCase.name, itr, testCase.getTestStepAt(stepCount).name, result.getStatus().toString(), comments, "${result.getTimeTaken().toString()} ms")
                        } else {
                            testCase.testSuite.project.getContext().report.addTestStepResultToTestExecutionResult(testCase.testSuite.name, testCase.name, itr, testCase.getTestStepAt(stepCount).name, "ERROR", comments, "N/A")
                        }
                    } else {
                        if (result != null) {
                            testCase.testSuite.project.getContext().report.addTestStepResultToTestExecutionResult(testCase.testSuite.name, testCase.name, itr, testCase.getTestStepAt(stepCount).name, result.getStatus().toString(), comments, "N/A")
                        } else {
                            testCase.testSuite.project.getContext().report.addTestStepResultToTestExecutionResult(testCase.testSuite.name, testCase.name, itr, testCase.getTestStepAt(stepCount).name, "ERROR", comments, "N/A")
                        }
                    }
                    if (testCase.getTestStepAt(stepCount).config.type.toString().contains("request")) {
                        printRequestResponse(testCase, testCase.getTestStepAt(stepCount).name, itr)
                    }
                }
            }
        }
    }

    /**
     *
     * @param testCase
     * @param stepCount
     * @param testStepName
     * @param itr
     * @return
     */
    static def printProperties(testCase, stepCount, testStepName, itr) {
        def outFileName = "${testCase.testSuite.project.getContext().resultsPath}/${testCase.testSuite.name}__${testCase.name}__Iteration-${itr}__${testStepName}.txt"
        def outFile = new File(outFileName)
        outFile.append("Properties\n----------\n")
        testCase.getTestStepAt(stepCount).getProperties().each {
            outFile.append("${it.key}:${it.value.value}\n")
        }
    }

    /**
     *
     * @param testCase
     * @param iterationNum
     */
    static def updateIterationProperties(testCase, iterationNum) {
        def jsonSlurper = new JsonSlurper()
        def dataTable = jsonSlurper.parseText(testCase.testSuite.project.getContext().datatable.toPrettyString())
        if (getTestCaseExecutionFlag(dataTable, testCase.testSuite.name, testCase.name, iterationNum)) {
            def params = getTestCaseParameters(dataTable, testCase.testSuite.name, testCase.name, iterationNum)
            for (int stepCount = 0; stepCount < testCase.getTestStepCount(); stepCount++) {
                if (testCase.getTestStepAt(stepCount).config.type.toString().equals("properties")) {
                    if (params) {
                        testCase.getTestStepAt(stepCount).getProperties().each {
                            if (params.containsKey(it.key)) {
                                testCase.getTestStepAt(stepCount).setPropertyValue(it.key, params.get(it.key))
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param testCase
     * @param testStepName
     * @return
     */
    static def getTestComments(testCase, testStepName) {
        def comments = ""
        def assertList = testCase.getTestStepByName(testStepName).getAssertionList()
        for (assertion in assertList) {
            if (testCase.testSuite.project.getContext().appProperties.printAllAssertions.toString().equalsIgnoreCase("true")) {
                def error = ""
                for (assertError in assertion.getErrors()) {
                    error += assertError.getMessage()
                }
                if (error) {
                    comments += "${assertion.getLabel()} - ${assertion.getStatus()} ( ${error} );\n"
                } else {
                    comments += "${assertion.getLabel()} - ${assertion.getStatus()};\n"

                }
            } else {
                if (!assertion.getStatus().toString().equals("VALID")) {
                    def error = ""
                    for (assertError in assertion.getErrors()) {
                        error += assertError.getMessage()
                    }
                    comments += "${assertion.getLabel()} - ${assertion.getStatus()} ( ${error} );\n"
                }
            }
        }
        if (!comments.equals("")) {
            comments = "${ASSERTION_HEADER}\n${comments}"
        }
        return comments
    }

    /**
     *
     * @param project
     * @return
     */
    static def loadProperties(project) {
        Properties prop = new Properties()
        File propertiesFile = new File("config/GlobalSettings.properties")
        propertiesFile.withInputStream { prop.load(it) }

        if (project.getContext().appProperties == null) {
            project.getContext().appProperties = prop
        }
    }

    /**
     *
     * @param testCase
     * @param testStepName
     * @param iterationNum
     */
    static def printRequestResponse(testCase, testStepName, iterationNum) {
        if (testCase.testSuite.project.getContext().appProperties.capturePOT.toString().equalsIgnoreCase("true")) {
            def outFileName = "${testCase.testSuite.project.getContext().resultsPath}/${testCase.testSuite.name}__${testCase.name}__Iteration-${iterationNum}__${testStepName}.txt"
            def outFile = new File(outFileName)
            outFile.append("${REQUEST_PRINT}${testCase.getTestStepByName(testStepName).getProperty(REQUEST_PROPERTY).value}${RESPONSE_PRINT}${testCase.getTestStepByName(testStepName).getProperty(RESPONSE_PROPERTY).value}${ASSERTION_RESULT_PRINT}")
            def assertList = testCase.getTestStepByName(testStepName).getAssertionList()
            for (assertion in assertList) {
                def status = assertion.getStatus()
                def error = ""
                if (!status.equals("VALID")) {
                    for (assertError in assertion.getErrors()) {
                        error += assertError.getMessage()
                    }
                }
                if (!error.equals("")) {
                    outFile.append("${assertion.getLabel()} - ${status} ( ${error} )\n")
                } else {
                    outFile.append("${assertion.getLabel()} - ${status}\n")
                }
            }
        }
    }

    /**
     *
     * @param testCase
     */
    static def updateEndpoints(testCase) {
        for (int stepCount = 0; stepCount < testCase.getTestStepCount(); stepCount++) {
            def key
            if (testCase.getTestStepAt(stepCount).config.type.toString().equals("request")) {
                key = "SOAP_${getEnv(testCase.testSuite.project)}".toString()
                testCase.getTestStepAt(stepCount).setPropertyValue("Endpoint", getEndpointURL(testCase.testSuite.project, key))
            }
            if (testCase.getTestStepAt(stepCount).config.type.toString().equals("restrequest")) {
                key = "REST_${getEnv(testCase.testSuite.project)}".toString()
                testCase.getTestStepAt(stepCount).setPropertyValue("Endpoint", getEndpointURL(testCase.testSuite.project, key))
            }
        }
    }

    /**
     *
     * @param testCase
     * @param testStepName
     * @return
     */
    static def getCustomData(testCase, testStepName) {
        def customData = ""
        def customDataMap = testCase.testSuite.project.getContext().comments
        if (customDataMap != null && customDataMap.size() > 0) {
            customData = "Details : \n"
            for (key in customDataMap.keySet()) {
                customData += "${key} = ${customDataMap.get(key)};\n"
            }
        }
        return customData
    }

    /**
     *
     * @param dataTable
     * @param testSuiteName
     * @param testCaseName
     * @param iterationValue
     * @return
     */
    static def getTestCaseParameters(dataTable, testSuiteName, testCaseName, iterationValue) {
        for (testsuite in dataTable.testSuites) {
            if (testsuite.testSuiteName.equals(testSuiteName)) {
                for (testcase in testsuite.testCases) {
                    if (testcase.testCaseName.equals(testCaseName)) {
                        for (iteration in testcase.iterations) {
                            if (iteration.iteration.toString().toInteger() == iterationValue) {
                                return iteration.parameters
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param dataTable
     * @param testSuiteName
     * @param testCaseName
     * @param iterationValue
     * @return
     */
    static def getTestCaseExecutionFlag(dataTable, testSuiteName, testCaseName, iterationValue) {
        for (testsuite in dataTable.testSuites) {
            if (testsuite.testSuiteName.equals(testSuiteName)) {
                for (testcase in testsuite.testCases) {
                    if (testcase.testCaseName.equals(testCaseName)) {
                        for (iteration in testcase.iterations) {
                            if (iteration.iteration.toString().toInteger() == iterationValue) {
                                return iteration.execute
                            }
                        }
                    }
                }
            }
        }
        return true
    }

    /**
     *
     * @param dataTable
     * @param testSuiteName
     * @param testCaseName
     * @return
     */
    static def getTestCaseIterationCount(dataTable, testSuiteName, testCaseName) {
        for (testsuite in dataTable.testSuites) {
            if (testsuite.testSuiteName.equals(testSuiteName)) {
                for (testcase in testsuite.testCases) {
                    if (testcase.testCaseName.equals(testCaseName)) {
                        return testcase.maxIteration
                    }
                }
            }
        }
        return 1
    }

    /**
     *
     * @param project
     * @return
     */
    static def getEnv(project) {
        if (System.getProperty("ENV") != null) {
            return System.getProperty("ENV")
        } else if (project.getContext().appProperties.get("ENV") != null) {
            return project.getContext().appProperties.get("ENV")
        } else {
            return "QA1"
        }
    }

    /**
     *
     * @param project
     * @param propertyName
     * @return
     */
    static def getEndpointURL(project, propertyName) {
        if (project.getContext().appProperties.get(propertyName) != null) {
            return project.getContext().appProperties.get(propertyName)
        } else {
            return ""
        }
    }

}
