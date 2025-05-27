package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.enums.EntityType;

public class OBJ_Chest extends Entity {
    GamePanel gp;
    public OBJ_Chest(GamePanel gp){
        super(gp);
        type = EntityType.INTERACTIVE_OBJECT;
        name = "Chest";
        down1 = setup("/objects/chest");
        collision =true;
    }
    public void update() {}
}
