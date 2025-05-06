package src.Items;

public class Crop extends Items{
    private int buyPrice;
    private int sellPrice;
    private Time time;
    private int harvestAmount;
    private int energy;

    public Crop(String itemName, itemType type, boolean isEdible, int buyPrice, int sellPrice, Time time, int harvestAmount, int energy) {
        super(itemName, type.CROP, true);
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.time = time;
        this.harvestAmount = harvestAmount;
        this.energy = energy;
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

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public int getHarvestAmount() {
        return harvestAmount;
    }

    public void setHarvestAmount(int harvestAmount) {
        this.harvestAmount = harvestAmount;
    }

    public int getCropEnergy() {
        return energy;
    }

    public void setCropEnergy(int energy) {
        this.energy = energy;
    }
}
