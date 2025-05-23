package spakborhills;

import spakborhills.entity.Entity;
import spakborhills.entity.NPC;
import spakborhills.entity.Player;
import spakborhills.Tile.TileManager;
import spakborhills.enums.EntityType;

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

    public void update(){
        if (gameState == playState){
            player.update(); // Update player
            //NPC
            for(NPC character: npcs){ // Pastikan entities adalah daftar NPC, bukan semua entitas termasuk objek
                character.update();
            }
            if (gameClock != null && gameClock.isPaused()) { // Jika game masuk playState dan clock masih pause
                gameClock.resumeTime();
            }
        } else if (gameState == pauseState){
            // nothing, waktu sudah di-pause oleh KeyHandler
        } else if (gameState == dialogueState) {
            // Waktu mungkin di-pause saat dialog dimulai (misalnya di Player.interactNPC)
            // dan di-resume saat dialog selesai (di KeyHandler atau Player.interactNPC)
        } else if (gameState == inventoryState) {
            // Waktu di-pause oleh KeyHandler saat inventaris dibuka
        } else if (gameState == farmNameInputState) {
            // Tidak ada update game logic yang signifikan di sini selain menunggu input.
            // GameClock idealnya di-pause di state ini.
            if (this.gameClock != null && !this.gameClock.isPaused()) {
                this.gameClock.pauseTime(); // Pastikan waktu dijeda
            }

            if (keyH.enterPressed) { // Jika Enter ditekan untuk konfirmasi nama farm
                String finalFarmName = ui.farmNameInput.trim();
                if (!finalFarmName.isEmpty()) {
                    // Pindahkan logika player.setFarmName setelah resetGameForNewGame
                    // agar tidak tertimpa oleh setDefaultValues() di dalam reset.

                    resetGameForNewGame(); // Reset SEMUA state game untuk NEW GAME
                    player.setFarmName(finalFarmName); // Set nama farm setelah player direset

                    System.out.println("Farm Name Confirmed: " + player.getFarmName()); // Untuk Debug
                    gameState = playState; // Pindah ke play state

                    // Mulai/Lanjutkan musik dan GameClock
                    if(gameClock != null) {
                        // gameClock.start() HANYA dipanggil sekali saat game pertama kali setup.
                        // Jika gameClock sudah pernah start dan sekarang di-pause, gunakan resumeTime.
                        if (gameClock.isAlive() && gameClock.isPaused()) {
                            gameClock.resumeTime();
                        } else if (!gameClock.isAlive()) {
                            // Ini seharusnya tidak terjadi jika gameClock sudah di-start di startGameThread
                            System.err.println("GameClock was not alive. Attempting to start.");
                            try {
                                gameClock.start();
                            } catch (IllegalThreadStateException e) {
                                System.err.println("GameClock already started: " + e.getMessage());
                            }
                        }
                    }
                } else {
                    ui.showMessage("Farm name cannot be empty!");
                }
                keyH.enterPressed = false; // Reset flag enter setelah diproses di sini
            }
        }
        else if (gameState == interactionMenuState) {
            // Biasanya hanya menunggu input, game logic utama di-pause
            if (this.gameClock != null && !this.gameClock.isPaused()) {
                this.gameClock.pauseTime();
            }
        } else if (gameState == giftSelectionState) {
            // Mirip inventoryState, waktu di-pause
            if (this.gameClock != null && !this.gameClock.isPaused()) {
                this.gameClock.pauseTime();
            }
        }
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