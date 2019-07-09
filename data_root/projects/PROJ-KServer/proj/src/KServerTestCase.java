
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import si.fri.algotest.entities.Variables;
import si.fri.algotest.execute.AbstractTestCase;

/**
 *
 * @author ...
 */
public class KServerTestCase extends AbstractTestCase {

    @Override
    public KServerInput getInput() {
        return (KServerInput) super.getInput();
    }

    @Override
    public KServerOutput getExpectedOutput() {
        return (KServerOutput) super.getExpectedOutput();
    }

    @Override
    public KServerTestCase getTestCase(String testCaseDescriptionLine, String path) {
        // create a set of variables ...
        Variables inputParameters = new Variables();
        inputParameters.setVariable("Path", path);

        // TODO:
        // ... using the testCaseDescriptionLine determine the parameters of
        //     the test case and add them to the inputParameters set ...            
        // ... Example: if description line contains two parameters (name_of_test and size N) separated with ":"
        String[] fields = testCaseDescriptionLine.split(":");
        if (fields.length < 7) {
            return null;
        }
        //TODO Maybe convert to doubles?
        //Name of test
        inputParameters.setVariable("Test", fields[0]);

        // K servers and R requests
        inputParameters.setVariable("K", fields[1]);
        inputParameters.setVariable("R", fields[2]);

        // Size of field in X and Y
        inputParameters.setVariable("X", fields[3]);
        inputParameters.setVariable("Y", fields[4]);

        // Type of starting position of K
        inputParameters.setVariable("spostype", fields[5]);

        // Type of requests or mode of functioning ex. RANDOM for full random requests
        inputParameters.setVariable("reqstype", fields[6]);

        // ... and finally, create a test case determined by these parameters
        return generateTestCase(inputParameters);
    }

    @Override
    public KServerTestCase generateTestCase(Variables inputParameters) {
        String path = inputParameters.getVariable("Path", "").getStringValue();

        // TODO: 
        // ... read the values of the parameters and create a corresponding test case
        // ... Example: if test case is an array of integers of size "N"
        // int size = inputParameters.getVariable("N", 0).getIntValue();              
        // int [] array = new int[size];
        // create a test case 
        int k = inputParameters.getVariable("K", 0).getIntValue();
        int r = inputParameters.getVariable("R", 0).getIntValue();

        int x = inputParameters.getVariable("X", 0).getIntValue();
        int y = inputParameters.getVariable("Y", 0).getIntValue();

        //Since servers can only hold 1 position if there are more places than them we return an error.
        if (k > x * y) {
            return null;
        }

        String spostype = inputParameters.getVariable("spostype", 0).getStringValue();
        String reqstype = inputParameters.getVariable("reqstype", 0).getStringValue();

        // Make
        int[][] ks = new int[k][2];
        int[][] req = new int[r][2];

        Random rand = new Random(System.currentTimeMillis());

        //To prevent servers being in the same starting position
        boolean[][] seen = new boolean[x][y];

        // Set initial server positions
        switch (spostype) {
            case "RANDOM":
                // Servers start completely randomly
                int posx = rand.nextInt(x);
                int posy = rand.nextInt(y);
                for (int i = 0; i < ks.length; i++) {

                    do {
                        posx = rand.nextInt(x);
                        posy = rand.nextInt(y);
                    } while (seen[posx][posy]);

                    ks[i][0] = posx;
                    ks[i][1] = posy;
                    seen[posx][posy] = true;
                }
                break;

            default:
                throw new IllegalArgumentException("Initial server position invalid argument! Try RANDOM!");
        }

        // Set request positions
        switch (reqstype) {
            // Requests appear completely randomly
            case "RANDOM":
                for (int i = 0; i < req.length; i++) {
                    req[i][0] = rand.nextInt(x);
                    req[i][1] = rand.nextInt(y);
                }
                break;
            default:
                throw new IllegalArgumentException("Request appearance method invalid argument! Try RANDOM!");
        }

        KServerTestCase kServerTestCase = new KServerTestCase();
        kServerTestCase.setInput(new KServerInput(ks, req, x, y));
        kServerTestCase.getInput().setParameters(inputParameters);

        //Use indexes for solution format, so for example 0 means first servers, 1 means second etc.
        String optimal_solution = "";
        
        //TODO fix solution format into index,index,index,index
        try {
            Ks kst = new Ks(ks, req, x, y);
            optimal_solution = kst.min_max_Offline();
        } catch (Exception e) {
            System.out.println(e);
            //TODO what happens if error?
        }

        kServerTestCase.setExpectedOutput(new KServerOutput(optimal_solution, ks, req));

        return kServerTestCase;

    }

}
