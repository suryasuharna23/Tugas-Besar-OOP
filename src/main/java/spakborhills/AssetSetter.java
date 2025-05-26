package spakborhills;

import spakborhills.entity.*;
import spakborhills.enums.FishType;
import spakborhills.enums.ItemType;
// Hapus impor Location jika tidak digunakan langsung di sini, atau pastikan path-nya benar
// import spakborhills.enums.Location;
import spakborhills.enums.Season;
import spakborhills.enums.Weather;
import spakborhills.object.*;

import java.util.Arrays;

public class AssetSetter {
    GamePanel gp;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    public void setObject(String currentMapName) {
        gp.entities.removeIf(e -> !(e instanceof Player) && !(e instanceof NPC)); // Hapus objek lama

        System.out.println("[AssetSetter.setObject()] INFO: Memuat aset untuk peta: " + currentMapName);

        // --- BAGIAN PENAMBAHAN IKAN (SEKARANG DI LUAR KONDISI PETA FARM) ---
        // Ini membuat ikan menjadi entitas yang 'selalu ada' di game,
        // ketersediaannya untuk ditangkap kemudian difilter oleh Player.startFishing()

        // COMMON FISH
        Entity bullhead = new OBJ_Fish(gp, ItemType.FISH, "Bullhead", true, 300, 200,
                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList("Mountain Lake"),
                FishType.COMMON, 0, 24);
        gp.entities.add(bullhead);

        Entity carp = new OBJ_Fish(gp, ItemType.FISH, "Carp", true, 300, 200,
                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList("Mountain Lake", "Pond"),
                FishType.COMMON, 0, 24);
        gp.entities.add(carp);

        Entity chub = new OBJ_Fish(gp, ItemType.FISH, "Chub", true, 300, 200,
                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList("Forest River", "Mountain Lake"),
                FishType.COMMON, 0, 24);
        gp.entities.add(chub);

        // REGULAR FISH
        Entity largemouthBass = new OBJ_Fish(gp, ItemType.FISH, "Largemouth Bass", true, 500, 300,
                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList("Mountain Lake"), FishType.REGULAR, 6, 18);
        gp.entities.add(largemouthBass);

        Entity rainbowTrout = new OBJ_Fish(gp, ItemType.FISH, "Rainbow Trout", true, 500, 300,
                Arrays.asList(Season.SUMMER), Arrays.asList(Weather.SUNNY),
                Arrays.asList("Forest River", "Mountain Lake"), FishType.REGULAR, 6, 18);
        gp.entities.add(rainbowTrout);

        Entity sturgeon = new OBJ_Fish(gp, ItemType.FISH, "Sturgeon", true, 500, 300,
                Arrays.asList(Season.SUMMER, Season.WINTER), Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList("Mountain Lake"), FishType.REGULAR, 6, 18);
        gp.entities.add(sturgeon);

        Entity midnightCarp = new OBJ_Fish(gp, ItemType.FISH, "Midnight Carp", true, 500, 300,
                Arrays.asList(Season.WINTER, Season.FALL), Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList("Mountain Lake", "Pond"), FishType.REGULAR, 20, 2);
        gp.entities.add(midnightCarp);

        Entity flounder = new OBJ_Fish(gp, ItemType.FISH, "Flounder", true, 500, 300,
                Arrays.asList(Season.SPRING, Season.SUMMER), Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList("Ocean"), FishType.REGULAR, 6, 22);
        gp.entities.add(flounder);

        Entity halibutMorning = new OBJ_Fish(gp, ItemType.FISH, "Halibut", true, 500, 300,
                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER), Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList("Ocean"), FishType.REGULAR, 6, 11);
        gp.entities.add(halibutMorning);

