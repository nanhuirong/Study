package com.huirong.oo.observer;

/**
 * Created by huirong on 17-4-7.
 */
public interface Subject {
    public void registerObserver(Observer observer);
    public void removeObserver(Observer observer);
    public void notifyObserver();
}
