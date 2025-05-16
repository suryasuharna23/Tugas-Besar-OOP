package spakborhills.entity;

import spakborhills.GamePanel;
import spakborhills.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public abstract class Entity {
    GamePanel gp;
    public int worldX, worldY;
    public int speed;
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public String direction;
    public int spriteCounter = 0;
    public int spriteNum = 1;
    public Rectangle solidArea = new Rectangle(0,0,48,48);
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionON = false;
    public int actionLockCounter = 0;
    ArrayList<String> dialogues = new ArrayList<>();
    int dialogueIndex = 0;

    public Entity(GamePanel gp){
        this.gp = gp;
    }
    public BufferedImage setup(String imagePath){
        UtilityTool utilityTool = new UtilityTool();
        BufferedImage image = null;
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(  imagePath + ".png")));
            image = utilityTool.scaleImage(image, gp.tileSize, gp.tileSize);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return image;
    }

    public void setAction(){}
    public void speak(){
        gp.ui.currentDialogue = dialogues.get(dialogueIndex);
        dialogueIndex++;

        if (dialogueIndex == dialogues.size()){
            dialogueIndex = 0;
        }

        switch (gp.player.direction){
            case "up":
                direction = "down";
                break;
            case "down":
                direction = "up";
                break;
            case "left":
                direction = "right";
                break;
            case "right":
                direction = "left";
                break;
        }
    };

    public void update(){
        setAction();
        collisionON = false;
        checkCollisionAndMove();
        updateSprite();
    }

    public void pickUpObject(int i){
        if (i != 999){

        }
    }


    private void checkCollisionAndMove(){
        //CHECK TILE COLLISION
        collisionON = false;
        gp.collisionChecker.checkTile(this);
        gp.collisionChecker.checkObject(this, false);
        gp.collisionChecker.checkPlayer(this);

        // Check object collision
        int objIndex = gp.collisionChecker.checkObject(this,true);
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
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                worldY - gp.tileSize < gp.player.worldY + gp.player.screenY){

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
}