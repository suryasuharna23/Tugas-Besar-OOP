package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.enums.EntityType;
import spakborhills.enums.ItemType;
import spakborhills.enums.Season;
import spakborhills.enums.Weather;

public class OBJ_Seed extends OBJ_Item {
    public int dayToHarvest;
    public int countWater;
    private Season season;
    public Weather weather;

    public OBJ_Seed(GamePanel gp, ItemType itemType, String name, boolean isEdible, int buyPrice, int sellPrice,
            int countWater, int dayToHarvest, Season season, Weather weather) {
        super(gp, itemType, name, isEdible, buyPrice, sellPrice, 1);
        this.type = EntityType.INTERACTIVE_OBJECT;
        this.season = season;
        this.weather = weather;
        this.dayToHarvest = dayToHarvest;
        this.countWater = countWater;

        switch (name) {
            case "Parsnip":
                down1 = setup("/objects/parsnip_seeds");
                break;
            case "Pumpkin":
                down1 = setup("/objects/pumpkin_seeds");
                break;
            case "Cranberry":
                down1 = setup("/objects/cranberry_seeds");
                break;
            case "Melon":
                down1 = setup("/objects/melon_seeds");
                break;
            case "Hot Pepper":
                down1 = setup("/objects/hot_pepper_seeds");
                break;
            case "Tomato":
                down1 = setup("/objects/tomato_seeds");
                break;
            case "Blueberry":
                down1 = setup("/objects/blueberry_seeds");
                break;
            case "Wheat":
                down1 = setup("/objects/wheat_seeds");
                break;
            case "Potato":
                down1 = setup("/objects/potato_seeds");
                break;
            case "Cauliflower":
                down1 = setup("/objects/cauliflower_seeds");
                break;
            case "Grape":
                down1 = setup("/objects/grape_seeds");
                break;
        }

        
        if (down1 != null) {
            up1 = down1;
            left1 = down1;
            right1 = down1;
            image = down1;
        }
    }

    public Season getSeason() {
        return season;
    }

    public int getCountWater() {
        return countWater;
    }

    public int getDayToHarvest(){
        return dayToHarvest;
    }

    public Weather getWeather() {
        return weather;
    }
    
}
