package spakborhills.entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import spakborhills.GamePanel;
import spakborhills.enums.EntityType;
import spakborhills.enums.Season;
import spakborhills.enums.Weather;
import spakborhills.object.OBJ_Item;

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
    public Rectangle solidArea;
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionON = false;
    public ArrayList<String> dialogues = new ArrayList<>();
    public int dialogueIndex = 0;
    public EntityType type;
    public String marriageDialogue;
    protected Season currentSeason;
    protected int currentHour;
    protected Weather currentWeather;

    public Map<String, OBJ_Item> shippingBinTypes = new HashMap<>();
    public int maxBinTypes = 16;

    public int imageWidth;
    public int imageHeight;

    public Entity(GamePanel gp) {
        this.gp = gp;
        this.imageWidth = gp.tileSize;
        this.imageHeight = gp.tileSize;
        this.solidArea = new Rectangle(0, 0, gp.tileSize, gp.tileSize);
    }

    public BufferedImage setup(String imagePath) {
        BufferedImage loadedImage = null;
        try {
            InputStream is = getClass().getResourceAsStream(imagePath + ".png");
            if (is == null) {
                System.err.println("Error in Entity.setup: Could not find image resource: " + imagePath + ".png");

                if (imagePath.startsWith("/objects/")) {
                    InputStream fallbackIs = getClass().getResourceAsStream("/objects/unknown_item.png");
                    if (fallbackIs != null) {
                        System.out.println("Attempting to load fallback placeholder: /objects/unknown_item.png");
                        loadedImage = ImageIO.read(fallbackIs);
                        fallbackIs.close();
                        if (loadedImage != null)
                            return loadedImage;
                    } else {
                        System.err.println("Fallback placeholder /objects/unknown_item.png also not found.");
                    }
                }
                return null;
            }
            loadedImage = ImageIO.read(is);
            is.close();
            if (loadedImage == null) {
                System.err.println("Error in Entity.setup: ImageIO.read returned null for: " + imagePath
                        + ".png. File might be corrupted or empty.");
            }
        } catch (IOException e) {
            System.err.println("IOException in Entity.setup for " + imagePath + ".png: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("IllegalArgumentException in Entity.setup for " + imagePath + ".png (ImageIO issue?): "
                    + e.getMessage());
        }
        return loadedImage;
    }

    public void interact() {
    };

    public void update() {
        collisionON = false;
        checkCollisionAndMove();
        updateSprite();
    }

    public boolean use(Entity player) {
        System.out.println("Mencoba menggunakan item: " + this.name + " (aksi default tidak melakukan apa-apa)");
        return false;
    }

    private void checkCollisionAndMove() {
        collisionON = false;
        gp.collisionChecker.checkTile(this);
        gp.collisionChecker.checkPlayer(this);

        if (!collisionON) {
            switch (direction) {
                case "up":
                    worldY -= speed;
                    break;
                case "down":
                    worldY += speed;
                    break;
                case "left":
                    worldX -= speed;
                    break;
                case "right":
                    worldX += speed;
                    break;
            }
        }
    }

    private void updateSprite() {
        spriteCounter++;
        if (spriteCounter > 12) {
            if (spriteNum == 1) {
                spriteNum = 2;
            } else if (spriteNum == 2) {
                spriteNum = 1;
            }
            spriteCounter = 0;
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage imageToRender = null;
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (worldX + this.imageWidth > gp.player.worldX - gp.player.screenX &&
                worldX - this.imageWidth < gp.player.worldX + gp.player.screenX &&
                worldY + this.imageHeight > gp.player.worldY - gp.player.screenY &&
                worldY - this.imageHeight < gp.player.worldY + gp.player.screenY) {

            if (type == EntityType.PLAYER || type == EntityType.NPC) {
                switch (direction) {
                    case "up":
                        imageToRender = (spriteNum == 1) ? up1 : up2;
                        break;
                    case "down":
                        imageToRender = (spriteNum == 1) ? down1 : down2;
                        break;
                    case "left":
                        imageToRender = (spriteNum == 1) ? left1 : left2;
                        break;
                    case "right":
                        imageToRender = (spriteNum == 1) ? right1 : right2;
                        break;
                    default:
                        imageToRender = down1;
                }
            } else {
                imageToRender = this.image != null ? this.image : this.down1;
            }

            if (imageToRender != null) {
                g2.drawImage(imageToRender, screenX, screenY, this.imageWidth, this.imageHeight, null);
            } else {
                g2.setColor(Color.MAGENTA);
                g2.fillRect(screenX, screenY, this.imageWidth, this.imageHeight);
                g2.setColor(Color.BLACK);
                String nameToDraw = (name != null && !name.isEmpty()) ? name : "N/A";
                g2.setFont(new Font("Arial", Font.PLAIN, 10));
                g2.drawString(nameToDraw.substring(0, Math.min(nameToDraw.length(), 3)), screenX + 2, screenY + 12);

                if (type == EntityType.PLAYER || type == EntityType.NPC) {
                    System.err.println("[Entity.draw WARN] imageToRender is NULL for animated entity: " + this.name +
                            " (direction: " + direction + ", spriteNum: " + spriteNum +
                            "). Check sprite loading (up1/down1 etc.).");
                } else {
                    System.err.println("[Entity.draw WARN] imageToRender is NULL for static entity: " + this.name +
                            ". Check 'image' or 'down1' loading in its constructor (path: "
                            + (this.down1 == null ? "primary image path problem" : "this.image likely not set") + ").");
                }
            }
        }
    }
}