package com.ae.framework

import com.ae.framework.FrameworkUtils
import com.ae.model.TestExecutionResult
import groovy.xml.MarkupBuilder

class ReportUtils {

    /**
     *
     * @param project
     * @return
     */
    static def generateReport(project) {
        TestExecutionResult result = project.getContext().report
        def writer = new StringWriter()
        def doc = new MarkupBuilder(writer)
        doc.doubleQuotes = true
        doc.expandEmptyElements = true
        doc.omitEmptyAttributes = false
        doc.omitNullAttributes = false
        doc.html {
            head {
                title("Test Summary Report")
                style(type: "text/css") {
                    mkp.yield('''
									h1,h2.summary {text-align: center}
        							div.pass,tr.pass,td.pass {color: green;}
        							div.fail,tr.fail,td.fail {color: red}
        							.table-default{text-align: center}
                                    .thead-default{text-align: center}
				                ''')
                }
                meta(name: "viewport", content: "width=device-width, initial-scale=1") {}
                link(rel: "stylesheet", href: "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css") {
                }
                script(src: "https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js") {}
                script(src: "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js") {}
                script(src: "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js") {}
            }
            body{
                div("class": "container"){
                    h1("class": "summary") { mkp.yield("Test Summary Report") }
                    def envDetails = " (Environment : " + FrameworkUtils.getEnv(project) + ")"
                    h2("class": "summary") { mkp.yield(envDetails) }
                    def rows = getTestSummary(result).split('\n').collect { row -> row.split(',') }
                    table("class": "table table-bordered table-default") {
                        thead("class": "thead-light") {
                            formatTableHeaderRow(doc, rows[0].toList())
                        }
                        tbody {
                            rows[1..-1].each {
                                formatRow(doc, it)
                            }
                        }
                    }
                }
                div("class": "container-fluid"){
                    div("id": "accordion"){
                        for (testSuite in result.getTestSuiteResultList()) {
                            div("class": "card"){
                                div("class": "card-header"){
                                    a("class": "card-link", "data-toggle": "collapse", "data-parent": "#accordion", "href": "#${testSuite.getTestSuiteName()}"){
                                        div("class": "d-flex justify-content-between"){
                                            div{
                                                mkp.yield("Test suite : ${testSuite.getTestSuiteName()}")
                                            }
                                            div("class":testSuite.getIsTestSuitePassed()?"pass":"fail"){
                                                mkp.yield(testSuite.getIsTestSuitePassed()?"Passed":"Failed")
                                            }
                                        }
                                    }
                                    div("id": "${testSuite.getTestSuiteName()}", "class": "collapse"){
                                        for (testCase in testSuite.getTestCaseResultList()) {
                                            div("class": "card"){
                                                div("class": "card-header"){
                                                    a("class": "card-link", "data-toggle": "collapse", "data-parent": "#accordion", "href": "#${testSuite.getTestSuiteName()}_${testCase.getTestCaseName()}"){
                                                        div("class": "d-flex justify-content-between"){
                                                            div{
                                                                mkp.yield("Test case : ${testCase.getTestCaseName()}"  )
                                                            }
                                                            div("class":testCase.getIsTestCasePassed()?"pass":"fail"){
                                                                mkp.yield(testCase.getIsTestCasePassed()?"Passed":"Failed")
                                                            }
                                                        }
                                                    }
                                                    div("id": "${testSuite.getTestSuiteName()}_${testCase.getTestCaseName()}", "class": "collapse"){
                                                        for (iteration in testCase.getIterationResultList()) {
                                                            div("class": "card"){
                                                                div("class": "card-header"){
                                                                    a("class": "card-link", "data-toggle": "collapse", "data-parent": "#accordion", "href": "#${testSuite.getTestSuiteName()}_${testCase.getTestCaseName()}_${iteration.getIterationNumber()}"){
                                                                        div("class": "d-flex justify-content-between"){
                                                                            div{
                                                                                mkp.yield("Iteration : " + iteration.getIterationNumber())
                                                                            }
                                                                            div("class":iteration.getIsIterationPassed()?"pass":"fail"){
                                                                                mkp.yield(iteration.getIsIterationPassed()?"Passed":"Failed")
                                                                            }
                                                                        }
                                                                    }
                                                                    div("id": "${testSuite.getTestSuiteName()}_${testCase.getTestCaseName()}_${iteration.getIterationNumber()}", "class": "collapse"){
                                                                        div("class": "card"){
                                                                            div("class": "card-header"){
                                                                                table("class": "table table-bordered table-sm table-responsive"){
                                                                                    if (project.getContext().appProperties.capturePOT.toString().equals("true")) {
                                                                                        thead("class": "thead-light thead-default") {
                                                                                            formatTableHeaderRow(doc, ["Test Step Name", "Test Step Result", "Comments/Assertions", "Response Time in millis", "Request/Response Path"])
                                                                                        }
                                                                                    } else {
                                                                                        thead("class": "thead-light thead-default") {
                                                                                            formatTableHeaderRow(doc, ["Test Step Name", "Test Step Result", "Comments/Assertions", "Response Time"])
                                                                                        }
                                                                                    }
                                                                                    tbody{
                                                                                        for(testStep in iteration.getTestStepResultList()){
                                                                                            if(true){
                                                                                                def hyperLinkText = "./${testSuite.getTestSuiteName()}__${testCase.getTestCaseName()}__Iteration-${iteration.getIterationNumber()}__${testStep.getTestStepName()}.txt"
                                                                                                formatTableDataRow(doc, [testStep.getTestStepName(), testStep.getTestStepResult(), testStep.getComments(), testStep.getResponseTime()], hyperLinkText)
                                                                                            }else{
                                                                                                formatTableDataRow(doc, [testStep.getTestStepName(), testStep.getTestStepResult(), testStep.getComments(), testStep.getResponseTime()])
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                div("class": "container"){
                    br{

                    }
                }
            }
        }
        def outFile = project.getContext().resultsPath + "/TestSummary.html"
        new File(outFile).append(writer.toString())
    }

    /**
     *
     * @param result
     * @return
     */
    static def getTestSummary(TestExecutionResult result) {
        def testSummary = ""
        def testSuiteCount = 0
        def testCaseCount = 0
        def testSuitePassCount = 0
        def testCasePassCount = 0
        for (testSuite in result.getTestSuiteResultList()) {
            testSuiteCount++
            if (testSuite.getIsTestSuitePassed()) {
                testSuitePassCount++
            }
            for (testCase in testSuite.getTestCaseResultList()) {
                testCaseCount++
                if (testCase.getIsTestCasePassed()) {
                    testCasePassCount++
                }
            }
        }
        testSummary += ",Total,Passed,Failed\n"
        testSummary += "Test Suites," + testSuiteCount + "," + testSuitePassCount + "," + (testSuiteCount - testSuitePassCount) + "\n"
        testSummary += "Test Cases," + testCaseCount + "," + testCasePassCount + "," + (testCaseCount - testCasePassCount) + "\n"
        return testSummary
    }

    /**
     *
     * @param doc
     * @param row
     * @param cssClass
     * @return
     */
    static def formatRow(doc, row, cssClass) {
        doc.tr {
            row.each { cell ->
                td("class": cssClass) {
                    mkp.yield(cell)
                }
            }
        }
    }

    /**
     *
     * @param doc
     * @param row
     * @return
     */
    static def formatRow(doc, row) {
        doc.tr {
            row.each { cell ->
                td {
                    mkp.yield(cell)
                }
            }
        }
    }

    /**
     *
     * @param doc
     * @param row
     * @return
     */
    static def formatTableHeaderRow(doc, row) {
        doc.tr {
            row.each { cell ->
                th("scope": "col") {
                    mkp.yield(cell)
                }
            }
        }
    }

    /**
     *
     * @param doc
     * @param row
     * @param hyperLinkText
     * @return
     */
    static def formatTableDataRow(doc, row, hyperLinkText) {
        def boldList = ['Details : ','Assertions : ']
        doc.tr {
            for (int i = 0; i < row.size(); i++) {
                if (i == 1) {
                    if (row[i].toString().equalsIgnoreCase("OK")) {
                        td("class": "pass") {
                            mkp.yield(row[i].toString())
                        }
                    } else {
                        td("class": "fail") {
                            mkp.yield(row[i].toString())
                        }
                    }
                } else if(i==2){
                    td{
                        for(key in row[i].split("\n")){
                            if(boldList.contains(key.toString())){
                                b{
                                    div{
                                        mkp.yield(key)
                                    }
                                }
                            }else{
                                div{
                                    mkp.yield(key)
                                }
                            }
                        }
                    }
                }
                else {
                    td {
                        mkp.yield(row[i].toString())
                    }
                }
            }
            td {
                a("href": hyperLinkText, "target": "_blank") {
                    mkp.yield("Request/Response")
                }
            }
        }
    }

    /**
     *
     * @param doc
     * @param row
     * @return
     */
    static def formatTableDataRow(doc, row) {
        doc.tr {
            for (int i = 0; i < row.size(); i++) {
                if (i == 1) {
                    if (row[i].toString().equalsIgnoreCase("OK")) {
                        td("class": "pass") {
                            mkp.yield(row[i].toString())
                        }
                    } else {
                        td("class": "fail") {
                            mkp.yield(row[i].toString())
                        }
                    }
                } else if(i==2){
                    td{
                        for(key in row[i].split("\n")){
                            if(boldList.contains(key.toString())){
                                b{
                                    div{
                                        mkp.yield(key)
                                    }
                                }
                            }else{
                                div{
                                    mkp.yield(key)
                                }
                            }
                        }
                    }
                }
                else {
                    td {
                        mkp.yield(row[i].toString())
                    }
                }
            }
        }
    }

}
