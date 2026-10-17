package org.example;

public class TVDisplay implements Observer<WeatherCondition>{
    @Override
    public void notifyUser(WeatherCondition weatherCondition) {
        System.out.println(weatherCondition.toString());
    }
}
