package spakborhills.entity;

import spakborhills.GamePanel;
import spakborhills.enums.EntityType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public abstract class Entity {
    public GamePanel gp;
    public int worldX, worldY;
    public int speed;
    public BufferedImage image;
    public String name;
    public boolean collision = false;
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public String direction = "down";
    public int spriteCounter = 0;
    public int spriteNum = 1;
    public Rectangle solidArea = new Rectangle(0,0,48,48);
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionON = false;
    public ArrayList<String> dialogues = new ArrayList<>();
    public int dialogueIndex = 0;
    public EntityType type;


    public Entity(GamePanel gp){
        this.gp = gp;
    }

    public BufferedImage setup(String imagePath){
        BufferedImage image = null;
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(  imagePath + ".png")));
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return image;
    }

    public void interact(){};

    public void update(){
        collisionON = false;
        checkCollisionAndMove();
        updateSprite();
    }

    public boolean use(Entity player) {
        System.out.println("Mencoba menggunakan item: " + this.name + " (aksi default tidak melakukan apa-apa)");
        return false;
    }

    private void checkCollisionAndMove(){
        //CHECK TILE COLLISION
        collisionON = false;
        gp.collisionChecker.checkTile(this);
        gp.collisionChecker.checkPlayer(this);

        // Check object collision

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