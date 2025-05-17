package spakborhills;

import spakborhills.entity.Entity;

import java.util.ArrayList;

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {
        // Hitung batas area solid entitas dalam koordinat dunia
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        // Tentukan kolom/baris entitas saat ini berdasarkan area solidnya.
        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        int tileNum1, tileNum2;

        switch (entity.direction) {
            case "up":
                // Prediksi posisi Y teratas entitas setelah bergerak
                int nextEntityTopY = entityTopWorldY - entity.speed;
                // Tentukan baris tile yang akan disentuh oleh bagian atas area solid entitas
                int nextTopRow = nextEntityTopY / gp.tileSize;
                // Ambil nomor tile di pojok kiri atas dan kanan atas dari prediksi area solid
                tileNum1 = gp.tileManager.mapTileNum[entityLeftCol][nextTopRow];
                tileNum2 = gp.tileManager.mapTileNum[entityRightCol][nextTopRow];
                if (gp.tileManager.tile[tileNum1].collision || gp.tileManager.tile[tileNum2].collision) {
                    entity.collisionON = true;
                }
                break;
            case "down":
                int nextEntityBottomY = entityBottomWorldY + entity.speed;
                int nextBottomRow = nextEntityBottomY / gp.tileSize;
                tileNum1 = gp.tileManager.mapTileNum[entityLeftCol][nextBottomRow];
                tileNum2 = gp.tileManager.mapTileNum[entityRightCol][nextBottomRow];
                if (gp.tileManager.tile[tileNum1].collision || gp.tileManager.tile[tileNum2].collision) {
                    entity.collisionON = true;
                }
                break;
            case "left":
                int nextEntityLeftX = entityLeftWorldX - entity.speed;
                int nextLeftCol = nextEntityLeftX / gp.tileSize;
                tileNum1 = gp.tileManager.mapTileNum[nextLeftCol][entityTopRow];
                tileNum2 = gp.tileManager.mapTileNum[nextLeftCol][entityBottomRow];
                if (gp.tileManager.tile[tileNum1].collision || gp.tileManager.tile[tileNum2].collision) {
                    entity.collisionON = true;
                }
                break;
            case "right":
                int nextEntityRightX = entityRightWorldX + entity.speed;
                int nextRightCol = nextEntityRightX / gp.tileSize;
                tileNum1 = gp.tileManager.mapTileNum[nextRightCol][entityTopRow];
                tileNum2 = gp.tileManager.mapTileNum[nextRightCol][entityBottomRow];
                if (gp.tileManager.tile[tileNum1].collision || gp.tileManager.tile[tileNum2].collision) {
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

            // Atur posisi solidArea entitas ke posisi dunia aktualnya untuk pemeriksaan
            entity.solidArea.x = entity.worldX + entity.solidAreaDefaultX;
            entity.solidArea.y = entity.worldY + entity.solidAreaDefaultY;

            // Atur posisi solidArea objek ke posisi dunia aktualnya untuk pemeriksaan
            currentObject.solidArea.x = currentObject.worldX + currentObject.solidAreaDefaultX;
            currentObject.solidArea.y = currentObject.worldY + currentObject.solidAreaDefaultY;

            // Prediksi pergerakan solidArea entitas
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
                if (currentObject.collision) { // Periksa properti collision dari SuperObject
                    entity.collisionON = true; // Entitas tidak bisa bergerak jika objek solid
                }
                if (isPlayer) { // Jika pemain, tandai indeks objek untuk potensi interaksi/pickup
                    index = i;
                }
            }
            // Kembalikan solidArea entitas ke offset default relatif terhadap posisi entitas
            // Ini penting agar posisi solidArea.x dan .y tidak mengakumulasi posisi dunia.
            entity.solidArea.x = entity.solidAreaDefaultX;
            entity.solidArea.y = entity.solidAreaDefaultY;

            // Kembalikan solidArea objek (meskipun tidak diubah di sini, ini praktik yang baik)
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
            if (targetNPC == null || targetNPC == entity) { // Jangan cek tabrakan dengan diri sendiri atau NPC null
                continue;
            }

            // Atur posisi solidArea entitas utama ke posisi dunia aktualnya
            entity.solidArea.x = entity.worldX + entity.solidAreaDefaultX;
            entity.solidArea.y = entity.worldY + entity.solidAreaDefaultY;

            // Atur posisi solidArea NPC target ke posisi dunia aktualnya
            targetNPC.solidArea.x = targetNPC.worldX + targetNPC.solidAreaDefaultX;
            targetNPC.solidArea.y = targetNPC.worldY + targetNPC.solidAreaDefaultY;

            // Prediksi pergerakan solidArea entitas utama
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
                entity.collisionON = true; // Entitas utama tidak bisa bergerak
                index = i; // Kembalikan indeks NPC yang ditabrak (berguna untuk interaksi)
            }

            // Kembalikan solidArea entitas utama ke offset defaultnya
            entity.solidArea.x = entity.solidAreaDefaultX;
            entity.solidArea.y = entity.solidAreaDefaultY;

            // Kembalikan solidArea NPC target (meskipun tidak diubah di sini)
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
        // Atur posisi solidArea NPC ke posisi dunia aktualnya
        npcEntity.solidArea.x = npcEntity.worldX + npcEntity.solidAreaDefaultX;
        npcEntity.solidArea.y = npcEntity.worldY + npcEntity.solidAreaDefaultY;

        // Atur posisi solidArea Pemain ke posisi dunia aktualnya
        gp.player.solidArea.x = gp.player.worldX + gp.player.solidAreaDefaultX;
        gp.player.solidArea.y = gp.player.worldY + gp.player.solidAreaDefaultY;

        // Prediksi pergerakan solidArea NPC
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
            npcEntity.collisionON = true; // NPC tidak bisa bergerak jika akan menabrak pemain
        }

        // Kembalikan solidArea NPC ke offset defaultnya
        npcEntity.solidArea.x = npcEntity.solidAreaDefaultX;
        npcEntity.solidArea.y = npcEntity.solidAreaDefaultY;

        // Kembalikan solidArea Pemain (meskipun tidak diubah di sini)
        gp.player.solidArea.x = gp.player.solidAreaDefaultX;
        gp.player.solidArea.y = gp.player.solidAreaDefaultY;
    }
}