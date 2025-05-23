package spakborhills.entity;

import spakborhills.GamePanel;
import spakborhills.enums.EntityType;

import java.util.Collections;

public class NPC_PERRY extends NPC{
    public NPC_PERRY(GamePanel gp){
        super(gp);
        direction = "down";
        speed = 1;
        name = "Perry";
        type = EntityType.NPC;
        Collections.addAll(lovedGiftsName, "Cranberry", "Blueberry");
        Collections.addAll(likedGiftsName, "Wine");
        Collections.addAll(hatedItems, "Semua item fish"); // tunggu fish selesai implement
        setDialogue();
        getNPCImage();
    }

    public void getNPCImage(){
        up1 = setup("/npc/Perry/Perry_W1");
        up2 = setup("/npc/Perry/Perry_W2");
        down1 = setup("/npc/Perry/Perry_S1");
        down2 = setup("/npc/Perry/Perry_S2");
        left1 = setup("/npc/Perry/Perry_A1");
        left2 = setup("/npc/Perry/Perry_A2");
        right1 = setup("/npc/Perry/Perry_D1");
        right2 = setup("/npc/Perry/Perry_D2");
    }

    public void setDialogue(){
        dialogues.add("Saya adalah platypus terhebat.");
    }
}