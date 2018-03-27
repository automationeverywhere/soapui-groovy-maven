package com.ae.framework

import com.ae.framework.FrameworkUtils
import com.ae.model.DataTable
import com.ae.model.Iteration
import com.ae.model.TestCase
import com.ae.model.TestSuite
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class CommonUtils {

    /**
     *
     * @param inputFilePath
     * @return
     */
    static def parseExcelData(inputFilePath,project) {
        def testSuiteList = []
        def testSuiteTestCaseMap = [:]
        def testSuiteTestCaseMaxIterationMap = [:]
        def testSuiteTestCaseIterationDataMap = [:]
        def testSuiteTestCaseIterationExecuteMap = [:]
        def testSuiteName = ""
        def testCaseName = ""
        def iterationNum = ""
        def parameterName = ""
        def parameterValue = ""
        def executeFlag = false

        FileInputStream fis = new FileInputStream(inputFilePath)
        XSSFWorkbook wb = new XSSFWorkbook(fis)
        XSSFSheet ws = wb.getSheet(FrameworkUtils.getEnv(project))
        def rowCount = ws.getLastRowNum();
        for (int rowNum = 1; rowNum <= rowCount; rowNum++) {
            XSSFRow row = ws.getRow(rowNum)
            if (row != null) {
                testSuiteName = getCellValue(row.getCell(0)).toString()
                testCaseName = getCellValue(row.getCell(1)).toString()
                iterationNum = getCellValue(row.getCell(2)).toString()
                parameterName = getCellValue(row.getCell(3)).toString()
                parameterValue = getCellValue(row.getCell(4)).toString()
                executeFlag = getCellValue(row.getCell(5)).toString()
            }

            if (!testSuiteList.contains(testSuiteName)) {
                testSuiteList.add(testSuiteName)
            }

            def testSuiteTestCaseIterationDataMapKey = "${testSuiteName}#:#${testCaseName}#:#${iterationNum}".toString()
            def testSuiteTestCaseIterationDataMapValue = parameterName + ":" + parameterValue

            if (testSuiteTestCaseIterationDataMap.get(testSuiteTestCaseIterationDataMapKey) != null) {
                testSuiteTestCaseIterationDataMapValue += ",${testSuiteTestCaseIterationDataMap.get(testSuiteTestCaseIterationDataMapKey)}"
                testSuiteTestCaseIterationDataMap[testSuiteTestCaseIterationDataMapKey] = testSuiteTestCaseIterationDataMapValue
            } else {
                testSuiteTestCaseIterationDataMap[testSuiteTestCaseIterationDataMapKey] = testSuiteTestCaseIterationDataMapValue
            }

            def testSuiteTestCaseIterationExecuteMapKey = "${testSuiteName}#:#${testCaseName}#:#${iterationNum}".toString()
            def testSuiteTestCaseIterationExecuteMapValue = executeFlag
            if (testSuiteTestCaseIterationExecuteMap.get(testSuiteTestCaseIterationExecuteMapKey) == null || !testSuiteTestCaseIterationExecuteMap.get(testSuiteTestCaseIterationExecuteMapKey).toString().equalsIgnoreCase("yes")) {

                testSuiteTestCaseIterationExecuteMap[testSuiteTestCaseIterationExecuteMapKey] = testSuiteTestCaseIterationExecuteMapValue
            }

            def testSuiteTestCaseMapKey = testSuiteName
            def testSuiteTestCaseMapValue = testCaseName
            if (testSuiteTestCaseMap.get(testSuiteTestCaseMapKey) != null) {
                if (!testSuiteTestCaseMap.get(testSuiteTestCaseMapKey).split(",").toList().contains(testSuiteTestCaseMapValue)) {
                    testSuiteTestCaseMapValue += ",${testSuiteTestCaseMap.get(testSuiteTestCaseMapKey)}"
                    testSuiteTestCaseMap[testSuiteTestCaseMapKey] = testSuiteTestCaseMapValue
                }
            } else {
                testSuiteTestCaseMap[testSuiteTestCaseMapKey] = testSuiteTestCaseMapValue
            }

            def testSuiteTestCaseMaxIterationMapKey = "${testSuiteName}#:#${testCaseName}".toString()
            def testSuiteTestCaseMaxIterationMapValue = iterationNum
            if (testSuiteTestCaseMaxIterationMap.get(testSuiteTestCaseMaxIterationMapKey) != null) {
                if (testSuiteTestCaseMaxIterationMap.get(testSuiteTestCaseMaxIterationMapKey) < testSuiteTestCaseMaxIterationMapValue) {
                    testSuiteTestCaseMaxIterationMap[testSuiteTestCaseMaxIterationMapKey] = testSuiteTestCaseMaxIterationMapValue
                }
            } else {
                testSuiteTestCaseMaxIterationMap[testSuiteTestCaseMaxIterationMapKey] = testSuiteTestCaseMaxIterationMapValue
            }
        }
        def testSuiteObjectList = []
        for (testSuite in testSuiteList) {
            def testCaseObjectList = []
            for (testCase in testSuiteTestCaseMap.get(testSuite).split(",")) {
                def iterationObjectList = []
                for (int iterationCount = 1; iterationCount <= (testSuiteTestCaseMaxIterationMap.get("${testSuite}#:#${testCase}".toString())).toString().toInteger(); iterationCount++) {
                    def parameterMap = [:]
                    for (parameter in testSuiteTestCaseIterationDataMap.get("${testSuite}#:#${testCase}#:#${iterationCount}".toString()).split(",")) {
                        parameterMap[parameter.split(":")[0]] = parameter.split(":")[1]
                    }
                    iterationObjectList.add(new Iteration(iterationCount, testSuiteTestCaseIterationExecuteMap.get("${testSuite}#:#${testCase}#:#${iterationCount}".toString()).toString().equalsIgnoreCase("yes"), parameterMap))
                }
                testCaseObjectList.add(new TestCase(testCase, testSuiteTestCaseMaxIterationMap.get("${testSuite}#:#${testCase}".toString()).toString().toInteger(), iterationObjectList))
            }
            testSuiteObjectList.add(new TestSuite(testSuite, testCaseObjectList))
        }
        return new DataTable(testSuiteObjectList)
    }

    /**
     *
     * @param cell
     * @return
     */
    static def getCellValue(XSSFCell cell) {
        if (cell != null) {
            if (cell.cellTypeEnum == CellType.STRING) {
                return cell.getStringCellValue()
            } else {
                cell.setCellType(CellType.STRING)
                return cell.getStringCellValue()
            }
        }
    }

    /**
     * 
     * @return
     */
    static def getDateFormat() {
        return new Date().format("MMddyyyy_HHmmss")
    }

}
