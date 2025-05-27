package spakborhills;

import spakborhills.entity.Entity;
import java.util.ArrayList;

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {
        
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        
        
        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        int tileNum1, tileNum2;

        
        java.util.function.IntPredicate isSolidTile = (tileNum) -> {
            
            if (tileNum >= 0 && tileNum < gp.tileManager.tile.length) {
                
                if (gp.tileManager.tile[tileNum] != null) {
                    return gp.tileManager.tile[tileNum].collision; 
                } else {
                    return true; 
                }
            }
            
            return true;
        };

        switch (entity.direction) {
            case "up":
                int nextEntityTopY = entityTopWorldY - entity.speed;
                int nextTopRow = nextEntityTopY / gp.tileSize; 

                
                if (entityLeftCol < 0 || entityLeftCol >= gp.maxWorldCol ||
                        entityRightCol < 0 || entityRightCol >= gp.maxWorldCol ||
                        nextTopRow < 0 || nextTopRow >= gp.maxWorldRow) {
                    entity.collisionON = true;
                    break;
                }

                tileNum1 = gp.tileManager.mapTileNum[entityLeftCol][nextTopRow];
                tileNum2 = gp.tileManager.mapTileNum[entityRightCol][nextTopRow];

                if (isSolidTile.test(tileNum1) || isSolidTile.test(tileNum2)) {
                    entity.collisionON = true;
                }
                break;
            case "down":
                int nextEntityBottomY = entityBottomWorldY + entity.speed;
                int nextBottomRow = nextEntityBottomY / gp.tileSize;

                if (entityLeftCol < 0 || entityLeftCol >= gp.maxWorldCol ||
                        entityRightCol < 0 || entityRightCol >= gp.maxWorldCol ||
                        nextBottomRow < 0 || nextBottomRow >= gp.maxWorldRow) {
                    entity.collisionON = true;
                    break;
                }

                tileNum1 = gp.tileManager.mapTileNum[entityLeftCol][nextBottomRow];
                tileNum2 = gp.tileManager.mapTileNum[entityRightCol][nextBottomRow];
                if (isSolidTile.test(tileNum1) || isSolidTile.test(tileNum2)) {
                    entity.collisionON = true;
                }
                break;
            case "left":
                int nextEntityLeftX = entityLeftWorldX - entity.speed;
                int nextLeftCol = nextEntityLeftX / gp.tileSize;

                if (nextLeftCol < 0 || nextLeftCol >= gp.maxWorldCol ||
                        entityTopRow < 0 || entityTopRow >= gp.maxWorldRow ||
                        entityBottomRow < 0 || entityBottomRow >= gp.maxWorldRow) {
                    entity.collisionON = true;
                    break;
                }

                tileNum1 = gp.tileManager.mapTileNum[nextLeftCol][entityTopRow];
                tileNum2 = gp.tileManager.mapTileNum[nextLeftCol][entityBottomRow];
                if (isSolidTile.test(tileNum1) || isSolidTile.test(tileNum2)) {
                    entity.collisionON = true;
                }
                break;
            case "right":
                int nextEntityRightX = entityRightWorldX + entity.speed;
                int nextRightCol = nextEntityRightX / gp.tileSize;

                if (nextRightCol < 0 || nextRightCol >= gp.maxWorldCol ||
                        entityTopRow < 0 || entityTopRow >= gp.maxWorldRow ||
                        entityBottomRow < 0 || entityBottomRow >= gp.maxWorldRow) {
                    entity.collisionON = true;
                    break;
                }

                tileNum1 = gp.tileManager.mapTileNum[nextRightCol][entityTopRow];
                tileNum2 = gp.tileManager.mapTileNum[nextRightCol][entityBottomRow];
                if (isSolidTile.test(tileNum1) || isSolidTile.test(tileNum2)) {
                    entity.collisionON = true;
                }
                break;
        }
    }

    /**
     * Memeriksa tabrakan antara sebuah entitas dan daftar objek (SuperObject).
     * Mengembalikan indeks objek yang bertabrakan, atau 999 jika tidak ada tabrakan yang signifikan
     * (misalnya, jika objek tidak solid tetapi pemain berinteraksi dengannya).
     *
     * @param entity   Entitas yang melakukan pemeriksaan.
     * @param isPlayer Apakah entitas tersebut adalah pemain (untuk menentukan apakah interaksi/pickup terjadi).
     * @return Indeks objek yang berinteraksi, atau 999.
     */
    public int checkObject(Entity entity, boolean isPlayer) {
        int index = 999;

        for (int i = 0; i < gp.entities.size(); i++) {
            Entity currentObject = gp.entities.get(i);
            if (currentObject == null) {
                continue;
            }

            
            entity.solidArea.x = entity.worldX + entity.solidAreaDefaultX;
            entity.solidArea.y = entity.worldY + entity.solidAreaDefaultY;

            
            currentObject.solidArea.x = currentObject.worldX + currentObject.solidAreaDefaultX;
            currentObject.solidArea.y = currentObject.worldY + currentObject.solidAreaDefaultY;

            
            switch (entity.direction) {
                case "up":
                    entity.solidArea.y -= entity.speed;
                    break;
                case "down":
                    entity.solidArea.y += entity.speed;
                    break;
                case "left":
                    entity.solidArea.x -= entity.speed;
                    break;
                case "right":
                    entity.solidArea.x += entity.speed;
                    break;
            }

            if (entity.solidArea.intersects(currentObject.solidArea)) {
                if (currentObject.collision) { 
                    entity.collisionON = true; 
                }
                if (isPlayer) { 
                    index = i;
                }
            }
            
            
            entity.solidArea.x = entity.solidAreaDefaultX;
            entity.solidArea.y = entity.solidAreaDefaultY;

            
            currentObject.solidArea.x = currentObject.solidAreaDefaultX;
            currentObject.solidArea.y = currentObject.solidAreaDefaultY;
        }
        return index;
    }

    /**
     * Memeriksa tabrakan antara sebuah entitas (misalnya pemain) dan daftar NPC.
     *
     * @param entity         Entitas yang melakukan pemeriksaan (biasanya pemain).
     * @param targetNPCs     Daftar NPC yang akan diperiksa tabrakannya (biasanya gp.npc).
     * @return Indeks NPC yang bertabrakan, atau 999 jika tidak ada.
     */
    public int checkEntity(Entity entity, ArrayList<Entity> targetNPCs) {
        int index = 999;

        for (int i = 0; i < targetNPCs.size(); i++) {
            Entity targetNPC = targetNPCs.get(i);
            if (targetNPC == null || targetNPC == entity) { 
                continue;
            }

            
            entity.solidArea.x = entity.worldX + entity.solidAreaDefaultX;
            entity.solidArea.y = entity.worldY + entity.solidAreaDefaultY;

            
            targetNPC.solidArea.x = targetNPC.worldX + targetNPC.solidAreaDefaultX;
            targetNPC.solidArea.y = targetNPC.worldY + targetNPC.solidAreaDefaultY;

            
            switch (entity.direction) {
                case "up":
                    entity.solidArea.y -= entity.speed;
                    break;
                case "down":
                    entity.solidArea.y += entity.speed;
                    break;
                case "left":
                    entity.solidArea.x -= entity.speed;
                    break;
                case "right":
                    entity.solidArea.x += entity.speed;
                    break;
            }

            if (entity.solidArea.intersects(targetNPC.solidArea)) {
                
                if (targetNPC.collision) { 
                    entity.collisionON = true;
                }
                index = i; 
            }

            
            entity.solidArea.x = entity.solidAreaDefaultX;
            entity.solidArea.y = entity.solidAreaDefaultY;

            
            targetNPC.solidArea.x = targetNPC.solidAreaDefaultX;
            targetNPC.solidArea.y = targetNPC.solidAreaDefaultY;
        }
        return index;
    }

    /**
     * Memeriksa tabrakan antara sebuah entitas (biasanya NPC yang bergerak) dan pemain.
     * Ini digunakan agar NPC tidak menembus pemain.
     *
     * @param npcEntity Entitas NPC yang melakukan pemeriksaan.
     */
    public void checkPlayer(Entity npcEntity) {
        
        npcEntity.solidArea.x = npcEntity.worldX + npcEntity.solidAreaDefaultX;
        npcEntity.solidArea.y = npcEntity.worldY + npcEntity.solidAreaDefaultY;

        
        gp.player.solidArea.x = gp.player.worldX + gp.player.solidAreaDefaultX;
        gp.player.solidArea.y = gp.player.worldY + gp.player.solidAreaDefaultY;

        
        switch (npcEntity.direction) {
            case "up":
                npcEntity.solidArea.y -= npcEntity.speed;
                break;
            case "down":
                npcEntity.solidArea.y += npcEntity.speed;
                break;
            case "left":
                npcEntity.solidArea.x -= npcEntity.speed;
                break;
            case "right":
                npcEntity.solidArea.x += npcEntity.speed;
                break;
        }

        if (npcEntity.solidArea.intersects(gp.player.solidArea)) {
            
            npcEntity.collisionON = true; 
        }

        
        npcEntity.solidArea.x = npcEntity.solidAreaDefaultX;
        npcEntity.solidArea.y = npcEntity.solidAreaDefaultY;

        
        gp.player.solidArea.x = gp.player.solidAreaDefaultX;
        gp.player.solidArea.y = gp.player.solidAreaDefaultY;
    }
}