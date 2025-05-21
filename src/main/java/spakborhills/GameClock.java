package spakborhills;

import spakborhills.enums.Season;

public class GameClock extends Thread {
    private final Time time;
    private final Weather weather;
    private Season currentSeason = Season.SPRING;
    private boolean running = true;

    public GameClock(Time time, Weather weather) {
        this.time = time;
        this.weather = weather;
    }

    private void updateSeason(int day) {
        int index = ((day - 1) / 10) % 4;
        currentSeason = Season.values()[index];
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(1000);
                time.advanceTime(5);

                if (time.isNewDay()) {
                    time.startNewDay();
                    updateSeason(time.getDay());

                    if ((time.getDay() - 1) % 10 == 0) {
                        weather.resetRainyCount();
                    }

                    weather.generateNewWeather();
                }

                System.out.printf("[Time] %s | Day %d | Season: %s | Weather: %s%n",
                        time.getFormattedTime(),
                        time.getDay(),
                        currentSeason.name(),
                        weather.getWeatherName());

            } catch (InterruptedException e) {
                System.err.println("GameClock interrupted");
                e.printStackTrace();
            }
        }
    }

    public Time getTime() {
        return time;
    }

    public void stopClock() {
        running = false;
    }

    public Season getCurrentSeason() {
        return currentSeason;
    }
    public String getFormattedTime() {
        return time.getFormattedTime();
    }

    public Weather getWeather() {
        return weather;
    }
}
