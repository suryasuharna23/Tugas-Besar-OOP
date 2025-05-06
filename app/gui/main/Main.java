package app.gui.main;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Spakbor Hills");

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        window.pack(); //biar fit  ke preferred size

        window.setLocationRelativeTo(null); //dia ada di center of screen
        window.setVisible(true);

        gamePanel.startGameThread();
    }
}