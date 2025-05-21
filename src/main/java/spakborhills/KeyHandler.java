// File: Tugas-Besar-OOP/src/main/java/spakborhills/KeyHandler.java
package spakborhills;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import spakborhills.entity.Entity; // Pastikan import ini ada jika Entity direferensikan

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
        else if (gp.gameState == gp.inventoryState) {
            if (code == KeyEvent.VK_I) {
                gp.gameState = gp.playState;
                if(gp.gameClock != null) gp.gameClock.resumeTime();
            } else if (!gp.player.inventory.isEmpty()) {
                if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                    gp.ui.inventoryCommandNum--;
                    if (gp.ui.inventoryCommandNum < 0) {
                        gp.ui.inventoryCommandNum = gp.player.inventory.size() - 1;
                    }
                } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                    gp.ui.inventoryCommandNum++;
                    if (gp.ui.inventoryCommandNum >= gp.player.inventory.size()) {
                        gp.ui.inventoryCommandNum = 0;
                    }
                } else if (code == KeyEvent.VK_ENTER) {
                    gp.player.selectItemAndUse();
                }
            }
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