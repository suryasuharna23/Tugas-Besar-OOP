package spakborhills;

import spakborhills.entity.*;
import spakborhills.object.*;

public class AssetSetter {
    GamePanel gp;

    public AssetSetter(GamePanel gp){
        this.gp = gp;
    }
    public void setObject(){
        Entity door1 = new OBJ_Door(gp); // Buat instance dan simpan di variabel lokal
        door1.worldX = gp.tileSize * 25;    // Atur koordinat X
        door1.worldY = gp.tileSize * 15;    // Atur koordinat Y
        gp.entities.add(door1);                  // Tambahkan objek yang sudah dikonfigurasi ke daftar gp.obj

        // Contoh menempatkan sebuah kunci
        Entity key1 = new OBJ_Key(gp);
        key1.worldX = gp.tileSize * 23;
        key1.worldY = gp.tileSize * 40;
        gp.entities.add(key1);

        // Contoh menempatkan peti
        Entity chest1 = new OBJ_Chest(gp);
        chest1.worldX = gp.tileSize * 22;
        chest1.worldY = gp.tileSize * 30;
        gp.entities.add(chest1);

    }

    public void setNPC(){
        // Buat instance NPC dan LANGSUNG atur propertinya sebelum menambahkannya ke daftar
        Entity npcGirl = new NPC_GIRL(gp);
        npcGirl.worldX = gp.tileSize * 21;   // Atur X untuk npcGirl
        npcGirl.worldY = gp.tileSize * 21;   // Atur Y untuk npcGirl
        // Pastikan npcGirl.type = EntityType.NPC; diatur di dalam konstruktor NPC_GIRL
        gp.entities.add(npcGirl);            // Baru tambahkan npcGirl yang sudah diatur ke daftar

        Entity npcOldman = new NPC_OLDMAN(gp);
        npcOldman.worldX = gp.tileSize * 20; // Atur X untuk npcOldman
        npcOldman.worldY = gp.tileSize * 21; // Atur Y untuk npcOldman
        // Pastikan npcOldman.type = EntityType.NPC; diatur di dalam konstruktor NPC_OLDMAN
        gp.entities.add(npcOldman);          // Baru tambahkan npcOldman yang sudah diatur ke daftar
    }
}