package spakborhills.entity;

import spakborhills.GamePanel;
import spakborhills.KeyHandler;
import spakborhills.enums.EntityType;
import spakborhills.object.OBJ_Door;
import spakborhills.object.OBJ_Key;
import spakborhills.object.OBJ_Potion;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class Player extends  Entity{
    KeyHandler keyH;
    public final int screenX;
    public final int screenY;
    private String farmName;
    public int maxEnergy;
    public int currentEnergy;
    public ArrayList<Entity> inventory = new ArrayList<>();
    public int currentEquippedItemIndex = -1;


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
        type = EntityType.PLAYER;
        inventory.add(new OBJ_Door(gp));
        inventory.add(new OBJ_Key(gp));
        inventory.add(new OBJ_Potion(gp));
    }

    public void setDefaultEnergy(){
        maxEnergy = 100;
        currentEnergy = 75;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
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
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed || keyH.enterPressed){
            if (!gp.keyH.inventoryPressed){
                updateDirection();
                checkCollisionAndMove();
            }
            updateSprite();
        }
//       INVENTORY LOGIC
        if (gp.keyH.inventoryPressed) {
            gp.keyH.inventoryPressed = false; // Konsumsi input agar tidak berulang
            if (gp.gameState == gp.playState) {
                gp.gameState = gp.inventoryState; // Buat state baru untuk inventaris
            } else if (gp.gameState == gp.inventoryState) {
                gp.gameState = gp.playState;
            }
        }
    }

    private void checkCollisionAndMove() {
        collisionON = false;
        gp.collisionChecker.checkTile(this); // Periksa kolisi dengan tile

        // Dapatkan indeks entitas (NPC atau Objek) yang disentuh atau dihadapi pemain
        // Metode checkEntity akan memeriksa semua entitas dalam gp.entities
        int entityIndex = gp.collisionChecker.checkEntity(this, gp.entities);

        // Hanya proses interaksi JIKA tombol Enter ditekan
        if (gp.keyH.enterPressed) {
            if (entityIndex != 999) { // Jika ada entitas yang terdeteksi
                Entity interactedEntity = gp.entities.get(entityIndex); // Dapatkan entitas tersebut

                // PERIKSA TIPE ENTITAS SEBELUM BERINTERAKSI
                if (interactedEntity.type == EntityType.NPC) {
                    // Jika itu NPC, masuk ke mode dialog dan panggil metode speak() dari NPC tersebut
                    gp.gameState = gp.dialogueState;
                    gp.currentInteractingNPC = interactedEntity;
                    interactedEntity.speak(); // Ini akan menjalankan metode speak() milik NPC
                } else if (interactedEntity.type == EntityType.INTERACTIVE_OBJECT) {
                    // Jika itu objek interaktif (pintu, peti, dll.)
                    // Di sini Anda akan menambahkan logika spesifik untuk objek tersebut
                    // Contoh:
                    if (interactedEntity.name.equals("Door")) {
                        // Logika untuk membuka pintu
                        // Misalnya, periksa apakah pemain punya kunci
                        // if (playerHasKey("Key")) { // Anda perlu implementasi playerHasKey
                        //    gp.ui.showMessage("Kamu membuka pintu!");
                        //    gp.entities.remove(entityIndex); // Hapus pintu jika terbuka permanen
                        // } else {
                        //    gp.ui.showMessage("Pintu ini terkunci.");
                        // }
                        gp.ui.showMessage("Kamu berinteraksi dengan " + interactedEntity.name); // Pesan sementara
                    } else if (interactedEntity.name.equals("Chest")) {
                        // Logika untuk membuka peti
                        gp.ui.showMessage("Kamu membuka " + interactedEntity.name);
                        // gp.entities.remove(entityIndex); // Mungkin peti bisa dibuka sekali saja
                        // Tambahkan item ke inventory pemain
                    } else {
                        gp.ui.showMessage("Kamu melihat " + interactedEntity.name);
                    }
                } else if (interactedEntity.type == EntityType.PICKUP_ITEM) {
                    // Jika itu item yang bisa diambil (kunci, sepatu bot)
                    // Logika untuk mengambil item
                    gp.ui.showMessage("Kamu mengambil " + interactedEntity.name + "!");
                    // Contoh: gp.player.addItemToInventory(interactedEntity); // Anda perlu implementasi inventory
                    gp.entities.remove(entityIndex); // Hapus item dari peta setelah diambil
                }
                // Anda bisa menambahkan lebih banyak else if untuk tipe entitas lain jika ada

            }
            gp.keyH.enterPressed = false; // Penting: Reset status tombol Enter setelah diproses
        }

        // Logika pergerakan pemain hanya jika tidak ada kolisi
        if (!collisionON) {
            switch (direction) {
                case "up": worldY -= speed; break;
                case "down": worldY += speed; break;
                case "left": worldX -= speed; break;
                case "right": worldX += speed; break;
            }
        }

    }

    public void interactNPC(int i){
        if (i != 999){
            if(gp.keyH.enterPressed){
                gp.gameState = gp.dialogueState;
                Entity npc = gp.entities.get(i);
                if (npc.type == EntityType.NPC){
                    gp.currentInteractingNPC = npc;
                    npc.speak();
                }
            }
        }
        gp.keyH.enterPressed = false;
    }


    public boolean addItemToInventory(Entity item){
        inventory.add(item);
        gp.ui.showMessage("You got: " + item.name);
        return true;
    }

    public void removeItemFromInventory(int item){
        inventory.remove(item);
    }

    public Entity getEquippedItem(){
        if (currentEquippedItemIndex != -1){
            return inventory.get(currentEquippedItemIndex);
        }
        return null;
    }

    public void selectItemAndUse() {
        if (inventory.isEmpty()) {
            return; // Tidak ada item untuk digunakan
        }

        int itemIndex = gp.ui.inventoryCommandNum; // Dapatkan indeks item yang dipilih dari UI

        if (itemIndex >= 0 && itemIndex < inventory.size()) {
            Entity selectedItem = inventory.get(itemIndex);

            // Panggil metode use() dari item yang dipilih
            // 'this' merujuk ke instance Player saat ini
            boolean itemWasConsumed = selectedItem.use(this);

            if (itemWasConsumed) {
                // Jika metode use() mengembalikan true, hapus item dari inventaris
                removeItemFromInventory(itemIndex); // Gunakan overload yang menerima indeks
                // Jika inventoryCommandNum menunjuk ke item terakhir dan item itu dihapus,
                // sesuaikan inventoryCommandNum agar tidak out of bounds
                if (gp.ui.inventoryCommandNum >= inventory.size() && !inventory.isEmpty()) {
                    gp.ui.inventoryCommandNum = inventory.size() - 1;
                } else if (inventory.isEmpty()) {
                    gp.ui.inventoryCommandNum = 0; // Atau -1 jika Anda handle itu
                }
            }

        } else {
            gp.ui.showMessage("Tidak ada item yang dipilih.");
        }
    }

    public void pickUpObject(int i) { // i adalah indeks di gp.entities
        if (i != 999) {
            Entity object = gp.entities.get(i);
            // Hanya item yang bisa diambil yang akan di-pickup
            if (object.type == EntityType.PICKUP_ITEM ||
                    object.name.equals("Key") || // Anda mungkin ingin membuat tipe khusus untuk kunci
                    object.name.equals("Boots")) { // Dan sepatu bot

                if (addItemToInventory(object)) {
                    gp.entities.remove(i);
                }
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
