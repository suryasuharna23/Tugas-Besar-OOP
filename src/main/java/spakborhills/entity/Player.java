package spakborhills.entity;

import spakborhills.GamePanel;
import spakborhills.KeyHandler;
import java.awt.*;
import java.awt.image.BufferedImage;


public class Player extends  Entity{
    KeyHandler keyH;
    public final int screenX;
    public final int screenY;
    public int maxEnergy;
    public int currentEnergy;

    public Player(GamePanel gp, KeyHandler keyH){
        super(gp);
        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        solidArea.width = 32;
        solidArea.height = 32;

        setDefaultValues();
        getPlayerImage();
        setDefaultEnergy();
    }

    public void getPlayerImage(){
        up1 = setup("/player/boy_up_1");
        up2 = setup("/player/boy_up_2");
        down1 = setup("/player/boy_down_1");
        down2 = setup("/player/boy_down_2");
        left1 = setup("/player/boy_left_1");
        left2 = setup("/player/boy_left_2");
        right1 = setup("/player/boy_right_1");
        right2 = setup("/player/boy_right_2");
    }

    public void setDefaultValues(){
        worldX = gp.tileSize * 21;
        worldY = gp.tileSize * 26;
        speed = 4;
        direction = "down";
    }

    public void setDefaultEnergy(){
        maxEnergy = 100;
        currentEnergy = maxEnergy;
    }

    public void decreaseEnergy(int amount){
        currentEnergy -= amount;
        if (currentEnergy < 0){
            currentEnergy = 0;
        }
    }

    public void increaseEnergy(int amount){
        currentEnergy += amount;
        if (currentEnergy > maxEnergy){
            currentEnergy = maxEnergy;
        }
    }

    private void updateDirection(){
        if (keyH.upPressed){
            direction = "up";
        }
        else if (keyH.downPressed){
            direction = "down";

        }
        else if (keyH.leftPressed){
            direction = "left";
        }
        else if (keyH.rightPressed){
            direction = "right";
        }
    }
    public void update(){
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed){
            updateDirection();
            checkCollisionAndMove();
            updateSprite();
        }
    }
    private void checkCollisionAndMove(){
        //CHECK TILE COLLISION
        collisionON = false;
        gp.collisionChecker.checkTile(this);

        // Check object collision
        int objIndex = gp.collisionChecker.checkObject(this,true);
        int npcIndex = gp.collisionChecker.checkEntity(this, gp.npc);
        interactNPC(npcIndex);
        pickUpObject(objIndex);

        //IF COLLISION FALSE, PLAYER CAN MOVE
        if (!collisionON){
            switch (direction){
                case "up": worldY -= speed; break;
                case "down": worldY+= speed; break;
                case "left": worldX -= speed; break;
                case "right": worldX += speed; break;
            }
        }
    }

    public void interactNPC(int i){
        if (i != 999){
            if(gp.keyH.enterPressed){
                gp.gameState = gp.dialogueState;
                gp.npc.get(i).speak();
            }
        }
        gp.keyH.enterPressed = false;
    }

    public void pickUpObject(int i){
        if (i != 999){

        }
    }

    private void updateSprite(){
        spriteCounter++;
        if(spriteCounter > 12){
            if(spriteNum == 1){
                spriteNum = 2;
            }
            else if(spriteNum == 2){
                spriteNum = 1;
            }
            spriteCounter = 0;
        }
    }
    public void draw(Graphics2D g2){
        BufferedImage image = null;
        switch (direction){
            case "up":
                if(spriteNum == 1){
                    image = up1;
                }
                if(spriteNum == 2){
                    image = up2;
                }
                break;
            case "down":
                if(spriteNum == 1){
                    image = down1;
                }
                if(spriteNum == 2){
                    image = down2;
                }
                break;
            case"left":
                if(spriteNum == 1){
                    image = left1;
                }
                if(spriteNum == 2){
                    image = left2;
                }
                break;
            case"right":
                if(spriteNum == 1){
                    image = right1;
                }
                if(spriteNum == 2){
                    image = right2;
                }
                break;
        }
        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
    }
}
