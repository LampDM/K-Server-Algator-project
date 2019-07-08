
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

/**
 * A class for common static methods (tools) of the project
 * @author tomaz
 */
public class BasicMatrixMulTools {

  public static boolean matrixEquals(int [][] A, int [][] B) {
    if (A == null || B == null) return false;
    if (A.length == 0) return false;
    
    if (A.length != B.length || A[0].length != B[0].length) return false;
    
    for (int i = 0; i < A.length; i++) {
      for (int j = 0; j < A[i].length; j++) {
        if (A[i][j] != B[i][j]) return false;
      }
    }
    return true;
  }  
  
  public static int[][] readMatrix(String path, String fileName) {
    try {
      Scanner sc = new Scanner(new File(path + File.separator + fileName));
      int nSq = sc.nextInt();
      int n = (int) Math.round(Math.sqrt(nSq));
      int[][] result = new int[n][n];
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
          result[i][j] = sc.nextInt();
        }
      }
      return result;
    } catch (Exception e) {
      return null;
    }
  }
  
  public static void writeMatrix(int[][] matrix, String fileName) {
    try {
      PrintWriter pw = new PrintWriter(fileName);
      pw.printf("%d\n", matrix.length*matrix.length);
      for (int i = 0; i < matrix.length; i++) {
	for (int j = 0; j < matrix.length; j++) {
	  pw.printf("%d ", matrix[i][j]);
	}
	pw.println();
      }
      pw.close();
    } catch (FileNotFoundException ex) {
      System.out.println(ex);
    }
  }  
  
  
  /**
   * Creates a random matrix of dimension n*n. 
   * The values in the matrix are within -maxValue and MaxValue.
   * @param n
   * @param id 
   */
  public static int[][] createRNDMatrix(int n, int maxValue) {
    Random rnd = new Random();
    int [][] matrix = new int[n][n];
    
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
	matrix[i][j] = rnd.nextInt(2*maxValue+2)-maxValue;
      }
    }    
    return matrix;
  }
  
  
  public static int [][] multiply(int [][]A, int [][]B) {
    int [][] C  = new int[A.length][A.length];
    
    for (int i = 0; i < A.length; i++) {
      for (int j = 0; j < A.length; j++) {
	for (int k = 0; k < A.length; k++) {
	  C[i][j] += A[i][k] * B[k][j];
	}
      }
    }
    
    return C;
  }
  
  

}
