package spakborhills.action;

import spakborhills.GamePanel;
import spakborhills.entity.Player;
import spakborhills.enums.TileState;

public class RecoverLandCommand implements Command {

    private final Player player;

    public RecoverLandCommand(Player player) {
        this.player = player;
    }

    @Override
    public void execute(GamePanel gp) {
        int tileSize = gp.tileSize;
        int playerCol = (player.worldX + player.solidArea.x) / tileSize;
        int playerRow = (player.worldY + player.solidArea.y) / tileSize;

        // Cek arah hadap
        switch (player.direction) {
            case "up" -> playerRow--;
            case "down" -> playerRow++;
            case "left" -> playerCol--;
            case "right" -> playerCol++;
        }

        if (!player.isHoldingTool("Pickaxe equipment")) {
            System.out.println("Anda harus memegang Pickaxe.");
            return;
        }

        // Validasi batas
        if (playerCol < 0 || playerCol >= gp.maxWorldCol || playerRow < 0 || playerRow >= gp.maxWorldRow) {
            return;
        }

        int tileIndex = gp.tileManager.mapTileNum[playerCol][playerRow];

        // Periksa apakah tile bisa di-recover
        if (tileIndex == getTileIndexFromState(TileState.SOIL) ||
                tileIndex == getTileIndexFromState(TileState.PLANTED) ||
                tileIndex == getTileIndexFromState(TileState.WATERED_PLANT)) {

            boolean success = player.tryDecreaseEnergy(5);
            if (!success) return;

            gp.tileManager.mapTileNum[playerCol][playerRow] = getTileIndexFromState(TileState.LAND);
            gp.gameClock.getTime().advanceTime(5);
            System.out.println("Tile berhasil di-recover menjadi LAND.");
        } else {
            System.out.println("Tile ini tidak bisa di-recover.");
        }
    }

    private int getTileIndexFromState(TileState state) {
        return switch (state) {
            case LAND -> 14;             // Sesuaikan dengan index land.png
            case SOIL -> 76;             // Sesuaikan dengan index soil.png
            case PLANTED -> 55;          // Sesuaikan dengan index planted.png
            case WATERED_PLANT -> 80;  // Sesuaikan dengan index watered_planted.png
        };
    }
}