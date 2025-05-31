
package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.enums.EntityType;

public class OBJ_Door extends Entity {
    public OBJ_Door(GamePanel gp) {
        super(gp);
        type = EntityType.INTERACTIVE_OBJECT;
        name = "Door";
        down1 = setup("/objects/door");
        collision = true;

        int tilesWide = 4;
        int tilesHigh = 1;

        this.imageWidth = tilesWide * gp.tileSize;
        this.imageHeight = tilesHigh * gp.tileSize;
        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.width = this.imageWidth;
        solidArea.height = this.imageHeight;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    @Override
    public void interact() {
        gp.playSE(4);
        System.out.println("[OBJ_Door] Player is exiting the house...");
        teleportToFarm();
    }

    private void teleportToFarm() {
        System.out.println("[OBJ_Door] Teleporting player back to Farm...");

        int farmIndex = -1;
        for (int i = 0; i < gp.mapInfos.size(); i++) {
            if (gp.mapInfos.get(i).getMapName().equalsIgnoreCase("Farm")) {
                farmIndex = i;
                break;
            }
        }

        if (farmIndex != -1) {
            gp.ui.showMessage("Keluar rumah...");
            gp.loadMapbyIndex(farmIndex);

            int[] safePosition = findSafeFarmExitPosition();
            gp.player.setPositionForMapEntry(safePosition[0], safePosition[1], "down");

            System.out.println("[OBJ_Door] Player teleported to Farm at safe position (" +
                    safePosition[0] + "," + safePosition[1] + ")");
        } else {
            System.err.println("[OBJ_Door] ERROR: Farm map not found!");
            gp.ui.showMessage("Tidak bisa ke luar rumah sekarang.");
        }
    }

    private int[] findSafeFarmExitPosition() {
        System.out.println("[OBJ_Door] Finding safe farm exit position...");

        if (gp.currentFarmLayout != null) {

            int[][] houseExitOffsets = {
                    { 3, 6 },
                    { 3, 7 },
                    { 4, 6 },
                    { 4, 7 },
                    { 2, 6 },
                    { 2, 7 },
                    { 5, 6 },
                    { 1, 6 },
                    { 3, 8 },
                    { 4, 8 }
            };

            int houseX = gp.currentFarmLayout.houseX;
            int houseY = gp.currentFarmLayout.houseY;

            for (int[] offset : houseExitOffsets) {
                int exitTileX = houseX + offset[0];
                int exitTileY = houseY + offset[1];
                int exitWorldX = exitTileX * gp.tileSize;
                int exitWorldY = exitTileY * gp.tileSize;

                if (isSafeFarmPosition(exitWorldX, exitWorldY)) {
                    System.out.println("[OBJ_Door] Using layout-based safe farm position at offset (" +
                            offset[0] + "," + offset[1] + ")");
                    return new int[] { exitWorldX, exitWorldY };
                }
            }

            System.out.println("[OBJ_Door] No safe position found near house, trying fallbacks...");
        }

        int[][] fallbackPositions = {
                { 3, 3 }, { 4, 3 }, { 5, 3 },
                { 3, 4 }, { 4, 4 }, { 5, 4 },
                { 3, 15 }, { 4, 15 }, { 5, 15 },
                { 3, 16 }, { 4, 16 }, { 5, 16 },
                { 25, 25 }, { 26, 25 }, { 27, 25 },
                { 2, 2 }, { 6, 2 }, { 7, 2 },
                { 2, 28 }, { 6, 28 }, { 7, 28 }
        };

        for (int[] pos : fallbackPositions) {
            int testX = pos[0] * gp.tileSize;
            int testY = pos[1] * gp.tileSize;

            if (isSafeFarmPosition(testX, testY)) {
                System.out.println("[OBJ_Door] Using fallback farm position: (" + pos[0] + "," + pos[1] + ")");
                return new int[] { testX, testY };
            }
        }

        System.out.println("[OBJ_Door] Using emergency farm position");
        return new int[] { 3 * gp.tileSize, 3 * gp.tileSize };
    }

    private boolean isSafeFarmPosition(int worldX, int worldY) {
        int tileX = worldX / gp.tileSize;
        int tileY = worldY / gp.tileSize;

        if (tileX < 1 || tileX >= 31 || tileY < 1 || tileY >= 31) {
            return false;
        }

        if (gp.currentFarmLayout != null) {

            if (isNearObject(tileX, tileY, gp.currentFarmLayout.houseX, gp.currentFarmLayout.houseY, 6, 6, 1)) {
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

                    if (Math.abs(objTileX - tileX) < 2 && Math.abs(objTileY - tileY) < 2) {
                        return false;
                    }
                }
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