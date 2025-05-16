package spakborhills;

import spakborhills.entity.NPC_OLDMAN;
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

    }
    public void setNPC(){
        gp.npc.add(new NPC_OLDMAN(gp));
        gp.npc.get(0).worldX = gp.tileSize * 21;
        gp.npc.get(0).worldY = gp.tileSize * 21;
    }
}