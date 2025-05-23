package spakborhills.entity;

import spakborhills.GamePanel;
import spakborhills.KeyHandler;
import spakborhills.enums.EntityType;
import spakborhills.enums.ItemType;
import spakborhills.enums.Season;
import spakborhills.enums.Weather;
import spakborhills.object.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class Player extends  Entity{
    KeyHandler keyH;
    public final int screenX;
    public final int screenY;
    private String farmName;
    public int currentEnergy;

    public ArrayList<Entity> inventory = new ArrayList<>();
    public int currentEquippedItemIndex = -1;

    // WEDDING
    public NPC partner;
    public boolean justGotMarried = false;
    private boolean married = false;
    // ENERGY
    public int MAX_POSSIBLE_ENERGY = 100;
    public static final int MIN_ENERGY_THRESHOLD = -20;
    public static final int LOW_ENERGY_PENALTY_THRESHOLD_PERCENT = 10;
    public static final int ENERGY_REFILL_AT_ZERO = 10;
    private boolean isCurrentlySleeping = false;


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
        currentEnergy = MAX_POSSIBLE_ENERGY;
        this.isCurrentlySleeping = false;
        inventory.add(new OBJ_Door(gp));
        inventory.add(new OBJ_Potion(gp));
        inventory.add(new OBJ_ProposalRing(gp));
        inventory.add(new OBJ_Seed(gp, ItemType.SEEDS, "Pumpkin", false, 150, 75, 1,7,Season.FALL, Weather.RAINY));
        inventory.add(new OBJ_Seed(gp, ItemType.SEEDS, "Cranberry", false,100, 50, 1,2,Season.FALL, Weather.RAINY));
        inventory.add(new OBJ_Seed(gp, ItemType.SEEDS, "Melon", false,80, 40, 1,4,Season.FALL, Weather.RAINY));
        inventory.add(new OBJ_Seed(gp, ItemType.SEEDS, "Hot Pepper", false, 40, 20, 1,1,Season.FALL, Weather.RAINY));
        inventory.add(new OBJ_Seed(gp, ItemType.SEEDS, "Tomato", false, 50, 25, 1,3, Season.SUMMER, Weather.RAINY));
    }


    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public boolean tryDecreaseEnergy(int cost){
        if (cost <= 0) return true; // Tidak ada biaya atau malah menambah (ditangani increaseEnergy)

        if (currentEnergy <= MIN_ENERGY_THRESHOLD) {
            gp.ui.showMessage("You're completely exhausted and can't do anything else!");
            // Otomatis tidur jika mencoba beraksi saat sudah -20 (seharusnya sudah tidur sebelumnya)
            // Namun, untuk keamanan, bisa panggil sleep() di sini jika belum tidur.
            if (!isCurrentlySleeping) {
                sleep("You tried to work while utterly exhausted and passed out again!");
            }
            return false; // Aksi gagal
        }

        currentEnergy -= cost;
        gp.ui.showMessage("Energy -" + cost); // Feedback ke UI

        if (currentEnergy <= MIN_ENERGY_THRESHOLD) {
            currentEnergy = MIN_ENERGY_THRESHOLD; // Pastikan tidak lebih rendah dari -20
            gp.ui.showMessage("You've collapsed from exhaustion!");
            sleep("You collapsed from sheer exhaustion!"); // Otomatis tidur
            return true; // Aksi dilakukan, tapi pemain pingsan setelahnya
        }
        return true;
    }
    public void increaseEnergy(int amount){
        currentEnergy += amount;
        if (currentEnergy > MAX_POSSIBLE_ENERGY){
            currentEnergy = MAX_POSSIBLE_ENERGY;
        }
    }
    public boolean isCurrentlySleeping() {
        return isCurrentlySleeping;
    }
    public void setCurrentlySleeping(boolean currentlySleeping) {
        isCurrentlySleeping = currentlySleeping;
    }

    // Metode utama untuk tidur
    public void sleep(String sleepMessagePrefix) {
        if (isCurrentlySleeping()) { // Gunakan getter jika ada
            System.out.println("[Player] sleep() called while already isCurrentlySleeping. Duplicate call ignored.");
            return;
        }
        setCurrentlySleeping(true); // Tandai bahwa pemain memulai proses tidur

        // 1. Logika Pemulihan Energi
        String energyRecoveryMessage;
        if (currentEnergy == 0) {
            currentEnergy = ENERGY_REFILL_AT_ZERO;
            energyRecoveryMessage = "You slept right on the brink and only recovered " + ENERGY_REFILL_AT_ZERO + " energy.";
        } else if (currentEnergy < (MAX_POSSIBLE_ENERGY * LOW_ENERGY_PENALTY_THRESHOLD_PERCENT / 100.0)) {
            // Contoh: jika maxEnergy 100, threshold 10% adalah 10. Jika energi < 10.
            currentEnergy = MAX_POSSIBLE_ENERGY / 2;
            energyRecoveryMessage = "You were deeply exhausted and \n only recovered half your energy.";
        } else {
            currentEnergy = MAX_POSSIBLE_ENERGY;
            energyRecoveryMessage = "You feel fully refreshed after a good night's sleep!";
        }

        // 2. Siapkan dialog untuk ditampilkan oleh GamePanel/UI
        // GamePanel akan mengambil gp.ui.currentDialogue ini saat dalam state transisi tidur.
        gp.ui.currentDialogue = sleepMessagePrefix + "\n" + energyRecoveryMessage;
        System.out.println("[Player] Sleeping. Message: " + gp.ui.currentDialogue); // Debug

    }

    public boolean isMarried() {
        return married;
    }

    public void setMarried(boolean married) {
        this.married = married;
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

    // In Player.java, method checkCollisionAndMove()
    private void checkCollisionAndMove() {
        collisionON = false;
        gp.collisionChecker.checkTile(this);

        // Check collision with general entities (which should include NPCs and objects)
        int entityIndex = gp.collisionChecker.checkEntity(this, gp.entities);

        if (gp.keyH.enterPressed) {
            if (entityIndex != 999) {
                Entity interactedEntity = gp.entities.get(entityIndex); // Get from the list used for collision

                if (interactedEntity instanceof NPC) { // Check if it's an NPC
                    NPC npc = (NPC) interactedEntity; // Cast to NPC
                    gp.gameState = gp.dialogueState;
                    gp.currentInteractingNPC = npc; // Store the actual NPC object
                    npc.openInteractionMenu();
                } else if (interactedEntity.type == EntityType.INTERACTIVE_OBJECT) {
                    // Handle interactive objects (doors, chests) as before
                    if (interactedEntity.name.equals("Door")) {
                        gp.ui.showMessage("Kamu berinteraksi dengan " + interactedEntity.name);
                    } else if (interactedEntity.name.equals("Chest")) {
                        gp.ui.showMessage("Kamu membuka " + interactedEntity.name);
                    } else {
                        gp.ui.showMessage("Kamu melihat " + interactedEntity.name);
                    }
                } else if (interactedEntity.type == EntityType.PICKUP_ITEM) {
                    // Handle pickup items as before
                    gp.ui.showMessage("Kamu mengambil " + interactedEntity.name + "!");
                    addItemToInventory(interactedEntity); // Make sure this method exists or is pickUpObject
                    gp.entities.remove(entityIndex); // Remove item from the map
                }
            }
            gp.keyH.enterPressed = false;
        }

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
                NPC npc = gp.npcs.get(i);
                if (npc.type == EntityType.NPC){
                    gp.currentInteractingNPC = npc;
                    npc.openInteractionMenu();
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

    public void plantSeed(String name) {
        tryDecreaseEnergy(5);
        gp.gameClock.getTime().advanceTime(5);
        }
}

//
//    public void fishing(Fish fish) {
//
//    }
//
//    public void eating(Items item) {
//
//    }