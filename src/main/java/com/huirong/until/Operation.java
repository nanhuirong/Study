package com.huirong.until;

import java.util.List;

/**
 * Created by huirong on 17-3-27.
 */
public enum Operation {
    PLUS("+") {
        @Override
        double apply(double x, double y) {
            return x + y;
        }
    },
    MINUS("-") {
        @Override
        double apply(double x, double y) {
            return x - y;
        }
    },
    TIMES("*") {
        @Override
        double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE("/") {
        @Override
        double apply(double x, double y) {
            return x / y;
        }
    };
    private final String symbol;

    Operation(String symbol) {
        this.symbol = symbol;
    }
    abstract double apply(double x, double y);

    @Override
    public String toString() {
        return symbol;
    }

    public static void main(String[] args) {

    }
}
