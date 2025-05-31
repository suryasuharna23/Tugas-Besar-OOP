
package spakborhills.entity;

import spakborhills.GamePanel;
import spakborhills.enums.EntityType;
import spakborhills.object.*;
import java.util.Collections;

public class NPC_MAYOR_TADI extends NPC {

    public NPC_MAYOR_TADI(GamePanel gp) {
        super(gp);
        direction = "down";
        speed = 1;
        name = "Mayor Tadi";
        type = EntityType.NPC;

        Collections.addAll(lovedGiftsName, "Legend");
        Collections.addAll(likedGiftsName, "Angler", "Crimsonfish", "Glacierfish");
        generateHatedItemsList();
        setDialogue();
        getNPCImage();
    }

    public void getNPCImage() {
        up1 = setup("/npc/MayorTadi/MayorTadi_W1");
        up2 = setup("/npc/MayorTadi/MayorTadi_W2");
        down1 = setup("/npc/MayorTadi/MayorTadi_S1");
        down2 = setup("/npc/MayorTadi/MayorTadi_S2");
        left1 = setup("/npc/MayorTadi/MayorTadi_A1");
        left2 = setup("/npc/MayorTadi/MayorTadi_A2");
        right1 = setup("/npc/MayorTadi/MayorTadi_D1");
        right2 = setup("/npc/MayorTadi/MayorTadi_D2");
    }

    public void setDialogue() {
        dialogues.add("Ya ndak tau kok tanya saya.");
    }

    @Override
    public void receiveGift(Entity itemEntity, Player player) {
        System.out.println("[NPC_MAYOR_TADI.receiveGift] START for " + this.name + ". Item: "
                + (itemEntity != null ? itemEntity.name : "null"));
        facePlayer();

        if (hasReceivedGiftToday) {
            System.out.println("[NPC_MAYOR_TADI.receiveGift] Already received gift today.");
            gp.ui.currentDialogue = alreadyGiftedDialogue;
            gp.gameState = gp.dialogueState;
            return;
        }

        if (!(itemEntity instanceof OBJ_Item) || (itemEntity instanceof OBJ_Equipment)) {
            if (itemEntity instanceof OBJ_Equipment) {
                gp.ui.showMessage("Equipment tidak bisa di gift!");
                System.out.println("[NPC_MAYOR_TADI.receiveGift] Item is an equipment!");
                return;
            }
            gp.ui.currentDialogue = this.name + ": I'm not sure what this is.";
            gp.gameState = gp.dialogueState;
            return;
        }

        OBJ_Item giftedItem = (OBJ_Item) itemEntity;
        String giftedItemBaseName = giftedItem.baseName != null ? giftedItem.baseName : giftedItem.name;
        String cleanItemName = giftedItemBaseName;

        System.out.println("[NPC_MAYOR_TADI.receiveGift] Processing gift: " + giftedItemBaseName +
                " (clean name: " + cleanItemName + ")");

        String reaction = "";
        int heartPointChange = 0;

        if (lovedGiftsName.contains(giftedItemBaseName) || lovedGiftsName.contains(cleanItemName)) {
            heartPointChange = 25;
            reaction = "AMAZING! Ini dia yang kucari-cari! " +
                    "Kamu mengerti passion saya! (HP: +" + heartPointChange + " = " +
                    (currentHeartPoints + heartPointChange) + ")";
            System.out.println("[NPC_MAYOR_TADI.receiveGift] LOVED gift detected: " + cleanItemName);

        } else if (likedGiftsName.contains(giftedItemBaseName) || likedGiftsName.contains(cleanItemName)) {
            heartPointChange = 10;
            reaction = "Ah, ikan yang bagus! Saya apresiasi. " +
                    "(HP: +" + heartPointChange + " = " +
                    (currentHeartPoints + heartPointChange) + ")";
            System.out.println("[NPC_MAYOR_TADI.receiveGift] LIKED gift detected: " + cleanItemName);

        } else if (hatedItems.contains(giftedItemBaseName) || hatedItems.contains(cleanItemName)) {
            heartPointChange = -25;
            reaction = "Ugh, barang apaan nih, sampah! " +
                    "Saya cuman peduli barang yang legend dan berharga! " +
                    "Malu maluin aja! (HP: " + heartPointChange + " = " +
                    (currentHeartPoints + heartPointChange) + ")";
            System.out.println("[NPC_MAYOR_TADI.receiveGift] HATED gift detected: " + cleanItemName);

        } else {
            heartPointChange = 0;
            reaction = "Hmm... Jujur gak tau ini buat apa dan maknanya apa, tapi terima kasih ya. " +
                    "(HP: " + heartPointChange + " = " +
                    currentHeartPoints + ")";
            System.out.println("[NPC_MAYOR_TADI.receiveGift] UNKNOWN gift (not in any list): " + cleanItemName);
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
            System.out.println("[NPC_MAYOR_TADI.receiveGift] Removed item from inventory: " + giftedItemBaseName);
        } else {
            System.out.println("[NPC_MAYOR_TADI.receiveGift] Decremented quantity for: " +
                    giftedItemBaseName + ", new qty: " + giftedItem.quantity);
        }

        if (gp.ui.inventoryCommandNum >= player.inventory.size() && !player.inventory.isEmpty()) {
            gp.ui.inventoryCommandNum = player.inventory.size() - 1;
        } else if (player.inventory.isEmpty()) {
            gp.ui.inventoryCommandNum = 0;
        }

        gp.gameClock.getTime().advanceTime(10);

        if (heartPointChange > 0) {
            gp.player.tryDecreaseEnergy(5);
        } else if (heartPointChange < 0) {
            gp.player.tryDecreaseEnergy(15);
        } else {
            gp.player.tryDecreaseEnergy(10);
        }

        System.out.println("[NPC_MAYOR_TADI.receiveGift] FINAL - Heart points: " + currentHeartPoints +
                ", Change: " + heartPointChange);
        System.out.println("[NPC_MAYOR_TADI.receiveGift] FINAL DIALOGUE: " + gp.ui.currentDialogue);

        gp.gameState = gp.dialogueState;
        System.out.println("[NPC_MAYOR_TADI.receiveGift] END - gameState set to dialogueState.");
    }
    
    private void generateHatedItemsList() {
        System.out.println("[NPC_MAYOR_TADI] Generating hated items list...");

        // Temporary: Add common items yang pasti ada di game
        Collections.addAll(hatedItems,
                // Seeds
                "Parsnip", "Cauliflower", "Potato", "Wheat", "Blueberry",
                "Tomato", "Hot Pepper", "Melon", "Cranberry", "Pumpkin", "Grape",

                // Common fish (exclude legendary ones)
                "Bullhead", "Carp", "Chub", "Catfish", "Flounder", "Halibut",
                "Largemouth Bass", "Midnight Carp", "Octopus", "Pufferfish",
                "Rainbow Trout", "Salmon", "Sardine", "Sturgeon", "Super Cucumber",

                // Food items
                "Fish n' Chips", "Baguette", "Fish Sandwich", "Fish Stew",
                "Fugu", "Pumpkin Pie", "Sashimi", "Spakbor Salad", "Veggie Soup",
                "Wine", "Cooked Pig's Head",

                // Resources & misc
                "Coal", "Firewood","Proposal Ring");

        System.out.println("[NPC_MAYOR_TADI] Generated " + hatedItems.size() + " hated items");
    }
}