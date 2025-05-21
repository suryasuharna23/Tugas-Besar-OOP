package spakborhills.entity;

import spakborhills.GamePanel;
import spakborhills.enums.EntityType;


public class NPC_ABIGAIL extends Entity{
    public NPC_ABIGAIL(GamePanel gp){
        super(gp);
        direction = "down";
        speed = 1;
        name = "Abigail";
        type = EntityType.NPC;
        setDialogue();
        getNPCImage();
    }
    public void getNPCImage(){
        up1 = setup("/npc/Abigail/Abigail_W1");
        up2 = setup("/npc/Abigail/Abigail_W2");
        down1 = setup("/npc/Abigail/Abigail_S1");
        down2 = setup("/npc/Abigail/Abigail_S2");
        left1 = setup("/npc/Abigail/Abigail_A1");
        left2 = setup("/npc/Abigail/Abigail_A2");
        right1 = setup("/npc/Abigail/Abigail_D1");
        right2 = setup("/npc/Abigail/Abigail_D2");
    }
    public void setDialogue(){
        dialogues.add("Haii aku Abigail! Salam kenal yaa!");
    }
    public void speak(){super.speak();}
}