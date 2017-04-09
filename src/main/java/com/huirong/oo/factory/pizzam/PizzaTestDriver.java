package com.huirong.oo.factory.pizzam;

/**
 * Created by huirong on 17-4-9.
 */
public class PizzaTestDriver {
    public static void main(String[] args) {
        PizzaStore nyStore = new NYPizzaStore();
        PizzaStore chicagoStore = new ChicagoPizzaStore();
        Pizza pizza = nyStore.orderPizza("cheese");
        System.out.println("------------------------");
        pizza = chicagoStore.orderPizza("cheese");
        System.out.println("------------------------");
        pizza = nyStore.orderPizza("clam");
        System.out.println("------------------------");
        pizza = chicagoStore.orderPizza("clam");
        System.out.println("------------------------");
        pizza = nyStore.orderPizza("pepperoni");
        System.out.println("------------------------");
        pizza = chicagoStore.orderPizza("pepperoni");
        System.out.println("------------------------");
        pizza = nyStore.orderPizza("veggie");
        System.out.println("------------------------");
        pizza = nyStore.orderPizza("veggie");
        System.out.println("------------------------");

    }
}
