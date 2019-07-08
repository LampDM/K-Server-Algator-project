import si.fri.algotest.execute.AbstractOutput;
import si.fri.algotest.execute.AbstractTestCase;

/**
 * 
 * @author ...
 */
public class KServerOutput extends AbstractOutput {

  // TODO: define fields to hold the output data of an algorithm
  // ...
  String solution;
  public KServerOutput(String s) {    
    this.solution = s;
  }
  
  
  @Override
  public String toString() {
    // TODO: provide a handy KServerOutput string representation (include only important data)
    return super.toString();
  }
  
  
  @Override
  protected Object getIndicatorValue(AbstractTestCase testCase, AbstractOutput algorithmOutput, String indicatorName) {
    KServerTestCase kServerTestCase        = (KServerTestCase) testCase;
    KServerOutput   kServerAlgorithmOutput = (KServerOutput) algorithmOutput;

    switch (indicatorName) {
      // TODO: for each indicator defined in the atrd file provide a "case" to determnine its value
      //case "indicator_name" :
      //  using the given test case kServerTestCase (which includes the input and the expected output)
      //    and the given kServerAlgorithmOutput (the actual output of the algorithm) calculate indicator_value
      //  return indicator_value;
      case "Check":
        return "nOK";
      case "p":
        return 1337.0;
    }
    
    return null;
  }
}