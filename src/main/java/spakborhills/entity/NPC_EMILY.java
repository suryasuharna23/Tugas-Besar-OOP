
package spakborhills.entity;

import spakborhills.GamePanel;
import spakborhills.enums.EntityType;
import spakborhills.enums.ItemType;
import spakborhills.enums.Season;
import spakborhills.enums.Weather;
import spakborhills.object.OBJ_Crop;
import spakborhills.object.OBJ_Food;
import spakborhills.object.OBJ_Item;
import spakborhills.object.OBJ_Misc;
import spakborhills.object.OBJ_Recipe;
import spakborhills.object.OBJ_Seed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NPC_EMILY extends NPC {
        public List<OBJ_Item> shopInventory;
        private List<String> purchasedRecipe;

        public NPC_EMILY(GamePanel gp) {
                super(gp);
                direction = "down";
                speed = 1;
                name = "Emily";
                type = EntityType.NPC;
                isMarriageCandidate = true;
                currentHeartPoints = 0;

                purchasedRecipe = new ArrayList<>();

                initializeShopInventory();
                addAllSeedsToLovedGifts();
                Collections.addAll(likedGiftsName, "Catfish", "Salmon", "Sardine");
                Collections.addAll(hatedItems, "Coal", "Firewood");
                setDialogue();
                getNPCImage();

        }

        public void initializeShopInventory() {
                shopInventory = new ArrayList<>();

                shopInventory
                                .add(new OBJ_Seed(gp, ItemType.SEEDS, "Parsnip", false, 20, 10, 1, 1, Season.SPRING,
                                                Weather.RAINY));
                shopInventory.add(
                                new OBJ_Seed(gp, ItemType.SEEDS, "Cauliflower", false, 80, 40, 2, 5, Season.SPRING,
                                                Weather.SUNNY));

                shopInventory
                                .add(new OBJ_Seed(gp, ItemType.SEEDS, "Potato", false, 50, 25, 2, 3, Season.SPRING,
                                                Weather.SUNNY));
                shopInventory.add(new OBJ_Seed(gp, ItemType.SEEDS, "Wheat", false, 60, 30, 2, 1, Season.SPRING,
                                Weather.SUNNY));

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

                shopInventory.add(new OBJ_Food(gp, ItemType.FOOD, "Fish n' Chips", true, 150, 135, 50));
                shopInventory.add(new OBJ_Food(gp, ItemType.FOOD, "Baguette", true, 100, 80, 25));
                shopInventory.add(new OBJ_Food(gp, ItemType.FOOD, "Sashimi", true, 300, 275, 70));
                shopInventory.add(new OBJ_Food(gp, ItemType.FOOD, "Wine", true, 100, 90, 20));
                shopInventory.add(new OBJ_Food(gp, ItemType.FOOD, "Pumpkin Pie", true, 120, 100, 35));
                shopInventory.add(new OBJ_Food(gp, ItemType.FOOD, "Veggie Soup", true, 140, 120, 40));
                shopInventory.add(new OBJ_Food(gp, ItemType.FOOD, "Fish Stew", true, 280, 260, 70));
                shopInventory.add(new OBJ_Food(gp, ItemType.FOOD, "Fish Sandwich", true, 200, 180, 50));
                shopInventory.add(new OBJ_Food(gp, ItemType.FOOD, "Cooked Pig's Head", true, 1000, 0, 100));

                shopInventory.add(new OBJ_Crop(gp, ItemType.CROP, "Parsnip", true, 50, 35, 1, 3));
                shopInventory.add(new OBJ_Crop(gp, ItemType.CROP, "Cauliflower", true, 200, 150, 1, 3));
                shopInventory.add(new OBJ_Crop(gp, ItemType.CROP, "Wheat", true, 50, 30, 3, 3));
                shopInventory.add(new OBJ_Crop(gp, ItemType.CROP, "Blueberry", true, 150, 40, 3, 3));
                shopInventory.add(new OBJ_Crop(gp, ItemType.CROP, "Tomato", true, 90, 60, 1, 3));
                shopInventory.add(new OBJ_Crop(gp, ItemType.CROP, "Pumpkin", true, 300, 250, 1, 3));
                shopInventory.add(new OBJ_Crop(gp, ItemType.CROP, "Grape", true, 100, 10, 20, 3));

                shopInventory.add(new OBJ_Recipe(gp, "Recipe: Fish n' Chips", 500, "recipe_1"));
                shopInventory.add(new OBJ_Recipe(gp, "Recipe: Fish Sandwich", 500, "recipe_10"));

                shopInventory.add(new OBJ_Misc(gp, ItemType.MISC, "Proposal Ring", false, 1500, 750));
                shopInventory.add(new OBJ_Misc(gp, ItemType.MISC, "Firewood", false, 50, 25));
                shopInventory.add(new OBJ_Misc(gp, ItemType.MISC, "Coal", false, 100, 50));

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

        }

        private void addAllSeedsToLovedGifts() {
                System.out.println("[NPC_EMILY] Auto-detecting seeds from shop inventory...");

                for (OBJ_Item item : shopInventory) {
                        if (item instanceof OBJ_Seed) {
                                String seedName = item.name;
                                if (!lovedGiftsName.contains(seedName)) {
                                        lovedGiftsName.add(seedName);
                                        System.out.println("[NPC_EMILY] Added seed to loved gifts: " + seedName);
                                }
                        }
                }

                System.out.println("[NPC_EMILY] Total loved seeds: " +
                                lovedGiftsName.stream().filter(name -> shopInventory.stream()
                                                .anyMatch(item -> item instanceof OBJ_Seed && item.name.equals(name)))
                                                .count());
        }

        public void purchaseRecipe(String recipeName) {
                System.out.println("[NPC_EMILY] Player purchased recipe: " + recipeName);

                if (!purchasedRecipe.contains(recipeName)) {
                        purchasedRecipe.add(recipeName);
                        System.out.println("[NPC_EMILY] Added to purchased recipes: " + recipeName);
                }

                shopInventory.removeIf(item -> {
                        if (item instanceof OBJ_Recipe && item.name.equals(recipeName)) {
                                System.out.println("[NPC_EMILY] Removed recipe from shop: " + recipeName);
                                return true;
                        }
                        return false;
                });

                System.out.println("[NPC_EMILY] Shop inventory size after removal: " + shopInventory.size());
        }

        public boolean isRecipePurchased(String recipeName) {
                return purchasedRecipe.contains(recipeName);
        }

        public List<OBJ_Item> getAvailableShopInventory() {
                List<OBJ_Item> availableItems = new ArrayList<>();

                for (OBJ_Item item : shopInventory) {

                        if (item instanceof OBJ_Recipe) {
                                if (!isRecipePurchased(item.name)) {
                                        availableItems.add(item);
                                }
                        } else {

                                availableItems.add(item);
                        }
                }

                return availableItems;
        }

        public List<String> getPurchasedRecipes() {
                return new ArrayList<>(purchasedRecipe);
        }

        public void resetPurchasedRecipes() {
                purchasedRecipe.clear();
                System.out.println("[NPC_EMILY] Reset purchased recipes list");

                initializeShopInventory();
                System.out.println("[NPC_EMILY] Restored all recipes to shop");
        }
}