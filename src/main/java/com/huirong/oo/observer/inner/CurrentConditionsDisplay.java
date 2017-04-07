package com.huirong.oo.observer.inner;

import com.huirong.oo.observer.DisplayElement;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by huirong on 17-4-7.
 */
public class CurrentConditionsDisplay
        implements Observer, DisplayElement {
    private float temperature;
    private float humidity;

    public CurrentConditionsDisplay(Observable observable) {
        observable.addObserver(this);
    }

    @Override
    public void display() {
        System.out.println("Current conditions: " + temperature
                + "F degrees and " + humidity + "% humidity");
    }

    @Override
    public void update(Observable obs, Object arg) {
        if (obs instanceof  WeatherData){
            WeatherData weatherData = (WeatherData) obs;
            this.temperature = weatherData.getTemperature();
            this.humidity = weatherData.getHumidity();
            display();
        }
    }
}
