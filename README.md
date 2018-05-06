# soapui-groovy-maven
This framework helps in automating SOAP and REST webservices using SoapUI. This framework is developed using SoapUI free version, Grrovy and Maven. This framework provides features (Test Data Management and Reporting) which are not available in SoapUI Free version.

# Steps to Install
  1. Clone the project to your local machine.
  2. Update properties file under config folder accordingly.
  3. Update configuration in pom.xml as per your project needs.
  4. Run 'mvn clean install' command to build code and run tests.
  
  In order to use the framework features for generating reports and test data management, we need to update setup and teardown scripts in SoapUI at all levels. Follow below steps to update those scripts.
# Steps to perform in SoapUI project xml
  1. Double click on SoapUI project and go to TestSuites tab and update setup and teardown scripts with below lines of code.
    Setup Script
        import com.ae.framework.*
        FrameworkUtils.projectSetUp(project)
    Teardown Script
        import com.ae.framework.*
        FrameworkUtils.projectTearDown(project)
  2. Now Double click on TestSuite and update setup and teardown scripts with below lines of code.
    Setup Script
        import com.ae.framework.*
        FrameworkUtils.testSuiteSetup(testSuite)
    Teardown Script
        import com.ae.framework.*
        FrameworkUtils.testSuiteTearDown(testSuite)
  3. Now Double click on TestCase and update setup and teardown scripts with below lines of code.
    Setup Script
        import com.ae.framework.*
        import groovy.json.JsonSlurper
        FrameworkUtils.testCaseSetup(testRunner)
        FrameworkUtils.updatePropertiesAndExecute(testRunner, testCase) 
    Teardown Script
        import com.ae.framework.*
        FrameworkUtils.testCaseTearDown(testRunner)
