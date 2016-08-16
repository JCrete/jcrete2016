package org.jcrete.bitterjava8.decorator;

/**
 *
 * @author ikost
 */
@org.jpatterns.gof.DecoratorPattern.ConcreteComponent
class Capuccino extends Beverage {

    public Capuccino() {
        description = "Capuccino";
    }
    
    @Override
    public double cost() {
        return 2.2;
    }
    
}