        Entity halibutNight = new OBJ_Fish(gp, ItemType.FISH, "Halibut", true, 500, 300,
                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER), Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList("Ocean"), FishType.REGULAR, 19, 2);
        gp.entities.add(halibutNight);

        Entity octopus = new OBJ_Fish(gp, ItemType.FISH, "Octopus", true, 500, 300,
                Arrays.asList(Season.SUMMER), Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList("Ocean"), FishType.REGULAR, 6, 22);
        gp.entities.add(octopus);

        Entity pufferfish = new OBJ_Fish(gp, ItemType.FISH, "Pufferfish", true, 500, 300,
                Arrays.asList(Season.SUMMER), Arrays.asList(Weather.SUNNY),
                Arrays.asList("Ocean"), FishType.REGULAR, 0, 16);
        gp.entities.add(pufferfish);

        Entity sardine = new OBJ_Fish(gp, ItemType.FISH, "Sardine", true, 500, 300,
                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER), Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList("Ocean"), FishType.REGULAR, 6, 18);
        gp.entities.add(sardine);

        Entity superCucumber = new OBJ_Fish(gp, ItemType.FISH, "Super Cucumber", true, 500, 300,
                Arrays.asList(Season.SUMMER, Season.FALL, Season.WINTER), Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList("Ocean"), FishType.REGULAR, 18, 2);
        gp.entities.add(superCucumber);

        Entity catfish = new OBJ_Fish(gp, ItemType.FISH, "Catfish", true, 500, 300,
                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL), Arrays.asList(Weather.RAINY),
                Arrays.asList("Forest River", "Pond"), FishType.REGULAR, 6, 22);
        gp.entities.add(catfish);

        Entity salmon = new OBJ_Fish(gp, ItemType.FISH, "Salmon", true, 500, 300,
                Arrays.asList(Season.FALL), Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList("Forest River"), FishType.REGULAR, 6, 18);
        gp.entities.add(salmon);

        // LEGENDARY FISH
        Entity angler = new OBJ_Fish(gp, ItemType.FISH, "Angler", true, 1000, 800,
                Arrays.asList(Season.FALL), Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList("Pond"), FishType.LEGENDARY, 8, 20);
        gp.entities.add(angler);

        Entity crimsonfish = new OBJ_Fish(gp, ItemType.FISH, "Crimsonfish", true, 1000, 800,
                Arrays.asList(Season.SUMMER), Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList("Ocean"), FishType.LEGENDARY, 8, 20);
        gp.entities.add(crimsonfish);

        Entity glacierfish = new OBJ_Fish(gp, ItemType.FISH, "Glacierfish", true, 1000, 800,
                Arrays.asList(Season.WINTER), Arrays.asList(Weather.RAINY, Weather.SUNNY),
                Arrays.asList("Forest River"), FishType.LEGENDARY, 8, 20);
        gp.entities.add(glacierfish);

        Entity legend = new OBJ_Fish(gp, ItemType.FISH, "Legend", true, 1000, 800,
                Arrays.asList(Season.SPRING), Arrays.asList(Weather.RAINY),
                Arrays.asList("Mountain Lake"), FishType.LEGENDARY, 8, 20);
        gp.entities.add(legend);
        // --- AKHIR BAGIAN PENAMBAHAN IKAN ---

        System.out.println("[AssetSetter.setObject()] INFO: Selesai menambahkan semua entitas ikan global. Jumlah entitas (termasuk ikan) sekarang: " + gp.entities.size());

        // Sekarang, tambahkan objek-objek yang spesifik untuk peta tertentu
        if ("Farm".equalsIgnoreCase(currentMapName)) {
            Entity key1 = new OBJ_Key(gp);
            key1.worldX = gp.tileSize * 23;
            key1.worldY = gp.tileSize * 40;
            gp.entities.add(key1);

            Entity chestOnFarm = new OBJ_Chest(gp); // Beri nama unik jika ada chest lain
            chestOnFarm.worldX = gp.tileSize * 22;
            chestOnFarm.worldY = gp.tileSize * 30;
            gp.entities.add(chestOnFarm);

            Entity shippingBin = new OBJ_ShippingBin(gp);
            shippingBin.worldX = gp.tileSize * 25;
            shippingBin.worldY = gp.tileSize * 15;
            gp.entities.add(shippingBin);

            // Baris gp.entities.add(chest1) yang kedua di kode asli Anda akan menambahkan objek yang sama dua kali.
            // Jika Anda membutuhkan dua chest berbeda di Farm, buat instance baru:
            // Entity anotherChestOnFarm = new OBJ_Chest(gp);
            // anotherChestOnFarm.worldX = ...;
            // gp.entities.add(anotherChestOnFarm);
        } else if ("Player's House".equalsIgnoreCase(currentMapName)) {
            Entity playerSingleBed = new OBJ_Bed(gp);
            playerSingleBed.worldX = gp.tileSize * 5;
            playerSingleBed.worldY = gp.tileSize * 5;
            gp.entities.add(playerSingleBed);
        }
        // Tambahkan logika untuk peta lain jika perlu
        System.out.println("[AssetSetter.setObject()] INFO: Selesai memuat aset spesifik peta. Jumlah total entitas akhir: " + gp.entities.size());
    }

    // Metode setNPC tetap sama
    public void setNPC(String currentMapName){
        // ... (kode setNPC Anda)
    }
}