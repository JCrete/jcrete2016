/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo.matrix;

/**
 *
 * @author Zoran Sevarac <zoran.sevarac@coldbrewai.com>
 */
public class Run {
    public static void main(String[] args) {
        
        int size = 3000;
        
        Matrix a = new Matrix(size, size);
        Matrix b = new Matrix(size, size);
        Matrix c = new Matrix(size, size);
        MatrixOp.randomize(a);
        MatrixOp.randomize(b);
        
        
        MatrixOp.multiply(a, b, c);
        
        System.out.println(b);
    }
}
