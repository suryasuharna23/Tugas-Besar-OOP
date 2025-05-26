package spakborhills.action;

import spakborhills.GamePanel;
import spakborhills.entity.Player;

public class WateringCommand implements Command {
    private final Player player;

    public WateringCommand(Player player) {
        this.player = player;
    }

    @Override
    public void execute(GamePanel gp) {
        int tileSize = gp.tileSize;
        int playerCol = (player.worldX + player.solidArea.x) / tileSize;
        int playerRow = (player.worldY + player.solidArea.y) / tileSize;

        // Hitung posisi tile di depan player
        switch (player.direction) {
            case "up" -> playerRow--;
            case "down" -> playerRow++;
            case "left" -> playerCol--;
            case "right" -> playerCol++;
        }

        // Validasi batas map
        if (playerCol < 0 || playerCol >= gp.maxWorldCol || playerRow < 0 || playerRow >= gp.maxWorldRow) {
            return;
        }
        // Ambil tile
        int tileIndex = gp.tileManager.mapTileNum[playerCol][playerRow];
        int plantedSoilIndex = 55;       // Tile sudah ditanami
        int wateredPlantedIndex = 80;    // Tile setelah disiram

        if (tileIndex == plantedSoilIndex) {
            boolean success = player.tryDecreaseEnergy(5);
            if (!success) return;

            gp.tileManager.mapTileNum[playerCol][playerRow] = wateredPlantedIndex;
            gp.gameClock.getTime().advanceTime(5);
            System.out.println("Tanah berhasil disiram.");
        } else {
            System.out.println("Tile ini tidak bisa disiram.");
        }
    }
}