package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.enums.ItemType;

public class OBJ_Crop extends OBJ_Item {
    private int harvestAmount;
    private int energy;

    public OBJ_Crop(GamePanel gp, ItemType itemType, String name, boolean isEdible, int buyPrice, int sellPrice, int harvestAmount, int energy) {
        super(gp, itemType, name, isEdible, buyPrice, sellPrice);
        this.harvestAmount = harvestAmount;
        this.energy = 3;

        if (this.name.equals("Parsnip")) {
            this.buyPrice = 50;
            this.sellPrice = 35;
            this.harvestAmount = 1;
        }
        else if (this.name.equals("Cauliflower")) {
            this.buyPrice = 200;
            this.sellPrice = 150;
            this.harvestAmount = 1;
        }
        else if (this.name.equals("Potato")) {
            this.buyPrice = 0;
            this.sellPrice = 80;
            this.harvestAmount = 1;
        } else if (this.name.equals("Wheat")) {
            this.buyPrice = 50;
            this.sellPrice = 30;
            this.harvestAmount = 3;
        } else if (this.name.equals("Blueberry")) {
            this.buyPrice = 0;
            this.sellPrice = 100;
            this.harvestAmount = 3;
        } else if (this.name.equals("Tomato")) {
            this.buyPrice = 90;
            this.sellPrice = 60;
            this.harvestAmount = 1;
        } else if (this.name.equals("Hot Pepper")) {
            this.buyPrice = 0;
            this.sellPrice = 40;
            this.harvestAmount = 1;
        } else if (this.name.equals("Melon")) {
            this.buyPrice = 0;
            this.sellPrice = 250;
            this.harvestAmount = 1;
        } else if (this.name.equals("Cranberry")) {
            this.buyPrice = 0;
            this.sellPrice = 25;
            this.harvestAmount = 1;
        } else if (this.name.equals("Pumpkin")) {
            this.buyPrice = 300;
            this.sellPrice = 250;
            this.harvestAmount = 1;
        } else if (this.name.equals("Grape")) {
            this.buyPrice = 100;
            this.sellPrice = 10;
            this.harvestAmount = 20;
        }

        switch (name) {
            case "Parsnip":
                down1 = setup("/objects/parsnip");
                break;
            case "Pumpkin":
                down1 = setup("/objects/pumpkin");
                break;
            case "Cranberry":
                down1 = setup("/objects/cranberry");
                break;
            case "Melon":
                down1 = setup("/objects/melon");
                break;
            case "Hot Pepper":
                down1 = setup("/objects/hot_pepper");
                break;
            case "Cauliflower":
                down1 = setup("/objects/cauliflower");
                break;
            case "Tomato":
                down1 = setup("/objects/tomato");
                break;
            case "Potato":
                down1 = setup("/objects/potato");
                break;
            case "Blueberry":
                down1 = setup("/objects/blueberry");
                break;
            case "Wheat":
                down1 = setup("/objects/wheat");
                break;
            case "Grape":
                down1 = setup("/objects/grape");
                break;
        }
    }

    public void update() {}

    public boolean use(Entity user) {
    //isi pake harvest
        return false;
    }

    public int getHarvestAmount() { return harvestAmount; }
    public int getEnergy() { return energy; }
}
