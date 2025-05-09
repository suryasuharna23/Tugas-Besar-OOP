package spakborhills.object;

import spakborhills.GamePanel;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_Chest extends SuperObject{
    GamePanel gp;
    public OBJ_Chest(GamePanel gp){
        name = "Chest";
        try {
            image = ImageIO.read(getClass()  .getResourceAsStream("/objects/chest.png"));
            utilityTool.scaleImage(image, gp.tileSize, gp.tileSize);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
