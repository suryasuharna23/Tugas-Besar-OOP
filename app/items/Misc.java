package items;

public class Misc extends Items {
    private int buyPrice;
    private int sellPrice;

    public Misc (String itemName, itemType type, boolean isEdible, int buyPrice, int sellPrice) {
        super(itemName, type.MISC, false);
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
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