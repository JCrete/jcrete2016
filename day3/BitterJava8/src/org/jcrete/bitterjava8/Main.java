package org.jcrete.bitterjava8;

import java.util.stream.IntStream;

/**
 *
 * @author ikost
 */
public class Main {

    private final static int SIZE = 100;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        int[] marks1 = new int[SIZE], marks2 = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            marks1[i] = (int) (10 * Math.random() + 1);
            marks2[i] = (int) (10 * Math.random() + 1);
        }        
        // java.lang.ArrayStoreException    
//        final int[] totals = Stream
//                .concat(Stream.of(marks1),
//                        Stream.of(marks2))
//                .toArray(int[]::new);
        final int[] totals = IntStream
            .concat(IntStream.of(marks1),        
                    IntStream.of(marks2))
            .toArray();
    }

}
