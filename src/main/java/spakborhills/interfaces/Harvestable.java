package spakborhills.interfaces;

import spakborhills.entity.Player;
import spakborhills.object.OBJ_Item;

public interface Harvestable {
    boolean isReadyToHarvest();

    OBJ_Item harvest(Player player);

    int getDaysUntilHarvest();
}