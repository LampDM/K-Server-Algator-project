
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
        int[][] fservers = new int[servers.length][servers[0].length];
        for(int i = 0;i<fservers.length;i++){
            for(int j = 0;j<fservers[i].length;j++){
                fservers[i][j]=servers[i][j];
            }
        }
        String solution = "";
        for (int i = 0; i < requests.length; i++) {
            int closest = KServerTools.findClosestG(requests[i], fservers);
            solution += Integer.toString(closest);
            
            //Move server to the new point
            KServerTools.moveTo(fservers[closest], requests[i]);
            
            if (i < requests.length - 1) {
                solution += ",";
            }
        }
        return solution;
    }

}
