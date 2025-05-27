package spakborhills.entity;

import spakborhills.GamePanel;
import spakborhills.enums.EntityType;

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


    // GIFTING
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
        // Hadapkan NPC ke pemain
        facePlayer();
        gp.currentInteractingNPC = this;
        gp.gameState = gp.interactionMenuState; // Game state baru untuk menu interaksi
        gp.ui.npcMenuCommandNum = 0; // Reset pilihan menu
    }

    public void chat() {
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
    public void receiveGift(Entity item, Player player) {
        facePlayer();
        if (hasReceivedGiftToday) {
            gp.ui.currentDialogue = alreadyGiftedDialogue;
        } else {
            // Logika penerimaan hadiah sederhana
            // Di masa depan, bisa ada preferensi item
            if (lovedGiftsName.contains(item.name)){
                addHeartPoints(25); // loved items +25 point
                this.hasReceivedGiftToday = true;
                player.inventory.remove(item); // Hapus item dari inventaris pemain
            }
            else if (likedGiftsName.contains(item.name)){
                 addHeartPoints(20); // Liked items +10 point
                this.hasReceivedGiftToday = true;
                player.inventory.remove(item); // Hapus item dari inventaris pemain
            }
            else if (!lovedGiftsName.contains(item.name) && !likedGiftsName.contains(item.name) && !hatedItems.contains(item.name)){
                addHeartPoints(0); // Neutral Items +5 point
                this.hasReceivedGiftToday = true;
                player.inventory.remove(item); // Hapus item dari inventaris pemain
            }
            else{
                addHeartPoints(-25); // Hated Items -25 point
                this.hasReceivedGiftToday = true;
                player.inventory.remove(item); // Hapus item dari inventaris pemain
            }
            // Berikan reaksi spesifik atau default
            gp.gameClock.getTime().advanceTime(10);
            gp.player.tryDecreaseEnergy(5);
            gp.ui.currentDialogue = giftReactionDialogue + " (HP: " + this.currentHeartPoints + ")";
        }
        gp.gameState = gp.dialogueState;
    }

    public void getProposedTo() {
        facePlayer();
        if (!isMarriageCandidate) {
            return;
        } else if (engaged || gp.player.isMarried()) { // Cek juga apakah pemain sudah menikah
            gp.ui.currentDialogue = alreadyMarriedDialogue;
        } else if (currentHeartPoints < 150) {
            gp.ui.currentDialogue = proposalRejectedDialogue_LowHearts + " (Current HP: " + currentHeartPoints + ")";
            gp.player.tryDecreaseEnergy(20);
        } else {
            boolean hasProposalItem = false;
            for (Entity item : gp.player.inventory) {
                if (item.name.equals("Proposal Ring")) { // Nama item lamaran
                    hasProposalItem = true;
                    break;
                }
            }
            if (hasProposalItem) {
                gp.ui.currentDialogue = proposalAcceptedDialogue;
                this.engaged = true;
                gp.player.tryDecreaseEnergy(10);
                gp.gameClock.getTime().advanceTime(60);
                // Tambahkan dialog atau event setelah menikah jika perlu
            } else {
                gp.ui.currentDialogue = "You need a special item to propose...";
            }
        }
        gp.gameState = gp.dialogueState;
    }

    // Di dalam kelas NPC.java

    public void getMarried() {
        facePlayer();
        if (!engaged) {
            gp.ui.currentDialogue = notEngagedDialogue;
            gp.gameState = gp.dialogueState; // Tetap di dialogue state untuk menampilkan pesan
        } else if (marriedToPlayer || gp.player.isMarried()) {
            gp.ui.currentDialogue = alreadyMarriedDialogue;
            gp.gameState = gp.dialogueState; // Tetap di dialogue state
        } else {
            // Proses internal pernikahan untuk NPC dan Player
            this.marriedToPlayer = true;
            this.engaged = false; // Setelah menikah, tidak lagi engaged
            gp.player.setMarried(true);
            gp.player.partner = this; // Set NPC ini sebagai partner pemain

            // Mengurangi energi pemain sebagai "biaya" pernikahan (opsional, sesuai desain Anda)
            // Anda bisa menyesuaikan jumlahnya atau menghapusnya jika tidak diperlukan.
            boolean energySpent = gp.player.tryDecreaseEnergy(80); // Mengurangi 80 energi
            // if (!energySpent) {
            //     // Jika pemain pingsan karena energi habis saat menikah,
            //     // Player.sleep() sudah akan di-trigger oleh tryDecreaseEnergy.
            //     // GamePanel akan menangani transisi tidurnya.
            //     // Tidak perlu melakukan time skip ke 22:00 secara manual di sini jika pemain sudah pingsan.
            //     // Namun, jika Anda tetap ingin event pernikahan "selesai" sebelum pingsan,
            //     // maka time skip ke 22:00 bisa diprioritaskan.
            //     // Untuk skenario ini, kita asumsikan pernikahan selesai dulu, baru efek energi.
            // }

            // Tampilkan dialog pernikahan awal
            gp.ui.currentDialogue = marriageDialogue; // "Ini hari terbahagia..."

            // Setelah dialog ini ditampilkan (pemain menekan Enter),
            // kita akan memicu event di GamePanel untuk time skip dan tidur.
            // Kita bisa menggunakan flag atau mengubah gameState ke state khusus
            // yang akan dikenali oleh GamePanel untuk memulai event akhir hari pernikahan.

            // Untuk implementasi yang lebih sederhana saat ini:
            // Setelah dialog pernikahan ditampilkan, dan pemain menekan Enter untuk menutupnya,
            // KeyHandler atau logika setelah dialogueState bisa memanggil metode event di GamePanel.

            // Atau, cara yang lebih langsung jika getMarried() sendiri bisa memicu event GamePanel:
            // Panggil metode di GamePanel untuk menangani akhir hari pernikahan
            // (yang akan melakukan time skip ke 22:00 dan memicu tidur pemain)
            // Pastikan gp.handleEndOfWeddingEvent() ada di GamePanel Anda.

            // gp.handleEndOfWeddingEvent(); // Panggil ini jika ingin langsung trigger dari sini
            // Namun, ini akan langsung terjadi tanpa menampilkan marriageDialogue terlebih dahulu.

            // Pendekatan yang lebih baik: Biarkan dialogueState menampilkan marriageDialogue.
            // Kemudian, di KeyHandler (saat Enter ditekan untuk menutup dialog terakhir pernikahan)
            // atau di GamePanel.update() (jika dialogueState selesai dan NPC yang baru dinikahi terdeteksi),
            // panggil gp.handleEndOfWeddingEvent().

            // Untuk saat ini, kita set dialogueState. Logika pemicu event akhir hari
            // akan bergantung pada bagaimana Anda menangani akhir dari sebuah dialog.
            // Misal, Anda bisa menambahkan flag di NPC atau Player:
            gp.player.justGotMarried = true; // Tambahkan flag ini di kelas Player: public boolean justGotMarried = false;
            // Atau di NPC: this.triggerWeddingDayEndEvent = true;

            gp.gameState = gp.dialogueState; // Tampilkan marriageDialogue
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
