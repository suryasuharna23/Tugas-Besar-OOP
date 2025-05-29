package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.enums.ItemType;

public class OBJ_Misc extends OBJ_Item {
    public OBJ_Misc(GamePanel gp, ItemType itemType, String name, boolean isEdible, int buyPrice, int sellPrice) {
        super(gp, itemType, name, isEdible, buyPrice, sellPrice, 1);

        System.out.println("[OBJ_Misc] DEBUG: Creating misc item with name: '" + name + "'");
        System.out.println("[OBJ_Misc] DEBUG: this.name after super(): '" + this.name + "'");

        setupImages(name);
    }

    private void setupImages(String name) {
        System.out.println("[OBJ_Misc] DEBUG: setupImages called with name: '" + name + "'");

        String imagePath = null;

        switch (name) {
            case "Firewood":
                imagePath = "/objects/firewood";
                break;
            case "Coal":
                imagePath = "/objects/coal";
                break;
            case "Proposal Ring":
                imagePath = "/objects/proposal_ring";
                break;
            default:

                System.err.println("[OBJ_Misc] WARNING: Unknown item name: '" + name + "'");

                imagePath = "/objects/misc_default";
                break;
        }

        System.out.println("[OBJ_Misc] DEBUG: imagePath determined: '" + imagePath + "'");

        if (imagePath != null) {

            down1 = setup(imagePath);

            if (down1 == null) {
                System.err.println("[OBJ_Misc] Warning: Could not load image for " + name + " at " + imagePath);
                System.err.println("[OBJ_Misc] Trying placeholder image...");
                down1 = setup("/objects/key");
            }

            if (down1 == null) {
                System.err.println("[OBJ_Misc] Creating simple colored rectangle as fallback");
                down1 = new java.awt.image.BufferedImage(48, 48, java.awt.image.BufferedImage.TYPE_INT_RGB);
                java.awt.Graphics2D g2 = down1.createGraphics();
                g2.setColor(java.awt.Color.GRAY);
                g2.fillRect(0, 0, 48, 48);
                g2.dispose();
            }

            up1 = down1;
            left1 = down1;
            right1 = down1;
        }
    }
}