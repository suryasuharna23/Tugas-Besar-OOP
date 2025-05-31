package spakborhills.action;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.entity.Player;
import spakborhills.object.OBJ_PlantedCrop;

public class HarvestCommand implements Command {
    private final Player player;

    public HarvestCommand(Player player) {
        this.player = player;
    }

    @Override
    public void execute(GamePanel gp) {
        System.out.println("DEBUG HarvestCommand: Starting harvest/interaction check");

        int tileSize = gp.tileSize;
        int targetCol = (player.worldX + player.solidArea.x + player.solidArea.width / 2) / tileSize;
        int targetRow = (player.worldY + player.solidArea.y + player.solidArea.height / 2) / tileSize;

        switch (player.direction) {
            case "up" -> targetRow = (player.worldY + player.solidArea.y - 1) / tileSize;
            case "down" -> targetRow = (player.worldY + player.solidArea.y + player.solidArea.height) / tileSize;
            case "left" -> targetCol = (player.worldX + player.solidArea.x - 1) / tileSize;
            case "right" -> targetCol = (player.worldX + player.solidArea.x + player.solidArea.width) / tileSize;
        }

        System.out.println("DEBUG HarvestCommand: Checking tile (" + targetCol + "," + targetRow + ")");

        if (targetCol < 0 || targetCol >= gp.maxWorldCol || targetRow < 0 || targetRow >= gp.maxWorldRow) {
            gp.ui.showMessage("Tidak bisa diharvest di luar batas map");
            return;
        }

        int targetWorldX = targetCol * tileSize;
        int targetWorldY = targetRow * tileSize;
        OBJ_PlantedCrop cropToInteract = findPlantedCropAt(gp, targetWorldX, targetWorldY);

        System.out.println("DEBUG HarvestCommand: Found crop entity = "
                + (cropToInteract != null ? cropToInteract.getCropType() : "null"));

        if (cropToInteract != null) {

            cropToInteract.interact();

            player.tryDecreaseEnergy(5);
            gp.gameClock.getTime().advanceTime(5);
            player.totalHarvested++;
        } else {
            gp.ui.showMessage("Tidak ada crop.");
        }
    }

    private OBJ_PlantedCrop findPlantedCropAt(GamePanel gp, int worldX, int worldY) {
        for (Entity entity : gp.entities) {
            if (entity instanceof OBJ_PlantedCrop) {
                OBJ_PlantedCrop crop = (OBJ_PlantedCrop) entity;

                if (crop.worldX == worldX && crop.worldY == worldY) {
                    return crop;
                }
            }
        }
        return null;
    }

}