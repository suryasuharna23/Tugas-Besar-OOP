// NPC_EMILY.java
package spakborhills.entity;

import spakborhills.GamePanel;
import spakborhills.enums.EntityType;
import spakborhills.enums.ItemType;
import spakborhills.enums.Season;
import spakborhills.enums.Weather;
import spakborhills.object.OBJ_Food;
import spakborhills.object.OBJ_Item;
import spakborhills.object.OBJ_Recipe;
import spakborhills.object.OBJ_Seed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NPC_EMILY extends NPC {
        public List<OBJ_Item> shopInventory;

        public NPC_EMILY(GamePanel gp) {
                super(gp);
                direction = "down";
                speed = 1;
                name = "Emily";
                type = EntityType.NPC;
                isMarriageCandidate = true;
                currentHeartPoints = 0;
                Collections.addAll(lovedGiftsName, "seluruh item seeds");
                Collections.addAll(likedGiftsName, "Catfish", "Salmon", "Sardine");
                Collections.addAll(hatedItems, "Coal", "Wood");
                setDialogue();
                getNPCImage();
                initializeShopInventory();
        }

        public void initializeShopInventory() {
                shopInventory = new ArrayList<>();

                // Add Seeds [cite: 114, 117]
                shopInventory
                                .add(new OBJ_Seed(gp, ItemType.SEEDS, "Parsnip", false, 20, 10, 1, 1, Season.SPRING,
                                                Weather.RAINY));
                shopInventory.add(
                                new OBJ_Seed(gp, ItemType.SEEDS, "Cauliflower", false, 80, 40, 2, 5, Season.SPRING,
                                                Weather.SUNNY)); // Assuming
                                                                 // sunny
                                                                 // default
                                                                 // for
                                                                 // simplicity
                shopInventory
                                .add(new OBJ_Seed(gp, ItemType.SEEDS, "Potato", false, 50, 25, 2, 3, Season.SPRING,
                                                Weather.SUNNY));
                shopInventory.add(new OBJ_Seed(gp, ItemType.SEEDS, "Wheat", false, 60, 30, 2, 1, Season.SPRING,
                                Weather.SUNNY)); // Spring/Fall,
                                                 // using
                                                 // Spring
                shopInventory
                                .add(new OBJ_Seed(gp, ItemType.SEEDS, "Blueberry", false, 80, 40, 2, 7, Season.SUMMER,
                                                Weather.SUNNY));
                shopInventory
                                .add(new OBJ_Seed(gp, ItemType.SEEDS, "Tomato", false, 50, 25, 2, 3, Season.SUMMER,
                                                Weather.SUNNY));
                shopInventory
                                .add(new OBJ_Seed(gp, ItemType.SEEDS, "Hot Pepper", false, 40, 20, 2, 1, Season.SUMMER,
                                                Weather.SUNNY));
                shopInventory.add(new OBJ_Seed(gp, ItemType.SEEDS, "Melon", false, 80, 80, 2, 4, Season.SUMMER,
                                Weather.SUNNY));
                shopInventory
                                .add(new OBJ_Seed(gp, ItemType.SEEDS, "Cranberry", false, 100, 50, 2, 2, Season.FALL,
                                                Weather.SUNNY));
                shopInventory
                                .add(new OBJ_Seed(gp, ItemType.SEEDS, "Pumpkin", false, 150, 75, 2, 7, Season.FALL,
                                                Weather.SUNNY));
                shopInventory.add(new OBJ_Seed(gp, ItemType.SEEDS, "Grape", false, 60, 30, 2, 3, Season.FALL,
                                Weather.SUNNY));

                // Add Food with buy prices [cite: 142, 145]
                shopInventory.add(new OBJ_Food(gp, ItemType.FOOD, "Fish n' Chips", true, 150, 135, 50));
                shopInventory.add(new OBJ_Food(gp, ItemType.FOOD, "Baguette", true, 100, 80, 25));
                shopInventory.add(new OBJ_Food(gp, ItemType.FOOD, "Sashimi", true, 300, 275, 70));
                shopInventory.add(new OBJ_Food(gp, ItemType.FOOD, "Wine", true, 100, 90, 20));
                shopInventory.add(new OBJ_Food(gp, ItemType.FOOD, "Pumpkin Pie", true, 120, 100, 35));
                shopInventory.add(new OBJ_Food(gp, ItemType.FOOD, "Veggie Soup", true, 140, 120, 40));
                shopInventory.add(new OBJ_Food(gp, ItemType.FOOD, "Fish Stew", true, 280, 260, 70));
                shopInventory.add(new OBJ_Food(gp, ItemType.FOOD, "Fish Sandwich", true, 200, 180, 50));
                shopInventory.add(new OBJ_Food(gp, ItemType.FOOD, "Cooked Pig's Head", true, 1000, 0, 100));

                shopInventory.add(new OBJ_Recipe(gp, "Recipe: Fish n' Chips", 500, "recipe_1"));
                shopInventory.add(new OBJ_Recipe(gp, "Recipe: Fish Sandwich", 500, "recipe_10"));
        }

        public void getNPCImage() {
                up1 = setup("/npc/Emily/Emily_W1");
                up2 = setup("/npc/Emily/Emily_W2");
                down1 = setup("/npc/Emily/Emily_S1");
                down2 = setup("/npc/Emily/Emily_S2");
                left1 = setup("/npc/Emily/Emily_A1");
                left2 = setup("/npc/Emily/Emily_A2");
                right1 = setup("/npc/Emily/Emily_D1");
                right2 = setup("/npc/Emily/Emily_D2");
        }

        public void setDialogue() {
                dialogues.add("Halo namaku Emily!");
                dialogues.add("Selamat datang di toko! Ada yang bisa kubantu?");
                dialogues.add("Lihat-lihat saja dulu, siapa tahu ada yang menarik!");
        }

        @Override
        public void openInteractionMenu() {
                facePlayer();
                gp.currentInteractingNPC = this;
                gp.gameState = gp.interactionMenuState;
                gp.ui.npcMenuCommandNum = 0;
                // Additional logic for shop if needed on menu open
        }
}