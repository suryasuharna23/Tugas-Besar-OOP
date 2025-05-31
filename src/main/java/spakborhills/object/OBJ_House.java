package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.enums.EntityType;

public class OBJ_House extends Entity {
    public OBJ_House(GamePanel gp){
        super(gp);
        type = EntityType.INTERACTIVE_OBJECT;
        name = "House";
        down1 = setup("/objects/House");
        collision = true;
        int tilesWide = 6;
        int tilesHigh = 6;

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
        System.out.println("[OBJ_House] Player is interacting with the house...");

        
        teleportToPlayerHouse();
    }

    private void teleportToPlayerHouse() {
        System.out.println("[OBJ_House] Teleporting player to Player's House...");
        
        
        int playerHouseIndex = -1;
        for (int i = 0; i < gp.mapInfos.size(); i++) {
            if (gp.mapInfos.get(i).getMapName().equalsIgnoreCase("Player's House")) {
                playerHouseIndex = i;
                break;
            }
        }
        
        if (playerHouseIndex != -1) {
            
            gp.ui.showMessage("Masuk rumah...");
            
            
            gp.loadMapbyIndex(playerHouseIndex);
            
            
            int entranceX = gp.tileSize * 25; 
            int entranceY = gp.tileSize * 32; 
            gp.player.setPositionForMapEntry(entranceX, entranceY, "up");
            
            System.out.println("[OBJ_House] Player teleported to Player's House at (" + 
                            entranceX + "," + entranceY + ")");
        } else {
            System.err.println("[OBJ_House] ERROR: Player's House map not found!");
            gp.ui.showMessage("Tidak bisa masuk rumah sekarang!");
        }
    }
}
