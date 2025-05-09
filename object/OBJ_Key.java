package spakborhills.object;

import spakborhills.GamePanel;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_Key extends SuperObject {
    GamePanel gp;
    public OBJ_Key(GamePanel gp){
        name = "Key";
        try {
            image = ImageIO.read(getClass()  .getResourceAsStream("/objects/key.png"));
            utilityTool.scaleImage(image,gp.tileSize, gp.tileSize);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
