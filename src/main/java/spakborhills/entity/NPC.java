package spakborhills.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import spakborhills.GamePanel;
import spakborhills.enums.EntityType;
import spakborhills.enums.ItemType;
import spakborhills.object.OBJ_Equipment;
import spakborhills.object.OBJ_Item;

public class NPC extends Entity {
    public int maxHeartPoints = 150;
    public int currentHeartPoints = 0;
    public boolean isMarriageCandidate = false;
    public boolean hasReceivedGiftToday = false;
    public boolean engaged = false;
    public boolean marriedToPlayer = false;
    public int actionLockCounter = 0;
    public int actionLockInterval = 120;
    public int proposalDay = -1;

    public List<String> lovedGiftsName = new ArrayList<>();
    public List<String> likedGiftsName = new ArrayList<>();
    public List<String> hatedItems = new ArrayList<>();
    public String giftReactionDialogue = "Oh, for me? Thank you!";
    public String proposalAcceptedDialogue = "Yes! A thousand times yes!";
    public String proposalRejectedDialogue_LowHearts = "I like you, but I'm not quite ready for that.";
    public String alreadyMarriedDialogue = "We're already together, my love!";
    public String alreadyGiftedDialogue = "You've already given me something today, thank you!";
    public String notEngagedDialogue = "Kamu saja belum melamar aku!";
    public String marriageDialogue = "Ini hari terbahagia dalam hidupku. Aku akan menemanimu seumur hidupku. (Ceritanya nikah)";
    public String proposalRejectedDialogue_TooSoon = "Cepet banget.. besok aja ya?";

    public NPC(GamePanel gp) {
        super(gp);
        type = EntityType.NPC;
        collision = true;
    }

    @Override
    public void update() {
        super.update();
        setAction();
    }

    public void openInteractionMenu() {

        facePlayer();
        gp.currentInteractingNPC = this;
        gp.gameState = gp.interactionMenuState;
        gp.ui.npcMenuCommandNum = 0;
    }

    public void chat() {
        facePlayer();
        if (dialogues.isEmpty()) {
            gp.ui.currentDialogue = name + ": ...";
        } else {
            if (dialogueIndex >= dialogues.size() || dialogueIndex < 0) {
                dialogueIndex = 0;
            }
            gp.ui.currentDialogue = dialogues.get(dialogueIndex);
            dialogueIndex++;
            if (dialogueIndex >= dialogues.size()) {
                dialogueIndex = 0;
            }
        }
        gp.player.tryDecreaseEnergy(10);
        gp.gameClock.getTime().advanceTime(10);
        addHeartPoints(10);
        gp.gameState = gp.dialogueState;
    }

    public int getCurrentHeartPoints() {
        return currentHeartPoints;
    }

    public void setCurrentHeartPoints(int currentHeartPoints) {
        this.currentHeartPoints = currentHeartPoints;
        if (this.currentHeartPoints > maxHeartPoints) {
            this.currentHeartPoints = maxHeartPoints;
        }
    }

    public void addHeartPoints(int heartPoints) {
        currentHeartPoints += heartPoints;
        if (currentHeartPoints + heartPoints > maxHeartPoints) {
            currentHeartPoints = maxHeartPoints;
        }
    }

