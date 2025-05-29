
package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.entity.Player;
import spakborhills.enums.ItemType;

public class OBJ_Recipe extends OBJ_Item {
    public String recipeIdToUnlock;

    public OBJ_Recipe(GamePanel gp, String name, int buyPrice, String recipeIdToUnlock) {
        super(gp, ItemType.RECIPE, name, false, buyPrice, 0, 1); 
        this.name = name; 
        this.recipeIdToUnlock = recipeIdToUnlock;
        down1 = setup("/objects/recipe"); 
        if (down1 != null) {
            image = down1;
        } else {       
            down1 = setup("/objects/gold"); 
            if (down1 != null)
                image = down1;
        }
    }
    @Override
    public boolean use(Entity entity) {    
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (player.recipeUnlockStatus.containsKey(recipeIdToUnlock) &&
                    !Boolean.TRUE.equals(player.recipeUnlockStatus.get(recipeIdToUnlock))) {
                player.recipeUnlockStatus.put(recipeIdToUnlock, true);
                if (gp.ui != null)
                    gp.ui.showMessage("You learned the recipe: " + this.name.replace("Recipe: ", ""));
                return true; 
            } else if (Boolean.TRUE.equals(player.recipeUnlockStatus.get(recipeIdToUnlock))) {
                if (gp.ui != null)
                    gp.ui.showMessage("You already know this recipe: " + this.name.replace("Recipe: ", ""));
                return false;
            } else {
                if (gp.ui != null)
                    gp.ui.showMessage("This recipe seems unfamiliar or invalid.");
                return false;
            }
        }
        return false;
    }
}