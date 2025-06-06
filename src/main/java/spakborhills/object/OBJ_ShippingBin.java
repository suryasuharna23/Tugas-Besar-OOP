
package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.enums.EntityType;

public class OBJ_ShippingBin extends Entity {

    public OBJ_ShippingBin(GamePanel gp) {
        super(gp);
        this.gp = gp;
        type = EntityType.INTERACTIVE_OBJECT;
        name = "Shipping Bin";
        down1 = setup("/objects/shipping_bin");
        collision = true;
        int tilesWide = 3;
        int tilesHigh = 2;

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

        if (gp.player.hasUsedShippingBinToday) {
            gp.ui.showMessage("Kamu sudah menjual item hari ini. Balik lagi besok ya!");
            return;
        }

        gp.gameState = gp.sellState;
        gp.ui.commandNumber = 0;
        gp.ui.showMessage("Letakkan items untuk dijual lewat Shipping Bin (MAX 16 jenis). Tekan ESC untuk keluar.");

        if (gp.gameClock != null && !gp.gameClock.isPaused()) {
            gp.gameClock.pauseTime();
        }

    }

    @Override
    public void update() {
    }
}