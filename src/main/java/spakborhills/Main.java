package spakborhills;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {

        Time time = new Time();
        Weather weather = new Weather();
        GameClock gameClock = new GameClock(time, weather);

        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Spakbor Hills");

        GamePanel gamePanel = new GamePanel();
        gamePanel.setTime(time);
        gamePanel.setWeather(weather);
        gamePanel.setGameClock(gameClock);

        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.setupGame();
        gamePanel.startGameThread();
    }
}