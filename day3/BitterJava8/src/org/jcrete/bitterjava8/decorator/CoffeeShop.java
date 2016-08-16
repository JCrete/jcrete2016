package org.jcrete.bitterjava8.decorator;

import java.util.Objects;
import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class CoffeeShop {

    public static void main(String args[]) {
        //@org.jpatterns.gof.DecoratorPattern.ConcreteDecorator
        DoubleUnaryOperator milk = v -> 0.10 + v;
        DoubleUnaryOperator whip = v -> 0.15 + v;
        DoubleUnaryOperator mocha = v -> 0.20 + v;

        //@org.jpatterns.gof.DecoratorPattern.ConcreteDecorator
        UnaryOperator<String> milkDescription = s -> s + ", Milk";
        UnaryOperator<String> whipDescription = s -> s + ", Whip";
        UnaryOperator<String> mochaDescription = s -> s + ", Mocha";

//        StringUnaryOperator milkDescription = s -> s + ", Milk";
//        StringUnaryOperator whipDescription = s -> s + ", Whip";
//        StringUnaryOperator mochaDescription = s -> s + ", Mocha";

        Beverage beverage = new Espresso();
        System.out.println(beverage.getDescription()
                + " €" + beverage.cost());

        Beverage beverage2 = new Decaf();
        System.out.println(
                milkDescription.compose(mochaDescription.andThen(mochaDescription)).apply(beverage2.getDescription())
                + " €" + milk.compose(mocha.andThen(mocha)).applyAsDouble(beverage2.cost()));

        Beverage beverage3 = new Espresso();
        System.out.println(
                whipDescription.compose(mochaDescription).apply(beverage3.getDescription())
                + " €" + whip.compose(mocha).applyAsDouble(beverage3.cost()));

        Beverage beverage4 = new Capuccino();
        System.out.println(
                whipDescription.compose(mochaDescription).andThen(mochaDescription).apply(beverage4.getDescription())
                + " €" + whip.compose(mocha).andThen(mocha).applyAsDouble(beverage4.cost()));

        System.out.println(
                decorateCondimentDescriptions(whipDescription, mochaDescription, mochaDescription).apply(beverage4.getDescription()) 
                + " €" + decorateCondiments(whip, mocha, mocha).applyAsDouble(beverage4.cost()));
    }

    private static DoubleUnaryOperator decorateCondiments(final DoubleUnaryOperator... decorators) {
        return Stream.of(decorators).reduce(DoubleUnaryOperator.identity(), DoubleUnaryOperator::andThen);
    }

    private static StringUnaryOperator decorateCondimentDescriptions(final UnaryOperator<String>... decorators) {
        return Stream.of(decorators)
                .reduce(UnaryOperator.identity(),
                        UnaryOperator::andThen);
    }
//    private static StringUnaryOperator decorateCondimentDescriptions(final StringUnaryOperator... decorators) {
//        return Stream.of(decorators)
//                .reduce(StringUnaryOperator.identity(),
//                        StringUnaryOperator::andThen);
//    }

    interface StringUnaryOperator extends UnaryOperator<String> {

        static StringUnaryOperator identity() {
            return s -> s;
        }

        default StringUnaryOperator andThen(StringUnaryOperator after) {
            Objects.requireNonNull(after);
            return s -> after.apply(this.apply(s));
        }

        default StringUnaryOperator compose(StringUnaryOperator before) {
            Objects.requireNonNull(before);
            return s -> this.apply(before.apply(s));
        }
    }
}
