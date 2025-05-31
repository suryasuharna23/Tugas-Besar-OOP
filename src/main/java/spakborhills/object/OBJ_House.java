package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.enums.EntityType;

public class OBJ_House extends Entity {
    public OBJ_House(GamePanel gp) {
        super(gp);
        type = EntityType.INTERACTIVE_OBJECT;
        name = "House";
        down1 = setup("/objects/House");
        collision = true;
        int tilesWide = 6;
        int tilesHigh = 6;

        this.imageWidth = tilesWide * gp.tileSize;
        this.imageHeight = tilesHigh * gp.tileSize;
        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.width = this.imageWidth;
        solidArea.height = this.imageHeight;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    public void interact() {
        System.out.println("[OBJ_House] Player is interacting with the house...");

        if (gp.currentMapIndex == gp.PLAYER_HOUSE_INDEX) {

            teleportToFarm();
        } else {

            teleportToPlayerHouse();
        }
    }

    private void teleportToPlayerHouse() {
        System.out.println("[OBJ_House] Teleporting player to Player's House...");

        int playerHouseIndex = -1;
        for (int i = 0; i < gp.mapInfos.size(); i++) {
            if (gp.mapInfos.get(i).getMapName().equalsIgnoreCase("Player's House")) {
                playerHouseIndex = i;
                break;
            }
        }

        if (playerHouseIndex != -1) {
            gp.ui.showMessage("Masuk rumah...");
            gp.loadMapbyIndex(playerHouseIndex);
            gp.playSE(4);

            int[] safePosition = findSafeEntrancePosition();
            gp.player.setPositionForMapEntry(safePosition[0], safePosition[1], "down");

            System.out.println("[OBJ_House] Player teleported to Player's House at safe position (" +
                    safePosition[0] + "," + safePosition[1] + ")");
        } else {
            System.err.println("[OBJ_House] ERROR: Player's House map not found!");
            gp.ui.showMessage("Tidak bisa masuk rumah sekarang!");
        }
    }

    private int[] findSafeEntrancePosition() {

        int defaultX = gp.tileSize * 7;
        int defaultY = gp.tileSize * 6;

        if (isSafePosition(defaultX, defaultY)) {
            System.out.println("[OBJ_House] Using default safe entrance position");
            return new int[] { defaultX, defaultY };
        }

        System.out.println("[OBJ_House] Default entrance position not safe, finding alternative...");

        int[][] offsets = {

                { 0, 0 }, { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 },
                { 1, 1 }, { -1, -1 }, { 1, -1 }, { -1, 1 },

                { 2, 0 }, { -2, 0 }, { 0, 2 }, { 0, -2 },
                { 2, 1 }, { -2, 1 }, { 1, 2 }, { 1, -2 },
                { 2, 2 }, { -2, -2 }, { 2, -2 }, { -2, 2 },

                { 3, 0 }, { -3, 0 }, { 0, 3 }, { 0, -3 }
        };

        for (int[] offset : offsets) {
            int testX = defaultX + (offset[0] * gp.tileSize);
            int testY = defaultY + (offset[1] * gp.tileSize);

            if (isSafePosition(testX, testY)) {
                System.out.println("[OBJ_House] Found safe entrance at offset (" + offset[0] + "," + offset[1] + ")");
                return new int[] { testX, testY };
            }
        }

        int[][] emergencyPositions = {
                { 5, 5 }, { 6, 5 }, { 8, 5 }, { 9, 5 },
                { 5, 7 }, { 6, 7 }, { 8, 7 }, { 9, 7 },
                { 5, 9 }, { 6, 9 }, { 8, 9 }, { 9, 9 }
        };

        for (int[] pos : emergencyPositions) {
            int emergencyX = pos[0] * gp.tileSize;
            int emergencyY = pos[1] * gp.tileSize;

            if (isSafePosition(emergencyX, emergencyY)) {
                System.out.println("[OBJ_House] Using emergency safe position: (" + pos[0] + "," + pos[1] + ")");
                return new int[] { emergencyX, emergencyY };
            }
        }

        System.out.println("[OBJ_House] No safe position found, using default anyway");
        return new int[] { defaultX, defaultY };
    }

    private int[] findSafeFarmExitPosition() {

        if (gp.currentFarmLayout != null) {

            int safeX = (gp.currentFarmLayout.houseX + 3) * gp.tileSize;
            int safeY = (gp.currentFarmLayout.houseY + 6) * gp.tileSize;

            if (isSafeFarmPosition(safeX, safeY)) {
                System.out.println("[OBJ_House] Using layout-based safe farm position");
                return new int[] { safeX, safeY };
            }
        }

        int[][] fallbackPositions = {
                { 3, 3 }, { 4, 3 }, { 5, 3 },
                { 3, 4 }, { 4, 4 }, { 5, 4 },
                { 3, 15 }, { 4, 15 }, { 5, 15 },
                { 3, 16 }, { 4, 16 }, { 5, 16 },
                { 25, 25 }, { 26, 25 }, { 27, 25 }
        };

        for (int[] pos : fallbackPositions) {
            int testX = pos[0] * gp.tileSize;
            int testY = pos[1] * gp.tileSize;

            if (isSafeFarmPosition(testX, testY)) {
                System.out.println("[OBJ_House] Using fallback farm position: (" + pos[0] + "," + pos[1] + ")");
                return new int[] { testX, testY };
            }
        }

        System.out.println("[OBJ_House] Using emergency farm position");
        return new int[] { 3 * gp.tileSize, 3 * gp.tileSize };
    }

    private boolean isSafePosition(int worldX, int worldY) {

        int tileX = worldX / gp.tileSize;
        int tileY = worldY / gp.tileSize;

        if (tileX < 0 || tileX >= gp.maxWorldCol || tileY < 0 || tileY >= gp.maxWorldRow) {
            return false;
        }

        if (gp.tileManager != null && gp.tileManager.mapTileNum != null) {

            if (tileX < gp.tileManager.mapTileNum.length && tileY < gp.tileManager.mapTileNum[0].length) {
                int tileNum = gp.tileManager.mapTileNum[tileX][tileY];

                if (tileNum < gp.tileManager.tile.length && gp.tileManager.tile[tileNum].collision) {
                    return false;
                }
            }
        }

        if (gp.entities != null) {
            for (Entity entity : gp.entities) {
                if (entity != null && entity.collision) {
                    int objTileX = entity.worldX / gp.tileSize;
                    int objTileY = entity.worldY / gp.tileSize;

                    if (Math.abs(objTileX - tileX) < 3 && Math.abs(objTileY - tileY) < 3) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private void teleportToFarm() {
        System.out.println("[OBJ_House] Teleporting player back to Farm...");

        int farmMapIndex = -1;
        for (int i = 0; i < gp.mapInfos.size(); i++) {
            if (gp.mapInfos.get(i).getMapName().equalsIgnoreCase("Farm")) {
                farmMapIndex = i;
                break;
            }
        }

        if (farmMapIndex != -1) {
            gp.ui.showMessage("Keluar rumah...");
            gp.loadMapbyIndex(farmMapIndex);
            gp.playSE(4);

            int[] safePosition = findSafeFarmExitPosition();
            gp.player.setPositionForMapEntry(safePosition[0], safePosition[1], "down");

            System.out.println("[OBJ_House] Player teleported to Farm at safe position (" +
                    safePosition[0] + "," + safePosition[1] + ")");
        } else {
            System.err.println("[OBJ_House] ERROR: Farm map not found!");
            gp.ui.showMessage("Tidak bisa keluar rumah sekarang!");
        }
    }

    private boolean isSafeFarmPosition(int worldX, int worldY) {
        int tileX = worldX / gp.tileSize;
        int tileY = worldY / gp.tileSize;

        if (tileX < 1 || tileX >= 31 || tileY < 1 || tileY >= 31) {
            return false;
        }

        if (gp.currentFarmLayout != null) {

            if (isNearObject(tileX, tileY, gp.currentFarmLayout.houseX, gp.currentFarmLayout.houseY, 6, 6, 2)) {
                return false;
            }

            if (isNearObject(tileX, tileY, gp.currentFarmLayout.shippingBinX, gp.currentFarmLayout.shippingBinY, 3, 2,
                    1)) {
                return false;
            }

            if (isNearObject(tileX, tileY, gp.currentFarmLayout.pondX, gp.currentFarmLayout.pondY, 4, 3, 1)) {
                return false;
            }
        }

        if (gp.farmMapTileData != null && tileX < gp.farmMapTileData.length && tileY < gp.farmMapTileData[0].length) {
            int tileNum = gp.farmMapTileData[tileX][tileY];
            if (gp.tileManager != null && tileNum < gp.tileManager.tile.length
                    && gp.tileManager.tile[tileNum].collision) {
                return false;
            }
        }

        return true;
    }

    private boolean isNearObject(int playerX, int playerY, int objX, int objY,
            int objWidth, int objHeight, int minDistance) {
        return (playerX >= objX - minDistance &&
                playerX <= objX + objWidth + minDistance - 1 &&
                playerY >= objY - minDistance &&
                playerY <= objY + objHeight + minDistance - 1);
    }
}
