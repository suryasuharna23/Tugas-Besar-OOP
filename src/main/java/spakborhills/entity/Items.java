//package spakborhills.entity;
//
//
//import spakborhills.enums.ItemType;
//
//public abstract class Items {
//    private String name;
//    private int buyPrice;
//    private int sellPrice;
//    private boolean isEdible;
//    private ItemType type;
//
//    // Common methods for all items
//    public Items(String name, boolean isEdible, ItemType type) {
//        this.name = name;
//        this.isEdible = false;
//        this.type = type;
//    }
//
//    public String getName() { return name; }
//    public int getBuyPrice() { return buyPrice; }
//    public int getSellPrice() { return sellPrice; }
//    public boolean isEdible() { return isEdible; }
//
//    public ItemType getType() {
//        return type;
//    }
//
//    public abstract void use(Player player);
//}
