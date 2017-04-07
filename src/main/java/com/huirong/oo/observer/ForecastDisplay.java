package com.huirong.oo.observer;

/**
 * Created by huirong on 17-4-7.
 */
public class ForecastDisplay
        implements Observer, DisplayElement {
    private float currentPressure = 29.92f;
    private float lastPressure;
    private WeatherData weatherData;

    public ForecastDisplay(WeatherData weatherData) {
        this.weatherData = weatherData;
        weatherData.registerObserver(this);
    }

    @Override
    public void update(float temp, float humidity, float pressure) {
        lastPressure = currentPressure;
        currentPressure = pressure;
        display();
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
