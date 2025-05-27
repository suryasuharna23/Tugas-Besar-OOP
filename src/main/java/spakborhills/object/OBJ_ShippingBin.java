

package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.enums.EntityType;

public class OBJ_ShippingBin extends Entity {
    

    public OBJ_ShippingBin(GamePanel gp){
        super(gp);
        this.gp = gp; 
        type = EntityType.INTERACTIVE_OBJECT;
        name = "Shipping Bin";
        down1 = setup("/objects/shipping_bin");
        collision = true;
    }

    public void interact() {
        

        if (gp.player.hasUsedShippingBinToday) {
            gp.ui.showMessage("You've already used the shipping bin today.");
            
            
            return;
        }

        gp.gameState = gp.sellState; 
        gp.ui.commandNumber = 0;     
        gp.ui.showMessage("Place items in bin to sell overnight (Max 16). Press ESC to finish.");

        
        if (gp.gameClock != null && !gp.gameClock.isPaused()) {
            gp.gameClock.pauseTime();
        }
        
    }

    @Override
    public void update() {}
}