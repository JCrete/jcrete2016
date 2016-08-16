package org.jcrete.bitterjava8.decorator;

/**
 *
 * @author ikost
 */
@org.jpatterns.gof.DecoratorPattern.ConcreteComponent
class Decaf extends Beverage {

    public Decaf() {
        description = "Decaf Coffee";
    }

    @Override
    public double cost() {
        return 1.5;
    }

}
