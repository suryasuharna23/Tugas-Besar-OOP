package spakborhills.models;

import spakborhills.enums.itemType;
public class Furniture extends Items {
    private String furnitureID;
    private int countOrang;
    private Tile size;

    public Furniture(String itemName, itemType type, boolean isEdible, String furnitureID, int countOrang, Tile size) {
        super(itemName, type.FURNITURE, false);
        this.furnitureID = furnitureID;
        this.countOrang = countOrang;
        this.size = size;
    }

    public String getFurnitureID() {
        return furnitureID;
    }

    public void setFurnitureID(String furnitureID) {
        this.furnitureID = furnitureID;
    }

    public int getCountOrang() {
        return countOrang;
    }

    public void setCountOrang(int countOrang) {
        this.countOrang = countOrang;
    }

    public Tile getSize() {
        return size;
    }

    public void setSize (Tile size) {
        this.size = size;
    }
}