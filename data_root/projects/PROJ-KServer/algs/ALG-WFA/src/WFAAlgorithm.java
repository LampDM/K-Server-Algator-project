/**
 *
 * @author ...
 */
public class WFAAlgorithm extends KServerAbsAlgorithm {

    @Override
    protected KServerOutput execute(KServerInput testCase) {

        KServerOutput result = new KServerOutput("",testCase.ks,testCase.req);

        result.solution = execute(testCase.ks, testCase.req, testCase.dimRange);
        return result;
    }

    public String execute(int[][] servers, int[][] requests, int[] dimRange) {
        String solution = "";
        try {
            if (dimRange.length == 2) {
                Ks ks = new Ks(servers, requests, dimRange[0], dimRange[1]);
                solution = ks.startWFA();
            }else{
                System.out.println("Warning - WFA only works with two dimensions, returning empty string.");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return solution;
    }
}