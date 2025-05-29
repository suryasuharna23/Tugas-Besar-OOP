
package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.enums.EntityType;

public class OBJ_Stove extends Entity {
    public OBJ_Stove(GamePanel gp) {
        super(gp);
        type = EntityType.INTERACTIVE_OBJECT;
        name = "Stove";
        collision = true;

        int tilesWide = 1;
        int tilesHigh = 1;
        String imagePath = "/objects/stove";

        this.imageWidth = tilesWide * gp.tileSize;
        this.imageHeight = tilesHigh * gp.tileSize;

        down1 = setup(imagePath);
        if (down1 == null) {
            System.err.println("Gagal memuat gambar untuk: " + imagePath + ".png");
        }

        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.width = this.imageWidth;
        solidArea.height = this.imageHeight;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    @Override
    public void interact() {
        System.out.println("[OBJ_Stove] Interact method called.");
        if (gp.currentMapIndex == gp.PLAYER_HOUSE_INDEX) {

            if (!gp.player.activeCookingProcesses.isEmpty()) {
                String cookingInfo = gp.player.getActiveCookingInfo();
                if (cookingInfo != null){
                    gp.ui.showMessage(cookingInfo + "\n Tunggu sampai selesai untuk memasak lagi.");
                }
                else{
                    gp.ui.showMessage("Kamu masih ada proses memasak yang berlangsung! Tunggu sampai selesai.");
                }
                return;
            }

            if (gp.gameState == gp.playState) {
                gp.selectedRecipeForCooking = null;
                gp.gameState = gp.cookingState;
                gp.ui.cookingCommandNum = 0;
                gp.ui.cookingSubState = 0;

                if (gp.gameClock != null && !gp.gameClock.isPaused()) {
                    gp.gameClock.pauseTime();
                }
                gp.ui.showMessage("Saatnya memasak! Apa yang akan kamu buat?");
            }
        } else {

            gp.ui.showMessage("Kamu hanya bisa memasak menggunakan kompor di rumahmu.");
        }
    }
}