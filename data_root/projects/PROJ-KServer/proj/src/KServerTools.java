
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author dani
 */
public class KServerTools {

    static String min_max_Offline(int[][] ks, int[][] req, int[] dimRange) {

        String solution = "";
        int negativeCycleC = 0;

        //Generate input for min cost max flow algorithm
        int nodeNumber = 2 * req.length + ks.length + 2;
        int[][] cap = new int[nodeNumber][nodeNumber];
        double[][] edge_cost_map = new double[nodeNumber][nodeNumber];

        //connect start to ks
        for (int i = 1; i <= ks.length; i++) {
            cap[0][i] = 1;
            edge_cost_map[0][1] = 0;
            edge_cost_map[1][0] = 0;
        }

        //connecting ks to first layer reqs
        for (int i = 1; i <= ks.length; i++) {

            cap[i][nodeNumber - 1] = 1;
            edge_cost_map[0][1] = 0;
            edge_cost_map[1][0] = 0;

            for (int k = 1; k <= req.length; k++) {
                cap[i][ks.length + k] = 1;

                //Get distance between ks[i-1] and req [k-1]
                edge_cost_map[i][ks.length + k] = calcDist(ks[i - 1], req[k - 1]);
                edge_cost_map[ks.length + k][i] = -1 * edge_cost_map[i][ks.length + k];

            }

        }
        //connecting first layer reqs to second layer reqs 
        int e = ks.length + 1;
        for (int k = e; k < e + req.length; k++) {
            cap[k][k + req.length] = 1;
            edge_cost_map[k][k + req.length] = -1000;
            edge_cost_map[k + req.length][k] = 1000;
        }

        int[][] ps = new int[1 + ks.length + req.length][dimRange.length];

        int ctr = 1;
        for (int k = req.length - 1; k > -1; k--) {
            ps[ps.length - ctr] = req[k];
            ctr++;
        }

        //connecting second layer of reqs to sink and to first layer of reqs
        int n = e + req.length;
        int c = 0;
        int s = 0;
        for (int k = n; k < req.length + n; k++) {
            c++;
            cap[k][nodeNumber - 1] = 1;
            for (int j = e + c; j < req.length + e; j++) {
                cap[k][j] = 1;

                edge_cost_map[k][j] = calcDist(req[s], ps[j]);
                edge_cost_map[j][k] = -1 * edge_cost_map[k][j];

            }

            s++;
        }

        int[][] residual = cap;

        int[][] original = new int[residual.length][residual[0].length];
        original = copyArray(original, residual);

        //cost test must be fixed
        double[][] res_cost = getCostMatrix(edge_cost_map, residual);

        //int maxflow = fordFulkerson(residual, 0, residual.length - 1);
        int maxflow = findSimpleMaxFlow(residual, ks.length);

        int[][] currentFlow = getFlow(residual, original);

        boolean negativeCycle = true;
        double[] d;
        int[][] pred;
        int[][] cycle;

        int lc = 0;
        //LOOP
        while (negativeCycle) {

            negativeCycle = false;

            //Make array of distances and fill it with infinity + sink node to 0
            d = new double[original.length];
            pred = new int[original.length][2];
            Arrays.fill(d, Double.MAX_VALUE / 2);
            d[d.length - 1] = 0;

            //Bellman ford d and pred is calculated
            //for every V
            for (int x = 0; x < residual.length; x++) {
                //for every E
                for (int k = 0; k < residual.length; k++) {

                    for (int j = 0; j < residual.length; j++) {
                        //if it's a valid edge
                        if (residual[k][j] == 1) {
                            if (d[j] > d[k] + res_cost[k][j]) {
                                //predecessor of j is edge from {k to j}
                                d[j] = d[k] + res_cost[k][j];
                                pred[j] = new int[]{k, j};
                            }
                        }
                    }
                }
            }

            //Detecting if negative cycle exists
            cycle = new int[residual.length * residual.length][2];

            //for every V
            outer:
            for (int x = 0; x < residual.length; x++) {
                //for every E
                for (int k = 0; k < residual.length; k++) {

                    for (int j = 0; j < residual.length; j++) {
                        //if it's a valid edge
                        if (residual[k][j] == 1) {
                            if (d[j] > d[k] + res_cost[k][j]) {
                                //negative cycle exists
                                negativeCycleC++;
                                cycle[0] = new int[]{k, j};
                                pred[j] = new int[]{k, j};
                                negativeCycle = true;
                                break outer;
                            }
                        }
                    }
                }
            }

            if (!negativeCycle) {
                //System.out.println("No negative cycles found!");
                break;
            } else {

                int[] prvedg;
                int i = 0;

                while (!contains(cycle, pred[cycle[i][0]])) {
                    prvedg = pred[cycle[i][0]];
                    i++;
                    cycle[i] = prvedg;
                }

                //Removing extra edges from cycle
                cycle = getEdges(cycle);

                //Creating new current flow
                for (int[] en : cycle) {

                    if ((en[0] == 0 && en[1] == 0)) {
                        continue;
                    }

                    if (currentFlow[en[1]][en[0]] == 1) {
                        currentFlow[en[1]][en[0]] = 0;

                    } else {

                        currentFlow[en[0]][en[1]] = 1;
                    }
                }

                residual = modifyResidual(original, currentFlow);

                res_cost = getCostMatrix(edge_cost_map, residual);

                lc++;
            }
        }

        String seqs = "";
        for (int k = 0; k < currentFlow[0].length; k++) {
            if (currentFlow[0][k] == 1) {
                //go down the rabbit hole
                String seq = traversePath(k, currentFlow);
                if (seqs.equals("")) {
                    seqs += seq;
                } else {
                    seqs += "a" + seq;
                }

            }
        }

        //Convert them to known solution
        int ri = -1;
        String[] seqlist = {""};

        for (int k = 0; k < req.length; k++) {
            ri = k + e;
            seqlist = seqs.split("a");
            String cchar = "";

            for (int j = 0; j < seqlist.length; j++) {
                if (Integer.parseInt(seqlist[j]) < e) {
                    cchar = seqlist[j];
                } else if (Integer.parseInt(seqlist[j]) == ri) {
                    solution += Integer.toString(Integer.parseInt(cchar) - 1) + ",";
                    break;

                }
            }

        }
        
        //Get rid of trailing comma
        solution = solution.substring(0, solution.length() - 1);
        
        //Returns a solution describing the indexes of the servers to serve the requests
        return solution;
    }

