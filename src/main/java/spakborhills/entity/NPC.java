package spakborhills.entity;

import spakborhills.GamePanel;
import spakborhills.enums.EntityType;
import spakborhills.enums.ItemType;
import spakborhills.object.OBJ_Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NPC extends Entity{
    public int maxHeartPoints = 150;
    public int currentHeartPoints = 0;
    public boolean isMarriageCandidate = false;
    public boolean hasReceivedGiftToday = false;
    public boolean engaged = false;
    public boolean marriedToPlayer = false;
    public int actionLockCounter = 0;
    public int actionLockInterval = 120;


    
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

    public NPC(GamePanel gp){
        super(gp);
        type = EntityType.NPC;
        collision = true;
    }
    @Override
    public void update(){
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
    public void setCurrentHeartPoints(int currentHeartPoints){
        this.currentHeartPoints = currentHeartPoints;
        if (this.currentHeartPoints > maxHeartPoints){
            this.currentHeartPoints = maxHeartPoints;
        }
    }
    public void addHeartPoints(int heartPoints){
        currentHeartPoints += heartPoints;
        if (currentHeartPoints + heartPoints > maxHeartPoints){
            currentHeartPoints = maxHeartPoints;
        }
    }

    public void receiveGift(Entity itemEntity, Player player) {
        facePlayer();
        if (hasReceivedGiftToday) {
            gp.ui.currentDialogue = alreadyGiftedDialogue;
            gp.gameState = gp.dialogueState;
            return;
        }

        if (!(itemEntity instanceof OBJ_Item)) {
            // Should not happen if player is gifting from OBJ_Item based inventory
            gp.ui.currentDialogue = this.name + ": I'm not sure what this is.";
            gp.gameState = gp.dialogueState;
            return;
        }

        OBJ_Item giftedItem = (OBJ_Item) itemEntity;
        String giftedItemBaseName = giftedItem.baseName; // Use the base name for comparison
        ItemType giftedItemType = giftedItem.getType();

        boolean giftConsumed = false;

        // Specific NPC preferences first
        if (this.name.equals("Emily") && giftedItemType == ItemType.SEEDS) { // [cite: 104]
            addHeartPoints(25); // Loved
            gp.ui.currentDialogue = this.name + ": Oh, seeds! I love these! Thank you so much! (HP: " + this.currentHeartPoints + ")";
            giftConsumed = true;
        } else if (this.name.equals("Perry") && giftedItemType == ItemType.FISH) { // [cite: 94]
            addHeartPoints(-25); // Hated
            gp.ui.currentDialogue = this.name + ": Ugh, fish? I really don't care for these. (HP: " + this.currentHeartPoints + ")";
            giftConsumed = true;
        } else {
            // General preferences
            if (lovedGiftsName.contains(giftedItemBaseName)) {
                addHeartPoints(25); // [cite: 194]
                gp.ui.currentDialogue = (this.name.equals("Caroline") || this.name.equals("Abigail")) ? "Wahh, buat aku? Makasih banyak yaa! Aku sangat suka ini! (HP: " + this.currentHeartPoints + ")" : this.name + ": This is amazing! Thank you! (HP: " + this.currentHeartPoints + ")";
                giftConsumed = true;
            } else if (likedGiftsName.contains(giftedItemBaseName)) {
                addHeartPoints(20); // [cite: 194]
                gp.ui.currentDialogue = this.name + ": Oh, this is very nice. Thank you. (HP: " + this.currentHeartPoints + ")";
                giftConsumed = true;
            } else if (this.name.equals("Mayor Tadi") && !lovedGiftsName.contains(giftedItemBaseName) && !likedGiftsName.contains(giftedItemBaseName)) { // Mayor Tadi hates everything not loved/liked [cite: 86, 83]
                addHeartPoints(-25);
                gp.ui.currentDialogue = this.name + ": This isn't really my style. (HP: " + this.currentHeartPoints + ")";
                giftConsumed = true;
            } else if (hatedItems.contains(giftedItemBaseName)) {
                addHeartPoints(-25); // [cite: 194]
                gp.ui.currentDialogue = this.name + ": I... appreciate the thought, but I don't like this much. (HP: " + this.currentHeartPoints + ")";
                giftConsumed = true;
            } else { // Neutral item
                addHeartPoints(0); // [cite: 194]
                gp.ui.currentDialogue = this.giftReactionDialogue + " (HP: " + this.currentHeartPoints + ")"; // Uses the NPC's default reaction or the generic one
                giftConsumed = true;
            }
        }

        if (giftConsumed) {
            this.hasReceivedGiftToday = true;

            giftedItem.quantity--;
            if (giftedItem.quantity <= 0) {
                player.inventory.remove(giftedItem); // Remove the item object itself
            }
            // Adjust player's inventory command number if the gifted item was selected and removed/quantity changed
            if (gp.ui.inventoryCommandNum >= player.inventory.size() && !player.inventory.isEmpty()) {
                gp.ui.inventoryCommandNum = player.inventory.size() - 1;
            } else if (player.inventory.isEmpty()) {
                gp.ui.inventoryCommandNum = 0;
            }


            gp.gameClock.getTime().advanceTime(10); // [cite: 194]
            gp.player.tryDecreaseEnergy(5); // [cite: 194]
        }

        gp.gameState = gp.dialogueState;
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
        } else {
            this.marriedToPlayer = true;
            this.engaged = false; 
            gp.player.setMarried(true);
            gp.player.partner = this; 
            boolean energySpent = gp.player.tryDecreaseEnergy(80); 
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
        public void setAction(){
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