package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.entity.Player;
import spakborhills.enums.EntityType;
import spakborhills.enums.ItemType;
import spakborhills.enums.Season;
import spakborhills.enums.Weather;

public class OBJ_Seed extends OBJ_Item {
    private int dayToHarvest;
    private int countWater;
    private Season season;
    private Weather weather;
 
    public OBJ_Seed(GamePanel gp, ItemType itemType, String name, boolean isEdible, int buyPrice, int sellPrice, int countWater, int dayToHarvest, Season season, Weather weather){
        super(gp, itemType, name, isEdible, buyPrice, sellPrice);
        this.type = EntityType.INTERACTIVE_OBJECT;
        this.season = season;
        this.weather = weather;
        this.dayToHarvest = dayToHarvest;
        this.countWater = countWater;

        if (this.name.equals("Parsnip")) {
            this.dayToHarvest = 1;
            this.buyPrice = 20;
            this.sellPrice = 10;
            this.season = Season.SPRING;
            if (this.weather == Weather.SUNNY) {
                this.countWater = 2;
            } else {
                this.countWater = 1;
            }
        } else if (this.name.equals("Cauliflower")) {
            this.dayToHarvest = 5;
            this.buyPrice = 80;
            this.sellPrice = 40;
            this.season = Season.SPRING;
            if (this.weather == Weather.SUNNY) {
                this.countWater = 2;
            } else {
                this.countWater = 1;
            }
        } else if (this.name.equals("Potato")) {
            this.dayToHarvest = 3;
            this.buyPrice = 50;
            this.sellPrice = 25;
            this.season = Season.SPRING;
            if (this.weather == Weather.SUNNY) {
                this.countWater = 2;
            } else {
                this.countWater = 1;
            }
        } else if (this.name.equals("Wheat")) {
            if (this.season == Season.SPRING || this.season == Season.FALL) {
                this.dayToHarvest = 1;
                this.buyPrice = 60;
                this.sellPrice = 30;

                if (this.weather == Weather.SUNNY) {
                    this.countWater = 2;
                } else {
                    this.countWater = 1;
                }
            }
        } else if (this.name.equals("Blueberry")) {
            this.dayToHarvest = 7;
            this.buyPrice = 80;
            this.sellPrice = 40;
            this.season = Season.SUMMER;
            if (this.weather == Weather.SUNNY) {
                this.countWater = 2;
            } else {
                this.countWater = 1;
            }
        } else if (this.name.equals("Tomato")) {
            this.dayToHarvest = 3;
            this.buyPrice = 50;
            this.sellPrice = 25;
            this.season = Season.SUMMER;
            if (this.weather == Weather.SUNNY) {
                this.countWater = 2;
            } else {
                this.countWater = 1;
            }
        } else if (this.name.equals("Hot Pepper")) {
            this.dayToHarvest = 1;
            this.buyPrice = 40;
            this.sellPrice = 20;
            this.season = Season.SUMMER;
            if (this.weather == Weather.SUNNY) {
                this.countWater = 2;
            } else {
                this.countWater = 1;
            }
        } else if (this.name.equals("Melon")) {
            this.dayToHarvest = 4;
            this.buyPrice = 80;
            this.sellPrice = 40;
            this.season = Season.SUMMER;
            if (this.weather == Weather.SUNNY) {
                this.countWater = 2;
            } else {
                this.countWater = 1;
            }
        } else if (this.name.equals("Cranberry")) {
            this.dayToHarvest = 2;
            this.buyPrice = 100;
            this.sellPrice = 50;
            this.season = Season.FALL;
            if (this.weather == Weather.SUNNY) {
                this.countWater = 2;
            } else {
                this.countWater = 1;
            }
        } else if (this.name.equals("Pumpkin")) {
            this.dayToHarvest = 7;
            this.buyPrice = 150;
            this.sellPrice = 75;
            this.season = Season.FALL;
            if (this.weather == Weather.SUNNY) {
                this.countWater = 2;
            } else {
                this.countWater = 1;
            }
        } else if (this.name.equals("Grape")) {
            this.dayToHarvest = 3;
            this.buyPrice = 60;
            this.sellPrice = 30;
            this.season = Season.FALL;
            if (this.weather == Weather.SUNNY) {
                this.countWater = 2;
            } else {
                this.countWater = 1;
            }
        }


        switch(name) {
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
        }
    }
    public void update() {}

    public boolean use(Entity user) {
        if (user instanceof Player player) {
            gp.ui.showMessage("Kamu menanam " + this.getName() + ".");
            player.plantSeed(this.getName() + " Seeds");
            return true; // true berarti item ini akan dikonsumsi (dihapus dari inventaris)
        }
        return false;
    }

}
