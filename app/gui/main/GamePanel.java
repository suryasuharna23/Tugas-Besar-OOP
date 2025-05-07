package app.gui.main;

import app.gui.entity.Player;
import app.srcs.tiles.TileManager;

import java.awt.*;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {
    //SCREEN SETTINGS
    public final int originalTileSize = 16; //16x16 tile
    public final int scale = 2;

    public final int tileSize = originalTileSize * scale; //48x48 tile
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    KeyHandler handler = new KeyHandler();
    Thread gameThread;
    Player player = new Player(this, handler);
    TileManager tileManager = new TileManager(this);

    int playerX = 100;
    int playerY = 100;
    int playerSpeed = 4;
    int fps = 60;


    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); //better rendering performance
        this.addKeyListener(handler);
        this.setFocusable(true);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run() {
        double interval = 1000000000.0/fps;
        double nextDrawTime = System.nanoTime() + interval;

        long currentTime = System.nanoTime();
        while(gameThread != null) {
            System.out.println("currentTime: " + currentTime);
            update();
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime/1000000;

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long)remainingTime); //pause game loop

                nextDrawTime += interval;
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void update() {
        player.update();
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        tileManager.draw(g2);
        player.draw(g2);
        g2.dispose();
    }
}