    public void receiveGift(Entity itemEntity, Player player) {
        System.out.println("[NPC.receiveGift] START for " + this.name + ". Item: "
                + (itemEntity != null ? itemEntity.name : "null"));
        facePlayer();

        if (hasReceivedGiftToday) {
            System.out.println("[NPC.receiveGift] Already received gift today.");
            gp.ui.currentDialogue = alreadyGiftedDialogue;
            System.out.println("[NPC.receiveGift] Dialogue set to: " + gp.ui.currentDialogue);
            gp.gameState = gp.dialogueState;
            System.out.println("[NPC.receiveGift] gameState set to dialogueState (already gifted).");
            return;
        }

        if (!(itemEntity instanceof OBJ_Item) || (itemEntity instanceof OBJ_Equipment)) {
            if (itemEntity instanceof OBJ_Equipment) {
                gp.ui.showMessage("Equipment tidak bisa di gift!");
                System.out.println("[NPC.receiveGift] Item is an equipment!");
                return;
            }
            System.out.println("[NPC.receiveGift] Item is not OBJ_Item.");
            gp.ui.currentDialogue = this.name + ": I'm not sure what this is.";
            System.out.println("[NPC.receiveGift] Dialogue set to: " + gp.ui.currentDialogue);
            gp.gameState = gp.dialogueState;
            System.out.println("[NPC.receiveGift] gameState set to dialogueState (not OBJ_Item).");
            return;
        }
        OBJ_Item giftedItem = (OBJ_Item) itemEntity;
        String giftedItemBaseName = giftedItem.baseName;
        ItemType giftedItemType = giftedItem.getType();
        System.out.println("[NPC.receiveGift] Processing gift: " + giftedItemBaseName + ", Type: " + giftedItemType);

        boolean giftProcessedLogically = false;

        if (this.name.equals("Emily") && giftedItemType == ItemType.SEEDS) {
            addHeartPoints(25);
            gp.ui.currentDialogue = this.name + ": Oh, seeds! I love these! Thank you so much! (HP: "
                    + this.currentHeartPoints + ")";
            System.out.println("[NPC.receiveGift] Emily loved seeds. Dialogue: " + gp.ui.currentDialogue);
            giftProcessedLogically = true;
        } else if (lovedGiftsName.contains(giftedItemBaseName)) {
            addHeartPoints(25);
            String reaction = (this.name.equals("Caroline") || this.name.equals("Abigail"))
                    ? "Wahh, buat aku? Makasih banyak yaa! Aku sangat suka ini! (HP: " + this.currentHeartPoints + ")"
                    : this.name + ": This is amazing! Thank you! (HP: " + this.currentHeartPoints + ")";
            gp.ui.currentDialogue = reaction;
            System.out.println("[NPC.receiveGift] Loved gift. Dialogue: " + gp.ui.currentDialogue);
            giftProcessedLogically = true;
        }

        else {
            addHeartPoints(0);
            gp.ui.currentDialogue = this.giftReactionDialogue + " (HP: " + this.currentHeartPoints + ")";
            System.out.println("[NPC.receiveGift] Neutral gift. Dialogue: " + gp.ui.currentDialogue);
            giftProcessedLogically = true;
        }

        if (giftProcessedLogically) {
            this.hasReceivedGiftToday = true;

            giftedItem.quantity--;
            if (giftedItem.quantity <= 0) {
                player.inventory.remove(giftedItem);
                System.out.println("[NPC.receiveGift] Removed item from inventory: " + giftedItemBaseName);
            } else {
                System.out.println("[NPC.receiveGift] Decremented quantity for: " + giftedItemBaseName + ", new qty: "
                        + giftedItem.quantity);
            }

            if (gp.ui.inventoryCommandNum >= player.inventory.size() && !player.inventory.isEmpty()) {
                gp.ui.inventoryCommandNum = player.inventory.size() - 1;
            } else if (player.inventory.isEmpty()) {
                gp.ui.inventoryCommandNum = 0;
            }

            gp.gameClock.getTime().advanceTime(10);
            gp.player.tryDecreaseEnergy(5);

            System.out.println("[NPC.receiveGift] FINAL DIALOGUE to be shown: " + gp.ui.currentDialogue);
            gp.gameState = gp.dialogueState;
            System.out.println("[NPC.receiveGift] END. gameState set to dialogueState.");
        } else {
            System.out.println(
                    "[NPC.receiveGift] Gift was not logically processed (no preference match or other issue). No state change.");

        }
    }

    public void getProposedTo() {
        facePlayer();
        if (!isMarriageCandidate) {
            return;
        } else if (engaged || gp.player.isMarried()) {
            gp.ui.currentDialogue = alreadyMarriedDialogue;
        } else if (currentHeartPoints < 150) {
            gp.ui.currentDialogue = proposalRejectedDialogue_LowHearts + " (Current HP: " + currentHeartPoints + ")";
            gp.player.tryDecreaseEnergy(20);
        } else {
            boolean hasProposalItem = false;
            for (Entity item : gp.player.inventory) {
                if (item.name.equals("Proposal Ring misc")) {
                    hasProposalItem = true;
                    break;
                }
            }
            if (hasProposalItem) {
                gp.ui.currentDialogue = proposalAcceptedDialogue;
                this.engaged = true;
                this.proposalDay = gp.gameClock.getTime().getDay();
                gp.player.tryDecreaseEnergy(10);
                gp.gameClock.getTime().advanceTime(60);
            } else {
                gp.ui.currentDialogue = "You need a special item to propose...";
            }
        }
        gp.gameState = gp.dialogueState;
    }

    public void getMarried() {
        facePlayer();
        if (!engaged) {
            gp.ui.currentDialogue = notEngagedDialogue;
            gp.gameState = gp.dialogueState;
        } else if (marriedToPlayer || gp.player.isMarried()) {
            gp.ui.currentDialogue = alreadyMarriedDialogue;
            gp.gameState = gp.dialogueState;
        } else if (gp.gameClock.getTime().getDay() <= this.proposalDay) {
            gp.ui.currentDialogue = proposalRejectedDialogue_TooSoon;
            gp.gameState = gp.dialogueState;
        } else {
            this.marriedToPlayer = true;
            this.engaged = false;
            gp.player.setMarried(true);
            gp.player.partner = this;
            gp.ui.currentDialogue = marriageDialogue;
            gp.player.justGotMarried = true;
            gp.gameState = gp.dialogueState;

        }
    }

    public void facePlayer() {
        switch (gp.player.direction) {
            case "up":
                direction = "down";
                break;
            case "down":
                direction = "up";
                break;
            case "left":
                direction = "right";
                break;
            case "right":
                direction = "left";
                break;
        }
    }

    public void setAction() {
        actionLockCounter++;
        if (actionLockCounter >= actionLockInterval) {
            Random random = new Random();
            int i = random.nextInt(125) + 1;
            if (i <= 20) {

            } else if (i <= 45) {
                direction = "up";
            } else if (i <= 70) {
                direction = "down";
            } else if (i <= 95) {
                direction = "left";
            } else {
                direction = "right";
            }
            actionLockCounter = 0;
        }
    }
}