package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.enums.EntityType;

public class OBJ_Door extends Entity {
    public OBJ_Door(GamePanel gp) {
        super(gp);
        type = EntityType.INTERACTIVE_OBJECT;
        name = "Door";
        down1 = setup("/objects/door");
        collision = true;

        int tilesWide = 4;
        int tilesHigh = 1;

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
        System.out.println("[OBJ_Door] Player is exiting the house...");
        teleportToFarm();
    }

    private void teleportToFarm() {
        System.out.println("[OBJ_Door] Teleporting player back to Farm...");

        
        int farmIndex = -1;
        for (int i = 0; i < gp.mapInfos.size(); i++) {
            if (gp.mapInfos.get(i).getMapName().equalsIgnoreCase("Farm")) {
                farmIndex = i;
                break;
            }
        }

        if (farmIndex != -1) {
            gp.ui.showMessage("Keluar rumah...");

            
            gp.loadMapbyIndex(farmIndex);

            
            int houseX = gp.currentFarmLayout.houseX * gp.tileSize;
            int houseY = gp.currentFarmLayout.houseY * gp.tileSize;

            
            int exitX = houseX + (3 * gp.tileSize); 
            int exitY = houseY + (7 * gp.tileSize); 

            gp.player.setPositionForMapEntry(exitX, exitY, "down");

            System.out.println("[OBJ_Door] Player teleported to Farm at (" + exitX + "," + exitY + ")");
        } else {
            System.err.println("[OBJ_Door] ERROR: Farm map not found!");
            gp.ui.showMessage("Tidak bisa ke luar rumah sekarang.");
        }
    }
}