package demo.matrix;

/**
 * Wrapper class for matrix
 * @author zoran
 */
public class Matrix {
    public int rows, cols;
    public int width, height, depth;
    
    /**
     * Values stored in this matrix
     */
    private double values[];

        
    
    public Matrix(int rows, int cols) {        
        this.rows = rows;
        this.cols = cols;
        values = new double[rows*cols];
    }

    // podrzati 3d matrice pa cak i 4d, i 5d
    public Matrix(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        values = new double[width*height*depth];
    }

    
    public double get(int i) {
        return values[i];
    }
    
    public double set(int i, double val) {
        return values[i] = val;
    }    
    
    /**
     * -XX:InlineSmallCode=n	Inline a previously compiled method only if its generated native code size is less than this. The default value varies with the platform on which the JVM is running.
     * -XX:MaxInlineSize=35	Maximum bytecode size of a method to be inlined.
     * -XX:FreqInlineSize=n	Maximum bytecode size of a frequently executed method to be inlined. The default value varies with the platform on which the JVM is running.
     * keeping hot methods small (35 bytecodes or less) final migh help, it will get inlined if its a hotspot - frequent calls
     * 
     * @param i matrix row
     * @param j matrix col
     * @return matrix value at [i][j]
     */
     public double get(int i, int j) {
        int idx = i*cols +j;
        return values[idx];
    }
    
    
    public final void set(int i, int j, double val) {
        int idx = i*cols +j;
        values[idx] = val;
    }
    
    
    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public double[] getValues() {
        return values;
    }  
        
    @Override
    public String toString() {
          StringBuilder sb = new StringBuilder();
          
          for(int i = 0; i < rows; i++) {
              sb.append("[");
              for(int j=0; j<cols; j++) {
                  sb.append(values[i*cols + j]).append("  ");
              }
              sb.append("]").append(System.lineSeparator());
          }
          return sb.toString();
    }
    
    public static String toString(Matrix m) {
        return m.toString();
    }
            
}
