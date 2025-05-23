package spakborhills.entity;

import spakborhills.GamePanel;
import spakborhills.enums.EntityType;

public class NPC_CAROLINE extends NPC{
    public NPC_CAROLINE(GamePanel gp){
        super(gp);
        direction = "down";
        speed = 1;
        name = "Caroline";
        type = EntityType.NPC;
        setDialogue();
        getNPCImage();
    }

    public void getNPCImage(){
        up1 = setup("/npc/Caroline/Caroline_W1");
        up2 = setup("/npc/Caroline/Caroline_W2");
        down1 = setup("/npc/Caroline/Caroline_S1");
        down2 = setup("/npc/Caroline/Caroline_S2");
        left1 = setup("/npc/Caroline/Caroline_A1");
        left2 = setup("/npc/Caroline/Caroline_A2");
        right1 = setup("/npc/Caroline/Caroline_D1");
        right2 = setup("/npc/Caroline/Caroline_D2");
    }

    public void setDialogue(){
        dialogues.add("Nama saya Caroline, salam kenal!");
    }
}

