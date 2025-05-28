package spakborhills;

import spakborhills.cooking.Recipe;
import spakborhills.cooking.RecipeManager;
import spakborhills.entity.Entity;
import spakborhills.entity.NPC;
import spakborhills.object.OBJ_Item;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UI {
    public int mapSelectionState = 0;
    GamePanel gp;
    GameClock gameClock;
    Font silkScreen, pressStart;
    public boolean messageOn = false;
    public String message = "";
    int messageCounter = 0;
    public boolean gameFinished = false;
    Graphics2D g2;
    double playTime;
    public String currentDialogue = "";
    public int commandNumber = 0;
    boolean isSelectingGift = false;
    public Entity itemSelectedByEnter = null;

    public int npcMenuCommandNum = 0;
    public int inventoryCommandNum = 0;
    public int inventorySlotCol = 0;
    public int inventorySlotRow = 0;

    public Entity focusedItem = null;
    public int inventorySubstate = 0;

    // PLAYER NAME INPUT
    public String playerNameInput = ""; // Menyimpan teks input nama pemain
    private String playerNamePromptMessage = "Enter Your Name:";
    private String playerNameSubMessage = "(Press ENTER to confirm, BACKSPACE to delete)";
    public int playerNameMaxLength = 15; // Batas maksimal karakter nama pemain

    // FARM NAME
    public String farmNameInput = ""; // Menyimpan teks input nama farm
    private String farmNamePromptMessage = "Enter Your Farm's Name:";
    private String farmNameSubMessage = "(Press ENTER to confirm, BACKSPACE to delete)";
    public int farmNameMaxLength = 20; // Batas maksimal karakter nama farm

    // TITLE BACKGROUND
    BufferedImage titleScreenBackground;

    //COOKING
    public int cookingCommandNum = 0;
    public int cookingSubState = 0;

    public UI(GamePanel gp, GameClock gameClock){
        this.gp = gp;
        this.gameClock = gameClock;

        InputStream inputStream = getClass().getResourceAsStream("/fonts/SilkscreenRegular.ttf");
        try {
            silkScreen = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            inputStream = getClass().getResourceAsStream("/fonts/PressStart2PRegular.ttf");
            pressStart = Font.createFont(Font.TRUETYPE_FONT, inputStream);
        } catch (FontFormatException | IOException e){
            System.out.println(e.getMessage());
        }
        loadTitleScreenImage();
    }

    public void showMessage(String text){
        message = text;
        messageOn = true;
    }

    public void loadTitleScreenImage(){
        try {
            InputStream inputStream = getClass().getResourceAsStream("/background/title_screen.png");

            if (inputStream != null){
                titleScreenBackground = ImageIO.read(inputStream);
            }
            else{
                System.err.println("Cannot load the title screen!");
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;

        // Set rendering hints dan font default di awal jika belum
        if (pressStart != null) {
            g2.setFont(pressStart);
        } else {
            g2.setFont(new Font("Arial", Font.PLAIN, 20)); // Font fallback umum
        }
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.white); // Warna default untuk teks, bisa di-override per komponen

        // Menggunakan struktur if-else if
        if (gp.gameState == gp.titleState) {
            drawTitleScreen();
        }
        // PLAYER NAME INPUT STATE
        else if (gp.gameState == gp.playerNameInputState) {
            drawPlayerNameInputScreen();
            drawTimedMessage(g2);
        }
        // FARM NAME INPUT STATE
        else if (gp.gameState == gp.farmNameInputState) {
            drawFarmNameInputScreen();
            drawTimedMessage(g2);
        }
        // SLEEP TRANSITION STATE  <-- TAMBAHKAN INI
        else if (gp.gameState == gp.sleepTransitionState) {
            drawSleepTransitionScreen(g2);
            drawTimedMessage(g2);
        }
        // PLAY STATE
        else if (gp.gameState == gp.playState) {
            drawTimeHUD(g2);
            drawLocationHUD(g2);
            drawEnergyBar(g2);
            drawPlayerGold();
            // Jika ada pesan singkat yang ingin ditampilkan (messageOn)
            if (messageOn) {
                g2.setFont(g2.getFont().deriveFont(20F)); // Sesuaikan ukuran font pesan
                g2.setColor(Color.YELLOW); // Warna pesan
                int messageX = gp.tileSize / 2;
                int messageY = gp.tileSize * 2; // Posisi pesan di bawah energy bar
                g2.drawString(message, messageX, messageY);

                messageCounter++;
                if (messageCounter > 120) { // Tampilkan pesan selama 2 detik (60fps * 2)
                    messageCounter = 0;
                    messageOn = false;
                    message = "";
                }
            }
        }
        // PAUSE STATE
        else if (gp.gameState == gp.pauseState) {
            drawPauseScreen();
        }
        // DIALOGUE STATE
        else if (gp.gameState == gp.dialogueState) {
            // Gambar HUD dasar seperti waktu dan energi dulu jika diinginkan
            // drawTimeHUD(g2);
            // drawEnergyBar(g2);
            drawDialogueScreen(); // Kemudian gambar window dialog di atasnya
        }
        // INVENTORY STATE / GIFT SELECTION
        else if (gp.gameState == gp.inventoryState || gp.gameState == gp.giftSelectionState) {
            // drawTimeHUD(g2);
            // drawEnergyBar(g2);
            drawInventoryScreen();
            drawTimedMessage(g2);
        }
        // NPC INTERACTION MENU
        else if (gp.gameState == gp.interactionMenuState) {
            // drawTimeHUD(g2);
            // drawEnergyBar(g2);
            drawNPCInteractionMenu();
        }
        // Pastikan semua state lain sudah tertangani atau tidak memerlukan penggambaran khusus
        // SELL STATE
        else if (gp.gameState == gp.sellState){
            drawSellScreen();
            drawTimedMessage(g2);
        }

        // COOKING STATE
        else if (gp.gameState == gp.cookingState){
            drawCookingScreen();
            drawTimedMessage(g2);
        }   else if (gp.gameState == gp.fishingMinigameState) { // <-- BLOK BARU
            drawFishingMinigameScreen(g2);
        }
    }

    // Tambahkan metode baru di kelas UI.java
    public void drawFishingMinigameScreen(Graphics2D g2) {
        // 1. Gambar window latar belakang (mirip drawDialogueScreen atau drawSubWindow)
        int frameX = gp.tileSize * 3;
        int frameY = gp.screenHeight / 2 - gp.tileSize * 3; // Posisikan di tengah agak ke atas
        int frameWidth = gp.screenWidth - (gp.tileSize * 6);
        int frameHeight = gp.tileSize * 6; // Cukup untuk beberapa baris teks
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        g2.setColor(Color.white);
        Font baseFont = pressStart != null ? pressStart : new Font("Arial", Font.PLAIN, 12);
        int currentTextY = frameY + gp.tileSize; // Y awal untuk teks di dalam window

        // 2. Tampilkan Informasi Minigame (dari Player)
        g2.setFont(baseFont.deriveFont(Font.PLAIN, 14F)); // Ukuran font untuk info
        if (gp.player.fishingInfoMessage != null && !gp.player.fishingInfoMessage.isEmpty()) {
            // Word wrapping sederhana untuk fishingInfoMessage
            List<String> infoLines = wrapText(gp.player.fishingInfoMessage, frameWidth - gp.tileSize, g2.getFontMetrics());
            for (String line : infoLines) {
                int lineX = getXForCenteredTextInFrame(line, frameX, frameWidth);
                g2.drawString(line, lineX, currentTextY);
                currentTextY += g2.getFontMetrics().getHeight() + 2;
            }
        }
        currentTextY += gp.tileSize / 2; // Spasi sebelum input

        // 3. Tampilkan Input Pemain Saat Ini (dari Player)
        g2.setFont(baseFont.deriveFont(Font.BOLD, 16F)); // Ukuran font untuk input
        String displayInput = gp.player.fishingPlayerInput;
        if (System.currentTimeMillis() % 1000 < 500) { // Kursor berkedip
            displayInput += "_";
        } else {
            displayInput += " ";
        }
        int inputX = getXForCenteredTextInFrame(displayInput, frameX, frameWidth);
        g2.drawString(displayInput, inputX, currentTextY);
        currentTextY += gp.tileSize;

        // 4. Tampilkan Feedback (dari Player)
        g2.setFont(baseFont.deriveFont(Font.ITALIC, 14F)); // Ukuran font untuk feedback
        if (gp.player.fishingFeedbackMessage != null && !gp.player.fishingFeedbackMessage.isEmpty()) {
            int feedbackX = getXForCenteredTextInFrame(gp.player.fishingFeedbackMessage, frameX, frameWidth);
            g2.drawString(gp.player.fishingFeedbackMessage, feedbackX, currentTextY);
        }
        currentTextY += gp.tileSize / 2;

        // 5. Tampilkan Sisa Percobaan
        g2.setFont(baseFont.deriveFont(Font.PLAIN, 12F));
        int attemptsLeft = gp.player.fishingMaxTry - gp.player.fishingCurrentAttempts;
        String attemptsText = "Percobaan tersisa: " + attemptsLeft + "/" + gp.player.fishingMaxTry;
        int attemptsX = frameX + gp.tileSize / 2; // Rata kiri
        g2.drawString(attemptsText, attemptsX, frameY + frameHeight - gp.tileSize + 15 );

        // 6. Instruksi
        g2.setFont(baseFont.deriveFont(Font.PLAIN, 10F));
        String instructions = "[0-9] Angka | [Enter] Tebak | [Backspace] Hapus | [Esc] Menyerah";
        int instructionX = getXForCenteredTextInFrame(instructions, frameX, frameWidth);
        g2.drawString(instructions, instructionX, frameY + frameHeight - gp.tileSize / 2 );
    }

    // Helper method untuk word wrapping (bisa ditaruh di UI.java)
    private List<String> wrapText(String text, int maxWidth, FontMetrics fm) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return lines;
        }
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        for (String word : words) {
            if (fm.stringWidth(currentLine.toString() + word) < maxWidth) {
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            } else {
                if (currentLine.length() > 0) { // Pastikan ada sesuatu di baris sebelum menambahkan
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else { // Jika satu kata saja sudah terlalu panjang
                    lines.add(word); // Tambahkan apa adanya (mungkin perlu pemotongan lebih lanjut)
                    currentLine = new StringBuilder();
                }
            }
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        return lines;
    }

    public void drawSharedBackground(Graphics2D g2) {
        if (titleScreenBackground != null) {
            g2.drawImage(titleScreenBackground, 0, 0, gp.screenWidth, gp.screenHeight, null);
        } else {
            // Fallback jika gambar tidak terload
            g2.setColor(new Color(7, 150, 255)); // Warna biru seperti di title screen
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }
    }

    public void drawPlayerGold(){
        int x = gp.tileSize / 2;
        int y = gp.tileSize * 2;
        g2.setFont(pressStart.deriveFont(Font.BOLD, 15F));
        g2.setColor(Color.white);
        InputStream inputStream = getClass().getResourceAsStream("/objects/gold.png");
        BufferedImage coinImage;
        try{
            coinImage = ImageIO.read(inputStream);
            g2.drawImage(coinImage, x, y - gp.tileSize/2, gp.tileSize/2, gp.tileSize/2, null);
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
        x += gp.tileSize/2 + 5;
        g2.drawString("Gold: " + gp.player.gold, x, y);
    }

    public void drawTitleScreen(){
        if (mapSelectionState == 0) {
            if (titleScreenBackground != null){
                g2.drawImage(titleScreenBackground, 0, 0, gp.screenWidth, gp.screenHeight, null);
            }
            else{
                g2.setColor(new Color(7, 150, 255));
                g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
            }

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 50F));
            String text = "SPAKBOR HILL'S";
            int x = getXForCenteredText(text);
            int y = gp.tileSize * 3;

            // SHADOWS
            g2.setColor(Color.black);
            g2.drawString(text, x+5, y+5);
            // DRAW TITLE TO SCREEN
            g2.setColor(Color.white);
            g2.drawString(text, x, y);

            // MENU
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 30F));

            text = "NEW GAME";
            x = getXForCenteredText(text);
            y += gp.tileSize * 4;
            g2.setColor(Color.black);
            g2.drawString(text, x+5, y+5);
            g2.setColor(Color.white);
            g2.drawString(text, x, y);
            if (commandNumber == 0){
                g2.drawString(">", x-gp.tileSize, y);
                if (gp.keyH.enterPressed){
                    gp.gameState = gp.playerNameInputState;
                    playerNameInput = "";
                    gp.keyH.enterPressed = false;
                    if (gp.gameClock != null && !gp.gameClock.isPaused()){
                        gp.gameClock.pauseTime();
                    }
                }
            }

            text = "LOAD GAME";
            x = getXForCenteredText(text);
            y += gp.tileSize;
            g2.setColor(Color.black);
            g2.drawString(text, x+5, y+5);
            g2.setColor(Color.white);
            g2.drawString(text, x, y);
            if (commandNumber == 1){
                g2.drawString(">", x-gp.tileSize, y);
            }

            text = "QUIT";
            x = getXForCenteredText(text);
            y += gp.tileSize;
            g2.setColor(Color.black);
            g2.drawString(text, x+5, y+5);
            g2.setColor(Color.white);
            g2.drawString(text, x, y);
            if (commandNumber == 2){
                g2.drawString(">", x-gp.tileSize, y);
            }
        }
        else if (mapSelectionState == 1){
            drawSharedBackground(g2); // 1. Gambar background utama (gambar game Anda)

            // 2. TAMBAHKAN OVERLAY HITAM TRANSPARAN DI SINI
            g2.setColor(new Color(0, 0, 0, 200)); // Nilai alpha (200) bisa disesuaikan
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

            // 3. Lanjutkan dengan menggambar elemen UI untuk pemilihan peta
            g2.setColor(Color.white); // Set warna untuk teks di atas overlay
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20F)); // Ukuran font yang lebih besar untuk judul "World Map"

            String text = "World Map";
            int x = getXForCenteredText(text);
            int y = gp.tileSize * 2; // Naikkan sedikit posisi Y untuk judul
            g2.drawString(text, x, y);

            // Opsi peta
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 12F)); // Font lebih kecil untuk opsi peta
            y = gp.tileSize * 4; // Atur posisi Y awal untuk opsi peta

            // Abigail's House
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 12F));

            text = "Abigail's";
            int x1 = gp.tileSize*3/2;
            y += gp.tileSize * 2;
            g2.drawString(text, x1, y);
            y += gp.tileSize/4;
            text = "House";
            g2.drawString(text, x1, y);
            y -= gp.tileSize/4;
            if (commandNumber == 0){
                g2.drawString(">", x1-gp.tileSize/4, y+gp.tileSize/8);
            }

            text = "Caroline's";
            int x2 = x1 + gp.tileSize*3;
            g2.drawString(text, x2, y);
            text = "House";
            y += gp.tileSize/4;
            g2.drawString(text, x2, y);
            y -= gp.tileSize/4;
            if (commandNumber == 1){
                g2.drawString(">", x2-gp.tileSize/4, y+gp.tileSize/8);
            }

            text = "Dasco's";
            int x3 = x2 + gp.tileSize*3;
            g2.drawString(text, x3, y);
            text = "House";
            y += gp.tileSize/4;
            g2.drawString(text, x3, y);
            y -= gp.tileSize/4;
            if (commandNumber == 2){
                g2.drawString(">", x3-gp.tileSize/4, y+gp.tileSize/8);
            }

            text = "MayorTadi's";
            int x4 = x3 + gp.tileSize*3;
            g2.drawString(text, x4, y);
            text = "House";
            y += gp.tileSize/4;
            g2.drawString(text, x4, y);
            y -= gp.tileSize/4;
            if (commandNumber == 3){
                g2.drawString(">", x4-gp.tileSize/4, y+gp.tileSize/8);
            }

            text = "Perry's";
            int x5 = x4 + gp.tileSize*3;
            g2.drawString(text, x5, y);
            text = "House";
            y += gp.tileSize/4;
            g2.drawString(text, x5, y);
            y -= gp.tileSize/4;
            if (commandNumber == 4){
                g2.drawString(">", x5-gp.tileSize/4, y+gp.tileSize/8);
            }

            text = "Store";
            x = x1;
            y += gp.tileSize * 2;
            g2.drawString(text, x, y);
            if (commandNumber == 5){
                g2.drawString(">", x-gp.tileSize/4, y);
            }

            text = "Farm";
            x = x2;
            g2.drawString(text, x, y);
            if (commandNumber == 6){
                g2.drawString(">", x-gp.tileSize/4, y);
            }

            text = "Forest";
            x = x3;
            g2.drawString(text, x, y);
            text = "River";
            y += gp.tileSize/4;
            g2.drawString(text, x, y);
            y -= gp.tileSize/4;
            if (commandNumber == 7){
                g2.drawString(">", x-gp.tileSize/4, y);
            }

            text = "Mountain";
            x = x4;
            g2.drawString(text, x, y);
            text = "Lake";
            y += gp.tileSize/4;
            g2.drawString(text, x, y);
            y -= gp.tileSize/4;
            if (commandNumber == 8){
                g2.drawString(">", x-gp.tileSize/4, y);
            }

            text = "Ocean";
            x = x5;
            g2.drawString(text, x, y);
            if (commandNumber == 9){
                g2.drawString(">", x-gp.tileSize/4, y);
            }

            text = "Player's House";
            x = getXForCenteredText(text);
            y += gp.tileSize * 2;
            g2.drawString(text, x, y);
            if (commandNumber == 10){
                g2.drawString(">", x-gp.tileSize/4, y);
            }
        }

    }

    public void drawPauseScreen(){
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 80f));
        String text = "PAUSED";
        int x = getXForCenteredText(text);
        int y = gp.screenHeight / 2;
        g2.drawString(text, x, y);
    }

    public void drawPlayerNameInputScreen() {
        drawSharedBackground(g2);
        // Latar belakang (bisa sama dengan title screen atau warna solid)
        g2.setColor(new Color(0, 0, 0, 200)); // Warna hitam semi-transparan
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(pressStart.deriveFont(Font.PLAIN, 30F)); // Gunakan font Anda
        g2.setColor(Color.white);

        // Pesan Prompt
        int x = getXForCenteredText(playerNamePromptMessage);
        int y = gp.screenHeight / 2 - gp.tileSize * 2;
        g2.drawString(playerNamePromptMessage, x, y);

        // Kotak Input Teks (atau hanya teks yang diketik)
        String displayText = playerNameInput;
        // Tambahkan kursor berkedip sederhana
        if (System.currentTimeMillis() % 1000 < 500) { // Setiap 0.5 detik
            displayText += "_";
        } else {
            displayText += " "; // Beri spasi agar lebar konsisten saat tidak ada kursor
        }

        g2.setFont(pressStart.deriveFont(Font.PLAIN, 28F)); // Gunakan font Anda
        int textWidth = (int) g2.getFontMetrics().getStringBounds(displayText, g2).getWidth();
        x = gp.screenWidth / 2 - textWidth / 2;
        y += gp.tileSize * 2;
        g2.drawString(displayText, x, y);

        // Pesan Sub/Instruksi
        g2.setFont(pressStart.deriveFont(Font.PLAIN, 16F)); // Gunakan font Anda
        x = getXForCenteredText(playerNameSubMessage);
        y += gp.tileSize * 1.5;
        g2.drawString(playerNameSubMessage, x, y);
    }

    public void drawFarmNameInputScreen() {
        drawSharedBackground(g2);
        // Latar belakang (bisa sama dengan title screen atau warna solid)
        g2.setColor(new Color(0, 0, 0, 200)); // Warna hitam semi-transparan
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(pressStart.deriveFont(Font.PLAIN, 30F));
        g2.setColor(Color.white);

        // Pesan Prompt
        int x = getXForCenteredText(farmNamePromptMessage);
        int y = gp.screenHeight / 2 - gp.tileSize * 2;
        g2.drawString(farmNamePromptMessage, x, y);

        // Kotak Input Teks (atau hanya teks yang diketik)
        String displayText = farmNameInput;
        // Tambahkan kursor berkedip sederhana
        if (System.currentTimeMillis() % 1000 < 500) { // Setiap 0.5 detik
            displayText += "_";
        } else {
            displayText += " "; // Beri spasi agar lebar konsisten saat tidak ada kursor
        }

        g2.setFont(pressStart.deriveFont(Font.PLAIN, 28F));
        int textWidth = (int) g2.getFontMetrics().getStringBounds(displayText, g2).getWidth();
        x = gp.screenWidth / 2 - textWidth / 2;
        y += gp.tileSize * 2;
        g2.drawString(displayText, x, y);

        // Pesan Sub/Instruksi
        g2.setFont(pressStart.deriveFont(Font.PLAIN, 16F));
        x = getXForCenteredText(farmNameSubMessage);
        y += gp.tileSize * 1.5;
        g2.drawString(farmNameSubMessage, x, y);
    }

    public void drawDialogueScreen() {
        // WINDOW
        int x = gp.tileSize * 2;
        int y = gp.tileSize / 2;
        int width = gp.screenWidth - (gp.tileSize * 4);
        int height = gp.tileSize * 4; // Tinggi window dialog

        drawSubWindow(x, y, width, height);

        // TEXT SETTINGS
        g2.setColor(Color.white); // Warna teks
        // Gunakan font yang sudah diinisialisasi
        if (pressStart != null) {
            g2.setFont(pressStart.deriveFont(Font.PLAIN, 20F));
        } else {
            g2.setFont(new Font("Arial", Font.PLAIN, 20)); // Fallback jika pressStart gagal load
        }


        int textPadding = gp.tileSize / 2; // Padding di dalam subWindow
        int dialogueContentX = x + textPadding;
        int currentY = y + textPadding;    // Y awal untuk baris pertama teks

        FontMetrics fm = g2.getFontMetrics();
        currentY += fm.getAscent(); // Menyesuaikan Y awal agar teks tidak terlalu menempel ke atas

        int lineHeight = fm.getHeight() + 5; // Jarak antar baris, tambahkan sedikit spasi jika perlu
        // fm.getHeight() sudah termasuk ascent, descent, dan leading

        // Menggambar Nama NPC jika ada
        String npcNamePrefix = "";
        if (gp.currentInteractingNPC != null && gp.currentInteractingNPC.name != null && !gp.currentInteractingNPC.name.isEmpty()) {
            npcNamePrefix = gp.currentInteractingNPC.name + ": ";
             Font currentFont = g2.getFont();
             g2.setFont(currentFont.deriveFont(Font.BOLD));
             g2.drawString(npcNamePrefix, dialogueContentX, currentY);
             g2.setFont(currentFont); // Kembalikan font
             currentY += lineHeight; // Pindah ke bawah untuk dialognya
            // Jika nama NPC ingin berada di baris yang sama dengan awal dialog, gabungkan saja:
        }

        // TEXT WRAPPING LOGIC
        int maxTextWidth = width - (2 * textPadding);
        String textToDisplay =  currentDialogue; // Gabungkan nama NPC dengan dialognya

        String[] words = textToDisplay.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            // Cek apakah kata ini mengandung \n (untuk pemaksaan baris baru manual)
            if (word.contains("\n")) {
                String[] subWords = word.split("\n", -1); // -1 untuk menjaga token kosong jika \n di akhir
                for (int i = 0; i < subWords.length; i++) {
                    String subWord = subWords[i];
                    // Coba tambahkan subWord ke baris saat ini
                    String testLineWithSubWord = currentLine.toString() + (currentLine.length() > 0 && !subWord.isEmpty() ? " " : "") + subWord;

                    if (!subWord.isEmpty() && fm.stringWidth(testLineWithSubWord) <= maxTextWidth) {
                        if (currentLine.length() > 0 && !subWord.isEmpty()) currentLine.append(" ");
                        currentLine.append(subWord);
                    } else {
                        // Gambar baris sebelumnya jika ada isinya
                        if (currentLine.length() > 0) {
                            g2.drawString(currentLine.toString(), dialogueContentX, currentY);
                            currentY += lineHeight;
                        }
                        currentLine = new StringBuilder(subWord); // subWord memulai baris baru
                        // Jika subWord sendiri sudah melebihi lebar, ia akan digambar apa adanya (dan mungkin keluar)
                        // Penanganan kata yang terlalu panjang lebih kompleks (misalnya, memotong kata)
                        if (fm.stringWidth(currentLine.toString()) > maxTextWidth && currentLine.length() > 0){
                            g2.drawString(currentLine.toString(), dialogueContentX, currentY);
                            currentY += lineHeight;
                            currentLine = new StringBuilder();
                        }
                    }

                    // Jika ini adalah hasil split dari \n (dan bukan bagian terakhir), paksa gambar baris dan pindah
                    if (i < subWords.length - 1) {
                        if (currentLine.length() > 0) {
                            g2.drawString(currentLine.toString(), dialogueContentX, currentY);
                        }
                        currentY += lineHeight;
                        currentLine = new StringBuilder();
                    }
                }
            } else { // Proses kata biasa tanpa newline manual
                String testLine = currentLine.toString() + (!currentLine.isEmpty() ? " " : "") + word;
                if (fm.stringWidth(testLine) <= maxTextWidth) {
                    if (!currentLine.isEmpty()) currentLine.append(" ");
                    currentLine.append(word);
                } else {
                    // Gambar baris saat ini
                    if (!currentLine.isEmpty()) {
                        g2.drawString(currentLine.toString(), dialogueContentX, currentY);
                    }
                    currentY += lineHeight; // Pindah ke baris berikutnya
                    currentLine = new StringBuilder(word); // Mulai baris baru dengan kata ini

                    // Jika kata pertama di baris baru sudah melebihi lebar
                    if (fm.stringWidth(currentLine.toString()) > maxTextWidth && currentLine.length() > 0){
                        g2.drawString(currentLine.toString(), dialogueContentX, currentY);
                        currentY += lineHeight;
                        currentLine = new StringBuilder();
                    }
                }
            }

            // Hentikan jika teks melebihi tinggi kotak dialog
            if (currentY > y + height - textPadding - fm.getDescent()) {
                // Bisa tambahkan indikator "..." jika teks terpotong
                if (!currentLine.isEmpty()) { // Gambar sisa terakhir yang mungkin masih muat sebagian
                    g2.drawString(currentLine.toString().trim() + "...", dialogueContentX, currentY);
                } else if (words[words.length-1] != word) { // Jika bukan kata terakhir yang menyebabkan overflow
                    String lastDrawnLine = g2.getFontMetrics().toString(); // Ini tidak benar, perlu cara lain ambil line terakhir
                    // Untuk simpelnya, kita bisa tambahkan ... di akhir baris terakhir yang berhasil digambar sebelumnya
                    // Ini agak tricky tanpa menyimpan state baris sebelumnya.
                }
                break;
            }
        }

        // Gambar sisa baris terakhir jika masih ada dan belum melebihi tinggi
        if (currentLine.length() > 0 && currentY <= y + height - textPadding - fm.getDescent()) {
            g2.drawString(currentLine.toString(), dialogueContentX, currentY);
        }
    }

    public void drawSubWindow(int x, int y, int width, int height){
        Color c = new Color(0, 0 ,0, 210);
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height,35, 35);

        c = new Color(255, 255, 255);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x+5, y+5, width-10, height-10, 25, 25);
    }
    public int getXForCenteredText(String text){
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth() ;
        return gp.screenWidth / 2 - length / 2;
    }

    public void drawEnergyBar(Graphics2D g2){
        int x = gp.tileSize / 2;
        int y = gp.tileSize / 2;
        int segmentWidth = gp.tileSize / 2;
        int segmenHeight = gp.tileSize / 2 - 5;
        int segmentSpacing = 3;
        int totalSegments = 10;

        int filledSegments = 0;
        if (gp.player.MAX_POSSIBLE_ENERGY > 0){
            double energyPerSegments = (double) gp.player.MAX_POSSIBLE_ENERGY / totalSegments;
            if (energyPerSegments > 0){
                filledSegments = (int) (gp.player.currentEnergy / energyPerSegments);
            }
        }
        g2.setFont(silkScreen);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 25f));
        g2.setColor(Color.black);
        FontMetrics fmLabel = g2.getFontMetrics(silkScreen);
        String labelText = "Energy";
        g2.drawString(labelText, x, y - fmLabel.getDescent());
        y += 5;

        for (int i = 0; i < totalSegments; i++){
            int currentSegmentX = x + (i * (segmentWidth + segmentSpacing));

            g2.setColor(Color.black);
            g2.fillRect(currentSegmentX - 1, y - 1, segmentWidth + 2, segmenHeight + 2);
            g2.setColor(Color.gray);
            g2.fillRect(currentSegmentX, y, segmentWidth, segmenHeight);

            if (i < filledSegments){
                Color segmentColor = getColor(i, totalSegments);

                g2.setColor(segmentColor);
                g2.fillRect(currentSegmentX, y, segmentWidth, segmenHeight);
            }
            g2.setColor(Color.white);
            g2.drawRect(currentSegmentX, y, segmentWidth, segmenHeight);
        }
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 15F));
        String energyText = gp.player.currentEnergy + "/" + gp.player.MAX_POSSIBLE_ENERGY;
        g2.setColor(Color.BLACK);
        int segmentsBarTotalWidth = totalSegments * (segmentWidth + segmentSpacing) - segmentSpacing;
        FontMetrics fmText = g2.getFontMetrics(silkScreen);
        int textHeightOffset = (segmenHeight - fmText.getAscent() - fmText.getDescent()) / 2 + fmText.getAscent();
        g2.drawString(energyText, x + segmentsBarTotalWidth + 10, y + textHeightOffset);
    }

    private Color getColor(int i, int totalSegments) {
        Color segmentColor;
        double currentEnergyPercentage = (double) gp.player.currentEnergy / gp.player.MAX_POSSIBLE_ENERGY;

        if (currentEnergyPercentage > 0.6){
            segmentColor = new Color(0, 200, 0);
        }
        else if (currentEnergyPercentage > 0.3){
            segmentColor = new Color(255, 200, 0);
        }
        else{
            segmentColor = new Color(200, 0 ,0);
        }
        return segmentColor;
    }

    public int getXForInventoryTitle(String text, int frameX, int frameWidth){
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth() ;
        return frameX + (frameWidth / 2) - (length / 2);
    }

    public int getXForCenteredTextInFrame(String text, int frameX, int frameWidth){
        if (g2 == null || text == null) return frameX; // Basic safety check
        FontMetrics fm = g2.getFontMetrics();
        int length = fm.stringWidth(text);
        return frameX + (frameWidth - length) / 2;
    }

    public void drawInventoryScreen() {
        int frameX = gp.tileSize * 2;
        int frameY = gp.tileSize;
        int frameWidth = gp.screenWidth - (gp.tileSize * 4);
        int frameHeight = gp.tileSize * 10; // Tinggi frame bisa disesuaikan
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(24F));
        g2.drawString("INVENTORY", getXForInventoryTitle("INVENTORY", frameX, frameWidth), frameY + gp.tileSize - 10);

        // Pengaturan Slot Awal
        final int slotStartX = frameX + gp.tileSize / 2;
        final int slotStartY = frameY + gp.tileSize;
        int currentSlotX = slotStartX;
        int currentSlotY = slotStartY;
        final int slotSize = gp.tileSize + 10;
        final int slotGap = 5;
        final int itemsPerRow = (frameWidth - gp.tileSize) / (slotSize + slotGap);

        int currentItemIndex = 0;




        for (Entity item : gp.player.inventory) {
            if (currentSlotY + slotSize > frameY + frameHeight - gp.tileSize / 2) {
                g2.setColor(Color.white);
                g2.drawString("...", currentSlotX, currentSlotY + slotSize / 2);
                break;
            }

            g2.setColor(new Color(100, 100, 100, 150)); // Warna slot
            g2.fillRoundRect(currentSlotX, currentSlotY, slotSize, slotSize, 10, 10);
            g2.setColor(Color.white);
            g2.drawRoundRect(currentSlotX, currentSlotY, slotSize, slotSize, 10, 10);

            // Gambar ikon item
            if (item.down1 != null) {
                g2.drawImage(item.down1, currentSlotX + 5, currentSlotY + 5, gp.tileSize, gp.tileSize, null);
            } else if (item.image != null) { // Fallback jika down1 tidak ada tapi image ada
                g2.drawImage(item.image, currentSlotX + 5, currentSlotY + 5, gp.tileSize, gp.tileSize, null);
            }

            // Tandai slot yang dipilih berdasarkan inventoryCommandNum
            // inventoryCommandNum sekarang harus merujuk ke indeks dalam gp.player.inventory
            if (currentItemIndex == inventoryCommandNum) {
                g2.setColor(Color.yellow);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(currentSlotX - 2, currentSlotY - 2, slotSize + 4, slotSize + 4, 12, 12);
                g2.setStroke(new BasicStroke(1)); // Reset stroke

                // Tampilkan nama item yang dipilih
                g2.setColor(Color.white); // Set ulang warna setelah highlight
                g2.setFont(g2.getFont().deriveFont(18F));
                int itemInfoY = frameY + frameHeight + gp.tileSize / 2; // Posisi di bawah frame
                g2.drawString("Item: " + item.name, slotStartX, itemInfoY);

                // kalo mau menambahkan deskripsi atau detail lain di sini:
                // if (item.description != null) {
                //     g2.drawString(item.description, slotStartX, itemInfoY + 20);
                // }
            }

            // Pindah ke posisi slot berikutnya
            currentSlotX += slotSize + slotGap;
            if ((currentItemIndex + 1) % itemsPerRow == 0) { // Jika item berikutnya adalah awal baris baru
                currentSlotX = slotStartX;  // Kembali ke kolom pertama
                currentSlotY += slotSize + slotGap; // Pindah ke baris baru
            }

            currentItemIndex++; // Increment indeks item saat ini
        }

        // Jika inventaris kosong atau tidak ada item yang dipilih (misalnya, commandNum di luar jangkauan)
        // Bagian ini perlu disesuaikan karena info item kini digambar di dalam loop jika slot terpilih.
        if (gp.player.inventory.isEmpty()) {
            g2.setColor(Color.white);
            g2.setFont(g2.getFont().deriveFont(18F));
            int itemInfoY = frameY + frameHeight + gp.tileSize / 2;
            g2.drawString("Inventory is Empty", slotStartX, itemInfoY);
        } else if (inventoryCommandNum < 0 || inventoryCommandNum >= gp.player.inventory.size()) {
            // Jika kursor menunjuk ke luar jangkauan item yang ada (seharusnya tidak sering terjadi dengan navigasi yang benar)
            g2.setColor(Color.white);
            g2.setFont(g2.getFont().deriveFont(18F));
            int itemInfoY = frameY + frameHeight + gp.tileSize / 2;
            g2.drawString("Select Item...", slotStartX, itemInfoY);
        }
    }

    public void drawNPCInteractionMenu() {
        if (gp.currentInteractingNPC == null) return;
        NPC npc = (NPC) gp.currentInteractingNPC;

        // Gambar window untuk menu
        int frameX = gp.tileSize * 2;
        int frameY = gp.screenHeight / 2 - gp.tileSize * 2;
        int frameWidth = gp.tileSize * 5; // Lebih lebar untuk opsi
        int frameHeight = gp.tileSize * 5;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight); // g2 sudah di-set di draw()

        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(20F));

        int textX = frameX + gp.tileSize / 2;
        int textY = frameY + gp.tileSize;

        List<String> options = new ArrayList<>();
        options.add("Chat");
        options.add("Give Gift");

        if (npc.isMarriageCandidate && !npc.marriedToPlayer && !gp.player.isMarried() && !npc.engaged) {
                options.add("Propose");
        }
        // Kondisi untuk Marry
        if (npc.isMarriageCandidate && npc.engaged && !npc.marriedToPlayer && !gp.player.isMarried()) {
            options.add("Marry");
        }
        options.add("Leave");

        for (int i = 0; i < options.size(); i++) {
            if (i == npcMenuCommandNum) {
                g2.drawString(">", textX - gp.tileSize / 4, textY + i * gp.tileSize);
            }
            g2.drawString(options.get(i), textX, textY + i * gp.tileSize);
        }
    }

    public void drawSellScreen() {
        // Background window
        int frameX = gp.tileSize * 2;
        int frameY = gp.tileSize;
        int frameWidth = gp.screenWidth - (gp.tileSize * 4);
        int frameHeight = gp.tileSize * 9; // Adjusted for more info
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        // Title
        g2.setColor(Color.white);
        g2.setFont(pressStart.deriveFont(Font.BOLD, 28F));
        String text = "Shipping Bin";
        // Use getXForCenteredTextInFrame if title should be centered within the subWindow's frameWidth
        int titleX = getXForCenteredText(text);
        // Or use getXForCenteredText(text) if it should be centered on the whole screen (then adjust for frameX)
        // For subwindow title, getXForCenteredTextInFrame is better:
        // int titleX = frameX + (frameWidth - (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth()) / 2;
        int titleY = frameY + gp.tileSize - 10;
        g2.drawString(text, titleX, titleY);

        // Items in Bin Counter
        g2.setFont(pressStart.deriveFont(Font.PLAIN, 18F));
        String binStatus = "In Bin: " + gp.player.itemsInShippingBinToday.size() + "/16";
        int binStatusX = frameX + gp.tileSize / 2;
        int binStatusY = titleY + gp.tileSize / 2 + 10; // Below title
        g2.drawString(binStatus, binStatusX, binStatusY);

        // Player Gold Display (optional, but good context)
        String playerGoldText = "Gold: " + gp.player.gold;
        int goldTextWidth = g2.getFontMetrics().stringWidth(playerGoldText);
        int goldTextX = frameX + frameWidth - goldTextWidth - (gp.tileSize /2);
        g2.drawString(playerGoldText, goldTextX, binStatusY);


        // Inventory Slots to pick from
        final int slotPadding = gp.tileSize / 2;
        final int slotStartX = frameX + slotPadding;
        final int slotStartY = binStatusY + gp.tileSize / 2 + 10; // Below bin status
        final int slotSize = gp.tileSize;
        final int slotGap = 8;
        final int slotTotalWidth = slotSize + slotGap;

        // Calculate how many slots fit per row based on frameWidth and paddings
        int availableWidthForSlots = frameWidth - (2 * slotPadding);
        final int slotsPerRow = Math.max(1, availableWidthForSlots / slotTotalWidth);
        final int maxRowsToDisplay = 4; // Or calculate based on frameHeight

        int itemsDisplayedCount = 0; // To track actual items shown from inventory

        for (int i = 0; i < gp.player.inventory.size(); i++) {
            if (itemsDisplayedCount >= slotsPerRow * maxRowsToDisplay) {
                // Optionally draw "..." if more items exist but not shown
                // g2.setColor(Color.GRAY);
                // g2.drawString("...", someX, someY);
                break;
            }

            Entity itemEntity = gp.player.inventory.get(i);
            // We only want to display items that are instances of OBJ_Item for selling
            if (!(itemEntity instanceof OBJ_Item)) {
                continue; // Skip non-OBJ_Item entities
            }
            OBJ_Item item = (OBJ_Item) itemEntity;

            int col = itemsDisplayedCount % slotsPerRow;
            int row = itemsDisplayedCount / slotsPerRow;
            int currentSlotX = slotStartX + (col * slotTotalWidth);
            int currentSlotY = slotStartY + (row * slotTotalWidth); // Assuming square grid, use slotTotalWidth for Y spacing too

            // Draw background slot
            if (item.getSellPrice() > 0) {
                g2.setColor(new Color(255, 255, 255, 60)); // Slightly more visible for sellable
            } else {
                g2.setColor(new Color(100, 100, 100, 60)); // Darker for non-sellable
            }
            g2.fillRoundRect(currentSlotX, currentSlotY, slotSize, slotSize, 10, 10);

            // Draw item image
            BufferedImage itemImageToDraw = item.down1 != null ? item.down1 : item.image;
            if (itemImageToDraw != null) {
                g2.drawImage(itemImageToDraw, currentSlotX, currentSlotY, slotSize, slotSize, null);
            }

            // Highlight item yang dipilih (gp.ui.commandNumber is the actual index in gp.player.inventory)
            if (i == gp.ui.commandNumber) {
                g2.setColor(Color.YELLOW);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(currentSlotX - 2, currentSlotY - 2, slotSize + 4, slotSize + 4, 12, 12);
                g2.setStroke(new BasicStroke(1)); // Reset stroke

                // Display name and sell price of the selected item below the grid
                g2.setFont(pressStart.deriveFont(Font.PLAIN, 18F));
                g2.setColor(Color.white); // Reset color for text
                String itemName = item.name;
                String itemSellPriceText = (item.getSellPrice() > 0) ? item.getSellPrice() + "G" : "Cannot be sold";

                int infoAreaY = frameY + frameHeight - gp.tileSize * 2 + 20; // Position for info text
                g2.drawString("Item: " + itemName, slotStartX, infoAreaY);
                g2.drawString("Price: " + itemSellPriceText, slotStartX, infoAreaY + 22); // Line spacing
            }
            itemsDisplayedCount++;
        }

        if (gp.player.inventory.isEmpty() || itemsDisplayedCount == 0) { // Check if inventory is empty OR no sellable items were displayed
            g2.setFont(pressStart.deriveFont(Font.PLAIN, 20F));
            g2.setColor(Color.WHITE);
            String emptyMsg = gp.player.inventory.isEmpty() ? "Inventory is empty." : "No items to sell.";
            int msgX = getXForCenteredText(emptyMsg);
            int msgY = slotStartY + (maxRowsToDisplay * slotTotalWidth) / 2 - g2.getFontMetrics().getHeight() / 2; // Centered in slot area
            g2.drawString(emptyMsg, msgX, msgY);
        }


        // Instructions
        g2.setFont(pressStart.deriveFont(Font.PLAIN, 10F));
        g2.setColor(Color.white);
        String instructions = "[Arrows] Navigate | [Enter] Add to Bin | [Esc] Finish";
        int instructionY = frameY + frameHeight - gp.tileSize / 2 - 5;
        int instructionX = getXForCenteredText(instructions);
        g2.drawString(instructions, instructionX, instructionY);

        // Display messages from gp.ui.showMessage() if messageOn is true
        // This is handled by the main draw() method, so no need to add it here unless
        // you want sellState-specific message display.
    }

    private void drawTimedMessage(Graphics2D g2) {
        if (messageOn && this.message != null && !this.message.isEmpty()) {
            // Pengaturan Font dan Warna Pesan
            Font messageFont = silkScreen.deriveFont(Font.PLAIN, 16F); // Ukuran font bisa disesuaikan
            g2.setFont(messageFont);
            g2.setColor(Color.YELLOW);
            FontMetrics fm = g2.getFontMetrics(messageFont);

            // Tentukan area untuk pesan
            int messageAreaX = gp.tileSize / 2;
            int messageAreaY = gp.tileSize * 2 + 10;
            int messageAreaWidth = gp.screenWidth - (gp.tileSize); // Lebar maksimum pesan
            int lineHeight = fm.getHeight() + 2;

            // Bagi pesan menjadi beberapa baris
            List<String> lines = new ArrayList<>();
            String[] words = this.message.split(" "); // Gunakan this.message
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                if (word.equals("\\n")) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                    continue;
                }
                String testLine = currentLine.length() > 0 ? currentLine.toString() + " " + word : word;
                if (fm.stringWidth(testLine) <= messageAreaWidth) {
                    if (currentLine.length() > 0) {
                        currentLine.append(" ");
                    }
                    currentLine.append(word);
                } else {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                    if (fm.stringWidth(currentLine.toString()) > messageAreaWidth && currentLine.length() > 0) {
                        // Jika satu kata terlalu panjang, gambar apa adanya (mungkin perlu penanganan lebih lanjut)
                        lines.add(currentLine.toString());
                        currentLine = new StringBuilder();
                    }
                }
            }
            if (currentLine.length() > 0) {
                lines.add(currentLine.toString());
            }

            // Gambar setiap baris pesan
            int currentMessageY = messageAreaY;
            for (String line : lines) {
                int lineX = messageAreaX;
                g2.drawString(line, lineX, currentMessageY);
                currentMessageY += lineHeight;
            }

            messageCounter++;
            if (messageCounter > 120) { // Tampilkan pesan selama 2 detik (60fps * 2)
                messageCounter = 0;
                messageOn = false;
                this.message = ""; // Reset pesan di UI
            }
        }
    }

    public void drawCookingScreen() {
        // 1. Gambar window latar belakang
        int frameX = gp.tileSize * 2;
        int frameY = gp.tileSize;
        int frameWidth = gp.screenWidth - (gp.tileSize * 4);
        int frameHeight = gp.screenHeight - (gp.tileSize * 2);
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        g2.setColor(Color.white);
        Font baseFont = pressStart != null ? pressStart : new Font("Arial", Font.PLAIN, 20);

        // 2. Judul
        String title;
        if (cookingSubState == 1 && gp.selectedRecipeForCooking != null) {
            title = "Confirm: Cook " + gp.selectedRecipeForCooking.outputFoodName + "?";
        } else {
            title = "Kitchen - Select Recipe";
        }
        g2.setFont(baseFont.deriveFont(Font.BOLD, 22F));
        int titleX = getXForCenteredTextInFrame(title, frameX, frameWidth);
        g2.drawString(title, titleX, frameY + gp.tileSize);

        // 3. Filter resep yang sudah di-unlock
        List<Recipe> displayableRecipes = new ArrayList<>();
        if (gp.player != null && gp.player.recipeUnlockStatus != null) {
            List<Recipe> allRecipes = RecipeManager.getAllRecipes();
            if (allRecipes != null) {
                // Gunakan .collect(Collectors.toList()) untuk kompatibilitas Java yang lebih luas
                displayableRecipes = allRecipes.stream()
                        .filter(r -> Boolean.TRUE.equals(gp.player.recipeUnlockStatus.get(r.recipeId)))
                        .collect(Collectors.toList()); // Diubah dari .toList()
            }
        }

        // 4. Tampilan jika tidak ada resep
        if (cookingSubState == 0 && displayableRecipes.isEmpty()) {
            g2.setFont(baseFont.deriveFont(Font.PLAIN, 18F));
            String noRecipeMsg = "You haven't learned any recipes yet.";
            g2.drawString(noRecipeMsg, getXForCenteredTextInFrame(noRecipeMsg, frameX, frameWidth), frameY + gp.tileSize * 3);
            g2.drawString("[Esc] Exit Kitchen", frameX + gp.tileSize, frameY + frameHeight - gp.tileSize + 10);
            return;
        }

        // 5. Pengaturan untuk daftar resep dan detail
        int listStartX = frameX + gp.tileSize / 2;
        int listStartY = frameY + gp.tileSize * 2 - 10;
        int recipeLineHeight = 30;
        int detailLineHeight = 22; // Sedikit lebih besar untuk font 12F/14F

        // Alokasi lebar: Misal daftar resep 45% dari frame, sisanya untuk detail & padding
        int recipeListColumnWidth = (int) (frameWidth * 0.45); // Lebar area untuk daftar nama resep

        // PENYESUAIAN detailStartX:
        // Mulai detail setelah recipeListColumnWidth + padding yang lebih besar
        int gapBetweenColumns = gp.tileSize; // Jarak antara kolom list dan kolom detail
        int detailStartX = listStartX + recipeListColumnWidth + gapBetweenColumns;

        if (cookingSubState == 0) { // Tampilan pemilihan resep
            g2.setFont(baseFont.deriveFont(Font.PLAIN, 18F));
            for (int i = 0; i < displayableRecipes.size(); i++) {
                Recipe recipe = displayableRecipes.get(i);
                String recipeName = recipe.outputFoodName;
                int currentY = listStartY + (i * recipeLineHeight);

                if (i == cookingCommandNum) {
                    g2.setColor(Color.yellow);
                    // Gambar nama resep hanya selebar recipeListColumnWidth
                    // Jika nama resep terlalu panjang, perlu dipotong atau di-wrap (tidak diimplementasikan di sini)
                    g2.drawString("> " + recipeName, listStartX, currentY);

                    // Tampilkan detail resep yang dipilih di sebelah kanan
                    g2.setColor(Color.lightGray);
                    // PENYESUAIAN FONT DETAIL: Ubah dari 10F ke 12F atau 14F
                    g2.setFont(baseFont.deriveFont(Font.PLAIN, 10F));
                    int detailCurrentY = listStartY;
                    g2.drawString("Ingredients for:", detailStartX, detailCurrentY);
                    detailCurrentY += detailLineHeight;
                    g2.drawString(recipe.outputFoodName, detailStartX, detailCurrentY); // Nama resep di detail
                    detailCurrentY += detailLineHeight;

                    for (Map.Entry<String, Integer> entry : recipe.ingredients.entrySet()) {
                        String ingredientText = "- " + entry.getKey() + ": " + entry.getValue();
                        g2.drawString(ingredientText, detailStartX + 10, detailCurrentY);
                        detailCurrentY += detailLineHeight;
                    }
                    g2.drawString("Fuel: Firewood or Coal", detailStartX + 10, detailCurrentY);
                    detailCurrentY += detailLineHeight;
                    if (recipe.unlockConditionDescription != null && !recipe.unlockConditionDescription.equals("Default/Bawaan")) {
                        g2.drawString("Unlocks: " + recipe.unlockConditionDescription, detailStartX + 10, detailCurrentY);
                    }

                    g2.setColor(Color.white);
                    g2.setFont(baseFont.deriveFont(Font.PLAIN, 18F)); // Reset font ke font daftar
                } else {
                    g2.drawString("  " + recipeName, listStartX, currentY);
                }
            }
            // Instruksi
            g2.setFont(baseFont.deriveFont(Font.PLAIN, 10F));
            g2.drawString("[Up/Down] Select | [Enter] View/Confirm | [Esc] Exit",
                    listStartX, frameY + frameHeight - gp.tileSize + 10);

        } else if (cookingSubState == 1 && gp.selectedRecipeForCooking != null) { // Tampilan konfirmasi
            Recipe selected = gp.selectedRecipeForCooking;
            g2.setFont(baseFont.deriveFont(Font.PLAIN, 18F));
            int confirmTextY = frameY + gp.tileSize * 3;
            String confirmMsg1 = "Cook: " + selected.outputFoodName + "?";
            String confirmMsg2 = "Energy Cost: 10";
            String confirmMsg3 = "Time to Cook: 1 game hour";

            g2.drawString(confirmMsg1, getXForCenteredTextInFrame(confirmMsg1, frameX, frameWidth), confirmTextY);
            confirmTextY += recipeLineHeight; // Gunakan recipeLineHeight untuk spasi yang konsisten
            g2.drawString(confirmMsg2, getXForCenteredTextInFrame(confirmMsg2, frameX, frameWidth), confirmTextY);
            confirmTextY += recipeLineHeight;
            g2.drawString(confirmMsg3, getXForCenteredTextInFrame(confirmMsg3, frameX, frameWidth), confirmTextY);

            g2.setFont(baseFont.deriveFont(Font.PLAIN, 16F));
            g2.drawString("Press [Enter] to Cook, or [Esc] to Cancel.",
                    getXForCenteredTextInFrame("Press [Enter] to Cook, or [Esc] to Cancel.", frameX, frameWidth),
                    frameY + frameHeight - gp.tileSize + 10);
        }
    }

    private void drawSleepTransitionScreen(Graphics2D g2) {
        // 1. Gambar latar belakang gelap (untuk efek transisi)
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // 2. Tampilkan currentDialogue (yang berisi pesan tidur & pemulihan energi)
        if (currentDialogue != null && !currentDialogue.isEmpty()) {
            // Atur font dan warna untuk pesan
            if (pressStart != null) {
                g2.setFont(pressStart.deriveFont(Font.PLAIN, 20F)); // Ukuran font bisa disesuaikan
            } else {
                g2.setFont(new Font("Arial", Font.PLAIN, 20)); // Fallback
            }
            g2.setColor(Color.WHITE);

            // Bagi dialog menjadi beberapa baris berdasarkan karakter newline (\n)
            // dan gambar setiap baris di tengah layar.
            int yPosition = gp.screenHeight / 3; // Posisi Y awal untuk baris pertama
            int lineHeight = g2.getFontMetrics().getHeight() + 8; // Jarak antar baris

            for (String line : currentDialogue.split("\n")) {
                int xPosition = getXForCenteredText(line); // Gunakan helper yang sudah ada
                g2.drawString(line, xPosition, yPosition);
                yPosition += lineHeight; // Pindah ke posisi Y untuk baris berikutnya
            }

            // GamePanel akan mengatur durasi tampilan state ini.
            // Tidak perlu input "Press Enter" di sini karena transisi otomatis.

        } else {
            // Fallback jika currentDialogue kosong (seharusnya tidak terjadi jika Player.sleep() bekerja)
            if (pressStart != null) {
                g2.setFont(pressStart.deriveFont(Font.PLAIN, 22F));
            } else {
                g2.setFont(new Font("Arial", Font.PLAIN, 22)); // Fallback
            }
            g2.setColor(Color.WHITE);
            String wakingUpMsg = "Waking up...";
            int x = getXForCenteredText(wakingUpMsg);
            g2.drawString(wakingUpMsg, x, gp.screenHeight / 2);
        }
    }

    private void drawTimeHUD(Graphics2D g2) {
        if (gp.gameState != gp.playState) return;

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 13F)); // Ukuran konsisten dengan HUD lainnya

        // Pisah label dan nilai
        String[] labels = {"Time:", "Season:", "Weather:"};
        String[] values = {
                gameClock.getFormattedTime(),
                gameClock.getCurrentSeason().name(),
                gameClock.getWeather().getWeatherName()
        };

        int padding = 10;
        int lineSpacing = g2.getFontMetrics().getHeight() + 4;

        // Ukur lebar label dan value terpanjang
        int labelWidth = Arrays.stream(labels)
                .mapToInt(g2.getFontMetrics()::stringWidth)
                .max()
                .orElse(0);

        int valueWidth = Arrays.stream(values)
                .mapToInt(g2.getFontMetrics()::stringWidth)
                .max()
                .orElse(0);

        int totalWidth = labelWidth + 12 + valueWidth; // 12 px jarak antara label dan value
        int totalHeight = lineSpacing * labels.length;

        // Posisi kanan atas layar
        int x = gp.screenWidth - totalWidth - padding;
        int y = padding;

        // Gambar background
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(
                x - 10,
                y - 10,
                totalWidth + 20,
                totalHeight + 10,
                15,
                15
        );

        // Gambar teks
        g2.setColor(Color.WHITE);
        for (int i = 0; i < labels.length; i++) {
            int textY = y + lineSpacing * (i + 1) - 4;

            // Label rata kiri
            g2.drawString(labels[i], x, textY);

            // Value rata kanan (digeser lebih dari label)
            g2.drawString(values[i], x + labelWidth + 12, textY);
        }
    }

    public void drawLocationHUD(Graphics2D g2) {
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));
        String locationText = "Location: " + gp.player.getLocation();

        int textWidth = g2.getFontMetrics().stringWidth(locationText);
        int padding = 10;
        int x = gp.screenWidth - textWidth - padding;
        int y = gp.screenHeight - padding;

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(x - 10, y - g2.getFontMetrics().getHeight(),
                textWidth + 20, g2.getFontMetrics().getHeight() + 10, 15, 15);

        g2.setColor(Color.WHITE);
        g2.drawString(locationText, x, y);
    }

    // Di UI.java
    public void startSelfDialogue(String text) {
        this.currentDialogue = text;
        gp.currentInteractingNPC = null; // SANGAT PENTING
        gp.gameState = gp.dialogueState;
        System.out.println("DEBUG: UI.startSelfDialogue - Text: " + text + ", GameState set to: " + gp.gameState); // DEBUG
        if (gp.gameClock != null && gp.gameClock.getTime() != null) { // Cek null getTime()
            if (!gp.gameClock.isPaused()) { // Hanya pause jika belum di-pause
                gp.gameClock.pauseTime();
                System.out.println("DEBUG: UI.startSelfDialogue - GameClock paused.");
            } else {
                System.out.println("DEBUG: UI.startSelfDialogue - GameClock was already paused.");
            }
        } else {
            System.out.println("DEBUG: UI.startSelfDialogue - GameClock or Time is null, cannot pause.");
        }
    }
}