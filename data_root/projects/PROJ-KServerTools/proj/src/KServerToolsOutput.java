import si.fri.algotest.execute.AbstractOutput;
import si.fri.algotest.execute.AbstractTestCase;

/**
 * 
 * @author ...
 */
public class KServerToolsOutput extends AbstractOutput {

  // TODO: define fields to hold the output data of an algorithm
  // ...
  
  public KServerToolsOutput(/* TODO: define appropriate constructor parameters */) {    
    // this.parameter = parameter;
  }
  
  
  @Override
  public String toString() {
    // TODO: provide a handy KServerToolsOutput string representation (include only important data)
    return super.toString();
  }
  
  
  @Override
  protected Object getIndicatorValue(AbstractTestCase testCase, AbstractOutput algorithmOutput, String indicatorName) {
    KServerToolsTestCase kServerToolsTestCase        = (KServerToolsTestCase) testCase;
    KServerToolsOutput   kServerToolsAlgorithmOutput = (KServerToolsOutput) algorithmOutput;

    switch (indicatorName) {
      // TODO: for each indicator defined in the atrd file provide a "case" to determnine its value
      //case "indicator_name" :
      //  using the given test case kServerToolsTestCase (which includes the input and the expected output)
      //    and the given kServerToolsAlgorithmOutput (the actual output of the algorithm) calculate indicator_value
      //  return indicator_value;
      case "Check":
        return "nOK";
    }
    
    return null;
  }
}