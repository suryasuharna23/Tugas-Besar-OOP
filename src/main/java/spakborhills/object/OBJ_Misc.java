package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.entity.Player;
import spakborhills.enums.ItemType;

public class OBJ_Misc extends OBJ_Item {
    public OBJ_Misc (GamePanel gp, ItemType itemType, String name, boolean isEdible, int buyPrice, int sellPrice) {
        super(gp, ItemType.MISC, name, false, 0, 0, 1);

        switch (name) {
            case "Firewood":
                down1 = setup("/objects/firewood");
                break;
            case "Coal":
                down1 = setup("/objects/coal");
                break;
            case "Proposal Ring":
                down1 = setup("/objects/proposal_ring");
                break;
        }
    }

    @Override
    public boolean use(Entity entity) {
        if (isEdible() && entity instanceof Player) { 
            gp.ui.showMessage("You can use " + this.name + " now.");
            return false;
        }
        return false;
    }
}