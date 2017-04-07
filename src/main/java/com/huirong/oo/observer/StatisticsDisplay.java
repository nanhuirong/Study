package com.huirong.oo.observer;

/**
 * Created by huirong on 17-4-7.
 */
public class StatisticsDisplay
        implements Observer, DisplayElement {
    private float maxTem = 0.0f;
    private float minTem = 200;
    private float tempSum = 0.0f;
    private int numberReadings;
    private WeatherData weatherData;

    public StatisticsDisplay(WeatherData weatherData) {
        this.weatherData = weatherData;
        weatherData.registerObserver(this);
    }

    @Override
    public void update(float temp, float humidity, float pressure) {
        tempSum += temp;
        numberReadings++;
        if (temp > maxTem){
            maxTem = temp;
        }
        if (temp < minTem){
            minTem = temp;
        }
        display();
    }

    @Override
    public void display() {
        System.out.println("Avg/Max/Min temperature = " +
                (tempSum / numberReadings)
                + "/" + maxTem
                + "/" + minTem);
    }
}
