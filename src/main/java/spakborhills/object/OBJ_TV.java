package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.Weather;
import spakborhills.entity.Entity;
import spakborhills.enums.EntityType;

public class OBJ_TV extends Entity {
    public OBJ_TV(GamePanel gp) {
        super(gp);

        type = EntityType.INTERACTIVE_OBJECT;
        name = "TV";
        collision = true;

        int tilesWide = 1;
        int tilesHigh = 1;
        String imagePath = "/objects/tv";

        this.imageWidth = tilesWide * gp.tileSize;
        this.imageHeight = tilesHigh * gp.tileSize;

        down1 = setup(imagePath);
        if (down1 == null) {
            System.err.println("Gagal memuat gambar untuk: " + imagePath + ".png");
        }
        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.width = this.imageWidth;
        solidArea.height = this.imageHeight;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    @Override
    public void interact() {
        System.out.println("[OBJ_TV] Player is watching TV...");

        
        String weatherReport = getWeatherReport();

        
        gp.ui.currentDialogue = weatherReport;
        gp.gameState = gp.dialogueState;

        
        if (gp.gameClock != null && !gp.gameClock.isPaused()) {
            gp.gameClock.pauseTime();
        }

        gp.gameClock.getTime().advanceTime(15);
        gp.player.tryDecreaseEnergy(5);
    }

    private String getWeatherReport() {

        gp.currentInteractingNPC = null;
        String currentWeather = "Unknown";
        String weatherIcon = "";
        String weatherDescription = "";

        
        Weather weather = null;
        if (gp.gameClock != null) {
            weather = gp.gameClock.getWeather();
        } else if (gp.weather != null) {
            weather = gp.weather;
        }

        if (weather != null) {
            String weatherName = weather.getWeatherName();
            switch (weatherName) {
                case "SUNNY":
                    currentWeather = "Sunny";
                    weatherIcon = "[SUN]";
                    weatherDescription = "It's a beautiful sunny day! Perfect for farming and outdoor activities.";
                    break;
                case "RAINY":
                    currentWeather = "Rainy";
                    weatherIcon = "[RAIN]";
                    weatherDescription = "It's raining today. Your crops will be watered automatically!";
                    break;
                default:
                    weatherDescription = "The weather is unpredictable today.";
                    break;
            }
        }

        
        String timeInfo = "";
        if (gp.gameClock != null && gp.gameClock.getTime() != null) {
            int day = gp.gameClock.getTime().getDay();
            String season = gp.gameClock.getCurrentSeason().name();
            timeInfo = "Day " + day + " of " + season;
        }

        
        StringBuilder report = new StringBuilder();
        report.append("WEATHER FORECAST\n\n");

        if (!timeInfo.isEmpty()) {
            report.append("Date: ").append(timeInfo).append("\n\n");
        }

        report.append("Current Weather: ").append(weatherIcon).append(" ").append(currentWeather).append("\n\n");
        report.append(weatherDescription).append("\n\n");

        
        report.append(getWeatherTips(weather));

        return report.toString();
    }

    private String getWeatherTips(Weather weather) {
        if (weather == null) {
            return "Have a great day!";
        }
        String weatherName = weather.getWeatherName();
        switch (weatherName) {
            case "SUNNY":
                return "TIP: Great day for fishing, farming, and socializing with neighbors!";
            case "RAINY":
                return "TIP: No need to water your crops today. Perfect time for indoor activities!";
            default:
                return "TIP: Check the weather regularly to plan your activities!";
        }
    }
}