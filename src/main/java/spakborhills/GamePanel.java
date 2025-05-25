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
    // GAME WINDOW
    final int originalTileSize = 16;
    final int scale = 3;
    public final int tileSize = originalTileSize * scale; //48x48 tile
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    // WORLD SETTINGS
    public int maxWorldCol = 50;
    public int maxWorldRow = 50;

    //FPS
    final int fps = 60;

    //SYSTEM
    public KeyHandler keyH = new KeyHandler(this);
    Sound music = new Sound();
    Sound se = new Sound();
    TileManager tileManager = new TileManager(this);
    public AssetSetter assetSetter = new AssetSetter(this);
    public CollisionChecker collisionChecker = new CollisionChecker(this);
    public UI ui;
    Thread gameThread;
    private Time time;
    private Weather weather;
    public GameClock gameClock;

    //Entity, NPC & OBJECT
    public Player player = new Player(this, keyH);
    public ArrayList<Entity> entities = new ArrayList<>();
    public ArrayList<NPC> npcs = new ArrayList<>();
    public Entity currentInteractingNPC = null;

    //GAME STATE
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

    // SLEEPING
    private boolean hasForcedSleepAt2AMToday = false;
    private boolean isProcessingNewDayDataInTransition = false;
    private long sleepTransitionStartTime = 0;
    private final long SLEEP_TRANSITION_MESSAGE_DURATION = 3500;

    // COOKING
    public Recipe selectedRecipeForCooking = null;

    public GamePanel(){
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void setupGame(){
        assetSetter.setNPC();
        assetSetter.setObject();
        gameState = titleState;
        playMusic(0);
    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start(); // Mulai thread utama game

        // Mulai GameClock thread di sini jika belum berjalan dan sudah di-set
        if (gameClock != null && !gameClock.isAlive()) { // (A)
            try {
                System.out.println("[GamePanel] Attempting to start GameClock thread..."); // Tambahkan log
                gameClock.start(); // << HANYA PANGGIL SEKALI DI SINI
                System.out.println("[GamePanel] GameClock thread started successfully."); // Tambahkan log
            } catch (IllegalThreadStateException e) {
                System.err.println("[GamePanel] GameClock thread may have already been started: " + e.getMessage());
            }
        } else if (gameClock != null) {
            System.out.println("[GamePanel] GameClock thread was already alive."); // Tambahkan log
        } else {
            System.err.println("[GamePanel] GameClock is null, cannot start."); // Tambahkan log
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

    public void run(){
        double drawInterval = (double) 1000000000 / fps;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (gameThread != null){
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1){
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if (timer >= 1000000000){
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        // PENANGANAN TIDUR OTOMATIS (2 PAGI) & INISIASI TRANSISI TIDUR
        // Cek ini sebelum logika state utama, tapi pastikan tidak mengganggu state non-play seperti title screen
        if (gameState != titleState && gameState != sleepTransitionState && gameClock != null && player != null) {
            // 1. Cek untuk tidur otomatis jam 2 pagi
            if (gameClock.getTime().getHour() == 2 && !player.isCurrentlySleeping()) {
                if (!hasForcedSleepAt2AMToday) {
                    System.out.println("[GamePanel] 2 AM detected. Forcing player to sleep.");
                    player.sleep("It's 2 AM! You pass out \n from staying up too late.");
                    // Player.sleep() akan set player.isCurrentlySleeping(true)
                    // Flag hasForcedSleepAt2AMToday akan di-handle di bawah saat transisi dimulai
                }
            }

            // 2. Jika pemain memulai tidur (atau dipaksa tidur), mulai transisi di GamePanel
            if (player.isCurrentlySleeping() && gameState != sleepTransitionState) {
                System.out.println("[GamePanel] Player initiated sleep. Switching to sleepTransitionState.");
                // previousGameState = gameState; // Simpan state saat ini jika perlu
                gameState = sleepTransitionState;
                gameClock.pauseTime(); // Jeda GameClock segera
                isProcessingNewDayDataInTransition = false; // Reset flag untuk proses data
                sleepTransitionStartTime = System.currentTimeMillis(); // Catat waktu mulai transisi
                hasForcedSleepAt2AMToday = true; // Tandai bahwa siklus tidur malam ini sedang/sudah terjadi
                // (mencegah pemicu 2AM berulang jika transisi lambat)
                // Pesan dialog sudah di-set oleh player.sleep() di gp.ui.currentDialogue
                // UI akan menggambar pesan ini selama sleepTransitionState
                return; // Keluar dari update() agar transisi dimulai bersih di frame berikutnya
            }
        }

        // LOGIKA GAME STATE UTAMA
        if (gameState == playState) {
            // Pastikan GameClock berjalan jika kita di playState dan tidak dalam proses tidur
            if (gameClock != null && gameClock.isPaused()) {
                // Hanya resume jika tidak ada proses tidur yang akan segera dimulai.
                // Flag player.isCurrentlySleeping() sudah ditangani di atas.
                System.out.println("[GamePanel] Resuming GameClock in playState.");
                gameClock.resumeTime();
            }

            if (player.justGotMarried){
                player.justGotMarried = false;
                handleEndOfWeddingEvent();
            }

            player.update();
            for (NPC character : npcs) {
                if (character != null) { // Tambahkan null check untuk keamanan
                    character.update();
                }
            }
        } else if (gameState == sleepTransitionState) {
            if (!isProcessingNewDayDataInTransition) {
                System.out.println("[GamePanel] sleepTransitionState: Processing new day data...");

                Time currentTime = gameClock.getTime();
                currentTime.forceStartNewDay(); // This will ++day and set waktu ke 06:00

                gameClock.updateSeasonBasedOnDay(currentTime.getDay()); // Update musim
                // Update cuaca untuk hari baru
                if ((currentTime.getDay() - 1) % 10 == 0) { // Awal siklus 10 hari (musim)
                    gameClock.getWeather().resetRainyCount();
                }
                gameClock.getWeather().generateNewWeather();

                performDailyResets(); // This now calls processShippingBin() which sets player.goldFromShipping

                // Augment the existing sleep dialogue (from player.sleep()) with shipping earnings
                // ui.currentDialogue is already set by player.sleep()
                if (player.goldFromShipping > 0) {
                    // Ensure ui.currentDialogue is not null and append to it
                    if (ui.currentDialogue == null) ui.currentDialogue = ""; // Safety check
                    if (!ui.currentDialogue.isEmpty()) ui.currentDialogue += "\n"; // Add newline if there's prior text
                    ui.currentDialogue += "You earned " + player.goldFromShipping + "G from shipping.";
                    // player.goldFromShipping is NOT reset here; it's reset in processShippingBin or after message display
                }

                isProcessingNewDayDataInTransition = true; // Tandai data sudah diproses
                System.out.println("[GamePanel] New day data processed. Day: " + currentTime.getDay() +
                        ", Time: " + currentTime.getFormattedTime() +
                        ", Season: " + gameClock.getCurrentSeason() +
                        ", Weather: " + gameClock.getWeather().getWeatherName());
                // Pesan (gp.ui.currentDialogue) akan digambar oleh UI.draw()
            }

            // Tunggu beberapa saat untuk menampilkan pesan tidur
            if (System.currentTimeMillis() - sleepTransitionStartTime >= SLEEP_TRANSITION_MESSAGE_DURATION) {
                System.out.println("[GamePanel] Sleep transition message duration ended. Returning to playState.");

                // goldFromShipping was used to build the dialogue. It can be reset now or in processShippingBin.
                // Since processShippingBin is called daily, it's better if it sets goldFromShipping to 0 after using it for dialogue.
                // For now, let's assume it's reset after being displayed or used.
                // If not, reset it here:
                player.goldFromShipping = 0;


                player.setCurrentlySleeping(false); // Pemain tidak lagi dalam proses tidur
                hasForcedSleepAt2AMToday = false;   // Reset flag untuk malam berikutnya
                isProcessingNewDayDataInTransition = false; // Reset flag untuk tidur berikutnya
                ui.currentDialogue = ""; // Hapus pesan tidur dari UI

                gameState = playState;
                if (gameClock != null && gameClock.isPaused()) {
                    gameClock.resumeTime();
                }
            }
            // Selama state ini, UI.draw() harusnya menampilkan gp.ui.currentDialogue
            // dan mungkin efek fade. Tidak ada update game logic lain.

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
                    resetGameForNewGame(); // Ini akan mereset GameClock juga
                    player.setFarmName(finalFarmName);
                    System.out.println("Farm Name Confirmed: " + player.getFarmName());
                    gameState = playState; // Pindah ke play state
                    // GameClock akan di-resume oleh blok playState di atas
                    // atau jika resetGameForNewGame() mereset GameClock ke kondisi berjalan.
                    // Jika GameClock direset ke paused, pastikan playState meresumenya.
                    if(gameClock != null && gameClock.isPaused()) { // Eksplisit resume jika perlu
                        gameClock.resumeTime();
                    }

                } else {
                    ui.showMessage("Farm name cannot be empty!");
                }
                keyH.enterPressed = false;
            }
        }
        // Pastikan state lain (titleState) tidak memicu update game logic atau GameClock.
    }

    // Di dalam GamePanel.java, atau di kelas yang mengelola event spesifik

    // Contoh penggunaannya dalam sebuah metode event di GamePanel:
    public void handleEndOfWeddingEvent() { // Atau nama event lain
        System.out.println("[GamePanel] Wedding event concluded. Skipping time to 22:00.");

        if (gameClock != null && gameClock.getTime() != null && player != null) {
            gameClock.pauseTime(); // 1. Jeda GameClock agar tidak ada update waktu lain saat kita atur manual

            Time currentTime = gameClock.getTime();
            currentTime.setCurrentTime(22, 0); // 2. Set waktu ke 22:00 pada hari ini

            // 3. Pindahkan pemain ke rumah (jika ini bagian dari event)
            // player.worldX = player.defaultWorldX; // Ganti dengan koordinat rumah/tempat tidur
            // player.worldY = player.defaultWorldY;
            // System.out.println("[GamePanel] Player moved home.");

            // 4. Tampilkan pesan bahwa hari telah berakhir atau event selesai
            ui.showMessage("The day flew by! It's now 10:00 PM."); // Pesan singkat
            // GamePanel.update() akan mendeteksi player.isCurrentlySleeping() dan masuk ke sleepTransitionState.
            // Di sleepTransitionState:
            // - Waktu akan dipaksa ke hari berikutnya jam 6 pagi (via currentTime.forceStartNewDay())
            // - Musim dan cuaca akan diupdate.
            // - Reset harian akan dilakukan.
            // - GameClock akan di-resume setelah transisi selesai.
        } else {
            System.err.println("[GamePanel] Cannot skip time: GameClock, Time, or Player is null.");
            // Jika tidak bisa skip time, mungkin langsung kembalikan ke playState
            // gameState = playState;
            // if (gameClock != null && gameClock.isPaused()) gameClock.resumeTime();
        }
        // Tidak perlu resume gameClock di sini jika player.sleep() dipanggil,
        // karena sleepTransitionState yang akan menanganinya.
    }

    public void processShippingBin() {
        if (player.itemsInShippingBinToday.isEmpty()) {
            player.goldFromShipping = 0; // Ensure it's reset even if nothing shipped
            return;
        }

        int totalEarnings = 0;
        for (Entity itemEntity : player.itemsInShippingBinToday) {
            if (itemEntity instanceof OBJ_Item) { // Ensure it's an OBJ_Item
                OBJ_Item item = (OBJ_Item) itemEntity;
                totalEarnings += item.getSellPrice();
            }
        }

        if (totalEarnings > 0) {
            player.gold += totalEarnings;
            player.goldFromShipping = totalEarnings; // Store for UI message after waking up
            System.out.println("[GamePanel] Player earned " + totalEarnings + "G from shipped items. Total gold: " + player.gold);
        } else {
            player.goldFromShipping = 0; // No earnings
        }
        player.itemsInShippingBinToday.clear(); // Clear the bin
        player.hasUsedShippingBinToday = false; // Reset flag for the new day
        System.out.println("[GamePanel] Shipping bin processed and reset for the new day.");
    }

    /**
     * Melakukan reset harian untuk berbagai entitas dan sistem dalam game.
     * Dipanggil saat transisi ke hari baru (misalnya, setelah pemain tidur).
     */
    public void performDailyResets() {
        if (gameClock == null || gameClock.getTime() == null) {
            System.err.println("[GamePanel] Cannot perform daily resets: GameClock or Time is null.");
            return;
        }
        System.out.println("[GamePanel] Performing daily resets for Day " + gameClock.getTime().getDay() + "...");

        // PROCESS SHIPPING BIN (calculates gold, clears bin, resets daily flag)
        processShippingBin(); // <<< ADD THIS CALL HERE

        // 1. Reset status NPC
        for (NPC npc : npcs) {
            if (npc != null) {
                npc.hasReceivedGiftToday = false;
                // npc.dialogueIndex = 0;
            }
        }
        System.out.println("[GamePanel] NPC daily states reset.");

        // ... (other daily resets like farm plots, shop inventories etc.) ...

        System.out.println("[GamePanel] Daily resets completed.");
    }

    public void resetGameForNewGame() {
        // 1. Reset Player
        player.setFarmName(ui.farmNameInput.trim()); // Set nama farm yang baru diinput setelah default// Berikan item awal
        entities.clear();

        // 2. Reset NPC (jika perlu, misalnya posisi atau dialog awal)
        assetSetter.setNPC(); // Atau metode reset NPC spesifik

        // 3. Reset Objek di map (jika ada yang berubah selama game sebelumnya)
        assetSetter.setObject(); // Atau metode reset objek spesifik

        // 4. Reset Waktu Game (GameClock)
        if (gameClock != null) { // Asumsi Anda punya objek GameClock
            gameClock.resetTime(); // Buat metode reset di GameClock (misal, ke hari 1, musim Spring, jam 06:00)
        } else {
            // Jika tidak ada GameClock, inisialisasi waktu default di sini atau di Player/GamePanel
            // time.reset() atau sejenisnya
        }


        // 5. Reset Kondisi Ladang (jika sudah ada sistem farming)
        // tileM.resetFarmTiles(); // Misalnya

        // 6. Reset UI elemen yang mungkin menyimpan state lama
        ui.currentDialogue = "";
        ui.farmNameInput = ""; // Sudah di-reset saat transisi ke farmNameInputState
        ui.commandNumber = 0; // Reset command di title screen

        // 7. Hentikan musik title screen (jika ada) dan mulai musik play state
        // stopMusic();
        // playMusic(soundEffectMusic); // Ganti dengan indeks musik play state Anda

        System.out.println("Game has been reset for a new game.");
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // TITLE SCREEN
        if (gameState == titleState){
            ui.draw(g2);
        }
        else {
            // TILES
            tileManager.draw(g2);

            entities.sort(new Comparator<Entity>() {
                @Override
                public int compare(Entity o1, Entity o2) {
                    return Integer.compare(o1.worldY, o2.worldY);
                }
            });
            // Entities
            for(Entity entity: entities){
                entity.draw(g2);
            }
            //Player
            player.draw(g2);
            //UI
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