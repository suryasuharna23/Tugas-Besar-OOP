package spakborhills;

import spakborhills.cooking.Recipe;
import spakborhills.cooking.RecipeManager;
import spakborhills.entity.Entity;
import spakborhills.entity.NPC;
import spakborhills.entity.NPC_EMILY;
import spakborhills.enums.Season;
import spakborhills.interfaces.Edible;
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
    public int storeCommandNum = 0;

    public Entity focusedItem = null;
    public int inventorySubstate = 0;

    public String playerNameInput = "";
    private String playerNamePromptMessage = "Enter Your Name:";
    private String playerNameSubMessage = "(Press ENTER to confirm, BACKSPACE to delete)";
    public int playerNameMaxLength = 15;

    public String farmNameInput = "";
    private String farmNamePromptMessage = "Enter Your Farm's Name:";
    private String farmNameSubMessage = "(Press ENTER to confirm, BACKSPACE to delete)";
    public int farmNameMaxLength = 20;

    BufferedImage titleScreenBackground;
    public int cookingCommandNum = 0;
    public int cookingSubState = 0;

    private List<String> currentDialogueLines;
    public int dialogueCurrentPage;
    private int dialogueLinesPerPage;

    public UI(GamePanel gp, GameClock gameClock) {
        this.gp = gp;
        this.gameClock = gameClock;

        InputStream inputStream = getClass().getResourceAsStream("/fonts/SilkscreenRegular.ttf");
        try {
            silkScreen = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            inputStream = getClass().getResourceAsStream("/fonts/PressStart2PRegular.ttf");
            pressStart = Font.createFont(Font.TRUETYPE_FONT, inputStream);
        } catch (FontFormatException | IOException e) {
            System.out.println(e.getMessage());
        }
        this.currentDialogueLines = new ArrayList<>();
        this.dialogueCurrentPage = 0;
        this.dialogueLinesPerPage = 6;
        this.lastProcessedDialogue = "";
        loadTitleScreenImage();
    }

    public void showMessage(String text) {
        message = text;
        messageOn = true;
    }

    public void loadTitleScreenImage() {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/background/title_screen.png");

            if (inputStream != null) {
                titleScreenBackground = ImageIO.read(inputStream);
            } else {
                System.err.println("Cannot load the title screen!");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;

        if (pressStart != null) {
            g2.setFont(pressStart);
        } else {
            g2.setFont(new Font("Arial", Font.PLAIN, 20));
        }
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.white);

        if (gp.gameState == gp.titleState) {
            drawTitleScreen();
        } else if (gp.gameState == gp.playerNameInputState) {
            drawPlayerNameInputScreen();
            drawTimedMessage(g2);
        } else if (gp.gameState == gp.farmNameInputState) {
            drawFarmNameInputScreen();
            drawTimedMessage(g2);
        } else if (gp.gameState == gp.sleepTransitionState) {
            drawSleepTransitionScreen(g2);
            drawTimedMessage(g2);
        } else if (gp.gameState == gp.playState) {
            drawTimeHUD(g2);
            drawLocationHUD(g2);
            drawEnergyBar(g2);
            drawPlayerGold();
            drawFarmNameHUD(g2);
            drawTimedMessage(g2);
        } else if (gp.gameState == gp.pauseState) {
            drawPauseScreen();
        } else if (gp.gameState == gp.dialogueState) {
            drawDialogueScreen();
        } else if (gp.gameState == gp.inventoryState || gp.gameState == gp.giftSelectionState) {
            drawInventoryScreen();
            drawTimedMessage(g2);
        } else if (gp.gameState == gp.interactionMenuState) {
            drawNPCInteractionMenu();
        } else if (gp.gameState == gp.sellState) {
            drawSellScreen();
            drawTimedMessage(g2);
        } else if (gp.gameState == gp.cookingState) {
            drawCookingScreen();
            drawTimedMessage(g2);
        } else if (gp.gameState == gp.buyingState) {
            drawBuyingScreen();
        } else if (gp.gameState == gp.fishingMinigameState) {
            drawFishingMinigameScreen(g2);
        } else if (gp.gameState == gp.endGameState) {
            drawEndGameStatisticsScreen(g2);
        }
    }

    public void drawFishingMinigameScreen(Graphics2D g2) {

        int frameX = gp.tileSize * 3;
        int frameY = gp.screenHeight / 2 - gp.tileSize * 3;
        int frameWidth = gp.screenWidth - (gp.tileSize * 6);
        int frameHeight = gp.tileSize * 6;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        g2.setColor(Color.white);
        Font baseFont = pressStart != null ? pressStart : new Font("Arial", Font.PLAIN, 12);
        int currentTextY = frameY + gp.tileSize;

        g2.setFont(baseFont.deriveFont(Font.PLAIN, 14F));
        if (gp.player.fishingInfoMessage != null && !gp.player.fishingInfoMessage.isEmpty()) {

            List<String> infoLines = wrapText(gp.player.fishingInfoMessage, frameWidth - gp.tileSize,
                    g2.getFontMetrics());
            for (String line : infoLines) {
                int lineX = getXForCenteredTextInFrame(line, frameX, frameWidth);
                g2.drawString(line, lineX, currentTextY);
                currentTextY += g2.getFontMetrics().getHeight() + 2;
            }
        }
        currentTextY += gp.tileSize / 2;

        g2.setFont(baseFont.deriveFont(Font.BOLD, 16F));
        String displayInput = gp.player.fishingPlayerInput;
        if (System.currentTimeMillis() % 1000 < 500) {
            displayInput += "_";
        } else {
            displayInput += " ";
        }
        int inputX = getXForCenteredTextInFrame(displayInput, frameX, frameWidth);
        g2.drawString(displayInput, inputX, currentTextY);
        currentTextY += gp.tileSize;

        g2.setFont(baseFont.deriveFont(Font.ITALIC, 14F));
        if (gp.player.fishingFeedbackMessage != null && !gp.player.fishingFeedbackMessage.isEmpty()) {
            int feedbackX = getXForCenteredTextInFrame(gp.player.fishingFeedbackMessage, frameX, frameWidth);
            g2.drawString(gp.player.fishingFeedbackMessage, feedbackX, currentTextY);
        }
        currentTextY += gp.tileSize / 2;

        g2.setFont(baseFont.deriveFont(Font.PLAIN, 12F));
        int attemptsLeft = gp.player.fishingMaxTry - gp.player.fishingCurrentAttempts;
        String attemptsText = "Percobaan tersisa: " + attemptsLeft + "/" + gp.player.fishingMaxTry;
        int attemptsX = frameX + gp.tileSize / 2;
        g2.drawString(attemptsText, attemptsX, frameY + frameHeight - gp.tileSize + 15);

        g2.setFont(baseFont.deriveFont(Font.PLAIN, 10F));
        String instructions = "[0-9] Angka | [Enter] Tebak | [Backspace] Hapus | [Esc] Menyerah";
        int instructionX = getXForCenteredTextInFrame(instructions, frameX, frameWidth);
        g2.drawString(instructions, instructionX, frameY + frameHeight - gp.tileSize / 2);
    }

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
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    lines.add(word);
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
            g2.setColor(new Color(7, 150, 255));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }
    }

    public void drawPlayerGold() {
        g2.setFont(pressStart.deriveFont(Font.BOLD, 15F));
        g2.setColor(Color.white);

        String goldText = "Gold: " + gp.player.gold;
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(goldText);
        int coinSize = gp.tileSize / 2;

        int padding = 10;
        int x = gp.screenWidth - textWidth - coinSize - padding * 2;
        int y = padding * 10;

        InputStream inputStream = getClass().getResourceAsStream("/objects/gold.png");
        BufferedImage coinImage;
        try {
            int coinY = y - fm.getAscent() + (fm.getAscent() - coinSize) / 2;
            coinImage = ImageIO.read(inputStream);
            g2.drawImage(coinImage, x, coinY, coinSize, coinSize, null);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        x += coinSize + 5;
        g2.drawString(goldText, x, y);
    }

    public void drawFarmNameHUD(Graphics2D g2) {
        if (gp.gameState != gp.playState || gp.player == null) {
            return;
        }

        String currentLocation = gp.player.getLocation();
        boolean isInFarm = false;

        if (currentLocation != null && currentLocation.equalsIgnoreCase("Farm")) {
            isInFarm = true;
        } else {

            if (gp.currentMapIndex >= 0 && gp.currentMapIndex < gp.mapInfos.size()) {
                String mapName = gp.mapInfos.get(gp.currentMapIndex).getMapName();
                if (mapName != null && mapName.equalsIgnoreCase("Farm")) {
                    isInFarm = true;
                }
            }
        }

        if (!isInFarm) {
            return;
        }

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 15F));
        g2.setColor(Color.WHITE);

        String farmName = gp.player.getFarmName();
        if (farmName == null || farmName.trim().isEmpty()) {
            farmName = "Unnamed Farm";
        }

        String farmText = farmName;
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(farmText);
        int textHeight = fm.getHeight();

        int padding = 10;
        int x = padding;
        int y = gp.screenHeight - padding;

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(
                x - 8,
                y - textHeight + fm.getDescent() - 5,
                textWidth + 16,
                textHeight + 10,
                15,
                15);

        g2.setColor(Color.WHITE);
        g2.drawString(farmText, x, y);
    }

    public void drawTitleScreen() {
        if (mapSelectionState == 0) {
            if (titleScreenBackground != null) {
                g2.drawImage(titleScreenBackground, 0, 0, gp.screenWidth, gp.screenHeight, null);
            } else {
                g2.setColor(new Color(7, 150, 255));
                g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
            }

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 50F));
            String text = "SPAKBOR HILL'S";
            int x = getXForCenteredText(text);
            int y = gp.tileSize * 3;

            g2.setColor(Color.black);
            g2.drawString(text, x + 5, y + 5);
            g2.setColor(Color.white);
            g2.drawString(text, x, y);

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 30F));

            text = "NEW GAME";
            x = getXForCenteredText(text);
            y += gp.tileSize * 4;
            g2.setColor(Color.black);
            g2.drawString(text, x + 5, y + 5);
            g2.setColor(Color.white);
            g2.drawString(text, x, y);
            if (commandNumber == 0) {
                g2.drawString(">", x - gp.tileSize, y);
                if (gp.keyH.enterPressed) {
                    gp.gameState = gp.playerNameInputState;
                    playerNameInput = "";
                    gp.keyH.enterPressed = false;
                    if (gp.gameClock != null && !gp.gameClock.isPaused()) {
                        gp.gameClock.pauseTime();
                    }
                }
            }

            text = "LOAD GAME";
            x = getXForCenteredText(text);
            y += gp.tileSize;
            g2.setColor(Color.black);
            g2.drawString(text, x + 5, y + 5);
            g2.setColor(Color.white);
            g2.drawString(text, x, y);
            if (commandNumber == 1) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "QUIT";
            x = getXForCenteredText(text);
            y += gp.tileSize;
            g2.setColor(Color.black);
            g2.drawString(text, x + 5, y + 5);
            g2.setColor(Color.white);
            g2.drawString(text, x, y);
            if (commandNumber == 2) {
                g2.drawString(">", x - gp.tileSize, y);
            }
        } else if (mapSelectionState == 1) {
            drawSharedBackground(g2);

            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

            g2.setColor(Color.white);
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20F));

            String text = "World Map";
            int x = getXForCenteredText(text);
            int y = gp.tileSize * 2;
            g2.drawString(text, x, y);

            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 12F));
            y = gp.tileSize * 4;

            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 12F));

            text = "Abigail's";
            int x1 = gp.tileSize * 3 / 2;
            y += gp.tileSize * 2;
            g2.drawString(text, x1, y);
            y += gp.tileSize / 4;
            text = "House";
            g2.drawString(text, x1, y);
            y -= gp.tileSize / 4;
            if (commandNumber == 0) {
                g2.drawString(">", x1 - gp.tileSize / 4, y + gp.tileSize / 8);
            }

            text = "Caroline's";
            int x2 = x1 + gp.tileSize * 3;
            g2.drawString(text, x2, y);
            text = "House";
            y += gp.tileSize / 4;
            g2.drawString(text, x2, y);
            y -= gp.tileSize / 4;
            if (commandNumber == 1) {
                g2.drawString(">", x2 - gp.tileSize / 4, y + gp.tileSize / 8);
            }

            text = "Dasco's";
            int x3 = x2 + gp.tileSize * 3;
            g2.drawString(text, x3, y);
            text = "House";
            y += gp.tileSize / 4;
            g2.drawString(text, x3, y);
            y -= gp.tileSize / 4;
            if (commandNumber == 2) {
                g2.drawString(">", x3 - gp.tileSize / 4, y + gp.tileSize / 8);
            }

            text = "MayorTadi's";
            int x4 = x3 + gp.tileSize * 3;
            g2.drawString(text, x4, y);
            text = "House";
            y += gp.tileSize / 4;
            g2.drawString(text, x4, y);
            y -= gp.tileSize / 4;
            if (commandNumber == 3) {
                g2.drawString(">", x4 - gp.tileSize / 4, y + gp.tileSize / 8);
            }

            text = "Perry's";
            int x5 = x4 + gp.tileSize * 3;
            g2.drawString(text, x5, y);
            text = "House";
            y += gp.tileSize / 4;
            g2.drawString(text, x5, y);
            y -= gp.tileSize / 4;
            if (commandNumber == 4) {
                g2.drawString(">", x5 - gp.tileSize / 4, y + gp.tileSize / 8);
            }

            text = "Store";
            x = x1;
            y += gp.tileSize * 2;
            g2.drawString(text, x, y);
            if (commandNumber == 5) {
                g2.drawString(">", x - gp.tileSize / 4, y);
            }

            text = "Farm";
            x = x2;
            g2.drawString(text, x, y);
            if (commandNumber == 6) {
                g2.drawString(">", x - gp.tileSize / 4, y);
            }

            text = "Forest";
            x = x3;
            g2.drawString(text, x, y);
            text = "River";
            y += gp.tileSize / 4;
            g2.drawString(text, x, y);
            y -= gp.tileSize / 4;
            if (commandNumber == 7) {
                g2.drawString(">", x - gp.tileSize / 4, y);
            }

            text = "Mountain";
            x = x4;
            g2.drawString(text, x, y);
            text = "Lake";
            y += gp.tileSize / 4;
            g2.drawString(text, x, y);
            y -= gp.tileSize / 4;
            if (commandNumber == 8) {
                g2.drawString(">", x - gp.tileSize / 4, y);
            }

            text = "Ocean";
            x = x5;
            g2.drawString(text, x, y);
            if (commandNumber == 9) {
                g2.drawString(">", x - gp.tileSize / 4, y);
            }

            text = "Player's House";
            x = getXForCenteredText(text);
            y += gp.tileSize * 2;
            g2.drawString(text, x, y);
            if (commandNumber == 10) {
                g2.drawString(">", x - gp.tileSize / 4, y);
            }
        }

    }

    public void drawPauseScreen() {
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 80f));
        String text = "PAUSED";
        int x = getXForCenteredText(text);
        int y = gp.screenHeight / 2;
        g2.drawString(text, x, y);
    }

    public void drawPlayerNameInputScreen() {
        drawSharedBackground(g2);
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(pressStart.deriveFont(Font.PLAIN, 30F));
        g2.setColor(Color.white);

        int x = getXForCenteredText(playerNamePromptMessage);
        int y = gp.screenHeight / 2 - gp.tileSize * 2;
        g2.drawString(playerNamePromptMessage, x, y);

        String displayText = playerNameInput;
        if (System.currentTimeMillis() % 1000 < 500) {
            displayText += "_";
        } else {
            displayText += " ";
        }

        g2.setFont(pressStart.deriveFont(Font.PLAIN, 28F));
        int textWidth = (int) g2.getFontMetrics().getStringBounds(displayText, g2).getWidth();
        x = gp.screenWidth / 2 - textWidth / 2;
        y += gp.tileSize * 2;
        g2.drawString(displayText, x, y);

        g2.setFont(pressStart.deriveFont(Font.PLAIN, 16F));
        x = getXForCenteredText(playerNameSubMessage);
        y += gp.tileSize * 1.5;
        g2.drawString(playerNameSubMessage, x, y);
    }

    public void drawFarmNameInputScreen() {
        drawSharedBackground(g2);
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(pressStart.deriveFont(Font.PLAIN, 30F));
        g2.setColor(Color.white);

        int x = getXForCenteredText(farmNamePromptMessage);
        int y = gp.screenHeight / 2 - gp.tileSize * 2;
        g2.drawString(farmNamePromptMessage, x, y);

        String displayText = farmNameInput;
        if (System.currentTimeMillis() % 1000 < 500) {
            displayText += "_";
        } else {
            displayText += " ";
        }

        g2.setFont(pressStart.deriveFont(Font.PLAIN, 28F));
        int textWidth = (int) g2.getFontMetrics().getStringBounds(displayText, g2).getWidth();
        x = gp.screenWidth / 2 - textWidth / 2;
        y += gp.tileSize * 2;
        g2.drawString(displayText, x, y);

        g2.setFont(pressStart.deriveFont(Font.PLAIN, 16F));
        x = getXForCenteredText(farmNameSubMessage);
        y += gp.tileSize * 1.5;
        g2.drawString(farmNameSubMessage, x, y);
    }

    public void drawDialogueScreen() {
        int x = gp.tileSize * 2;
        int y = gp.tileSize / 2;
        int width = gp.screenWidth - (gp.tileSize * 4);
        int height = gp.tileSize * 4;

        drawSubWindow(x, y, width, height);

        g2.setColor(Color.white);
        if (pressStart != null) {
            g2.setFont(pressStart.deriveFont(Font.PLAIN, 18F));
        } else {
            g2.setFont(new Font("Arial", Font.PLAIN, 18));
        }

        int textPadding = gp.tileSize / 2;
        int dialogueContentX = x + textPadding;
        int startY = y + textPadding;
        int maxTextWidth = width - (2 * textPadding);
        int maxHeight = y + height - textPadding;

        FontMetrics fm = g2.getFontMetrics();
        int lineHeight = fm.getHeight() + 3;
        int currentY = startY + fm.getAscent();

        if (gp.currentInteractingNPC != null && gp.currentInteractingNPC.name != null
                && !gp.currentInteractingNPC.name.isEmpty()) {
            Font currentFont = g2.getFont();
            g2.setFont(currentFont.deriveFont(Font.BOLD));
            String npcName = gp.currentInteractingNPC.name + ": ";
            g2.drawString(npcName, dialogueContentX, currentY);
            g2.setFont(currentFont);
            currentY += lineHeight;
        }

        if (currentDialogue != null && !currentDialogue.isEmpty()) {

            if (currentDialogueLines.isEmpty() || !currentDialogue.equals(getLastProcessedDialogue())) {
                processDialogueIntoLines(currentDialogue, maxTextWidth, fm);
                dialogueCurrentPage = 0;
            }

            int availableHeight = maxHeight - currentY;
            int maxDisplayableLines = availableHeight / lineHeight - 1;
            dialogueLinesPerPage = Math.max(1, maxDisplayableLines);

            int totalPages = (int) Math.ceil((double) currentDialogueLines.size() / dialogueLinesPerPage);
            int startLineIndex = dialogueCurrentPage * dialogueLinesPerPage;
            int endLineIndex = Math.min(startLineIndex + dialogueLinesPerPage, currentDialogueLines.size());

            for (int i = startLineIndex; i < endLineIndex; i++) {
                String line = currentDialogueLines.get(i);
                if (currentY + fm.getDescent() > maxHeight - lineHeight * 2) {
                    break;
                }

                if (!line.trim().isEmpty()) {
                    String displayLine = line;
                    if (fm.stringWidth(line) > maxTextWidth) {
                        while (fm.stringWidth(displayLine + "...") > maxTextWidth && displayLine.length() > 1) {
                            displayLine = displayLine.substring(0, displayLine.length() - 1);
                        }
                        displayLine += "...";
                    }
                    g2.drawString(displayLine, dialogueContentX, currentY);
                }
                currentY += lineHeight;
            }

            g2.setFont(pressStart.deriveFont(Font.PLAIN, 12F));
            g2.setColor(Color.LIGHT_GRAY);
            if (totalPages > 1) {

                String pageInfo = "Page " + (dialogueCurrentPage + 1) + "/" + totalPages;
                int pageInfoX = dialogueContentX;
                int pageInfoY = y + height - textPadding - 25;
                g2.drawString(pageInfo, pageInfoX, pageInfoY);

                String navHint = "";
                if (dialogueCurrentPage > 0 && dialogueCurrentPage < totalPages - 1) {
                    navHint = "[↑] Prev | [↓] Next | [ENTER] OK";
                } else if (dialogueCurrentPage > 0) {
                    navHint = "[↑] Previous | [ENTER] OK";
                } else if (dialogueCurrentPage < totalPages - 1) {
                    navHint = "[↓] Next | [ENTER] OK";
                }

                if (!navHint.isEmpty()) {
                    FontMetrics hintFm = g2.getFontMetrics();
                    int maxHintWidth = width - pageInfo.length() * 8 - textPadding * 2;

                    if (hintFm.stringWidth(navHint) > maxHintWidth) {
                        int navHintX = dialogueContentX;
                        int navHintY = pageInfoY + 15;
                        g2.drawString(navHint, navHintX, navHintY);
                    } else {

                        int navHintX = pageInfoX + hintFm.stringWidth(pageInfo) + 20;
                        g2.drawString(navHint, navHintX, pageInfoY);
                    }
                }
            } else {
                String continueText = "Press ENTER to continue...";
                int continueX = x + width - fm.stringWidth(continueText) - textPadding;
                int continueY = y + height - textPadding;
                g2.drawString(continueText, continueX, continueY);
            }

            g2.setColor(Color.WHITE);
        }
    }

    private void processDialogueIntoLines(String dialogue, int maxTextWidth, FontMetrics fm) {
        currentDialogueLines.clear();
        this.lastProcessedDialogue = dialogue;
        String[] paragraphs = dialogue.split("\\n");

        for (String paragraph : paragraphs) {
            if (paragraph.trim().isEmpty()) {
                currentDialogueLines.add("");
                continue;
            }

            String[] words = paragraph.split(" ");
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                String testLine = currentLine.length() > 0 ? currentLine + " " + word : word;

                if (fm.stringWidth(testLine) <= maxTextWidth) {
                    if (currentLine.length() > 0) {
                        currentLine.append(" ");
                    }
                    currentLine.append(word);
                } else {
                    if (currentLine.length() > 0) {
                        currentDialogueLines.add(currentLine.toString());
                        currentLine = new StringBuilder(word);
                    } else {

                        String longWord = word;
                        while (fm.stringWidth(longWord) > maxTextWidth && longWord.length() > 1) {
                            int cutPoint = longWord.length() - 1;
                            while (cutPoint > 0
                                    && fm.stringWidth(longWord.substring(0, cutPoint) + "-") > maxTextWidth) {
                                cutPoint--;
                            }
                            if (cutPoint > 0) {
                                currentDialogueLines.add(longWord.substring(0, cutPoint) + "-");
                                longWord = longWord.substring(cutPoint);
                            } else {
                                break;
                            }
                        }
                        currentLine = new StringBuilder(longWord);
                    }
                }
            }

            if (currentLine.length() > 0) {
                currentDialogueLines.add(currentLine.toString());
            }
        }
    }

    private String lastProcessedDialogue = "";

    private String getLastProcessedDialogue() {
        return lastProcessedDialogue;
    }

    public void drawSubWindow(int x, int y, int width, int height) {
        Color c = new Color(0, 0, 0, 210);
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        c = new Color(255, 255, 255);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);
    }

    public int getXForCenteredText(String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return gp.screenWidth / 2 - length / 2;
    }

    public void drawEnergyBar(Graphics2D g2) {
        int x = gp.tileSize / 2;
        int y = gp.tileSize / 2;
        int segmentWidth = gp.tileSize / 2;
        int segmenHeight = gp.tileSize / 2 - 5;
        int segmentSpacing = 3;
        int totalSegments = 10;

        int filledSegments = 0;
        if (gp.player.MAX_POSSIBLE_ENERGY > 0) {
            double energyPerSegments = (double) gp.player.MAX_POSSIBLE_ENERGY / totalSegments;
            if (energyPerSegments > 0) {
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

        for (int i = 0; i < totalSegments; i++) {
            int currentSegmentX = x + (i * (segmentWidth + segmentSpacing));

            g2.setColor(Color.black);
            g2.fillRect(currentSegmentX - 1, y - 1, segmentWidth + 2, segmenHeight + 2);
            g2.setColor(Color.gray);
            g2.fillRect(currentSegmentX, y, segmentWidth, segmenHeight);

            if (i < filledSegments) {
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

        if (currentEnergyPercentage > 0.6) {
            segmentColor = new Color(0, 200, 0);
        } else if (currentEnergyPercentage > 0.3) {
            segmentColor = new Color(255, 200, 0);
        } else {
            segmentColor = new Color(200, 0, 0);
        }
        return segmentColor;
    }

    public int getXForInventoryTitle(String text, int frameX, int frameWidth) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return frameX + (frameWidth / 2) - (length / 2);
    }

    public int getXForCenteredTextInFrame(String text, int frameX, int frameWidth) {
        if (g2 == null || text == null)
            return frameX;
        FontMetrics fm = g2.getFontMetrics();
        int length = fm.stringWidth(text);
        return frameX + (frameWidth - length) / 2;
    }

    public void drawInventoryScreen() {
        int frameX = gp.tileSize * 2;
        int frameY = gp.tileSize;
        int frameWidth = gp.screenWidth - (gp.tileSize * 4);
        int frameHeight = gp.tileSize * 10;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        g2.setColor(Color.white);
        Font titleFont = (pressStart != null) ? pressStart.deriveFont(24F) : new Font("Arial", Font.BOLD, 24);
        g2.setFont(titleFont);
        g2.drawString("INVENTORY", getXForInventoryTitle("INVENTORY", frameX, frameWidth), frameY + gp.tileSize - 10);

        final int slotStartX = frameX + gp.tileSize / 2;
        final int slotStartY = frameY + gp.tileSize + 20;
        int currentSlotX = slotStartX;
        int currentSlotY = slotStartY;
        final int slotSize = gp.tileSize + 10;
        final int slotGap = 5;
        final int itemsPerRow = (frameWidth - gp.tileSize) / (slotSize + slotGap);

        int currentItemIndexInList = 0;

        Font itemQuantityFont = (pressStart != null) ? pressStart.deriveFont(Font.BOLD, 12F)
                : new Font("Arial", Font.BOLD, 12);
        Font itemInfoFont = (pressStart != null) ? pressStart.deriveFont(18F) : new Font("Arial", Font.PLAIN, 18);

        for (Entity itemEntity : gp.player.inventory) {
            if (!(itemEntity instanceof OBJ_Item item)) {
                continue;
            }

            if (currentSlotY + slotSize > frameY + frameHeight - gp.tileSize / 2) {
                g2.setColor(Color.white);
                g2.setFont(itemInfoFont);
                g2.drawString("...", currentSlotX, currentSlotY + slotSize / 2);
                break;
            }

            g2.setColor(new Color(100, 100, 100, 150));
            g2.fillRoundRect(currentSlotX, currentSlotY, slotSize, slotSize, 10, 10);
            g2.setColor(Color.white);
            g2.drawRoundRect(currentSlotX, currentSlotY, slotSize, slotSize, 10, 10);

            BufferedImage imageToDraw = item.image != null ? item.image : item.down1;
            if (imageToDraw != null) {
                g2.drawImage(imageToDraw, currentSlotX + 5, currentSlotY + 5, gp.tileSize, gp.tileSize, null);
            }

            if (item.quantity > 0) {
                g2.setFont(itemQuantityFont);
                g2.setColor(Color.white);
                String quantityText = "x" + item.quantity;
                FontMetrics qfm = g2.getFontMetrics(itemQuantityFont);
                int qtyX = currentSlotX + slotSize - qfm.stringWidth(quantityText) - 5;
                int qtyY = currentSlotY + slotSize - 5;

                g2.setColor(Color.black);
                g2.drawString(quantityText, qtyX + 1, qtyY + 1);
                g2.setColor(Color.white);
                g2.drawString(quantityText, qtyX, qtyY);
            }
            if (currentItemIndexInList == inventoryCommandNum) {
                g2.setColor(Color.yellow);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(currentSlotX - 2, currentSlotY - 2, slotSize + 4, slotSize + 4, 12, 12);
                g2.setStroke(new BasicStroke(1));

                g2.setColor(Color.white);
                int itemInfoY = frameY + frameHeight + gp.tileSize / 2;
                g2.drawString("Item: " + item.getName(), slotStartX, itemInfoY);
                if (item.isEdible() && item instanceof Edible) {
                    g2.drawString("Tekan 'E' untuk Makan", slotStartX, itemInfoY + 20);
                }
            }

            currentSlotX += slotSize + slotGap;
            if ((currentItemIndexInList + 1) % itemsPerRow == 0) {
                currentSlotX = slotStartX;
                currentSlotY += slotSize + slotGap;
            }
            currentItemIndexInList++;
        }

        if (gp.player.inventory.isEmpty()) {
            g2.setColor(Color.white);
            g2.setFont(itemInfoFont);
            int itemInfoY = frameY + frameHeight + gp.tileSize / 2;
            g2.drawString("Inventaris Kosong", slotStartX, itemInfoY);
        } else if (inventoryCommandNum < 0 || inventoryCommandNum >= gp.player.inventory.size()) {
            g2.setColor(Color.white);
            g2.setFont(itemInfoFont);
            int itemInfoY = frameY + frameHeight + gp.tileSize / 2;
            g2.drawString("Pilih item...", slotStartX, itemInfoY);
        }
        g2.setFont((pressStart != null) ? pressStart.deriveFont(Font.PLAIN, 10F) : new Font("Arial", Font.PLAIN, 10));
        g2.setColor(Color.white);
        String instructions = "[Enter] Pilih/Equip | [I/Esc] Tutup";
        if (gp.player.getEquippedItem() instanceof Edible) {
            instructions += " | [E] Makan Item yang Dipegang";
        }
        int instructionY = frameY + frameHeight - gp.tileSize + 35;
        int instructionX = getXForCenteredTextInFrame(instructions, frameX, frameWidth);
        g2.drawString(instructions, instructionX, instructionY);
    }

    public void drawNPCInteractionMenu() {
        if (gp.currentInteractingNPC == null)
            return;
        NPC npc = (NPC) gp.currentInteractingNPC;

        List<String> options = new ArrayList<>();
        options.add("Chat");
        options.add("Give Gift");

        if (npc.isMarriageCandidate && !npc.marriedToPlayer && !gp.player.isMarried() && !npc.engaged) {
            options.add("Propose");
        }
        if (npc.isMarriageCandidate && npc.engaged && !npc.marriedToPlayer && !gp.player.isMarried()) {
            options.add("Marry");
        }
        if (npc.name.equals("Emily")) {
            options.add("Shop");
        }
        options.add("Leave");

        int frameX = gp.tileSize * 2;
        int frameY = gp.screenHeight / 2 - gp.tileSize * 2;
        int frameWidth = gp.tileSize * 6;

        int titleHeight = gp.tileSize;
        int optionHeight = gp.tileSize * options.size();
        int padding = gp.tileSize / 2;
        int frameHeight = titleHeight + optionHeight + padding * 2;

        if (frameY + frameHeight > gp.screenHeight) {
            frameY = gp.screenHeight - frameHeight - gp.tileSize / 2;
        }
        if (frameY < gp.tileSize / 2) {
            frameY = gp.tileSize / 2;

            int maxHeight = gp.screenHeight - gp.tileSize;
            if (frameHeight > maxHeight) {
                frameHeight = maxHeight;
            }
        }

        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        g2.setColor(Color.white);
        g2.setFont(pressStart.deriveFont(Font.PLAIN, 18F));

        String title = npc.name;
        int titleX = getXForCenteredTextInFrame(title, frameX, frameWidth);
        int titleY = frameY + gp.tileSize / 2 + 15;
        g2.drawString(title, titleX, titleY);

        int textX = frameX + gp.tileSize / 2;
        int textY = titleY + gp.tileSize / 2 + 10;
        int lineSpacing = (frameHeight - titleHeight - padding * 2) / options.size();
        lineSpacing = Math.max(lineSpacing, 25);

        for (int i = 0; i < options.size(); i++) {
            int currentY = textY + (i * lineSpacing);

            if (currentY > frameY + frameHeight - gp.tileSize / 2) {
                break;
            }

            if (i == npcMenuCommandNum) {
                g2.setColor(Color.YELLOW);
                g2.drawString("> " + options.get(i), textX, currentY);
                g2.setColor(Color.WHITE);
            } else {
                g2.drawString("  " + options.get(i), textX, currentY);
            }
        }

        g2.setFont(pressStart.deriveFont(Font.PLAIN, 10F));
        g2.setColor(Color.LIGHT_GRAY);
    }

    public void drawSellScreen() {
        int frameX = gp.tileSize * 2;
        int frameY = gp.tileSize;
        int frameWidth = gp.screenWidth - (gp.tileSize * 4);
        int frameHeight = gp.tileSize * 9;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        g2.setColor(Color.white);
        g2.setFont(pressStart.deriveFont(Font.BOLD, 28F));
        String text = "Shipping Bin";
        int titleX = getXForCenteredText(text);
        int titleY = frameY + gp.tileSize - 10;
        g2.drawString(text, titleX, titleY);

        g2.setFont(pressStart.deriveFont(Font.PLAIN, 18F));
        String binStatus = "In Bin: " + gp.player.itemsInShippingBinToday.size() + "/16";
        int binStatusX = frameX + gp.tileSize / 2;
        int binStatusY = titleY + gp.tileSize / 2 + 10;
        g2.drawString(binStatus, binStatusX, binStatusY);

        String playerGoldText = "Gold: " + gp.player.gold;
        int goldTextWidth = g2.getFontMetrics().stringWidth(playerGoldText);
        int goldTextX = frameX + frameWidth - goldTextWidth - (gp.tileSize / 2);
        g2.drawString(playerGoldText, goldTextX, binStatusY);

        final int slotPadding = gp.tileSize / 2;
        final int slotStartX = frameX + slotPadding;
        final int slotStartY = binStatusY + gp.tileSize / 2 + 10;
        final int slotSize = gp.tileSize;
        final int slotGap = 8;
        final int slotTotalWidth = slotSize + slotGap;

        int availableWidthForSlots = frameWidth - (2 * slotPadding);
        final int slotsPerRow = Math.max(1, availableWidthForSlots / slotTotalWidth);
        final int maxRowsToDisplay = 4;

        int itemsDisplayedCount = 0;

        for (int i = 0; i < gp.player.inventory.size(); i++) {
            if (itemsDisplayedCount >= slotsPerRow * maxRowsToDisplay) {
                break;
            }

            Entity itemEntity = gp.player.inventory.get(i);
            if (!(itemEntity instanceof OBJ_Item)) {
                continue;
            }
            OBJ_Item item = (OBJ_Item) itemEntity;

            int col = itemsDisplayedCount % slotsPerRow;
            int row = itemsDisplayedCount / slotsPerRow;
            int currentSlotX = slotStartX + (col * slotTotalWidth);
            int currentSlotY = slotStartY + (row * slotTotalWidth);

            if (item.getSellPrice() > 0) {
                g2.setColor(new Color(255, 255, 255, 60));
            } else {
                g2.setColor(new Color(100, 100, 100, 60));
            }
            g2.fillRoundRect(currentSlotX, currentSlotY, slotSize, slotSize, 10, 10);

            BufferedImage itemImageToDraw = item.down1 != null ? item.down1 : item.image;
            if (itemImageToDraw != null) {
                g2.drawImage(itemImageToDraw, currentSlotX, currentSlotY, slotSize, slotSize, null);
            }

            if (i == gp.ui.commandNumber) {
                g2.setColor(Color.YELLOW);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(currentSlotX - 2, currentSlotY - 2, slotSize + 4, slotSize + 4, 12, 12);
                g2.setStroke(new BasicStroke(1));

                g2.setFont(pressStart.deriveFont(Font.PLAIN, 18F));
                g2.setColor(Color.white);
                String itemName = item.name;
                String itemSellPriceText = (item.getSellPrice() > 0) ? item.getSellPrice() + "G" : "Cannot be sold";

                int infoAreaY = frameY + frameHeight - gp.tileSize * 2 + 20;
                g2.drawString("Item: " + itemName, slotStartX, infoAreaY);
                g2.drawString("Price: " + itemSellPriceText, slotStartX, infoAreaY + 22);
            }
            itemsDisplayedCount++;
        }

        if (gp.player.inventory.isEmpty() || itemsDisplayedCount == 0) {
            g2.setFont(pressStart.deriveFont(Font.PLAIN, 20F));
            g2.setColor(Color.WHITE);
            String emptyMsg = gp.player.inventory.isEmpty() ? "Inventory is empty." : "No items to sell.";
            int msgX = getXForCenteredText(emptyMsg);
            int msgY = slotStartY + (maxRowsToDisplay * slotTotalWidth) / 2 - g2.getFontMetrics().getHeight() / 2;
            g2.drawString(emptyMsg, msgX, msgY);
        }

        g2.setFont(pressStart.deriveFont(Font.PLAIN, 10F));
        g2.setColor(Color.white);
        String instructions = "[Arrows] Navigate | [Enter] Add to Bin | [Esc] Finish";
        int instructionY = frameY + frameHeight - gp.tileSize / 2 - 5;
        int instructionX = getXForCenteredText(instructions);
        g2.drawString(instructions, instructionX, instructionY);
    }

    private void drawTimedMessage(Graphics2D g2) {
        if (messageOn && this.message != null && !this.message.isEmpty()) {

            float timedMessageFontSize = 15F;
            Font messageFont = (silkScreen != null) ? silkScreen.deriveFont(Font.PLAIN, timedMessageFontSize)
                    : new Font("Arial", Font.PLAIN, (int) timedMessageFontSize);
            g2.setFont(messageFont);
            FontMetrics fm = g2.getFontMetrics(messageFont);

            int textPadding = 15;
            int innerLinePadding = 3;
            int outerBorderThickness = 4;

            int maxContentWidth = gp.screenWidth - (gp.tileSize * 6) - (textPadding * 2) - (innerLinePadding * 2)
                    - (outerBorderThickness * 2);

            List<String> lines = new ArrayList<>();
            String[] paragraphs = this.message.split("\\n");
            for (String paragraph : paragraphs) {
                if (paragraph.trim().isEmpty()) {
                    lines.add("");
                    continue;
                }
                String[] words = paragraph.split(" ");
                StringBuilder currentLine = new StringBuilder();
                for (String word : words) {
                    String testLine = currentLine.length() > 0 ? currentLine.toString() + " " + word : word;
                    if (fm.stringWidth(testLine) <= maxContentWidth) {
                        if (currentLine.length() > 0)
                            currentLine.append(" ");
                        currentLine.append(word);
                    } else {
                        if (currentLine.length() > 0)
                            lines.add(currentLine.toString());
                        currentLine = new StringBuilder(word);
                        while (fm.stringWidth(currentLine.toString()) > maxContentWidth && currentLine.length() > 1) {
                            String part = currentLine.substring(0, currentLine.length() - 1);
                            lines.add(part + "-");
                            currentLine = new StringBuilder(currentLine.substring(currentLine.length() - 1));
                            if (fm.stringWidth(currentLine.toString()) <= maxContentWidth)
                                break;
                        }
                    }
                }
                if (currentLine.length() > 0)
                    lines.add(currentLine.toString());
            }

            int maxLinesToDisplay = 7;
            List<String> displayLines = lines;
            if (lines.size() > maxLinesToDisplay) {
                displayLines = lines.subList(0, maxLinesToDisplay - 1);
                displayLines.add("...(pesan terlalu panjang)");
            }

            int lineHeight = fm.getHeight() + 4;
            int actualContentWidth = 0;
            for (String line : displayLines) {
                actualContentWidth = Math.max(actualContentWidth, fm.stringWidth(line));
            }
            actualContentWidth = Math.min(actualContentWidth, maxContentWidth);

            int mainContentBoxWidth = actualContentWidth + (textPadding * 2);
            int mainContentBoxHeight = displayLines.size() * lineHeight + (textPadding * 2)
                    - (displayLines.isEmpty() ? 0 : 4);

            int blackLineAreaWidth = mainContentBoxWidth + (innerLinePadding * 2);
            int blackLineAreaHeight = mainContentBoxHeight + (innerLinePadding * 2);

            int totalBoxWidth = blackLineAreaWidth + (outerBorderThickness * 2);
            int totalBoxHeight = blackLineAreaHeight + (outerBorderThickness * 2);

            int totalBoxX = (gp.screenWidth - totalBoxWidth) / 2;
            int totalBoxY = (gp.screenHeight / 3) - (totalBoxHeight / 2) - gp.tileSize;
            if (totalBoxY < gp.tileSize / 2)
                totalBoxY = gp.tileSize / 2;

            int blackLineAreaX = totalBoxX + outerBorderThickness;
            int blackLineAreaY = totalBoxY + outerBorderThickness;

            int mainContentBoxX = blackLineAreaX + innerLinePadding;
            int mainContentBoxY = blackLineAreaY + innerLinePadding;

            Color stardewOuterBorder = new Color(101, 67, 33);
            Color stardewThinBlackLine = Color.BLACK;
            Color stardewMainBackground = new Color(245, 222, 179);
            Color stardewTextColor = Color.BLACK;

            g2.setColor(stardewOuterBorder);
            g2.fillRoundRect(totalBoxX, totalBoxY, totalBoxWidth, totalBoxHeight, 10, 10);

            g2.setColor(stardewMainBackground);
            g2.fillRoundRect(mainContentBoxX, mainContentBoxY, mainContentBoxWidth, mainContentBoxHeight, 6, 6);

            g2.setColor(stardewThinBlackLine);
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(mainContentBoxX - 1, mainContentBoxY - 1, mainContentBoxWidth + 2,
                    mainContentBoxHeight + 2, 7, 7);

            g2.setStroke(new BasicStroke(1));

            g2.setColor(stardewTextColor);
            int currentTextY = mainContentBoxY + textPadding + fm.getAscent();
            for (String line : displayLines) {
                if (!line.trim().isEmpty()) {
                    int textX = mainContentBoxX + textPadding;
                    g2.drawString(line, textX, currentTextY);
                }
                currentTextY += lineHeight;
            }

            messageCounter++;
            if (messageCounter > 180) {
                messageCounter = 0;
                messageOn = false;
                this.message = "";
            }
        }
    }

    public void drawCookingScreen() {
        int frameX = gp.tileSize * 2;
        int frameY = gp.tileSize;
        int frameWidth = gp.screenWidth - (gp.tileSize * 4);
        int frameHeight = gp.screenHeight - (gp.tileSize * 2);
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        g2.setColor(Color.white);
        Font baseFont = pressStart != null ? pressStart : new Font("Arial", Font.PLAIN, 20);

        String title;
        if (cookingSubState == 1 && gp.selectedRecipeForCooking != null) {
            title = "Confirm: Cook " + gp.selectedRecipeForCooking.outputFoodName + "?";
        } else {
            title = "Kitchen - Select Recipe";
        }
        g2.setFont(baseFont.deriveFont(Font.BOLD, 22F));
        int titleX = getXForCenteredTextInFrame(title, frameX, frameWidth);
        g2.drawString(title, titleX, frameY + gp.tileSize);

        List<Recipe> displayableRecipes = new ArrayList<>();
        if (gp.player != null && gp.player.recipeUnlockStatus != null) {
            List<Recipe> allRecipes = RecipeManager.getAllRecipes();
            if (allRecipes != null) {
                displayableRecipes = allRecipes.stream()
                        .filter(r -> Boolean.TRUE.equals(gp.player.recipeUnlockStatus.get(r.recipeId)))
                        .collect(Collectors.toList());
            }
        }

        if (cookingSubState == 0 && displayableRecipes.isEmpty()) {
            g2.setFont(baseFont.deriveFont(Font.PLAIN, 18F));
            String noRecipeMsg = "You haven't learned any recipes yet.";
            g2.drawString(noRecipeMsg, getXForCenteredTextInFrame(noRecipeMsg, frameX, frameWidth),
                    frameY + gp.tileSize * 3);
            g2.drawString("[Esc] Exit Kitchen", frameX + gp.tileSize, frameY + frameHeight - gp.tileSize + 10);
            return;
        }

        int listStartX = frameX + gp.tileSize / 2;
        int listStartY = frameY + gp.tileSize * 2 - 10;
        int recipeLineHeight = 30;
        int detailLineHeight = 22;

        int recipeListColumnWidth = (int) (frameWidth * 0.45);

        int gapBetweenColumns = gp.tileSize;
        int detailStartX = listStartX + recipeListColumnWidth + gapBetweenColumns;

        if (cookingSubState == 0) {
            g2.setFont(baseFont.deriveFont(Font.PLAIN, 18F));
            for (int i = 0; i < displayableRecipes.size(); i++) {
                Recipe recipe = displayableRecipes.get(i);
                String recipeName = recipe.outputFoodName;
                int currentY = listStartY + (i * recipeLineHeight);

                if (i == cookingCommandNum) {
                    g2.setColor(Color.yellow);
                    g2.drawString("> " + recipeName, listStartX, currentY);

                    g2.setColor(Color.lightGray);
                    g2.setFont(baseFont.deriveFont(Font.PLAIN, 10F));
                    int detailCurrentY = listStartY;
                    g2.drawString("Ingredients for:", detailStartX, detailCurrentY);
                    detailCurrentY += detailLineHeight;
                    g2.drawString(recipe.outputFoodName, detailStartX, detailCurrentY);
                    detailCurrentY += detailLineHeight;

                    for (Map.Entry<String, Integer> entry : recipe.ingredients.entrySet()) {
                        String ingredientText = "- " + entry.getKey() + ": " + entry.getValue();
                        g2.drawString(ingredientText, detailStartX + 10, detailCurrentY);
                        detailCurrentY += detailLineHeight;
                    }
                    g2.drawString("Fuel: Firewood or Coal", detailStartX + 10, detailCurrentY);
                    detailCurrentY += detailLineHeight;
                    if (recipe.unlockConditionDescription != null
                            && !recipe.unlockConditionDescription.equals("Default/Bawaan")) {
                        g2.drawString("Unlocks: " + recipe.unlockConditionDescription, detailStartX + 10,
                                detailCurrentY);
                    }

                    g2.setColor(Color.white);
                    g2.setFont(baseFont.deriveFont(Font.PLAIN, 18F));
                } else {
                    g2.drawString("  " + recipeName, listStartX, currentY);
                }
            }
            g2.setFont(baseFont.deriveFont(Font.PLAIN, 10F));
            g2.drawString("[Up/Down] Select | [Enter] View/Confirm | [Esc] Exit",
                    listStartX, frameY + frameHeight - gp.tileSize + 10);

        } else if (cookingSubState == 1 && gp.selectedRecipeForCooking != null) {
            Recipe selected = gp.selectedRecipeForCooking;
            g2.setFont(baseFont.deriveFont(Font.PLAIN, 18F));
            int confirmTextY = frameY + gp.tileSize * 3;
            String confirmMsg1 = "Cook: " + selected.outputFoodName + "?";
            String confirmMsg2 = "Energy Cost: 10";
            String confirmMsg3 = "Time to Cook: 1 game hour";

            g2.drawString(confirmMsg1, getXForCenteredTextInFrame(confirmMsg1, frameX, frameWidth), confirmTextY);
            confirmTextY += recipeLineHeight;
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
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        if (currentDialogue != null && !currentDialogue.isEmpty()) {
            if (pressStart != null) {
                g2.setFont(pressStart.deriveFont(Font.PLAIN, 20F));
            } else {
                g2.setFont(new Font("Arial", Font.PLAIN, 20));
            }
            g2.setColor(Color.WHITE);

            int yPosition = gp.screenHeight / 3;
            int lineHeight = g2.getFontMetrics().getHeight() + 8;

            for (String line : currentDialogue.split("\n")) {
                int xPosition = getXForCenteredText(line);
                g2.drawString(line, xPosition, yPosition);
                yPosition += lineHeight;
            }

        } else {
            if (pressStart != null) {
                g2.setFont(pressStart.deriveFont(Font.PLAIN, 22F));
            } else {
                g2.setFont(new Font("Arial", Font.PLAIN, 22));
            }
            g2.setColor(Color.WHITE);
            String wakingUpMsg = "Waking up...";
            int x = getXForCenteredText(wakingUpMsg);
            g2.drawString(wakingUpMsg, x, gp.screenHeight / 2);
        }
    }

    private void drawTimeHUD(Graphics2D g2) {
        if (gp.gameState != gp.playState)
            return;

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 13F));

        String[] labels = { "Day:", "Time:", "Season:", "Weather:" };
        String[] values = {
                String.valueOf(gameClock.getTime().getDay()),
                gameClock.getFormattedTime(),
                gameClock.getCurrentSeason().name(),
                gameClock.getWeather().getWeatherName()
        };

        int padding = 10;
        int lineSpacing = g2.getFontMetrics().getHeight() + 4;

        int labelWidth = Arrays.stream(labels)
                .mapToInt(g2.getFontMetrics()::stringWidth)
                .max()
                .orElse(0);

        int valueWidth = Arrays.stream(values)
                .mapToInt(g2.getFontMetrics()::stringWidth)
                .max()
                .orElse(0);

        int totalWidth = labelWidth + 12 + valueWidth;
        int totalHeight = lineSpacing * labels.length;

        int x = gp.screenWidth - totalWidth - padding;
        int y = padding;

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(
                x - 10,
                y - 10,
                totalWidth + 20,
                totalHeight + 10,
                15,
                15);

        g2.setColor(Color.WHITE);
        for (int i = 0; i < labels.length; i++) {
            int textY = y + lineSpacing * (i + 1) - 4;

            g2.drawString(labels[i], x, textY);

            g2.drawString(values[i], x + labelWidth + 12, textY);
        }
    }

    public void drawLocationHUD(Graphics2D g2) {
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));

        String currentLocation = "Unknown";

        System.out.println("[UI] DEBUG - drawLocationHUD called");
        System.out.println("[UI] DEBUG - currentMapIndex: " + gp.currentMapIndex);

        if (gp.player != null) {
            String playerLocation = gp.player.getLocation();
            System.out.println("[UI] DEBUG - player.getLocation(): '" + playerLocation + "'");

            if (playerLocation != null && !playerLocation.isEmpty() && !playerLocation.equals("Unknown")) {
                currentLocation = playerLocation;
            } else {

                if (gp.currentMapIndex >= 0 && gp.currentMapIndex < gp.mapInfos.size()) {
                    String mapName = gp.mapInfos.get(gp.currentMapIndex).getMapName();
                    System.out.println("[UI] DEBUG - fallback mapName: '" + mapName + "'");
                    if (mapName != null && !mapName.isEmpty()) {
                        currentLocation = mapName;
                    }
                }
            }
        }

        String locationText = "Location: " + currentLocation;

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

    public void startSelfDialogue(String text) {
        this.currentDialogue = text;
        gp.currentInteractingNPC = null;
        gp.gameState = gp.dialogueState;
        System.out.println("DEBUG: UI.startSelfDialogue - Text: " + text + ", GameState set to: " + gp.gameState);
        if (gp.gameClock != null && gp.gameClock.getTime() != null) {
            if (!gp.gameClock.isPaused()) {
                gp.gameClock.pauseTime();
                System.out.println("DEBUG: UI.startSelfDialogue - GameClock paused.");
            } else {
                System.out.println("DEBUG: UI.startSelfDialogue - GameClock was already paused.");
            }
        } else {
            System.out.println("DEBUG: UI.startSelfDialogue - GameClock or Time is null, cannot pause.");
        }
    }

    public void drawBuyingScreen() {
        if (!(gp.currentInteractingNPC instanceof NPC_EMILY))
            return;
        NPC_EMILY emily = (NPC_EMILY) gp.currentInteractingNPC;

        int frameX = gp.tileSize;
        int frameY = gp.tileSize;
        int frameWidth = gp.screenWidth - (gp.tileSize * 2);
        int frameHeight = gp.screenHeight - (gp.tileSize * 2);
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        g2.setColor(Color.white);
        Font baseFont = pressStart != null ? pressStart : new Font("Arial", Font.PLAIN, 20);
        g2.setFont(baseFont.deriveFont(Font.BOLD, 24F));
        String title = "Emily's Shop";
        g2.drawString(title, getXForCenteredTextInFrame(title, frameX, frameWidth), frameY + gp.tileSize - 5);

        g2.setFont(baseFont.deriveFont(Font.PLAIN, 16F));
        g2.drawString("Your Gold: " + gp.player.gold + "G", frameX + gp.tileSize / 2,
                (int) (frameY + gp.tileSize * 1.5f));
        int padding = 20;
        int listStartX = frameX + padding;
        int listStartY = (int) (frameY + gp.tileSize * 2.5f);
        int itemLineHeight = 22;
        int usableWidth = frameWidth - (padding * 2);
        int itemListWidth = usableWidth * 2 / 5;
        int detailAreaWidth = usableWidth * 3 / 5 - padding;

        int detailsStartX = listStartX + itemListWidth + padding;
        int maxDetailWidth = Math.min(detailAreaWidth, frameX + frameWidth - detailsStartX - padding);

        if (emily.shopInventory.isEmpty()) {
            g2.setFont(baseFont.deriveFont(Font.PLAIN, 18F));
            String noItemMsg = "Sorry, nothing in stock right now!";
            g2.drawString(noItemMsg, getXForCenteredTextInFrame(noItemMsg, frameX, frameWidth),
                    frameY + frameHeight / 2);
        } else {

            int availableListHeight = frameHeight - gp.tileSize * 4;
            int maxVisibleItems = availableListHeight / itemLineHeight;
            int scrollOffset = 0;
            if (storeCommandNum >= maxVisibleItems) {
                scrollOffset = storeCommandNum - maxVisibleItems + 1;
            }

            for (int i = 0; i < emily.shopInventory.size(); i++) {
                if (i < scrollOffset || i >= scrollOffset + maxVisibleItems) {
                    continue;
                }
                OBJ_Item shopItem = emily.shopInventory.get(i);
                String itemName = shopItem.name;
                int itemPrice = shopItem.getBuyPrice();
                String displayText = itemName + " - " + itemPrice + "G";

                int currentY = listStartY + ((i - scrollOffset) * itemLineHeight);

                if (i == storeCommandNum) {
                    g2.setColor(Color.YELLOW);
                    g2.setFont(baseFont.deriveFont(Font.PLAIN, 16F));
                    g2.drawString("> " + displayText, listStartX, currentY);

                    int detailBackgroundHeight = gp.tileSize * 2 + 40;
                    g2.setColor(new Color(50, 50, 50, 150));
                    g2.fillRoundRect(detailsStartX - 10, listStartY - 10,
                            maxDetailWidth + 20, detailBackgroundHeight, 10, 10);
                    g2.setColor(Color.WHITE);
                    g2.drawRoundRect(detailsStartX - 10, listStartY - 10,
                            maxDetailWidth + 20, detailBackgroundHeight, 10, 10);

                    int imageSize = gp.tileSize;
                    if (shopItem.down1 != null) {
                        g2.drawImage(shopItem.down1, detailsStartX, listStartY + 5, imageSize, imageSize, null);
                    } else {

                        g2.setColor(Color.GRAY);
                        g2.fillRect(detailsStartX, listStartY + 5, imageSize, imageSize);
                        g2.setColor(Color.WHITE);
                        g2.setFont(baseFont.deriveFont(Font.PLAIN, 10F));
                        g2.drawString("No", detailsStartX + 5, listStartY + imageSize / 2);
                        g2.drawString("Image", detailsStartX + 5, listStartY + imageSize / 2 + 12);
                    }

                    int textStartX = detailsStartX + imageSize + 15;
                    int textStartY = listStartY + 20;
                    int maxTextWidth = maxDetailWidth - imageSize - 25;

                    if (textStartX + maxTextWidth > frameX + frameWidth - padding) {
                        maxTextWidth = frameX + frameWidth - padding - textStartX;
                    }

                    g2.setColor(Color.WHITE);
                    g2.setFont(baseFont.deriveFont(Font.BOLD, 14F));

                    String displayName = shopItem.name;
                    FontMetrics fm = g2.getFontMetrics();
                    if (fm.stringWidth(displayName) > maxTextWidth) {
                        while (fm.stringWidth(displayName + "...") > maxTextWidth && displayName.length() > 1) {
                            displayName = displayName.substring(0, displayName.length() - 1);
                        }
                        displayName += "...";
                    }
                    g2.drawString(displayName, textStartX, textStartY);

                    g2.setFont(baseFont.deriveFont(Font.PLAIN, 12F));
                    g2.setColor(Color.YELLOW);
                    g2.drawString("Price: " + itemPrice + "G", textStartX, textStartY + 18);

                    g2.setColor(Color.LIGHT_GRAY);
                    g2.setFont(baseFont.deriveFont(Font.PLAIN, 10F));
                    String itemInfo = "";

                    if (fm.stringWidth(itemInfo) > maxTextWidth) {
                        while (fm.stringWidth(itemInfo + "...") > maxTextWidth && itemInfo.length() > 1) {
                            itemInfo = itemInfo.substring(0, itemInfo.length() - 1);
                        }
                        itemInfo += "...";
                    }
                    g2.drawString(itemInfo, textStartX, textStartY + 36);

                } else {
                    g2.setColor(Color.WHITE);
                    g2.setFont(baseFont.deriveFont(Font.PLAIN, 16F));

                    FontMetrics fm = g2.getFontMetrics();
                    if (fm.stringWidth(displayText) > itemListWidth - 30) {
                        while (fm.stringWidth(displayText + "...") > itemListWidth - 30 && displayText.length() > 1) {
                            displayText = displayText.substring(0, displayText.length() - 1);
                        }
                        displayText += "...";
                    }
                    g2.drawString("  " + displayText, listStartX, currentY);
                }
            }
        }

        g2.setFont(baseFont.deriveFont(Font.PLAIN, 10F));
        g2.setColor(Color.WHITE);
        String instructions = "[Up/Down] Select | [Enter] Buy | [Esc] Exit";
        g2.drawString(instructions, listStartX, frameY + frameHeight - padding);
    }

    public void resetDialoguePagination() {
        currentDialogueLines.clear();
        dialogueCurrentPage = 0;
        lastProcessedDialogue = "";
    }

    public List<String> getCurrentDialogueLines() {
        return currentDialogueLines;
    }

    public int getDialogueCurrentPage() {
        return dialogueCurrentPage;
    }

    public void setDialogueCurrentPage(int page) {
        this.dialogueCurrentPage = page;
    }

    public int getDialogueLinesPerPage() {
        return dialogueLinesPerPage;
    }

    public void drawEndGameStatisticsScreen(Graphics2D g2) {

        drawSharedBackground(g2);
        g2.setColor(new Color(0, 0, 0, 230));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(pressStart.deriveFont(Font.BOLD, 24F));
        g2.setColor(Color.WHITE);
        String title = "End Game Statistics";
        int titleX = getXForCenteredText(title);
        int titleY = gp.tileSize;
        g2.drawString(title, titleX, titleY);

        Font headerFont = pressStart.deriveFont(Font.BOLD, 12F);
        Font statTextFont = pressStart.deriveFont(Font.PLAIN, 8F);
        Font npcNameFont = pressStart.deriveFont(Font.BOLD, 9F);
        int lineHeight = 15;
        int sectionSpacing = gp.tileSize / 4;

        int contentStartY = titleY + g2.getFontMetrics().getHeight() + gp.tileSize / 3;
        int paddingHorizontal = gp.tileSize / 3;

        int totalUsableWidth = gp.screenWidth - (paddingHorizontal * 2);
        int columnWidth = (totalUsableWidth - (paddingHorizontal * 2)) / 3;

        class StatDrawer {
            private int currentY;
            private int startX;
            private final Graphics2D g;
            private final Font hFont;
            private final Font tFont;
            private final int lht;
            private final int lhs;

            public StatDrawer(Graphics2D g, int startX, int startY, Font headerFont, Font textFont, int lineHeightText,
                    int lineHeightSection) {
                this.g = g;
                this.startX = startX;
                this.currentY = startY;
                this.hFont = headerFont;
                this.tFont = textFont;
                this.lht = lineHeightText;
                this.lhs = lineHeightSection;
            }

            public void drawStat(String label, String value) {
                g.setFont(tFont);
                g.setColor(Color.LIGHT_GRAY);
                int labelWidth = g.getFontMetrics(tFont).stringWidth(label + ": ");
                if (startX + labelWidth + g.getFontMetrics(tFont).stringWidth(value) > startX + columnWidth - 5) {
                    g.drawString(label + ":", startX, currentY);
                    currentY += lht;
                    g.setColor(Color.WHITE);
                    g.drawString(value, startX + 10, currentY);
                } else {
                    g.drawString(label + ":", startX, currentY);
                    g.setColor(Color.WHITE);
                    g.drawString(value, startX + labelWidth + 2, currentY);
                }
                currentY += lht;
            }

            public void drawStat(String label, long value) {
                drawStat(label, String.valueOf(value));
            }

            public void drawHeader(String header) {
                currentY += lhs;
                g.setFont(hFont);
                g.setColor(new Color(255, 215, 0));
                g.drawString(header, startX, currentY);
                currentY += (int) (lht * 1.2);
                g.setColor(Color.WHITE);
            }

            public int getCurrentY() {
                return currentY;
            }

            public void advanceY(double multiplier) {
                currentY += (int) (lht * multiplier);
            }

            public void setCurrentY(int y) {
                this.currentY = y;
            }

            public void setStartX(int x) {
                this.startX = x;
            }
        }

        int statStartXCol1 = paddingHorizontal;
        StatDrawer statsDrawer = new StatDrawer(g2, statStartXCol1, contentStartY, headerFont, statTextFont, lineHeight,
                sectionSpacing);

        statsDrawer.drawHeader("~ General ~");
        if (gp.gameClock != null && gp.gameClock.getTime() != null && gp.player != null) {
            statsDrawer.drawStat("Days Played", gp.gameClock.getTime().getDay());
        } else {
            statsDrawer.drawStat("Days Played", "N/A");
        }

        statsDrawer.drawHeader("~ Financial ~");
        if (gp.player != null) {
            statsDrawer.drawStat("Total Income", gp.player.totalIncome + "G");
            statsDrawer.drawStat("Total Expenditure", gp.player.totalExpenditure + "G");
            statsDrawer.drawStat("Net Worth", gp.player.gold + "G");

            statsDrawer.advanceY(0.2);
            for (Season season : Season.values()) {
                long income = gp.player.seasonalIncome.getOrDefault(season, 0L);
                long expenditure = gp.player.seasonalExpenditure.getOrDefault(season, 0L);
                int timesPlayed = gp.player.seasonPlayed.getOrDefault(season, 0);

                float avgIncome = (timesPlayed > 0) ? (float) income / timesPlayed : 0;
                float avgExpenditure = (timesPlayed > 0) ? (float) expenditure / timesPlayed : 0;

                Font italicFont = statTextFont.deriveFont(Font.ITALIC, 8F);
                g2.setFont(italicFont);
                statsDrawer.drawStat(String.format("%s Inc (x%d)", season.name().substring(0, 3), timesPlayed),
                        String.format("%.0fG", avgIncome));
                statsDrawer.drawStat(String.format("%s Exp (x%d)", season.name().substring(0, 3), timesPlayed),
                        String.format("%.0fG", avgExpenditure));
            }
        } else {
            statsDrawer.drawStat("Financial Data", "N/A");
        }

        int statStartXCol2 = statStartXCol1 + columnWidth + paddingHorizontal;
        statsDrawer.setStartX(statStartXCol2);
        statsDrawer.setCurrentY(contentStartY);

        statsDrawer.drawHeader("~ Agricultural ~");
        if (gp.player != null) {
            statsDrawer.drawStat("Crops Harvested", gp.player.totalHarvested);
        } else {
            statsDrawer.drawStat("Crops Harvested", "N/A");
        }

        statsDrawer.drawHeader("~ Fishing ~");
        if (gp.player != null) {
            statsDrawer.drawStat("Total Fish", gp.player.totalFishCaught);
            statsDrawer.drawStat("  Common", gp.player.totalCommonFishCaught);
            statsDrawer.drawStat("  Regular", gp.player.totalRegularFishCaught);
            statsDrawer.drawStat("  Legendary", gp.player.totalLegendaryFishCaught);
        } else {
            statsDrawer.drawStat("Fishing Data", "N/A");
        }

        int statStartXCol3 = statStartXCol2 + columnWidth + paddingHorizontal;
        statsDrawer.setStartX(statStartXCol3);
        statsDrawer.setCurrentY(contentStartY);

        statsDrawer.drawHeader("~ NPC Relationships ~");
        if (gp.allNpcsInWorld == null || gp.allNpcsInWorld.isEmpty()) {
            statsDrawer.drawStat("No NPCs met", "");
        } else {
            boolean npcDataAvailable = false;
            for (NPC npc : gp.allNpcsInWorld) {
                if (npc == null || gp.player == null)
                    continue;
                npcDataAvailable = true;

                g2.setFont(npcNameFont);
                g2.setColor(new Color(173, 216, 230));
                g2.drawString(npc.name, statStartXCol3, statsDrawer.getCurrentY());
                statsDrawer.advanceY(0.95);

                statsDrawer.drawStat(" Hearts", npc.currentHeartPoints + "/" + npc.maxHeartPoints);
                statsDrawer.drawStat(" Chats", gp.player.npcChatFrequency.getOrDefault(npc.name, 0));
                statsDrawer.drawStat(" Gifts", gp.player.npcGiftFrequency.getOrDefault(npc.name, 0));

                if (npc.isMarriageCandidate) {
                    String marriageStatus = "Single";
                    if (npc.marriedToPlayer)
                        marriageStatus = "Married to You";
                    else if (npc.engaged)
                        marriageStatus = "Engaged";
                    statsDrawer.drawStat(" Status", marriageStatus);
                }
                statsDrawer.advanceY(0.3);
            }

            if (!npcDataAvailable) {
                statsDrawer.drawStat("No valid NPC data", "");
            }
        }

        int exitY = gp.screenHeight - 25;
        String exitMessage = "Press ENTER to Continue Playing | Press ESC to Return to Menu";
        g2.setFont(pressStart.deriveFont(Font.PLAIN, 9F));
        int exitX = getXForCenteredText(exitMessage);
        g2.setColor(new Color(255, 255, 255, 180));
        g2.drawString(exitMessage, exitX, exitY);
    }
}