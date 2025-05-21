package spakborhills;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed, inventoryPressed;
    GamePanel gp;
    GameClock gameClock;
    public KeyHandler(GamePanel gp){
        this.gp = gp;
    }

    public void keyTyped(KeyEvent e){}
    public void keyPressed(KeyEvent e){
        int code = e.getKeyCode();

        // TITLE STATE
        if (gp.gameState == gp.titleState){
            if (code == KeyEvent.VK_W){
                gp.ui.commandNumber--;
                if( gp.ui.commandNumber < 0){
                    gp.ui.commandNumber = 2;
                }
            }
            if (code == KeyEvent.VK_S){
                gp.ui.commandNumber++;
                if( gp.ui.commandNumber > 2){
                    gp.ui.commandNumber = 0;
                }
            }

            if (code == KeyEvent.VK_ENTER){
                if (gp.ui.commandNumber == 0){
                    gp.gameState = gp.playState;
                    gp.playMusic(0);
                    gp.stopMusic();
                    gp.gameClock.start();
                }
                if (gp.ui.commandNumber == 1){

                }
                if (gp.ui.commandNumber == 2){
                    System.exit(0);
                }
            }
        }

        if (gp.gameState == gp.playState){
            if (code == KeyEvent.VK_W){
                upPressed = true;
            }
            if (code == KeyEvent.VK_A){
                leftPressed = true;
            }
            if (code == KeyEvent.VK_S){
                downPressed = true;
            }
            if (code == KeyEvent.VK_D){
                rightPressed = true;
            }
            if (code == KeyEvent.VK_P){
                gp.gameState = gp.pauseState;
                gp.gameClock.getTime().pauseTime();
            }
            if (code == KeyEvent.VK_ENTER){
                enterPressed = true;
            }
            if (code == KeyEvent.VK_I){
                inventoryPressed = true;
            }
        }

        // PAUSE STATE
        else if (gp.gameState == gp.pauseState){
            if (code == KeyEvent.VK_P){
                gp.gameState = gp.playState;
                gp.gameClock.getTime().resumeTime();
            }

        }
        // DIALOGUE STATE
        else if (gp.gameState == gp.dialogueState){
            if (code == KeyEvent.VK_ENTER){
                gp.gameState = gp.playState;
            }
        }

        // INVENTORY STATE
        else if (gp.gameState == gp.inventoryState) {
            if (code == KeyEvent.VK_I) {
                inventoryPressed = true; // Untuk menutup inventaris
            }

            if (!gp.player.inventory.isEmpty()) { // Hanya proses navigasi jika ada item
                if (code == KeyEvent.VK_W) { // Tombol Atas (atau Kiri jika linear)
                    gp.ui.inventoryCommandNum--;
                    if (gp.ui.inventoryCommandNum < 0) {
                        gp.ui.inventoryCommandNum = gp.player.inventory.size() - 1; // Wrap ke item terakhir
                    }
                }
                if (code == KeyEvent.VK_S) { // Tombol Bawah (atau Kanan jika linear)
                    gp.ui.inventoryCommandNum++;
                    if (gp.ui.inventoryCommandNum >= gp.player.inventory.size()) {
                        gp.ui.inventoryCommandNum = 0; // Wrap ke item pertama
                    }
                }
                if (code == KeyEvent.VK_ENTER) { // Ketika Enter ditekan di inventaris
                    gp.player.selectItemAndUse(); // Panggil metode untuk menggunakan item
                }
            }
        }
    }

    public void keyReleased(KeyEvent e){
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W){
            upPressed = false;
        }
        if (code == KeyEvent.VK_A){
            leftPressed = false;
        }
        if (code == KeyEvent.VK_S){
            downPressed = false;
        }
        if (code == KeyEvent.VK_D){
            rightPressed = false;
        }
        if (code == KeyEvent.VK_ENTER){
            enterPressed = false;
        }
    }
}