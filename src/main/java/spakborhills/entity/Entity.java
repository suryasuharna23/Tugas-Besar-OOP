package spakborhills.entity;

    import spakborhills.GamePanel;
    import spakborhills.enums.EntityType;
    import spakborhills.enums.Season;
    import spakborhills.enums.Weather;

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
        public Rectangle solidArea;
        public int solidAreaDefaultX, solidAreaDefaultY;
        public boolean collisionON = false;
        public ArrayList<String> dialogues = new ArrayList<>();
        public int dialogueIndex = 0;
        public EntityType type;
        protected Season currentSeason;
        protected int currentHour;
        protected Weather currentWeather;


    public int imageWidth;
    public int imageHeight;

    public Entity(GamePanel gp){
        this.gp = gp;
        
        this.imageWidth = gp.tileSize;
        this.imageHeight = gp.tileSize;
        this.solidArea = new Rectangle(0, 0, gp.tileSize, gp.tileSize); 
        
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
        
        collisionON = false;
        gp.collisionChecker.checkTile(this);
        gp.collisionChecker.checkPlayer(this);

        

        
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

    public void draw(Graphics2D g2) {
        BufferedImage imageToRender = null;
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (worldX + this.imageWidth > gp.player.worldX - gp.player.screenX &&
                worldX < gp.player.worldX + gp.player.screenX &&
                worldY + this.imageHeight > gp.player.worldY - gp.player.screenY &&
                worldY < gp.player.worldY + gp.player.screenY) {

            
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
            } else if (type == EntityType.INTERACTIVE_OBJECT ||
                    type == EntityType.PICKUP_ITEM ||
                    type == EntityType.STATIC_DECORATION) {
                
                if (this.image != null) {
                    imageToRender = this.image;
                } else {
                    imageToRender = this.down1; 
                }
            } else {
                
                
                imageToRender = this.down1;
            }

            if (imageToRender != null) {
                g2.drawImage(imageToRender, screenX, screenY, this.imageWidth, this.imageHeight, null);
            } else {
                
                g2.setColor(Color.MAGENTA);
                g2.fillRect(screenX, screenY, this.imageWidth, this.imageHeight);
                g2.setColor(Color.BLACK);
                String nameToDraw = (name != null && name.length() > 0) ? name : "NUL";
                g2.drawString(nameToDraw.substring(0, Math.min(nameToDraw.length(), 3)), screenX + 2, screenY + 12);

                
                if (type == EntityType.PLAYER || type == EntityType.NPC) {
                    System.err.println("[Entity.draw ERROR] imageToRender adalah NULL untuk animated entity: " + this.name +
                            " (direction: " + direction + ", spriteNum: " + spriteNum +
                            "). Periksa apakah sprite up1/up2/down1/down2/etc. sudah dimuat dengan benar.");
                } else if (type == EntityType.INTERACTIVE_OBJECT) {
                    System.err.println("[Entity.draw ERROR] imageToRender adalah NULL untuk INTERACTIVE_OBJECT: " + this.name +
                            ". Periksa apakah 'down1' atau 'image' sudah dimuat di konstruktor objek (misal OBJ_Bed).");
                }
            }
        }
    }

}