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
        int playerCol = (player.worldX + player.solidArea.x) / tileSize;
        int playerRow = (player.worldY + player.solidArea.y) / tileSize;
        
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
            gp.playSE(7);
            System.out.println("Tile berhasil dibajak.");
        } else {
            System.out.println("Tile ini tidak bisa dibajak.");
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