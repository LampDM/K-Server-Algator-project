import java.io.File;
import java.util.Scanner;
import si.fri.algotest.entities.Variables;
import si.fri.algotest.execute.AbstractTestCase;
import si.fri.algotest.global.ErrorStatus;

/**
 *
 * @author Tomaž
 */
public class BasicMatrixMulTestCase extends AbstractTestCase {

  @Override
  public BasicMatrixMulInput getInput() {
    return (BasicMatrixMulInput) super.getInput(); 
  } 

  @Override
  public BasicMatrixMulOutput getExpectedOutput() {
    return (BasicMatrixMulOutput) super.getExpectedOutput();
  }
  
  
  @Override
  public BasicMatrixMulTestCase getTestCase(String testCaseDescriptionLine, String path) {    
    // create a set of variables ...
    Variables inputParameters = new Variables();
    inputParameters.setVariable("Path", path);
    

    String[] params = testCaseDescriptionLine.split(":");
    if (params.length < 3) {
      ErrorStatus.setLastErrorMessage(ErrorStatus.ERROR, "Invalid testset file - line ");
      return null;
    }

    inputParameters.setVariable("Name",      params[0]);
    inputParameters.setVariable("Group",     params[1]);
    inputParameters.setVariable("N",         params[2]);

    if (params[1].toUpperCase().equals("FILE")) {
      inputParameters.setVariable("FilenameA", params.length > 3 ? params[3] : "");
      inputParameters.setVariable("FilenameB", params.length > 4 ? params[4] : "");
      inputParameters.setVariable("FilenameC", params.length > 5 ? params[5] : "");
    }

    // ... and finally, create a test case determined by these parameters
    return generateTestCase(inputParameters);
  } 

  @Override
  public BasicMatrixMulTestCase generateTestCase(Variables inputParameters) {
    String path      = inputParameters.getVariable("Path",    "")   .getStringValue();              
    int N            = inputParameters.getVariable("N",        1000).getIntValue();              
    String group     = inputParameters.getVariable("Group",   "RND").getStringValue().toUpperCase();              

    String filenameA = inputParameters.getVariable("FilenameA", "").getStringValue();              
    String filenameB = inputParameters.getVariable("FilenameB", "").getStringValue();              
    String filenameC = inputParameters.getVariable("FilenameC", "").getStringValue();              

    int [][] A=null, B=null, C=null;
    
    switch (group) {
      case "FILE":
        A = BasicMatrixMulTools.readMatrix(path, filenameA);
        B = BasicMatrixMulTools.readMatrix(path, filenameB);
        C = BasicMatrixMulTools.readMatrix(path, filenameC);
        break;
      case "RND":
        A = BasicMatrixMulTools.createRNDMatrix(N, 100000);
        B = BasicMatrixMulTools.createRNDMatrix(N, 100000);
        C = BasicMatrixMulTools.multiply(A, B);
        break;    
    }
                    
    if (A == null || B == null || C == null) {
      ErrorStatus.setLastErrorMessage(ErrorStatus.ERROR, "Invalid parameters, can not generate matrices.");
      return null;
    }

    
    // create a test case 
    BasicMatrixMulTestCase basicMatrixMulTestCase = new BasicMatrixMulTestCase();                
    basicMatrixMulTestCase.setInput(new BasicMatrixMulInput(A, B));    
    basicMatrixMulTestCase.getInput().setParameters(inputParameters);    
    basicMatrixMulTestCase.setExpectedOutput(new BasicMatrixMulOutput(C));
    
    return basicMatrixMulTestCase;
    
  }

}