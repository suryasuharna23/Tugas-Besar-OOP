package spakborhills;

import spakborhills.cooking.Recipe;
import spakborhills.entity.Entity;
import spakborhills.entity.NPC;
import spakborhills.entity.Player;
import spakborhills.Tile.TileManager;
import spakborhills.enums.EntityType;
import spakborhills.object.OBJ_Item;

import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class GamePanel extends  JPanel implements Runnable {
    final int originalTileSize = 16;
    final int scale = 3;
    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    public int maxWorldCol = 50;
    public int maxWorldRow = 50;

    final int fps = 60;

    public KeyHandler keyH = new KeyHandler(this);
    Sound music = new Sound();
    Sound se = new Sound();
    public TileManager tileManager = new TileManager(this);
    public AssetSetter assetSetter = new AssetSetter(this);
    public CollisionChecker collisionChecker = new CollisionChecker(this);
    public UI ui;
    Thread gameThread;
    private Time time;
    private Weather weather;
    public GameClock gameClock;


    public Player player = new Player(this, keyH);
    public ArrayList<Entity> entities = new ArrayList<>();
    public ArrayList<NPC> npcs = new ArrayList<>();
    public Entity currentInteractingNPC = null;

    public int gameState;
    public final int titleState = 0;
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

    public final int PLAYER_HOUSE_INDEX = 10;

    public Recipe selectedRecipeForCooking = null;

    private boolean hasForcedSleepAt2AMToday = false;
    private boolean isProcessingNewDayDataInTransition = false;
    private long sleepTransitionStartTime = 0;
    private final long SLEEP_TRANSITION_MESSAGE_DURATION = 3500;

    public ArrayList<MapInfo> mapInfos = new ArrayList<>();
    public int currentMapIndex = -1;
    public int previousMapIndex = -1;

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

    public void run() {
        double drawInterval = (double) 1000000000 / fps;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if (timer >= 1000000000) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
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
                gameClock.pauseTime();
                isProcessingNewDayDataInTransition = false;
                sleepTransitionStartTime = System.currentTimeMillis();
                hasForcedSleepAt2AMToday = true;
                return;
            }
        }

        if (gameState == playState) {
            if (gameClock != null && gameClock.isPaused()) {
                System.out.println("[GamePanel] Resuming GameClock in playState.");
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
        }
        else if (gameState == sleepTransitionState) {
            if (!isProcessingNewDayDataInTransition) {
                System.out.println("[GamePanel] sleepTransitionState: Processing new day data...");

                Time currentTime = gameClock.getTime();
                currentTime.forceStartNewDay();

                gameClock.updateSeasonBasedOnDay(currentTime.getDay());
                if ((currentTime.getDay() - 1) % 10 == 0) {
                    gameClock.getWeather().resetRainyCount();
                }
                gameClock.getWeather().generateNewWeather();

                performDailyResets();

                if (player.goldFromShipping > 0) {
                    if (ui.currentDialogue == null) ui.currentDialogue = "";
                    if (!ui.currentDialogue.isEmpty()) ui.currentDialogue += "\n";
                    ui.currentDialogue += "You earned " + player.goldFromShipping + "G from shipping.";
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
            if (gameClock != null && !gameClock.isPaused()) {
                gameClock.pauseTime();
            }
        } else if (gameState == playerNameInputState || gameState == farmNameInputState) {
            if (this.gameClock != null && !this.gameClock.isPaused()) {
                this.gameClock.pauseTime();
            }
            if (gameState == farmNameInputState && keyH.enterPressed) {
                String finalFarmName = ui.farmNameInput.trim();
                if (!finalFarmName.isEmpty()) {
                    loadMapbyIndex(PLAYER_HOUSE_INDEX);
                    resetGameForNewGame();
                    player.setFarmName(finalFarmName);
                    System.out.println("Farm Name Confirmed: " + player.getFarmName());
                    gameState = playState;
                    if (gameClock != null && gameClock.isPaused()) {
                        gameClock.resumeTime();
                    }

                } else {
                    ui.showMessage("Farm name cannot be empty!");
                }
                keyH.enterPressed = false;
            }
        }
    }

    private void initializeMapInfos() {
        mapInfos.add(new MapInfo("Abigail's House", "/maps/abigail_house_data.txt", "/maps/abigail_house.txt"));
        mapInfos.add(new MapInfo("Caroline's House", "/maps/caroline_house_data.txt", "/maps/caroline_house.txt"));
        mapInfos.add(new MapInfo("Dasco's House", "/maps/dasco_house_data.txt", "/maps/dasco_house.txt"));
        mapInfos.add(new MapInfo("Mayor Tadi's House", "/maps/mayor_tadi_house_data.txt", "/maps/mayor_tadi_house.txt"));
        mapInfos.add(new MapInfo("Perry's House", "/maps/perry_house_data.txt", "/maps/perry_house.txt"));
        mapInfos.add(new MapInfo("Store", "/maps/store_data.txt", "/maps/store.txt"));
        mapInfos.add(new MapInfo("Farm", "/maps/farm_map_1_data.txt", "/maps/farm_map_1.txt"));
        mapInfos.add(new MapInfo("Forest River", "/maps/forest_river_data.txt", "/maps/forest_river.txt"));
        mapInfos.add(new MapInfo("Mountain Lake", "/maps/mountain_lake_data.txt", "/maps/mountain_lake.txt"));
        mapInfos.add(new MapInfo("Ocean", "/maps/ocean_data.txt", "/maps/ocean.txt"));
        mapInfos.add(new MapInfo("Player's House", "/maps/player_house_data.txt", "/maps/player_house.txt"));
    }

    public void loadMapbyIndex(int newMapIndex){
        if (newMapIndex >= 0 && newMapIndex < mapInfos.size()) {
            this.previousMapIndex = this.currentMapIndex;
            this.currentMapIndex = newMapIndex;
            MapInfo selectedMap = mapInfos.get(this.currentMapIndex);

            System.out.println("[GamePanel] Transitioning from map index " + previousMapIndex + " to " + this.currentMapIndex + " (" + selectedMap.getMapName() + ")");

            boolean isSafeTransition = (previousMapIndex == PLAYER_HOUSE_INDEX && this.currentMapIndex == 6) ||
                    (previousMapIndex == 6 && this.currentMapIndex == PLAYER_HOUSE_INDEX);

            if (previousMapIndex != -1 && !isSafeTransition && this.currentMapIndex != previousMapIndex) {
                if (player.tryDecreaseEnergy(15)) {
                    ui.showMessage("Travel tired you out. -15 Energy.");
                    System.out.println("[GamePanel] Energy decreased by 15 for map travel.");
                } else {
                    System.out.println("[GamePanel] Player tried to travel with insufficient energy (may have passed out).");
                }
            }

            tileManager.loadMap(selectedMap);

            entities.clear();
            npcs.clear();

            int targetX = this.tileSize * 20;
            int targetY = this.tileSize * 29;
            String targetDir = "down";

            if (this.currentMapIndex == PLAYER_HOUSE_INDEX) {
                targetX = this.tileSize * 20;
                targetY = this.tileSize * 29;
                targetDir = "down";

            } else if (this.currentMapIndex == 6) {
                targetDir = "up";
            }

            player.setPositionForMapEntry(targetX, targetY, targetDir);

            assetSetter.setObject(mapInfos.get(currentMapIndex).getMapName());
            assetSetter.setNPC(mapInfos.get(currentMapIndex).getMapName());

            System.out.println("[GamePanel] Map loaded: " + selectedMap.getMapName());
            gameState = playState;
        } else {
            System.err.println("[GamePanel] Invalid map index: " + newMapIndex + ". Cannot load map.");
            gameState = titleState;
        }
    }


    public void handleEndOfWeddingEvent() {
        System.out.println("[GamePanel] Wedding event concluded. Skipping time to 22:00.");

        if (gameClock != null && gameClock.getTime() != null && player != null) {
            gameClock.pauseTime();

            Time currentTime = gameClock.getTime();
            currentTime.setCurrentTime(22, 0);

            ui.showMessage("The day flew by! It's now 10:00 PM.");
        } else {
            System.err.println("[GamePanel] Cannot skip time: GameClock, Time, or Player is null.");
        }
    }


    public void processShippingBin() {
        if (player.itemsInShippingBinToday.isEmpty()) {
            player.goldFromShipping = 0;
            return;
        }

        int totalEarnings = 0;
        for (Entity itemEntity : player.itemsInShippingBinToday) {
            if (itemEntity instanceof OBJ_Item) {
                OBJ_Item item = (OBJ_Item) itemEntity;
                totalEarnings += item.getSellPrice();
            }
        }

        if (totalEarnings > 0) {
            player.gold += totalEarnings;
            player.goldFromShipping = totalEarnings;
            System.out.println("[GamePanel] Player earned " + totalEarnings + "G from shipped items. Total gold: " + player.gold);
        } else {
            player.goldFromShipping = 0;
        }
        player.itemsInShippingBinToday.clear();
        player.hasUsedShippingBinToday = false;
        System.out.println("[GamePanel] Shipping bin processed and reset for the new day.");
    }

    public void performDailyResets() {
        if (gameClock == null || gameClock.getTime() == null) {
            System.err.println("[GamePanel] Cannot perform daily resets: GameClock or Time is null.");
            return;
        }
        System.out.println("[GamePanel] Performing daily resets for Day " + gameClock.getTime().getDay() + "...");
        processShippingBin();
        for (NPC npc : npcs) {
            if (npc != null) {
                npc.hasReceivedGiftToday = false;
            }
        }
        System.out.println("[GamePanel] NPC daily states reset.");
        System.out.println("[GamePanel] Daily resets completed.");
    }

    public void resetGameForNewGame() {
        player.setFarmName(ui.farmNameInput.trim());
        entities.clear();

        if (gameClock != null) {
            gameClock.resetTime();
        } else {
        }

        ui.currentDialogue = "";
        ui.farmNameInput = "";
        ui.commandNumber = 0;

        System.out.println("Game has been reset for a new game.");
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == titleState){
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
            for(Entity entity: entities){
                entity.draw(g2);
            }
            player.draw(g2);

            ui.draw(g2);
        }
        g2.dispose();
    }

    public void playMusic(int i){
        music.setFile(i);
        music.play();
        music.loop();
    }

    public void stopMusic(){
        music.stop();
    }

    public void playSE(int i){
        se.setFile(i);
        se.play();
    }
}