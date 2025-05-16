package spakborhills;

import spakborhills.entity.*;
import spakborhills.object.*;

public class AssetSetter {
    GamePanel gp;

    public AssetSetter(GamePanel gp){
        this.gp = gp;
    }
    public void setObject(){

    }
    public void setNPC(){
        gp.npc.add(new NPC_GIRL(gp));
        gp.npc.getFirst().worldX = gp.tileSize * 21;
        gp.npc.getFirst().worldY = gp.tileSize * 21;

        gp.npc.add(new NPC_OLDMAN(gp));
        gp.npc.get(1).worldX = gp.tileSize * 20;
        gp.npc.get(1).worldY = gp.tileSize * 21;
    }
}