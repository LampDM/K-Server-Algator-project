
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
    int[][] ks;
    int[][] req;

    public KServerOutput(String s, int[][] servers, int[][] requests) {
        this.solution = s;
        this.ks = servers;
        this.req = requests;
    }

    @Override
    public String toString() {
        // TODO: provide a handy KServerOutput string representation (include only important data)
        return super.toString();
    }

    @Override
    protected Object getIndicatorValue(AbstractTestCase testCase, AbstractOutput algorithmOutput, String indicatorName) {
        KServerTestCase kServerTestCase = (KServerTestCase) testCase;
        KServerOutput kServerAlgorithmOutput = (KServerOutput) algorithmOutput;

        switch (indicatorName) {
            // TODO: for each indicator defined in the atrd file provide a "case" to determnine its value
            //case "indicator_name" :
            //  using the given test case kServerTestCase (which includes the input and the expected output)
            //    and the given kServerAlgorithmOutput (the actual output of the algorithm) calculate indicator_value
            //  return indicator_value;
            case "Check":
                //TODO Do a bunch of checks if output is valid, check length, if indexes are OK and so on
                return "nOK";
            case "p":
                // Solution/Optimal solution to get p-factor
                double sol = KServerTools.distTravelled(kServerAlgorithmOutput.solution,kServerAlgorithmOutput.ks,kServerAlgorithmOutput.req);
                double opt = KServerTools.distTravelled(kServerTestCase.getExpectedOutput().solution,kServerAlgorithmOutput.ks,kServerAlgorithmOutput.req);
                //TODO What if opt is 0.0 ?
                return sol/opt;
        }

        return null;
    }
}
