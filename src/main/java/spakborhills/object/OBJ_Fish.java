package spakborhills.object;

import java.util.List;
import spakborhills.GamePanel;
import spakborhills.enums.EntityType;
import spakborhills.enums.ItemType;
import spakborhills.enums.Season;
import spakborhills.enums.Weather;
import spakborhills.enums.FishType;

public class OBJ_Fish extends OBJ_Item {
    private String fishName;
    private List<Season> seasons;
    private List<Weather> weathers;
    private List<String> locations;
    private FishType fishType;
    private int buyFishPrice;
    private int sellFishPrice;

    // Tambahan: Constraint jam ikan tersedia (0-23, range jam 24 jam)
    private int startHour;
    private int endHour;

    public OBJ_Fish(GamePanel gp, ItemType itemType, String name, boolean isEdible,
                    int buyPrice, int sellPrice,
                    List<Season> seasons, List<Weather> weathers, List<String> locations,
                    FishType fishType, int startHour, int endHour) { // Tambahkan startHour, endHour

        super(gp, itemType, name, isEdible, buyPrice, sellPrice, 1);
        this.type = EntityType.INTERACTIVE_OBJECT;

        this.fishName = name;
        this.seasons = seasons;
        this.weathers = weathers;
        this.locations = locations;
        this.fishType = fishType;
        this.buyFishPrice = buyPrice;
        this.sellFishPrice = sellPrice;
        this.startHour = startHour;
        this.endHour = endHour;

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

    // Method constraint: apakah ikan tersedia sesuai param saat ini
    public boolean isAvailable(Season season, Weather weather, int currentHour, String location) {
        return seasons.contains(season)
            && weathers.contains(weather)
            && locations.contains(location)
            && isTimeInRange(currentHour, startHour, endHour);
    }

    private boolean isTimeInRange(int hour, int start, int end) {
        if (start < end) {
            return hour >= start && hour < end;
        } else { // Range melintasi tengah malam, misal 20-02
            return hour >= start || hour < end;
        }
    }

    public String getFishName () { return fishName; }
    public List<Season> getSeasons () { return seasons; }
    public List<Weather> getWeathers () { return weathers; }
    public List<String> getLocations () { return locations; }
    public FishType getFishType () { return fishType; }
    public int getBuyFishPrice () { return buyFishPrice; }
    public int getSellFishPrice () { return sellFishPrice; }
    public int getStartHour() { return startHour; }
    public int getEndHour() { return endHour; }
    public int getTotalAvailableHour() {
        return endHour - startHour;
    }
}