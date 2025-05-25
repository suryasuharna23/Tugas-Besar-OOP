// In spakborhills/object/OBJ_ShippingBin.java

package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.enums.EntityType;

public class OBJ_ShippingBin extends Entity {
    // GamePanel gp; // Already in Entity via constructor, gp available as this.gp

    public OBJ_ShippingBin(GamePanel gp){
        super(gp);
        this.gp = gp; // Ensure gp is assigned if super(gp) doesn't cover all uses.
        type = EntityType.INTERACTIVE_OBJECT;
        name = "Shipping Bin";
        down1 = setup("/objects/shipping_bin");
        collision = true;
    }

    public void interact() {
        // super.interact(); // Call if Entity.interact() has base logic

        if (gp.player.hasUsedShippingBinToday) {
            gp.ui.showMessage("You've already used the shipping bin today.");
            // To make this message last longer like a dialogue:
            // gp.ui.startSelfDialogue("You've already used the shipping bin today.");
            return;
        }

        gp.gameState = gp.sellState; // Change gameState to sellState
        gp.ui.commandNumber = 0;     // Reset commandNumber for navigation in UI penjualan
        gp.ui.showMessage("Place items in bin to sell overnight (Max 16). Press ESC to finish.");

        // Pause game clock if sellState requires it
        if (gp.gameClock != null && !gp.gameClock.isPaused()) {
            gp.gameClock.pauseTime();
        }
        // System.out.println("DEBUG: Interacted with Shipping Bin, gameState -> sellState");
    }

    @Override
    public void update() {}
}