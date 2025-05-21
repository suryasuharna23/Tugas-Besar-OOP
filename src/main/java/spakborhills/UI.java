package spakborhills;

import spakborhills.entity.Entity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

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

    public int inventoryCommandNum = 0;
    public int inventorySlotCol = 0;
    public int inventorySlotRow = 0;

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

    public void draw(Graphics2D g2){
        this.g2 = g2;

        g2.setFont(pressStart);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.white);

        // TITLE STATE
        if (gp.gameState == gp.titleState){
            drawTitleScreen();
            return;
        }

        if (gp.gameState == gp.playState){
            drawTimeHUD(g2);
            drawEnergyBar(g2);
        }
        // PAUSE STATE
        if (gp.gameState == gp.pauseState){
            drawPauseScreen();
        }

        // DIALOGUE STATE
        if (gp.gameState == gp.dialogueState){
            drawDialogueScreen();
        }
        if (gp.gameState == gp.inventoryState){
            drawInventoryScreen();
        }
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

    public void drawDialogueScreen(){
        // WINDOW
        int x = gp.tileSize * 2;
        int y = gp.tileSize / 2;
        int width = gp.screenWidth - (gp.tileSize * 4);
        int height = gp.tileSize * 4;

        drawSubWindow(x, y, width, height);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20));
        x += gp.tileSize;
        y += gp.tileSize;
        for (String line: currentDialogue.split("\n")){
            g2.drawString(line, x, y);
            y += 40;
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
        if (gp.player.maxEnergy > 0){
            double energyPerSegments = (double) gp.player.maxEnergy / totalSegments;
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
        String energyText = gp.player.currentEnergy + "/" + gp.player.maxEnergy;
        g2.setColor(Color.BLACK);
        int segmentsBarTotalWidth = totalSegments * (segmentWidth + segmentSpacing) - segmentSpacing;
        FontMetrics fmText = g2.getFontMetrics(silkScreen);
        int textHeightOffset = (segmenHeight - fmText.getAscent() - fmText.getDescent()) / 2 + fmText.getAscent();
        g2.drawString(energyText, x + segmentsBarTotalWidth + 10, y + textHeightOffset);
    }

    private Color getColor(int i, int totalSegments) {
        Color segmentColor;
        double currentEnergyPercentage = (double) gp.player.currentEnergy / gp.player.maxEnergy;

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
                y - 24,
                g2.getFontMetrics().stringWidth(text) + padding * 2,
                36,
                10,
                10
        );

        g2.setColor(Color.white);
        g2.drawString(text, x, y);
    }
}