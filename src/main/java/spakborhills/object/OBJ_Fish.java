package spakborhills.object;

import java.util.List;
import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.entity.Player;
import spakborhills.enums.EntityType;
import spakborhills.enums.ItemType;
import spakborhills.enums.Location;
import spakborhills.enums.Season;
import spakborhills.enums.Weather;
import spakborhills.enums.FishType;
import java.util.Arrays;

public class OBJ_Fish extends OBJ_Item {
    private String fishName;
    private List<Season> seasons;
    private List<Weather> weathers;
    private List<Location> locations;
    private FishType fishType;
    private int buyFishPrice;
    private int sellFishPrice;

    public OBJ_Fish(GamePanel gp, ItemType itemType, String name, boolean isEdible,
                    int buyPrice, int sellPrice,
                    List<Season> seasons, List<Weather> weathers, List<Location> locations,
                    FishType fishType) {

        super(gp, itemType, name, isEdible, buyPrice, sellPrice);
        this.type = EntityType.INTERACTIVE_OBJECT;

        this.fishName = name;
        this.seasons = seasons;
        this.weathers = weathers;
        this.locations = locations;
        this.fishType = fishType;
        this.buyFishPrice = buyPrice;
        this.sellFishPrice = sellPrice;

        // Setup sprite based on fish name
        switch(name) {
            // Common Fish
            case "Bullhead": down1 = setup("/objects/common/bullhead"); break;
            case "Carp": down1 = setup("/objects/common/carp"); break;
            case "Chub": down1 = setup("/objects/common/chub"); break;

            // Legendary Fish
            case "Angler": down1 = setup("/objects/legendary/angler"); break;
            case "Crimsonfish": down1 = setup("/objects/legendary/crimsonfish"); break;
            case "Glacierfish": down1 = setup("/objects/legendary/glacierfish"); break;
            case "Legend": down1 = setup("/objects/legendary/legend"); break;

            // Regular Fish
            case "Catfish": down1 = setup("/objects/regular/catfish"); break;
            case "Flounder": down1 = setup("/objects/regular/flounder"); break;
            case "Halibut": down1 = setup("/objects/regular/halibut"); break;
            case "Largemouth Bass": down1 = setup("/objects/regular/largemouth_bass"); break;
            case "Midnight Carp": down1 = setup("/objects/regular/midnight_carp"); break;
            case "Octopus": down1 = setup("/objects/regular/octopus"); break;
            case "Pufferfish": down1 = setup("/objects/regular/pufferfish"); break;
            case "Rainbow Trout": down1 = setup("/objects/regular/rainbow_trout"); break;
            case "Salmon": down1 = setup("/objects/regular/salmon"); break;
            case "Sardine": down1 = setup("/objects/regular/sardine"); break;
            case "Sturgeon": down1 = setup("/objects/regular/sturgeon"); break;
            case "Super Cucumber": down1 = setup("/objects/regular/super_cucumber"); break;
            default: down1 = setup("/objects/default/fish_default"); break;
        }
    }

}
