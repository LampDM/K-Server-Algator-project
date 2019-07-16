/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.math.*;
import java.util.Arrays;
import java.util.HashMap;


/**
 *
 * @author dani
 */
public class Ks implements Serializable {

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

    int x;
    int y;

    public Ks(int[][] servers, int[][] requests, int x, int y) throws FileNotFoundException {

        this.x = x;
        this.y = y;

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

    public String startWFA() {
        String solution = "";

        int xvrstica = binomial(this.x * this.y, ks.length).intValue();
        int yvrstica = req.length;
        wfa_w = new double[xvrstica][yvrstica + 1];
        wfa_s = new String[xvrstica][yvrstica];
        wfa_combos = new String[xvrstica];
        wfa_reqs = new String[yvrstica + 1];
        combc = 0;

        String starter = "";

        for (int i = 0; i < this.x; i++) {
            for (int j = 0; j < this.y; j++) {
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
        //System.out.println("combos: " + combos);
        String[] combo_sequence = combos.split(" ");

        double distvalue = 0;

        //If one or more of reqs at the start are the same as initial pos then server gives shorter answer
//        System.out.println("Combos:");
//        for (String str : combo_sequence) {
//            System.out.println(str);
//        }
        //Now convert to normal solution format
        boolean triggered = false;
        solution = "";
        String[] prev = combo_sequence[0].split("");
        String[] oldnames = combo_sequence[0].split("");
        for (int k = 1; k < combo_sequence.length; k++) {
            for (int j = 0; j < prev.length; j++) {
                if (!combo_sequence[k].contains(prev[j])) {
                    solution += oldnames[j];
                    triggered = true;
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
                } else if (j + 1 == prev.length) {

                }
            }
        }

        resetK();
        String nsol = solutionToIndexes(solution, this.ks);
        return nsol;

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

    private void resetK() {
        for (Point k : ks) {
            k.nx = k.x;
            k.ny = k.y;
        }
        globaldistC = 0;
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

    private String stringToIndex(String c, Point[] servers) {
        for (int i = 0; i < servers.length; i++) {
            if (servers[i].name == c.charAt(0)) {
                return Integer.toString(i);
            }
        }
        return "";
    }

    private String getComma(boolean b) {
        return b ? "," : "";
    }

    private String solutionToIndexes(String solution, Point[] servers) {

        //Make fake servers
        Point[] fservers = new Point[servers.length];
        for (int i = 0; i < servers.length; i++) {
            fservers[i] = new Point(0, 0);
            fservers[i].x = servers[i].x;
            fservers[i].y = servers[i].y;
            fservers[i].name = servers[i].name;
        }

        String nsolution = "";
        String[] sols = solution.split("");
        int scount = 0;
        boolean addcomma = true;
        //Made only for WFA output because it outputs only movements of servers, problem if requests
        // appear on top of servers
        //simuliraj tudi premikanje serverjev ker čene if ki presliši ne bo deloval
        if (solution.length() != req.length) {
            for (int i = 0; i < req.length; i++) {
                if (i + 1 == req.length) {
                    addcomma = false;
                }
                for (int j = 0; j < fservers.length; j++) {
                    if (req[i].x == fservers[j].x && req[i].y == fservers[j].y) {
                        nsolution += j + getComma(addcomma);
                        break;
                    }
                    if (j + 1 == fservers.length) {
                        int ind = Integer.parseInt(stringToIndex(sols[scount], fservers));
                        nsolution += stringToIndex(sols[scount], fservers) + getComma(addcomma);
                        //Move apropriate fserver correctly
                        fservers[ind].x = req[i].x;
                        fservers[ind].y = req[i].y;
                        scount++;
                    }
                }
            }
        } else {

            for (int i = 0; i < sols.length - 1; i++) {
                nsolution += stringToIndex(sols[i], servers) + ",";
            }
            nsolution += stringToIndex(sols[sols.length - 1], servers);

        }
        return nsolution;
    }

    void outputStats() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
