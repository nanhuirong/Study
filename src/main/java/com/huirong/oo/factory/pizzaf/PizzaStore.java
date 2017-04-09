package com.huirong.oo.factory.pizzaf;

/**
 * Created by huirong on 17-4-9.
 */
public abstract class PizzaStore {
    protected abstract Pizza createPizza(String item);
    public Pizza orderPizza(String type){
        Pizza pizza = createPizza(type);
        pizza.prepare();
        pizza.bake();
        pizza.cut();
        pizza.box();
        return pizza;
    }
}
