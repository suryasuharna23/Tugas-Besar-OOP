package spakborhills.action;

import spakborhills.GamePanel;
import spakborhills.entity.Player;
import spakborhills.enums.TileState;

public class PlantingCommand implements Command {
    private final Player player;

    public PlantingCommand(Player player) {
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
        if (playerCol < 0 || playerCol >= gp.maxWorldCol || playerRow < 0 || playerRow >= gp.maxWorldRow)
            return;

        // Ambil index tile
        int tileIndex = gp.tileManager.mapTileNum[playerCol][playerRow];

        // Jika tile adalah SOIL maka bisa ditanami
        if (tileIndex == getTileIndexFromState(TileState.SOIL)) {
            boolean success = player.tryDecreaseEnergy(5);
            if (!success) return;

            gp.tileManager.mapTileNum[playerCol][playerRow] = 55; // Asumsikan 6 = tile dengan benih
            gp.gameClock.getTime().advanceTime(5);

            System.out.println("Benih berhasil ditanam.");
        } else {
            System.out.println("Tile ini bukan SOIL, tidak bisa ditanami.");
        }
    }

    private int getTileIndexFromState(TileState state) {
        return switch (state) {
            case LAND -> 14;  // sesuaikan index LAND
            case SOIL -> 76;  // sesuaikan index SOIL
        };
    }
}