package src.Items.Seeds

public class Seeds extends Items{
    private Season season;
    private int dayToHarvest;
    private int buyPrice;
    private int sellPrice;
    private int countWater;

    public Seeds (String itemName, itemType type, boolean isEdible, Season season, int dayToHarvest, int buyPrice, int sellPrice, int countWater) {
        super(itemName, SEEDS, false);
        this.season = season;
        this.dayToHarvest = dayToHarvest;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.countWater = countWater;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public int getDayToHarvest() {
        return dayToHarvest;
    }

    public void setDayToHarvest(int dayToHarvest) {
        this.dayToHarvest = dayToHarvest;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(int buyPrice) {
        this.buyPrice = buyPrice;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }
}