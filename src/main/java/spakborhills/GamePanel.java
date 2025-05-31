package spakborhills;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

import javax.swing.JPanel;

import spakborhills.Tile.TileManager;
import spakborhills.cooking.Recipe;
import spakborhills.entity.Entity;
import spakborhills.entity.NPC;
import spakborhills.entity.Player;
import spakborhills.enums.Season;
import spakborhills.environment.EnvironmentManager;
import spakborhills.object.OBJ_Item;
import spakborhills.object.OBJ_PlantedCrop;

public class GamePanel extends JPanel implements Runnable {
    final int originalTileSize = 16;
    final int scale = 3;
    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    private FarmLayoutGenerator layoutGenerator;
    private FarmLayoutGenerator.FarmLayout currentRandomLayout;

    public int maxWorldCol = 50;
    public int maxWorldRow = 50;

    final int fps = 60;

    EnvironmentManager environmentManager = new EnvironmentManager(this);
    public KeyHandler keyH = new KeyHandler(this);
    Sound music = new Sound();
    Sound se = new Sound();
    public TileManager tileManager = new TileManager(this);
    public AssetSetter assetSetter = new AssetSetter(this);
    public CollisionChecker collisionChecker = new CollisionChecker(this);
    public UI ui;
    Thread gameThread;
    public Time time;
    public Weather weather;
    public GameClock gameClock;

    public Player player = new Player(this, keyH);
    public ArrayList<Entity> entities = new ArrayList<>();
    public ArrayList<NPC> npcs = new ArrayList<>();
    public Entity currentInteractingNPC = null;
    public ArrayList<CropSaveData> farmCropData = new ArrayList<>();
    private float musicVolume = 0.7f;
    private float seVolume = 0.8f;
    private boolean musicMuted = false;
    private boolean seMuted = false;

    public static class CropSaveData {
        public String cropType;
        public int worldX, worldY;
        public int currentGrowthDays;
        public boolean isWatered;
        public boolean grewToday;

        public CropSaveData(String cropType, int worldX, int worldY, int currentGrowthDays, boolean isWatered,
                boolean grewToday) {
            this.cropType = cropType;
            this.worldX = worldX;
            this.worldY = worldY;
            this.currentGrowthDays = currentGrowthDays;
            this.isWatered = isWatered;
            this.grewToday = grewToday;
        }
    }

    public int gameState;
    public static final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    public final int inventoryState = 4;
    public final int farmNameInputState = 5;
    public final int interactionMenuState = 6;
    public final int giftSelectionState = 7;
    public final int playerNameInputState = 8;
    public final int sleepTransitionState = 9;
    public final int eatState = 10;
    public final int sellState = 11;
    public final int cookingState = 12;
    public final int buyingState = 13;
    public final int fishingMinigameState = 14;
    public final int endGameState = 15;
    public int previousGameState = -1;
    public int creditPageState = 16;
    public int helpPageState = 17;
    public int playerStatsState = 18;
    public final int genderSelectionState = 19;
    public final int playerInfoState = 20;

    public final int FARM_MAP_INDEX = 8;
    public final int PLAYER_HOUSE_INDEX = 9;
    public final int STORE_INDEX = 10;

    public Recipe selectedRecipeForCooking = null;

    public boolean hasForcedSleepAt2AMToday = false;
    private boolean isProcessingNewDayDataInTransition = false;
    private long sleepTransitionStartTime = 0;
    private final long SLEEP_TRANSITION_MESSAGE_DURATION = 3500;
    public boolean shouldTeleportToPlayerHouse = false;
    public int playerHouseTeleportX = 0;
    public int playerHouseTeleportY = 0;
    private boolean isInMapTransition = false;

    private boolean hasTriggeredEndgame = false;

    public ArrayList<MapInfo> mapInfos = new ArrayList<>();
    public int currentMapIndex = -1;
    public int previousMapIndex = -1;
    public ArrayList<NPC> allNpcsInWorld = new ArrayList<>();

    public int[][] farmMapTileData = null;
    public int farmMapMaxCols = 0;
    public int farmMapMaxRows = 0;

    public static class SimpleFarmLayout {
        public int houseX = 23;
        public int houseY = 21;
        public int shippingBinX = 25;
        public int shippingBinY = 15;
        public int pondX = 8;
        public int pondY = 6;

        public SimpleFarmLayout(FarmLayoutGenerator.FarmLayout randomLayout) {
            if (randomLayout != null && randomLayout.isValid) {
                this.houseX = randomLayout.houseX;
                this.houseY = randomLayout.houseY;
                this.shippingBinX = randomLayout.shippingBinX;
                this.shippingBinY = randomLayout.shippingBinY;
                this.pondX = randomLayout.pondX;
                this.pondY = randomLayout.pondY;
            }
        }

        public SimpleFarmLayout() {
        }
    }

