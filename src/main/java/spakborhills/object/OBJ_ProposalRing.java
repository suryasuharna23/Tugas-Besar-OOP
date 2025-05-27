package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.enums.EntityType;

public class OBJ_ProposalRing extends Entity {
    public OBJ_ProposalRing(GamePanel gp){
        super(gp);
        type = EntityType.INTERACTIVE_OBJECT;
        name = "Proposal Ring";
        down1 = setup("/objects/proposal_ring");
    }
}