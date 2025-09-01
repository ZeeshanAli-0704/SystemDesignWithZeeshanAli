package org.example;


public class Main {
    public static void main(String[] args) {

        WeatherStation weatherStation = new WeatherStation();
        TVDisplay tvDisplay = new TVDisplay();
        MobileDisplay mobileDisplay = new MobileDisplay();

        weatherStation.registerObserver(tvDisplay);
        weatherStation.registerObserver(mobileDisplay);

        weatherStation.setData(new WeatherCondition(30, 30, 30));

    }
}