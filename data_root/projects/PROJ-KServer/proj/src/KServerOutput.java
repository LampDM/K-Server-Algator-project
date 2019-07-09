
import si.fri.algotest.execute.AbstractOutput;
import si.fri.algotest.execute.AbstractTestCase;

/**
 *
 * @author ...
 */
public class KServerOutput extends AbstractOutput {

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
            case "Check":
                // Briefly check if solutions are of same size, although the case "p" won't work if they are not aswell
                boolean checkOK = (kServerAlgorithmOutput.solution.length() == kServerTestCase.getExpectedOutput().solution.length());
                return checkOK ? "OK" : "nOK";
            case "p":
                // Solution/Optimal solution to get p-factor
                double sol = KServerTools.distTravelled(kServerAlgorithmOutput.solution, kServerAlgorithmOutput.ks, kServerAlgorithmOutput.req);
                double opt = KServerTools.distTravelled(kServerTestCase.getExpectedOutput().solution, kServerAlgorithmOutput.ks, kServerAlgorithmOutput.req);

                //In case the requests are perfectly where the servers are at the moment, then non optimal will be 0.0 aswell
                if (opt == 0.0) {
                    return 1.0;
                }

                return sol / opt;
        }

        return null;
    }
}
