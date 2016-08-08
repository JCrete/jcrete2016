package demo.matrix;

/**
 * Basic matrix operations and utility functions
 * 
 * @author Zoran Sevarac
 */
public class MatrixOp {

    /**
     * Adds a and b matrices and stores result in result matrix
     * 
     * result = a + b
     * 
     * @param a
     * @param b
     * @param result 
     */
    public static void add(Matrix a, Matrix b, Matrix result) {
        for (int i = 0; i < a.rows; i++) {
            for (int j = 0; j < a.cols; j++) {
                result.set(i, j, a.get(i, j) + b.get(i,j));
            }
        }        
    }
    
    /** 
     * Fills  matrix with value
     * 
     * @param a
     * @param value 
     */
    public static void fill(Matrix a, double value) {
        for(int i = 0; i < a.rows; i++) {
            for(int j = 0; j < a.cols; j++) {
                a.set(i, j, value);
            }
            
        }
    }
        
            
    /**
     * Matrix multiplication
     * 
     * result = a * b
     * 
     * @param a
     * @param b
     * @param result 
     */
    public static void multiply(Matrix a, Matrix b, Matrix result) {
        for (int ri = 0; ri < result.rows; ri++) {
            for (int rc = 0; rc < result.cols; rc++) {
                double sum = 0;
                for (int j = 0; j < b.rows; j++) { // ili granica b.rows
                    sum += a.get(ri, j) * b.get(j, rc);                    
                }
                result.set(ri, rc, sum);
            }
        }

    } 
    
    /**
     * Multiply with scalar
     * 
     * @param a
     * @param b
     * @param result 
     */
    public static void multiply(Matrix a, double b, Matrix result) {
        for (int i = 0; i < a.rows; i++) {
            for (int j = 0; j < a.cols; j++) {
               result.set(i, j, a.get(i, j) * b);
            }
        }
    } 
    
    /**
     * Calculates direct/Hadamard product for two matrices (of same dimension)
     * Each element ij in resu;t matrix is the product of elements ij of the original two matrices
     * 
     * result[i][j] = a[i][j] * b[i][j]
     * 
     * @param a
     * @param b
     * @param result 
     */
    public static void multiplyDirect(Matrix a, Matrix b, Matrix result) {
        for (int i = 0; i < a.rows; i++) {
            for (int j = 0; j < a.cols; j++) {
               result.set(i, j, a.get(i, j) * b.get(i, j));
            }
        }
    }     

    
    public static void randomize(Matrix m) {
        for(int i=0; i < m.rows; i++) {
            for(int j= 0; j < m.cols ; j++) {
                m.set(i, j, Math.random()); 
            }
        }
    }
    
    
   
//    public Matrix transform(); // apply fome function or something
    

}
