package com.huirong.oo.observer.inner;

import com.huirong.oo.observer.DisplayElement;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by huirong on 17-4-7.
 */
public class ForecastDisplay implements Observer, DisplayElement {
    private float currentPressure = 29.92f;
    private float lastPressure;

    public ForecastDisplay(Observable observable) {
        observable.addObserver(this);
    }

    @Override
    public void update(Observable obj, Object arg) {
        if (obj instanceof WeatherData){
            WeatherData weatherData = (WeatherData) obj;
            lastPressure = currentPressure;
            currentPressure = weatherData.getPressure();
            display();
        }
    }

    @Override
    public void display() {
        System.out.print("Forecast: ");
        if (currentPressure > lastPressure){
            System.out.println("Improving weather on the way");
        }else if (currentPressure == lastPressure){
            System.out.println("More of the same");
        }else{
            System.out.println("Watch out for coller, rainy weather");
        }
    }
}
