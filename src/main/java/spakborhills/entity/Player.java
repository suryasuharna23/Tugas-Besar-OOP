package spakborhills.entity;

import spakborhills.GameClock;
import spakborhills.GamePanel;
import spakborhills.KeyHandler;
import spakborhills.action.Command;
import spakborhills.action.EatCommand;
import spakborhills.cooking.ActiveCookingProcess;
import spakborhills.cooking.FoodFactory;
import spakborhills.cooking.Recipe;
import spakborhills.cooking.RecipeManager;
import spakborhills.enums.EntityType;
import spakborhills.enums.ItemType;
import spakborhills.enums.Season;
import spakborhills.enums.Weather;
import spakborhills.enums.Location;
import spakborhills.interfaces.Edible;
import spakborhills.object.*;
import spakborhills.Tile.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

import spakborhills.enums.Location;
import spakborhills.enums.FishType;
import spakborhills.enums.ItemType;
import java.util.List;


public class Player extends Entity{
    KeyHandler keyH;
    public final int screenX;
    public final int screenY;
    private String farmName;
    public int currentEnergy;
    private String location;

    public ArrayList<Entity> inventory = new ArrayList<>();
    public int currentEquippedItemIndex = -1;

    
    public int gold;
    public ArrayList<Entity> itemsInShippingBinToday = new ArrayList<>();
    public boolean hasUsedShippingBinToday = false;
    public int goldFromShipping = 0;

    

    
    public Map<String, Boolean> recipeUnlockStatus = new HashMap<>();
    public ArrayList<ActiveCookingProcess> activeCookingProcesses = new ArrayList<>();

    
    public int totalFishCaught = 0;
    public int totalCommonFishCaught = 0;
    public int totalRegularFishCaught = 0;
    public int totalLegendaryFishCaught = 0;
    public Map<String, Boolean> firstHarvestByName = new HashMap<>(); 
    public boolean hasFishedPufferfish = false;
    public boolean hasFishedLegend = false;
    public boolean hasObtainedHotPepper = false;


    
    public NPC partner;
    public boolean justGotMarried = false;
    private boolean married = false;
    
    public int MAX_POSSIBLE_ENERGY = 100;
    public static final int MIN_ENERGY_THRESHOLD = -20;
    public static final int LOW_ENERGY_PENALTY_THRESHOLD_PERCENT = 10;
    public static final int ENERGY_REFILL_AT_ZERO = 10;
    private boolean isCurrentlySleeping = false;

    
    private Location currentLocation;
    private boolean playerIsActuallyFishing;

