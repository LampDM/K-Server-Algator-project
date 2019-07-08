import si.fri.algotest.execute.AbstractOutput;
import si.fri.algotest.execute.AbstractTestCase;

/**
 * 
 * @author Tomaž
 */
public class BasicMatrixMulOutput extends AbstractOutput {

  // the product of the input matrices
  int [][] C;
  
  public BasicMatrixMulOutput(int [][] C) {    
    this.C = C;
  }
  
  
  @Override
  public String toString() {
    String dataC = "";
    for (int i = 0; i < 5; i++) {
      if (i < C.length) {
        dataC += (!dataC.isEmpty() ? ", ":"") + C[0][i];
      }
    }
    String matrika = " %c = [%s ...] ";
    return "N = " + C.length + String.format(matrika, 'C', dataC);
  }
  
  
  @Override
  protected Object getIndicatorValue(AbstractTestCase testCase, AbstractOutput algorithmOutput, String indicatorName) {
    BasicMatrixMulTestCase basicMatrixMulTestCase        = (BasicMatrixMulTestCase) testCase;
    BasicMatrixMulOutput   basicMatrixMulAlgorithmOutput = (BasicMatrixMulOutput) algorithmOutput;

    switch (indicatorName) {
      case "Check":
        return BasicMatrixMulTools.matrixEquals(
                  basicMatrixMulTestCase.getExpectedOutput().C, basicMatrixMulAlgorithmOutput.C
               ) ? "OK" : "NOK";
    }
    
    return null;
  }
}