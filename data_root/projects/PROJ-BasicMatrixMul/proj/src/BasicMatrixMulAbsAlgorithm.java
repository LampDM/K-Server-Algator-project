import si.fri.algotest.execute.AbstractAlgorithm;

/**
 *
 * @author Tomaž
 */
public abstract class BasicMatrixMulAbsAlgorithm extends AbstractAlgorithm {

  protected abstract BasicMatrixMulOutput execute(BasicMatrixMulInput basicMatrixMulInput);
  
  @Override
  public BasicMatrixMulTestCase getCurrentTestCase() {
    return (BasicMatrixMulTestCase) super.getCurrentTestCase(); 
  }

  @Override
  public void run() {    
    algorithmOutput = execute(getCurrentTestCase().getInput());
  }
}