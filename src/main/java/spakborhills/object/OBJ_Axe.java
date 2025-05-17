package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.enums.EntityType;

public class OBJ_Axe extends Entity {
    GamePanel gp;
    public OBJ_Axe(GamePanel gp){
        super(gp);
        type = EntityType.INTERACTIVE_OBJECT;
        name = "Axe";
        down1 = setup("/objects/axe");
    }
    public void update() {}
}
