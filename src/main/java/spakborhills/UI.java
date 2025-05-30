package spakborhills;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import spakborhills.cooking.Recipe;
import spakborhills.cooking.RecipeManager;
import spakborhills.entity.Entity;
import spakborhills.entity.NPC;
import spakborhills.entity.NPC_EMILY;
import spakborhills.enums.Season;
import spakborhills.interfaces.Edible;
import spakborhills.object.OBJ_Item;

public class UI {
    public int mapSelectionState = 0;
    GamePanel gp;
    GameClock gameClock;
    Font silkScreen, pressStart, mineCraftia;
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

    public int genderSelectionIndex = 0;

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

    private int[][] cachedMapPositions = null;
    private int lastTileSize = -1;

    private FontMetrics cachedFontMetrics = null;
    private Font lastCachedFont = null;

    private BufferedImage worldMapUI;
    Color themecolor = new Color(102, 63, 12);

    private static final Map<String, BufferedImage> imageCache = new HashMap<>();

    public UI(GamePanel gp, GameClock gameClock) {
        this.gp = gp;
        this.gameClock = gameClock;

        InputStream inputStream = getClass().getResourceAsStream("/fonts/Minecraftia-Regular.ttf");
        try {
            mineCraftia = Font.createFont(Font.TRUETYPE_FONT, inputStream);
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
            InputStream inputStream = getClass().getResourceAsStream("/background/welcome.png");

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

        drawSharedBackground(g2, gp.gameState);
        if (gp.gameState == gp.titleState) {
            drawTitleScreen();
        } else if (gp.gameState == gp.playerNameInputState) {
            drawPlayerNameInputScreen();
            drawTimedMessage(g2);
        } else if (gp.gameState == gp.genderSelectionState) {
            drawGenderSelectionScreen();
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

        int frameBaseWidth = gp.screenWidth - (gp.tileSize * 6);
        int frameBaseHeight = (int) (gp.tileSize * 3.75);

        Color stardewOuterBorder = new Color(101, 67, 33);
        Color stardewThinBlackLine = Color.BLACK;
        Color stardewMainBackground = new Color(245, 222, 179);
        Color stardewTextColor = Color.BLACK;

        int textPadding = 6;
        int innerLinePadding = 2;
        int outerBorderThickness = 3;

        int totalBoxWidth = frameBaseWidth;
        int totalBoxHeight = frameBaseHeight;

        int totalBoxX = (gp.screenWidth - totalBoxWidth) / 2;
        int totalBoxY = (gp.screenHeight / 2) - (totalBoxHeight / 2) - (int) (gp.tileSize * 2.0);
        if (totalBoxY < gp.tileSize / 2)
            totalBoxY = gp.tileSize / 2;

        int blackLineAreaX = totalBoxX + outerBorderThickness;
        int blackLineAreaY = totalBoxY + outerBorderThickness;

        int mainContentBoxWidth = totalBoxWidth - (outerBorderThickness * 2) - (innerLinePadding * 2);
        int mainContentBoxHeight = totalBoxHeight - (outerBorderThickness * 2) - (innerLinePadding * 2);
        int mainContentBoxX = blackLineAreaX + innerLinePadding;
        int mainContentBoxY = blackLineAreaY + innerLinePadding;

        g2.setColor(stardewOuterBorder);
        g2.fillRoundRect(totalBoxX, totalBoxY, totalBoxWidth, totalBoxHeight, 8, 8);

        g2.setColor(stardewMainBackground);
        g2.fillRoundRect(mainContentBoxX, mainContentBoxY, mainContentBoxWidth, mainContentBoxHeight, 5, 5);

        g2.setColor(stardewThinBlackLine);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(mainContentBoxX - 1, mainContentBoxY - 1, mainContentBoxWidth + 2, mainContentBoxHeight + 2, 6,
                6);
        g2.setStroke(new BasicStroke(1));

        int contentAreaX = mainContentBoxX + textPadding;
        int contentAreaY = mainContentBoxY + textPadding;
        int contentAreaWidth = mainContentBoxWidth - (textPadding * 2);
        int contentAreaHeight = mainContentBoxHeight - (textPadding * 2);

        int fishImageSize = (int) (gp.tileSize * 1.25);

        int imageAreaWidth = fishImageSize + textPadding;
        int textBlockMaxWidth = contentAreaWidth - imageAreaWidth;

        int fishImageX = contentAreaX + textBlockMaxWidth + (imageAreaWidth - fishImageSize) / 2;
        int fishImageY = contentAreaY + (contentAreaHeight - fishImageSize) / 2;

        if (gp.player.fishToCatchInMinigame != null && gp.player.fishToCatchInMinigame.down1 != null) {
            BufferedImage fishImage = gp.player.fishToCatchInMinigame.down1;
            g2.drawImage(fishImage, fishImageX, fishImageY, fishImageSize, fishImageSize, null);
        } else {
            g2.setColor(stardewThinBlackLine);
            Font placeholderFont = pressStart != null ? pressStart.deriveFont(Font.PLAIN, 7F)
                    : new Font("Arial", Font.PLAIN, 7);
            g2.setFont(placeholderFont);
            String noImgTxt = "Fish?";
            FontMetrics pfm = g2.getFontMetrics(placeholderFont);
            int sLen = pfm.stringWidth(noImgTxt);
            g2.drawString(noImgTxt, fishImageX + (fishImageSize - sLen) / 2,
                    fishImageY + fishImageSize / 2 + pfm.getAscent() / 2);
        }

        g2.setColor(stardewTextColor);
        Font baseFont = pressStart != null ? pressStart : new Font("Arial", Font.PLAIN, 10);
        FontMetrics fmSmall = g2.getFontMetrics(baseFont.deriveFont(Font.PLAIN, 7F));
        FontMetrics fmInfo = g2.getFontMetrics(baseFont.deriveFont(Font.PLAIN, 9F));

        int currentTextY = contentAreaY + fmInfo.getAscent();
        int textBlockX = contentAreaX;

        int spaceForBottomText = fmSmall.getHeight() * 2 + 4;

        g2.setFont(baseFont.deriveFont(Font.PLAIN, 9F));
        if (gp.player.fishingInfoMessage != null && !gp.player.fishingInfoMessage.isEmpty()) {
            List<String> infoLines = wrapText(gp.player.fishingInfoMessage, textBlockMaxWidth, fmInfo);
            for (String line : infoLines) {
                if (currentTextY + fmInfo.getHeight() > contentAreaY + contentAreaHeight - spaceForBottomText)
                    break;
                g2.drawString(line, textBlockX, currentTextY);
                currentTextY += fmInfo.getHeight();
            }
        }
        currentTextY += gp.tileSize / 6;

        g2.setFont(baseFont.deriveFont(Font.BOLD, 11F));
        String displayInput = gp.player.fishingPlayerInput;
        if (System.currentTimeMillis() % 1000 < 500) {
            displayInput += "_";
        } else {
            displayInput += " ";
        }
        if (currentTextY + g2.getFontMetrics().getHeight() <= contentAreaY + contentAreaHeight - spaceForBottomText) {
            g2.drawString(displayInput, textBlockX, currentTextY);
            currentTextY += g2.getFontMetrics().getHeight() + gp.tileSize / 6;
        }

        g2.setFont(baseFont.deriveFont(Font.ITALIC, 9F));
        if (gp.player.fishingFeedbackMessage != null && !gp.player.fishingFeedbackMessage.isEmpty()) {
            List<String> feedbackLines = wrapText(gp.player.fishingFeedbackMessage, textBlockMaxWidth,
                    g2.getFontMetrics());
            for (String line : feedbackLines) {
                if (currentTextY + g2.getFontMetrics().getHeight() > contentAreaY + contentAreaHeight
                        - spaceForBottomText)
                    break;
                g2.drawString(line, textBlockX, currentTextY);
                currentTextY += g2.getFontMetrics().getHeight();
            }
        }

        int bottomAreaStartY = mainContentBoxY + mainContentBoxHeight - textPadding;

        g2.setFont(baseFont.deriveFont(Font.PLAIN, 7F));
        String instructions = "[0-9] [Ent] [Esc]";
        int instructionsY = bottomAreaStartY - fmSmall.getDescent() + 1;
        g2.drawString(instructions, textBlockX, instructionsY);

        g2.setFont(baseFont.deriveFont(Font.PLAIN, 8F));
        int attemptsLeft = gp.player.fishingMaxTry - gp.player.fishingCurrentAttempts;
        String attemptsText = "Try: " + attemptsLeft + "/" + gp.player.fishingMaxTry;
        int attemptsY = instructionsY - fmSmall.getHeight();
        g2.drawString(attemptsText, textBlockX, attemptsY);
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

    public void drawSharedBackground(Graphics2D g2, int gameState) {
        BufferedImage backgroundImage = null;
        InputStream inputStream = null;

        try {
            if (gameState == gp.endGameState) {
                inputStream = getClass().getResourceAsStream("/background/welcome.png");
            } else if (gameState == gp.helpPageState) {
                inputStream = getClass().getResourceAsStream("/background/help.png");
            } else if (gameState == gp.titleState) {
                if (this.mapSelectionState == 0) {
                    inputStream = getClass().getResourceAsStream("/background/welcome.png");
                } else if (this.mapSelectionState == 1) {
                    inputStream = getClass().getResourceAsStream("/background/world_map.png");
                }
            } else if (gameState == gp.creditPageState) {
                inputStream = getClass().getResourceAsStream("/background/credits.png");
            } else if (gameState == gp.playerNameInputState) {
                inputStream = getClass().getResourceAsStream("/background/input_player.png");
            } else if (gameState == gp.farmNameInputState) {
                inputStream = getClass().getResourceAsStream("/background/input_farm.png");
            } else if (gameState == gp.genderSelectionState) {
                inputStream = getClass().getResourceAsStream("/background/input_gender.png");
            }    

            if (inputStream != null) {
                backgroundImage = ImageIO.read(inputStream);
            } else {
                // System.err.println("Cannot load the background for state: " + gameState);

                return;
            }
            g2.drawImage(backgroundImage, 0, 0, gp.screenWidth, gp.screenHeight, null);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    public void drawPlayerGold() {
        String goldText = "" + gp.player.gold;
        float scale = 0.8f;
        int padding = Math.round(18 * scale);
        int iconPadding = Math.round(8 * scale);
        int boxHeight = Math.round(38 * scale);
        int iconSize = Math.round(28 * scale);

        Font goldFont = pressStart.deriveFont(Font.BOLD, 15F * scale);
        g2.setFont(goldFont);
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(goldText);

        int boxWidth = iconSize + iconPadding + textWidth + padding * 2;

        int energyBarX = gp.tileSize / 2;
        int energyBarY = gp.tileSize / 2;
        int segmentWidth = gp.tileSize / 2;
        int segmentSpacing = 2;
        int totalSegments = 10;
        int barTotalWidth = totalSegments * (segmentWidth + segmentSpacing) - segmentSpacing;
        int barTotalHeight = segmentWidth - 5;

        int x = energyBarX - 7;
        int y = energyBarY + 8 + barTotalHeight + 3;

        g2.setColor(new Color(0, 0, 0, 110));
        g2.fillRoundRect(x + 3, y + 4, boxWidth, boxHeight, Math.round(18 * scale), Math.round(18 * scale));

        g2.setColor(new Color(30, 30, 40, 200));
        g2.fillRoundRect(x, y, boxWidth, boxHeight, Math.round(18 * scale), Math.round(18 * scale));

        g2.setColor(new Color(255, 255, 255, 60));
        g2.setStroke(new BasicStroke(2f * scale));
        g2.drawRoundRect(x, y, boxWidth, boxHeight, Math.round(18 * scale), Math.round(18 * scale));

        int contentWidth = iconSize + iconPadding + textWidth;
        int contentX = x + (boxWidth - contentWidth) / 2;
        int iconX = contentX;
        int iconY = y + (boxHeight - iconSize) / 2 + 1;

        try {
            InputStream inputStream = getClass().getResourceAsStream("/objects/gold.png");
            BufferedImage coinImage = ImageIO.read(inputStream);
            g2.drawImage(coinImage, iconX, iconY, iconSize, iconSize, null);
        } catch (IOException e) {
            g2.setColor(new Color(255, 215, 0));
            g2.fillOval(iconX, iconY, iconSize, iconSize);
        }

        int textX = iconX + iconSize + iconPadding;
        int textY = y + (boxHeight + fm.getAscent()) / 2 + 1;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.drawString(goldText, textX + 2, textY + 2);
        g2.setColor(Color.WHITE);
        g2.drawString(goldText, textX, textY);
    }

    private int[][] getCachedMapPositions() {

        if (cachedMapPositions == null || gp.tileSize != lastTileSize) {
            lastTileSize = gp.tileSize;
            cachedMapPositions = new int[][] {

                    { gp.tileSize * 5 / 2, gp.tileSize * 5 + 8 },
                    { gp.tileSize * 5 / 2 + gp.tileSize * 5 / 2 + 4, gp.tileSize * 5 + 8 },
                    { gp.tileSize * 5 / 2 + gp.tileSize * 5 / 2 + 2 + gp.tileSize * 3 + 5, gp.tileSize * 5 + 8 },
                    { gp.tileSize * 5 / 2 + gp.tileSize * 5 / 2 + 2 + gp.tileSize * 3 + 1 + gp.tileSize * 3,
                            gp.tileSize * 5 + 8 },

                    { gp.tileSize * 5 / 2, gp.tileSize * 5 + gp.tileSize * 5 / 2 - 3 },
                    { gp.tileSize * 5 / 2 + gp.tileSize * 5 / 2 + 2, gp.tileSize * 5 + gp.tileSize * 5 / 2 - 3 },
                    { gp.tileSize * 5 / 2 + gp.tileSize * 5 / 2 + 2 + gp.tileSize * 3,
                            gp.tileSize * 5 + gp.tileSize * 5 / 2 - 3 },
                    { gp.tileSize * 5 / 2 + gp.tileSize * 5 / 2 + 2 + gp.tileSize * 3 + gp.tileSize * 3,
                            gp.tileSize * 5 + gp.tileSize * 5 / 2 - 3 },

                    { gp.tileSize * 5 / 2 + gp.tileSize + 3,
                            gp.tileSize * 5 + 7 + gp.tileSize * 5 / 2 + 2 + gp.tileSize * 2 },
                    { gp.tileSize * 5 / 2 + gp.tileSize * 4 + 3,
                            gp.tileSize * 5 + 7 + gp.tileSize * 5 / 2 + 2 + gp.tileSize * 2 },
                    { gp.tileSize * 5 / 2 + gp.tileSize * 3 + gp.tileSize * 2 + gp.tileSize * 2 + 3,
                            gp.tileSize * 5 + 7 + gp.tileSize * 5 / 2 + gp.tileSize * 2 + 2 }
            };
        }
        return cachedMapPositions;
    }

    private void createWorldMapUI() {
        int width = gp.screenWidth;
        int height = gp.screenHeight;
        worldMapUI = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2Buffer = worldMapUI.createGraphics();
        g2Buffer.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2Buffer.setColor(themecolor);
        g2Buffer.setFont(g2Buffer.getFont().deriveFont(Font.PLAIN, 12F));

        int[][] positions = getCachedMapPositions();
        g2Buffer.dispose();
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
            int x = getXForCenteredText(text) + 5;
            int y = gp.tileSize * 3 + 5;

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

            text = "CREDITS";
            x = getXForCenteredText(text);
            y += gp.tileSize;
            g2.setColor(Color.black);
            g2.drawString(text, x + 5, y + 5);
            g2.setColor(Color.white);
            g2.drawString(text, x, y);
            if (commandNumber == 2) {
                g2.drawString(">", x - gp.tileSize, y);
                if (gp.keyH.enterPressed) {
                    gp.gameState = gp.creditPageState;
                }
            }

            text = "QUIT";
            x = getXForCenteredText(text);
            y += gp.tileSize;
            g2.setColor(Color.black);
            g2.drawString(text, x + 5, y + 5);
            g2.setColor(Color.white);
            g2.drawString(text, x, y);
            if (commandNumber == 3) {
                g2.drawString(">", x - gp.tileSize, y);
            }
        } else if (mapSelectionState == 1) {
            createWorldMapUI();
            g2.drawImage(worldMapUI, 0, 0, null);
            Color themecolor = new Color(102, 63, 12);
            g2.setColor(themecolor);
            g2.setFont(pressStart.deriveFont(Font.BOLD, 12F));

            int[][] positions = getCachedMapPositions();
            if (commandNumber >= 0 && commandNumber < positions.length) {
                g2.drawString(">", positions[commandNumber][0], positions[commandNumber][1]);
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

    public void drawCreditPage(Graphics2D g2) {
        drawSharedBackground(g2, gp.creditPageState);
        g2.setColor(new Color(0, 0, 0, 0));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
    }

    public void drawHelp(Graphics2D g2) {
        drawSharedBackground(g2, gp.helpPageState);
        g2.setColor(new Color(0, 0, 0, 0));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
    }

    public void drawPlayerNameInputScreen() {
        drawSharedBackground(g2, gp.playerNameInputState);
        g2.setColor(new Color(0, 0, 0, 0));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(pressStart.deriveFont(Font.PLAIN, 30F));
        g2.setColor(themecolor);

        String displayText = playerNameInput;
        if (System.currentTimeMillis() % 1000 < 500) {
            displayText += "_";
        } else {
            displayText += " ";
        }

        g2.setFont(pressStart.deriveFont(Font.PLAIN, 28F));
        int textWidth = (int) g2.getFontMetrics().getStringBounds(displayText, g2).getWidth();
        int x = gp.screenWidth / 2 - textWidth / 2;
        int y = gp.screenHeight / 2;
        g2.drawString(displayText, x, y);
    }

    public void drawFarmNameInputScreen() {
        drawSharedBackground(g2, gp.farmNameInputState);
        g2.setColor(new Color(0, 0, 0, 0));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setColor(themecolor);

        String displayText = farmNameInput;
        if (System.currentTimeMillis() % 1000 < 500) {
            displayText += "_";
        } else {
            displayText += " ";
        }

        g2.setFont(pressStart.deriveFont(Font.PLAIN, 28F));
        int textWidth = (int) g2.getFontMetrics().getStringBounds(displayText, g2).getWidth();
        int x = gp.screenWidth / 2 - textWidth / 2;
        int y = gp.screenHeight / 2;
        g2.drawString(displayText, x, y);
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
        int y = gp.tileSize / 2 - 15;
        int segmentWidth = gp.tileSize / 2;
        int segmentHeight = gp.tileSize / 2 - 5;
        int segmentSpacing = 2;
        int totalSegments = 10;
        int barTotalWidth = totalSegments * (segmentWidth + segmentSpacing) - segmentSpacing;
        int barTotalHeight = segmentHeight;

        int filledSegments = 0;
        if (gp.player.MAX_POSSIBLE_ENERGY > 0) {
            double energyPerSegment = (double) gp.player.MAX_POSSIBLE_ENERGY / totalSegments;
            if (energyPerSegment > 0) {
                filledSegments = (int) Math.ceil(gp.player.currentEnergy / energyPerSegment);
            }
        }

        // --- SHADOW & BACKGROUND: Ikuti style gold box ---
        int boxX = x - 7;
        int boxY = y;
        int boxWidth = barTotalWidth + 14;
        int boxHeight = barTotalHeight + 16;

        // Shadow
        g2.setColor(new Color(0, 0, 0, 110));
        g2.fillRoundRect(boxX + 3, boxY + 4, boxWidth, boxHeight, 18, 18);

        // Background
        g2.setColor(new Color(30, 30, 40, 200));
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 18, 18);

        // Border
        g2.setColor(new Color(255, 255, 255, 60));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 18, 18);

        // --- END SHADOW & BACKGROUND ---

        double partialFill = 0;
        if (gp.player.MAX_POSSIBLE_ENERGY > 0) {
            double energyPerSegment = (double) gp.player.MAX_POSSIBLE_ENERGY / totalSegments;
            if (energyPerSegment > 0 && filledSegments > 0) {
                double currentSegmentEnergy = gp.player.currentEnergy - ((filledSegments - 1) * energyPerSegment);
                partialFill = Math.min(1.0, currentSegmentEnergy / energyPerSegment);
            }
        }

        g2.setFont(pressStart.deriveFont(Font.BOLD, 14f));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        y += 8;

        // Segments
        for (int i = 0; i < totalSegments; i++) {
            int currentSegmentX = x + (i * (segmentWidth + segmentSpacing));

            g2.setColor(new Color(40, 40, 40, 180));
            g2.fillRoundRect(currentSegmentX, y, segmentWidth, segmentHeight, 4, 4);

            g2.setColor(new Color(80, 80, 80, 200));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(currentSegmentX, y, segmentWidth, segmentHeight, 4, 4);

            if (i < filledSegments - 1) {
                Color segmentColor = getEnergyColor(i, totalSegments, 1.0);
                drawGradientSegment(g2, currentSegmentX, y, segmentWidth, segmentHeight, segmentColor, 1.0);
            } else if (i == filledSegments - 1 && partialFill > 0) {
                Color segmentColor = getEnergyColor(i, totalSegments, partialFill);
                drawGradientSegment(g2, currentSegmentX, y, segmentWidth, segmentHeight, segmentColor, partialFill);
            }

            if (i < filledSegments || (i == filledSegments - 1 && partialFill > 0)) {
                g2.setColor(new Color(255, 255, 255, 60));
                g2.fillRoundRect(currentSegmentX + 2, y + 2, segmentWidth - 4, 3, 2, 2);
            }
        }

        // ...existing code...

        // Setelah loop segmen bar
        g2.setFont(pressStart.deriveFont(Font.BOLD, 14F));
        String energyText = gp.player.currentEnergy + "/" + gp.player.MAX_POSSIBLE_ENERGY;
        FontMetrics fmText = g2.getFontMetrics();
        int textWidth = fmText.stringWidth(energyText);

        // Posisi angka di luar box, rata kiri dengan bar, sedikit jarak ke kanan
        int textX = boxX + boxWidth + 14;
        int textY = boxY + (boxHeight + fmText.getAscent()) / 2 - 2;

        // Shadow tipis untuk teks
        g2.setColor(new Color(0, 0, 0, 180));
        g2.drawString(energyText, textX + 2, textY + 2);

        // Teks utama
        g2.setColor(Color.WHITE);
        g2.drawString(energyText, textX, textY);

        g2.setStroke(new BasicStroke(1));
    }

    private void drawGradientSegment(Graphics2D g2, int x, int y, int width, int height, Color baseColor,
            double fillRatio) {
        int fillHeight = (int) (height * fillRatio);
        int fillY = y + height - fillHeight;

        Color lightColor = new Color(
                Math.min(255, baseColor.getRed() + 40),
                Math.min(255, baseColor.getGreen() + 40),
                Math.min(255, baseColor.getBlue() + 40),
                200);

        Color darkColor = new Color(
                Math.max(0, baseColor.getRed() - 20),
                Math.max(0, baseColor.getGreen() - 20),
                Math.max(0, baseColor.getBlue() - 20),
                220);

        GradientPaint gradient = new GradientPaint(
                x, fillY, lightColor,
                x, fillY + fillHeight, darkColor);

        g2.setPaint(gradient);
        g2.fillRoundRect(x + 1, fillY, width - 2, fillHeight, 3, 3);

        g2.setPaint(Color.WHITE);
    }

    private Color getEnergyColor(int segmentIndex, int totalSegments, double fillRatio) {
        double currentEnergyPercentage = (double) gp.player.currentEnergy / gp.player.MAX_POSSIBLE_ENERGY;

        Color baseColor;
        if (currentEnergyPercentage > 0.7) {

            baseColor = new Color(0, 220, 100);
        } else if (currentEnergyPercentage > 0.4) {

            baseColor = new Color(255, 180, 0);
        } else if (currentEnergyPercentage > 0.2) {

            baseColor = new Color(255, 120, 0);
        } else {

            int pulse = (int) (Math.sin(System.currentTimeMillis() / 200.0) * 30 + 30);

            int red = Math.min(255, Math.max(0, 220 + pulse));
            int green = Math.min(255, Math.max(0, 50));
            int blue = Math.min(255, Math.max(0, 50));

            baseColor = new Color(red, green, blue);
        }

        if (fillRatio < 1.0) {
            int alpha = (int) (180 * fillRatio + 75);

            alpha = Math.min(255, Math.max(0, alpha));
            baseColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha);
        }

        return baseColor;
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

        final int FRAME_PADDING = gp.tileSize / 2;
        final int SECTION_SPACING = gp.tileSize / 3;
        final int TEXT_PADDING = 12;
        final int SLOT_SIZE = gp.tileSize - 8;
        final int SLOT_GAP = 6;

        int frameX = FRAME_PADDING;
        int frameY = FRAME_PADDING;
        int frameWidth = gp.screenWidth - (FRAME_PADDING * 2);
        int frameHeight = gp.screenHeight - (FRAME_PADDING * 2);
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        Font titleFont = pressStart.deriveFont(Font.BOLD, 24F);
        Font headerFont = pressStart.deriveFont(Font.BOLD, 14F);
        Font bodyFont = pressStart.deriveFont(Font.PLAIN, 12F);
        Font smallFont = pressStart.deriveFont(Font.PLAIN, 10F);
        Font tinyFont = pressStart.deriveFont(Font.PLAIN, 8F);

        int currentY = frameY + TEXT_PADDING;
        g2.setFont(titleFont);
        g2.setColor(new Color(255, 215, 0));
        String title = "SHIPPING BIN";
        int titleX = getXForCenteredText(title);
        g2.drawString(title, titleX, currentY + g2.getFontMetrics().getAscent());
        currentY += g2.getFontMetrics().getHeight() + SECTION_SPACING;

        g2.setColor(new Color(40, 40, 50, 180));
        int statusBarHeight = 40;
        g2.fillRoundRect(frameX + TEXT_PADDING, currentY, frameWidth - (TEXT_PADDING * 2), statusBarHeight, 8, 8);
        g2.setColor(new Color(100, 100, 120, 100));
        g2.drawRoundRect(frameX + TEXT_PADDING, currentY, frameWidth - (TEXT_PADDING * 2), statusBarHeight, 8, 8);

        int totalItemsInBin = 0;
        int totalStacks = gp.player.itemsInShippingBinToday.size();
        int estimatedValue = 0;

        for (Entity binEntity : gp.player.itemsInShippingBinToday) {
            if (binEntity instanceof OBJ_Item) {
                OBJ_Item binItem = (OBJ_Item) binEntity;
                totalItemsInBin += binItem.quantity;
                estimatedValue += binItem.getSellPrice() * binItem.quantity;
            }
        }

        g2.setFont(bodyFont);
        g2.setColor(Color.WHITE);
        int statusTextY = currentY + (statusBarHeight / 2) + (g2.getFontMetrics().getAscent() / 2);
        String binStatus = "Items: " + totalItemsInBin + " (" + totalStacks + " types)";
        g2.drawString(binStatus, frameX + TEXT_PADDING * 2, statusTextY);

        if (estimatedValue > 0) {
            g2.setColor(new Color(255, 215, 0));
            String valueText = "Est. Value: " + estimatedValue + "G";
            int valueX = frameX + (frameWidth / 2) - (g2.getFontMetrics().stringWidth(valueText) / 2);
            g2.drawString(valueText, valueX, statusTextY);
        }

        g2.setColor(new Color(144, 238, 144));
        String goldText = "Gold: " + gp.player.gold + "G";
        int goldX = frameX + frameWidth - TEXT_PADDING * 2 - g2.getFontMetrics().stringWidth(goldText);
        g2.drawString(goldText, goldX, statusTextY);

        currentY += statusBarHeight + SECTION_SPACING;

        int binPreviewHeight = 0;
        if (!gp.player.itemsInShippingBinToday.isEmpty()) {
            g2.setFont(headerFont);
            g2.setColor(new Color(200, 200, 220));
            g2.drawString("Bin Contents:", frameX + TEXT_PADDING, currentY + g2.getFontMetrics().getAscent());
            currentY += g2.getFontMetrics().getHeight() + 8;

            g2.setColor(new Color(30, 30, 40, 160));
            binPreviewHeight = Math.min(120, totalStacks * 18 + TEXT_PADDING);
            g2.fillRoundRect(frameX + TEXT_PADDING, currentY, frameWidth - (TEXT_PADDING * 2), binPreviewHeight, 6, 6);
            g2.setColor(new Color(80, 80, 100, 120));
            g2.drawRoundRect(frameX + TEXT_PADDING, currentY, frameWidth - (TEXT_PADDING * 2), binPreviewHeight, 6, 6);

            g2.setFont(smallFont);
            g2.setColor(Color.LIGHT_GRAY);
            int itemListY = currentY + TEXT_PADDING + g2.getFontMetrics().getAscent();
            int maxItemsToShow = Math.min(6, totalStacks);

            for (int i = 0; i < maxItemsToShow; i++) {
                Entity binEntity = gp.player.itemsInShippingBinToday.get(i);
                if (binEntity instanceof OBJ_Item) {
                    OBJ_Item binItem = (OBJ_Item) binEntity;
                    String itemText = "• " + binItem.name + " x" + binItem.quantity +
                            " (" + (binItem.getSellPrice() * binItem.quantity) + "G)";
                    g2.drawString(itemText, frameX + TEXT_PADDING * 2, itemListY);
                    itemListY += 16;
                }
            }

            if (totalStacks > maxItemsToShow) {
                g2.setColor(new Color(150, 150, 150));
                g2.drawString("... and " + (totalStacks - maxItemsToShow) + " more items",
                        frameX + TEXT_PADDING * 2, itemListY);
            }

            currentY += binPreviewHeight + SECTION_SPACING;
        }

        g2.setFont(headerFont);
        g2.setColor(new Color(200, 200, 220));
        g2.drawString("Your Inventory:", frameX + TEXT_PADDING, currentY + g2.getFontMetrics().getAscent());
        currentY += g2.getFontMetrics().getHeight() + 8;

        int inventoryAreaHeight = frameY + frameHeight - currentY - 80;
        int slotsPerRow = Math.max(1, (frameWidth - (TEXT_PADDING * 2)) / (SLOT_SIZE + SLOT_GAP));
        int maxRows = Math.max(2, inventoryAreaHeight / (SLOT_SIZE + SLOT_GAP));

        g2.setColor(new Color(25, 25, 35, 180));
        g2.fillRoundRect(frameX + TEXT_PADDING, currentY, frameWidth - (TEXT_PADDING * 2), inventoryAreaHeight, 8, 8);
        g2.setColor(new Color(60, 60, 80, 120));
        g2.drawRoundRect(frameX + TEXT_PADDING, currentY, frameWidth - (TEXT_PADDING * 2), inventoryAreaHeight, 8, 8);

        int slotStartX = frameX + TEXT_PADDING + 10;
        int slotStartY = currentY + 10;
        int itemsDisplayed = 0;

        for (int i = 0; i < gp.player.inventory.size() && itemsDisplayed < slotsPerRow * maxRows; i++) {
            Entity itemEntity = gp.player.inventory.get(i);
            if (!(itemEntity instanceof OBJ_Item))
                continue;

            OBJ_Item item = (OBJ_Item) itemEntity;

            int col = itemsDisplayed % slotsPerRow;
            int row = itemsDisplayed / slotsPerRow;
            int slotX = slotStartX + (col * (SLOT_SIZE + SLOT_GAP));
            int slotY = slotStartY + (row * (SLOT_SIZE + SLOT_GAP));

            if (item.getSellPrice() > 0) {
                g2.setColor(new Color(0, 120, 0, 100));
            } else {
                g2.setColor(new Color(120, 0, 0, 100));
            }
            g2.fillRoundRect(slotX, slotY, SLOT_SIZE, SLOT_SIZE, 6, 6);

            if (i == gp.ui.commandNumber) {
                g2.setColor(new Color(255, 215, 0, 150));
                g2.fillRoundRect(slotX - 2, slotY - 2, SLOT_SIZE + 4, SLOT_SIZE + 4, 8, 8);
                g2.setColor(new Color(255, 215, 0));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(slotX - 2, slotY - 2, SLOT_SIZE + 4, SLOT_SIZE + 4, 8, 8);
                g2.setStroke(new BasicStroke(1));
            }

            g2.setColor(new Color(80, 80, 100));
            g2.drawRoundRect(slotX, slotY, SLOT_SIZE, SLOT_SIZE, 6, 6);

            BufferedImage itemImage = item.down1 != null ? item.down1 : item.image;
            if (itemImage != null) {
                int imageSize = SLOT_SIZE - 6;
                g2.drawImage(itemImage, slotX + 3, slotY + 3, imageSize, imageSize, null);
            }

            if (item.quantity > 1) {
                g2.setFont(tinyFont);
                g2.setColor(Color.BLACK);
                String qtyText = "x" + item.quantity;
                FontMetrics qfm = g2.getFontMetrics();
                int qtyX = slotX + SLOT_SIZE - qfm.stringWidth(qtyText) - 2;
                int qtyY = slotY + SLOT_SIZE - 2;

                g2.drawString(qtyText, qtyX + 1, qtyY + 1);

                g2.setColor(Color.WHITE);
                g2.drawString(qtyText, qtyX, qtyY);
            }

            itemsDisplayed++;
        }

        if (!gp.player.inventory.isEmpty() && gp.ui.commandNumber >= 0
                && gp.ui.commandNumber < gp.player.inventory.size()) {
            Entity selectedEntity = gp.player.inventory.get(gp.ui.commandNumber);
            if (selectedEntity instanceof OBJ_Item) {
                OBJ_Item selectedItem = (OBJ_Item) selectedEntity;

                int infoPanelY = currentY + inventoryAreaHeight - 60;
                g2.setColor(new Color(50, 50, 60, 200));
                g2.fillRoundRect(frameX + TEXT_PADDING, infoPanelY, frameWidth - (TEXT_PADDING * 2), 50, 6, 6);
                g2.setColor(new Color(100, 100, 120, 150));
                g2.drawRoundRect(frameX + TEXT_PADDING, infoPanelY, frameWidth - (TEXT_PADDING * 2), 50, 6, 6);

                g2.setFont(bodyFont);
                g2.setColor(Color.WHITE);
                int infoTextX = frameX + TEXT_PADDING + 10;
                int infoTextY = infoPanelY + 16;

                g2.drawString("Item: " + selectedItem.name, infoTextX, infoTextY);

                if (selectedItem.getSellPrice() > 0) {
                    String priceText = "Price: " + selectedItem.getSellPrice() + "G each";
                    if (selectedItem.quantity > 1) {
                        int totalValue = selectedItem.getSellPrice() * selectedItem.quantity;
                        priceText += " (Total: " + totalValue + "G)";
                    }
                    g2.setColor(new Color(144, 238, 144));
                    g2.drawString(priceText, infoTextX, infoTextY + 16);
                } else {
                    g2.setColor(new Color(255, 100, 100));
                    g2.drawString("This item cannot be sold", infoTextX, infoTextY + 16);
                }
            }
        }

        if (gp.player.inventory.isEmpty()) {
            g2.setFont(bodyFont);
            g2.setColor(new Color(150, 150, 150));
            String emptyMsg = "Your inventory is empty.";
            int emptyMsgX = frameX + (frameWidth / 2) - (g2.getFontMetrics().stringWidth(emptyMsg) / 2);
            int emptyMsgY = currentY + (inventoryAreaHeight / 2);
            g2.drawString(emptyMsg, emptyMsgX, emptyMsgY);
        }

        currentY = frameY + frameHeight - 50;

        g2.setColor(new Color(20, 30, 40, 220));
        g2.fillRoundRect(frameX + TEXT_PADDING, currentY, frameWidth - (TEXT_PADDING * 2), 40, 8, 8);
        g2.setColor(new Color(80, 100, 120, 150));
        g2.drawRoundRect(frameX + TEXT_PADDING, currentY, frameWidth - (TEXT_PADDING * 2), 40, 8, 8);

        g2.setFont(smallFont);
        g2.setColor(Color.WHITE);
        String instructions = "[↑↓] Navigate  |  [Enter] Move 1 Item to Bin  |  [Esc] Finish Transaction";
        int instrX = getXForCenteredTextInFrame(instructions, frameX, frameWidth);
        g2.drawString(instructions, instrX, currentY + 16);

        g2.setFont(tinyFont);
        g2.setColor(new Color(180, 180, 180));
        String helpText = "Items with same type will stack automatically in the bin";
        int helpX = getXForCenteredTextInFrame(helpText, frameX, frameWidth);
        g2.drawString(helpText, helpX, currentY + 30);
    }

    private void drawTimedMessage(Graphics2D g2) {
        if (messageOn && this.message != null && !this.message.isEmpty()) {

            float timedMessageFontSize = 12F;
            Font messageFont = (mineCraftia != null) ? mineCraftia.deriveFont(Font.PLAIN, timedMessageFontSize)
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

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        String[] labels = { "DAY", "TIME", "SEASON", "WEATHER" };
        String[] values = {
                String.valueOf(gameClock.getTime().getDay()),
                gameClock.getFormattedTime(),
                gameClock.getCurrentSeason().name(),
                gameClock.getWeather().getWeatherName()
        };

        Season currentSeason = gameClock.getCurrentSeason();
        Weather currentWeather = gameClock.getWeather();

        Color primaryColor = getSeasonColor(currentSeason);
        Color accentColor = getWeatherColor(currentWeather);

        Font titleFont = pressStart.deriveFont(Font.BOLD, 11F);
        Font valueFont = pressStart.deriveFont(Font.PLAIN, 10F);

        g2.setFont(titleFont);
        FontMetrics titleFm = g2.getFontMetrics();
        g2.setFont(valueFont);
        FontMetrics valueFm = g2.getFontMetrics();

        int padding = 15;
        int innerPadding = 12;
        int lineSpacing = 22;
        int gapBetweenLabelAndValue = 12;

        int maxLabelWidth = 0;
        int maxValueWidth = 0;
        for (int i = 0; i < labels.length; i++) {
            maxLabelWidth = Math.max(maxLabelWidth, titleFm.stringWidth(labels[i]));
            maxValueWidth = Math.max(maxValueWidth, valueFm.stringWidth(values[i]));
        }

        int contentWidth = maxLabelWidth + gapBetweenLabelAndValue + maxValueWidth;
        int contentHeight = lineSpacing * labels.length - 4;

        int totalWidth = contentWidth + (innerPadding * 2);
        int totalHeight = contentHeight + (innerPadding * 2);

        int x = gp.screenWidth - totalWidth - padding;
        int y = padding - 6;

        GradientPaint backgroundGradient = new GradientPaint(
                x, y, new Color(20, 25, 35, 220),
                x, y + totalHeight, new Color(35, 40, 50, 240));
        g2.setPaint(backgroundGradient);
        g2.fillRoundRect(x, y, totalWidth, totalHeight, 16, 16);

        g2.setStroke(new BasicStroke(2.5f));
        g2.setColor(new Color(primaryColor.getRed(), primaryColor.getGreen(), primaryColor.getBlue(), 150));
        g2.drawRoundRect(x + 1, y + 1, totalWidth - 2, totalHeight - 2, 15, 15);

        g2.setStroke(new BasicStroke(1f));
        g2.setColor(new Color(255, 255, 255, 30));
        g2.drawRoundRect(x + 3, y + 3, totalWidth - 6, totalHeight - 6, 12, 12);

        int contentX = x + innerPadding;
        int contentY = y + innerPadding;

        for (int i = 0; i < labels.length; i++) {
            int lineY = contentY + (i * lineSpacing) + titleFm.getAscent();

            Color labelColor = (i == 0) ? primaryColor
                    : (i == 1) ? new Color(255, 215, 0)
                            : (i == 2) ? primaryColor
                                    : accentColor;

            int labelX = contentX;
            g2.setFont(titleFont);

            g2.setColor(new Color(0, 0, 0, 120));
            g2.drawString(labels[i], labelX + 1, lineY + 1);

            g2.setColor(labelColor);
            g2.drawString(labels[i], labelX, lineY);

            int valueX = labelX + maxLabelWidth + gapBetweenLabelAndValue;
            g2.setFont(valueFont);

            g2.setColor(new Color(0, 0, 0, 150));
            g2.drawString(values[i], valueX + 1, lineY + 1);

            Color valueColor = getValueColor(i, currentSeason, currentWeather);
            g2.setColor(valueColor);
            g2.drawString(values[i], valueX, lineY);

            if (i == 0) {
                int highlightWidth = 3;
                int highlightHeight = lineSpacing - 6;
                int highlightX = contentX - 8;
                int highlightY = lineY - titleFm.getAscent() + 3;

                GradientPaint highlightGradient = new GradientPaint(
                        highlightX, highlightY, primaryColor,
                        highlightX, highlightY + highlightHeight,
                        new Color(primaryColor.getRed(), primaryColor.getGreen(), primaryColor.getBlue(), 100));
                g2.setPaint(highlightGradient);
                g2.fillRoundRect(highlightX, highlightY, highlightWidth, highlightHeight, 2, 2);
            }
        }

        if (isNightTime()) {
            g2.setStroke(new BasicStroke(3f));
            g2.setColor(new Color(70, 130, 180, 80));
            g2.drawRoundRect(x - 2, y - 2, totalWidth + 4, totalHeight + 4, 18, 18);
        }

        g2.setPaint(Color.WHITE);
        g2.setStroke(new BasicStroke(1));
    }

    private Color getSeasonColor(Season season) {
        switch (season) {
            case SPRING:
                return new Color(144, 238, 144);
            case SUMMER:
                return new Color(255, 215, 0);
            case FALL:
                return new Color(255, 140, 0);
            case WINTER:
                return new Color(176, 224, 230);
            default:
                return new Color(255, 255, 255);
        }
    }

    private Color getWeatherColor(Weather weather) {
        String weatherName = weather.getWeatherName().toLowerCase();
        if (weatherName.contains("rain") || weatherName.contains("storm")) {
            return new Color(100, 149, 237);
        } else if (weatherName.contains("snow")) {
            return new Color(240, 248, 255);
        } else if (weatherName.contains("sunny") || weatherName.contains("clear")) {
            return new Color(255, 215, 0);
        } else if (weatherName.contains("cloud")) {
            return new Color(169, 169, 169);
        }
        return new Color(255, 255, 255);
    }

    private Color getValueColor(int index, Season season, Weather weather) {
        switch (index) {
            case 0:
                return new Color(255, 255, 255);
            case 1:
                return isNightTime() ? new Color(173, 216, 230) : new Color(255, 223, 0);
            case 2:
                return getSeasonColor(season);
            case 3:
                return getWeatherColor(weather);
            default:
                return new Color(255, 255, 255);
        }
    }

    private boolean isNightTime() {
        if (gameClock == null || gameClock.getTime() == null)
            return false;
        int hour = gameClock.getTime().getHour();
        return hour >= 20 || hour < 6;
    }

    public void drawLocationHUD(Graphics2D g2) {
        float scale = 0.65f;

        Font baseFont = (pressStart != null) ? pressStart.deriveFont(Font.BOLD, 18F * scale)
                : g2.getFont().deriveFont(Font.BOLD, 18F * scale);
        g2.setFont(baseFont);

        String currentLocation = "Unknown";
        if (gp.player != null) {
            String playerLocation = gp.player.getLocation();
            if (playerLocation != null && !playerLocation.isEmpty() && !playerLocation.equals("Unknown")) {
                // Jika di farm, pakai nama farm hasil input
                if (playerLocation.equalsIgnoreCase("Farm")) {
                    String farmName = gp.player.getFarmName();
                    if (farmName != null && !farmName.trim().isEmpty()) {
                        currentLocation = farmName.toUpperCase();
                    } else {
                        currentLocation = "FARM";
                    }
                } else {
                    currentLocation = playerLocation;
                }
            } else if (gp.currentMapIndex >= 0 && gp.currentMapIndex < gp.mapInfos.size()) {
                String mapName = gp.mapInfos.get(gp.currentMapIndex).getMapName();
                // Jika di farm, pakai nama farm hasil input
                if (mapName != null && mapName.equalsIgnoreCase("Farm")) {
                    String farmName = gp.player.getFarmName();
                    if (farmName != null && !farmName.trim().isEmpty()) {
                        currentLocation = farmName.toUpperCase();
                    } else {
                        currentLocation = "FARM";
                    }
                } else if (mapName != null) {
                    currentLocation = mapName;
                }
            }
        }

        String locationText = currentLocation.replace("'", "’").toUpperCase();


        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(locationText);
        int textHeight = fm.getHeight();

        int iconSize = Math.round((textHeight + 2));
        int padding = Math.round(16 * scale);
        int spacing = Math.round(10 * scale);
        int boxHeight = Math.max(iconSize, textHeight) + padding * 2;
        int boxWidth = iconSize + spacing + textWidth + padding * 2;

        int x = gp.screenWidth - boxWidth - 18;
        int y = gp.screenHeight - boxHeight - 18;

        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRoundRect(x + 3, y + 4, boxWidth, boxHeight, Math.round(18 * scale), Math.round(18 * scale));

        g2.setColor(new Color(30, 30, 40, 200));
        g2.fillRoundRect(x, y, boxWidth, boxHeight, Math.round(18 * scale), Math.round(18 * scale));

        g2.setColor(new Color(255, 255, 255, 60));
        g2.setStroke(new BasicStroke(2f * scale));
        g2.drawRoundRect(x, y, boxWidth, boxHeight, Math.round(18 * scale), Math.round(18 * scale));

        int iconX = x + (boxWidth - (iconSize + spacing + textWidth)) / 2;
        int iconY = y + (boxHeight - iconSize) / 2;
        drawLocationPinIcon(g2, iconX, iconY, iconSize, iconSize);

        int textX = iconX + iconSize + spacing;
        int textY = y + (boxHeight + textHeight) / 2 - fm.getDescent();

        g2.setColor(new Color(0, 0, 0, 180));
        g2.drawString(locationText, textX + 2, textY + 2);
        g2.setColor(Color.WHITE);
        g2.drawString(locationText, textX, textY);
    }

    private void drawLocationPinIcon(Graphics2D g2, int x, int y, int w, int h) {

        g2.setColor(new Color(255, 215, 0, 220));
        g2.fillOval(x + w / 6, y, w * 2 / 3, h * 2 / 3);

        int[] px = { x + w / 2, x + w / 6, x + w * 5 / 6 };
        int[] py = { y + h, y + h * 2 / 3, y + h * 2 / 3 };
        g2.setColor(new Color(255, 215, 0, 200));
        g2.fillPolygon(px, py, 3);

        g2.setColor(new Color(180, 140, 0, 200));
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(x + w / 6, y, w * 2 / 3, h * 2 / 3);
        g2.drawLine(x + w / 2, y + h * 2 / 3, x + w / 2, y + h);
    }

    public void drawBuyingScreen() {
        if (!(gp.currentInteractingNPC instanceof NPC_EMILY))
            return;
        NPC_EMILY emily = (NPC_EMILY) gp.currentInteractingNPC;

        int frameX = gp.tileSize;
        int frameY = gp.tileSize;
        int frameWidth = gp.screenWidth - (gp.tileSize * 2);
        int frameHeight = gp.screenHeight - (gp.tileSize * 2);

        g2.setColor(new Color(15, 25, 35, 240));
        g2.fillRoundRect(frameX, frameY, frameWidth, frameHeight, 20, 20);

        g2.setStroke(new BasicStroke(3f));
        g2.setColor(new Color(200, 180, 120, 180));
        g2.drawRoundRect(frameX + 2, frameY + 2, frameWidth - 4, frameHeight - 4, 18, 18);

        g2.setStroke(new BasicStroke(1f));
        g2.setColor(new Color(255, 255, 255, 50));
        g2.drawRoundRect(frameX + 6, frameY + 6, frameWidth - 12, frameHeight - 12, 15, 15);

        Font titleFont = pressStart != null ? pressStart.deriveFont(Font.BOLD, 24F) : new Font("Arial", Font.BOLD, 24);
        Font headerFont = pressStart != null ? pressStart.deriveFont(Font.BOLD, 14F) : new Font("Arial", Font.BOLD, 14);
        Font bodyFont = pressStart != null ? pressStart.deriveFont(Font.PLAIN, 12F) : new Font("Arial", Font.PLAIN, 12);

        int headerY = frameY + gp.tileSize - 10;

        g2.setFont(titleFont);
        g2.setColor(new Color(255, 220, 120));
        String title = "Emily's General Store";
        int titleX = getXForCenteredTextInFrame(title, frameX, frameWidth);
        g2.drawString(title, titleX, headerY);

        g2.setFont(headerFont);
        g2.setColor(new Color(255, 215, 0));
        String goldText = "Gold: " + gp.player.gold + "G";
        int goldX = frameX + frameWidth - g2.getFontMetrics().stringWidth(goldText) - 30;
        g2.drawString(goldText, goldX, headerY + 30);

        if (emily.shopInventory.isEmpty()) {
            g2.setFont(bodyFont.deriveFont(Font.ITALIC, 16F));
            g2.setColor(Color.LIGHT_GRAY);
            String noItemMsg = "Store is closed today!";
            int msgX = getXForCenteredTextInFrame(noItemMsg, frameX, frameWidth);
            g2.drawString(noItemMsg, msgX, frameY + frameHeight / 2);

            g2.setFont(bodyFont);
            String exitMsg = "Press ESC to leave";
            int exitX = getXForCenteredTextInFrame(exitMsg, frameX, frameWidth);
            g2.drawString(exitMsg, exitX, frameY + frameHeight / 2 + 30);
            return;
        }

        int contentStartY = headerY + 60;
        int leftPanelX = frameX + 20;
        int leftPanelWidth = (frameWidth - 60) / 2;
        int rightPanelX = leftPanelX + leftPanelWidth + 20;
        int rightPanelWidth = leftPanelWidth;

        g2.setColor(new Color(25, 35, 45, 200));
        g2.fillRoundRect(leftPanelX - 10, contentStartY - 20, leftPanelWidth + 20, frameHeight - 150, 15, 15);
        g2.setColor(new Color(100, 120, 140, 100));
        g2.drawRoundRect(leftPanelX - 10, contentStartY - 20, leftPanelWidth + 20, frameHeight - 150, 15, 15);

        g2.setFont(headerFont);
        g2.setColor(new Color(180, 200, 220));
        g2.drawString("Items for Sale", leftPanelX - 5, contentStartY - 5);

        int itemY = contentStartY + 20;
        int itemSpacing = 28;
        int maxVisibleItems = (frameHeight - 200) / itemSpacing;
        int scrollOffset = Math.max(0, storeCommandNum - maxVisibleItems + 1);

        for (int i = scrollOffset; i < Math.min(scrollOffset + maxVisibleItems, emily.shopInventory.size()); i++) {
            OBJ_Item item = emily.shopInventory.get(i);
            boolean isSelected = (i == storeCommandNum);

            if (isSelected) {
                g2.setColor(new Color(255, 215, 0, 120));
                g2.fillRoundRect(leftPanelX - 8, itemY - 14, leftPanelWidth + 16, 24, 8, 8);
                g2.setColor(new Color(255, 215, 0, 200));
                g2.drawRoundRect(leftPanelX - 8, itemY - 14, leftPanelWidth + 16, 24, 8, 8);
            }

            g2.setFont(bodyFont);
            g2.setColor(isSelected ? Color.WHITE : new Color(200, 200, 200));

            String itemName = item.name;
            if (itemName.length() > 15) {
                itemName = itemName.substring(0, 12) + "...";
            }

            String itemText = (isSelected ? "> " : "  ") + itemName;
            g2.drawString(itemText, leftPanelX, itemY);

            String priceText = item.getBuyPrice() + "G";
            FontMetrics fm = g2.getFontMetrics();
            int priceX = leftPanelX + leftPanelWidth - fm.stringWidth(priceText) - 5;
            g2.setColor(isSelected ? new Color(255, 215, 0) : new Color(150, 150, 150));
            g2.drawString(priceText, priceX, itemY);

            itemY += itemSpacing;
        }

        if (storeCommandNum >= 0 && storeCommandNum < emily.shopInventory.size()) {
            OBJ_Item selectedItem = emily.shopInventory.get(storeCommandNum);

            g2.setColor(new Color(35, 45, 55, 220));
            g2.fillRoundRect(rightPanelX - 10, contentStartY - 20, rightPanelWidth + 20, 180, 15, 15);
            g2.setColor(new Color(120, 140, 160, 120));
            g2.drawRoundRect(rightPanelX - 10, contentStartY - 20, rightPanelWidth + 20, 180, 15, 15);

            g2.setFont(headerFont);
            g2.setColor(new Color(180, 200, 220));
            g2.drawString("Item Details", rightPanelX - 5, contentStartY - 5);

            int imageSize = gp.tileSize;
            int imageX = rightPanelX + 5;
            int imageY = contentStartY + 10;

            g2.setColor(new Color(60, 70, 80, 150));
            g2.fillRoundRect(imageX - 3, imageY - 3, imageSize + 6, imageSize + 6, 8, 8);

            if (selectedItem.down1 != null) {
                g2.drawImage(selectedItem.down1, imageX, imageY, imageSize, imageSize, null);
            } else {
                g2.setColor(Color.GRAY);
                g2.fillRoundRect(imageX, imageY, imageSize, imageSize, 6, 6);
                g2.setColor(Color.WHITE);
                g2.setFont(bodyFont.deriveFont(Font.PLAIN, 9F));
                String noImgText = "No Image";
                FontMetrics noImgFm = g2.getFontMetrics();
                int noImgX = imageX + (imageSize - noImgFm.stringWidth(noImgText)) / 2;
                int noImgY = imageY + (imageSize + noImgFm.getHeight()) / 2;
                g2.drawString(noImgText, noImgX, noImgY);
            }

            int detailX = imageX + imageSize + 15;
            int detailY = contentStartY + 25;

            g2.setFont(headerFont.deriveFont(Font.BOLD, 14F));
            g2.setColor(Color.WHITE);
            String fullName = selectedItem.name;
            if (fullName.length() > 12) {
                fullName = fullName.substring(0, 9) + "...";
            }
            g2.drawString(fullName, detailX, detailY);

            g2.setFont(bodyFont.deriveFont(Font.BOLD, 12F));
            g2.setColor(new Color(255, 215, 0));
            g2.drawString("Price: " + selectedItem.getBuyPrice() + "G", detailX, detailY + 20);

            if (selectedItem.isEdible()) {
                g2.setFont(bodyFont.deriveFont(Font.PLAIN, 10F));
                g2.setColor(new Color(144, 238, 144));
                g2.drawString("+ Restores Energy", detailX, detailY + 35);
            }

            g2.setFont(bodyFont.deriveFont(Font.ITALIC, 10F));
            if (gp.player.gold >= selectedItem.getBuyPrice()) {
                g2.setColor(new Color(144, 238, 144));
                g2.drawString("You can afford this", detailX, detailY + 50);
            } else {
                g2.setColor(new Color(255, 100, 100));
                g2.drawString("Not enough gold", detailX, detailY + 50);
            }
        }

        int footerY = frameY + frameHeight - 30;
        g2.setColor(new Color(20, 30, 40, 200));
        g2.fillRoundRect(frameX + 15, footerY - 10, frameWidth - 30, 25, 8, 8);

        g2.setFont(bodyFont.deriveFont(Font.PLAIN, 10F));
        g2.setColor(Color.WHITE);
        String instructions = "UP/DOWN: Navigate  |  ENTER: Buy  |  ESC: Exit";
        int instrX = getXForCenteredTextInFrame(instructions, frameX, frameWidth);
        g2.drawString(instructions, instrX, footerY + 5);

        g2.setStroke(new BasicStroke(1f));
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

        drawSharedBackground(g2, gp.endGameState);
        g2.setColor(new Color(0, 0, 0, 0));
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

    public void drawGenderSelectionScreen() {
        drawSharedBackground(g2, gp.genderSelectionState);

        String[] genderLabels = { "Male", "Female" };
        String[] spriteFiles = { "male_standing.png", "female_standing.png" };
        String[] genderFolders = { "male", "female" };

        int selected = genderSelectionIndex; // 0 = Male, 1 = Female

        // Load gambar sesuai pilihan
        BufferedImage preview = null;
        try {
            preview = ImageIO.read(getClass().getResourceAsStream(
                "/player/" + genderFolders[selected] + "/" + spriteFiles[selected]
            ));
        } catch (Exception e) {
            // Gambar tidak ditemukan, biarkan preview null
        }

        int imgW = gp.tileSize * 3;
        int imgH = gp.tileSize * 3;
        int imgX = gp.screenWidth / 2 - imgW / 2;
        int imgY = gp.screenHeight / 2 - imgH / 2 - 20;

        if (preview != null) {
            g2.drawImage(preview, imgX, imgY, imgW, imgH, null);
        }

        // --- Tulisan gender di bawah gambar ---
        g2.setFont(pressStart.deriveFont(Font.BOLD, 24F));
        g2.setColor(themecolor); // warna sama dengan judul
        String genderText = genderLabels[selected];
        int textWidth = g2.getFontMetrics().stringWidth(genderText);
        int textX = gp.screenWidth / 2 - textWidth / 2;
        int textY = imgY + imgH + 60; // bawah gambar, beri jarak 40px
        g2.drawString(genderText, textX, textY);
    }
}