package spakborhills;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import spakborhills.interfaces.Observer;
import spakborhills.interfaces.Observerable;

public class Weather implements Observerable {
    public String getWeatherName() {
        return currentWeather.name();
    }

    public enum WeatherType {
        SUNNY, RAINY
    }

    private WeatherType currentWeather;
    private int rainyDaysThisSeason = 0;
    private final Random rand = new Random();
    private static final WeatherType DEFAULT_START_WEATHER = WeatherType.SUNNY;
    private List<Observer> observers = new ArrayList<>();

    public Weather() {
        generateNewWeather();
    }

    public void generateNewWeather() {
        WeatherType oldWeather = currentWeather;
        if (rainyDaysThisSeason < 2 && rand.nextInt(4) == 0) {
            currentWeather = WeatherType.RAINY;
            rainyDaysThisSeason++;
        } else {
            currentWeather = rand.nextBoolean() ? WeatherType.SUNNY : WeatherType.RAINY;
            if (currentWeather == WeatherType.RAINY) {
                rainyDaysThisSeason++;
            }
        }

        if (oldWeather != currentWeather) {
            notifyObservers(currentWeather);
        }
    }

    public void resetRainyCount() {
        rainyDaysThisSeason = 0;
    }

    public void resetToDefault() {
        this.currentWeather = DEFAULT_START_WEATHER;
        this.rainyDaysThisSeason = 0;
        System.out.println("Weather reset to " + currentWeather);
    }

    public WeatherType getCurrentWeather() {
        return currentWeather;
    }

    public String getWeather() {
        return currentWeather.name();
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Object event) {
        for (Observer observer : observers) {
            observer.update(event);
        }
    }
}