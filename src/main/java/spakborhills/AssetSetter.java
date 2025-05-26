package spakborhills;

import spakborhills.entity.*;
import spakborhills.object.*;

public class AssetSetter {
    GamePanel gp;

    public AssetSetter(GamePanel gp){
        this.gp = gp;
    }

    public void setObject(String currentMapName){ // Anda mungkin juga ingin membuat setObject bergantung pada peta
        gp.entities.removeIf(e -> !(e instanceof Player) && !(e instanceof NPC)); // Hapus objek lama, sisakan player & NPC

        // Contoh: Hanya tempatkan item-item ini jika di peta "Farm"
        if ("Farm".equalsIgnoreCase(currentMapName)) {
            Entity key1 = new OBJ_Key(gp);
            key1.worldX = gp.tileSize * 23;
            key1.worldY = gp.tileSize * 40;
            gp.entities.add(key1);

            Entity shippingBin = new OBJ_ShippingBin(gp);
            shippingBin.worldX = gp.tileSize * 25;
            shippingBin.worldY = gp.tileSize * 15;
            gp.entities.add(shippingBin);

            Entity chest1 = new OBJ_Chest(gp);
            chest1.worldX = gp.tileSize * 22;
            chest1.worldY = gp.tileSize * 30;
            gp.entities.add(chest1);
        } else if ("Player's House".equalsIgnoreCase(currentMapName)) {
            // Tambahkan objek spesifik untuk rumah pemain jika ada
        }
        // Tambahkan logika untuk peta lain jika perlu
    }

    // Ubah metode setNPC untuk menerima nama peta saat ini
    public void setNPC(String currentMapName){
        // Logika penempatan NPC berdasarkan nama peta
        if ("Abigail's House".equalsIgnoreCase(currentMapName)) {
            NPC abigail = new NPC_ABIGAIL(gp);
            abigail.worldX = gp.tileSize * 10; // Contoh koordinat X untuk Abigail di rumahnya
            abigail.worldY = gp.tileSize * 12; // Contoh koordinat Y untuk Abigail di rumahnya
            gp.npcs.add(abigail);
            gp.entities.add(abigail);

        } else if ("Caroline's House".equalsIgnoreCase(currentMapName)) {
            NPC caroline = new NPC_CAROLINE(gp);
            caroline.worldX = gp.tileSize * 8; // Contoh koordinat untuk Caroline
            caroline.worldY = gp.tileSize * 10;
            gp.npcs.add(caroline);
            gp.entities.add(caroline);
        } else if ("Farm".equalsIgnoreCase(currentMapName)) {
            // Contoh NPC di Peta Farm
            NPC perry = new NPC_PERRY(gp);
            perry.worldX = gp.tileSize * 30;
            perry.worldY = gp.tileSize * 15;
            gp.npcs.add(perry);
            gp.entities.add(perry);

        } else if ("Store".equalsIgnoreCase(currentMapName)) {
            NPC emily = new NPC_EMILY(gp);
            emily.worldX = gp.tileSize * 7;
            emily.worldY = gp.tileSize * 9;
            gp.npcs.add(emily);
            gp.entities.add(emily);
        } else if ("Perry's House".equalsIgnoreCase(currentMapName)){
            NPC perry = new NPC_PERRY(gp);
            perry.worldX = gp.tileSize * 23;
            perry.worldY = gp.tileSize * 12;
            gp.npcs.add(perry);
            gp.entities.add(perry);
        } else if ("Mayor_Tadi_House".equalsIgnoreCase(currentMapName)){
            NPC mayorTadi = new NPC_ABIGAIL(gp);
            mayorTadi.worldX = gp.tileSize * 23;
            mayorTadi.worldY = gp.tileSize * 12;
            gp.npcs.add(mayorTadi);
            gp.entities.add(mayorTadi);
        }
    }
}