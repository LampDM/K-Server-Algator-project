
/**
 *
 * @author ...
 */
public class GreedyAlgorithm extends KServerAbsAlgorithm {

    @Override
    protected KServerOutput execute(KServerInput testCase) {

        // TODO: implement the algorithm that uses the input and generates the output
        KServerOutput result = new KServerOutput("",testCase.ks,testCase.req);

        result.solution = execute(testCase.ks, testCase.req, testCase.x, testCase.y);
        return result;
    }

    public String execute(int[][] servers, int[][] requests, int x, int y) {
        String solution = "";
        try {
            Ks ks = new Ks(servers, requests, x, y);
            solution = ks.startGreedy();
        } catch (Exception e) {

        }
        return solution;
    }
}
