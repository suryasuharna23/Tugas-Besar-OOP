package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.*;
import spakborhills.enums.EntityType;

public class OBJ_Potion extends Entity {
    public OBJ_Potion(GamePanel gp){
        super(gp);
        type = EntityType.INTERACTIVE_OBJECT;
        name = "Potion";
        down1 = setup("/objects/potion_red");
    }
    public void update() {}

    public boolean use(Entity user) {
        if (user instanceof Player player) {
            gp.ui.showMessage("Kamu meminum " + name + ".");
            player.increaseEnergy(25); // Contoh: Pulihkan 25 energi
            return true; // true berarti item ini akan dikonsumsi (dihapus dari inventaris)
        }
        return false;
    }
}