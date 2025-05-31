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
        down1 = setup("/objects/pond"); // Pastikan path gambar benar
        collision = true;
        int tilesWide = 4; // Sesuaikan jika perlu
        int tilesHigh = 3; // Sesuaikan jika perlu

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
        if (!gp.player.isHoldingTool("Fishing Rod equipment")) { // Pastikan nama tool benar
            gp.ui.showMessage("Kamu butuh FISHING ROD");
            return;
        }

        // Tetapkan lokasi logis ke "Farm" jika pemain berinteraksi dengan kolam di peta Farm
        if (gp.currentMapIndex == gp.FARM_MAP_INDEX) {
            gp.player.setLocation("FARM");
        } else {

        }

        gp.ui.showMessage("Kamu sudah di dekat pond...");
        gp.player.startFishing(true); // MODIFIKASI: kirim true
    }
}