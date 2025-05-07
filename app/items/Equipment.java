package items;

public class Equipment extends Items {
    public Equipment(String itemName, itemType type, boolean isEdible) {
        super(itemName, type.EQUIPMENT, false);
    }
}