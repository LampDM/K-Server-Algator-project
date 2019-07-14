import si.fri.algotest.entities.Variables;
import si.fri.algotest.execute.AbstractTestCase;

/**
 *
 * @author ...
 */
public class KServerToolsTestCase extends AbstractTestCase {

  @Override
  public KServerToolsInput getInput() {
    return (KServerToolsInput) super.getInput(); 
  } 

  @Override
  public KServerToolsOutput getExpectedOutput() {
    return (KServerToolsOutput) super.getExpectedOutput();
  }
  
  

  @Override
  public KServerToolsTestCase getTestCase(String testCaseDescriptionLine, String path) {    
    // create a set of variables ...
    Variables inputParameters = new Variables();
    inputParameters.setVariable("Path", path);
    

    // TODO:
    // ... using the testCaseDescriptionLine determine the parameters of
    //     the test case and add them to the inputParameters set ...            
    
    // ... Example: if description line contains two parameters (name_of_test and size N) separated with ":"
    // String [] fields = testCaseDescriptionLine.split(":");     
    // inputParameters.setVariable("Test", fields[0]);
    // inputParameters.setVariable("N",    Integer.parseInt(fields[1]));
    inputParameters.setVariable("N",       17);


    // ... and finally, create a test case determined by these parameters
    return generateTestCase(inputParameters);
  } 

  @Override
  public KServerToolsTestCase generateTestCase(Variables inputParameters) {
    String path       = inputParameters.getVariable("Path",    "").getStringValue();              

    // TODO: 
    // ... read the values of the parameters and create a corresponding test case

    // ... Example: if test case is and array of integers of size "N"
    // int size = inputParameters.getVariable("N", 0).getIntValue();              
    // int [] array = new int[size];

                    
    
    // create a test case 
    KServerToolsTestCase kServerToolsTestCase = new KServerToolsTestCase();                
    kServerToolsTestCase.setInput(new KServerToolsInput(/* TODO: add parameters for constructor */));    
    kServerToolsTestCase.getInput().setParameters(inputParameters);    
    kServerToolsTestCase.setExpectedOutput(new KServerToolsOutput(/* TODO: add parameters for constructor */));
    
    return kServerToolsTestCase;
    
  }

}