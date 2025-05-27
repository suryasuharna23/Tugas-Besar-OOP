package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.enums.EntityType;

public class OBJ_Door extends Entity {
    GamePanel gp;
    public OBJ_Door(GamePanel gp){
        super(gp);
        type = EntityType.INTERACTIVE_OBJECT;
        name = "Door";
        down1 = setup("/objects/door");
        collision = true;
    }

    @Override
    public void update() {}
}
