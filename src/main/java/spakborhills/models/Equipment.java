package spakborhills.models;

import spakborhills.enums.itemType;
public class Equipment extends Items {
    public Equipment(String itemName, itemType type, boolean isEdible) {
        super(itemName, itemType.EQUIPMENT, false);
    }
}