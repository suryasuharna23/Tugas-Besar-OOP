package spakborhills.entity;

import spakborhills.GamePanel;
import spakborhills.enums.EntityType;

public class NPC_EMILY extends NPC{
    public NPC_EMILY(GamePanel gp){
        super(gp);
        direction = "down";
        speed = 1;
        name = "Emily";
        type = EntityType.NPC;
        setDialogue();
        getNPCImage();
    }

    public void getNPCImage(){
        up1 = setup("/npc/Emily/Emily_W1");
        up2 = setup("/npc/Emily/Emily_W2");
        down1 = setup("/npc/Emily/Emily_S1");
        down2 = setup("/npc/Emily/Emily_S2");
        left1 = setup("/npc/Emily/Emily_A1");
        left2 = setup("/npc/Emily/Emily_A2");
        right1 = setup("/npc/Emily/Emily_D1");
        right2 = setup("/npc/Emily/Emily_D2");
    }

    public void setDialogue(){
        dialogues.add("Halo namaku Emily!");
    }
}