    private static int[][] copyArray(int[][] original, int[][] test) {

        for (int k = 0; k < test.length; k++) {
            for (int j = 0; j < test[k].length; j++) {
                original[k][j] = test[k][j];
            }
        }
        return original;
    }

    private static double[][] getCostMatrix(double[][] edge_cost_map, int[][] residual) {
        double[][] cm = new double[residual.length][residual[0].length];
        for (int k = 0; k < residual.length; k++) {
            for (int j = 0; j < residual[k].length; j++) {
                if (residual[k][j] == 1) {
                    cm[k][j] = edge_cost_map[k][j];
                }

            }
        }
        return cm;
    }

    private static int findSimpleMaxFlow(int[][] residual, int kl) {

        int[] temp = new int[residual[0].length];
        temp = residual[0];
        residual[0] = residual[residual.length - 1];
        residual[residual.length - 1] = temp;

        for (int k = 1; k <= kl; k++) {
            residual[k][0] = 1;
            residual[k][residual[k].length - 1] = 0;
        }

        return kl;
    }

    private static int[][] getFlow(int[][] test, int[][] original) {

        int[][] flowMatrix = new int[test.length][test[0].length];

        for (int k = 0; k < test.length; k++) {
            for (int j = 0; j < test[k].length; j++) {
                if (0 > (test[k][j] - original[k][j])) {
                    flowMatrix[k][j] = 1;
                }

            }
        }
        return flowMatrix;
    }

    private static boolean contains(int[][] cycle, int[] i) {
        boolean value = false;
        for (int[] c : cycle) {
            if (c == i) {
                return true;
            }
        }
        return false;
    }

