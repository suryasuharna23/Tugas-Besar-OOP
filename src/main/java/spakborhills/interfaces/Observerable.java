package spakborhills.interfaces;

public interface Observerable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(Object event);
}


