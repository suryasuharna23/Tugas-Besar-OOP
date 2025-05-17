package spakborhills.entity;

import spakborhills.GamePanel;

import java.util.Random;


public class NPC_GIRL extends Entity{
    public NPC_GIRL(GamePanel gp){
        super(gp);
        direction = "down";
        speed = 1;
        setDialogue();
        getNPCImage();
    }

    public void getNPCImage(){
        up1 = setup("/npc/Abigail_W1");
        up2 = setup("/npc/Abigail_W2");
        down1 = setup("/npc/Abigail_S1");
        down2 = setup("/npc/Abigail_S2");
        left1 = setup("/npc/Abigail_A1");
        left2 = setup("/npc/Abigail_A2");
        right1 = setup("/npc/Abigail_D1");
        right2 = setup("/npc/Abigail_D2");
    }

    public void setDialogue(){
        dialogues.add("Kamu ganteng banget deh.");
        dialogues.add("I love you.");
        dialogues.add("Kangen kamu deh.");
        dialogues.add("Kamu mau gak \njadi pacar aku?");
        dialogues.add("Jadi kapan \nnih halalin aku?");
    }

    @Override
    public void setAction(){
        actionLockCounter++;

        if(actionLockCounter == 120){
            Random random = new Random();
            int i = random.nextInt(100) + 1;
            if (i <= 25){
                direction = "up";
            }
            if (i > 25 && i <= 50){
                direction = "down";
            }
            if (i > 50 && i <= 75){
                direction = "left";
            }
            if (i > 75 && i <= 100){
                direction = "right";
            }
            actionLockCounter = 0;
        }
    }
    public void speak(){
        super.speak();
    }
}