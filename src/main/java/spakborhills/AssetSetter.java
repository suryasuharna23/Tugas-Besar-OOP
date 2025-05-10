package spakborhills;

import spakborhills.object.OBJ_Boots;
import spakborhills.object.OBJ_Chest;
import spakborhills.object.OBJ_Door;
import spakborhills.object.OBJ_Key;

public class AssetSetter {
    GamePanel gp;

    public AssetSetter(GamePanel gp){
        this.gp = gp;
    }
    public void setObject(){
        gp.obj[0] = new OBJ_Boots(gp);
        gp.obj[0].worldX = 21 * gp.tileSize ;
        gp.obj[0].worldY = 28 * gp.tileSize;

        gp.obj[1] = new OBJ_Door(gp);
        gp.obj[1].worldX = 21 * gp.tileSize ;
        gp.obj[1].worldY = 30 * gp.tileSize;

        gp.obj[2] = new OBJ_Key(gp);
        gp.obj[2].worldX = 21 * gp.tileSize ;
        gp.obj[2].worldY = 29 * gp.tileSize;

        gp.obj[3] = new OBJ_Chest(gp);
        gp.obj[3].worldX = 21 * gp.tileSize ;
        gp.obj[3].worldY = 23 * gp.tileSize;
    }
}
