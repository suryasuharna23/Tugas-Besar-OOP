
package spakborhills;

import java.util.ArrayList;
import java.util.List;

import spakborhills.enums.Season;
import spakborhills.interfaces.Observer;
import spakborhills.interfaces.Observerable;

public class GameClock extends Thread implements Runnable, Observerable {
    private final Time time;
    private final Weather weather;
    private Season currentSeason = Season.SPRING;
    private volatile boolean running = true;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();
    private List<Observer> observers = new ArrayList<>();

    public GameClock(Time time, Weather weather) {
        this.time = time;
        this.weather = weather;

        if (this.time != null) {
            updateSeasonBasedOnDay(this.time.getDay());
        }
    }

    public void updateSeasonBasedOnDay(int currentDay) {
        int index = ((currentDay - 1) / 10) % 4;
        if (index < Season.values().length) {
            currentSeason = Season.values()[index];
        } else {

            currentSeason = Season.SPRING;
            System.err.println("[GameClock] Warning: Season index out of bounds. Defaulting to SPRING.");
        }

    }

    @Override
    public void run() {
        while (running) {
            try {
                synchronized (pauseLock) {
                    if (paused) {
                        try {
                            pauseLock.wait();
                        } catch (InterruptedException e) {
                            if (!running)
                                break;
                            Thread.currentThread().interrupt();
                        }
                    }
                }

                if (!running)
                    break;

                Thread.sleep(1000);

                time.advanceTime(5);

                notifyObservers("waktu_berjalan");

                if (time.isNewDay()) {
                    time.startNewDay();
                    updateSeasonBasedOnDay(time.getDay());
                    notifyObservers("ganti_hari");

                    if ((time.getDay() - 1) % 10 == 0) {
                        weather.resetRainyCount();
                    }
                    weather.generateNewWeather();
                    notifyObservers(weather.getCurrentWeather());
                }
            } catch (InterruptedException e) {
                if (running) {

                }
            } catch (Exception e) {
                System.err.println("Error in GameClock run loop: " + e.getMessage());
                e.printStackTrace();
                running = false;
            }
        }
    }

    public Time getTime() {
        return time;
    }

    public void stopClock() {
        running = false;

        resumeTime();
        this.interrupt();
    }


    public void pauseTime() {
        if (!paused) {
            paused = true;

        }
    }
    public void resumeTime() {
        if (paused) {
            synchronized (pauseLock) {
                paused = false;
                pauseLock.notifyAll();
            }

        }
    }

    public boolean isPaused() {
        return paused;
    }

    public Season getCurrentSeason() {
        return currentSeason;
    }

    public String getFormattedTime() {
        if (time != null) {
            return time.getFormattedTime();
        }
        return "00:00";
    }

    public Weather getWeather() {
        return weather;
    }

    public void resetTime() {
        boolean wasPaused = this.paused;
        if (wasPaused) {
            resumeTime();
        }
        if (this.time != null) {
            this.time.resetToDefault();
            updateSeasonBasedOnDay(this.time.getDay());
        } else {
            currentSeason = Season.SPRING;
        }
        if (this.weather != null) {
            this.weather.resetToDefault();
        }
    }

    public spakborhills.enums.Weather getCurrentWeather() {
        if (weather != null) {
            return spakborhills.enums.Weather.valueOf(weather.getCurrentWeather().name());
        }

        return spakborhills.enums.Weather.SUNNY;
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