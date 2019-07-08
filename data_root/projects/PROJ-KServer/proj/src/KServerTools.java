/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author dani
 */
public class KServerTools {

    //Re-modelled the point naming system 1 coordinate == 1 letter
    public Point[] ks;
    public Point[] req;
    public static String[] perms; //Permutations
    public static int pc = 0; //Permutation counter
    public double globaldistC = 0;

    //WFA data
    static double[][] wfa_w;
    static String[][] wfa_s;
    static String[] wfa_combos;
    static String[] wfa_reqs;
    public static int combc;

    //name to point --> np
    public HashMap<Integer, Point> np = new HashMap<Integer, Point>();

    //comb to indx
    public static HashMap<String, Integer> ci = new HashMap<String, Integer>();

    public char[][] cordnames;
    public Point[][] cordpoints;

    public KServerTools(int[][] servers, int[][] requests, int x, int y) throws FileNotFoundException {
        
        this.cordnames = new char[x][y];
        this.cordpoints = new Point[x][y];
        
        //Setup
        ks = new Point[servers.length];
        req = new Point[requests.length];


        char c = (char) 1000;

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                cordnames[i][j] = (char) String.valueOf(c).charAt(0);
                cordpoints[i][j] = new Point(i, j);
                cordpoints[i][j].name = cordnames[i][j];
                np.put((int) cordnames[i][j], cordpoints[i][j]);
                c++;
            }
        }
        
        //Server locations being set
        for (int i = 0; i < servers.length; i++) {
            ks[i] = cordpoints[servers[i][0]][servers[i][1]];
        }
        
        //Request locations being set
        for (int i = 0; i < requests.length; i++) {
            req[i] = cordpoints[requests[i][0]][requests[i][1]];
        }


        
    }

    public String startGreedy() {
        String solution = "";
        for (Point r : req) {
            solution = solution + closest(r);

        }
        resetK();
        return solutionToIndexes(solution, this.ks);
    }

    void startHarmonic(boolean v) {
        //System.out.println(" _|Harmonic starting|_ ");
        //printPoints(ks);
        //printPoints(req);
        String solution = "";
        long startTime = System.nanoTime();
        for (Point r : req) {
            solution = solution + closestH(r);

        }
        long estimatedTime = System.nanoTime() - startTime;
        //System.out.println(solution);
        System.out.print(globaldistC);
        System.out.print(",");
        System.out.print(estimatedTime);

        resetK();

    }

    void startWFA(boolean v) {
        //System.out.println(" _|WFA starting|_ ");
        //printPoints(ks);
        //printPoints(req);
        String solution = "";

        int xvrstica = binomial(121, ks.length).intValue();
        int yvrstica = req.length;
        wfa_w = new double[xvrstica][yvrstica + 1];
        wfa_s = new String[xvrstica][yvrstica];
        wfa_combos = new String[xvrstica];
        wfa_reqs = new String[yvrstica + 1];
        combc = 0;

        String starter = "";
        long startTime = System.nanoTime();

        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                starter += cordnames[i][j];

            }
        }
        //System.out.println(starter);
        String arr[] = starter.split("");

        //FIlls up wfa_combos with all the combinations
        //Also maps combs to indxs - ci
        setCombinations(arr, arr.length, ks.length);
        Arrays.sort(wfa_combos);

        //Set up request values
        for (int k = 0; k < wfa_reqs.length; k++) {
            if (k == 0) {
                wfa_reqs[k] = "\n";
            } else {
                wfa_reqs[k] = "" + (char) req[k - 1].name;
            }

        }

        //Find the points that corrispond to the first configuration ABC and place them on position 0
        String sk = "";
        for (Point p : ks) {
            sk += (char) p.name;
        }

        sk = sortString(sk);

        //System.out.println(sk);
        int first = ci.get(sk);

        String holder = "";

        //Switching nth column with 0th
        holder = wfa_combos[first];
        wfa_combos[first] = wfa_combos[0];
        ci.put(wfa_combos[0], first);
        wfa_combos[0] = holder;
        ci.put(holder, 0);

        //Getting first row done
        for (int k = 0; k < wfa_combos.length; k++) {

            wfa_w[k][0] = wfa_D(wfa_combos[0], wfa_combos[k]);

        }

        //Getting the rest done
        for (int i = 1; i < wfa_reqs.length; i++) {
            for (int k = 0; k < wfa_combos.length; k++) {
                wfa_w[k][i] = minWFA(k, i);
            }
        }

        //System.out.println("REST DONE");
        //printMatrix(wfa_w);
        //Getting final solution
        String current = wfa_combos[0];
        String combos = current;

        for (int k = 1; k < wfa_reqs.length; k++) {
            int j = ci.get(current);
            solution = solution + wfa_s[j][k - 1];
            current = changeCombination(wfa_s[j][k - 1], current, wfa_reqs[k]);

            combos = combos + " " + current;
        }
        long estimatedTime = System.nanoTime() - startTime;
        //System.out.println("combos: " + combos);
        String[] combo_sequence = combos.split(" ");

        double distvalue = 0;

        //Now convert to normal solution format
        solution = "";
        String[] prev = combo_sequence[0].split("");
        String[] oldnames = combo_sequence[0].split("");
        for (int k = 1; k < combo_sequence.length; k++) {
            for (int j = 0; j < prev.length; j++) {
                if (!combo_sequence[k].contains(prev[j])) {
                    solution += oldnames[j];
                    //replace prev[j] with which sign?
                    //najdi razliko med prev in combo_sequence[k]
                    for (int i = 0; i < prev.length; i++) {
                        if (i != j) {
                            combo_sequence[k] = combo_sequence[k].replace(prev[i], "");
                        }
                    }
                    //System.out.println(prev[j]+" went to "+combo_sequence[k]);
                    Point A = np.get((int) prev[j].charAt(0));
                    Point B = np.get((int) combo_sequence[k].charAt(0));
                    distvalue += calcDist(A.x, A.y, B.x, B.y);
                    //System.out.println(calcDist(A.x, A.y, B.x, B.y));
                    prev[j] = combo_sequence[k];
                    //System.out.println("New prev "+prev[0]+prev[1]);
                    break;
                } else {
                    if (j + 1 == prev.length) {

                    }
                }
            }
        }

        //
        System.out.println(distvalue);
        System.out.println(solution);
        //System.out.print(",");
        //System.out.print(estimatedTime);
        resetK();

    }

    private double calcDist(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y2 - y1), 2));
    }

    void printArr(int[] arr) {
        for (int k = 0; k < arr.length; k++) {
            System.out.printf("%d", arr[k]);
        }
        System.out.println("");
    }

    void printPoints(Point[] arr) {
        for (int k = 0; k < arr.length; k++) {
            System.out.printf(" %c:(%d,%d) ", arr[k].name, (int) arr[k].x, (int) arr[k].y);
        }
        System.out.println("");
    }

    public void permutation1(String str, int c) {
        pc = 0;
        permutation2("", str, c);
    }

    private static void permutation2(String prefix, String str, int c) {
        int r = str.length();
        if (c == 0) {
            perms[pc] = prefix;
            //System.out.println(perms[pc]);
            pc++;

        } else {
            for (int i = 0; i < r; i++) {
                permutation2(prefix + str.charAt(i), str, c - 1);
            }
        }
    }

    static void combinationUtil(String arr[], String data[], int start,
            int end, int index, int r) {
        // Current combination is ready to be printed, print it
        StringBuilder sb = new StringBuilder("");
        //String str = "";
        if (index == r) {
            for (int j = 0; j < r; j++) {
                //System.out.print(data[j] + "");
                sb.append(data[j]);
            }
            //System.out.println("");
            wfa_combos[combc] = sortString(sb.toString());
            ci.put(sb.toString(), combc);
            //System.out.println(combc+" "+str);
            combc++;
            return;
        }

        for (int i = start; i <= end && end - i + 1 >= r - index; i++) {
            data[index] = arr[i];
            combinationUtil(arr, data, i + 1, end, index + 1, r);
        }
    }

    static void setCombinations(String arr[], int n, int r) {
        String data[] = new String[r];

        combinationUtil(arr, data, 0, n - 1, 0, r);
    }

    static BigInteger binomial(final int N, final int K) {
        BigInteger ret = BigInteger.ONE;
        for (int k = 0; k < K; k++) {
            ret = ret.multiply(BigInteger.valueOf(N - k))
                    .divide(BigInteger.valueOf(k + 1));
        }
        return ret;
    }

    private String optimize(Point[] subReqs) {
        String opt = perms[0];
        double dist = Integer.MAX_VALUE;
        double mindist = Integer.MAX_VALUE;

        for (String s : perms) {
            dist = getDist(s, subReqs);

            if (dist < mindist) {
                mindist = dist;
                opt = s;
            }
        }

        for (int i = 0, n = opt.length(); i < n; i++) {
            char c2 = opt.charAt(i);
            for (Point k1 : ks) {
                if (k1.name == (int) c2) {
                    //System.out.println((char) c2);
                    //System.out.println(k1.nx + " -> " + subReqs[i].x);
                    //System.out.println(k1.ny + " -> " + subReqs[i].y);

                    k1.nx = subReqs[i].x;
                    k1.ny = subReqs[i].y;

                }
            }

        }

        //System.out.println(mindist);
        globaldistC = globaldistC + mindist;
        return opt;
    }

    private double getDist(String s, Point[] subReqs) {
        double dist = 0;
        Point[] cks = ks;
        for (int i = 0, n = s.length(); i < n; i++) {
            char c = s.charAt(i);
            for (Point k : cks) {
                //This function only gets the distanced
                //The thing should move through the whole string here but this conflicts with the bs
                if (k.name == (int) c) {
                    dist = dist + calcDist(k.nx, k.ny, subReqs[i].x, subReqs[i].y);
                    k.nx = subReqs[i].x;
                    k.ny = subReqs[i].y;
                }
            }

        }

        return dist;
    }

    private char closest(Point r) {
        char c = (char) ks[0].name;
        Point x = r;
        double dist = Integer.MAX_VALUE;
        double mindist = Integer.MAX_VALUE;
        //TODO fix this autistic bug where if request is the same point as where we started distance gets counted as 0?
        for (Point k : ks) {

            dist = calcDist(k.nx, k.ny, r.x, r.y);
            if (dist < mindist) {
                //System.out.println(dist);
                mindist = dist;
                c = (char) k.name;
                x = k;

            }

        }
        //System.out.println(mindist);
        globaldistC = globaldistC + mindist;
        //System.out.println(x.nx + " -> " + r.x);
        //System.out.println(x.ny + " -> " + r.y);
        x.nx = r.x;
        x.ny = r.y;
        return c;
    }

    private char closestH(Point r) {
        char c = (char) ks[0].name;
        Point x = r;
        double totaldist = 0;

        double N = 0;
        for (Point k : ks) {
            N = N + (1 / calcDist(k.nx, k.ny, r.nx, r.ny));
        }

        for (Point k : ks) {
            k.prob = 1 / (N * calcDist(k.nx, k.ny, r.nx, r.ny));
        }

        Comparator<Point> pointComparator = new Comparator<Point>() {
            public int compare(Point p1, Point p2) {
                int i = 0;
                if (p1.prob < p2.prob) {
                    i = 1;
                } else if (p1.prob > p2.prob) {
                    i = -1;
                }
                return i;
            }
        };

        Arrays.sort(ks, pointComparator);

        double roll = Math.random();
        double total = 0;
        for (Point k : ks) {
            total += k.prob;
            if (roll <= total) {
                //Choose this one
                x = k;
                c = (char) k.name;
                globaldistC += calcDist(k.nx, k.ny, r.x, r.y);
                break;
            }
        }

        x.nx = r.x;
        x.ny = r.y;
        return c;
    }

    private void resetK() {
        for (Point k : ks) {
            k.nx = k.x;
            k.ny = k.y;
        }
        globaldistC = 0;
    }

    private Point[] invertProbabilities(Point[] ks) {
        int n = 0;
        double m = 0.0;
        if (ks.length % 2 == 0) {
            n = ks.length / 2;
            for (int i = 0; i < n; i++) {

                m = ks[i].prob;
                ks[i].prob = ks[ks.length - 1 - i].prob;
                ks[ks.length - 1 - i].prob = m;
            }
        } else {
            n = (ks.length - 1) / 2;
            for (int i = 0; i < n; i++) {

                m = ks[i].prob;
                ks[i].prob = ks[ks.length - 1 - i].prob;
                ks[ks.length - 1 - i].prob = m;
            }
        }

        return ks;
    }

    private String subOffline(Point[] subReqs) {

        perms = new String[(int) Math.pow(ks.length, subReqs.length)];

        String s = "";

        char c = (char) 65;
        for (Point k1 : ks) {
            s = s + c;
            c = (char) ((int) c + 1);
        }

        permutation1(s, subReqs.length);

        String solution = optimize(subReqs);

        return solution;
    }

    private double minWFA(int j, int i) {
        double val = 0;
        String c = "";
        String bestc = "";
        String abc = wfa_combos[j];
        String sr = wfa_reqs[i];
        double best_total = Double.MAX_VALUE;

        //TODO - Imamo ABC ter X
        // w0(ABX) + d(C,X)
        // w0(XBC) + d(A,X)
        // w0(AXC) + d(B,X)
        //MIN OD TEGA
        //System.out.println("doing "+ wfa_combos[j]);
        if (abc.contains(sr)) {
            wfa_s[j][i - 1] = sr;
            return wfa_w[j][i - 1];

        } else {

            for (int k = 0; k < abc.length(); k++) {

                c = "" + abc.charAt(k); //d(c,sr) x je v tem primeru sr
                String newc = createCombo(abc, k, sr);

                double dist = Double.MAX_VALUE;
                double wv = Double.MAX_VALUE;

                //Sorting our string alphabetically
                newc = sortString(newc);

                //Use dict to find comb id instantly
                int h = ci.get(newc);

                int ci = h;//combo id and "i" is the req number(int)
                Point p1 = np.get((int) (c.charAt(0)));
                Point p2 = np.get((int) (sr.charAt(0)));
                dist = calcDist(p1.x, p1.y, p2.x, p2.y);
                wv = wfa_w[h][i - 1];
                if ((dist + wv) < best_total) {
                    best_total = dist + wv;
                    bestc = c;
                }

            }
            wfa_s[j][i - 1] = bestc;
            return best_total;
        }

    }

    private double wfa_D(String abc, String xyz) {
        //Optimised - finds the least costly move pattern
        double val = 0;
        double mindist;
        double curdist;
        String taken = "";
        Point p1;
        Point p2;
        char c1;
        char c2;
        char curchar2;

        int[] choicesABC = new int[abc.length()];
        int[] choicesXYZ = new int[xyz.length()];

        for (int i = 0; i < abc.length(); i++) {
            mindist = Double.MAX_VALUE;
            curdist = Double.MAX_VALUE;
            int curindx1 = 0;
            int curindx2 = 0;

            for (int k = 0; k < abc.length(); k++) {

                if (choicesABC[k] == 1) {
                    continue;
                }

                for (int j = 0; j < xyz.length(); j++) {

                    if (choicesXYZ[j] == 1) {
                        continue;
                    }

                    c1 = abc.charAt(k);
                    c2 = xyz.charAt(j);
                    p1 = np.get((int) c1);
                    p2 = np.get((int) c2);

                    curdist = calcDist(p1.x, p1.y, p2.x, p2.y);
                    if (curdist < mindist) {
                        mindist = curdist;
                        curindx2 = j;
                        curindx1 = k;
                    }

                }
            }

            choicesABC[curindx1] = 1;
            choicesXYZ[curindx2] = 1;
            val += mindist;

        }
        return val;
    }

    private String createCombo(String abc, int k, String c) {
        String str = "";
        for (int i = 0; i < abc.length(); i++) {
            if (i != k) {
                str += abc.charAt(i);
            } else {
                str += c;
            }
        }
        return str;
    }

    private boolean sameCombination(String wfa_combo, String abc) {
        for (int k = 0; k < wfa_combo.length(); k++) {
            if (!abc.contains(wfa_combo.split("")[k])) {
                return false;
            }

        }
        return true;
    }

    private void printMatrix(double[][] wfa_w) {
        for (int k = 0; k < wfa_w[0].length; k++) {

            for (int j = 0; j < wfa_w.length; j++) {
                System.out.print(wfa_w[k][j] + " ");

            }
            System.out.println("");
        }
    }

    private void printMatrix(int[][] wfa_w) {
        for (int k = 0; k < wfa_w[0].length; k++) {

            for (int j = 0; j < wfa_w.length; j++) {
                System.out.print(wfa_w[k][j] + " ");

            }
            System.out.println("");
        }
    }

    private String changeCombination(String c, String abc, String e) {
        StringBuilder news = new StringBuilder();
        for (int k = 0; k < abc.length(); k++) {
            if (abc.split("")[k].equals(c)) {
                news.append(e);
            } else {
                news.append(abc.split("")[k]);
            }
        }

        return sortString(news.toString());
    }

    private static String sortString(String sk) {
        String[] arr = sk.split("");
        Arrays.sort(arr);
        return String.join("", arr);
    }

    String min_max_Offline() {
        String solution = "";
        int negativeCycleC = 0;
        long startTime = System.nanoTime();
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
                edge_cost_map[i][ks.length + k] = calcDist(ks[i - 1].x, ks[i - 1].y, req[k - 1].x, req[k - 1].y);
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

        Point[] ps = new Point[1 + ks.length + req.length];

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

                edge_cost_map[k][j] = calcDist(req[s].x, req[s].y, ps[j].x, ps[j].y);
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
                } else {
                    if (Integer.parseInt(seqlist[j]) == ri) {

                        solution = solution + (char) ks[Integer.parseInt(cchar) - 1].name;
                        break;

                    }
                }
            }

        }

        for (int k = 0; k < seqlist.length - 1; k++) {
            int x = Integer.parseInt(seqlist[k]);
            int y = Integer.parseInt(seqlist[k + 1]);
            if (edge_cost_map[x][y] != -1000) {
                globaldistC += edge_cost_map[x][y];
            }

        }

        //System.out.println(globaldistC);
        resetK();
        //Returns a solution describing the indexes of the servers to serve the requests
        return solutionToIndexes(solution, this.ks);
    }
    
    private String solutionToIndexes(String solution,Point[] servers){
        String nsolution = "";
        for(String s : solution.split("")){
            for(int k = 0;k<servers.length;k++){
                if(servers[k].name == s.charAt(0)){
                    nsolution+=k;
                }
            }
        }
        return nsolution;
    }

   private int fordFulkerson(int[][] graph, int source, int sink) {
        int[] parentTracker = new int[graph.length];
        int u, v = 0;
        int[][] rg = graph;
        int maxflow = 0;

        while (breadthFirstSearch(rg, source, sink, parentTracker)) {
            int pathFlow = Integer.MAX_VALUE;
            v = sink;

            while (!(v == source)) {
                u = parentTracker[v];
                pathFlow = Math.min(pathFlow, rg[u][v]);
                v = parentTracker[v];
            }
            v = sink;

            while (!(v == source)) {
                u = parentTracker[v];
                rg[u][v] -= pathFlow;
                rg[v][u] += pathFlow;
                v = parentTracker[v];
            }

            maxflow += pathFlow;
        }

        return maxflow;
    }

    private boolean breadthFirstSearch(int[][] rg, int source, int sink, int[] parentTracker) {
        Queue<Integer> q = new LinkedList<Integer>();
        boolean[] visited = new boolean[rg.length];

        q.add(source);
        visited[source] = true;
        parentTracker[source] = -1;

        while (!(q.toArray().length == 0)) {
            int u = q.poll();

            for (int v = 0; v < rg.length; v++) {
                if (visited[v] == false && rg[u][v] > 0) {
                    q.add(v);
                    visited[v] = true;
                    parentTracker[v] = u;
                }
            }

        }

        return visited[sink];

    }

    private int[][] copyArray(int[][] original, int[][] test) {

        for (int k = 0; k < test.length; k++) {
            for (int j = 0; j < test[k].length; j++) {
                original[k][j] = test[k][j];
            }
        }
        return original;
    }

    private int[] prevEdge(int[][] pred, int[] cy) {
        return pred[cy[0]];
    }

    private boolean contains(int[][] cycle, int[] i) {
        boolean value = false;
        for (int[] c : cycle) {
            if (c == i) {
                return true;
            }
        }
        return false;
    }

    private boolean contains(double[] d, double d0) {
        for (double d1 : d) {
            if (d1 == d0) {
                return true;
            }
        }
        return false;
    }

    private int[][] getFlow(int[][] test, int[][] original) {

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

    private int[][] modifyResidual(int[][] original, int[][] currentFlow) {
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

    private double[][] getCostMatrix(double[][] edge_cost_map, int[][] residual) {
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

    private int[][] getEdges(int[][] cycle) {
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

    private String traversePath(int k, int[][] currentFlow) {
        String seq = "" + k;
        for (int j = 0; j < currentFlow[0].length; j++) {
            if (currentFlow[k][j] == 1) {
                seq = seq + "a" + traversePath(j, currentFlow);
            }
        }
        return seq;
    }

    void outputStats() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private int findSimpleMaxFlow(int[][] residual, int kl) {

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

}

class Point {

    int nx;
    int ny;
    int x;
    int y;
    char name;
    double prob;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
        this.name = 'x';
    }
}