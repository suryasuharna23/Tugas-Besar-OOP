package spakborhills.object;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.entity.Player;
import spakborhills.enums.EntityType;
import spakborhills.enums.ItemType;
import spakborhills.enums.Season;
import spakborhills.interfaces.Harvestable;

public class OBJ_PlantedCrop extends Entity implements Harvestable {
    private String cropType;
    private int daysToGrow;
    private int currentGrowthDays;
    private int harvestAmount;
    private int sellPrice;
    private int energy;
    private boolean isWatered;
    private BufferedImage[] growthStages;
    private boolean grewToday;

    public OBJ_PlantedCrop(GamePanel gp, String cropType, int worldX, int worldY) {
        super(gp);
        this.cropType = cropType;
        this.worldX = worldX;
        this.worldY = worldY;
        this.currentGrowthDays = 0;
        this.isWatered = false;
        this.grewToday = false;
        this.type = EntityType.INTERACTIVE_OBJECT;
        this.name = cropType + " Plant";
        this.collision = false;

        setupCropData();
        loadGrowthStages();

        solidArea = new Rectangle(0, 0, gp.tileSize, gp.tileSize);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    private void setupCropData() {
        switch (cropType) {
            case "Parsnip":
                daysToGrow = 1;
                harvestAmount = 1;
                sellPrice = 35;
                energy = 3;
                break;
            case "Cauliflower":
                daysToGrow = 5;
                harvestAmount = 1;
                sellPrice = 150;
                energy = 3;
                break;
            case "Potato":
                daysToGrow = 3;
                harvestAmount = 1;
                sellPrice = 80;
                energy = 3;
                break;
            case "Wheat":
                daysToGrow = 1;
                harvestAmount = 3;
                sellPrice = 30;
                energy = 3;
                break;
            case "Blueberry":
                daysToGrow = 7;
                harvestAmount = 3;
                sellPrice = 40;
                energy = 3;
                break;
            case "Tomato":
                daysToGrow = 3;
                harvestAmount = 1;
                sellPrice = 60;
                energy = 3;
                break;
            case "Hot Pepper":
                daysToGrow = 1;
                harvestAmount = 1;
                sellPrice = 40;
                energy = 3;
                break;
            case "Melon":
                daysToGrow = 4;
                harvestAmount = 1;
                sellPrice = 250;
                energy = 3;
                break;
            case "Cranberry":
                daysToGrow = 2;
                harvestAmount = 10;
                sellPrice = 25;
                energy = 3;
                break;
            case "Pumpkin":
                daysToGrow = 7;
                harvestAmount = 1;
                sellPrice = 250;
                energy = 3;
                break;
            case "Grape":
                daysToGrow = 3;
                harvestAmount = 20;
                sellPrice = 10;
                energy = 3;
                break;
            default:
                daysToGrow = 5;
                harvestAmount = 1;
                sellPrice = 10;
                energy = 3;
                break;
        }
    }

    private void loadGrowthStages() {

        growthStages = new BufferedImage[4];
        growthStages[0] = setup("/objects/planted");
        growthStages[1] = setup("/objects/plant_small");
        growthStages[2] = setup("/objects/plant_medium");
        growthStages[3] = setup("/objects/" + cropType.toLowerCase());

        for (int i = 0; i < growthStages.length; i++) {
            if (growthStages[i] == null) {
                System.err.println("[OBJ_PlantedCrop] Gagal memuat gambar tahap " + i + " untuk " + cropType
                        + ". Menggunakan placeholder.");
                if (i == 3) {
                    growthStages[i] = setup("/objects/plant_medium");
                    if (growthStages[i] == null)
                        growthStages[i] = setup("/objects/planted");
                } else {
                    growthStages[i] = setup("/objects/planted");
                }
            }
        }
        updateGrowthImage();
    }

    private void updateGrowthImage() {

        int stage = getCurrentGrowthStage();
        if (stage >= 0 && stage < growthStages.length && growthStages[stage] != null) {
            down1 = growthStages[stage];
        } else {
            System.err.println("[OBJ_PlantedCrop] Invalid growth stage " + stage + " for " + cropType);
            down1 = growthStages[0];
        }
        if (down1 != null) {
            up1 = down1;
            left1 = down1;
            right1 = down1;
        }
    }

    private int getCurrentGrowthStage() {
        if (currentGrowthDays == 0)
            return 0;
        if (isReadyToHarvest())
            return 3;

        double progress = (double) currentGrowthDays / daysToGrow;
        if (progress < 0.33)
            return 0;
        if (progress < 0.66)
            return 1;
        return 2;
    }

    public void processGrowthForCompletedDay() {
        if (grewToday) {
            System.out.println("DEBUG: " + cropType + " already grew today, skipping");
            return;
        }

        if (currentGrowthDays >= daysToGrow) {
            System.out.println("DEBUG: " + cropType + " is already fully grown");
            return;
        }

        if (gp.gameClock != null) {
            Season currentSeason = gp.gameClock.getCurrentSeason();
            if (currentSeason == Season.WINTER) {
                System.out.println("Tidak bisa tumbuh karena WINTER");
                return;
            }
        }

        if (this.isWatered) {
            this.currentGrowthDays++;
            System.out.println("DEBUG: " + cropType + " GREW to day " + currentGrowthDays + "/" + daysToGrow);
            updateGrowthImage();
            grewToday = true;
            updateTileState();
        } else {
            System.out.println("DEBUG: " + cropType + " SKIPPED growth - not watered");
        }
    }

    public void resetForNewDay(boolean isRainingForNewDay) {

        grewToday = false;

        if (isRainingForNewDay) {

            isWatered = true;
            System.out.println("DEBUG: " + cropType + " remains watered (raining today)");
        } else {

            isWatered = false;
            System.out.println("DEBUG: " + cropType + " watered status RESET (no rain today)");
        }

        updateTileState();
    }

    @Override
    public boolean isReadyToHarvest() {
        return currentGrowthDays >= daysToGrow;
    }

    @Override
    public OBJ_Item harvest(Player player) {
        if (!isReadyToHarvest()) {
            return null;
        }
        OBJ_Crop harvestedCrop = new OBJ_Crop(gp, ItemType.CROP, cropType, true,
                0,
                sellPrice, harvestAmount, energy);
        harvestedCrop.quantity = this.harvestAmount;

        if (player.firstHarvestByName != null && !player.firstHarvestByName.containsKey(cropType)) {
            player.firstHarvestByName.put(cropType, true);
        }
        if ("Hot Pepper".equals(cropType)) {
            player.hasObtainedHotPepper = true;
        }
        return harvestedCrop;
    }

    @Override
    public int getDaysUntilHarvest() {
        return Math.max(0, daysToGrow - currentGrowthDays);
    }

    /**
     * Dipanggil ketika pemain menyiram tanaman secara manual.
     */
    public void water() {
        isWatered = true;
        System.out.println("DEBUG: " + cropType + " manually watered TODAY");

        updateTileState();
    }

    private void updateTileState() {
        int tileX = worldX / gp.tileSize;
        int tileY = worldY / gp.tileSize;

        if (tileX >= 0 && tileX < gp.maxWorldCol && tileY >= 0 && tileY < gp.maxWorldRow) {
            if (isWatered) {
                gp.tileManager.mapTileNum[tileX][tileY] = 80;
            } else {
                gp.tileManager.mapTileNum[tileX][tileY] = 55;
            }
        }
    }

    public double getGrowthProgress() {

        if (daysToGrow == 0)
            return 1.0;
        return Math.min(1.0, (double) currentGrowthDays / daysToGrow);
    }

    public String getCropType() {
        return cropType;
    }

    public int getCurrentGrowthDays() {
        return currentGrowthDays;
    }

    public int getDaysToGrow() {
        return daysToGrow;
    }

    public boolean isWatered() {
        return isWatered;
    }

    public boolean getGrewToday() {
        return grewToday;
    }

    public void setCurrentGrowthDays(int days) {
        this.currentGrowthDays = Math.max(0, Math.min(days, daysToGrow));
        updateGrowthImage();
        System.out.println("DEBUG: " + cropType + " growth days set to " + this.currentGrowthDays);
    }

    public void setWatered(boolean watered) {
        this.isWatered = watered;
        updateTileState();
        System.out.println("DEBUG: " + cropType + " watered status set to " + watered);
    }

    public void setGrewToday(boolean grewToday) {
        this.grewToday = grewToday;
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Graphics2D g2) {

        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {

            if (down1 != null) {
                g2.drawImage(down1, screenX, screenY, gp.tileSize, gp.tileSize, null);
            } else {
                g2.setColor(Color.PINK);
                g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
            }

            if (!isReadyToHarvest()) {
                drawGrowthProgressBar(g2, screenX, screenY);
            } else {
                g2.setColor(new Color(255, 223, 0, 200));
                g2.fillOval(screenX + gp.tileSize - 16, screenY + 4, 12, 12);
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.BOLD, 10));
                g2.drawString("!", screenX + gp.tileSize - 13, screenY + 13);
            }
        }
    }

    private void drawGrowthProgressBar(Graphics2D g2, int screenX, int screenY) {

        int barWidth = gp.tileSize - 8;
        int barHeight = 5;
        int barX = screenX + 4;
        int barY = screenY + gp.tileSize - barHeight - 2;

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(barX - 1, barY - 1, barWidth + 2, barHeight + 2, 3, 3);

        double progress = getGrowthProgress();
        int progressWidth = (int) (barWidth * progress);

        if (progress < 0.33)
            g2.setColor(new Color(255, 70, 70));
        else if (progress < 0.66)
            g2.setColor(new Color(255, 180, 50));
        else
            g2.setColor(new Color(100, 200, 100));

        g2.fillRoundRect(barX, barY, progressWidth, barHeight, 2, 2);
    }

    @Override
    public void interact() {
        Player player = gp.player;

        if (isReadyToHarvest()) {
            OBJ_Item harvestedItem = harvest(player);
            if (harvestedItem != null) {
                boolean added = player.addItemToInventory(harvestedItem);
                if (added) {
                    gp.ui.showMessage("Berhasil panen " + harvestedItem.quantity + "x " + cropType + "!");
                    System.out.println("[Crop: " + cropType + "] Dipanen. Jumlah: " + harvestedItem.quantity);

                    int tileX = worldX / gp.tileSize;
                    int tileY = worldY / gp.tileSize;
                    gp.entities.remove(this);
                    gp.tileManager.mapTileNum[tileX][tileY] = 76;
                    System.out.println("[Crop: " + cropType + "] Tanaman sekali panen, tile (" + tileX + "," + tileY
                            + ") direset ke SOIL (76).");

                } else {
                    gp.ui.showMessage("Inventory penuh. Tidak bisa panen " + cropType + ".");
                }
            }
        } else {
            int daysLeft = getDaysUntilHarvest();
            String wateredStatusMsg;
 
            String winterStatusMsg = "";
            if (gp.gameClock != null) {
                Season currentSeason = gp.gameClock.getCurrentSeason();
                if (currentSeason == Season.WINTER) {
                    winterStatusMsg = "(Tidak bisa tumbuh karena sekarang Winter)";
                }
            }
            
            if (isWatered) {
                wateredStatusMsg = " (Sudah disiram hari ini)";
            } else {
                boolean isRainingToday = false;
                if (gp.gameClock != null && gp.gameClock.getWeather() != null) {
                    isRainingToday = (gp.gameClock.getCurrentWeather() == spakborhills.enums.Weather.RAINY);
                }
                if (isRainingToday) {
                    wateredStatusMsg = " (Will be watered by rain today)";
                } else {
                    wateredStatusMsg = " (NEEDS WATERING TODAY)";
                }
            }
            gp.ui.showMessage(cropType + " - " + String.format("%.0f%%", getGrowthProgress() * 100) +
                    " tumbuh. " + daysLeft + " hari lagi." + wateredStatusMsg);
        }
    }
}