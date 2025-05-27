package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.enums.EntityType;

public class OBJ_Bed extends Entity {
    public OBJ_Bed(GamePanel gp) { 
        super(gp);
        type = EntityType.INTERACTIVE_OBJECT;
        name = "Single Bed"; 
        collision = true; 
        int tilesWide = 2; 
        int tilesHigh = 4;
        String imagePath = "/objects/single_bed"; 
        this.imageWidth = tilesWide * gp.tileSize;
        this.imageHeight = tilesHigh * gp.tileSize;
        down1 = setup(imagePath);
        if (down1 == null) {
            System.err.println("Gagal memuat gambar untuk: " + imagePath);
            
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
        System.out.println("[OBJ_Bed] Interact method called for " + name);
        if (gp.currentMapIndex == gp.PLAYER_HOUSE_INDEX) { 
            if (gp.player != null && !gp.player.isCurrentlySleeping()) {
                gp.player.sleep("Waktunya tidur di " + name.toLowerCase() + "...");
            }
        } else {
            gp.ui.showMessage("Ranjang ini bukan milikmu!");
        }
    }
}