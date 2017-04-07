package com.huirong.until;

/**
 * Created by huirong on 17-3-25.
 */
public class ComplexAD {
    private final double re;
    private final double im;

    private ComplexAD(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public static Complex valueOf(double re, double im){
        return new Complex(re, im);
    }
}
