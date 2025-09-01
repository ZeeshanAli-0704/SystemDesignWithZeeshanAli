package org.example;

import java.util.ArrayList;
import java.util.List;

public class WeatherStation implements Subject<WeatherCondition> {
    private List<Observer<WeatherCondition>> observers;
    WeatherCondition currentCondition;


    public WeatherStation() {
        observers = new ArrayList<>();
    }

    @Override
    public void registerObserver(Observer<WeatherCondition>  o) {
        observers.add(o);
    }

    @Override
    public void unRegisterObserver(Observer<WeatherCondition>  o) {
        observers.remove(o);
    }

    @Override
    public void notifyAllObservers() {
        for (Observer<WeatherCondition> o : observers) {
            o.notifyUser(this.currentCondition);
        }
    }

    public void setData(WeatherCondition data) {
        this.currentCondition = data;
        notifyAllObservers();
    }
}
