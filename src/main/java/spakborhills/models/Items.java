package spakborhills.models;

import spakborhills.enums.itemType;
public class Items {
    private String itemName;



    private itemType type;
    private boolean isEdible;

    public Items(String itemName, itemType type, boolean isEdible) {
        this.itemName = itemName;
        this.type = type;
        this.isEdible = isEdible;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String name) {
        this.itemName = name;
    }

    public itemType getItemType() {
        return type;
    }

    public void setItemType(itemType type) {
        this.type = type;
    }

    public boolean getIsEdible() {
        return isEdible;
    }
}