    public Location getCurrentLocation() {
        return currentLocation;
    }
    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

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

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDefaultValues(){
        inventory.clear();
        worldX = gp.tileSize * 21;
        worldY = gp.tileSize * 26;
        speed = 4;
        direction = "down";
        type = EntityType.PLAYER;
        currentEnergy = MAX_POSSIBLE_ENERGY;
        this.isCurrentlySleeping = false;
        gold = 500;
        initializeRecipeStatus();
        addItemToInventory(new OBJ_Misc(gp, ItemType.MISC, "Proposal Ring", false, 0, 0));
        addItemToInventory(new OBJ_Seed(gp, ItemType.SEEDS, "Pumpkin", false, 150, 75, 1,7,Season.FALL, Weather.RAINY));
        addItemToInventory(new OBJ_Seed(gp, ItemType.SEEDS, "Cranberry", false,100, 50, 1,2,Season.FALL, Weather.RAINY));
        addItemToInventory(new OBJ_Seed(gp, ItemType.SEEDS, "Melon", false,80, 40, 1,4,Season.FALL, Weather.RAINY));
        addItemToInventory(new OBJ_Seed(gp, ItemType.SEEDS, "Hot Pepper", false, 40, 20, 1,1,Season.FALL, Weather.RAINY));
        addItemToInventory(new OBJ_Seed(gp, ItemType.SEEDS, "Tomato", false, 50, 25, 1,3, Season.SUMMER, Weather.RAINY));
        addItemToInventory(new OBJ_Food(gp, ItemType.FOOD, "Fish n' Chips", true, 150, 135, 50));
        
        addItemToInventory(new OBJ_Equipment(gp, ItemType.EQUIPMENT, "Hoe", false, 0, 0));
        addItemToInventory(new OBJ_Equipment(gp, ItemType.EQUIPMENT, "Watering Can", false, 0, 0));
        addItemToInventory(new OBJ_Equipment(gp, ItemType.EQUIPMENT, "Pickaxe", false, 0, 0));
        addItemToInventory(new OBJ_Equipment(gp, ItemType.EQUIPMENT, "Fishing Rod", false, 0, 0));
        addItemToInventory(new OBJ_Misc(gp,ItemType.MISC, "Coal", false, 0, 0));
        addItemToInventory(new OBJ_Crop(gp, ItemType.CROP, "Grape", true,100, 10, 20, 3));
        addItemToInventory(new OBJ_Crop(gp, ItemType.CROP, "Grape", true,100, 10, 20, 3));


    }
    private void initializeRecipeStatus() {
        recipeUnlockStatus.clear();
        for (Recipe recipe : RecipeManager.getAllRecipes()) {
            recipeUnlockStatus.put(recipe.recipeId, "DEFAULT".equals(recipe.unlockMechanismKey));
        }
    }
    public void checkAndUnlockRecipes() {
        if (gp.gameState == gp.titleState) return; 

        for (Recipe recipe : RecipeManager.getAllRecipes()) {
            if (Boolean.FALSE.equals(recipeUnlockStatus.get(recipe.recipeId))) { 
                boolean unlocked = false;
                switch (recipe.unlockMechanismKey) {
                    case "FISH_COUNT_10":
                        if (totalFishCaught >= 10) unlocked = true;
                        break;
                    case "FISH_SPECIFIC_PUFFERFISH":
                        if (hasFishedPufferfish) unlocked = true;
                        break;
                    case "HARVEST_ANY_FIRST": 
                        if (!firstHarvestByName.isEmpty() && firstHarvestByName.containsValue(true)) unlocked = true;
                        break;
                    case "OBTAIN_HOT_PEPPER":
                        if (hasObtainedHotPepper) unlocked = true;
                        break;
                    case "FISH_SPECIFIC_LEGEND":
                        if (hasFishedLegend) unlocked = true;
                        break;
                    
                }

                if (unlocked) {
                    recipeUnlockStatus.put(recipe.recipeId, true);
                    if (gp.ui != null) { 
                        gp.ui.showMessage("New Recipe Unlocked: " + recipe.outputFoodName + "!");
                    }
                }
            }
        }
    }
    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public boolean tryDecreaseEnergy(int cost){
        if (cost <= 0) return true; 

        if (currentEnergy <= MIN_ENERGY_THRESHOLD) {
            gp.ui.showMessage("You're completely exhausted and can't do anything else!");
            
            
            if (!isCurrentlySleeping) {
                sleep("You tried to work while utterly exhausted and passed out again!");
            }
            return false; 
        }

        currentEnergy -= cost;
        gp.ui.showMessage("Energy -" + cost); 

        if (currentEnergy <= MIN_ENERGY_THRESHOLD) {
            currentEnergy = MIN_ENERGY_THRESHOLD; 
            sleep("You collapsed from sheer exhaustion!"); 
            return true; 
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

    
    public void sleep(String sleepMessagePrefix) {
        if (isCurrentlySleeping()) { 
            System.out.println("[Player] sleep() called while already isCurrentlySleeping. Duplicate call ignored.");
            return;
        }
        setCurrentlySleeping(true); 

        
        String energyRecoveryMessage;
        if (currentEnergy == 0) {
            currentEnergy = ENERGY_REFILL_AT_ZERO;
            energyRecoveryMessage = "You slept right on the brink and only recovered " + ENERGY_REFILL_AT_ZERO + " energy.";
        } else if (currentEnergy < (MAX_POSSIBLE_ENERGY * LOW_ENERGY_PENALTY_THRESHOLD_PERCENT / 100.0)) {
            
            currentEnergy = MAX_POSSIBLE_ENERGY / 2;
            energyRecoveryMessage = "You were deeply exhausted and \n only recovered half your energy.";
        } else {
            currentEnergy = MAX_POSSIBLE_ENERGY;
            energyRecoveryMessage = "You feel fully refreshed\nafter a good night's sleep!";
        }

        
        
        gp.ui.currentDialogue = sleepMessagePrefix + "\n" + energyRecoveryMessage;
        System.out.println("[Player] Sleeping. Message: " + gp.ui.currentDialogue); 
        teleportToPlayerHouse();

    }

    /**
 * Memindahkan player ke rumahnya di samping tempat tidur
 */
private void teleportToPlayerHouse() {
    int houseX = gp.tileSize * 7; 
    int houseY = gp.tileSize * 6; 

    if (gp.currentMapIndex != gp.PLAYER_HOUSE_INDEX) {
        gp.loadMapbyIndex(10);
    } 
    this.worldX = houseX;
    this.worldY = houseY;
    this.direction = "down"; 

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

        if (gp.keyH.inventoryPressed) {
            gp.keyH.inventoryPressed = false; 
            if (gp.gameState == gp.playState) {
                gp.gameState = gp.inventoryState; 
            } else if (gp.gameState == gp.inventoryState) {
                gp.gameState = gp.playState;
            }
        }

        if (gp.gameState == gp.playState) {
            updateActiveCookingProcesses();
            checkAndUnlockRecipes();
            
            if (this.keyH != null && this.keyH.eatPressed) {
                System.out.println("DEBUG: Player.update (playState) - eatPressed is true.");
                Entity equippedItem = getEquippedItem(); 
                if (equippedItem != null) {
                    System.out.println("DEBUG: Player.update - Equipped item: " + equippedItem.name + " | Class: " + equippedItem.getClass().getSimpleName());
                    if (equippedItem instanceof Edible) { 
                        System.out.println("DEBUG: Player.update - Equipped item IS Edible. Creating EatCommand.");
                        Command eatAction = new EatCommand(this, (Edible) equippedItem);
                        eatAction.execute(gp); 
                    } else {
                        if (gp.ui != null) gp.ui.showMessage(equippedItem.name + " is not edible.");
                        System.out.println("DEBUG: Player.update - Equipped item " + equippedItem.name + " is NOT Edible.");
                    }
                } else {
                    if (gp.ui != null) gp.ui.showMessage("Nothing held to eat.");
                    System.out.println("DEBUG: Player.update - No item equipped to eat.");
                }
                this.keyH.eatPressed = false; 
            }

            
            
            if (this.keyH != null && this.keyH.enterPressed && !(keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed)) {
                checkCollisionAndMove(); 
                
            }

        } else if (gp.gameState == gp.dialogueState || gp.gameState == gp.inventoryState) {
            
            spriteNum = 1;
            spriteCounter = 0;
        }
    }
    private void updateActiveCookingProcesses() {
        if (activeCookingProcesses.isEmpty() || gp.gameClock == null || gp.gameClock.isPaused()) {
            return;
        }

        Iterator<ActiveCookingProcess> iterator = activeCookingProcesses.iterator();
        spakborhills.Time currentTime = gp.gameClock.getTime();

        while (iterator.hasNext()) {
            ActiveCookingProcess process = iterator.next();
            boolean dayMatches = currentTime.getDay() == process.gameDayFinish;
            boolean timeIsDue = currentTime.getHour() > process.gameHourFinish ||
                    (currentTime.getHour() == process.gameHourFinish && currentTime.getMinute() >= process.gameMinuteFinish);
            boolean dayHasPassed = currentTime.getDay() > process.gameDayFinish;


            if (dayHasPassed || (dayMatches && timeIsDue)) {
                OBJ_Food cookedFood = FoodFactory.createFood(gp, process.foodNameToProduce);
                if (cookedFood != null) {
                    for (int i = 0; i < process.foodQuantityToProduce; i++) {
                        
                        addItemToInventory(FoodFactory.createFood(gp, process.foodNameToProduce));
                    }
                    gp.ui.showMessage(process.foodNameToProduce + " is ready!");
                    
                } else {
                    gp.ui.showMessage("Error creating " + process.foodNameToProduce + " after cooking.");
                }
                iterator.remove();
            }
        }
    }

    
    public void setPositionForMapEntry(int worldX, int worldY, String direction) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.direction = direction;
        System.out.println("[Player] Position set for map entry: X=" + this.worldX + ", Y=" + this.worldY + ", Dir=" + this.direction);
    }

    

    private void checkCollisionAndMove() {
        collisionON = false;
        gp.collisionChecker.checkTile(this); 

        
        
        
        
        
        
        int entityIndex = gp.collisionChecker.checkEntity(this, gp.entities);

        if (gp.keyH.enterPressed) {
            boolean interactionHandled = false; 

            
            if (entityIndex != 999) {
                Entity interactedEntity = gp.entities.get(entityIndex);

                if (interactedEntity instanceof NPC) {
                    NPC npc = (NPC) interactedEntity;
                    gp.gameState = gp.dialogueState;
                    gp.currentInteractingNPC = npc;
                    npc.openInteractionMenu();
                    interactionHandled = true;
                } else if (interactedEntity.type == EntityType.INTERACTIVE_OBJECT) {
                    interactedEntity.interact(); 
                    interactionHandled = true;
                }
                else if (interactedEntity.type == EntityType.PICKUP_ITEM) {
                    gp.ui.showMessage("Kamu mengambil " + interactedEntity.name + "!");
                    addItemToInventory(interactedEntity);
                    gp.entities.remove(entityIndex); 
                    interactionHandled = true;
                }
            }
            gp.keyH.enterPressed = false;
        }

        
        if (!collisionON) {
            if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
                switch (direction) {
                    case "up": worldY -= speed; break;
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
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
    public boolean addItemToInventory(Entity itemToAdd) { 
        if (!(itemToAdd instanceof OBJ_Item newItem)) {
            if (inventory.size() < 20) { 
                inventory.add(itemToAdd); 
                if (gp.ui != null) gp.ui.showMessage("Kamu mendapatkan: " + itemToAdd.name); 
                return true;
            } else {
                if (gp.ui != null) gp.ui.showMessage("Inventaris penuh!"); 
                return false;
            }
        }

        
        
        for (Entity existingEntity : inventory) { 
            if (existingEntity instanceof OBJ_Item existingItem) {
                
                if (newItem instanceof OBJ_Item) { 
                    OBJ_Item newItemAsObjItem = (OBJ_Item) newItem;
                    if (existingItem.name.equals(newItemAsObjItem.name) &&
                            existingItem.getType() == newItemAsObjItem.getType()) { 

                        existingItem.quantity += newItemAsObjItem.quantity; 
                        return true;
                    }
                }
            }
        }

        
        if (inventory.size() < 20) { 
            inventory.add(newItem); 
            if (gp.ui != null) gp.ui.showMessage("Kamu mendapatkan: " + newItem.name + (newItem.quantity > 1 ? " x" + newItem.quantity : "")); 
            return true;
        } else {
            if (gp.ui != null) gp.ui.showMessage("Inventaris penuh! Tidak dapat menambahkan " + newItem.name); 
            return false;
        }
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
            return; 
        }

        int itemIndex = gp.ui.inventoryCommandNum; 

        if (itemIndex >= 0 && itemIndex < inventory.size()) {
            Entity selectedItem = inventory.get(itemIndex);

            
            
            boolean itemWasConsumed = selectedItem.use(this);

            if (itemWasConsumed) {
                
                removeItemFromInventory(itemIndex); 
                
                
                if (gp.ui.inventoryCommandNum >= inventory.size() && !inventory.isEmpty()) {
                    gp.ui.inventoryCommandNum = inventory.size() - 1;
                } else if (inventory.isEmpty()) {
                    gp.ui.inventoryCommandNum = 0; 
                }
            }

        } else {
            gp.ui.showMessage("Tidak ada item yang dipilih.");
        }
    }


    public boolean buyItem(OBJ_Item itemToBuy, String recipeIdToUnlockIfApplicable) {
        if (itemToBuy == null) {
            gp.ui.showMessage("Item tidak valid.");
            return false;
        }

        int price = itemToBuy.getBuyPrice();
        if (gold >= price) {
            gold -= price;

            if (itemToBuy instanceof OBJ_Recipe) {
                
                if (recipeIdToUnlockIfApplicable != null
                        && recipeUnlockStatus.containsKey(recipeIdToUnlockIfApplicable)) {
                    recipeUnlockStatus.put(recipeIdToUnlockIfApplicable, true);
                    gp.ui.showMessage(
                            "Kamu membeli dan mempelajari resep: " + itemToBuy.name.replace("Recipe: ", "") + "!");
                } else {
                    gp.ui.showMessage("Kamu membeli resep: " + itemToBuy.name.replace("Recipe: ", "")
                            + ", tapi ada masalah saat mempelajarinya.");
                }
            } else {
                
                OBJ_Item purchasedItemInstance = null;
                if (itemToBuy instanceof OBJ_Seed) {
                    OBJ_Seed seedTemplate = (OBJ_Seed) itemToBuy;
                    purchasedItemInstance = new OBJ_Seed(gp, seedTemplate.getType(),
                            seedTemplate.name.replace(" seeds", ""), seedTemplate.isEdible(),
                            seedTemplate.getBuyPrice(), seedTemplate.getSellPrice(),
                            1, 1, Season.SPRING, Weather.SUNNY); 
                    purchasedItemInstance = itemToBuy; 

                } else if (itemToBuy instanceof OBJ_Food) {
                    OBJ_Food foodTemplate = (OBJ_Food) itemToBuy;
                    purchasedItemInstance = FoodFactory.createFood(gp, foodTemplate.name.replace(" food", "")); 
                }
                if (purchasedItemInstance != null) {
                    addItemToInventory(purchasedItemInstance); 
                    gp.ui.showMessage("Kamu membeli: " + purchasedItemInstance.name + " seharga " + price + "G.");
                } else {
                    OBJ_Item genericItem = new OBJ_Item(gp, itemToBuy.getType(),
                            itemToBuy.name.replace(" " + itemToBuy.getType().name().toLowerCase(), ""),
                            itemToBuy.isEdible(), itemToBuy.getBuyPrice(), itemToBuy.getSellPrice(),
                            itemToBuy.quantity);
                    addItemToInventory(genericItem);
                    gp.ui.showMessage("Kamu membeli: " + genericItem.name + " seharga " + price + "G.");
                }
            }
            return true;
        } else {
            gp.ui.showMessage("Gold tidak cukup untuk membeli " + itemToBuy.name + ".");
            return false;
        }
    }
    public void consumeItemFromInventory(Entity itemToConsume) {
        Entity equippedItemBeforeConsumption = getEquippedItem();
        boolean wasEquippedAndIsTheSameItem = (equippedItemBeforeConsumption == itemToConsume);

        boolean removed = this.inventory.remove(itemToConsume); 

        if (removed) {
            System.out.println("DEBUG: Consumed and removed from inventory: " + itemToConsume.name);
            if (wasEquippedAndIsTheSameItem) {
                this.currentEquippedItemIndex = -1; 
                System.out.println("DEBUG: Equipped item slot cleared because consumed item was equipped.");
            }
            
            if (gp.ui.inventoryCommandNum >= inventory.size() && !inventory.isEmpty()) {
                gp.ui.inventoryCommandNum = inventory.size() - 1;
            } else if (inventory.isEmpty()) {
                gp.ui.inventoryCommandNum = 0; 
            }
        } else {
            System.out.println("WARNING: Attempted to consume " + itemToConsume.name + " but it was not found/removed from inventory list.");
            
            
            if (wasEquippedAndIsTheSameItem) {
                this.currentEquippedItemIndex = -1;
            }
        }
    }
    public void pickUpObject(int i) { 
        if (i != 999) {
            Entity object = gp.entities.get(i);
            
            if (object.type == EntityType.PICKUP_ITEM ||
                    object.name.equals("Key") || 
                    object.name.equals("Boots")) { 

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
                
                
                
                equipped.use(this);
            }
        } else {
            this.currentEquippedItemIndex = -1; 
            
        }
    }
    public void startFishing() {    
        Season currentSeasonForFishing = gp.gameClock.getCurrentSeason(); 
        Weather currentWeatherForFishing = gp.gameClock.getCurrentWeather(); 
        int currentHourForFishing = gp.gameClock.getTime().getHour(); 
        String playerCurrentLocationString = getLocation(); 
        System.out.println("--------------------------------------------------");
        System.out.println("[PLAYER.startFishing()] Memulai proses memancing...");
        System.out.println("[PLAYER.startFishing()] Player.getLocation() (String): '" + playerCurrentLocationString + "'");
        System.out.println("[PLAYER.startFishing()] Musim saat ini: " + currentSeasonForFishing);
        System.out.println("[PLAYER.startFishing()] Cuaca saat ini: " + currentWeatherForFishing);
        System.out.println("[PLAYER.startFishing()] Jam saat ini: " + currentHourForFishing);
        System.out.println("--------------------------------------------------");
        if (this.playerIsActuallyFishing) { 
            System.out.println("[PLAYER.startFishing()] INFO: Pemain sudah dalam proses memancing. Permintaan baru diabaikan.");
            gp.ui.showMessage("Sedang memancing, tunggu proses selesai."); 
            return;
        }
        boolean isValidFishingLocation = (playerCurrentLocationString != null &&
                (playerCurrentLocationString.equals("Pond") 
                        || playerCurrentLocationString.equals("Mountain Lake") 
                        || playerCurrentLocationString.equals("Forest River") 
                        || playerCurrentLocationString.equals("Ocean"))); 
        System.out.println("[PLAYER.startFishing()] INFO: Validitas lokasi '" + playerCurrentLocationString + "' untuk memancing: " + isValidFishingLocation);
        if (!isValidFishingLocation) {
            System.out.println("[PLAYER.startFishing()] ERROR: Lokasi '" + playerCurrentLocationString + "' TIDAK VALID atau null untuk memancing!");
            gp.ui.showMessage("Kamu tidak berada di lokasi memancing yang valid!"); 
            return;
        }

        
        if (!tryDecreaseEnergy(5)) { 
            System.out.println("[PLAYER.startFishing()] INFO: Energi tidak cukup atau pemain pingsan. Memancing dibatalkan.");
            
            return; 
        }
        gp.gameClock.pauseTime(); 
        this.playerIsActuallyFishing = true; 
        gp.ui.showMessage("Mulai memancing..."); 

        
        List<OBJ_Fish> availableFish = new ArrayList<>(); 
        System.out.println("[PLAYER.startFishing()] INFO: Mencari ikan... Jumlah total entitas di gp.entities: " + gp.entities.size());

        for (Entity entity : gp.entities) { 
            if (entity instanceof OBJ_Fish) { 
                OBJ_Fish fish = (OBJ_Fish) entity;
                if (fish.isAvailable(currentSeasonForFishing, currentWeatherForFishing, currentHourForFishing, playerCurrentLocationString)) { 
                    availableFish.add(fish); 
                    System.out.println("    +++ [IKAN TERSEDIA]: " + fish.getFishName() 
                            + " (Lokasi Cocok: " + fish.getLocations().contains(playerCurrentLocationString) + ")"); 
                } else {
                    return;
                }
            }
        }
        System.out.println("--------------------------------------------------");
        System.out.println("[PLAYER.startFishing()] INFO: Jumlah ikan yang tersedia di list 'availableFish': " + availableFish.size());
        System.out.println("--------------------------------------------------");

        if (availableFish.isEmpty()) { 
            System.out.println("[PLAYER.startFishing()] INFO: Tidak ada ikan yang memenuhi syarat. Proses memancing dihentikan.");
            gp.ui.showMessage("Tidak ada ikan yang tersedia."); 
            this.playerIsActuallyFishing = false; 
            gp.gameClock.resumeTime(); 
            return;
        }

        
        Random rand = new Random(); 
        OBJ_Fish targetFish = availableFish.get(rand.nextInt(availableFish.size())); 
        System.out.println("[PLAYER.startFishing()] INFO: Ikan target untuk minigame: " + targetFish.getFishName());

        int guessRange, maxTry; 
        switch (targetFish.getFishType()) { 
            case REGULAR: guessRange = 100; maxTry = 10; break; 
            case LEGENDARY: guessRange = 500; maxTry = 7; break; 
            default: guessRange = 10; maxTry = 10; break; 
        }

        int answerNumber = rand.nextInt(guessRange) + 1; 
        System.out.println("[PLAYER.startFishing()] DEBUG MINIGAME: Angka rahasia ikan adalah = " + answerNumber); 

        boolean success = false; 
        String infoMsg = "Tebak angka untuk menangkap " + targetFish.getFishName() + 
                " (1-" + guessRange + "). Kamu punya " + maxTry + " percobaan."; 

        for (int attempt = 1; attempt <= maxTry; attempt++) { 
            String input = javax.swing.JOptionPane.showInputDialog( 
                    null, 
                    infoMsg + "\nPercobaan " + attempt + "/" + maxTry + "\nMasukkan angka:", 
                    "Mini Game Memancing", 
                    javax.swing.JOptionPane.QUESTION_MESSAGE 
            );
            if (input == null) { 
                gp.ui.showMessage("Batal menebak, gagal menangkap ikan."); 
                System.out.println("[PLAYER.startFishing()] INFO MINIGAME: Pemain membatalkan tebakan.");
                break; 
            }
            int guessedNumber; 
            try {
                guessedNumber = Integer.parseInt(input.trim()); 
                if (guessedNumber < 1 || guessedNumber > guessRange) { 
                    gp.ui.showMessage("Angka harus antara 1 dan " + guessRange + "!");
                    attempt--; continue; 
                }
            } catch (NumberFormatException e) {
                gp.ui.showMessage("Input tidak valid! Masukkan angka."); 
                attempt--; continue; 
            }
            if (guessedNumber == answerNumber) { 
                success = true; 
                System.out.println("[PLAYER.startFishing()] INFO MINIGAME: Tebakan benar!");
                break; 
            } else if (guessedNumber < answerNumber) { 
                infoMsg = "Terlalu kecil!\n"; 
            } else { 
                infoMsg = "Terlalu besar!\n"; 
            }
            infoMsg += "Tebak angka untuk menangkap " + targetFish.getFishName() + 
                    " (1-" + guessRange + "). Kamu punya " + (maxTry - attempt) + " percobaan lagi."; 
        }

        
        if (success) { 
            inventory.add(targetFish); 
            
            
            System.out.println("[PLAYER.startFishing()] SUKSES: Berhasil menangkap " + targetFish.getFishName() + "!");
        } else {
            gp.ui.showMessage("Gagal menangkap " + targetFish.getFishName() + "!"); 
            System.out.println("[PLAYER.startFishing()] GAGAL: Tidak berhasil menangkap " + targetFish.getFishName() + ".");
        }

        gp.gameClock.getTime().advanceTime(15);
        boolean isFishing = false;
        gp.gameClock.resumeTime();
    }
    
    private int calculateFishPrice(OBJ_Fish fish) {
        int nSeason = fish.getSeasons().size();
        int hourSpan = fish.getTotalAvailableHour(); 
        int nWeather = fish.getWeathers().size();
        int nLocation = fish.getLocations().size();
        int C = switch (fish.getFishType()) {
            case REGULAR -> 5;
            case LEGENDARY -> 25;
            default -> 10;
        };
        return 4 * nSeason * hourSpan * 2 * nWeather * 4 * nLocation * C / 32;
    }

}