    public SimpleFarmLayout currentFarmLayout = new SimpleFarmLayout();

    public void generateNewFarmLayout() {
        if (layoutGenerator == null) {
            layoutGenerator = new FarmLayoutGenerator();
        }

        System.out.println("[GamePanel] Generating new randomized farm layout...");
        currentRandomLayout = layoutGenerator.generateRandomLayout();

        if (currentRandomLayout.isValid) {

            currentFarmLayout = new SimpleFarmLayout(currentRandomLayout);

            System.out.println("[GamePanel] New farm layout generated: " + currentRandomLayout.toString());

            int[][] newMapData = layoutGenerator.generateFarmMapData(currentRandomLayout);

            maxWorldCol = 32;
            maxWorldRow = 32;

            if (tileManager != null) {
                tileManager.mapTileNum = newMapData;
                System.out.println("[GamePanel] Farm map data updated with new layout");
            }

            farmMapTileData = null;
            farmCropData.clear();

        } else {
            System.err.println("[GamePanel] Failed to generate valid farm layout!");
        }
    }

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.requestFocusInWindow();

    }

    public void setupGame() {
        initializeMapInfos();
        gameState = titleState;
        environmentManager.setup();
        assetSetter.initializeAllNPCs();
        generateNewFarmLayout();
        gameClock.addObserver(player);
        weather.addObserver(player);
        playMusic(0);
        setMusicVolume(20f);

    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();

        if (gameClock != null && !gameClock.isAlive()) {
            try {
                System.out.println("[GamePanel] Attempting to start GameClock thread...");
                gameClock.start();
                System.out.println("[GamePanel] GameClock thread started successfully.");
            } catch (IllegalThreadStateException e) {
                System.err.println("[GamePanel] GameClock thread may have already been started: " + e.getMessage());
            }
        } else if (gameClock != null) {
            System.out.println("[GamePanel] GameClock thread was already alive.");
        } else {
            System.err.println("[GamePanel] GameClock is null, cannot start.");
        }
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public void setGameClock(GameClock gameClock) {
        this.gameClock = gameClock;
        this.ui = new UI(this, gameClock);
    }

    @Override
    public void run() {
        double drawInterval = (double) 1000000000 / fps;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {

                update();
                repaint();
                delta--;
            }

            if (timer >= 1000000000) {
                timer = 0;
            }
        }
    }

    public void update() {
        if (shouldTeleportToPlayerHouse) {
            System.out.println("[GamePanel] Processing shouldTeleportToPlayerHouse flag in update()");
            if (currentMapIndex != PLAYER_HOUSE_INDEX) {
                loadMapbyIndex(PLAYER_HOUSE_INDEX);
            } else {
                player.worldX = (playerHouseTeleportX != 0) ? playerHouseTeleportX : tileSize * 7;
                player.worldY = (playerHouseTeleportY != 0) ? playerHouseTeleportY : tileSize * 6;
                player.direction = "down";

                shouldTeleportToPlayerHouse = false;
                playerHouseTeleportX = 0;
                playerHouseTeleportY = 0;
            }
        }
        if (gameState != titleState && gameState != sleepTransitionState && gameClock != null && player != null) {
            if (gameClock.getTime().getHour() == 2 && !player.isCurrentlySleeping()) {
                if (!hasForcedSleepAt2AMToday) {
                    System.out.println("[GamePanel] 2 AM detected. Forcing player to sleep.");
                    player.sleep("It's 2 AM! You pass out \n from staying up too late.");

                }
            }

            if (player.isCurrentlySleeping() && gameState != sleepTransitionState) {
                System.out.println("[GamePanel] Player initiated sleep. Switching to sleepTransitionState.");
                gameState = sleepTransitionState;
                if (gameClock != null)
                    gameClock.pauseTime();
                isProcessingNewDayDataInTransition = false;
                sleepTransitionStartTime = System.currentTimeMillis();
                hasForcedSleepAt2AMToday = true;
                return;
            }
        }

        if (gameState == playState) {


            if (!hasTriggeredEndgame && (player.gold >= 17209 || player.isMarried())) {
                System.out.println("[GamePanel] Endgame condition met for the first time. Gold: " + player.gold
                        + ", Married: " + player.isMarried());
                previousGameState = gameState;
                gameState = endGameState;
                hasTriggeredEndgame = true;
                if (gameClock != null && !gameClock.isPaused()) {
                    gameClock.pauseTime();
                }
                System.out.println("[GamePanel] hasTriggeredEndgame flag set to true");

            } else {

                if (gameClock != null && gameClock.isPaused() && !hasTriggeredEndgame) {

                    gameClock.resumeTime();
                }

                if (player.justGotMarried) {
                    player.justGotMarried = false;
                    handleEndOfWeddingEvent();
                }

                player.update();

                for (NPC character : npcs) {
                    if (character != null) {
                        character.update();
                    }
                }

                ArrayList<Entity> entitiesCopy = new ArrayList<>(entities);
                for (Entity entity : entitiesCopy) {
                    if (entity != null && !(entity instanceof Player) && !(entity instanceof NPC)) {
                        try {
                            entity.update();
                        } catch (Exception e) {
                            System.err.println("[GamePanel] Error updating entity " + entity.getClass().getSimpleName()
                                    + ": " + e.getMessage());

                            entities.remove(entity);
                        }
                    }
                }

                if (environmentManager != null) {
                    environmentManager.update();
                }
            }
        }

        else if (gameState == endGameState) {
            if (environmentManager != null) {
                playSE(1);
                environmentManager.update();
            }

        } else if (gameState == sleepTransitionState) {

            if (!isProcessingNewDayDataInTransition) {
                System.out.println("[GamePanel] sleepTransitionState: Processing new day data...");

                Time currentTime = gameClock.getTime();
                currentTime.forceStartNewDay();

                gameClock.updateSeasonBasedOnDay(currentTime.getDay());
                if ((currentTime.getDay() - 1) % 10 == 0) {
                    gameClock.getWeather().resetRainyCount();
                }
                performDailyResets();

                if (player.goldFromShipping > 0) {
                    if (ui.currentDialogue == null)
                        ui.currentDialogue = "";
                    if (!ui.currentDialogue.isEmpty())
                        ui.currentDialogue += "\n";
                    ui.currentDialogue += "Kamu mendapat " + player.goldFromShipping + "G dari penjualan.";

                    System.out.println("TESTTT PLIS MASUK");
                }

                Time countTimeforSeason = gameClock.getTime();
                if ((countTimeforSeason.getDay() - 1) % 10 == 0) {
                    Season season = gameClock.getCurrentSeason();
                    player.seasonPlayed.put(season, player.seasonPlayed.getOrDefault(season, 0) + 1);
                }

                isProcessingNewDayDataInTransition = true;
                System.out.println("[GamePanel] New day data processed. Day: " + currentTime.getDay() +
                        ", Time: " + currentTime.getFormattedTime() +
                        ", Season: " + gameClock.getCurrentSeason() +
                        ", Weather: " + gameClock.getWeather().getWeatherName());
            }

            if (System.currentTimeMillis() - sleepTransitionStartTime >= SLEEP_TRANSITION_MESSAGE_DURATION) {
                System.out.println("[GamePanel] Sleep transition message duration ended. Returning to playState.");
                player.setCurrentlySleeping(false);
                hasForcedSleepAt2AMToday = false;
                isProcessingNewDayDataInTransition = false;
                ui.currentDialogue = "";

                gameState = playState;
                if (gameClock != null && gameClock.isPaused()) {
                    gameClock.resumeTime();
                }
            }
        } else if (gameState == pauseState) {
            if (gameClock != null && !gameClock.isPaused()) {
                gameClock.pauseTime();
            }
        } else if (gameState == dialogueState || gameState == inventoryState ||
                gameState == interactionMenuState || gameState == giftSelectionState || gameState == sellState) {
            if (environmentManager != null) {
                environmentManager.update();
            } else {
                System.err.println("[GamePanel.update()] FATAL: environmentManager is null!");
            }
            if (gameClock != null && !gameClock.isPaused()) {
                gameClock.pauseTime();
            }
        } else if (gameState == playerNameInputState || gameState == farmNameInputState
                || gameState == genderSelectionState) {
            if (this.gameClock != null && !this.gameClock.isPaused()) {
                this.gameClock.pauseTime();
            }
        }
    }

    public void initializeMapInfos() {
        mapInfos.add(new MapInfo("Abigail's House", "/maps/abigail_house_data.txt", "/maps/abigail_house.txt"));

        mapInfos.add(new MapInfo("Caroline's House", "/maps/caroline_house_data.txt", "/maps/caroline_house.txt"));

        mapInfos.add(new MapInfo("Dasco's House", "/maps/dasco_house_data.txt", "/maps/dasco_house.txt"));
        mapInfos.add(
                new MapInfo("Mayor Tadi's House", "/maps/mayor_tadi_house_data.txt", "/maps/mayor_tadi_house.txt"));

        mapInfos.add(new MapInfo("Perry's House", "/maps/perry_house_data.txt", "/maps/perry_house.txt"));

        mapInfos.add(new MapInfo("Forest River", "/maps/forest_river_data.txt", "/maps/forest_river.txt"));
        mapInfos.add(new MapInfo("Mountain Lake", "/maps/mountain_lake_data.txt", "/maps/mountain_lake.txt"));

        mapInfos.add(new MapInfo("Ocean", "/maps/ocean_data.txt", "/maps/ocean.txt"));
        mapInfos.add(new MapInfo("Farm", "/maps/farm_map_1_data.txt", "/maps/farm_map_1.txt"));
        mapInfos.add(new MapInfo("Player's House", "/maps/player_house_data.txt", "/maps/player_house.txt"));
        mapInfos.add(new MapInfo("Store", "/maps/store_data.txt", "/maps/store.txt"));

    }

    public void loadMapbyIndex(int newMapIndex) {
        if (newMapIndex >= 0 && newMapIndex < mapInfos.size()) {
            if (isInMapTransition) {
                System.out.println("[GamePanel] Already in map transition, ignoring loadMapbyIndex call");
                return;
            }
            isInMapTransition = true;

            if (this.currentMapIndex == FARM_MAP_INDEX && newMapIndex != FARM_MAP_INDEX) {
                System.out.println("[GamePanel] Leaving farm - saving tiles and crops");

                if (tileManager.mapTileNum != null && maxWorldCol > 0 && maxWorldRow > 0) {
                    farmMapTileData = new int[maxWorldCol][maxWorldRow];
                    for (int col = 0; col < maxWorldCol; col++) {
                        for (int row = 0; row < maxWorldRow; row++) {
                            farmMapTileData[col][row] = tileManager.mapTileNum[col][row];
                        }
                    }
                    farmMapMaxCols = maxWorldCol;
                    farmMapMaxRows = maxWorldRow;
                    System.out.println(
                            "[GamePanel] Farm tiles saved - dimensions: " + farmMapMaxCols + "x" + farmMapMaxRows);
                }

                farmCropData.clear();
                for (Entity entity : entities) {
                    if (entity instanceof OBJ_PlantedCrop) {
                        OBJ_PlantedCrop crop = (OBJ_PlantedCrop) entity;
                        CropSaveData saveData = new CropSaveData(
                                crop.getCropType(),
                                crop.worldX,
                                crop.worldY,
                                crop.getCurrentGrowthDays(),
                                crop.isWatered(),
                                crop.getGrewToday());
                        farmCropData.add(saveData);
                        System.out.println("[GamePanel] Saved crop: " + crop.getCropType() +
                                " at (" + (crop.worldX / tileSize) + "," + (crop.worldY / tileSize) + ")" +
                                " - Growth: " + crop.getCurrentGrowthDays() +
                                ", Watered: " + crop.isWatered());
                    }
                }
                System.out.println("[GamePanel] Farm data saved successfully - " + farmCropData.size() + " crops");
            }

            this.previousMapIndex = this.currentMapIndex;
            this.currentMapIndex = newMapIndex;
            MapInfo selectedMap = mapInfos.get(this.currentMapIndex);
            String mapName = selectedMap.getMapName();
            System.out.println("[GamePanel] Loading map: " + mapName + " (Index: " + newMapIndex + ")");

            try {
                String enumCompatibleName = mapName.toUpperCase().replace(" ", "_").replace("'S", "S");
                System.out.println("[GamePanel] Trying to set location enum: " + enumCompatibleName);
                spakborhills.enums.Location locationEnum = spakborhills.enums.Location.valueOf(enumCompatibleName);
                player.setCurrentLocation(locationEnum);
                System.out.println(
                        "[GamePanel] Location set using enum: " + locationEnum + " -> " + player.getLocation());
            } catch (IllegalArgumentException e) {
                System.out.println("[GamePanel] Enum not found for '" + mapName + "', using string location");
                player.setLocation(mapName);
                System.out.println("[GamePanel] Location set using string: " + player.getLocation());
            }

            System.out.println("[GamePanel] Final player location after setting: " + player.getLocation());
            System.out.println("[GamePanel] Transitioning from map index " + previousMapIndex + " to "
                    + this.currentMapIndex + " (" + selectedMap.getMapName() + ")");

            boolean isSafeTransition = (previousMapIndex == PLAYER_HOUSE_INDEX && this.currentMapIndex == 8) ||
                    (previousMapIndex == 8 && this.currentMapIndex == PLAYER_HOUSE_INDEX) ||
                    this.currentMapIndex == 8 || this.currentMapIndex == PLAYER_HOUSE_INDEX;

            boolean playerCollapsedFromTravel = false;
            if (previousMapIndex != -1 && !isSafeTransition && this.currentMapIndex != previousMapIndex) {
                if (player.tryDecreaseEnergy(10)) {
                    this.time.advanceTime(15);
                    ui.showMessage("Capek juga jalan-jalan. -10 energy.");
                }
                if (player.isCurrentlySleeping()) {
                    playerCollapsedFromTravel = true;
                    System.out.println(
                            "[GamePanel] Player collapsed from travel exhaustion. Redirecting to Player House instead of destination.");
                    this.currentMapIndex = PLAYER_HOUSE_INDEX;
                    selectedMap = mapInfos.get(this.currentMapIndex);
                    mapName = selectedMap.getMapName();
                }
            }

            if (shouldTeleportToPlayerHouse) {
                System.out.println("[GamePanel] Teleport to Player House requested");
                this.currentMapIndex = PLAYER_HOUSE_INDEX;
                selectedMap = mapInfos.get(this.currentMapIndex);
                mapName = selectedMap.getMapName();
                playerCollapsedFromTravel = true;
                shouldTeleportToPlayerHouse = false;
            }

            tileManager.loadMap(selectedMap);
            entities.clear();
            npcs.clear();

            int targetX = this.tileSize * 20;
            int targetY = this.tileSize * 29;
            String targetDir = "down";

            if (playerCollapsedFromTravel) {
                targetX = this.tileSize * 7;
                targetY = this.tileSize * 6;
                targetDir = "down";
                System.out.println("[GamePanel] Player collapsed - positioned at Player House bed");
            } else {

                if (this.currentMapIndex == PLAYER_HOUSE_INDEX) {
                    if (previousMapIndex == FARM_MAP_INDEX) {
                        targetX = this.tileSize * 25;
                        targetY = this.tileSize * 32;
                        targetDir = "up";
                    } else {
                        targetX = this.tileSize * 10;
                        targetY = this.tileSize * 10;
                        targetDir = "down";
                    }
                } else if (this.currentMapIndex == FARM_MAP_INDEX) {
                    if (previousMapIndex == PLAYER_HOUSE_INDEX) {
                        targetX = currentFarmLayout.houseX * tileSize + (3 * tileSize);
                        targetY = currentFarmLayout.houseY * tileSize + (7 * tileSize);
                        targetDir = "down";
                    } else {
                        targetX = this.tileSize * 25;
                        targetY = this.tileSize * 25;
                        targetDir = "down";
                    }
                } else if (selectedMap.getMapName().equalsIgnoreCase("Ocean")) {
                    targetX = this.tileSize * 1;
                    targetY = this.tileSize * 1;
                    targetDir = "down";
                    System.out.println("[GamePanel] Player spawning in Ocean at tile (1,1) based on map name.");
                }
            }

            player.setPositionForMapEntry(targetX, targetY, targetDir);

            assetSetter.setObject(selectedMap.getMapName());
            assetSetter.setNPC(selectedMap.getMapName());

            if (this.currentMapIndex == FARM_MAP_INDEX) {
                System.out.println("[GamePanel] Entering farm - restoring tiles and crops");

                if (farmMapTileData != null && farmMapMaxCols > 0 && farmMapMaxRows > 0) {
                    System.out.println("[GamePanel] Restoring farm tile data...");

                    if (tileManager.mapTileNum == null ||
                            tileManager.mapTileNum.length != farmMapMaxCols ||
                            (tileManager.mapTileNum.length > 0 && tileManager.mapTileNum[0].length != farmMapMaxRows)) {

                        maxWorldCol = farmMapMaxCols;
                        maxWorldRow = farmMapMaxRows;
                        tileManager.mapTileNum = new int[farmMapMaxCols][farmMapMaxRows];
                    }

                    for (int col = 0; col < farmMapMaxCols; col++) {
                        for (int row = 0; row < farmMapMaxRows; row++) {
                            if (col < farmMapTileData.length && row < farmMapTileData[col].length) {
                                tileManager.mapTileNum[col][row] = farmMapTileData[col][row];
                            }
                        }
                    }
                    System.out
                            .println("[GamePanel] Farm tile data restored - " + farmMapMaxCols + "x" + farmMapMaxRows);
                } else {
                    System.out.println("[GamePanel] No saved farm tile data to restore");
                }

                if (!farmCropData.isEmpty()) {
                    System.out.println("[GamePanel] Restoring " + farmCropData.size() + " farm crops...");

                    for (CropSaveData cropData : farmCropData) {

                        OBJ_PlantedCrop restoredCrop = new OBJ_PlantedCrop(this, cropData.cropType, cropData.worldX,
                                cropData.worldY);

                        restoredCrop.setCurrentGrowthDays(cropData.currentGrowthDays);
                        restoredCrop.setWatered(cropData.isWatered);
                        restoredCrop.setGrewToday(cropData.grewToday);

                        entities.add(restoredCrop);

                        int tileX = cropData.worldX / tileSize;
                        int tileY = cropData.worldY / tileSize;
                        if (tileX >= 0 && tileX < maxWorldCol && tileY >= 0 && tileY < maxWorldRow) {
                            if (cropData.isWatered) {
                                tileManager.mapTileNum[tileX][tileY] = 80;
                            } else {
                                tileManager.mapTileNum[tileX][tileY] = 55;
                            }
                        }

                        System.out.println("[GamePanel] Restored " + cropData.cropType + " at (" + tileX + "," + tileY +
                                ") - Growth: " + cropData.currentGrowthDays + ", Watered: " + cropData.isWatered +
                                ", Tile: " + tileManager.mapTileNum[tileX][tileY]);
                    }
                    System.out.println("[GamePanel] All farm crops restored successfully");
                } else {
                    System.out.println("[GamePanel] No saved crop data to restore for farm");
                }
            }

            System.out.println("[GamePanel] Map loaded successfully: " + selectedMap.getMapName());
            gameState = playState;
            isInMapTransition = false;

        } else {
            System.err.println("[GamePanel] Invalid map index: " + newMapIndex + ". Cannot load map.");
            gameState = titleState;
            isInMapTransition = false;
        }
    }

    public void handleEndOfWeddingEvent() {
        System.out.println("[GamePanel] Wedding event concluded. Skipping time to 22:00.");

        if (gameClock != null && gameClock.getTime() != null && player != null) {
            gameClock.pauseTime();

            Time currentTime = gameClock.getTime();
            currentTime.setCurrentTime(22, 0);
            player.tryDecreaseEnergy(80);

            ui.showMessage("Udah jam 22.00 aja nih");

        } else {
            System.err.println("[GamePanel] Cannot skip time: GameClock, Time, or Player is null.");
        }
    }

    public void processShippingBin() {
        if (!player.hasUsedShippingBinToday || player.shippingBinTypes.isEmpty()) {
            player.goldFromShipping = 0;
            return;
        }

        int totalEarnings = 0;
        int totalItemsSold = 0;

        for (Map.Entry<String, OBJ_Item> entry : player.shippingBinTypes.entrySet()) {
            String itemName = entry.getKey();
            OBJ_Item item = entry.getValue();
            int earnings = item.getSellPrice() * item.quantity;
            totalEarnings += earnings;
            totalItemsSold += item.quantity;

            System.out.println("[GamePanel] Sold " + item.quantity + "x " + itemName + " for " + earnings + "G");
        }

        if (totalEarnings > 0) {
            player.gold += totalEarnings;
            player.goldFromShipping = totalEarnings;

            player.totalIncome += totalEarnings;

            Season currentSeason = gameClock.getCurrentSeason();
            player.seasonalIncome.put(currentSeason,
                    player.seasonalIncome.getOrDefault(currentSeason, 0L) + totalEarnings);
            player.countIncome.put(currentSeason, player.countIncome.getOrDefault(currentSeason, 0) + 1);

            System.out.println("[GamePanel] Player earned " + totalEarnings +
                    "G from shipped items. Total gold: " + player.gold);
            playSE(5);
        } else {
            player.goldFromShipping = 0;
        }
        player.clearShippingBin();
        System.out.println("[GamePanel] Shipping bin processed and reset for the new day.");
    }

    public void performDailyResets() {
        if (gameClock == null || gameClock.getTime() == null) {
            System.err.println("[GamePanel] Cannot perform daily resets: GameClock or Time is null.");
            return;
        }
        System.out.println("[GamePanel] Performing daily resets for Day " + gameClock.getTime().getDay() + "...");
        processShippingBin();
        growAllCrops();

        for (NPC npc : npcs) {
            if (npc != null) {
                npc.hasReceivedGiftToday = false;
            }
        }

        for (NPC npc_world : allNpcsInWorld) {
            if (npc_world != null) {
                npc_world.hasReceivedGiftToday = false;
            }
        }
        System.out.println("[GamePanel] NPC daily states reset.");
        System.out.println("[GamePanel] Daily resets completed.");
    }

    public void resetCoreGameDataForNewGame() {
        if (player != null) {
            player.setDefaultValues();
        }
        entities.clear();
        npcs.clear();

        if (gameClock != null) {
            gameClock.resetTime();
        }

        if (ui != null) {
            ui.currentDialogue = "";
            ui.commandNumber = 0;
        }
        hasTriggeredEndgame = false;

        generateNewFarmLayout();

        System.out.println("Core game data has been reset for a new game session with new farm layout.");
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == titleState) {
            ui.draw(g2);
        }

        else {
            tileManager.draw(g2);

            entities.sort(new Comparator<Entity>() {
                @Override
                public int compare(Entity o1, Entity o2) {
                    return Integer.compare(o1.worldY, o2.worldY);
                }
            });

            for (Entity entity : entities) {
                entity.draw(g2);
            }

            player.draw(g2);

            if (environmentManager != null) {
                environmentManager.draw(g2);
            }

            ui.draw(g2);
        }
        g2.dispose();
    }

    public void completeShippingBinTransaction() {
        if (player == null) {
            System.err.println("[GamePanel] Cannot complete shipping: Player is null");
            return;
        }

        if (player.shippingBinTypes.isEmpty()) {
            System.out.println("[GamePanel] No items shipped, shipping bin remains available");
            ui.showMessage("Tidak ada item di shipping bin.");
        } else {

            player.hasUsedShippingBinToday = true;
            System.out.println("[GamePanel] Shipping bin transaction completed. " +
                    player.shippingBinTypes.size() + " items will be sold overnight.");
            ui.showMessage("Item berhasil diletakkan di shipping bin. Kamu akan dapat uang besok!");
        }

        gameState = playState;

        if (gameClock != null && gameClock.isPaused()) {
            gameClock.resumeTime();
        }
    }

    public void cancelShippingBinTransaction() {
        if (player == null) {
            System.err.println("[GamePanel] Cannot cancel shipping: Player is null");
            return;
        }

        if (!player.shippingBinTypes.isEmpty()) {
            System.out.println("[GamePanel] Returning " + player.shippingBinTypes.size() +
                    " item types from shipping bin to inventory");

            for (Map.Entry<String, OBJ_Item> entry : player.shippingBinTypes.entrySet()) {
                OBJ_Item binItem = entry.getValue();

                boolean addedSuccessfully = player.addItemToInventory(binItem);

                if (!addedSuccessfully) {
                    System.out.println("[GamePanel] Warning: Could not return " + binItem.name +
                            " to inventory (full). Item may be lost.");
                    ui.showMessage("Inventory penuh. Beberapa barang bisa hilang.");
                }
            }

            player.clearShippingBin();
            ui.showMessage("Item dikembalikan.");
        }

        player.hasUsedShippingBinToday = false;
        System.out.println("[GamePanel] Shipping bin transaction cancelled, remains available");

        gameState = playState;

        if (gameClock != null && gameClock.isPaused()) {
            gameClock.resumeTime();
        }
    }

    public void setMusicVolume(float volume) {
        musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        music.setVolume(musicMuted ? 0.0f : musicVolume);
        System.out.println("[GamePanel] Music volume set to: " + (musicVolume * 100) + "%");
    }

    public void setSoundEffectVolume(float volume) {
        seVolume = Math.max(0.0f, Math.min(1.0f, volume));
        se.setVolume(seMuted ? 0.0f : seVolume);
        System.out.println("[GamePanel] Sound effects volume set to: " + (seVolume * 100) + "%");
    }

    public void toggleMusicMute() {
        musicMuted = !musicMuted;
        music.setVolume(musicMuted ? 0.0f : musicVolume);
        System.out.println("[GamePanel] Music " + (musicMuted ? "muted" : "unmuted"));
    }

    public void toggleSoundEffectMute() {
        seMuted = !seMuted;
        se.setVolume(seMuted ? 0.0f : seVolume);
        System.out.println("[GamePanel] Sound effects " + (seMuted ? "muted" : "unmuted"));
    }

    public void increaseMusicVolume() {
        setMusicVolume(musicVolume + 0.1f);
    }

    public void decreaseMusicVolume() {
        setMusicVolume(musicVolume - 0.1f);
    }

    public void increaseSoundEffectVolume() {
        setSoundEffectVolume(seVolume + 0.1f);
    }

    public void decreaseSoundEffectVolume() {
        setSoundEffectVolume(seVolume - 0.1f);
    }

    public void playMusic(int i) {
        music.setFile(i);
        music.setVolume(musicMuted ? 0.0f : musicVolume);
        music.play();
        music.loop();
    }

    public void playSE(int i) {
        se.setFile(i);
        se.setVolume(seMuted ? 0.0f : seVolume);
        se.play();
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public float getSoundEffectVolume() {
        return seVolume;
    }

    public boolean isMusicMuted() {
        return musicMuted;
    }

    public boolean isSoundEffectMuted() {
        return seMuted;
    }

    public void growAllCrops() {
        System.out.println("[GamePanel] ======= PROCESSING END OF DAY GROWTH & PREPARING CROPS FOR NEW DAY =======");

        boolean isRainingForNewDay = false;
        if (gameClock != null && gameClock.getWeather() != null) {
            System.out.println("[GamePanel] Current weather before generation: " + gameClock.getCurrentWeather());
            gameClock.getWeather().generateNewWeather();
            isRainingForNewDay = (gameClock.getCurrentWeather() == spakborhills.enums.Weather.RAINY);
            System.out.println("[GamePanel] NEW weather generated: " + gameClock.getCurrentWeather());
        } else {
            System.err.println("[GamePanel] WARNING: GameClock or Weather is null!");
        }

        System.out.println("[GamePanel] Weather for the NEW DAY will be: " +
                (isRainingForNewDay ? "RAINY (crops auto-watered)" : "CLEAR (crops need manual watering)"));

        int totalCropsProcessed = 0;
        int cropsGrown = 0;
        int cropsNeedWater = 0;

        if (currentMapIndex == FARM_MAP_INDEX) {

            System.out.println("[GamePanel] Currently on farm - processing active crop entities");
            ArrayList<Entity> entitiesCopy = new ArrayList<>(entities);

            for (Entity entity : entitiesCopy) {
                if (entity instanceof OBJ_PlantedCrop) {
                    totalCropsProcessed++;
                    OBJ_PlantedCrop crop = (OBJ_PlantedCrop) entity;
                    boolean grewThisRound = processSingleCrop(crop, isRainingForNewDay);
                    if (grewThisRound)
                        cropsGrown++;
                    else if (!crop.isWatered())
                        cropsNeedWater++;
                }
            }
        } else {

            System.out.println("[GamePanel] NOT on farm - processing saved crop data");
            System.out.println("[GamePanel] Saved farm crop data count: " + farmCropData.size());

            if (!farmCropData.isEmpty()) {
                ArrayList<CropSaveData> updatedCropData = new ArrayList<>();

                for (CropSaveData cropData : farmCropData) {
                    totalCropsProcessed++;
                    System.out.println("[GamePanel] Processing saved crop: " + cropData.cropType +
                            " (Growth: " + cropData.currentGrowthDays + ", Watered: " + cropData.isWatered + ")");

                    boolean grewThisRound = false;
                    if (cropData.isWatered && !cropData.grewToday
                            && cropData.currentGrowthDays < getCropMaxGrowthDays(cropData.cropType)) {
                        cropData.currentGrowthDays++;
                        grewThisRound = true;
                        cropsGrown++;
                        System.out.println(
                                "[GamePanel] ✓ " + cropData.cropType + " GREW to day " + cropData.currentGrowthDays);
                    } else if (!cropData.isWatered) {
                        System.out.println("[GamePanel] ✗ " + cropData.cropType + " SKIPPED growth - not watered");
                        cropsNeedWater++;
                    }

                    cropData.grewToday = false;
                    if (isRainingForNewDay) {
                        cropData.isWatered = true;
                        System.out.println("[GamePanel] " + cropData.cropType + " auto-watered by rain");
                    } else {
                        cropData.isWatered = false;
                        System.out.println("[GamePanel] " + cropData.cropType + " watered status reset");
                    }

                    updatedCropData.add(cropData);
                }

                farmCropData.clear();
                farmCropData.addAll(updatedCropData);
                System.out.println("[GamePanel] Farm crop data updated with growth progress");
            } else {
                System.out.println("[GamePanel] No saved farm crop data to process");
            }
        }

        System.out.println("[GamePanel] ======= CROP PROCESSING SUMMARY =======");
        System.out.println("[GamePanel] Total crops processed: " + totalCropsProcessed);
        System.out.println("[GamePanel] Crops that grew: " + cropsGrown);
        System.out.println("[GamePanel] Crops that needed water: " + cropsNeedWater);
        System.out.println("[GamePanel] Weather for new day: " + (isRainingForNewDay ? "RAINY" : "CLEAR"));
        System.out.println("[GamePanel] ======================= CROP PROCESSING COMPLETE ========================");
    }

    private boolean processSingleCrop(OBJ_PlantedCrop crop, boolean isRainingForNewDay) {
        System.out.println("[GamePanel] Processing crop: " + crop.getCropType() +
                " (Growth: " + crop.getCurrentGrowthDays() + "/" + crop.getDaysToGrow() +
                ", Watered: " + crop.isWatered() + ", GrewToday: " + crop.getGrewToday() + ")");

        boolean grewThisRound = false;
        if (crop.isWatered() && !crop.getGrewToday()) {
            System.out.println("[GamePanel] Crop conditions met for growth - processing...");
            crop.processGrowthForCompletedDay();
            grewThisRound = true;
            System.out.println("[GamePanel] ✓ " + crop.getCropType() + " GREW!");
        } else if (!crop.isWatered()) {
            System.out.println("[GamePanel] ✗ " + crop.getCropType() + " SKIPPED growth - not watered yesterday");
        } else if (crop.getGrewToday()) {
            System.out.println("[GamePanel]" + crop.getCropType() + " already grew today");
        }

        System.out.println("[GamePanel] Resetting " + crop.getCropType() + " for new day...");
        crop.resetForNewDay(isRainingForNewDay);

        System.out.println(
                "[GamePanel] After processing - Growth: " + crop.getCurrentGrowthDays() + "/" + crop.getDaysToGrow() +
                        ", Watered: " + crop.isWatered() + ", Ready: " + crop.isReadyToHarvest());

        return grewThisRound;
    }

    private int getCropMaxGrowthDays(String cropType) {
        switch (cropType) {
            case "Parsnip":
                return 1;
            case "Potato":
                return 3;
            case "Cauliflower":
                return 5;
            case "Blueberry":
                return 7;
            case "Cranberry":
                return 2;
            case "Hot Pepper":
                return 1;
            case "Wheat":
                return 4;
            case "Grape":
                return 3;
            case "Melon":
                return 4;
            case "Pumpkin":
                return 4;
            default:
                return 4;
        }
    }
}