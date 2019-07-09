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

    public KServerTools() {

    }

    public static void printSomething(int[][] something){
        System.out.println("");
        for (int[] el : something){
            System.out.print("("+el[0]+","+el[1]+") ");
        }
        System.out.println("");
    }
    
    public static double calcDist(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y2 - y1), 2));
    }

    public static double distTravelled(String movements, int[][] servers, int[][] requests) {
        double dist = 0.0;
        //TODO turn this into some function or find more elegant solution
        int [][] ks = new int[servers.length][servers[0].length];
        int [][] req = new int[requests.length][requests[0].length];
        
        //Copying arrays
        for(int i =0;i<servers.length;i++){
            ks[i][0] = servers[i][0];
            ks[i][1] = servers[i][1];
        }
        
        for(int i =0;i<requests.length;i++){
            req[i][0] = requests[i][0];
            req[i][1] = requests[i][1];
        }

        String[] movs = movements.split(",");
        
        for (int i = 0; i < req.length; i++) {
            //server servers[movs[i]] to requests[i], change position of servers[movs[i]]
            int mov = Integer.parseInt(movs[i]);
            dist += (calcDist(ks[mov][0],ks[mov][1],req[i][0],req[i][1]));
            ks[mov][0]=req[i][0];
            ks[mov][1]=req[i][1];
        }
        return dist;
    }

}
