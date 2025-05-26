// File: Tugas-Besar-OOP/src/main/java/spakborhills/KeyHandler.java
package spakborhills;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import spakborhills.entity.Entity; // Pastikan import ini ada jika Entity direferensikan
import spakborhills.entity.NPC;
import spakborhills.interfaces.Edible;
import spakborhills.object.OBJ_Item;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed, inventoryPressed, eatPressed;
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
        // PLAYER NAME INPUT STATE
        else if (gp.gameState == gp.playerNameInputState){
            handlePlayerNameInput(code, e.getKeyChar());
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
            } else if (code == KeyEvent.VK_E) {
                eatPressed = true; // Flag ini akan ditangani di Player.update()
            } else if (code == KeyEvent.VK_I){
                if (gp.player != null) { // Selalu baik untuk null check
                    gp.gameState = gp.inventoryState;
                    System.out.println("DEBUG: KeyHandler - GameState changed to inventoryState via 'I'.");
                }
                } else if (code == KeyEvent.VK_E) { // Tombol 'E' untuk makan item yang di-equip
                    gp.gameState = gp.eatState;
                    eatPressed = true;
                }
            if (e.getKeyCode() == java.awt.event.KeyEvent.VK_F) {
                if (gp.player != null) {
                    gp.player.startFishing();
                }
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

    private void handlePlayerNameInput(int keyCode, char keyChar) {
        if (keyCode == KeyEvent.VK_ENTER) {
            if (!gp.ui.playerNameInput.trim().isEmpty()) {
                // Simpan nama pemain ke objek Player
                gp.player.name = gp.ui.playerNameInput.trim(); // atau gp.player.setPlayerName(...)
                System.out.println("Player Name Set: " + gp.player.name); // Untuk Debug

                // Pindah ke state input nama farm
                gp.gameState = gp.farmNameInputState;
                gp.ui.farmNameInput = ""; // Reset input nama farm
                // enterPressed tidak perlu di-set true di sini karena transisi state langsung
            } else {
                gp.ui.showMessage("Player name cannot be empty!"); // Tampilkan pesan jika kosong
            }
        } else if (keyCode == KeyEvent.VK_BACK_SPACE) {
            if (gp.ui.playerNameInput.length() > 0) {
                gp.ui.playerNameInput = gp.ui.playerNameInput.substring(0, gp.ui.playerNameInput.length() - 1);
            }
        } else {
            if (gp.ui.playerNameInput.length() < gp.ui.playerNameMaxLength) { // Gunakan playerNameMaxLength
                // Cek karakter yang valid (mirip dengan farmNameInput)
                if (Character.isLetterOrDigit(keyChar) || Character.isWhitespace(keyChar)) {
                    if(keyChar != KeyEvent.CHAR_UNDEFINED && keyCode != KeyEvent.VK_ENTER && keyCode != KeyEvent.VK_BACK_SPACE &&
                            keyCode != KeyEvent.VK_SHIFT && keyCode != KeyEvent.VK_CONTROL && keyCode != KeyEvent.VK_ALT &&
                            keyCode != KeyEvent.VK_CAPS_LOCK && keyCode != KeyEvent.VK_TAB) {
                        gp.ui.playerNameInput += keyChar;
                    }
                }
            }
        }
    }


    private void handleNPCInteractionMenuInput(int code) {
        if (gp.currentInteractingNPC == null) return;
        NPC npc = (NPC) gp.currentInteractingNPC;

        // Buat daftar opsi yang dinamis sesuai kondisi NPC (HARUS SAMA DENGAN DI UI)
        ArrayList<String> options = new ArrayList<>();
        options.add("Chat");
        options.add("Give Gift");

        if (npc.isMarriageCandidate && !npc.marriedToPlayer && !npc.engaged && !gp.player.isMarried()) {
            options.add("Propose");
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
                case "Chat":
                    npc.chat();
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

        if (code == KeyEvent.VK_E) { // Tombol untuk makan
            if (gp.player.inventory.isEmpty()) return;

            int itemIndex = gp.ui.inventoryCommandNum;
            if (itemIndex >= 0 && itemIndex < gp.player.inventory.size()) {
                Entity selectedItem = gp.player.inventory.get(itemIndex);
                if (selectedItem instanceof Edible edibleItem) { // Cek apakah item bisa dimakan
                    // Tidak perlu lagi memanggil use() yang hanya menampilkan pesan.
                    // Langsung panggil method makan.
                    edibleItem.eat(gp.player);

                    // Setelah makan, mungkin ingin menutup inventory atau tetap terbuka
                    // gp.gameState = gp.playState;
                    // if (gp.gameClock != null && gp.gameClock.isPaused()) {
                    //     gp.gameClock.resumeTime();
                    // }
                    // Atau biarkan UI yang mengatur state selanjutnya jika perlu
                } else {
                    gp.ui.showMessage(selectedItem.name + " cannot be eaten.");
                }
            }
            enterPressed = false; // Konsumsi 'E' jika dianggap sebagai "aksi"
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
            else if (gp.gameState == gp.eatState) {
                eatPressed = false;
            }
        }


    }


    private void handleInventoryInput(int code, boolean isGifting) {
        int maxInventoryItems = gp.player.inventory.size();
        if (maxInventoryItems == 0) { // Handle inventory kosong
            if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_I) {
                gp.gameState = gp.playState;
                if (gp.gameClock != null && gp.gameClock.isPaused()) gp.gameClock.resumeTime();
                gp.ui.isSelectingGift = false;
            }
            return;
        }

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            gp.ui.inventoryCommandNum--;
            if (gp.ui.inventoryCommandNum < 0) gp.ui.inventoryCommandNum = maxInventoryItems - 1;
            gp.ui.itemSelectedByEnter = null; // Reset item yang dipilih Enter saat navigasi
        } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            gp.ui.inventoryCommandNum++;
            if (gp.ui.inventoryCommandNum >= maxInventoryItems) gp.ui.inventoryCommandNum = 0;
            gp.ui.itemSelectedByEnter = null; // Reset item yang dipilih Enter saat navigasi
        }

        if (code == KeyEvent.VK_ENTER) {
            enterPressed = false;
            if (isGifting) {
                NPC currentNPC = (NPC) gp.currentInteractingNPC;
                if (currentNPC != null && gp.ui.inventoryCommandNum < maxInventoryItems) {
                    Entity selectedItem = gp.player.inventory.get(gp.ui.inventoryCommandNum);
                    currentNPC.receiveGift(selectedItem, gp.player);
                }
                gp.ui.isSelectingGift = false;
            } else { // Bukan gifting, ini adalah aksi equip/select item
                if (!gp.player.inventory.isEmpty() && gp.ui.inventoryCommandNum >= 0 && gp.ui.inventoryCommandNum < gp.player.inventory.size()) {
                    gp.player.equipItem(gp.ui.inventoryCommandNum); // Panggil metode equipItem baru di Player
                }
            }
        }

        if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_I || (code == KeyEvent.VK_E && !isGifting) ) {
            if (isGifting) {
                gp.gameState = gp.interactionMenuState;
            } else {
                gp.gameState = gp.playState;
            }
            if (gp.gameClock != null && gp.gameClock.isPaused()) gp.gameClock.resumeTime();
            gp.ui.isSelectingGift = false;
            gp.ui.itemSelectedByEnter = null;
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