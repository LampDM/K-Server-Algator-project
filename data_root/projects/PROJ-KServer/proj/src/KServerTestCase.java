
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
        switch (sposTypeHandler(spostype)) {
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
            case "INLINE":
                spostype = spostype.replaceAll("INLINE ", "");
                String[] cords = spostype.split(" ");
                for (int i = 0; i < cords.length; i++) {
                    ks[i][0] = Integer.parseInt(cords[i].split(",")[0]);
                    ks[i][1] = Integer.parseInt(cords[i].split(",")[1]);
                }
                break;
            default:
                throw new IllegalArgumentException("Initial server position invalid argument! Try RANDOM!");
        }

        // Set request positions
        switch (reqsTypeHandler(reqstype)) {
            // Requests appear completely randomly
            case "RANDOM":
                for (int i = 0; i < req.length; i++) {
                    req[i][0] = rand.nextInt(x);
                    req[i][1] = rand.nextInt(y);
                }
                break;
            case "INLINE":
                reqstype = reqstype.replaceAll("INLINE ", "");
                String[] cords = reqstype.split(" ");
                for (int i = 0; i < cords.length; i++) {
                    req[i][0] = Integer.parseInt(cords[i].split(",")[0]);
                    req[i][1] = Integer.parseInt(cords[i].split(",")[1]);
                }
                break;
            //The margins are 20% and 80% for x and y
            case "CORNER":
                for (int i = 0; i < req.length; i++) {
                    //70% of reqs appear in the corner
                    if (i < 0.7 * req.length) {
                        req[i][0] = rand.nextInt((int) Math.round(x * 0.2));
                        req[i][1] = rand.nextInt((int) Math.round(y * 0.2));
                        //30% of reqs appear elsewhere
                    } else {
                        int xh = (int) (x * 0.2);
                        int yh = (int) (y * 0.2);
                        //Random number from xh to x and yh to y
                        req[i][0] = (int) (rand.nextInt(x - xh) + xh);
                        req[i][1] = (int) (rand.nextInt(y - yh) + yh);
                    }
                }
                break;
            //The margins are 20% then 45% then 35% for x and y
            case "WAVE":
                for (int i = 0; i < req.length; i++) {

                    //20% of reqs in the corner
                    if (i < 0.2 * req.length) {
                        req[i][0] = rand.nextInt((int) Math.round(x * 0.2));
                        req[i][1] = rand.nextInt((int) Math.round(y * 0.2));
                    } else //60% of reqs in the center wave
                    if ((i > 0.2 * req.length) && (i < 0.8 * req.length)) {
                        int xh = (int) (x * 0.2);
                        int yh = (int) (y * 0.2);
                        int xd = (int) (x * 0.7);
                        int yd = (int) (y * 0.7);
                        //Random number from xh to x and yh to y
                        req[i][0] = (int) (rand.nextInt(xd - xh) + xh);
                        req[i][1] = (int) (rand.nextInt(yd - yh) + yh);
                        //20% of reqs in the last wave
                    } else {
                        int xh = (int) (x * 0.7);
                        int yh = (int) (y * 0.7);
                        //Random number from xh to x and yh to y
                        req[i][0] = (int) (rand.nextInt(x - xh) + xh);
                        req[i][1] = (int) (rand.nextInt(y - yh) + yh);
                    }
                }
                break;
            case "FILE":
                String filename = reqstype.split(" ")[1];
                String testfile = path + File.separator + filename;
                        
                File file = new File(testfile);
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader(file));
                    String st = br.readLine();
                    String[] rqs = st.split(" ");
                    for(int i = 0;i<req.length;i++){
                        String[] duo = rqs[i].split(",");
                        req[i][0] = Integer.parseInt(duo[0]);
                        req[i][1] = Integer.parseInt(duo[1]);
                    }
                    
                } catch (Exception ex) {
                    System.out.println(ex);
                    //TODO what to do with exception?
                }

                break;

            default:
                throw new IllegalArgumentException("Request appearance method invalid argument! Try RANDOM!");
        }
        //For debugging purposes
        //KServerTools.printSomething(ks);
        //KServerTools.printSomething(req);

        KServerTestCase kServerTestCase = new KServerTestCase();

        kServerTestCase.setInput(
                new KServerInput(ks, req, x, y));
        kServerTestCase.getInput()
                .setParameters(inputParameters);

        //Use indexes for solution format, so for example 0 means first servers, 1 means second etc.
        String optimal_solution = "";

        try {
            Ks kst = new Ks(ks, req, x, y);
            optimal_solution = kst.min_max_Offline();
        } catch (Exception e) {
            System.out.println(e);
            //TODO what happens if error?
        }

        kServerTestCase.setExpectedOutput(
                new KServerOutput(optimal_solution, ks, req));

        return kServerTestCase;

    }

    public String sposTypeHandler(String spostype) {
        if (spostype.startsWith("RANDOM")) {
            return "RANDOM";
        }
        if (spostype.startsWith("INLINE")) {
            return "INLINE";
        }
        if (spostype.startsWith("FILE")) {
            return "FILE";
        }
        return null;
    }

    public String reqsTypeHandler(String reqstype) {
        if (reqstype.startsWith("RANDOM")) {
            return "RANDOM";
        }
        if (reqstype.startsWith("INLINE")) {
            return "INLINE";
        }
        if (reqstype.startsWith("CORNER")) {
            return "CORNER";
        }
        if (reqstype.startsWith("WAVE")) {
            return "WAVE";
        }
        if (reqstype.startsWith("FILE")) {
            return "FILE";
        }
        return null;
    }

}
