package spakborhills.entity;

import spakborhills.GamePanel;
import spakborhills.enums.EntityType;

import java.util.Collections;

public class NPC_DASCO extends NPC{
    public NPC_DASCO(GamePanel gp){
        super(gp);
        direction = "down";
        speed = 1;
        name = "Dasco";
        type = EntityType.NPC;
        Collections.addAll(lovedGiftsName, "The Legends of Spakbor", "Cooked Pig's Head", "Wine", "Fugu", "Spakbor Salad");
        Collections.addAll(likedGiftsName, "Fish Sandwich", "Fish Stew", "Baguette", "Fish n’ Chips");
        Collections.addAll(hatedItems, "Legend", "Grape", "Cauliflower", "Wheat", "Pufferfish", "Salmon");
        setDialogue();
        getNPCImage();
    }

    public void getNPCImage(){

        up1 = setup("/npc/Dasco/Dasco_W1");
        up2 = setup("/npc/Dasco/Dasco_W2");
        down1 = setup("/npc/Dasco/Dasco_S1");
        down2 = setup("/npc/Dasco/Dasco_S2");
        left1 = setup("/npc/Dasco/Dasco_A1");
        left2 = setup("/npc/Dasco/Dasco_A2");
        right1 = setup("/npc/Dasco/Dasco_D1");
        right2 = setup("/npc/Dasco/Dasco_D2");
    }

    public void setDialogue(){
        dialogues.add("HOHOOO");
    }
}