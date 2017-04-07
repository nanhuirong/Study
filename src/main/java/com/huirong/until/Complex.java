package com.huirong.until;

import java.util.Scanner;

/**
 * Created by huirong on 17-3-25.
 */
public final class Complex {
    private final double re;
    private final double im;

    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }
    public double realPart(){
        return re;
    }

    public double imaginaryPart(){
        return  im;
    }

    //注意返回的是新对象
    public Complex add(Complex c){
        return new Complex(re + c.re, im + c.im);
    }

    @Override
    public int hashCode() {
        int result = 17 + hashDouble(re);
        result += 31 * result + hashDouble(im);
        return result;
    }

    private int hashDouble(double val){
        long longBits = Double.doubleToLongBits(val);
        return (int)(longBits ^ (longBits >>> 32));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }
        if (!(obj instanceof Complex)){
            return false;
        }
        Complex dest = (Complex) obj;
        return Double.compare(this.re, dest.re) == 0 &&
                Double.compare(this.im, dest.im) == 0;
    }

    @Override
    public String toString() {
        return "(" + re + " + " + im + "i)";
    }

    public static void main(String[] args) {

    }
}
