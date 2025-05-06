package src.Items.Food;

public class Food extends Items {
    private int energy;
    private int buyPrice;
    private int sellPrice;

    public Food (String itemName, itemType type, boolean isEdible, int energy, int buyPrice, int sellPrice) {
        super(itemName, FISH, true);
        this.energy = energy;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
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
}