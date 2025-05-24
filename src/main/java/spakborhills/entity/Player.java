package spakborhills.entity;

import spakborhills.GamePanel;
import spakborhills.KeyHandler;
import spakborhills.action.Command;
import spakborhills.action.EatCommand;
import spakborhills.enums.EntityType;
import spakborhills.enums.ItemType;
import spakborhills.enums.Season;
import spakborhills.enums.Weather;
import spakborhills.interfaces.Edible;
import spakborhills.object.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import spakborhills.enums.Location;
import spakborhills.enums.FishType;


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
        // COMMON FISH
        inventory.add(new OBJ_Fish(
            gp, ItemType.FISH, "Bullhead", true, 50, 35,
            Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
            Arrays.asList(Weather.SUNNY, Weather.RAINY),
            Arrays.asList(Location.MOUNTAIN_LAKE),
            FishType.COMMON
        ));
        inventory.add(new OBJ_Fish(
            gp, ItemType.FISH, "Carp", true, 45, 30,
            Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
            Arrays.asList(Weather.SUNNY, Weather.RAINY),
            Arrays.asList(Location.MOUNTAIN_LAKE, Location.POND),
            FishType.COMMON
        ));
        inventory.add(new OBJ_Fish(
            gp, ItemType.FISH, "Chub", true, 40, 25,
            Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
            Arrays.asList(Weather.SUNNY, Weather.RAINY),
            Arrays.asList(Location.FOREST_RIVER, Location.MOUNTAIN_LAKE),
            FishType.COMMON
        ));

        // REGULAR FISH
        inventory.add(new OBJ_Fish(
            gp, ItemType.FISH, "Largemouth Bass", true, 65, 40,
            Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
            Arrays.asList(Weather.SUNNY, Weather.RAINY),
            Arrays.asList(Location.MOUNTAIN_LAKE),
            FishType.REGULAR
        ));
        inventory.add(new OBJ_Fish(
            gp, ItemType.FISH, "Rainbow Trout", true, 120, 60,
            Arrays.asList(Season.SUMMER),
            Arrays.asList(Weather.SUNNY),
            Arrays.asList(Location.FOREST_RIVER, Location.MOUNTAIN_LAKE),
            FishType.REGULAR
        ));
        inventory.add(new OBJ_Fish(
            gp, ItemType.FISH, "Sturgeon", true, 200, 150,
            Arrays.asList(Season.SUMMER, Season.WINTER),
            Arrays.asList(Weather.SUNNY, Weather.RAINY),
            Arrays.asList(Location.MOUNTAIN_LAKE),
            FishType.REGULAR
        ));
        inventory.add(new OBJ_Fish(
            gp, ItemType.FISH, "Midnight Carp", true, 180, 120,
            Arrays.asList(Season.WINTER, Season.FALL),
            Arrays.asList(Weather.SUNNY, Weather.RAINY),
            Arrays.asList(Location.MOUNTAIN_LAKE, Location.POND),
            FishType.REGULAR
        ));
        inventory.add(new OBJ_Fish(
            gp, ItemType.FISH, "Flounder", true, 100, 80,
            Arrays.asList(Season.SPRING, Season.SUMMER),
            Arrays.asList(Weather.SUNNY, Weather.RAINY),
            Arrays.asList(Location.OCEAN),
            FishType.REGULAR
        ));
        inventory.add(new OBJ_Fish(
            gp, ItemType.FISH, "Halibut", true, 90, 65,
            Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
            Arrays.asList(Weather.SUNNY, Weather.RAINY),
            Arrays.asList(Location.OCEAN),
            FishType.REGULAR
        ));
        inventory.add(new OBJ_Fish(
            gp, ItemType.FISH, "Octopus", true, 350, 215,
            Arrays.asList(Season.SUMMER),
            Arrays.asList(Weather.SUNNY, Weather.RAINY),
            Arrays.asList(Location.OCEAN),
            FishType.REGULAR
        ));
        inventory.add(new OBJ_Fish(
            gp, ItemType.FISH, "Pufferfish", true, 200, 125,
            Arrays.asList(Season.SUMMER),
            Arrays.asList(Weather.SUNNY),
            Arrays.asList(Location.OCEAN),
            FishType.REGULAR
        ));
        inventory.add(new OBJ_Fish(
            gp, ItemType.FISH, "Sardine", true, 60, 40,
            Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
            Arrays.asList(Weather.SUNNY, Weather.RAINY),
            Arrays.asList(Location.OCEAN),
            FishType.REGULAR
        ));
        inventory.add(new OBJ_Fish(
            gp, ItemType.FISH, "Super Cucumber", true, 250, 175,
            Arrays.asList(Season.SUMMER, Season.FALL, Season.WINTER),
            Arrays.asList(Weather.SUNNY, Weather.RAINY),
            Arrays.asList(Location.OCEAN),
            FishType.REGULAR
        ));
        inventory.add(new OBJ_Fish(
            gp, ItemType.FISH, "Catfish", true, 150, 100,
            Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL),
            Arrays.asList(Weather.RAINY),
            Arrays.asList(Location.FOREST_RIVER, Location.POND),
            FishType.REGULAR
        ));
        inventory.add(new OBJ_Fish(
            gp, ItemType.FISH, "Salmon", true, 120, 80,
            Arrays.asList(Season.FALL),
            Arrays.asList(Weather.SUNNY, Weather.RAINY),
            Arrays.asList(Location.FOREST_RIVER),
            FishType.REGULAR
        ));

        // LEGENDARY FISH
        inventory.add(new OBJ_Fish(
            gp, ItemType.FISH, "Angler", true, 1000, 800,
            Arrays.asList(Season.FALL),
            Arrays.asList(Weather.SUNNY, Weather.RAINY),
            Arrays.asList(Location.POND),
            FishType.LEGENDARY
        ));
        inventory.add(new OBJ_Fish(
            gp, ItemType.FISH, "Crimsonfish", true, 1200, 950,
            Arrays.asList(Season.SUMMER),
            Arrays.asList(Weather.SUNNY, Weather.RAINY),
            Arrays.asList(Location.OCEAN),
            FishType.LEGENDARY
        ));
        inventory.add(new OBJ_Fish(
            gp, ItemType.FISH, "Glacierfish", true, 1200, 950,
            Arrays.asList(Season.WINTER),
            Arrays.asList(Weather.SUNNY, Weather.RAINY),
            Arrays.asList(Location.FOREST_RIVER),
            FishType.LEGENDARY
        ));
        inventory.add(new OBJ_Fish(
            gp, ItemType.FISH, "Legend", true, 1500, 1200,
            Arrays.asList(Season.SPRING),
            Arrays.asList(Weather.RAINY),
            Arrays.asList(Location.MOUNTAIN_LAKE),
            FishType.LEGENDARY
        ));
        inventory.add(new OBJ_Food(gp, ItemType.FOOD, "Fish n' Chips", true, 150, 135, 50));
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

        if (gp.gameState == gp.playState) {

            // Handle aksi makan dengan tombol 'E'
            if (this.keyH != null && this.keyH.eatPressed) {
                System.out.println("DEBUG: Player.update (playState) - eatPressed is true.");
                Entity equippedItem = getEquippedItem(); // Mendapatkan item yang di-equip
                if (equippedItem != null) {
                    System.out.println("DEBUG: Player.update - Equipped item: " + equippedItem.name + " | Class: " + equippedItem.getClass().getSimpleName());
                    if (equippedItem instanceof Edible) { // Pengecekan Edible interface yang benar
                        System.out.println("DEBUG: Player.update - Equipped item IS Edible. Creating EatCommand.");
                        Command eatAction = new EatCommand(this, (Edible) equippedItem);
                        eatAction.execute(gp); // gp mungkin tidak diperlukan jika EatCommand tidak menggunakannya
                    } else {
                        if (gp.ui != null) gp.ui.showMessage(equippedItem.name + " is not edible.");
                        System.out.println("DEBUG: Player.update - Equipped item " + equippedItem.name + " is NOT Edible.");
                    }
                } else {
                    if (gp.ui != null) gp.ui.showMessage("Nothing held to eat.");
                    System.out.println("DEBUG: Player.update - No item equipped to eat.");
                }
                this.keyH.eatPressed = false; // Reset flag setelah diproses
            }

            // Handle interaksi umum dengan 'Enter' (diproses di dalam checkCollisionAndMove jika tidak bergerak)
            // atau jika pemain menekan Enter tanpa bergerak:
            if (this.keyH != null && this.keyH.enterPressed && !(keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed)) {
                checkCollisionAndMove(); // Panggil untuk interaksi saat diam
                // enterPressed akan direset di dalam checkCollisionAndMove
            }

        } else if (gp.gameState == gp.dialogueState || gp.gameState == gp.inventoryState) {
            // Animasi idle saat di state ini
            spriteNum = 1;
            spriteCounter = 0;
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

    public void consumeItemFromInventory(Entity itemToConsume) {
        Entity equippedItemBeforeConsumption = getEquippedItem();
        boolean wasEquippedAndIsTheSameItem = (equippedItemBeforeConsumption == itemToConsume);

        boolean removed = this.inventory.remove(itemToConsume); // remove(Object)

        if (removed) {
            System.out.println("DEBUG: Consumed and removed from inventory: " + itemToConsume.name);
            if (wasEquippedAndIsTheSameItem) {
                this.currentEquippedItemIndex = -1; // Reset slot equip jika item yang dipegang habis
                System.out.println("DEBUG: Equipped item slot cleared because consumed item was equipped.");
            }
            // Sesuaikan inventoryCommandNum jika perlu (setelah item dihapus)
            if (gp.ui.inventoryCommandNum >= inventory.size() && !inventory.isEmpty()) {
                gp.ui.inventoryCommandNum = inventory.size() - 1;
            } else if (inventory.isEmpty()) {
                gp.ui.inventoryCommandNum = 0; // Atau -1, tergantung bagaimana UI Anda handle inventory kosong
            }
        } else {
            System.out.println("WARNING: Attempted to consume " + itemToConsume.name + " but it was not found/removed from inventory list.");
            // Jika item yang di-equip tidak bisa dihapus dari inventory (seharusnya tidak terjadi),
            // tapi tetap di-flag untuk dikonsumsi, tetap bersihkan pegangannya.
            if (wasEquippedAndIsTheSameItem) {
                this.currentEquippedItemIndex = -1;
            }
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

    public void equipItem(int inventoryIndex) {
        if (inventoryIndex >= 0 && inventoryIndex < inventory.size()) {
            this.currentEquippedItemIndex = inventoryIndex;
            Entity equipped = getEquippedItem();
            if (equipped != null) {
                // Memanggil use() pada item yang di-equip.
                // Untuk OBJ_Food, ini akan menampilkan "Press 'E' to eat."
                // Untuk item lain, bisa jadi aksi equip atau pesan berbeda.
                equipped.use(this);
            }
        } else {
            this.currentEquippedItemIndex = -1; // Indeks tidak valid, tidak ada item yang di-equip
            // gp.ui.showMessage("No item selected to equip."); // Opsional
        }
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