package spakborhills;

import spakborhills.entity.Entity;

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp){
        this.gp = gp;
    }
    public void checkTile(Entity entity){
        // Calculate the entity’s solid area boundaries in world coordinates
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        // Determine the entity’s a current column/row based on its solid area.
        int currentEntityLeftCol = entityLeftWorldX / gp.tileSize;
        int currentEntityRightCol = entityRightWorldX / gp.tileSize;
        int currentEntityTopRow = entityTopWorldY / gp.tileSize;
        int currentEntityBottomRow = entityBottomWorldY / gp.tileSize;

        int tileToCheckCol1, tileToCheckRow1; // Coordinates for the first collision check point
        int tileToCheckCol2, tileToCheckRow2; // Coordinates for the second collision check point

        // Determine the target tiles based on the entity’s direction
        switch (entity.direction) {
            case "up":
                int nextTopWorldY = entityTopWorldY - entity.speed;
                if (nextTopWorldY < 0) {
                    entity.collisionON = true;
                    return;
                }
                tileToCheckCol1 = currentEntityLeftCol;
                tileToCheckRow1 = nextTopWorldY / gp.tileSize;
                tileToCheckCol2 = currentEntityRightCol;
                tileToCheckRow2 = nextTopWorldY / gp.tileSize;
                break;
            case "down":
                int nextBottomWorldY = entityBottomWorldY + entity.speed;
                if (nextBottomWorldY >= gp.maxWorldCol * gp.tileSize) {
                    entity.collisionON = true;
                    return;
                }
                tileToCheckCol1 = currentEntityLeftCol;
                tileToCheckRow1 = nextBottomWorldY / gp.tileSize;
                tileToCheckCol2 = currentEntityRightCol;
                tileToCheckRow2 = nextBottomWorldY / gp.tileSize;
                break;
            case "left":
                int nextLeftWorldX = entityLeftWorldX - entity.speed;
                if (nextLeftWorldX < 0) {
                    entity.collisionON = true;
                    return;
                }
                tileToCheckCol1 = nextLeftWorldX / gp.tileSize;
                tileToCheckRow1 = currentEntityTopRow;
                tileToCheckCol2 = nextLeftWorldX / gp.tileSize;
                tileToCheckRow2 = currentEntityBottomRow;
                break;
            case "right":
                int nextRightWorldX = entityRightWorldX + entity.speed;
                if (nextRightWorldX >= gp.maxWorldRow * gp.tileSize) {
                    entity.collisionON = true;
                    return;
                }
                tileToCheckCol1 = nextRightWorldX / gp.tileSize;
                tileToCheckRow1 = currentEntityTopRow;
                tileToCheckCol2 = nextRightWorldX / gp.tileSize;
                tileToCheckRow2 = currentEntityBottomRow;
                break;
            default:
                return;
        }
        // If within bounds, get the tile numbers and check their collision property.
        // Ensure the tile indices are within the valid range before accessing the mapTileNum array.
        if (tileToCheckCol1 >= 0 && tileToCheckCol1 < gp.maxWorldCol &&
                tileToCheckRow1 >= 0 && tileToCheckRow1 < gp.maxWorldRow &&
                tileToCheckCol2 >= 0 && tileToCheckCol2 < gp.maxWorldCol &&
                tileToCheckRow2 >= 0 && tileToCheckRow2 < gp.maxWorldRow) {

            int tileNum1 = gp.tileManager.mapTileNum[tileToCheckCol1][tileToCheckRow1];
            int tileNum2 = gp.tileManager.mapTileNum[tileToCheckCol2][tileToCheckRow2];

            if (gp.tileManager.tile[tileNum1].collision || gp.tileManager.tile[tileNum2].collision) {
                entity.collisionON = true;
            }
        }
    }

    public int checkObject(Entity entity, boolean player){
        int index = 999;
        for(int i = 0; i < gp.obj.length; i++){
            if(gp.obj[i] != null){
                // Get entities solid area position
                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;
                // Get object solid area position
                gp.obj[i].solidArea.x = gp.obj[i].worldX + gp.obj[i].solidArea.x;
                gp.obj[i].solidArea.y = gp.obj[i].worldY + gp.obj[i].solidArea.y;

                switch(entity.direction){
                    case "up":
                        entity.solidArea.y -= entity.speed;
                        if(entity.solidArea.intersects(gp.obj[i].solidArea)){
                            if(gp.obj[i].collision){
                                entity.collisionON = true;
                            }
                            if(player == true){
                                index = i;
                            }
                        }
                        break;
                    case "down":
                        entity.solidArea.y += entity.speed;
                        if(entity.solidArea.intersects(gp.obj[i].solidArea)){
                            if(gp.obj[i].collision){
                                entity.collisionON = true;
                            }
                            if(player == true){
                                index = i;
                            }
                        }
                        break;
                    case "left":
                        entity.solidArea.x -= entity.speed;
                        if(entity.solidArea.intersects(gp.obj[i].solidArea)){
                            if(gp.obj[i].collision){
                                entity.collisionON = true;
                            }
                            if(player == true){
                                index = i;
                            }
                        }
                        break;
                    case "right":
                        entity.solidArea.x += entity.speed;
                        if(entity.solidArea.intersects(gp.obj[i].solidArea)){
                            if(gp.obj[i].collision){
                                entity.collisionON = true;
                            }
                            if(player == true){
                                index = i;
                            }
                        }
                        break;
                }
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                gp.obj[i].solidArea.x = gp.obj[i].solidAreaDefaultX;
                gp.obj[i].solidArea.y = gp.obj[i].solidAreaDefaultY;
            }
        }
        return index;
    }
    //NPC COLLISION
    public int checkEntity(Entity entity, Entity[] target){
        int index = 999;

        for(int i = 0; i < target.length; i++){
            if(target[i] != null){
                // Get entities solid area position
                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;
                // Get object solid area position
                target[i].solidArea.x = target[i].worldX + target[i].solidArea.x;
                target[i].solidArea.y = target[i].worldY + target[i].solidArea.y;

                switch(entity.direction){
                    case "up":
                        entity.solidArea.y -= entity.speed;
                        if(entity.solidArea.intersects(target[i].solidArea)){
                            entity.collisionON = true;
                            index = i;
                        }
                        break;
                    case "down":
                        entity.solidArea.y += entity.speed;
                        if(entity.solidArea.intersects(target[i].solidArea)){
                            entity.collisionON = true;
                            index = i;
                        }
                        break;
                    case "left":
                        entity.solidArea.x -= entity.speed;
                        if(entity.solidArea.intersects(target[i].solidArea)){
                            entity.collisionON = true;
                            index = i;
                        }
                        break;
                    case "right":
                        entity.solidArea.x += entity.speed;
                        if(entity.solidArea.intersects(target[i].solidArea)){
                            entity.collisionON = true;
                            index = i;
                        }
                        break;
                }
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                target[i].solidArea.x = target[i].solidAreaDefaultX;
                target[i].solidArea.y = target[i].solidAreaDefaultY;
            }
        }
        return index;
    }

    public void checkPlayer(Entity entity){
        // Get entities solid area position
        entity.solidArea.x = entity.worldX + entity.solidArea.x;
        entity.solidArea.y = entity.worldY + entity.solidArea.y;
        // Get object solid area position
        gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
        gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;

        switch(entity.direction){
            case "up":
                entity.solidArea.y -= entity.speed;
                if(entity.solidArea.intersects(gp.player.solidArea)){
                    entity.collisionON = true;
                }
                break;
            case "down":
                entity.solidArea.y += entity.speed;
                if(entity.solidArea.intersects(gp.player.solidArea)){
                    entity.collisionON = true;
                }
                break;
            case "left":
                entity.solidArea.x -= entity.speed;
                if(entity.solidArea.intersects(gp.player.solidArea)){
                    entity.collisionON = true;
                }
                break;
            case "right":
                entity.solidArea.x += entity.speed;
                if(entity.solidArea.intersects(gp.player.solidArea)){
                    entity.collisionON = true;
                }
                break;
        }
        entity.solidArea.x = entity.solidAreaDefaultX;
        entity.solidArea.y = entity.solidAreaDefaultY;
        gp.player.solidArea.x = gp.player.solidAreaDefaultX;
        gp.player.solidArea.y = gp.player.solidAreaDefaultY;
    }
}
