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
        public String marriageDialogue;
        protected Season currentSeason;
        protected int currentHour;
        protected Weather currentWeather;


    public int imageWidth;
    public int imageHeight;

    public Entity(GamePanel gp){
        this.gp = gp;
        // Default ukuran visual dan solidArea adalah 1 tile
        this.imageWidth = gp.tileSize;
        this.imageHeight = gp.tileSize;
        this.solidArea = new Rectangle(0, 0, gp.tileSize, gp.tileSize); // Default solid area
        // solidAreaDefaultX dan Y akan diatur oleh sub-kelas jika perlu offset
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

    public void draw(Graphics2D g2) {
        BufferedImage imageToRender = null;
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (worldX + this.imageWidth > gp.player.worldX - gp.player.screenX &&
                worldX < gp.player.worldX + gp.player.screenX &&
                worldY + this.imageHeight > gp.player.worldY - gp.player.screenY &&
                worldY < gp.player.worldY + gp.player.screenY) {

            // Logika pemilihan gambar berdasarkan tipe entitas
            if (type == EntityType.PLAYER || type == EntityType.NPC) {
                // Untuk Player dan NPC, selalu gunakan logika animasi sprite
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
                        // Fallback jika arah tidak valid (seharusnya tidak terjadi untuk Player/NPC aktif)
                        imageToRender = down1; // Atau gambar default lainnya
                }
            } else if (type == EntityType.INTERACTIVE_OBJECT ||
                    type == EntityType.PICKUP_ITEM ||
                    type == EntityType.STATIC_DECORATION) {
                // Untuk objek, item, atau dekorasi statis, prioritaskan this.image, lalu this.down1
                if (this.image != null) {
                    imageToRender = this.image;
                } else {
                    imageToRender = this.down1; // OBJ_Bed Anda mengatur down1
                }
            } else {
                // Fallback untuk tipe entitas lain yang tidak terdefinisi secara spesifik di atas
                // Mungkin bisa default ke this.down1 jika ada, atau null
                imageToRender = this.down1;
            }

            if (imageToRender != null) {
                g2.drawImage(imageToRender, screenX, screenY, this.imageWidth, this.imageHeight, null);
            } else {
                // Fallback: Gambar kotak berwarna jika tidak ada gambar
                g2.setColor(Color.MAGENTA);
                g2.fillRect(screenX, screenY, this.imageWidth, this.imageHeight);
                g2.setColor(Color.BLACK);
                String nameToDraw = (name != null && name.length() > 0) ? name : "NUL";
                g2.drawString(nameToDraw.substring(0, Math.min(nameToDraw.length(), 3)), screenX + 2, screenY + 12);

                // Log error jika gambar null untuk tipe yang seharusnya punya gambar
                if (type == EntityType.PLAYER || type == EntityType.NPC) {
                    System.err.println("[Entity.draw ERROR] imageToRender adalah NULL untuk animated entity: " + this.name +
                            " (direction: " + direction + ", spriteNum: " + spriteNum +
                            "). Periksa apakah sprite up1/up2/down1/down2/etc. sudah dimuat dengan benar.");
                } else if (type == EntityType.INTERACTIVE_OBJECT) {
                    System.err.println("[Entity.draw ERROR] imageToRender adalah NULL untuk INTERACTIVE_OBJECT: " + this.name +
                            ". Periksa apakah 'down1' atau 'image' sudah dimuat di konstruktor objek (misal OBJ_Bed).");
                }
            }
        } else {
//             Log jika objek di-cull (untuk debugging jika objek tidak muncul karena dianggap di luar layar)
             if (this.name != null && this.name.toLowerCase().contains("bed")) {
                 System.out.println("[Entity.draw DEBUG] " + this.name +
                                    " DI-CULL. worldX=" + worldX + ", worldY=" + worldY +
                                    ", playerX=" + gp.player.worldX + ", playerY=" + gp.player.worldY);
             }
        }
    }

}