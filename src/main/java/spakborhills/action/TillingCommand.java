package spakborhills.action;

import spakborhills.GamePanel;
import spakborhills.entity.Player;
import spakborhills.enums.TileState;

public class TillingCommand implements Command {
    private final Player player;

    public TillingCommand(Player player) {
        this.player = player;
    }

    @Override
    public void execute(GamePanel gp) {
        int tileSize = gp.tileSize;
        int playerCol = player.worldX / tileSize;
        int playerRow = player.worldY / tileSize;

        // Hitung posisi di depan player
        switch (player.direction) {
            case "up" -> playerRow--;
            case "down" -> playerRow++;
            case "left" -> playerCol--;
            case "right" -> playerCol++;
        }

        // Validasi batas map
        if (playerCol < 0 || playerCol >= gp.maxWorldCol || playerRow < 0 || playerRow >= gp.maxWorldRow)
            return;

        // [DISABLED] Validasi alat Hoe untuk uji coba
        /*
        if (!hasHoeEquippedOrInInventory()) {
            System.out.println("Anda membutuhkan Hoe untuk membajak tanah.");
            return;
        }
        */

        // Ambil tile depan
        int tileIndex = gp.tileManager.mapTileNum[playerCol][playerRow];

        if (tileIndex == getTileIndexFromState(TileState.LAND)) {
            boolean success = player.tryDecreaseEnergy(5);
            if (!success) return;

            gp.tileManager.mapTileNum[playerCol][playerRow] = getTileIndexFromState(TileState.SOIL);
            gp.gameClock.getTime().advanceTime(5);

            System.out.println("Tile berhasil dibajak.");
        } else {
            System.out.println("Tile ini tidak bisa dibajak.");
        }
    }

    private int getTileIndexFromState(TileState state) {
        return switch (state) {
            case LAND -> 14;
            case SOIL -> 76;
        };
    }
}