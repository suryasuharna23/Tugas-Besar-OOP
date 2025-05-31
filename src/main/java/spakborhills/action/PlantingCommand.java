package spakborhills.action;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.entity.Player;
import spakborhills.enums.Season;
import spakborhills.enums.TileState;
import spakborhills.object.OBJ_PlantedCrop;
import spakborhills.object.OBJ_Seed;

public class PlantingCommand implements Command {
    private final Player player;

    public PlantingCommand(Player player) {
        this.player = player;
    }

    @Override
    public void execute(GamePanel gp) {
        Entity equipped = player.getEquippedItem();

        if (!(equipped instanceof OBJ_Seed)) {
            gp.ui.showMessage("Kamu harus equip seed dulu.");
            return;
        }

        OBJ_Seed seed = (OBJ_Seed) equipped;

        Season currentSeason = gp.gameClock.getCurrentSeason();
        if (seed.getSeason() != currentSeason) {
            gp.ui.showMessage("Seed ini hanya bisa ditanam di musim " + seed.getSeason() + ".");
            return;
        }

        int tileSize = gp.tileSize;
        int playerCol = (player.worldX + player.solidArea.x) / tileSize;
        int playerRow = (player.worldY + player.solidArea.y) / tileSize;

        switch (player.direction) {
            case "up" -> playerRow--;
            case "down" -> playerRow++;
            case "left" -> playerCol--;
            case "right" -> playerCol++;
        }

        if (playerCol < 0 || playerCol >= gp.maxWorldCol || playerRow < 0 || playerRow >= gp.maxWorldRow) {
            gp.ui.showMessage("Tidak bisa ditanam di luar batas map");
            return;
        }

        int tileIndex = gp.tileManager.mapTileNum[playerCol][playerRow];

        if (tileIndex == getTileIndexFromState(TileState.SOIL)) {

            if (isLocationOccupied(gp, playerCol * tileSize, playerRow * tileSize)) {
                gp.ui.showMessage("Sudah ada tanaman di sini!");
                return;
            }

            boolean success = player.tryDecreaseEnergy(5);
            if (!success) {
                gp.ui.showMessage("Lelah sekali! Tidak bisa planting");
                return;
            }

            gp.tileManager.mapTileNum[playerCol][playerRow] = getTileIndexFromState(TileState.PLANTED);

            String cropType = seed.name.replace(" seeds", "").replace(" seed", "");
            int worldX = playerCol * tileSize;
            int worldY = playerRow * tileSize;
            OBJ_PlantedCrop plantedCrop = new OBJ_PlantedCrop(gp, cropType, worldX, worldY);
            gp.entities.add(plantedCrop);

            gp.gameClock.getTime().advanceTime(5);

            gp.ui.showMessage("Berhasil menanam " + cropType + " seeds!");
            System.out.println("DEBUG: Planted " + cropType + " at tile (" + playerCol + "," + playerRow + ")");

            player.consumeItemFromInventory(seed);

        } else {
            gp.ui.showMessage("Tile ini tidak bisa ditanam (perlu tilled soil).");
            System.out.println(
                    "DEBUG: Tile index " + tileIndex + " is not SOIL (" + getTileIndexFromState(TileState.SOIL) + ")");
        }
    }

    private boolean isLocationOccupied(GamePanel gp, int worldX, int worldY) {

        for (Entity entity : gp.entities) {
            if (entity instanceof OBJ_PlantedCrop) {
                int distance = Math.abs(entity.worldX - worldX) + Math.abs(entity.worldY - worldY);
                if (distance < gp.tileSize / 2) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getTileIndexFromState(TileState state) {
        return switch (state) {
            case LAND -> 14;
            case SOIL -> 76;
            case PLANTED -> 55;
            case WATERED_PLANT -> 80;
        };
    }
}