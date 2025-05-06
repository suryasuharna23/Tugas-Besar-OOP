package app.gui.main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public boolean pressUp = false;
    public boolean pressDown = false;
    public boolean pressLeft = false;
    public boolean pressRight = false;

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) {
            pressUp = true;
        }
        if (code == KeyEvent.VK_A) {
            pressLeft = true;
        }
        if (code == KeyEvent.VK_S) {
            pressDown = true;
        }
        if (code == KeyEvent.VK_D) {
            pressRight = true;
        }
    }


    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) {
            pressUp = false;
        }
        if (code == KeyEvent.VK_A) {
            pressLeft = false;
        }
        if (code == KeyEvent.VK_S) {
            pressDown = false;
        }
        if (code == KeyEvent.VK_D) {
            pressRight = false;
        }
    }
}
