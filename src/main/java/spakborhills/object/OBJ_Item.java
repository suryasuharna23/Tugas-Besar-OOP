package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.enums.EntityType;
import spakborhills.enums.ItemType;

public class OBJ_Item extends Entity {
    private ItemType itemType; 
    int buyPrice;
    int sellPrice;
    private final boolean isEdible;
    public int quantity;
    public String baseName;

    public OBJ_Item(GamePanel gp, ItemType itemType, String name, boolean isEdible, int buyPrice, int sellPrice, int quantity) {
        super(gp);
        this.itemType = itemType;
        this.baseName = name;
        this.name = name + " " + itemType.name().toString().toLowerCase();
        this.type = EntityType.INTERACTIVE_OBJECT;
        this.isEdible = isEdible;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.quantity = quantity;
    }
    
    public OBJ_Item(GamePanel gp, ItemType itemType, String name, boolean isEdible, int buyPrice, int sellPrice) {
        this(gp, itemType, name, isEdible, buyPrice, sellPrice, 1); 
    }

    
    public boolean isStackable() {
        
        
        
        
        
        return true;
    }

    public void update() {}

    public String getName() { return name; }
    public int getBuyPrice() { return buyPrice; }
    public int getSellPrice() { return sellPrice; }
    public boolean isEdible() { return isEdible; }
    public ItemType getType() {return itemType;}
}