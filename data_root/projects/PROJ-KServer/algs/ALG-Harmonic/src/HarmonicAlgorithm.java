/**
 *
 * @author ...
 */
public class HarmonicAlgorithm extends KServerAbsAlgorithm {

    @Override
    protected KServerOutput execute(KServerInput testCase) {

        KServerOutput result = new KServerOutput("",testCase.ks,testCase.req);

        result.solution = execute(testCase.ks, testCase.req, testCase.x, testCase.y);
        return result;
    }

    public String execute(int[][] servers, int[][] requests, int x, int y) {
        String solution = "";
        try {
            Ks ks = new Ks(servers, requests, x, y);
            solution = ks.startHarmonic();
        } catch (Exception e) {

        }
        return solution;
    }
}