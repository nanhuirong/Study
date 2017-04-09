package com.huirong.oo.factory.pizzam;

/**
 * Created by huirong on 17-4-9.
 */
public class NYPizzaStore extends PizzaStore {
    @Override
    Pizza createPizza(String type) {
        if (type.equals("cheese")){
            return new NYStyleCheesePizza();
        }else if (type.equals("veggie")){
            return new NYStyleVeggiePizza();
        }else if (type.equals("clam")){
            return new NYStyleClamPizza();
        }else if (type.equals("pepperoni")){
            return new NYStylePepperoniPizza();
        }else return null;
    }
}
