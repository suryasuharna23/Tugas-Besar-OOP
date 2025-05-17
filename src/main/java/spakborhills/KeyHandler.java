package spakborhills;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.security.Key;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed;
    GamePanel gp;
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
            }
            if (code == KeyEvent.VK_ENTER){
                enterPressed = true;
            }
        }

        // PAUSE STATE
        else if (gp.gameState == gp.pauseState){
            if (code == KeyEvent.VK_P){
                gp.gameState = gp.playState;
            }

        }
        // DIALOGUE STATE
        if (gp.gameState == gp.dialogueState){
            if (code == KeyEvent.VK_ENTER){
                gp.gameState = gp.playState;
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

    }
}