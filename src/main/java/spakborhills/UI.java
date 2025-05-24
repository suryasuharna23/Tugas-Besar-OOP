package spakborhills;

import spakborhills.entity.Entity;
import spakborhills.entity.NPC;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class UI {
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
        }
        // FARM NAME INPUT STATE
        else if (gp.gameState == gp.farmNameInputState) {
            drawFarmNameInputScreen();
        }
        // SLEEP TRANSITION STATE  <-- TAMBAHKAN INI
        else if (gp.gameState == gp.sleepTransitionState) {
            drawSleepTransitionScreen(g2); // Panggil metode baru
        }
        // PLAY STATE
        else if (gp.gameState == gp.playState) {
            drawTimeHUD(g2);
            drawEnergyBar(g2);
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
        }
        // NPC INTERACTION MENU
        else if (gp.gameState == gp.interactionMenuState) {
            // drawTimeHUD(g2);
            // drawEnergyBar(g2);
            drawNPCInteractionMenu();
        }
        // Pastikan semua state lain sudah tertangani atau tidak memerlukan penggambaran khusus
    }

    public void drawTitleScreen(){
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

    public void drawPauseScreen(){
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 80f));
        String text = "PAUSED";
        int x = getXForCenteredText(text);
        int y = gp.screenHeight / 2;
        g2.drawString(text, x, y);
    }

    public void drawPlayerNameInputScreen() {
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

        ArrayList<String> options = new ArrayList<>();
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
        String text = "Time: " + gameClock.getFormattedTime()
                + " | Season: " + gameClock.getCurrentSeason().name()
                + " | Weather: " + gameClock.getWeather().getWeatherName();

        int padding = 10;
        int x = gp.screenWidth - g2.getFontMetrics().stringWidth(text) - padding * 33;
        int y = gp.tileSize / 2;

        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRoundRect(
                x - 10,
                0,
                g2.getFontMetrics().stringWidth(text) + padding * 2,
                36,
                10,
                10
        );

        g2.setColor(Color.white);
        g2.drawString(text, x, y);
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