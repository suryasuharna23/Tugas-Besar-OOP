package spakborhills;

import spakborhills.entity.*;
import spakborhills.enums.FishType;
import spakborhills.enums.ItemType;
import spakborhills.enums.Location;
import spakborhills.enums.Season;
import spakborhills.enums.Weather;
import spakborhills.object.*;

import java.util.Arrays;

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

        // Contoh menempatkan peti
        Entity chest1 = new OBJ_Chest(gp);
        chest1.worldX = gp.tileSize * 22;
        chest1.worldY = gp.tileSize * 30;
        gp.entities.add(chest1);

// COMMON FISH
        Entity bullhead = new OBJ_Fish(gp, ItemType.FISH, "Bullhead", true, 300, 200,
                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList(Location.MOUNTAIN_LAKE),
                FishType.COMMON,
                0, 24 // Jam Any
        );

        Entity carp = new OBJ_Fish(gp, ItemType.FISH, "Carp", true, 300, 200,
                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList(Location.MOUNTAIN_LAKE, Location.POND),
                FishType.COMMON,
                0, 24
        );

        Entity chub = new OBJ_Fish(gp, ItemType.FISH, "Chub", true, 300, 200,
                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList(Location.FOREST_RIVER, Location.MOUNTAIN_LAKE),
                FishType.COMMON,
                0, 24
        );

