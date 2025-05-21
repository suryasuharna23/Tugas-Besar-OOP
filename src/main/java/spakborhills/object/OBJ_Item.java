package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.enums.EntityType;
import spakborhills.enums.ItemType;

public class OBJ_Item extends Entity {
    private ItemType itemType;
    private int buyPrice;
    private int sellPrice;
    private boolean isEdible;

    public OBJ_Item(GamePanel gp, ItemType itemType, String name, boolean isEdible, int buyPrice, int sellPrice) {
        super(gp);
        this.itemType = itemType;
        this.name = name + " " + itemType.name().toString().toLowerCase();
        this.type = EntityType.INTERACTIVE_OBJECT;
        this.isEdible = isEdible;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public void update() {}

    public String getName() { return name; }
    public int getBuyPrice() { return buyPrice; }
    public int getSellPrice() { return sellPrice; }
    public boolean isEdible() { return isEdible; }
    public ItemType getType() {return itemType;}
}
