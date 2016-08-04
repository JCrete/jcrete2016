package org.jcrete.bitterjava8.decorator.wrapper;

/**
 *
 * @author ikost
 */
public interface RemovePeriods extends BaseWrapper {

    static String before(String aText) {
        return aText;
    }    
    
    static String after(String aText) {
        String result = aText;
        if (aText != null) {
            result = result.replace(".", "");
        }
        return result;
    }
}
