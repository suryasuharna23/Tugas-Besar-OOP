package spakborhills.object;

import spakborhills.GamePanel;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_Door extends SuperObject{
    GamePanel gp;
    public OBJ_Door(GamePanel gp){
        name = "Door";
        try {
            image = ImageIO.read(getClass()  .getResourceAsStream("/objects/door.png"));
            utilityTool.scaleImage(image, gp.tileSize, gp.tileSize);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        collision = true;
    }

}
