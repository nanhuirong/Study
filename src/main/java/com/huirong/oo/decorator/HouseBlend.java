package com.huirong.oo.decorator;

/**
 * Created by huirong on 17-4-8.
 * 综合咖啡
 */
public class HouseBlend extends Beverage {

    public HouseBlend() {
        description = "House Blend Coffee";
    }

    @Override
    public double cost() {
        return .89;
    }
}
