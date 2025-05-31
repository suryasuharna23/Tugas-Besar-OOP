package spakborhills.action;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.entity.Player;
import spakborhills.object.OBJ_PlantedCrop;

public class WateringCommand implements Command {
    private final Player player;

    public WateringCommand(Player player) {
        this.player = player;
    }

    @Override
    public void execute(GamePanel gp) {
        int tileSize = gp.tileSize;

        int playerCenterX = player.worldX + player.solidArea.x + player.solidArea.width / 2;
        int playerCenterY = player.worldY + player.solidArea.y + player.solidArea.height / 2;

        int targetCol = playerCenterX / tileSize;
        int targetRow = playerCenterY / tileSize;

        switch (player.direction) {
            case "up" -> targetRow = (player.worldY + player.solidArea.y - 1) / tileSize;
            case "down" -> targetRow = (player.worldY + player.solidArea.y + player.solidArea.height) / tileSize;
            case "left" -> targetCol = (player.worldX + player.solidArea.x - 1) / tileSize;
            case "right" -> targetCol = (player.worldX + player.solidArea.x + player.solidArea.width) / tileSize;
        }

        if (!player.isHoldingTool("Watering Can equipment")) {
            gp.ui.showMessage("Kamu harus memegang Watering Can.");
            return;
        }

        if (targetCol < 0 || targetCol >= gp.maxWorldCol || targetRow < 0 || targetRow >= gp.maxWorldRow) {
            gp.ui.showMessage("Tidak bisa watering di luar batas map");
            return;
        }

        int tileIndexAtTarget = gp.tileManager.mapTileNum[targetCol][targetRow];
        int plantedSoilIndex = 55;
        int wateredPlantedIndex = 80;

        System.out.println(
                "DEBUG WateringCommand: Attempting to water tile (" + targetCol + "," + targetRow + ") = "
                        + tileIndexAtTarget);

        OBJ_PlantedCrop cropToWater = null;
        int targetWorldX = targetCol * tileSize;
        int targetWorldY = targetRow * tileSize;

        for (Entity entity : gp.entities) {
            if (entity instanceof OBJ_PlantedCrop) {
                OBJ_PlantedCrop crop = (OBJ_PlantedCrop) entity;
                if (crop.worldX == targetWorldX && crop.worldY == targetWorldY) {
                    cropToWater = crop;
                    break;
                }
            }
        }

        if (cropToWater != null) {
            System.out.println("DEBUG WateringCommand: Found crop " + cropToWater.getCropType() + " at target. Ready: "
                    + cropToWater.isReadyToHarvest() + ", Watered: " + cropToWater.isWatered());
            if (cropToWater.isReadyToHarvest()) {
                gp.ui.showMessage(cropToWater.getCropType() + " sudah siap dipanen. Tidak perlu air lagi.");
                return;
            }
            if (cropToWater.isWatered()) {
                gp.ui.showMessage(cropToWater.getCropType() + " sudah diairi hari ini");
                return;
            }

            boolean success = player.tryDecreaseEnergy(5);
            if (!success) {
                gp.ui.showMessage("Lelah sekali! Ga bisa menyiram");
                return;
            }

            cropToWater.water();
            gp.ui.showMessage("Berhasil menyiram " + cropToWater.getCropType() + " hari ini");
            gp.gameClock.getTime().advanceTime(5);

        } else if (tileIndexAtTarget == plantedSoilIndex) {

            System.out.println(
                    "DEBUG WateringCommand: Tile is PLANTED_SOIL (55) but no crop entity found. Watering tile directly.");
            boolean success = player.tryDecreaseEnergy(5);
            if (!success) {
                gp.ui.showMessage("Lelah sekali! Ga bisa menyiram");
                return;
            }
            gp.tileManager.mapTileNum[targetCol][targetRow] = wateredPlantedIndex;
            gp.ui.showMessage("Berhasil menyiram!");
            gp.gameClock.getTime().advanceTime(5);

        } else if (tileIndexAtTarget == wateredPlantedIndex) {

            gp.ui.showMessage("Sudah disiram hari ini!");
        } else {
            gp.ui.showMessage("Tile tidak bisa disiram");
        }
    }
}