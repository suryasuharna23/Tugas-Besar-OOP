package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.enums.EntityType;
import spakborhills.enums.ItemType;

public class OBJ_Item extends Entity {
    private ItemType itemType; 
    int buyPrice;
    int sellPrice;
    private boolean isEdible;
    public int quantity;

    public OBJ_Item(GamePanel gp, ItemType itemType, String name, boolean isEdible, int buyPrice, int sellPrice, int quantity) {
        super(gp);
        this.itemType = itemType;
        this.name = name + " " + itemType.name().toString().toLowerCase();
        this.type = EntityType.INTERACTIVE_OBJECT;
        this.isEdible = isEdible;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.quantity = quantity;
    }
    // OVERLOAD
    public OBJ_Item(GamePanel gp, ItemType itemType, String name, boolean isEdible, int buyPrice, int sellPrice) {
        this(gp, itemType, name, isEdible, buyPrice, sellPrice, 1); // Kuantitas awal default 1
    }

    // Item selalu dianggap bisa ditumpuk jika tidak ada batasan maxStack
    public boolean isStackable() {
        // Jika sebuah item secara inheren tidak bisa ditumpuk (misalnya alat),
        // Anda bisa membuat konstruktornya selalu mengatur quantity=1
        // dan logika addItemToInventory tidak akan pernah menumpuknya jika namanya unik.
        // Atau, Anda bisa menambahkan field boolean `canBeStacked` jika diperlukan.
        // Untuk saat ini, asumsikan semua OBJ_Item bisa ditumpuk secara tak terbatas.
        return true;
    }

    public void update() {}

    public String getName() { return name; }
    public int getBuyPrice() { return buyPrice; }
    public int getSellPrice() { return sellPrice; }
    public boolean isEdible() { return isEdible; }
    public ItemType getType() {return itemType;}
}