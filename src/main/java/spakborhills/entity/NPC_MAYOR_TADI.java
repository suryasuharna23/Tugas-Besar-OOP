package spakborhills.entity;

import spakborhills.GamePanel;
import spakborhills.enums.EntityType;

import java.util.Collections;

public class NPC_MAYOR_TADI extends NPC{
    public NPC_MAYOR_TADI(GamePanel gp){
        super(gp);
        direction = "down";
        speed = 1;
        name = "Mayor Tadi";
        type = EntityType.NPC;
        Collections.addAll(lovedGiftsName, "Legend");
        Collections.addAll(likedGiftsName, "Angler", "Crimsonfish", "Glacierfish");
        Collections.addAll(hatedItems, "Seluruh item yang bukan merupakan lovedItems dan likedItems"); // Tunggu selesai implement items
        setDialogue();
        getNPCImage();
    }

    public void getNPCImage(){
        up1 = setup("/npc/MayorTadi/MayorTadi_W1");
        up2 = setup("/npc/MayorTadi/MayorTadi_W2");
        down1 = setup("/npc/MayorTadi/MayorTadi_S1");
        down2 = setup("/npc/MayorTadi/MayorTadi_S2");
        left1 = setup("/npc/MayorTadi/MayorTadi_A1");
        left2 = setup("/npc/MayorTadi/MayorTadi_A2");
        right1 = setup("/npc/MayorTadi/MayorTadi_D1");
        right2 = setup("/npc/MayorTadi/MayorTadi_D2");
    }

    public void setDialogue(){
        dialogues.add("Ya ndak tau kok tanya saya.");
    }
}