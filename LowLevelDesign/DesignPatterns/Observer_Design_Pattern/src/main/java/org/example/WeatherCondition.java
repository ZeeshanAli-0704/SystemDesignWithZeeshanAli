package org.example;

public class WeatherCondition {
    public int temperature;
    public int humidity;
    public int pressure ;

    public WeatherCondition(int humidity, int temperature, int pressure) {
        this.humidity = humidity;
        this.temperature = temperature;
        this.pressure = pressure;
    }

    @Override
    public String toString() {
        return "WeatherCondition{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                ", pressure=" + pressure +
                '}';
    }
}
