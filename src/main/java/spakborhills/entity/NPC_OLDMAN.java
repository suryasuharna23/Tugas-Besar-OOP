package spakborhills.entity;

import spakborhills.GamePanel;

import java.util.Random;


public class NPC_OLDMAN extends Entity{
    public NPC_OLDMAN(GamePanel gp){
        super(gp);
        direction = "down";
        speed = 1;
        setDialogue();
        getNPCImage();
    }

    public void getNPCImage(){
        up1 = setup("/npc/oldman_up_1");
        up2 = setup("/npc/oldman_up_2");
        down1 = setup("/npc/oldman_down_1");
        down2 = setup("/npc/oldman_down_2");
        left1 = setup("/npc/oldman_left_1");
        left2 = setup("/npc/oldman_left_2");
        right1 = setup("/npc/oldman_right_1");
        right2 = setup("/npc/oldman_right_2");
    }

    public void setDialogue(){
        dialogues.add("HIDUP UNPADDDDDDDDDDDDD\nDDDDDDDDDDDDDDDDDDD!");
        dialogues.add("PERSIB NU AINGG!");
        dialogues.add("MALAM KEOS INIEEHHH");
        dialogues.add("Geda Gedi Geda Gedaooo");
        dialogues.add("Lumah enak, lah gua");
        dialogues.add("Selain donatur dilarang ngatur");
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