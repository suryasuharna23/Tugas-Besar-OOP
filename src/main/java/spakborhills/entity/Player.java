package spakborhills.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import spakborhills.GamePanel;
import spakborhills.KeyHandler;
import spakborhills.Time;
import spakborhills.action.Command;
import spakborhills.action.EatCommand;
import spakborhills.cooking.ActiveCookingProcess;
import spakborhills.cooking.FoodRegistry;
import spakborhills.cooking.Recipe;
import spakborhills.cooking.RecipeManager;
import spakborhills.enums.EntityType;
import spakborhills.enums.Gender;
import spakborhills.enums.ItemType;
import spakborhills.enums.Location;
import spakborhills.enums.Season;
import spakborhills.enums.Weather;
import spakborhills.interfaces.Edible;
import spakborhills.interfaces.Observer;
import spakborhills.object.OBJ_Crop;
import spakborhills.object.OBJ_Equipment;
import spakborhills.object.OBJ_Fish;
import spakborhills.object.OBJ_Food;
import spakborhills.object.OBJ_Item;
import spakborhills.object.OBJ_Misc;
import spakborhills.object.OBJ_PlantedCrop;
import spakborhills.object.OBJ_Recipe;
import spakborhills.object.OBJ_Seed;

public class Player extends Entity implements Observer {
    KeyHandler keyH;
    public final int screenX;
    public final int screenY;
    private String farmName;
    private Gender gender = Gender.MALE;
    public int currentEnergy;

    public ArrayList<Entity> inventory = new ArrayList<>();
    public int currentEquippedItemIndex = -1;

    public int gold;
    public ArrayList<Entity> itemsInShippingBinToday = new ArrayList<>();
    public boolean hasUsedShippingBinToday = false;
    public int goldFromShipping = 0;
    public long totalHarvested = 0;
    public long totalIncome = 0;
    public long totalExpenditure = 0;
    public Map<Season, Long> seasonalIncome = new HashMap<>();
    public Map<Season, Long> seasonalExpenditure = new HashMap<>();
    public Map<Season, Integer> seasonPlayed = new HashMap<>();
    public Map<Season, Integer> countIncome = new HashMap<>();
    public Map<Season, Integer> countExpenditure = new HashMap<>();

    public Map<String, Integer> npcChatFrequency = new HashMap<>();
    public Map<String, Integer> npcGiftFrequency = new HashMap<>();
    public Map<String, Integer> npcVisitFrequency = new HashMap<>();

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
    public int MAX_SHIPPING_BIN_TYPES = 16;
    public static final int MIN_ENERGY_THRESHOLD = -20;
    public static final int LOW_ENERGY_PENALTY_THRESHOLD_PERCENT = 10;
    public static final int ENERGY_REFILL_AT_ZERO = 10;
    private boolean isCurrentlySleeping = false;

    private Location currentLocation;
    private boolean playerIsActuallyFishing = false;

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getLocationName() {
        return (this.currentLocation != null) ? this.currentLocation.name().replace("_", " ") : "Unknown";
    }

    public OBJ_Fish fishToCatchInMinigame;
    private int fishingAnswerNumber;
    private int fishingGuessRange;
    public int fishingMaxTry;
    public int fishingCurrentAttempts;
    public String fishingPlayerInput = "";
    public String fishingInfoMessage = "";
    public String fishingFeedbackMessage = "";

