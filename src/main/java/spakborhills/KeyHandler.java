// File: Tugas-Besar-OOP/src/main/java/spakborhills/KeyHandler.java
package spakborhills;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import spakborhills.entity.Entity; // Pastikan import ini ada jika Entity direferensikan
import spakborhills.entity.NPC;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed, inventoryPressed;
    GamePanel gp;
    // GameClock gameClock; // Tidak perlu instance terpisah, akses via gp.gameClock

    public KeyHandler(GamePanel gp){
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e){}

    @Override
    public void keyPressed(KeyEvent e){
        int code = e.getKeyCode();

        // Menggunakan struktur if-else if untuk memastikan hanya satu blok state yang aktif
        if (gp.gameState == gp.titleState){
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP){
                gp.ui.commandNumber--;
                if( gp.ui.commandNumber < 0){
                    gp.ui.commandNumber = 2; // Asumsi 3 menu: 0, 1, 2
                }
            } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){
                gp.ui.commandNumber++;
                if( gp.ui.commandNumber > 2){ // Asumsi 3 menu: 0, 1, 2
                    gp.ui.commandNumber = 0;
                }
            } else if (code == KeyEvent.VK_ENTER){
                enterPressed = true; // SET FLAG INI. UI.drawTitleScreen() akan memprosesnya.

                // Logika khusus untuk QUIT bisa tetap di sini jika diinginkan,
                // atau biarkan UI yang menanganinya juga.
                if (gp.ui.commandNumber == 2){ // Jika "QUIT" dipilih
                    System.exit(0);
                }
            }
        }
        // FARM NAME INPUT STATE
        else if (gp.gameState == gp.farmNameInputState) {
            handleFarmNameInput(code, e.getKeyChar());
        }
        else if (gp.gameState == gp.interactionMenuState){
            handleNPCInteractionMenuInput(code);
        }
        // PLAY STATE
        else if (gp.gameState == gp.playState){
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP){
                upPressed = true;
            } else if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT){
                leftPressed = true;
            } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){
                downPressed = true;
            } else if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT){
                rightPressed = true;
            } else if (code == KeyEvent.VK_P){
                gp.gameState = gp.pauseState;
                if(gp.gameClock != null) gp.gameClock.pauseTime(); // Panggil metode GameClock yang benar
            } else if (code == KeyEvent.VK_ENTER){
                enterPressed = true;
            } else if (code == KeyEvent.VK_I){
                gp.gameState = gp.inventoryState;
                if(gp.gameClock != null) gp.gameClock.pauseTime(); // Pause waktu
            }
        }
        // PAUSE STATE
        else if (gp.gameState == gp.pauseState){
            if (code == KeyEvent.VK_P){
                gp.gameState = gp.playState;
                if(gp.gameClock != null) gp.gameClock.resumeTime(); // Panggil metode GameClock yang benar
            }
        }
        // DIALOGUE STATE
        else if (gp.gameState == gp.dialogueState){
            if (code == KeyEvent.VK_ENTER){
                Entity currentNpc = gp.currentInteractingNPC;
                if(currentNpc != null && currentNpc.dialogueIndex < currentNpc.dialogues.size() -1 ){
                    currentNpc.dialogueIndex++;
                    gp.ui.currentDialogue = currentNpc.dialogues.get(currentNpc.dialogueIndex);
                } else {
                    gp.gameState = gp.playState;
                    if(currentNpc != null) currentNpc.dialogueIndex = 0;
                    if(gp.gameClock != null && gp.gameClock.isPaused()){
                        gp.gameClock.resumeTime();
                    }
                }
            }
        }
        // INVENTORY STATE
        else if (gp.gameState == gp.inventoryState || gp.gameState == gp.giftSelectionState) {
            handleInventoryInput(code, gp.gameState == gp.giftSelectionState);
        }
    }

    private void handleNPCInteractionMenuInput(int code) {
        if (gp.currentInteractingNPC == null) return;
        NPC npc = (NPC) gp.currentInteractingNPC;

        // Buat daftar opsi yang dinamis sesuai kondisi NPC (HARUS SAMA DENGAN DI UI)
        ArrayList<String> options = new ArrayList<>();
        options.add("Talk");
        options.add("Give Gift");

        if (npc.isMarriageCandidate && !npc.marriedToPlayer && !npc.engaged && !gp.player.isMarried()) {
            if (npc.currentHeartPoints >= 80) {
                options.add("Propose");
            }
        }
        if (npc.isMarriageCandidate && npc.engaged && !npc.marriedToPlayer && !gp.player.isMarried()) {
            options.add("Marry");
        }
        options.add("Leave");
        int maxCommands = options.size();

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            gp.ui.npcMenuCommandNum--;
            if (gp.ui.npcMenuCommandNum < 0) {
                gp.ui.npcMenuCommandNum = maxCommands - 1;
            }
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            gp.ui.npcMenuCommandNum++;
            if (gp.ui.npcMenuCommandNum >= maxCommands) {
                gp.ui.npcMenuCommandNum = 0;
            }
        }
        if (code == KeyEvent.VK_ENTER) {
            enterPressed = false;
            String selectedOption = options.get(gp.ui.npcMenuCommandNum);

            switch (selectedOption) {
                case "Talk":
                    npc.talk();
                    break;
                case "Give Gift":
                    gp.ui.isSelectingGift = true;
                    gp.gameState = gp.giftSelectionState;
                    gp.ui.inventoryCommandNum = 0;
                    break;
                case "Propose":
                    npc.getProposedTo();
                    break;
                case "Marry": // <-- TAMBAHKAN CASE UNTUK MARRY
                    npc.getMarried();
                    break;
                case "Leave":
                    gp.gameState = gp.playState;
                    if (gp.gameClock != null && gp.gameClock.isPaused()) gp.gameClock.resumeTime();
                    break;
            }
        }
        if (code == KeyEvent.VK_ESCAPE) {
            gp.gameState = gp.playState;
            if (gp.gameClock != null && gp.gameClock.isPaused()) gp.gameClock.resumeTime();
        }
    }

    @Override
    public void keyReleased(KeyEvent e){
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP){
            upPressed = false;
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT){
            leftPressed = false;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){
            downPressed = false;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT){
            rightPressed = false;
        }

        // Reset enterPressed untuk state tertentu setelah diproses.
        // UI.drawTitleScreen akan mereset enterPressed untuk titleState.
        // GamePanel.update akan mereset enterPressed untuk farmNameInputState.
        if (code == KeyEvent.VK_ENTER){
            if(gp.gameState == gp.playState || gp.gameState == gp.dialogueState) { // Hanya reset untuk state ini di sini
                enterPressed = false;
            }
        }
    }


    private void handleInventoryInput(int code, boolean isGifting) {
        // Navigasi inventory (disesuaikan dengan layout inventory Anda)
        // Contoh sederhana untuk daftar linear:
        int maxInventoryItems = gp.player.inventory.size();
        if (maxInventoryItems == 0 && code == KeyEvent.VK_ENTER && isGifting) { // Jika inventory kosong saat mau memberi hadiah
            NPC currentNPC = (NPC) gp.currentInteractingNPC;
            if(currentNPC != null) {
                gp.ui.currentDialogue = "You have nothing to give!";
                gp.gameState = gp.dialogueState;
                gp.ui.isSelectingGift = false; // Reset flag
            }
            return;
        } else if (maxInventoryItems == 0) { // Jika inventory kosong (umum)
            if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_E) { // 'E' untuk keluar inventory
                gp.gameState = gp.playState;
                if (gp.gameClock != null && gp.gameClock.isPaused()) gp.gameClock.resumeTime();
                gp.ui.isSelectingGift = false;
            }
            return;
        }


        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            gp.ui.inventoryCommandNum--;
            if (gp.ui.inventoryCommandNum < 0) gp.ui.inventoryCommandNum = maxInventoryItems - 1;
