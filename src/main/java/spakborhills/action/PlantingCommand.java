package spakborhills.action;

import spakborhills.GamePanel;
import spakborhills.entity.Player;
import spakborhills.enums.Season;
import spakborhills.enums.TileState;
import spakborhills.object.OBJ_Seed;
import spakborhills.entity.Entity;

public class PlantingCommand implements Command {
    private final Player player;

    public PlantingCommand(Player player) {
        this.player = player;
    }

    @Override
    public void execute(GamePanel gp) {
        Entity equipped = player.getEquippedItem();

        // Validasi: harus seed
        if (!(equipped instanceof OBJ_Seed)) {
            System.out.println("Anda harus memegang benih untuk menanam.");
            return;
        }

        OBJ_Seed seed = (OBJ_Seed) equipped;

        // Validasi musim
        Season currentSeason = gp.gameClock.getCurrentSeason(); // sesuaikan jika kamu simpan musim di tempat lain
        if (seed.getSeason() != currentSeason) {
            System.out.println("Benih ini hanya bisa ditanam pada musim " + seed.getSeason() + ".");
            return;
        }

        // Hitung posisi tile di depan player
        int tileSize = gp.tileSize;
        int playerCol = (player.worldX + player.solidArea.x) / tileSize;
        int playerRow = (player.worldY + player.solidArea.y) / tileSize;

        switch (player.direction) {
            case "up" -> playerRow--;
            case "down" -> playerRow++;
            case "left" -> playerCol--;
            case "right" -> playerCol++;
        }

        // Validasi map
        if (playerCol < 0 || playerCol >= gp.maxWorldCol || playerRow < 0 || playerRow >= gp.maxWorldRow)
            return;

        int tileIndex = gp.tileManager.mapTileNum[playerCol][playerRow];

        if (tileIndex == getTileIndexFromState(TileState.SOIL)) {
            boolean success = player.tryDecreaseEnergy(5);
            if (!success) return;

            // Ubah tile jadi PLANTED
            gp.tileManager.mapTileNum[playerCol][playerRow] = getTileIndexFromState(TileState.PLANTED);
            gp.gameClock.getTime().advanceTime(5);

            // Catat penanaman
            System.out.println("Benih " + seed.getName() + " berhasil ditanam.");

            // Kurangi jumlah seed dari inventory
            player.consumeItemFromInventory(seed);

        } else {
            System.out.println("Tile ini tidak bisa ditanami (bukan SOIL).");
        }
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