    public Player(GamePanel gp, KeyHandler keyH) {
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

    public void getPlayerImage() {
        String genderFolder = (gender == Gender.FEMALE) ? "female" : "male";
        up1 = setup("/player/" + genderFolder + "/"+ capitalize(genderFolder) + "_W1");
        up2 = setup("/player/" + genderFolder + "/"+ capitalize(genderFolder) + "_W2");
        down1 = setup("/player/" + genderFolder + "/"+ capitalize(genderFolder) + "_S1");
        down2 = setup("/player/" + genderFolder + "/"+ capitalize(genderFolder) + "_S2");
        left1 = setup("/player/" + genderFolder + "/"+ capitalize(genderFolder) + "_A1");
        left2 = setup("/player/" + genderFolder + "/"+ capitalize(genderFolder) + "_A2");
        right1 = setup("/player/" + genderFolder + "/"+ capitalize(genderFolder) + "_D1");
        right2 = setup("/player/" + genderFolder + "/"+ capitalize(genderFolder) + "_D2");
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public String getLocation() {
        if (currentLocation != null) {
            return currentLocation.name().replace("_", " ");
        }
        return "Unknown";
    }

    public void setLocation(String locationName) {
        try {
            String enumCompatibleName = locationName.toUpperCase().replace(" ", "_");
            this.currentLocation = Location.valueOf(enumCompatibleName);
        } catch (IllegalArgumentException e) {
            System.err
                    .println("Peringatan: Nama lokasi string tidak valid '" + locationName + "'. Lokasi tidak diubah.");
        }
    }

    public void setDefaultValues() {
        inventory.clear();
        this.playerIsActuallyFishing = false;
        this.fishingPlayerInput = "";
        this.fishingInfoMessage = "";
        this.fishingFeedbackMessage = "";
        worldX = gp.tileSize * 21;
        worldY = gp.tileSize * 26;
        speed = 4;
        direction = "down";
        type = EntityType.PLAYER;
        currentEnergy = MAX_POSSIBLE_ENERGY;
        this.isCurrentlySleeping = false;
        gold = 1500;
        initializeRecipeStatus();

        addItemToInventory(new OBJ_Equipment(gp, ItemType.EQUIPMENT, "Hoe", false, 0, 0));
        addItemToInventory(new OBJ_Equipment(gp, ItemType.EQUIPMENT, "Watering Can", false, 0, 0));
        addItemToInventory(new OBJ_Equipment(gp, ItemType.EQUIPMENT, "Pickaxe", false, 0, 0));
        addItemToInventory(new OBJ_Equipment(gp, ItemType.EQUIPMENT, "Fishing Rod", false, 0, 0));
        for (int i = 0; i < 15; i++) {
            addItemToInventory(new OBJ_Seed(gp, ItemType.SEEDS, "Parsnip", false, 20, 10, 1, 99, Season.SPRING,
                    Weather.RAINY));
        }

        itemsInShippingBinToday.clear();
        if (shippingBinTypes == null) {
            shippingBinTypes = new HashMap<>();
        }
        shippingBinTypes.clear();
        hasUsedShippingBinToday = false;
        goldFromShipping = 0;

    }

    private void initializeRecipeStatus() {
        recipeUnlockStatus.clear();
        for (Recipe recipe : RecipeManager.getAllRecipes()) {
            recipeUnlockStatus.put(recipe.recipeId, "DEFAULT".equals(recipe.unlockMechanismKey));
        }
    }

    public void checkAndUnlockRecipes() {
        if (gp.gameState == gp.titleState)
            return;

        for (Recipe recipe : RecipeManager.getAllRecipes()) {
            if (Boolean.FALSE.equals(recipeUnlockStatus.get(recipe.recipeId))) {
                boolean unlocked = false;
                switch (recipe.unlockMechanismKey) {
                    case "FISH_COUNT_10":
                        if (totalFishCaught >= 10)
                            unlocked = true;
                        break;
                    case "FISH_SPECIFIC_PUFFERFISH":
                        if (hasFishedPufferfish)
                            unlocked = true;
                        break;
                    case "HARVEST_ANY_FIRST":
                        if (!firstHarvestByName.isEmpty() && firstHarvestByName.containsValue(true))
                            unlocked = true;
                        break;
                    case "OBTAIN_HOT_PEPPER":
                        if (hasObtainedHotPepper)
                            unlocked = true;
                        break;
                    case "FISH_SPECIFIC_LEGEND":
                        if (hasFishedLegend)
                            unlocked = true;
                        break;

                }
                if (unlocked) {
                    recipeUnlockStatus.put(recipe.recipeId, true);
                    if (gp.ui != null) {
                        gp.ui.showMessage("Resep behasil didapat: " + recipe.outputFoodName + "!");
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

    public boolean tryDecreaseEnergy(int cost) {
        if (cost <= 0)
            return true;

        if (currentEnergy <= MIN_ENERGY_THRESHOLD) {
            gp.ui.showMessage("Kamu terlalu lelah, tidak bisa melakukan apapun!");
            if (!isCurrentlySleeping) {
                sleep("Kamu mencoba bekerja saat sedang lelah, kamu ketiduran lagi!");
            }
            return false;
        }
        currentEnergy -= cost;
        gp.ui.showMessage("Energy -" + cost);

        if (currentEnergy <= MIN_ENERGY_THRESHOLD) {
            currentEnergy = MIN_ENERGY_THRESHOLD;
            gp.ui.showMessage("Kamu ketiduran karena terlalu lelah!");
            sleep("Kamu ketiduran karena terlalu lelah!");
            return true;
        }
        return true;
    }

    public void increaseEnergy(int amount) {
        currentEnergy += amount;
        if (currentEnergy > MAX_POSSIBLE_ENERGY) {
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
        if (currentEnergy <= 0) {
            currentEnergy = ENERGY_REFILL_AT_ZERO;
            energyRecoveryMessage = "You slept right on the brink and only recovered " + ENERGY_REFILL_AT_ZERO
                    + " energy.";
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

    private void teleportToPlayerHouse() {
        int houseX = gp.tileSize * 7;
        int houseY = gp.tileSize * 6;

        if (gp.currentMapIndex != gp.PLAYER_HOUSE_INDEX) {
            System.out.println("[Player] Requesting teleport to Player House due to sleep/collapse");

            gp.shouldTeleportToPlayerHouse = true;
            gp.playerHouseTeleportX = houseX;
            gp.playerHouseTeleportY = houseY;
        } else {

            this.worldX = houseX;
            this.worldY = houseY;
        }
        this.direction = "down";
    }

    public boolean isMarried() {
        return married;
    }

    public void setMarried(boolean married) {
        this.married = married;
    }

    private void updateDirection() {
        if (keyH.upPressed) {
            direction = "up";
        } else if (keyH.downPressed) {
            direction = "down";
        } else if (keyH.leftPressed) {
            direction = "left";
        } else if (keyH.rightPressed) {
            direction = "right";
        }
    }

    public void update() {
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed || keyH.enterPressed) {
            if (!gp.keyH.inventoryPressed) {
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
                    System.out.println("DEBUG: Player.update - Equipped item: " + equippedItem.name + " | Class: "
                            + equippedItem.getClass().getSimpleName());
                    if (equippedItem instanceof Edible) {
                        System.out.println("DEBUG: Player.update - Equipped item IS Edible. Creating EatCommand.");
                        Command eatAction = new EatCommand(this, (Edible) equippedItem);
                        eatAction.execute(gp);
                    } else {
                        if (gp.ui != null)
                            gp.ui.showMessage(equippedItem.name + " tidak dedible.");
                    }
                } else {
                    if (gp.ui != null)
                        gp.ui.showMessage("Belum memegang makanan.");
                }
                this.keyH.eatPressed = false;
            }

            if (this.keyH != null && this.keyH.enterPressed
                    && !(keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed)) {
                checkCollisionAndMove();
            }
        } else if (gp.gameState == gp.dialogueState || gp.gameState == gp.inventoryState) {
            spriteNum = 1;
            spriteCounter = 0;
        }

        if (gp.gameClock != null) {
            
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
                    (currentTime.getHour() == process.gameHourFinish
                            && currentTime.getMinute() >= process.gameMinuteFinish);
            boolean dayHasPassed = currentTime.getDay() > process.gameDayFinish;

            if (dayHasPassed || (dayMatches && timeIsDue)) {
                OBJ_Food cookedFood = FoodRegistry.createFood(gp, process.foodNameToProduce);
                if (cookedFood != null) {
                    for (int i = 0; i < process.foodQuantityToProduce; i++) {

                        addItemToInventory(FoodRegistry.createFood(gp, process.foodNameToProduce));
                    }
                    gp.ui.showMessage(process.foodNameToProduce + " sudah siap!");
                } else {
                    gp.ui.showMessage("Error membuat " + process.foodNameToProduce + " setelah masak.");
                }
                iterator.remove();
            }
        }
    }

    private Entity findNearbyHarvestableEntity() {
        int checkDistance = gp.tileSize + 10;

        for (Entity entity : gp.entities) {
            if (entity instanceof OBJ_PlantedCrop) {

                int dx = Math.abs(entity.worldX - this.worldX);
                int dy = Math.abs(entity.worldY - this.worldY);

                if (dx <= checkDistance && dy <= checkDistance) {
                    return entity;
                }
            }
        }
        return null;
    }

    public void setPositionForMapEntry(int worldX, int worldY, String direction) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.direction = direction;
        System.out.println("[Player] Position set for map entry: X=" + this.worldX + ", Y=" + this.worldY + ", Dir="
                + this.direction);
    }

    private void checkCollisionAndMove() {
        collisionON = false;
        gp.collisionChecker.checkTile(this);
        int entityIndex = gp.collisionChecker.checkEntity(this, gp.entities);

        if (gp.keyH.enterPressed) {
            if (entityIndex != 999) {
                Entity interactedEntity = gp.entities.get(entityIndex);
                if (interactedEntity instanceof NPC) {
                    NPC npc = (NPC) interactedEntity;
                    gp.gameState = gp.dialogueState;
                    gp.currentInteractingNPC = npc;
                    npc.openInteractionMenu();
                } else if (interactedEntity.type == EntityType.INTERACTIVE_OBJECT) {
                    interactedEntity.interact();
                } else if (interactedEntity.type == EntityType.PICKUP_ITEM) {
                    gp.ui.showMessage("Kamu mengambil " + interactedEntity.name + "!");
                    addItemToInventory(interactedEntity);
                    gp.entities.remove(entityIndex);
                }
            }
            gp.keyH.enterPressed = false;
        }

        if (!collisionON) {
            if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
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
    }

    public void interactNPC(int i) {
        if (i != 999) {
            if (gp.keyH.enterPressed) {
                gp.gameState = gp.dialogueState;
                NPC npc = gp.npcs.get(i);
                if (npc.type == EntityType.NPC) {
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
                if (gp.ui != null)
                    gp.ui.showMessage("Kamu mendapatkan: " + itemToAdd.name);
                return true;
            } else {
                if (gp.ui != null)
                    gp.ui.showMessage("Inventory penuh!");
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
            if (gp.ui != null)
                gp.ui.showMessage(
                        "Kamu mendapatkan: " + newItem.name + (newItem.quantity > 1 ? " x" + newItem.quantity : ""));
            return true;
        }
        return false;
    }

    public void removeItemFromInventory(int itemIndex) {
        if (itemIndex >= 0 && itemIndex < inventory.size()) {
            inventory.remove(itemIndex);
        }
    }

    public Entity getEquippedItem() {
        if (currentEquippedItemIndex >= 0 && currentEquippedItemIndex < inventory.size()) {
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

            if (gp.gameClock != null) {
                totalExpenditure += price;
                Season currentSeason = gp.gameClock.getCurrentSeason();
                seasonalExpenditure.put(currentSeason, seasonalExpenditure.getOrDefault(currentSeason, 0L) + price);
                countExpenditure.put(currentSeason, countExpenditure.getOrDefault(currentSeason, 0) + 1);
            }

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
                            seedTemplate.getCountWater(), seedTemplate.getDayToHarvest(), seedTemplate.getSeason(), seedTemplate.getWeather());
                } else if (itemToBuy instanceof OBJ_Food) {
                    OBJ_Food foodTemplate = (OBJ_Food) itemToBuy;
                    purchasedItemInstance = FoodRegistry.createFood(gp, foodTemplate.name.replace(" food", ""));
                } else if (itemToBuy instanceof OBJ_Misc) {
                    OBJ_Misc miscTemplate = (OBJ_Misc) itemToBuy;
                    purchasedItemInstance = new OBJ_Misc(gp, miscTemplate.getType(),
                            miscTemplate.baseName, miscTemplate.isEdible(),
                            miscTemplate.getBuyPrice(), miscTemplate.getSellPrice());
                } else if (itemToBuy instanceof OBJ_Crop) {
                    OBJ_Crop cropTemplate = (OBJ_Crop) itemToBuy;
                    purchasedItemInstance = new OBJ_Crop(gp, cropTemplate.getType(),
                            cropTemplate.baseName, true,
                            cropTemplate.getBuyPrice(), cropTemplate.getSellPrice(),
                            cropTemplate.getHarvestAmount(), cropTemplate.getEnergy());
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

        if (itemToConsume instanceof OBJ_Item) {
            OBJ_Item objItem = (OBJ_Item) itemToConsume;
            if (objItem.quantity > 1) {
                objItem.quantity--;
                System.out.println(
                        "DEBUG: Consumed 1 " + itemToConsume.name + ". Remaining quantity: " + objItem.quantity);
                return;
            }
        }

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
            System.out.println("WARNING: Attempted to consume " + itemToConsume.name
                    + " but it was not found/removed from inventory list.");

            if (wasEquippedAndIsTheSameItem) {
                this.currentEquippedItemIndex = -1;
            }
        }
    }

    public String getActiveCookingInfo() {
        if (activeCookingProcesses.isEmpty()) {
            return null;
        }

        ActiveCookingProcess process = activeCookingProcesses.get(0);
        spakborhills.Time currentTime = gp.gameClock.getTime();

        int timeRemainingMinutes = 0;
        if (currentTime.getDay() == process.gameDayFinish) {
            int currentTotalMinutes = currentTime.getHour() * 60 + currentTime.getMinute();
            int finishTotalMinutes = process.gameHourFinish * 60 + process.gameMinuteFinish;
            timeRemainingMinutes = finishTotalMinutes - currentTotalMinutes;
        } else if (currentTime.getDay() < process.gameDayFinish) {

            int remainingTodayMinutes = (24 - currentTime.getHour()) * 60 - currentTime.getMinute();
            int tomorrowMinutes = process.gameHourFinish * 60 + process.gameMinuteFinish;
            timeRemainingMinutes = remainingTodayMinutes + tomorrowMinutes;
        }

        if (timeRemainingMinutes <= 0) {
            return process.foodNameToProduce + " sudah siap!";
        }

        int hoursRemaining = timeRemainingMinutes / 60;
        int minutesRemaining = timeRemainingMinutes % 60;

        return "Sedang memasak: " + process.foodNameToProduce +
                " (selesai dalam " + hoursRemaining + "h " + minutesRemaining + "m)";
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
        BufferedImage image = null;
        switch (direction) {
            case "up":
                if (spriteNum == 1) {
                    image = up1;
                }
                if (spriteNum == 2) {
                    image = up2;
                }
                break;
            case "down":
                if (spriteNum == 1) {
                    image = down1;
                }
                if (spriteNum == 2) {
                    image = down2;
                }
                break;
            case "left":
                if (spriteNum == 1) {
                    image = left1;
                }
                if (spriteNum == 2) {
                    image = left2;
                }
                break;
            case "right":
                if (spriteNum == 1) {
                    image = right1;
                }
                if (spriteNum == 2) {
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

    private List<Integer> getFishableWaterTileIdsForMap(String mapName) {
        List<Integer> waterTileIds = new ArrayList<>();
        if ("Mountain Lake".equalsIgnoreCase(mapName)) {

            waterTileIds.addAll(Arrays.asList(
                    932, 905, 528, 948, 951,
                    830, 831, 203, 366,
                    301, 302, 303, 304, 305,
                    333, 334, 335,
                    0, 76, 590, 232, 301, 239,914, 974, 972, 923, 913, 940, 945, 913, 914, 915, 931, 882, 845, 846, 847, 848, 850, 931, 931, 877, 878, 879, 880, 931, 882, 883

            ));
        } else if ("Forest River".equalsIgnoreCase(mapName)) {
            waterTileIds.addAll(Arrays.asList(
                    430, 816, 252, 410, 388, 389, 293, 840, 841, 833, 706, 752,
                    783, 867, 969, 992, 553, 209, 210, 212, 327, 354, 221, 222,
                    306, 333, 335, 339, 351, 28, 30, 31, 0, 733, 671, 417, 449, 126, 449, 481, 575, 413, 513, 449, 417, 385, 321, 639, 257));
        } else if ("Ocean".equalsIgnoreCase(mapName)) {
            waterTileIds.addAll(Arrays.asList(
                    143, 992, 32, 58, 96, 235, 267, 171, 203, 331, 504, 547, 548, 565, 549, 575, 551, 552, 572, 573,
                    570, 553, 554, 574, 314, 61, 93, 330, 221, 256));
        } else if ("Pond".equalsIgnoreCase(mapName)) {

        }

        return waterTileIds;
    }

    public void startFishing() {
        if (!isHoldingTool("Fishing Rod")) {
            gp.ui.showMessage("Kamu harus memegang Fishing Rod untuk memancing.");
            System.out.println("[DEBUG Player.startFishing] ERROR: Pemain tidak memegang Fishing Rod. Proses dihentikan.");
            return;
        }

        System.out.println("\n[DEBUG Player.startFishing] === MEMULAI PROSES MEMANCING ===");

        System.out.println("[DEBUG Player.startFishing] Mengecek apakah sudah dalam proses memancing...");
        if (this.playerIsActuallyFishing) {
            System.out.println(
                    "[DEBUG Player.startFishing] INFO: Pemain sudah dalam proses memancing. Permintaan baru diabaikan.");
            gp.ui.showMessage("Sedang memancing, tunggu proses selesai.");
            return;
        }
        System.out.println("[DEBUG Player.startFishing] Belum dalam proses memancing, melanjutkan.");

        System.out.println("[DEBUG Player.startFishing] Mengecek kepemilikan Fishing Rod...");
        boolean hasFishingRod = false;
        for (Entity item : inventory) {
            if (item instanceof OBJ_Equipment && item.name != null && item.name.startsWith("Fishing Rod")) {
                hasFishingRod = true;
                System.out.println("[DEBUG Player.startFishing] Fishing Rod ditemukan di inventaris.");
                break;
            }
        }
        if (!hasFishingRod) {
            gp.ui.showMessage("Kamu membutuhkan Fishing Rod untuk memancing!");
            System.out.println(
                    "[DEBUG Player.startFishing] ERROR: Pemain tidak memiliki Fishing Rod. Proses dihentikan.");
            return;
        }

        String playerCurrentLocationString = getLocation();
        System.out.println(
                "[DEBUG Player.startFishing] Lokasi pemain saat ini (String): '" + playerCurrentLocationString + "'");

        System.out.println("[DEBUG Player.startFishing] Menentukan tile di depan pemain...");
        int tileInFrontX = worldX;
        int tileInFrontY = worldY;
        int playerSolidAreaCenterX = solidArea.x + solidArea.width / 2;
        int playerSolidAreaCenterY = solidArea.y + solidArea.height / 2;
        int interactionReach = gp.tileSize / 2 + 1;

        switch (direction) {
            case "up":
                tileInFrontY = worldY + playerSolidAreaCenterY - interactionReach;
                tileInFrontX = worldX + playerSolidAreaCenterX;
                break;
            case "down":
                tileInFrontY = worldY + playerSolidAreaCenterY + interactionReach;

                tileInFrontX = worldX + playerSolidAreaCenterX;
                break;
            case "left":
                tileInFrontX = worldX + playerSolidAreaCenterX - interactionReach;
                tileInFrontY = worldY + playerSolidAreaCenterY;
                break;
            case "right":
                tileInFrontX = worldX + playerSolidAreaCenterX + interactionReach;

                tileInFrontY = worldY + playerSolidAreaCenterY;
                break;
        }
        System.out.println("[DEBUG Player.startFishing] Koordinat mentah tile di depan: X=" + tileInFrontX + ", Y="
                + tileInFrontY);

        int tileInFrontCol = tileInFrontX / gp.tileSize;
        int tileInFrontRow = tileInFrontY / gp.tileSize;
        System.out.println("[DEBUG Player.startFishing] Kolom tile di depan: " + tileInFrontCol
                + ", Baris tile di depan: " + tileInFrontRow);

        if (tileInFrontCol < 0 || tileInFrontCol >= gp.maxWorldCol ||
                tileInFrontRow < 0 || tileInFrontRow >= gp.maxWorldRow) {
            gp.ui.showMessage("Kamu tidak menghadap ke area yang valid di peta.");
            System.out.println("[DEBUG Player.startFishing] ERROR: Tile di depan pemain (" + tileInFrontCol + ","
                    + tileInFrontRow + ") di luar batas peta (" + gp.maxWorldCol + "," + gp.maxWorldRow
                    + "). Proses dihentikan.");
            return;
        }

        int tileNumInFront = gp.tileManager.mapTileNum[tileInFrontCol][tileInFrontRow];
        System.out.println("[DEBUG Player.startFishing] Nomor Tile di depan pemain: " + tileNumInFront);

        boolean isTileInFrontFishableWater = false;
        List<Integer> fishableWaterIds = getFishableWaterTileIdsForMap(playerCurrentLocationString);
        System.out.println("[DEBUG Player.startFishing] Daftar ID tile air untuk lokasi '" + playerCurrentLocationString
                + "': " + fishableWaterIds);

        if (tileNumInFront >= 0 && tileNumInFront < gp.tileManager.tile.length
                && gp.tileManager.tile[tileNumInFront] != null) {

            if (gp.tileManager.tile[tileNumInFront].collision && fishableWaterIds.contains(tileNumInFront)) {
                isTileInFrontFishableWater = true;
                System.out.println("[DEBUG Player.startFishing] Tile di depan (" + tileNumInFront
                        + ") adalah air yang bisa dipancing (collision=true dan ada di daftar ID).");
            } else {
                System.out.println("[DEBUG Player.startFishing] Tile di depan (" + tileNumInFront
                        + ") BUKAN air yang bisa dipancing. Collision: " + gp.tileManager.tile[tileNumInFront].collision
                        + ", Ada di daftar ID: " + fishableWaterIds.contains(tileNumInFront));
            }
        } else {
            System.out.println("[DEBUG Player.startFishing] Tile di depan (" + tileNumInFront
                    + ") tidak valid (null atau di luar batas array tile).");
        }

        if (!isTileInFrontFishableWater) {
            gp.ui.showMessage("Kamu harus menghadap air untuk memancing!");
            System.out.println(
                    "[DEBUG Player.startFishing] INFO: Pemain tidak menghadap tile air yang bisa dipancing. TileNum: "
                            + tileNumInFront + " di Peta: " + playerCurrentLocationString + ". Proses dihentikan.");
            return;
        }
        System.out.println("[DEBUG Player.startFishing] INFO: Pemain menghadap tile air yang valid (TileNum: "
                + tileNumInFront + ", Col: " + tileInFrontCol + ", Row: " + tileInFrontRow + ").");

        Season currentSeasonForFishing = gp.gameClock.getCurrentSeason();
        Weather currentWeatherForFishing = gp.gameClock.getCurrentWeather();
        int currentHourForFishing = gp.gameClock.getTime().getHour();

        System.out.println("--------------------------------------------------");
        System.out.println("[DEBUG Player.startFishing] Memulai proses memancing aktual...");
        System.out.println("[DEBUG Player.startFishing] Lokasi Pemain (String): '" + playerCurrentLocationString + "'");
        System.out.println("[DEBUG Player.startFishing] Musim saat ini: " + currentSeasonForFishing);
        System.out.println("[DEBUG Player.startFishing] Cuaca saat ini: " + currentWeatherForFishing);
        System.out.println("[DEBUG Player.startFishing] Jam saat ini: " + currentHourForFishing);
        System.out.println("--------------------------------------------------");

        boolean isValidFishingMap = (playerCurrentLocationString != null &&
                (playerCurrentLocationString.equalsIgnoreCase("Pond")
                        || playerCurrentLocationString.equalsIgnoreCase("Mountain Lake")
                        || playerCurrentLocationString.equalsIgnoreCase("Forest River")
                        || playerCurrentLocationString.equalsIgnoreCase("Ocean")));

        System.out.println("[DEBUG Player.startFishing] Validitas lokasi peta '" + playerCurrentLocationString
                + "' untuk memancing: " + isValidFishingMap);
        if (!isValidFishingMap) {
            System.out.println("[DEBUG Player.startFishing] ERROR: Peta '" + playerCurrentLocationString
                    + "' bukan lokasi memancing yang valid! Proses dihentikan.");
            gp.ui.showMessage("Kamu tidak bisa memancing di sini!");
            return;
        }

        System.out.println("[DEBUG Player.startFishing] Mengecek energi pemain...");
        if (!tryDecreaseEnergy(5)) {
            System.out.println("[DEBUG Player.startFishing] INFO: Energi tidak cukup (" + currentEnergy
                    + ") atau pemain pingsan. Memancing dibatalkan.");
            return;
        }
        System.out.println("[DEBUG Player.startFishing] Energi cukup (" + currentEnergy + "). Melanjutkan.");

        System.out.println("[DEBUG Player.startFishing] Menjeda GameClock.");
        gp.gameClock.pauseTime();
        this.playerIsActuallyFishing = true;
        System.out.println("[DEBUG Player.startFishing] playerIsActuallyFishing diatur ke true.");

        List<OBJ_Fish> availableFish = new ArrayList<>();
        System.out.println("[DEBUG Player.startFishing] INFO: Mencari ikan... Jumlah total entitas di gp.entities: "
                + gp.entities.size());
        for (Entity entity : gp.entities) {
            if (entity instanceof OBJ_Fish) {
                OBJ_Fish fish = (OBJ_Fish) entity;
                boolean isAvailableNow = fish.isAvailable(currentSeasonForFishing, currentWeatherForFishing,
                        currentHourForFishing, playerCurrentLocationString);
                System.out.println("    [DEBUG Player.startFishing] Mengecek ikan: " + fish.getFishName()
                        + " | Lokasi: " + fish.getLocations() + " | Musim: " + fish.getSeasons() + " | Cuaca: "
                        + fish.getWeathers() + " | Jam: " + fish.getStartHour() + "-" + fish.getEndHour()
                        + " | Tersedia: " + isAvailableNow);
                if (isAvailableNow) {
                    availableFish.add(fish);
                    System.out.println("        +++ [IKAN TERSEDIA DITAMBAHKAN]: " + fish.getFishName()
                            + " (Lokasi Cocok: " + fish.getLocations().contains(playerCurrentLocationString) + ")");
                }
            }
        }
        System.out.println("--------------------------------------------------");
        System.out.println("[DEBUG Player.startFishing] INFO: Jumlah ikan yang tersedia di list 'availableFish': "
                + availableFish.size());
        System.out.println("--------------------------------------------------");

        if (availableFish.isEmpty()) {
            System.out.println(
                    "[DEBUG Player.startFishing] INFO: Tidak ada ikan yang memenuhi syarat. Proses memancing dihentikan.");
            gp.ui.showMessage("Tidak ada ikan yang tersedia saat ini.");
            this.playerIsActuallyFishing = false;
            System.out.println(
                    "[DEBUG Player.startFishing] playerIsActuallyFishing diatur ke false karena tidak ada ikan.");
            System.out.println("[DEBUG Player.startFishing] Melanjutkan GameClock karena tidak ada ikan.");
            gp.gameClock.resumeTime();
            return;
        }

        Random rand = new Random();
        this.fishToCatchInMinigame = availableFish.get(rand.nextInt(availableFish.size()));
        System.out.println("[PLAYER.startFishing()] INFO: Ikan target untuk minigame: "
                + this.fishToCatchInMinigame.getFishName());

        switch (this.fishToCatchInMinigame.getFishType()) {
            case REGULAR:
                this.fishingGuessRange = 100;
                this.fishingMaxTry = 10;
                break;
            case LEGENDARY:
                this.fishingGuessRange = 500;
                this.fishingMaxTry = 7;
                break;
            default:
                this.fishingGuessRange = 10;
                this.fishingMaxTry = 10;
                break;
        }
        System.out.println("[DEBUG Player.startFishing] Pengaturan Minigame: Range Tebakan=" + this.fishingGuessRange
                + ", Max Percobaan=" + this.fishingMaxTry);

        this.fishingAnswerNumber = rand.nextInt(this.fishingGuessRange) + 1;
        System.out.println("[DEBUG Player.startFishing] ***** ANGKA RAHASIA IKAN (UNTUK DEBUG): "
                + this.fishingAnswerNumber + " *****");

        this.fishingCurrentAttempts = 0;
        this.fishingPlayerInput = "";
        this.fishingInfoMessage = "Tebak angka (1-" + this.fishingGuessRange + ") untuk menangkap "
                + this.fishToCatchInMinigame.getFishName();
        this.fishingFeedbackMessage = "";
        System.out.println("[DEBUG Player.startFishing] Pesan Info Minigame: " + this.fishingInfoMessage);

        gp.gameState = gp.fishingMinigameState;
        System.out.println("[DEBUG Player.startFishing] INFO: Mengubah GameState ke fishingMinigameState ("
                + gp.fishingMinigameState + "). GUI Minigame seharusnya muncul.");
        System.out.println(
                "[DEBUG Player.startFishing] === PROSES MEMANCING DIMULAI DENGAN SUKSES (MENUNGGU INPUT MINIGAME) ===\n");
    }

    public void processFishingAttempt(int guessedNumber) {
        System.out.println("\n[DEBUG Player.processFishingAttempt] === MEMPROSES TEBAKAN IKAN ===");
        System.out.println("[DEBUG Player.processFishingAttempt] Tebakan pemain: " + guessedNumber);
        if (!this.playerIsActuallyFishing || this.fishToCatchInMinigame == null) {
            System.out.println(
                    "[DEBUG Player.processFishingAttempt] ERROR: Tidak dalam mode memancing atau tidak ada ikan target. Mengakhiri minigame (gagal).");
            endFishingMinigame(false);
            return;
        }
        this.fishingCurrentAttempts++;
        System.out.println("[DEBUG Player.processFishingAttempt] Percobaan ke-" + this.fishingCurrentAttempts + " dari "
                + this.fishingMaxTry);
        boolean correctGuess = (guessedNumber == this.fishingAnswerNumber);

        if (correctGuess) {
            this.fishingFeedbackMessage = "Tangkapan Sukses!";
            System.out.println("[DEBUG Player.processFishingAttempt] INFO MINIGAME: Tebakan BENAR! (" + guessedNumber
                    + " == " + this.fishingAnswerNumber + ")");
            endFishingMinigame(true);
        } else {
            System.out.println("[DEBUG Player.processFishingAttempt] INFO MINIGAME: Tebakan SALAH. (" + guessedNumber
                    + " != " + this.fishingAnswerNumber + ")");
            if (this.fishingCurrentAttempts >= this.fishingMaxTry) {
                this.fishingFeedbackMessage = "Gagal! Kesempatan habis.";
                System.out.println(
                        "[DEBUG Player.processFishingAttempt] INFO MINIGAME: Kesempatan habis, gagal menangkap.");
                endFishingMinigame(false);
            } else {
                if (guessedNumber < this.fishingAnswerNumber) {
                    this.fishingFeedbackMessage = "Terlalu kecil!";
                    System.out.println("[DEBUG Player.processFishingAttempt] Feedback: Terlalu kecil.");
                } else {
                    this.fishingFeedbackMessage = "Terlalu besar!";
                    System.out.println("[DEBUG Player.processFishingAttempt] Feedback: Terlalu besar.");
                }
                System.out.println("[DEBUG Player.processFishingAttempt] Minigame berlanjut.");
            }
        }
    }

    public void endFishingMinigame(boolean success) {
        System.out.println("\n[DEBUG Player.endFishingMinigame] === MENGAKHIRI MINIGAME MEMANCING ===");
        System.out.println("[DEBUG Player.endFishingMinigame] Status Sukses: " + success);
        if (this.fishToCatchInMinigame != null) {
            System.out.println("[DEBUG Player.endFishingMinigame] Ikan target adalah: "
                    + this.fishToCatchInMinigame.getFishName());
            if (success) {
                OBJ_Fish caughtFishInstance = new OBJ_Fish(gp, ItemType.FISH,
                        this.fishToCatchInMinigame.getFishName(),
                        true,
                        this.fishToCatchInMinigame.getSellPrice(),
                        this.fishToCatchInMinigame.getSeasons(),
                        this.fishToCatchInMinigame.getWeathers(),
                        this.fishToCatchInMinigame.getLocations(),
                        this.fishToCatchInMinigame.getFishType(),
                        this.fishToCatchInMinigame.getStartHour(),
                        this.fishToCatchInMinigame.getEndHour());
                addItemToInventory(caughtFishInstance);
                gp.ui.showMessage("Berhasil menangkap " + this.fishToCatchInMinigame.getFishName() + "!");
                System.out
                        .println("[DEBUG Player.endFishingMinigame] Berhasil menangkap dan menambahkan ke inventaris: "
                                + this.fishToCatchInMinigame.getFishName());

                totalFishCaught++;
                switch (this.fishToCatchInMinigame.getFishType()) {
                    case COMMON:
                        totalCommonFishCaught++;
                        break;
                    case REGULAR:
                        totalRegularFishCaught++;
                        break;
                    case LEGENDARY:
                        totalLegendaryFishCaught++;
                        break;
                }
                System.out.println("[DEBUG Player.endFishingMinigame] Statistik Ikan: Total=" + totalFishCaught
                        + ", Common=" + totalCommonFishCaught + ", Regular=" + totalRegularFishCaught + ", Legendary="
                        + totalLegendaryFishCaught);
                if ("Pufferfish".equals(this.fishToCatchInMinigame.getFishName())) {
                    hasFishedPufferfish = true;
                    System.out.println(
                            "[DEBUG Player.endFishingMinigame] Pufferfish berhasil dipancing, hasFishedPufferfish=true");
                }
                if ("Legend".equals(this.fishToCatchInMinigame.getFishName())) {
                    hasFishedLegend = true;
                    System.out.println(
                            "[DEBUG Player.endFishingMinigame] Legend berhasil dipancing, hasFishedLegend=true");
                }

            } else {
                gp.ui.showMessage("Gagal menangkap " + this.fishToCatchInMinigame.getFishName() + "!");
                System.out.println("[DEBUG Player.endFishingMinigame] Gagal menangkap ikan.");
            }
        } else if (!success) {
            gp.ui.showMessage("Memancing dibatalkan.");
            System.out.println(
                    "[DEBUG Player.endFishingMinigame] Memancing dibatalkan (tidak ada ikan target saat gagal).");
        }

        gp.gameClock.getTime().advanceTime(15);
        this.playerIsActuallyFishing = false;
        gp.gameClock.resumeTime();

        this.fishToCatchInMinigame = null;
        this.fishingPlayerInput = "";
        this.fishingInfoMessage = "";
        this.fishingFeedbackMessage = "";
        this.fishingCurrentAttempts = 0;

        gp.gameState = gp.playState;
        System.out.println("[DEBUG Player.endFishingMinigame] GameState kembali ke PlayState (" + gp.playState + ").");
        System.out.println("[DEBUG Player.endFishingMinigame] === MINIGAME MEMANCING SELESAI ===\n");
    }

    public void addToInventory(Entity item) {
        this.inventory.add(item);
    }

    public boolean isHoldingTool(String keyword) {
        Entity equipped = getEquippedItem();
        return (equipped instanceof OBJ_Equipment) &&
                ((OBJ_Equipment) equipped).getName().toLowerCase().contains(keyword.toLowerCase());
    }

    public void update(Object event) {
        if (event instanceof String) {
            String eventString = (String) event;
            switch (eventString) {
                case "hour_change":
                case "day_change":

                    if (gp.gameClock != null && gp.gameClock.getTime() != null) {
                        Time currentTime = gp.gameClock.getTime();
                        if (currentTime.getHour() == 2 && !this.isCurrentlySleeping() && !gp.hasForcedSleepAt2AMToday) {
                            gp.ui.currentDialogue = "It's 2 AM! You pass out \n from staying up too late.";
                            sleep("You tried to work while utterly exhausted and passed out again!");
                            gp.hasForcedSleepAt2AMToday = true;
                            gp.gameState = gp.sleepTransitionState;
                            if (gp.gameClock != null && !gp.gameClock.isPaused()) {
                                gp.gameClock.pauseTime();
                            }
                        }
                    }
                    checkAndUnlockRecipes();
                    break;
                case "time_tick":

                    break;
                default:
                    System.out.println("[Player] Unhandled time event: " + eventString);
                    break;
            }
        } else if (event instanceof Weather newWeather) {

            if (!newWeather.equals(gp.weather.getCurrentWeather())) {
                if (newWeather == Weather.RAINY) {
                    System.out.println("Hujan Turun!");
                } else {
                    System.out.println("Cuaca Cerah!");
                    speed = 4;
                }

            }
        } else {
            System.out.println("[Player] Unhandled event type: " + event.getClass().getSimpleName());
        }
    }

    public void incrementChatFrequency(String name) {
        if (name != null && !name.trim().isEmpty()) {
            int currentChat  = npcChatFrequency.getOrDefault(name, 0);
            npcChatFrequency.put(name, currentChat+1);
             System.out.println("[Player] Chat count for " + name + ": " + npcChatFrequency.get(name));
        }
    }

    public void incrementGiftFrequency(String name) {
        if (name != null && !name.trim().isEmpty()) {
            int currentGift = npcGiftFrequency.getOrDefault(name, 0);
            npcGiftFrequency.put(name, currentGift+1);
            System.out.println("[Player] Gift count for " + name + ": " + npcGiftFrequency.get(name));

        }
    }

    public void incrementVisitFrequency(String name) {
        if (name != null && !name.trim().isEmpty()) {
            int currentVisit = npcVisitFrequency.getOrDefault(name, 0);
            npcVisitFrequency.put(name, currentVisit+1);
            System.out.println("[Player] Visit count for " + name + ": " + npcVisitFrequency.get(name));

        }
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public OBJ_Item createShippingBinItem(OBJ_Item original) {
        OBJ_Item newItem = null;
        
        if (original instanceof spakborhills.object.OBJ_Crop) {
            spakborhills.object.OBJ_Crop originalCrop = (spakborhills.object.OBJ_Crop) original;
            newItem = new spakborhills.object.OBJ_Crop(gp, originalCrop.getType(), 
                                                originalCrop.baseName, originalCrop.isEdible(),
                                                originalCrop.getBuyPrice(), originalCrop.getSellPrice(),
                                                originalCrop.getHarvestAmount(), originalCrop.getEnergy());
        } else if (original instanceof spakborhills.object.OBJ_Fish) {
            spakborhills.object.OBJ_Fish originalFish = (spakborhills.object.OBJ_Fish) original;
            newItem = new spakborhills.object.OBJ_Fish(gp, originalFish.getType(), originalFish.getFishName(),
                                                originalFish.isEdible(), originalFish.getSellPrice(),
                                                originalFish.getSeasons(), originalFish.getWeathers(),
                                                originalFish.getLocations(), originalFish.getFishType(),
                                                originalFish.getStartHour(), originalFish.getEndHour());
        } else {
            
            newItem = new OBJ_Item(gp, original.getType(), original.name, original.isEdible(),
                            original.getBuyPrice(), original.getSellPrice(), 1); 
        }
        
        
        if (newItem != null) {
            newItem.quantity = original.quantity;
        }
        
        return newItem;
    }

    public boolean addItemToShippingBin(OBJ_Item itemToAdd) {
        if (itemToAdd == null) {
            gp.ui.showMessage("Invalid item!");
            return false;
        }
        
        String itemKey = itemToAdd.name; 
        
        
        if (shippingBinTypes.containsKey(itemKey)) {
            
            OBJ_Item existingItem = shippingBinTypes.get(itemKey);
            existingItem.quantity += itemToAdd.quantity;
            
            
            for (Entity entity : itemsInShippingBinToday) {
                if (entity instanceof OBJ_Item && ((OBJ_Item) entity).name.equals(itemKey)) {
                    ((OBJ_Item) entity).quantity = existingItem.quantity;
                    break;
                }
            }
            
            gp.ui.showMessage("Menambahkan " + itemToAdd.quantity + " " + itemToAdd.name + " ke shipping bin!");
            System.out.println("[Player] Added to existing type: " + itemKey + ", New quantity: " + existingItem.quantity);
            return true;
            
        } else {
            
            if (shippingBinTypes.size() >= MAX_SHIPPING_BIN_TYPES) { 
                gp.ui.showMessage("Shipping bin penuh! Maximum " + MAX_SHIPPING_BIN_TYPES + " jenis item.");
                return false;
            }
            
            
            OBJ_Item newShippingItem = createShippingBinItem(itemToAdd);
            shippingBinTypes.put(itemKey, newShippingItem);
            itemsInShippingBinToday.add(newShippingItem); 
            
            gp.ui.showMessage("Menambahkan " + itemToAdd.name + " ke shipping bin! (" + 
                            shippingBinTypes.size() + "/" + MAX_SHIPPING_BIN_TYPES + " jenis)"); 
            System.out.println("[Player] Added new type: " + itemKey + ", Types in bin: " + shippingBinTypes.size());
            return true;
        }
    }

    public void clearShippingBin() {
        shippingBinTypes.clear();
        itemsInShippingBinToday.clear();
        hasUsedShippingBinToday = false;
        System.out.println("[Player] Shipping bin cleared for new day");
    }
}