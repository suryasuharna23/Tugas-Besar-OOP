package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.entity.Player;
import spakborhills.enums.EntityType;
import spakborhills.enums.ItemType;
import spakborhills.interfaces.Edible;

public class OBJ_Food extends OBJ_Item implements Edible {
    private int energy;

    public OBJ_Food(GamePanel gp, ItemType itemType, String name, boolean isEdible, int buyPrice, int sellPrice,
            int energy) {
        super(gp, itemType, name, isEdible, buyPrice, sellPrice, 1);
        this.energy = energy;
        this.type = EntityType.INTERACTIVE_OBJECT;

        switch (name) {
            case "Fish n' Chips":
                down1 = setup("/objects/fish_chips");
                break;
            case "Baguette":
                down1 = setup("/objects/baguette");
                break;
            case "Fish Sandwich":
                down1 = setup("/objects/fish_sandwich");
                break;
            case "Fish Stew":
                down1 = setup("/objects/fish_stew");
                break;
            case "Fugu":
                down1 = setup("/objects/fugu");
                break;
            case "The Legends of Spakbor":
                down1 = setup("/objects/legends_spakbor");
                break;
            case "Pumpkin Pie":
                down1 = setup("/objects/pumpkin_pie");
                break;
            case "Sashimi":
                down1 = setup("/objects/sashimi");
                break;
            case "Spakbor Salad":
                down1 = setup("/objects/spakbor_salad");
                break;
            case "Veggie Soup":
                down1 = setup("/objects/veggie_soup");
                break;
            case "Wine":
                down1 = setup("/objects/wine");
                break;
            case "Cooked Pig's Head":
                down1 = setup("/objects/cooked_pig_head");
        }
        if (down1 != null) {
            image = down1;
        }
        up1 = down1;
        left1 = down1;
        right1 = down1;
    }

    public int getEnergy() {
        return energy;
    }

    public void update() {
    }

    @Override
    public boolean use(Entity entity) {
        if (isEdible() && entity instanceof Player) {
            gp.ui.showMessage(this.name + " is now held. Press 'E' to eat.");
            return false;
        }
        return false;
    }

    @Override
    public void eat(Player player) {
        System.out.println("DEBUG: OBJ_Food.eat() called for " + this.name);
        if (player.gp.ui != null) {
            player.gp.ui.showMessage("You are eating " + this.name + ".");
        }

        if (this.getEnergy() != 0) {
            player.increaseEnergy(this.getEnergy());
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
