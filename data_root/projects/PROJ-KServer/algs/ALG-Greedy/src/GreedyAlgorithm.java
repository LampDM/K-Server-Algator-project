
/**
 *
 * @author ...
 */
public class GreedyAlgorithm extends KServerAbsAlgorithm {

    @Override
    protected KServerOutput execute(KServerInput testCase) {

        KServerOutput result = new KServerOutput("", testCase.ks, testCase.req);

        result.solution = execute(testCase.ks, testCase.req, testCase.dimRange);
        return result;
    }

    public String execute(int[][] servers, int[][] requests, int[] dimRange) {
        String solution = "";
        try {
            if (dimRange.length == 2) {
                Ks ks = new Ks(servers, requests, dimRange[0], dimRange[1]);
                solution = ks.startGreedy();
            }
        } catch (Exception e) {

        }
        return solution;
    }
}
