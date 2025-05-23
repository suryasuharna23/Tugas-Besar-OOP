package spakborhills.entity;

import spakborhills.GamePanel;
import spakborhills.enums.EntityType;

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

    // GIFT DIALOGUES
    public String giftReactionDialogue = "Oh, for me? Thank you!";
    public String proposalAcceptedDialogue = "Yes! A thousand times yes!";
    public String proposalRejectedDialogue_LowHearts = "I like you, but I'm not quite ready for that.";
    public String proposalRejectedDialogue_NotCandidate = "I'm flattered, but that's not what I'm looking for.";
    public String alreadyMarriedDialogue = "We're already together, my love!";
    public String alreadyGiftedDialogue = "You've already given me something today, thank you!";
    public String notEngagedDialogue = "Kamu saja belum melamar akU!";
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
        // Hadapkan NPC ke pemain
        facePlayer();
        gp.currentInteractingNPC = this;
        gp.gameState = gp.interactionMenuState; // Game state baru untuk menu interaksi
        gp.ui.npcMenuCommandNum = 0; // Reset pilihan menu
    }

    public void talk() {
        facePlayer();
        if (dialogues.isEmpty()) {
            gp.ui.currentDialogue = name + ": ..."; // Default jika tidak ada dialog
        } else {
            if (dialogueIndex >= dialogues.size() || dialogueIndex < 0) {
                dialogueIndex = 0; // Reset jika indeks tidak valid
            }
            gp.ui.currentDialogue = dialogues.get(dialogueIndex);
            dialogueIndex++;
            if (dialogueIndex >= dialogues.size()) {
                dialogueIndex = 0; // Kembali ke awal jika sudah semua dialog ditampilkan
            }
        }
        gp.gameState = gp.dialogueState;
    }

    public void receiveGift(Entity item, Player player) {
        facePlayer();
        if (hasReceivedGiftToday) {
            gp.ui.currentDialogue = alreadyGiftedDialogue;
        } else {
            // Logika penerimaan hadiah sederhana
            // Di masa depan, bisa ada preferensi item
            this.currentHeartPoints += 10; // Misal: setiap hadiah +10 poin
            this.hasReceivedGiftToday = true;
            player.inventory.remove(item); // Hapus item dari inventaris pemain

            // Berikan reaksi spesifik atau default
            gp.ui.currentDialogue = giftReactionDialogue + " (HP: " + this.currentHeartPoints + ")";
        }
        gp.gameState = gp.dialogueState;
    }

    public void getProposedTo() {
        facePlayer();
        if (!isMarriageCandidate) {
            gp.ui.currentDialogue = proposalRejectedDialogue_NotCandidate;
        } else if (engaged || gp.player.isMarried()) { // Cek juga apakah pemain sudah menikah
            gp.ui.currentDialogue = alreadyMarriedDialogue;
        } else if (currentHeartPoints < 80) { // Misal butuh 80 poin hati (setara 8 hati jika 1 hati = 10 poin)
            gp.ui.currentDialogue = proposalRejectedDialogue_LowHearts + " (Current HP: " + currentHeartPoints + ")";
        } else {
            // Cek apakah pemain punya item lamaran (misal: "Mermaid Pendant")
            boolean hasProposalItem = false;
            for (Entity item : gp.player.inventory) {
                if (item.name.equals("Potion")) { // Nama item lamaran
                    hasProposalItem = true;
                    gp.player.inventory.remove(item); // Konsumsi item lamaran
                    break;
                }
            }
            if (hasProposalItem) {
                gp.ui.currentDialogue = proposalAcceptedDialogue;
                // Di sini bisa set flag misal: isEngaged = true;
                this.engaged = true;
                // Tambahkan dialog atau event setelah menikah jika perlu
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
        } else if (marriedToPlayer || gp.player.isMarried()) {
            gp.ui.currentDialogue = alreadyMarriedDialogue; // Seharusnya tidak terjadi jika engaged = true dan isMarriedToPlayer = false
        } else {
            // Proses pernikahan
            gp.ui.currentDialogue = marriageDialogue;
            this.marriedToPlayer = true;
            this.engaged = false; // Setelah menikah, tidak lagi engaged
            gp.player.setMarried(true);
            // Di sini Anda bisa menambahkan event setelah menikah, misal:
            // - Memindahkan NPC ke rumah pemain (jika ada sistemnya)
            // - Mengubah dialog harian NPC
            // - Trigger sebuah cutscene pernikahan (jika ada)
        }
        gp.gameState = gp.dialogueState;
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
        if (actionLockCounter >= actionLockInterval) { // Gunakan >= untuk memastikan eksekusi
            Random random = new Random();
            int i = random.nextInt(125) + 1; // Range 1-125 untuk lebih banyak opsi

            // Kembalikan kecepatan ke default jika sebelumnya diam
            // (asumsi speed bisa diset 0 saat diam)
            // if (this.speed == 0 && defaultSpeed > 0) {
            //     this.speed = defaultSpeed;
            // }

            if (i <= 20) { // ~16% kemungkinan untuk diam
                // Untuk diam, kita bisa tidak mengubah arah dan mungkin mengatur speed = 0
                // Jika speed diatur ke 0, pastikan ada cara untuk mengembalikannya.
                // Untuk saat ini, kita biarkan NPC tidak mengubah arah, yang berarti dia akan terus
                // bergerak ke arah terakhir jika speed > 0, atau berhenti jika speed dihandle saat diam.
                // Opsi lain: set speed = 0 dan kembalikan di awal blok if ini.
                // Atau, tidak melakukan apa-apa pada direction, yang berarti dia akan melanjutkan arah sebelumnya
                // atau berhenti jika speed = 0.
                // Paling sederhana: jangan ubah arah, jika NPC menabrak, dia akan berhenti.
            } else if (i <= 45) { // ~20% (25/125)
                direction = "up";
            } else if (i <= 70) { // ~20%
                direction = "down";
            } else if (i <= 95) { // ~20%
                direction = "left";
            } else { // ~24% (30/125)
                direction = "right";
            }
            actionLockCounter = 0; // Reset counter
        }
    }
}
