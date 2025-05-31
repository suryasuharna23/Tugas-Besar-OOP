package spakborhills.action;

import spakborhills.GamePanel;
import spakborhills.entity.Player;
import spakborhills.interfaces.Edible;

public class EatCommand implements Command {
    Player player;
    Edible edible;

    public EatCommand(Player player, Edible edible) {
        this.player = player;
        this.edible = edible;
    }

    public void execute(GamePanel gp) {
        if (this.edible == null) {
            if (player.gp.ui != null) {
                player.gp.ui.showMessage("Tidak ada yang dapat dijangkau.");
            }
            return;
        }
        this.edible.eat(player);
        gp.playSE(6);
    }
}
