import si.fri.algotest.execute.AbstractAlgorithm;

/**
 *
 * @author ...
 */
public abstract class KServerToolsAbsAlgorithm extends AbstractAlgorithm {
 
  @Override
  public KServerToolsTestCase getCurrentTestCase() {
    return (KServerToolsTestCase) super.getCurrentTestCase(); 
  }

  protected abstract KServerToolsOutput execute(KServerToolsInput kServerToolsInput);

  @Override
  public void run() {    
    algorithmOutput = execute(getCurrentTestCase().getInput());
  }
}