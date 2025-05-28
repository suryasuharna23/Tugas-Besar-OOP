package spakborhills;

import spakborhills.entity.*;
import spakborhills.enums.FishType;
import spakborhills.enums.ItemType;
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
                gp.entities.removeIf(e -> !(e instanceof Player) && !(e instanceof NPC)); 

                
                Entity chest1 = new OBJ_Chest(gp);
                chest1.worldX = gp.tileSize * 22;
                chest1.worldY = gp.tileSize * 30;
                gp.entities.add(chest1);

                
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
                                Arrays.asList(Season.SUMMER, Season.WINTER),
                                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                                Arrays.asList("Mountain Lake"), FishType.REGULAR, 6, 18);
                gp.entities.add(sturgeon);

                Entity midnightCarp = new OBJ_Fish(gp, ItemType.FISH, "Midnight Carp", true, 500, 300,
                                Arrays.asList(Season.WINTER, Season.FALL), Arrays.asList(Weather.RAINY, Weather.SUNNY),
                                Arrays.asList("Mountain Lake", "Pond"), FishType.REGULAR, 20, 2);
                gp.entities.add(midnightCarp);

                Entity flounder = new OBJ_Fish(gp, ItemType.FISH, "Flounder", true, 500, 300,
                                Arrays.asList(Season.SPRING, Season.SUMMER),
                                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                                Arrays.asList("Ocean"), FishType.REGULAR, 6, 22);
                gp.entities.add(flounder);

                Entity halibutMorning = new OBJ_Fish(gp, ItemType.FISH, "Halibut", true, 500, 300,
                                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
                                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                                Arrays.asList("Ocean"), FishType.REGULAR, 6, 11);
                gp.entities.add(halibutMorning);

                Entity halibutNight = new OBJ_Fish(gp, ItemType.FISH, "Halibut", true, 500, 300,
                                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
                                Arrays.asList(Weather.RAINY, Weather.SUNNY),
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
                                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
                                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                                Arrays.asList("Ocean"), FishType.REGULAR, 6, 18);
                gp.entities.add(sardine);

                Entity superCucumber = new OBJ_Fish(gp, ItemType.FISH, "Super Cucumber", true, 500, 300,
                                Arrays.asList(Season.SUMMER, Season.FALL, Season.WINTER),
                                Arrays.asList(Weather.RAINY, Weather.SUNNY),
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
                

                System.out.println(
                                "[AssetSetter.setObject()] INFO: Selesai menambahkan semua entitas ikan global. Jumlah entitas (termasuk ikan) sekarang: "
                                                + gp.entities.size());

                
                if ("Farm".equalsIgnoreCase(currentMapName)) {
                        Entity key1 = new OBJ_Key(gp);
                        key1.worldX = gp.tileSize * 23;
                        key1.worldY = gp.tileSize * 40;
                        gp.entities.add(key1);

                        Entity chestOnFarm = new OBJ_Chest(gp); 
                        chestOnFarm.worldX = gp.tileSize * 22;
                        chestOnFarm.worldY = gp.tileSize * 30;
                        gp.entities.add(chestOnFarm);

                        Entity shippingBin = new OBJ_ShippingBin(gp);
                        shippingBin.worldX = gp.tileSize * 25;
                        shippingBin.worldY = gp.tileSize * 15;
                        gp.entities.add(shippingBin);

                        chest1.worldX = gp.tileSize * 22;
                        chest1.worldY = gp.tileSize * 30;
                        gp.entities.add(chest1);
                } else if ("Player's House".equalsIgnoreCase(currentMapName)) {
                        Entity playerSingleBed = new OBJ_Bed(gp);
                        playerSingleBed.worldX = gp.tileSize * 5;
                        playerSingleBed.worldY = gp.tileSize * 5;
                        gp.entities.add(playerSingleBed);

                        Entity stove = new OBJ_Stove(gp);
                        stove.worldX = gp.tileSize * 15;
                        stove.worldY = gp.tileSize * 20;
                        gp.entities.add(stove);

                        Entity tv = new OBJ_TV(gp);
                        tv.worldX = gp.tileSize * 5;
                        tv.worldY = gp.tileSize * 11;
                        gp.entities.add(tv);
                }
                
        }

        
        public void setNPC(String currentMapName) {
                
                if ("Abigail's House".equalsIgnoreCase(currentMapName)) {
                        NPC abigail = new NPC_ABIGAIL(gp);
                        abigail.worldX = gp.tileSize * 10; 
                        abigail.worldY = gp.tileSize * 12; 
                        gp.npcs.add(abigail);
                        gp.entities.add(abigail);

                } else if ("Caroline's House".equalsIgnoreCase(currentMapName)) {
                        NPC caroline = new NPC_CAROLINE(gp);
                        caroline.worldX = gp.tileSize * 8; 
                        caroline.worldY = gp.tileSize * 10;
                        gp.npcs.add(caroline);
                        gp.entities.add(caroline);
                } else if ("Store".equalsIgnoreCase(currentMapName)) {
                        NPC emily = new NPC_EMILY(gp);
                        emily.worldX = gp.tileSize * 7;
                        emily.worldY = gp.tileSize * 9;
                        gp.npcs.add(emily);
                        gp.entities.add(emily);
                } else if ("Perry's House".equalsIgnoreCase(currentMapName)) {
                        NPC perry = new NPC_PERRY(gp);
                        perry.worldX = gp.tileSize * 23;
                        perry.worldY = gp.tileSize * 12;
                        gp.npcs.add(perry);
                        gp.entities.add(perry);
                } else if ("Mayor_Tadi_House".equalsIgnoreCase(currentMapName)) {
                        NPC mayorTadi = new NPC_ABIGAIL(gp);
                        mayorTadi.worldX = gp.tileSize * 23;
                        mayorTadi.worldY = gp.tileSize * 12;
                        gp.npcs.add(mayorTadi);
                        gp.entities.add(mayorTadi);
                }
        }
}