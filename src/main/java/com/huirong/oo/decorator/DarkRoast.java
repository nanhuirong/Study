package com.huirong.oo.decorator;

/**
 * Created by huirong on 17-4-8.
 */
public class DarkRoast extends Beverage {

    public DarkRoast() {
        description = "Dark Roast Coffee";
    }

    @Override
    public double cost() {
        return .99;
    }
}
