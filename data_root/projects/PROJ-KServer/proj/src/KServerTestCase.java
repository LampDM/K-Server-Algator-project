
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Hashtable;
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

        String[] fields = testCaseDescriptionLine.split(":");
        if (fields.length < 6) {
            return null;
        }

        //Name of test
        inputParameters.setVariable("Test", fields[0]);

        // K servers and R requests
        inputParameters.setVariable("K", fields[1]);
        inputParameters.setVariable("R", fields[2]);

        // Get the dimensions of testing space
        inputParameters.setVariable("DIM", fields[3]);

        // Type of starting position of K
        inputParameters.setVariable("spostype", fields[4]);

        // Type of requests or mode of functioning ex. RANDOM for full random requests
        inputParameters.setVariable("reqstype", fields[5]);

        return generateTestCase(inputParameters);
    }

    @Override
    public KServerTestCase generateTestCase(Variables inputParameters) {
        String path = inputParameters.getVariable("Path", "").getStringValue();

        int k = inputParameters.getVariable("K", 0).getIntValue();
        int r = inputParameters.getVariable("R", 0).getIntValue();

        String[] dim = inputParameters.getVariable("DIM", 0).getStringValue().split(" ");
        int dimensions = dim.length;
        int[] dimRange = new int[dimensions];

        //Set up the ranges of each dimension
        for (int i = 0; i < dimensions; i++) {
            dimRange[i] = Integer.parseInt(dim[i]);
        }

        String spostype = inputParameters.getVariable("spostype", 0).getStringValue();
        String reqstype = inputParameters.getVariable("reqstype", 0).getStringValue();

        // Make
        int[][] ks = new int[k][dimensions];
        int[][] req = new int[r][dimensions];

        //Determine group of test by size of K,R and Dimensions
        int S = 0;
        for (int j = 0; j < dimensions; j++) {
            if (dimRange[j] > 15) {
                S++;
            }
            if (dimRange[j] < 6) {
                S--;
            }
        }

        if (k > 4) {
            S++;
        }
        if (r > 13) {
            S++;
        }
        if (r < 5) {
            S--;
        }
        if (S != 0) {
            String gname = (S > 0) ? "LARGE" : "SMALL";
            inputParameters.setVariable("Group", gname);
        }

        //Since servers can only hold 1 position if there are more places than them we return an error
        int allLocs = dimRange[0];
        for (int j = 1; j < dimensions; j++) {
            allLocs *= dimRange[j];
        }
        if (k > allLocs) {
            return null;
        }

        Random rand = new Random(System.currentTimeMillis());

        //To prevent servers being in the same starting position
        Hashtable<String, String> seen = new Hashtable<>();
        StringBuilder dims = new StringBuilder();
        
        // Set initial server positions
        switch (sposTypeHandler(spostype)) {
            case "RANDOM":
                // Servers start completely randomly
                for (int i = 0; i < ks.length; i++) {
                    
                    do {
                        dims.setLength(0);
                        for (int j = 0; j < dimensions; j++) {
                            dims.append(rand.nextInt(dimRange[j]));
                        }
                        
                    } while (seen.containsKey(dims.toString()));
                    
                    seen.put(dims.toString(), "");
                    
                    String[] dimsl = dims.toString().split("");
                    for (int j = 0; j < dimensions; j++) {
                        ks[i][j] = Integer.parseInt(dimsl[j]);
                    }
                }
                break;
            case "INLINE":
                spostype = spostype.replaceAll("INLINE ", "");
                String[] cords = spostype.split(" ");
                for (int i = 0; i < cords.length; i++) {
                    String[] cord = cords[i].split(",");
                    for (int j = 0; j < dimensions; j++) {
                        ks[i][j] = Integer.parseInt(cord[j]);
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Initial server position invalid argument! Try RANDOM!");
        }

        // Set request positions
        switch (reqsTypeHandler(reqstype)) {
            case "RANDOM":
                // Requests start completely randomly
                for (int i = 0; i < req.length; i++) {
                    for (int j = 0; j < dimensions; j++) {
                        req[i][j] = rand.nextInt(dimRange[j]);
                    }
                }
                break;
            case "INLINE":
                reqstype = reqstype.replaceAll("INLINE ", "");
                String[] cords = reqstype.split(" ");
                for (int i = 0; i < cords.length; i++) {
                    String[] cord = cords[i].split(",");
                    for (int j = 0; j < dimensions; j++) {
                        req[i][j] = Integer.parseInt(cord[j]);
                    }
                }
                break;

            //The margins are 20% and 80% for x and y
            case "CORNER":
                for (int i = 0; i < req.length; i++) {
                    //70% of reqs appear in the corner
                    if (i < 0.7 * req.length) {
                        for (int j = 0; j < dimensions; j++) {
                            req[i][j] = rand.nextInt((int) Math.round(dimRange[j] * 0.2));
                        }

                        //30% of reqs appear elsewhere
                    } else {
                        for (int j = 0; j < dimensions; j++) {
                            //Random number from xh to x and yh to y
                            int jh = (int) (dimRange[j] * 0.2);
                            req[i][j] = (int) (rand.nextInt(dimRange[j] - jh) + jh);
                        }
                    }
                }
                break;
            //The margins are 20% then 45% then 35% for x and y
            case "WAVE":
                for (int i = 0; i < req.length; i++) {

                    //20% of reqs in the corner
                    if (i < 0.2 * req.length) {
                        for (int j = 0; j < dimensions; j++) {
                            req[i][j] = rand.nextInt((int) Math.round(dimRange[j] * 0.2));
                        }

                    } else //60% of reqs in the center wave
                     if ((i > 0.2 * req.length) && (i < 0.8 * req.length)) {
                            for (int j = 0; j < dimensions; j++) {
                                int jh = (int) (dimRange[j] * 0.2);
                                int jd = (int) (dimRange[j] * 0.7);
                                //Random number from jh to j
                                req[i][j] = (int) (rand.nextInt(jd - jh) + jh);
                            }
                            //20% of reqs in the last wave
                        } else {
                            for (int j = 0; j < dimensions; j++) {
                                int jh = (int) (dimRange[j] * 0.7);
                                //Random number from jh to j
                                req[i][j] = (int) (rand.nextInt(dimRange[j] - jh) + jh);
                            }
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
                    for (int i = 0; i < req.length; i++) {
                        String[] multi = rqs[i].split(",");
                        for (int j = 0; j < dimensions; j++) {
                            req[i][j] = Integer.parseInt(multi[j]);
                        }
                    }

                } catch (Exception ex) {
                    System.out.println(ex);
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
                new KServerInput(ks, req, dimRange));
        kServerTestCase.getInput()
                .setParameters(inputParameters);

        //Use indexes for solution format, so for example 0 means first servers, 1 means second etc.
        String optimal_solution = "";

        try {
            optimal_solution = KServerTools.min_max_Offline(ks, req, dimRange);
        } catch (Exception e) {
            System.out.println(e);
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
