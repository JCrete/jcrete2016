package org.jcrete.bitterjava8.decorator;

/**
 *
 * @author ikost
 */
@org.jpatterns.gof.DecoratorPattern.Component
abstract class Beverage {
    protected String description = "Unknown Beverage";

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public abstract double cost();
}
