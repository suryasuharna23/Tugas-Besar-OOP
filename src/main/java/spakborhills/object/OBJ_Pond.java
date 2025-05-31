package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.enums.EntityType;

public class OBJ_Pond extends Entity {
    public OBJ_Pond(GamePanel gp) {
        super(gp);
        this.gp = gp;
        type = EntityType.INTERACTIVE_OBJECT;
        name = "Pond";
        down1 = setup("/objects/pond");
        collision = true;
        int tilesWide = 4;
        int tilesHigh = 3;

        this.imageWidth = tilesWide * gp.tileSize;
        this.imageHeight = tilesHigh * gp.tileSize;
        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.width = this.imageWidth;
        solidArea.height = this.imageHeight;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    public void interact() {
        System.out.println("[OBJ_Pond] Player is interacting with the pond...");
        
        // Cek apakah player memiliki Fishing Rod
        if (!gp.player.isHoldingTool("Fishing Rod equipment")) {
            gp.ui.showMessage("Kamu butuh FISHING rOD");
            System.out.println("[OBJ_Pond] Player doesn't have Fishing Rod");
            return;
        }
        
        // Set player location ke "Pond" untuk fishing
        gp.player.setLocation("Pond");
        System.out.println("[OBJ_Pond] Player location set to 'Pond' for fishing");
        
        // Start fishing process
        gp.ui.showMessage("Kamu sudah di dekat pond...");
        gp.player.startFishing();
        
        System.out.println("[OBJ_Pond] Fishing started at pond");
    }
}
