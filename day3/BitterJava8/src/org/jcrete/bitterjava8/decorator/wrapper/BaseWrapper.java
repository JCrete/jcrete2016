package org.jcrete.bitterjava8.decorator.wrapper;

import java.util.function.UnaryOperator;

/**
 *
 * @author ikost
 */
public interface BaseWrapper extends UnaryOperator<String> {

    /**
     * @param aText
     * @return aText
     */
    static String before(String aText) {
        return aText;
    }

    /**
     * @param aText
     * @return aText
     */
    static String after(String aText) {
        return aText;
    }
}
