package app.gui.entity;

import app.gui.main.GamePanel;
import app.gui.main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Entity {

    GamePanel panel;
    KeyHandler key;

    //Constructor
    public Player(GamePanel panel, KeyHandler key) {
        this.panel = panel;
        this.key = key;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        x = 100;
        y = 100;
        speed = 4;
        direction = "down";
    }

    public void getPlayerImage() {
        try {
            w1 = ImageIO.read(getClass().getResourceAsStream("/app/gui/asset/abigail/Abigail_W1.png"));
            w2 = ImageIO.read(getClass().getResourceAsStream("/app/gui/asset/abigail/Abigail_W2.png"));
            a1 = ImageIO.read(getClass().getResourceAsStream("/app/gui/asset/abigail/Abigail_A1.png"));
            a2 = ImageIO.read(getClass().getResourceAsStream("/app/gui/asset/abigail/Abigail_A2.png"));
            s1 = ImageIO.read(getClass().getResourceAsStream("/app/gui/asset/abigail/Abigail_S1.png"));
            s2 = ImageIO.read(getClass().getResourceAsStream("/app/gui/asset/abigail/Abigail_S2.png"));
            d1 = ImageIO.read(getClass().getResourceAsStream("/app/gui/asset/abigail/Abigail_D1.png"));
            d2 = ImageIO.read(getClass().getResourceAsStream("/app/gui/asset/abigail/Abigail_D2.png"));


        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(){
        if (key.pressUp == true) {
            y -= speed;
            direction = "up";
        }
        else if (key.pressDown == true) {
            y += speed;
            direction = "down";
        }
        else if (key.pressLeft == true) {
            x -= speed;
            direction = "left";
        }
        else if (key.pressRight == true) {
            x += speed;
            direction = "right";
        }
        spriteCounter++;
        if (spriteCounter > 10) {
            if (spriteNum == 1) {
                spriteNum = 2;
            }
            else if (spriteNum == 2) {
                spriteNum = 1;
            }
            spriteCounter = 0;
        }
    }

    public void draw(Graphics2D g2){
//        g2.setColor(Color.pink);
//        g2.fillRect(x, y, panel.tileSize, panel.tileSize);

        BufferedImage image = null;
        switch (direction) {
            case "up":
                if (spriteNum == 1) {
                    image = w1;
                }
                if (spriteNum == 2) {
                    image = w2;
                }
                break;
            case "down":
                if (spriteNum == 1) {
                    image = s1;
                }
                if (spriteNum == 2) {
                    image = s2;
                }
                break;
            case "left":
                if (spriteNum == 1) {
                    image = a1;
                }
                if (spriteNum == 2) {
                    image = a2;
                }
                break;
            case "right":
                if (spriteNum == 1) {
                    image = d1;
                }
                if (spriteNum == 2) {
                    image = d2;
                }
                break;
        }
        g2.drawImage(image, x, y, panel.tileSize, panel.tileSize, null);
    }



}
