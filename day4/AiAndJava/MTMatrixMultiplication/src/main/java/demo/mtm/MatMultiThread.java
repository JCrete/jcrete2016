package demo.mtm;

import demo.matrix.Matrix;
import java.util.concurrent.Callable;

public class MatMultiThread implements Callable<Matrix>{
    Matrix a, b, result;
    int startIdx, stop, step;
        
    public MatMultiThread(Matrix a, Matrix b, Matrix result, int startIdx, int step) {
        this.a = a;
        this.b = b;
        this.result = result;
        this.startIdx = startIdx;
        this.step = step;
    }
           
    @Override
    public Matrix call() throws Exception {
           for (int rr = startIdx; rr < result.rows; rr+=step) {
            for (int rc = startIdx; rc < result.cols; rc+=step) {
                double sum = 0;
                for (int j = 0; j < b.rows; j+=1) {
                    sum += a.get(rr, j) * b.get(j, rc);                    
                }
                result.set(rr, rc, sum);
            }
        }
          return result;
    }
            
}