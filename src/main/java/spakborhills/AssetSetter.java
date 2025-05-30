package spakborhills;

import java.util.Arrays;

import spakborhills.entity.Entity;
import spakborhills.entity.NPC;
import spakborhills.entity.NPC_ABIGAIL;
import spakborhills.entity.NPC_CAROLINE;
import spakborhills.entity.NPC_DASCO;
import spakborhills.entity.NPC_EMILY;
import spakborhills.entity.NPC_MAYOR_TADI;
import spakborhills.entity.NPC_PERRY;
import spakborhills.entity.Player;
import spakborhills.enums.FishType;
import spakborhills.enums.ItemType;
import spakborhills.enums.Season;
import spakborhills.enums.Weather;
import spakborhills.object.*;

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
                                Arrays.asList("MOUNTAIN LAKE"),
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
                                Arrays.asList("FOREST RIVER", "MOUNTAIN LAKE"),
                                FishType.COMMON, 0, 24);
                gp.entities.add(chub);

                
                Entity largemouthBass = new OBJ_Fish(gp, ItemType.FISH, "Largemouth Bass", true, 500, 300,
                                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
                                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                                Arrays.asList("MOUNTAIN LAKE"), FishType.REGULAR, 6, 18);
                gp.entities.add(largemouthBass);

                Entity rainbowTrout = new OBJ_Fish(gp, ItemType.FISH, "Rainbow Trout", true, 500, 300,
                                Arrays.asList(Season.SUMMER), Arrays.asList(Weather.SUNNY),
                                Arrays.asList("FOREST RIVER", "MOUNTAIN LAKE"), FishType.REGULAR, 6, 18);
                gp.entities.add(rainbowTrout);

                Entity sturgeon = new OBJ_Fish(gp, ItemType.FISH, "Sturgeon", true, 500, 300,
                                Arrays.asList(Season.SUMMER, Season.WINTER),
                                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                                Arrays.asList("MOUNTAIN LAKE"), FishType.REGULAR, 6, 18);
                gp.entities.add(sturgeon);

                Entity midnightCarp = new OBJ_Fish(gp, ItemType.FISH, "Midnight Carp", true, 500, 300,
                                Arrays.asList(Season.WINTER, Season.FALL), Arrays.asList(Weather.RAINY, Weather.SUNNY),
                                Arrays.asList("MOUNTAIN LAKE", "POND"), FishType.REGULAR, 20, 2);
                gp.entities.add(midnightCarp);

                Entity flounder = new OBJ_Fish(gp, ItemType.FISH, "Flounder", true, 500, 300,
                                Arrays.asList(Season.SPRING, Season.SUMMER),
                                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                                Arrays.asList("OCEAN"), FishType.REGULAR, 6, 22);
                gp.entities.add(flounder);

                Entity halibutMorning = new OBJ_Fish(gp, ItemType.FISH, "Halibut", true, 500, 300,
                                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
                                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                                Arrays.asList("OCEAN"), FishType.REGULAR, 6, 11);
                gp.entities.add(halibutMorning);

                Entity halibutNight = new OBJ_Fish(gp, ItemType.FISH, "Halibut", true, 500, 300,
                                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
                                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                                Arrays.asList("OCEAN"), FishType.REGULAR, 19, 2);
                gp.entities.add(halibutNight);

                Entity octopus = new OBJ_Fish(gp, ItemType.FISH, "Octopus", true, 500, 300,
                                Arrays.asList(Season.SUMMER), Arrays.asList(Weather.RAINY, Weather.SUNNY),
                                Arrays.asList("OCEAN"), FishType.REGULAR, 6, 22);
                gp.entities.add(octopus);

                Entity pufferfish = new OBJ_Fish(gp, ItemType.FISH, "Pufferfish", true, 500, 300,
                                Arrays.asList(Season.SUMMER), Arrays.asList(Weather.SUNNY),
                                Arrays.asList("OCEAN"), FishType.REGULAR, 0, 16);
                gp.entities.add(pufferfish);

                Entity sardine = new OBJ_Fish(gp, ItemType.FISH, "Sardine", true, 500, 300,
                                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
                                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                                Arrays.asList("OCEAN"), FishType.REGULAR, 6, 18);
                gp.entities.add(sardine);

                Entity superCucumber = new OBJ_Fish(gp, ItemType.FISH, "Super Cucumber", true, 500, 300,
                                Arrays.asList(Season.SUMMER, Season.FALL, Season.WINTER),
                                Arrays.asList(Weather.RAINY, Weather.SUNNY),
                                Arrays.asList("OCEAN"), FishType.REGULAR, 18, 2);
                gp.entities.add(superCucumber);

                Entity catfish = new OBJ_Fish(gp, ItemType.FISH, "Catfish", true, 500, 300,
                                Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL), Arrays.asList(Weather.RAINY),
                                Arrays.asList("FOREST RIVER", "POND"), FishType.REGULAR, 6, 22);
                gp.entities.add(catfish);

                Entity salmon = new OBJ_Fish(gp, ItemType.FISH, "Salmon", true, 500, 300,
                                Arrays.asList(Season.FALL), Arrays.asList(Weather.RAINY, Weather.SUNNY),
                                Arrays.asList("FOREST RIVER"), FishType.REGULAR, 6, 18);
                gp.entities.add(salmon);

                
                Entity angler = new OBJ_Fish(gp, ItemType.FISH, "Angler", true, 1000, 800,
                                Arrays.asList(Season.FALL), Arrays.asList(Weather.RAINY, Weather.SUNNY),
                                Arrays.asList("POND"), FishType.LEGENDARY, 8, 20);
                gp.entities.add(angler);

                Entity crimsonfish = new OBJ_Fish(gp, ItemType.FISH, "Crimsonfish", true, 1000, 800,
                                Arrays.asList(Season.SUMMER), Arrays.asList(Weather.RAINY, Weather.SUNNY),
                                Arrays.asList("OCEAN"), FishType.LEGENDARY, 8, 20);
                gp.entities.add(crimsonfish);

                Entity glacierfish = new OBJ_Fish(gp, ItemType.FISH, "Glacierfish", true, 1000, 800,
                                Arrays.asList(Season.WINTER), Arrays.asList(Weather.RAINY, Weather.SUNNY),
                                Arrays.asList("FOREST RIVER"), FishType.LEGENDARY, 8, 20);
                gp.entities.add(glacierfish);

                Entity legend = new OBJ_Fish(gp, ItemType.FISH, "Legend", true, 1000, 800,
                                Arrays.asList(Season.SPRING), Arrays.asList(Weather.RAINY),
                                Arrays.asList("MOUNTAIN LAKE"), FishType.LEGENDARY, 8, 20);
                gp.entities.add(legend);
                

                System.out.println(
                                "[AssetSetter.setObject()] INFO: Selesai menambahkan semua entitas ikan global. Jumlah entitas (termasuk ikan) sekarang: "
                                                + gp.entities.size());

                
                if ("Farm".equalsIgnoreCase(currentMapName)) {
                        Entity shippingBin = new OBJ_ShippingBin(gp);
                        shippingBin.worldX = gp.tileSize * 25;
                        shippingBin.worldY = gp.tileSize * 15;
                        gp.entities.add(shippingBin);

                        Entity house = new OBJ_House(gp);
                        house.worldX = gp.tileSize * 23;
                        house.worldY = gp.tileSize * 21;
                        gp.entities.add(house);

                        Entity pond = new OBJ_Pond(gp);
                        pond.worldX = gp.tileSize * 15;
                        pond.worldY = gp.tileSize * 21;
                        gp.entities.add(pond);

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

                        Entity exitDoor = new OBJ_Door(gp);
                        exitDoor.worldX = gp.tileSize * 23; // Posisi pintu keluar
                        exitDoor.worldY = gp.tileSize * 33; // Near bottom of house
                        gp.entities.add(exitDoor);

                }
                
        }

        public void setNPC(String currentMapName) {
                gp.npcs.clear();
                gp.entities.removeIf(e -> e instanceof NPC);

                System.out.println("[AssetSetter.setNPC] Setting NPCs for map: " + currentMapName + ". Iterating " + gp.allNpcsInWorld.size() + " NPCs from master list.");

                for (NPC npcPersistentInstance : gp.allNpcsInWorld) { 
                        boolean shouldBeOnThisMap = false;
                        int npcMapX = 0; 
                        int npcMapY = 0;









                        if (npcPersistentInstance.marriedToPlayer == true) {
                                if ("Player's House".equalsIgnoreCase(currentMapName) && gp.player.partner == npcPersistentInstance) {
                                        currentMapName = "Player's House";
                                        shouldBeOnThisMap = true;
                                        npcMapX = gp.tileSize * 12; 
                                        npcMapY = gp.tileSize * 10; 
                                        System.out.println("  -> " + npcPersistentInstance.name + " is married to player. Placing in Player's House.");
                                }
                        }
                        else  {
                                if (npcPersistentInstance.name.equals("Abigail") && "Abigail's House".equalsIgnoreCase(currentMapName)) {
                                        shouldBeOnThisMap = true;
                                        npcMapX = gp.tileSize * 10;
                                        npcMapY = gp.tileSize * 12;

                                } else if (npcPersistentInstance.name.equals("Caroline") && "Caroline's House".equalsIgnoreCase(currentMapName)) {
                                        shouldBeOnThisMap = true;
                                        npcMapX = gp.tileSize * 8;
                                        npcMapY = gp.tileSize * 10;
                                } else if (npcPersistentInstance.name.equals("Emily") && "Store".equalsIgnoreCase(currentMapName)) {
                                        shouldBeOnThisMap = true;
                                        npcMapX = gp.tileSize * 7;
                                        npcMapY = gp.tileSize * 9;
                                } else if (npcPersistentInstance.name.equals("Perry") && "Perry's House".equalsIgnoreCase(currentMapName)) {
                                        shouldBeOnThisMap = true;
                                        npcMapX = gp.tileSize * 23;
                                        npcMapY = gp.tileSize * 12;
                                } else if (npcPersistentInstance.name.equals("Mayor Tadi") && "Mayor Tadi's House".equalsIgnoreCase(currentMapName)) {
                                        shouldBeOnThisMap = true;
                                        npcMapX = gp.tileSize * 15;
                                        npcMapY = gp.tileSize * 15;
                                } else if (npcPersistentInstance.name.equals("Dasco") && "Dasco's House".equalsIgnoreCase(currentMapName)) {
                                        shouldBeOnThisMap = true;
                                        npcMapX = gp.tileSize * 15;
                                        npcMapY = gp.tileSize * 15;
                                }
                        }

                        if (shouldBeOnThisMap) {
                                npcPersistentInstance.worldX = npcMapX; 
                                npcPersistentInstance.worldY = npcMapY;
                                gp.npcs.add(npcPersistentInstance); 
                                gp.entities.add(npcPersistentInstance); 
                                                                        
                                System.out.println("  -> Placing " + npcPersistentInstance.name + " on current map '"
                                                + currentMapName + "' at X:" + npcMapX + ", Y:" + npcMapY
                                                + ". Current hearts: " + npcPersistentInstance.currentHeartPoints);
                        }
                }
                System.out.println("[AssetSetter.setNPC] NPCs placed on map '" + currentMapName + "': " + gp.npcs.size()
                                + ". Total entities after NPC placement: " + gp.entities.size());
        }

        public void initializeAllNPCs() {
                System.out.println("[AssetSetter] Initializing all persistent NPCs in the world...");
                gp.allNpcsInWorld.clear(); 

                NPC_ABIGAIL abigail = new NPC_ABIGAIL(gp);
                
                gp.allNpcsInWorld.add(abigail);

                NPC_CAROLINE caroline = new NPC_CAROLINE(gp);
                gp.allNpcsInWorld.add(caroline);

                NPC_DASCO dasco = new NPC_DASCO(gp);
                gp.allNpcsInWorld.add(dasco);

                NPC_EMILY emily = new NPC_EMILY(gp);
                gp.allNpcsInWorld.add(emily);

                NPC_MAYOR_TADI mayorTadi = new NPC_MAYOR_TADI(gp);
                gp.allNpcsInWorld.add(mayorTadi);

                NPC_PERRY perry = new NPC_PERRY(gp);
                gp.allNpcsInWorld.add(perry);
                

                System.out.println("[AssetSetter] Total persistent NPCs initialized: " + gp.allNpcsInWorld.size());
                for (NPC npc : gp.allNpcsInWorld) {
                        System.out.println("  - NPC Master Loaded: " + npc.name + " (Initial Hearts: " + npc.currentHeartPoints + ")");
                }
        }
}