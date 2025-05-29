package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.entity.Player;
import spakborhills.enums.ItemType;
import spakborhills.interfaces.Edible;

public class OBJ_Crop extends OBJ_Item implements Edible {
    private int harvestAmount;
    private int energy;

    public OBJ_Crop(GamePanel gp, ItemType itemType, String name, boolean isEdible, int buyPrice, int sellPrice,
            int harvestAmount, int energy) {
        super(gp, itemType, name, true, buyPrice, sellPrice);
        this.harvestAmount = harvestAmount;
        this.energy = 3;
        if (this.baseName.equals("Parsnip")) {
            this.buyPrice = 50;
            this.sellPrice = 35;
            this.harvestAmount = 1;
        } else if (this.baseName.equals("Cauliflower")) {
            this.buyPrice = 200;
            this.sellPrice = 150;
            this.harvestAmount = 1;
        } else if (this.baseName.equals("Potato")) {
            this.buyPrice = 0;
            this.sellPrice = 80;
            this.harvestAmount = 1;
        } else if (this.baseName.equals("Wheat")) {
            this.buyPrice = 50;
            this.sellPrice = 30;
            this.harvestAmount = 3;
        } else if (this.baseName.equals("Blueberry")) {
            this.buyPrice = 0;
            this.sellPrice = 100;
            this.harvestAmount = 3;
        } else if (this.baseName.equals("Tomato")) {
            this.buyPrice = 90;
            this.sellPrice = 60;
            this.harvestAmount = 1;
        } else if (this.baseName.equals("Hot Pepper")) {
            this.buyPrice = 0;
            this.sellPrice = 40;
            this.harvestAmount = 1;
        } else if (this.baseName.equals("Melon")) {
            this.buyPrice = 0;
            this.sellPrice = 250;
            this.harvestAmount = 1;
        } else if (this.baseName.equals("Cranberry")) {
            this.buyPrice = 0;
            this.sellPrice = 25;
            this.harvestAmount = 1;
        } else if (this.baseName.equals("Pumpkin")) {
            this.buyPrice = 300;
            this.sellPrice = 250;
            this.harvestAmount = 1;
        } else if (this.baseName.equals("Grape")) {
            this.buyPrice = 100;
            this.sellPrice = 10;
            this.harvestAmount = 20;
        }

        switch (baseName) {
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

    public void update() {
    }

    @Override
    public boolean use(Entity user) {

        if (isEdible() && user instanceof Player) {
            gp.ui.showMessage(this.name + " is now held. Press 'E' to eat.");
            return false;
        }
        return false;
    }

    public int getHarvestAmount() {
        return harvestAmount;
    }

    public int getEnergy() {
        return energy;
    }

    @Override
    public void eat(Player player) {
        System.out.println("DEBUG: OBJ_Crop.eat() called for " + this.baseName);
        if (player.gp.ui != null) {
            player.gp.ui.showMessage("You are eating " + this.baseName + ".");
        }

        if (this.getEnergy() != 0) {
            player.increaseEnergy(this.getEnergy());
            System.out.println("DEBUG: Player energy increased by " + this.getEnergy());
        }

        if (player.gp.gameClock != null && player.gp.gameClock.getTime() != null) {
            player.gp.gameClock.getTime().advanceTime(-5);
            System.out.println("DEBUG: Game time advanced by 5 minutes due to eating.");
        } else {
            System.out.println("DEBUG: GameClock or Time is null, cannot advance time.");
        }

        player.consumeItemFromInventory(this);
    }
}
