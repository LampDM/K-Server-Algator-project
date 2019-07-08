import si.fri.algotest.execute.AbstractAlgorithm;

/**
 *
 * @author ...
 */
public abstract class KServerAbsAlgorithm extends AbstractAlgorithm {
 
  @Override
  public KServerTestCase getCurrentTestCase() {
    return (KServerTestCase) super.getCurrentTestCase(); 
  }

  protected abstract KServerOutput execute(KServerInput kServerInput);

  @Override
  public void run() {    
    algorithmOutput = execute(getCurrentTestCase().getInput());
  }
}