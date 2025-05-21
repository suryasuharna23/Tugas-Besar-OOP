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
