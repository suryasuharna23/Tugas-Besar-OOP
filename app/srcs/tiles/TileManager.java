package app.srcs.tiles;

import app.gui.main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class TileManager {
    GamePanel gp;
    Tile[] tile;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[10];
        getTileImage();
    }

    public void getTileImage() {
        try {
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResource("/app/gui/asset/tile/Tile_1.png"));
            tile[1] = new Tile();
            tile[1].image = ImageIO.read(getClass().getResource("/app/gui/asset/tile/Tile_2.png"));
            tile[2] = new Tile();
            tile[2].image = ImageIO.read(getClass().getResource("/app/gui/asset/tile/Tile_3.png"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        int col = 0;
        int row = 0;
        int x = 0;
        int y = 0;

        while (col < gp.maxScreenCol && row < gp.maxScreenRow) {
            g2.drawImage(tile[0].image, x, y, gp.tileSize, gp.tileSize, null);
            col++;
            x +=32;

            if (col == gp.maxScreenCol) {
                col = 0;
                row++;
                x = 0;
                y += 32;
            }

        }



    }


}
