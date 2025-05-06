package src.Items.Fish;

public class Fish extends Items {
    private enum fishType {
        COMMON, REGULAR, LEGENDARY
    }
    private fishType fishType;
    private Season season;
    private Time time;
    private enum fishLocation {
        FOREST RIVER, MOUNTAIN LAKE, OCEAN, POND
    }
    private fishLocation loc;
    private Weather weather;
    private int typeValue; //5, 10, 15

    public Fish(String itemName, itemType type, boolean isEdible, Season season, Time time, fishType fishType, fishLocation loc, Weather weather, int typeValue) {
        super(itemName, FISH, true);
        this.season = season;
        this.time = time;
        this.loc = loc;
        this.fishType = fishType;
        this.weather = weather;
        this.typeValue = typeValue;
    }

    public fishType getFishType() {
        return fishType;
    }

    public void setFishType(fishType fishType) {
        this.fishType = fishType;
    }

    public fishLocation getLoc() {
        return loc;
    }

    public void setLoc(fishLocation loc) {
        this.loc = loc;
    }

    public int getTypeValue {
        return typeValue;
    }
}