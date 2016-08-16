package org.jcrete.bitterjava8.decorator;

/**
 *
 * @author ikost
 */
@org.jpatterns.gof.DecoratorPattern.ConcreteComponent
class Espresso extends Beverage {

    public Espresso() {
        description = "Espresso";
    }
    
    @Override
    public double cost() {
        return 1.8;
    }
    
}