    private static int[][] getEdges(int[][] cycle) {
        int[][] ncycle = new int[cycle.length][2];
        int key = -1;
        for (int k = ncycle.length - 1; k > -1; k--) {
            if (cycle[k][0] == 0 && cycle[k][1] == 0) {
                continue;
            }
            if (key == -1) {
                key = cycle[k][0];
                ncycle[k] = cycle[k];
            } else {
                ncycle[k] = cycle[k];
                if (cycle[k][1] == key) {
                    break;
                }

            }

        }
        return ncycle;
    }

    private static int[][] modifyResidual(int[][] original, int[][] currentFlow) {
        int[][] test = new int[original.length][original[0].length];
        for (int k = 0; k < original.length; k++) {
            for (int j = 0; j < original[0].length; j++) {

                if (currentFlow[k][j] == 1) {
                    test[k][j] = 0;
                    test[j][k] = 1;
                } else if (original[k][j] == 1) {
                    test[k][j] = 1;
                }

            }
        }
        return test;
    }

    private static String traversePath(int k, int[][] currentFlow) {
        String seq = "" + k;
        for (int j = 0; j < currentFlow[0].length; j++) {
            if (currentFlow[k][j] == 1) {
                seq = seq + "a" + traversePath(j, currentFlow);
            }
        }
        return seq;
    }

    public KServerTools() {

    }

    public static void printSomething(int[][] something) {
        for (int i = 0; i < something.length; i++) {
            System.out.print(" ");
            for (int j = 0; j < something[i].length; j++) {
                System.out.print(something[i][j]);
                if (j + 1 != something[i].length) {
                    System.out.print(",");
                }

            }
        }
        System.out.println("");
    }

    public static double calcDist(int[] a, int[] b) {
        double rez = 0.0;
        for (int i = 0; i < a.length; i++) {
            rez += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(rez);
    }
    
    public static int findClosestG(int[] request, int[][] servers){
        int sol = 0;
        Double minDist = Double.MAX_VALUE;
        Double cur = 0.0;
        for(int i = 0;i<servers.length;i++){
            cur = calcDist(request,servers[i]);
            if(minDist>cur){
                minDist=cur;
                sol=i;
            }
        }
        return sol;
    }
    
    public static int findClosestH(int[] request, int[][] servers) {
        
        double totaldist = 0;

        double N = 0;
        
        for(int i = 0;i<servers.length;i++){
            N += (1/calcDist(request,servers[i]));
        }
        
        double[][] serverprobs = new double[servers.length][2];
        
        for(int i = 0;i<serverprobs.length;i++){
            serverprobs[i][0]=1 / (N * calcDist(request, servers[i]));
            serverprobs[i][1]=i;
        }
        
        Comparator<double[]> comp = new Comparator<double[]>() {
            public int compare(double[] p1, double[] p2) {
                int i = 0;
                if (p1[0] < p2[0]) {
                    i = 1;
                } else if (p1[0] > p2[0]) {
                    i = -1;
                }
                return i;
            }
        };

        Arrays.sort(serverprobs, comp);
        
        double roll = Math.random();
        double total = 0;
        for(int i = 0;i<serverprobs.length;i++){
            total += serverprobs[i][0];
            if(roll <= total){
                //Choose this one
                return (int)serverprobs[i][1];
            }
        }
        
        return 0;
    }
    
    public static void moveTo(int[] server, int[] request){
        for(int i = 0;i<server.length;i++){
            server[i]=request[i];
        }
    
    }

    public static double distTravelled(String movements, int[][] servers, int[][] requests) {
        double dist = 0.0;
        int[][] ks = new int[servers.length][servers[0].length];
        int[][] req = new int[requests.length][requests[0].length];

        //Copying arrays
        for (int i = 0; i < servers.length; i++) {
            moveTo(ks[i],servers[i]);
        }

        for (int i = 0; i < requests.length; i++) {
            moveTo(req[i],requests[i]);
        }

        String[] movs = movements.split(",");

        for (int i = 0; i < req.length; i++) {
            //server servers[movs[i]] to requests[i], change position of servers[movs[i]]
            int mov = Integer.parseInt(movs[i]);
            dist += calcDist(ks[mov], req[i]);
            moveTo(ks[mov],req[i]);
        }
        return dist;
    }

}
