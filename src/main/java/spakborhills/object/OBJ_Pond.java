// spakborhills/object/OBJ_Pond.java
package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.enums.EntityType;

public class OBJ_Pond extends Entity {
    public OBJ_Pond(GamePanel gp) {
        super(gp);
        this.gp = gp;
        type = EntityType.INTERACTIVE_OBJECT;
        name = "POND";
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

    @Override
    public void interact() {
        if (!gp.player.isHoldingTool("Fishing Rod equipment")) {
            gp.ui.showMessage("Kamu butuh FISHING ROD");
            return;
        }

        if (gp.currentMapIndex == gp.FARM_MAP_INDEX) {
            gp.player.setLocation("FARM");
        } else {

        }

        gp.ui.showMessage("Kamu sudah di dekat pond...");
        gp.player.startFishing(true);
    }
}