/**
 *
 * @author ...
 */
public class WFAAlgorithm extends KServerAbsAlgorithm {

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
            //@COUNT{CNT1, 1} 
            solution = ks.startWFA();
        } catch (Exception e) {
            System.out.println(e);
        }
        return solution;
    }
}