package com.huirong.until;

/**
 * Created by huirong on 17-3-24.
 * Nu
 */
public class NutritionFacts {
    private final int serviceSize;
    private final int services;
    private final int calories;
    private final int fat;
    private final int sodium;
    private final int carbohydrate;

    private NutritionFacts(Builder builder){
        this.serviceSize = builder.serviceSize;
        this.services = builder.services;
        this.calories = builder.calories;
        this.fat = builder.fat;
        this.sodium = builder.sodium;
        this.carbohydrate = builder.carbohydrate;
    }

    public static class Builder{
        //required
        private final int serviceSize;
        private final int services;
        //optional
        private int calories = 0;
        private int fat = 0;
        private int sodium;
        private int carbohydrate;

        public Builder(int serviceSize, int services) {
            this.serviceSize = serviceSize;
            this.services = services;
        }

        public Builder calories(int calories){
            this.calories = calories;
            return this;
        }

        public Builder fat(int fat){
            this.fat = fat;
            return this;
        }

        public Builder sodium(int sodium){
            this.sodium = sodium;
            return this;
        }

        public Builder carbohydrate(int carbohydrate){
            this.carbohydrate = carbohydrate;
            return this;
        }

        public NutritionFacts build(){
            return new NutritionFacts(this);
        }
    }

    public static void main(String[] args) {
        NutritionFacts cocaCloa = new Builder(240, 8)
                .calories(100)
                .sodium(35)
                .carbohydrate(27)
                .build();
    }
}
