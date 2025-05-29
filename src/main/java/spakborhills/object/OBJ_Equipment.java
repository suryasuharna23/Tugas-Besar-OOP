package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.enums.ItemType;

public class OBJ_Equipment extends OBJ_Item {
    public OBJ_Equipment(GamePanel gp, ItemType itemType, String name, boolean isEdible, int buyPrice, int sellPrice) {
        super(gp, ItemType.EQUIPMENT, name, false, 0, 0);

        switch (name) {
            case "Hoe":
                down1 = setup("/objects/hoe");
                break;
            case "Pickaxe":
                down1 = setup("/objects/pickaxe");
                break;
            case "Watering Can":
                down1 = setup("/objects/watering_can");
                break;
            case "Fishing Rod":
                down1 = setup("/objects/fishing_rod");
                break;
        }
    }

    @Override
    public boolean use(Entity user) {
        if (gp != null && gp.ui != null) {
            gp.ui.showMessage("Equipped: " + this.name);
        } else {
            System.out.println("[UI Not Available] Equipped: " + this.name);
        }
        return false;
    }
}
