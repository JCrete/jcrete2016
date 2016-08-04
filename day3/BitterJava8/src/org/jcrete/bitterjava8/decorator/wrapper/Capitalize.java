package org.jcrete.bitterjava8.decorator.wrapper;

/**
 *
 * @author ikost
 */
public interface Capitalize extends BaseWrapper {
    
    static String before(String aText) {
        String result = aText;
        if (aText != null) {
            result = result.toUpperCase();
        }
        return result;
    }
    
    static String after(String aText) {
        return aText;
    }
}
