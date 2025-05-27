package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.enums.EntityType;

public class OBJ_Boots extends Entity {
    GamePanel gp;
    public OBJ_Boots(GamePanel gp){
        super(gp);
        type = EntityType.INTERACTIVE_OBJECT;
        name = "Boots";
        setup("/objects/boots");
    }
    public void update() {}
}
