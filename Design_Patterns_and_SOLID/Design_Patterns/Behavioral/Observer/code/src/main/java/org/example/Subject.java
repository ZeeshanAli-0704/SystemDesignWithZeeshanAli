package org.example;

public interface Subject<T> {

    public void registerObserver(Observer<T> ob);
    public void unRegisterObserver(Observer<T> ob);
    public void notifyAllObservers();
    public void setData(T data);

}
