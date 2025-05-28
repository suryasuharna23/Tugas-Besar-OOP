package spakborhills.action;

import spakborhills.GamePanel;
import spakborhills.entity.Player;
import spakborhills.enums.TileState;
import spakborhills.object.OBJ_Equipment;
import spakborhills.object.OBJ_Item;
import spakborhills.entity.Entity;

public class TillingCommand implements Command {
    private final Player player;

    public TillingCommand(Player player) {
        this.player = player;
    }

    @Override
    public void execute(GamePanel gp) {
        int tileSize = gp.tileSize;
        int playerCol = (player.worldX + player.solidArea.x) / tileSize;
        int playerRow = (player.worldY + player.solidArea.y) / tileSize;


        // Hitung posisi di depan player
        switch (player.direction) {
            case "up" -> playerRow--;
            case "down" -> playerRow++;
            case "left" -> playerCol--;
            case "right" -> playerCol++;
        }

        if (!player.isHoldingTool("Hoe equipment")) {
            System.out.println("Anda harus memegang Hoe.");
            return;
        }


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
            case LAND -> 14;             // Sesuaikan dengan index land.png
            case SOIL -> 76;             // Sesuaikan dengan index soil.png
            case PLANTED -> 55;          // Sesuaikan dengan index planted.png
            case WATERED_PLANT -> 80;
        };
    }
}