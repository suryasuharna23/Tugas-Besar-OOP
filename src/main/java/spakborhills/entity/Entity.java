    package spakborhills.entity;

    import spakborhills.GamePanel;
    import spakborhills.enums.EntityType;

    import javax.imageio.ImageIO;
    import java.awt.*;
    import java.awt.image.BufferedImage;
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.Objects;
    import java.util.Random;

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
        public int actionLockCounter = 0;
        public int actionLockInterval = 120; // 2 x 60 detik (fps)
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
        public void speak(){
            if (type == EntityType.NPC){
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
            }
            else{
                System.err.println("Peringatan: speak() dipanggil pada entitas non-NPC: " + this.name + " Tipe: " + this.type);
            }
        }

        public void update(){
            setAction();
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

        public void setAction(){
            if (type == EntityType.NPC) { // Hanya berlaku untuk NPC atau tipe lain yang memerlukan AI gerakan
                actionLockCounter++;

                if (actionLockCounter >= actionLockInterval) { // Gunakan >= untuk memastikan eksekusi
                    Random random = new Random();
                    int i = random.nextInt(125) + 1; // Range 1-125 untuk lebih banyak opsi

                    // Kembalikan kecepatan ke default jika sebelumnya diam
                    // (asumsi speed bisa diset 0 saat diam)
                    // if (this.speed == 0 && defaultSpeed > 0) {
                    //     this.speed = defaultSpeed;
                    // }

                    if (i <= 20) { // ~16% kemungkinan untuk diam
                        // Untuk diam, kita bisa tidak mengubah arah dan mungkin mengatur speed = 0
                        // Jika speed diatur ke 0, pastikan ada cara untuk mengembalikannya.
                        // Untuk saat ini, kita biarkan NPC tidak mengubah arah, yang berarti dia akan terus
                        // bergerak ke arah terakhir jika speed > 0, atau berhenti jika speed dihandle saat diam.
                        // Opsi lain: set speed = 0 dan kembalikan di awal blok if ini.
                        // Atau, tidak melakukan apa-apa pada direction, yang berarti dia akan melanjutkan arah sebelumnya
                        // atau berhenti jika speed = 0.
                        // Paling sederhana: jangan ubah arah, jika NPC menabrak, dia akan berhenti.
                    } else if (i <= 45) { // ~20% (25/125)
                        direction = "up";
                    } else if (i <= 70) { // ~20%
                        direction = "down";
                    } else if (i <= 95) { // ~20%
                        direction = "left";
                    } else { // ~24% (30/125)
                        direction = "right";
                    }
                    actionLockCounter = 0; // Reset counter
                }
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