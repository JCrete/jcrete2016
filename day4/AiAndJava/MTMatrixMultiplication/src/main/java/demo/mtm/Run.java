package demo.mtm;

import demo.matrix.Matrix;
import demo.matrix.MatrixOp;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Zoran Sevarac <zoran.sevarac@coldbrewai.com>
 */
public class Run {
    
    
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // create matrices
        // call multiply on them
        int size = 3000;
        
        Matrix a = new Matrix(size, size);
        Matrix b = new Matrix(size, size);
        Matrix result = new Matrix(size, size);
        MatrixOp.randomize(a);
        MatrixOp.randomize(b);

        // 2 threads one iterate even, one odd rows/cols
        MatMultiThread evenTask = new MatMultiThread(a, b, result, 0, 2);
        MatMultiThread oddTask = new MatMultiThread(a, b, result, 1, 2);
               
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        threadPool.submit(evenTask);
        Matrix r = threadPool.submit(oddTask).get();
        
        System.out.println(r);
        
        threadPool.shutdown();
    }
    
}
