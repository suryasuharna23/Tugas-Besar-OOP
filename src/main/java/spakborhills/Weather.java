package spakborhills;

import java.util.Random;

public class Weather {
    public enum WeatherType { SUNNY, RAINY }

    private WeatherType currentWeather;
    private int rainyDaysThisSeason = 0;
    private final Random rand = new Random();

    public Weather() {
        generateNewWeather();
    }

    public void generateNewWeather() {
        if (rainyDaysThisSeason < 2 && rand.nextInt(4) == 0) {
            currentWeather = WeatherType.RAINY;
            rainyDaysThisSeason++;
        } else {
            currentWeather = rand.nextBoolean() ? WeatherType.SUNNY : WeatherType.RAINY;
            if (currentWeather == WeatherType.RAINY) {
                rainyDaysThisSeason++;
            }
        }
    }

    public void resetRainyCount() {
        rainyDaysThisSeason = 0;
    }

    public WeatherType getCurrentWeather() {
        return currentWeather;
    }

    public String getWeatherName() {
        return currentWeather.name();
    }
}

