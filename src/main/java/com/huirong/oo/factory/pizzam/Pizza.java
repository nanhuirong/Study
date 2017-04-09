package com.huirong.oo.factory.pizzam;

import java.util.ArrayList;

/**
 * Created by huirong on 17-4-9.
 */
public abstract class Pizza {
    String name;
    String dough;
    String sauce;
    ArrayList toppings = new ArrayList();

    void prepare(){
        System.out.println("preparing " + name);
        System.out.println("Tossing doughs...");
        System.out.println("adding sauce...");
        System.out.println("adding toppings: ");
        for (int i = 0; i < toppings.size(); i++){
            System.out.println(" " + toppings.get(i));
        }
    }

    void bake(){
        System.out.println("bake for 20 minutes at 350");
    }

    void cut(){
        System.out.println("cutting the pizza into diagonal slices");
    }

    void box(){
        System.out.println("place pizza into in official pizzaStore box");
    }

    public String getName() {
        return name;
    }
}