// REGULAR FISH
        Entity largemouthBass = new OBJ_Fish(gp, ItemType.FISH, "Largemouth Bass", true, 500, 300,
                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList(Location.MOUNTAIN_LAKE),
                FishType.REGULAR,
                6, 18 // 06-18
        );

        Entity rainbowTrout = new OBJ_Fish(gp, ItemType.FISH, "Rainbow Trout", true, 500, 300,
                Arrays.asList(Season.SUMMER),
                Arrays.asList(Weather.SUNNY),
                Arrays.asList(Location.FOREST_RIVER, Location.MOUNTAIN_LAKE),
                FishType.REGULAR,
                6, 18 // 06-18
        );

        Entity sturgeon = new OBJ_Fish(gp, ItemType.FISH, "Sturgeon", true, 500, 300,
                Arrays.asList(Season.SUMMER, Season.WINTER),
                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList(Location.MOUNTAIN_LAKE),
                FishType.REGULAR,
                6, 18 // 06-18
        );

        Entity midnightCarp = new OBJ_Fish(gp, ItemType.FISH, "Midnight Carp", true, 500, 300,
                Arrays.asList(Season.WINTER, Season.FALL),
                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList(Location.MOUNTAIN_LAKE, Location.POND),
                FishType.REGULAR,
                20, 2 // 20-02 (melewati midnight)
        );

        Entity flounder = new OBJ_Fish(gp, ItemType.FISH, "Flounder", true, 500, 300,
                Arrays.asList(Season.SPRING, Season.SUMMER),
                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList(Location.OCEAN),
                FishType.REGULAR,
                6, 22 // 06-22
        );

        // Halibut punya dua jam aktif: 06-11 dan 19-02, jadi tambahkan dua entri jika perlu
        Entity halibutMorning = new OBJ_Fish(gp, ItemType.FISH, "Halibut", true, 500, 300,
                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList(Location.OCEAN),
                FishType.REGULAR,
                6, 11 // 06-11
        );

        Entity halibutNight = new OBJ_Fish(gp, ItemType.FISH, "Halibut", true, 500, 300,
                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList(Location.OCEAN),
                FishType.REGULAR,
                19, 2 // 19-02 (melewati midnight)
        );

        Entity octopus = new OBJ_Fish(gp, ItemType.FISH, "Octopus", true, 500, 300,
                Arrays.asList(Season.SUMMER),
                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList(Location.OCEAN),
                FishType.REGULAR,
                6, 22 // 06-22
        );

        Entity pufferfish = new OBJ_Fish(gp, ItemType.FISH, "Pufferfish", true, 500, 300,
                Arrays.asList(Season.SUMMER),
                Arrays.asList(Weather.SUNNY),
                Arrays.asList(Location.OCEAN),
                FishType.REGULAR,
                0, 16 // 00-16
        );

        Entity sardine = new OBJ_Fish(gp, ItemType.FISH, "Sardine", true, 500, 300,
                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList(Location.OCEAN),
                FishType.REGULAR,
                6, 18 // 06-18
        );

        Entity superCucumber = new OBJ_Fish(gp, ItemType.FISH, "Super Cucumber", true, 500, 300,
                Arrays.asList(Season.SUMMER, Season.FALL, Season.WINTER),
                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList(Location.OCEAN),
                FishType.REGULAR,
                18, 2 // 18-02 (sore ke dini hari)
        );

        Entity catfish = new OBJ_Fish(gp, ItemType.FISH, "Catfish", true, 500, 300,
                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL),
                Arrays.asList(Weather.RAINY),
                Arrays.asList(Location.FOREST_RIVER, Location.POND),
                FishType.REGULAR,
                6, 22 // 06-22
        );

        Entity salmon = new OBJ_Fish(gp, ItemType.FISH, "Salmon", true, 500, 300,
                Arrays.asList(Season.FALL),
                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList(Location.FOREST_RIVER),
                FishType.REGULAR,
                6, 18 // 06-18
        );

        // LEGENDARY FISH
        Entity angler = new OBJ_Fish(gp, ItemType.FISH, "Angler", true, 1000, 800,
                Arrays.asList(Season.FALL),
                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList(Location.POND),
                FishType.LEGENDARY,
                8, 20 // 08-20
        );

        Entity crimsonfish = new OBJ_Fish(gp, ItemType.FISH, "Crimsonfish", true, 1000, 800,
                Arrays.asList(Season.SUMMER),
                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList(Location.OCEAN),
                FishType.LEGENDARY,
                8, 20 // 08-20
        );

        Entity glacierfish = new OBJ_Fish(gp, ItemType.FISH, "Glacierfish", true, 1000, 800,
                Arrays.asList(Season.WINTER),
                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList(Location.FOREST_RIVER),
                FishType.LEGENDARY,
                8, 20 // 08-20
        );

        Entity legend = new OBJ_Fish(gp, ItemType.FISH, "Legend", true, 1000, 800,
                Arrays.asList(Season.SPRING),
                Arrays.asList(Weather.RAINY),
                Arrays.asList(Location.MOUNTAIN_LAKE),
                FishType.LEGENDARY,
                8, 20 // 08-20
        );

        gp.entities.add(bullhead);
        gp.entities.add(carp);
        gp.entities.add(chub);
        gp.entities.add(largemouthBass);
        gp.entities.add(rainbowTrout);
        gp.entities.add(sturgeon);
        gp.entities.add(midnightCarp);
        gp.entities.add(flounder);
        gp.entities.add(halibutMorning);
        gp.entities.add(halibutNight);
        gp.entities.add(octopus);
        gp.entities.add(pufferfish);
        gp.entities.add(sardine);
        gp.entities.add(superCucumber);
        gp.entities.add(catfish);
        gp.entities.add(salmon);
        gp.entities.add(angler);
        gp.entities.add(crimsonfish);
        gp.entities.add(glacierfish);
        gp.entities.add(legend);
            Entity shippingBin = new OBJ_ShippingBin(gp);
            shippingBin.worldX = gp.tileSize * 25;
            shippingBin.worldY = gp.tileSize * 15;
            gp.entities.add(shippingBin);
            
            chest1.worldX = gp.tileSize * 22;
            chest1.worldY = gp.tileSize * 30;
            gp.entities.add(chest1);
        } else if ("Player's House".equalsIgnoreCase(currentMapName)) {
            Entity playerSingleBed = new OBJ_Bed(gp); // Beri tahu tipe ranjangnya
            playerSingleBed.worldX = gp.tileSize * 5; // Sesuaikan X (kolom ke-8 dari kiri jika tile 0)
            playerSingleBed.worldY = gp.tileSize * 5; // Sesuaikan Y (baris ke-4 dari atas jika tile 0)
            gp.entities.add(playerSingleBed);
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
        }else if ("Store".equalsIgnoreCase(currentMapName)) {
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