//            gp.playSE(0);
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            gp.ui.inventoryCommandNum++;
            if (gp.ui.inventoryCommandNum >= maxInventoryItems) gp.ui.inventoryCommandNum = 0;
//            gp.playSE(0);
        }
        // Anda bisa menambahkan navigasi Kiri/Kanan jika inventory berbentuk grid

        if (code == KeyEvent.VK_ENTER) {
            enterPressed = false; // Konsumsi Enter
            if (isGifting) {
                NPC currentNPC = (NPC) gp.currentInteractingNPC;
                if (currentNPC != null && gp.ui.inventoryCommandNum < maxInventoryItems) {
                    Entity selectedItem = gp.player.inventory.get(gp.ui.inventoryCommandNum);
                    currentNPC.receiveGift(selectedItem, gp.player); // Kirim juga instance Player
                    // State akan diubah ke dialogueState oleh receiveGift()
                }
                gp.ui.isSelectingGift = false; // Selesai memilih hadiah
            } else {
                // Logika penggunaan item dari inventory biasa (jika ada)
                gp.player.selectItemAndUse();
                gp.ui.showMessage("Selected: " + gp.player.inventory.get(gp.ui.inventoryCommandNum).name);
            }
//            gp.playSE(1);
        }

        if (code == KeyEvent.VK_ESCAPE || (code == KeyEvent.VK_E && !isGifting) ) { // 'E' untuk keluar inventory umum
            if (isGifting) {
                gp.gameState = gp.interactionMenuState; // Kembali ke menu NPC
                gp.ui.isSelectingGift = false;
            } else {
                gp.gameState = gp.playState;
                if (gp.gameClock != null && gp.gameClock.isPaused()) gp.gameClock.resumeTime();
            }
        }
    }

    private void handleFarmNameInput(int keyCode, char keyChar) {
        if (keyCode == KeyEvent.VK_ENTER) {
            if (!gp.ui.farmNameInput.trim().isEmpty()) {
                enterPressed = true; // Flag ini akan dibaca oleh GamePanel.update()
            } else {
                gp.ui.showMessage("Farm name cannot be empty!");
            }
        } else if (keyCode == KeyEvent.VK_BACK_SPACE) {
            if (gp.ui.farmNameInput.length() > 0) {
                gp.ui.farmNameInput = gp.ui.farmNameInput.substring(0, gp.ui.farmNameInput.length() - 1);
            }
        } else {
            if (gp.ui.farmNameInput.length() < gp.ui.farmNameMaxLength) { // farmNameMaxLength dari UI
                if (Character.isLetterOrDigit(keyChar) || Character.isWhitespace(keyChar)) {
                    // Hindari menambahkan karakter kontrol atau tombol aksi seperti Enter/Backspace sebagai karakter input
                    if(keyChar != KeyEvent.CHAR_UNDEFINED && keyCode != KeyEvent.VK_ENTER && keyCode != KeyEvent.VK_BACK_SPACE &&
                            keyCode != KeyEvent.VK_SHIFT && keyCode != KeyEvent.VK_CONTROL && keyCode != KeyEvent.VK_ALT &&
                            keyCode != KeyEvent.VK_CAPS_LOCK && keyCode != KeyEvent.VK_TAB) { // Tambahkan tombol lain yang ingin diabaikan
                        gp.ui.farmNameInput += keyChar;
                    }
                }
            }
        }
    }
}