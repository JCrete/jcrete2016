package org.jcrete.bitterjava8.decorator.wrapper;

import static java.lang.System.out;
import java.util.function.UnaryOperator;

/**
 *
 * @author ikost
 */
public final class WrapperDemo {

    public static void main(String... aArgs) {

        final UnaryOperator<String> echo = s -> s;

        out.println(echo.apply("blah."));               // 'blah.'
        out.println(getBaseWrapper(echo).apply("blah."));   // 'blah.'

        out.println(getCapitalizeWrapper(echo).apply("blah."));   // 'BLAH.'
        out.println(getRemovePeriodsWrapper(echo).apply("blah."));   // 'blah'
        out.println(getRemovePeriodsWrapper(getCapitalizeWrapper(echo)).apply("blah."));   // 'BLAH'
    }

    private static BaseWrapper getBaseWrapper(UnaryOperator<String> showText) {
        return s -> {
            String text = BaseWrapper.before(s);
            text = showText.apply(text); //call-forward
            return BaseWrapper.after(text);
        };
    }

    private static Capitalize getCapitalizeWrapper(UnaryOperator<String> showText) {
        return s -> {
            String text = Capitalize.before(s);
            text = showText.apply(text); //call-forward
            return Capitalize.after(text);
        };
    }

    private static RemovePeriods getRemovePeriodsWrapper(UnaryOperator<String> showText) {
        return s -> {
            String text = RemovePeriods.before(s);
            text = showText.apply(text); //call-forward
            return RemovePeriods.after(text);
        };
    }

}
