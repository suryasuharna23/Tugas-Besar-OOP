
package spakborhills.entity;

import spakborhills.GamePanel;
import spakborhills.enums.EntityType;
import spakborhills.enums.FishType;
import spakborhills.enums.ItemType;
import spakborhills.enums.Season;
import spakborhills.enums.Weather;
import spakborhills.object.OBJ_Equipment;
import spakborhills.object.OBJ_Fish;
import spakborhills.object.OBJ_Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NPC_PERRY extends NPC {
    public NPC_PERRY(GamePanel gp) {
        super(gp);
        direction = "down";
        speed = 1;
        name = "Perry";
        type = EntityType.NPC;
        currentHeartPoints = 50;

        Collections.addAll(lovedGiftsName, "Cranberry", "Blueberry");
        Collections.addAll(likedGiftsName, "Wine");

        try {
            addAllFishToHatedItems();
        } catch (Exception e) {
            System.err.println("[NPC_PERRY] Error during fish detection: " + e.getMessage());

            addFallbackFishToHated();
        }

        setDialogue();
        getNPCImage();
    }

    public void getNPCImage() {
        up1 = setup("/npc/Perry/Perry_W1");
        up2 = setup("/npc/Perry/Perry_W2");
        down1 = setup("/npc/Perry/Perry_S1");
        down2 = setup("/npc/Perry/Perry_S2");
        left1 = setup("/npc/Perry/Perry_A1");
        left2 = setup("/npc/Perry/Perry_A2");
        right1 = setup("/npc/Perry/Perry_D1");
        right2 = setup("/npc/Perry/Perry_D2");
    }

    public void setDialogue() {
        dialogues.add("Saya adalah platypus terhebat.");
        dialogues.add("Ikan? Ugh! Saya benci semua ikan!");
        dialogues.add("Berry dan wine itu yang terbaik, bukan ikan busuk!");
    }

    private List<String> createAndScanAllFish() {
        List<String> fishNames = new ArrayList<>();
        System.out.println("[NPC_PERRY] Creating fish instances for detection...");

        String[][] fishCategories = {

                { "Bullhead", "Carp", "Chub" },

                { "Catfish", "Flounder", "Halibut", "Largemouth Bass", "Midnight Carp",
                        "Octopus", "Pufferfish", "Rainbow Trout", "Salmon", "Sardine",
                        "Sturgeon", "Super Cucumber" },

                { "Angler", "Crimsonfish", "Glacierfish", "Legend" }

        };

        for (String[] category : fishCategories) {
            for (String fishName : category) {
                try {
                    List<Season> seasons = Arrays.asList(Season.SPRING);
                    List<Weather> weathers = Arrays.asList(Weather.SUNNY);
                    List<String> locations = Arrays.asList("Ocean");

                    FishType fishType = Arrays.asList("Angler", "Crimsonfish", "Glacierfish", "Legend")
                            .contains(fishName) ? FishType.LEGENDARY : FishType.REGULAR;

                    OBJ_Fish fish = new OBJ_Fish(gp, ItemType.FISH, fishName, true, 100,
                            seasons, weathers, locations, fishType, 6, 18);

                    String cleanName = fish.name;
                    if (cleanName != null && !cleanName.isEmpty() && !fishNames.contains(cleanName)) {
                        fishNames.add(cleanName);
                        System.out.println("[NPC_PERRY] Detected fish: " + cleanName);
                    }

                } catch (Exception e) {
                    System.err.println("[NPC_PERRY] Failed to create fish: " + fishName + " - " + e.getMessage());

                    if (!fishNames.contains(fishName)) {
                        fishNames.add(fishName);
                        System.out.println("[NPC_PERRY] Added fallback fish: " + fishName);
                    }
                }
            }
        }

        System.out.println("[NPC_PERRY] Created and scanned " + fishNames.size() + " fish instances");
        return fishNames;
    }

    private void addAllFishToHatedItems() {
        System.out.println("[NPC_PERRY] Starting fish detection for hate list...");

        List<String> allFishNames = detectAllFishItems();

        for (String fishName : allFishNames) {
            if (!lovedGiftsName.contains(fishName) && !likedGiftsName.contains(fishName)) {
                hatedItems.add(fishName);
            }
        }

        System.out.println("[NPC_PERRY] Added " + allFishNames.size() + " fish items to hate list");
        if (!allFishNames.isEmpty()) {
            System.out.println("[NPC_PERRY] Sample hated fish: " +
                    allFishNames.subList(0, Math.min(5, allFishNames.size())));
        }
    }

    private List<String> detectAllFishItems() {
        List<String> allFish = new ArrayList<>();
        allFish.addAll(createAndScanAllFish());

        return allFish.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private void addFallbackFishToHated() {
        System.out.println("[NPC_PERRY] Using fallback fish list...");

        List<String> fallbackFish = Arrays.asList(

                "Bullhead", "Carp", "Chub",

                "Catfish", "Flounder", "Halibut", "Largemouth Bass", "Midnight Carp",
                "Octopus", "Pufferfish", "Rainbow Trout", "Salmon", "Sardine",
                "Sturgeon", "Super Cucumber",

                "Angler", "Crimsonfish", "Glacierfish", "Legend");

        for (String fishName : fallbackFish) {
            if (!lovedGiftsName.contains(fishName) && !likedGiftsName.contains(fishName)) {
                hatedItems.add(fishName);
            }
        }

        System.out.println("[NPC_PERRY] Added " + fallbackFish.size() + " fallback fish to hate list");
    }

    public void debugFishHateList() {
        System.out.println("\n===== PERRY FISH HATE DEBUG =====");
        System.out.println("Loved items: " + lovedGiftsName);
        System.out.println("Liked items: " + likedGiftsName);
        System.out.println("Total hated items: " + hatedItems.size());
        System.out.println("Hated fish: " + hatedItems);
        System.out.println("==================================\n");
    }

    @Override
    public void receiveGift(Entity itemEntity, Player player) {
        System.out.println("[NPC_PERRY.receiveGift] START for " + this.name + ". Item: "
                + (itemEntity != null ? itemEntity.name : "null"));
        facePlayer();

        if (hasReceivedGiftToday) {
            System.out.println("[NPC_PERRY.receiveGift] Already received gift today.");
            gp.ui.currentDialogue = alreadyGiftedDialogue;
            gp.gameState = gp.dialogueState;
            return;
        }

        if (!(itemEntity instanceof OBJ_Item) || (itemEntity instanceof OBJ_Equipment)) {
            if (itemEntity instanceof OBJ_Equipment) {
                gp.ui.showMessage("Equipment tidak bisa di gift!");
                System.out.println("[NPC_PERRY.receiveGift] Item is an equipment!");
                return;
            }
            gp.ui.currentDialogue = this.name + ": I'm not sure what this is.";
            gp.gameState = gp.dialogueState;
            return;
        }

        OBJ_Item giftedItem = (OBJ_Item) itemEntity;
        String giftedItemBaseName = giftedItem.baseName != null ? giftedItem.baseName : giftedItem.name;
        String giftedItemName = giftedItem.name;

        System.out.println("[NPC_PERRY.receiveGift] Processing gift: " + giftedItemBaseName +
                " (name: " + giftedItemName + ")");
        System.out.println("[NPC_PERRY.receiveGift] Is fish? " + (giftedItem instanceof OBJ_Fish));
        System.out.println(
                "[NPC_PERRY.receiveGift] Hated items contains baseName? " + hatedItems.contains(giftedItemBaseName));
        System.out.println("[NPC_PERRY.receiveGift] Hated items contains name? " + hatedItems.contains(giftedItemName));

        String reaction = "";
        int heartPointChange = 0;

        if (lovedGiftsName.contains(giftedItemBaseName) || lovedGiftsName.contains(giftedItemName)) {
            heartPointChange = 25;
            reaction = "Wahhh! " + giftedItemBaseName + "! Ini berry favorit saya! " +
                    "Terima kasih banyak! (HP: +" + heartPointChange + " = " +
                    (currentHeartPoints + heartPointChange) + ")";
            System.out.println("[NPC_PERRY.receiveGift] LOVED gift detected: " + giftedItemBaseName);

        } else if (likedGiftsName.contains(giftedItemBaseName) || likedGiftsName.contains(giftedItemName)) {
            heartPointChange = 10;
            reaction = "Ah, " + giftedItemBaseName + "! Wine yang bagus. " +
                    "Saya apresiasi ini. (HP: +" + heartPointChange + " = " +
                    (currentHeartPoints + heartPointChange) + ")";
            System.out.println("[NPC_PERRY.receiveGift] LIKED gift detected: " + giftedItemBaseName);

        } else if (giftedItem instanceof OBJ_Fish) {
            heartPointChange = -25;
            reaction = "EEEWWW! " + giftedItemBaseName + "?! IKAN?! " +
                    "Saya BENCI semua ikan! Platypus doesn't eat fish! " +
                    "Jijik banget! (HP: " + heartPointChange + " = " +
                    (currentHeartPoints + heartPointChange) + ")";
            System.out.println("[NPC_PERRY.receiveGift] FISH detected (instanceof): " + giftedItemBaseName);

        } else if (hatedItems.contains(giftedItemBaseName) || hatedItems.contains(giftedItemName)) {
            heartPointChange = -25;
            reaction = "Ugh! " + giftedItemBaseName + "! Saya tidak suka ini sama sekali! " +
                    "Kenapa kasih barang begini?! (HP: " + heartPointChange + " = " +
                    (currentHeartPoints + heartPointChange) + ")";
            System.out.println("[NPC_PERRY.receiveGift] HATED gift detected: " + giftedItemBaseName);

        } else {
            heartPointChange = 0;
            reaction = "Oh, " + giftedItemBaseName + ". Hmm... tidak bagus tidak jelek. " +
                    "Terima kasih mungkin? (HP: " + heartPointChange + " = " +
                    currentHeartPoints + ")";
            System.out.println("[NPC_PERRY.receiveGift] NEUTRAL gift: " + giftedItemBaseName);
        }

        addHeartPoints(heartPointChange);
        gp.ui.currentDialogue = reaction;

        this.hasReceivedGiftToday = true;

        if (gp.player != null) {
            gp.player.incrementGiftFrequency(this.name);
        }

        giftedItem.quantity--;
        if (giftedItem.quantity <= 0) {
            player.inventory.remove(giftedItem);
            System.out.println("[NPC_PERRY.receiveGift] Removed item from inventory: " + giftedItemBaseName);
        } else {
            System.out.println("[NPC_PERRY.receiveGift] Decremented quantity for: " +
                    giftedItemBaseName + ", new qty: " + giftedItem.quantity);
        }

        if (gp.ui.inventoryCommandNum >= player.inventory.size() && !player.inventory.isEmpty()) {
            gp.ui.inventoryCommandNum = player.inventory.size() - 1;
        } else if (player.inventory.isEmpty()) {
            gp.ui.inventoryCommandNum = 0;
        }

        gp.gameClock.getTime().advanceTime(10);

        gp.player.tryDecreaseEnergy(5);

        System.out.println("[NPC_PERRY.receiveGift] FINAL - Heart points: " + currentHeartPoints +
                ", Change: " + heartPointChange);
        System.out.println("[NPC_PERRY.receiveGift] FINAL DIALOGUE: " + gp.ui.currentDialogue);

        gp.gameState = gp.dialogueState;
        System.out.println("[NPC_PERRY.receiveGift] END - gameState set to dialogueState.");
    }
}