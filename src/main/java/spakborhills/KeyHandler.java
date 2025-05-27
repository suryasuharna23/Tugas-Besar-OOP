// KeyHandler.java
package spakborhills;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import spakborhills.action.PlantingCommand;
import spakborhills.action.RecoverLandCommand;
import spakborhills.action.TillingCommand;
import spakborhills.action.WateringCommand;
import spakborhills.cooking.ActiveCookingProcess;
import spakborhills.cooking.Recipe;
import spakborhills.cooking.RecipeManager;
import spakborhills.entity.Entity;
import spakborhills.entity.NPC;
import spakborhills.entity.NPC_EMILY;
import spakborhills.entity.Player;
import spakborhills.interfaces.Edible;
import spakborhills.object.OBJ_Fish;
import spakborhills.object.OBJ_Item;
import spakborhills.object.OBJ_Misc;
import spakborhills.object.OBJ_Recipe;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed, inventoryPressed, eatPressed;
    GamePanel gp;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (gp.gameState == gp.titleState) {

            if (gp.ui.mapSelectionState == 0) {
                if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                    gp.ui.commandNumber--;
                    if (gp.ui.commandNumber < 0) {
                        gp.ui.commandNumber = 2;
                    }
                } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                    gp.ui.commandNumber++;
                    if (gp.ui.commandNumber > 2) {
                        gp.ui.commandNumber = 0;

                    }
                } else if (code == KeyEvent.VK_ENTER) {
                    // enterPressed = true; // Let specific actions handle this
                    if (gp.ui.commandNumber == 0) { // New Game
                        gp.gameState = gp.playerNameInputState;
                        gp.ui.playerNameInput = "";
                        if (gp.gameClock != null && !gp.gameClock.isPaused()) {
                            gp.gameClock.pauseTime();
                        }
                    } else if (gp.ui.commandNumber == 1) { // Load Game (Bonus)
                        gp.ui.showMessage("Load Game (Not Implemented Yet)");
                    } else if (gp.ui.commandNumber == 2) { // Quit
                        System.exit(0);
                    }
                }
            }

            else if (gp.ui.mapSelectionState == 1) { // World Map Selection
                if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                    gp.ui.commandNumber--;
                    if (gp.ui.commandNumber < 0) {
                        gp.ui.commandNumber = gp.mapInfos.size() - 1; // Assuming mapInfos list size
                    }
                } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                    gp.ui.commandNumber++;
                    if (gp.ui.commandNumber >= gp.mapInfos.size()) {
                        gp.ui.commandNumber = 0;
                    }
                } else if (code == KeyEvent.VK_ENTER) {
                    if (gp.ui.commandNumber >= 0 && gp.ui.commandNumber < gp.mapInfos.size()) {
                        System.out.println("DEBUG: KeyHandler - Map Selected Index: " + gp.ui.commandNumber);
                        gp.loadMapbyIndex(gp.ui.commandNumber);
                        gp.gameState = gp.playState;
                        if (gp.gameClock != null && gp.gameClock.isPaused()) {
                            gp.gameClock.resumeTime();
                        }
                    }
                } else if (code == KeyEvent.VK_ESCAPE) {
                    gp.ui.mapSelectionState = 0; // Go back to main title screen options
                    gp.ui.commandNumber = 0;
                }
            }
        }

        else if (gp.gameState == gp.playerNameInputState) {
            handlePlayerNameInput(code, e.getKeyChar());

        }

        else if (gp.gameState == gp.farmNameInputState) {
            handleFarmNameInput(code, e.getKeyChar());

        } else if (gp.gameState == gp.interactionMenuState) {
            handleNPCInteractionMenuInput(code);
        }

        else if (gp.gameState == gp.playState) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                upPressed = true;
            } else if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                leftPressed = true;
            } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                downPressed = true;
            } else if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                rightPressed = true;
            } else if (code == KeyEvent.VK_P) {
                gp.gameState = gp.pauseState;
                if (gp.gameClock != null)
                    gp.gameClock.pauseTime();
            } else if (code == KeyEvent.VK_ENTER) {
                enterPressed = true;
            } else if (code == KeyEvent.VK_M) { // Open World Map
                gp.gameState = gp.titleState; // Re-use title state for map selection UI
                gp.ui.mapSelectionState = 1; // Set UI to map selection mode
                gp.ui.commandNumber = gp.currentMapIndex != -1 ? gp.currentMapIndex : 0; // Start on current map or
                                                                                         // first map

                if (gp.gameClock != null && !gp.gameClock.isPaused()) {
                    gp.gameClock.pauseTime();
                }
            } else if (code == KeyEvent.VK_K) { // Fishing
                gp.player.startFishing();
            } else if (code == KeyEvent.VK_E) { // Eat
                eatPressed = true;
            } else if (code == KeyEvent.VK_R) { // Tilling
                new TillingCommand(gp.player).execute(gp);
            } else if (code == KeyEvent.VK_T) { // Recover Land
                new RecoverLandCommand(gp.player).execute(gp);
            } else if (code == KeyEvent.VK_G) { // Watering
                new WateringCommand(gp.player).execute(gp);
            } else if (code == KeyEvent.VK_F) { // Planting (assuming seed is equipped or handled by command)
                new PlantingCommand(gp.player).execute(gp);
            } else if (code == KeyEvent.VK_I) { // Inventory
                if (gp.player != null) {
                    gp.gameState = gp.inventoryState;
                    gp.ui.inventoryCommandNum = 0; // Reset selection
                    System.out.println("DEBUG: KeyHandler - GameState changed to inventoryState via 'I'.");
                    if (gp.gameClock != null && !gp.gameClock.isPaused()) {
                        gp.gameClock.pauseTime();
                    }
                }
            }
        }

        else if (gp.gameState == gp.pauseState) {
            if (code == KeyEvent.VK_P) {
                gp.gameState = gp.playState;
                if (gp.gameClock != null)
                    gp.gameClock.resumeTime();
            }
        }

        else if (gp.gameState == gp.dialogueState) {
            if (code == KeyEvent.VK_ENTER) {
                // If dialogue is from an NPC
                if (gp.currentInteractingNPC != null && gp.currentInteractingNPC instanceof NPC) {
                    NPC npc = (NPC) gp.currentInteractingNPC;
                    if (npc.dialogueIndex < npc.dialogues.size() - 1) {
                        npc.dialogueIndex++;
                        gp.ui.currentDialogue = npc.dialogues.get(npc.dialogueIndex);
                    } else {
                        npc.dialogueIndex = 0; // Reset for next time
                        gp.gameState = gp.playState; // Default return to play state
                        if (gp.gameClock != null && gp.gameClock.isPaused()) {
                            gp.gameClock.resumeTime();
                        }
                    }
                } else { // Self-dialogue or system message
                    gp.gameState = gp.playState;
                    if (gp.gameClock != null && gp.gameClock.isPaused()) {
                        gp.gameClock.resumeTime();
                    }
                }
                gp.ui.messageOn = false; // Clear any short messages if dialogue takes over
                gp.ui.message = "";
            }
        }

        else if (gp.gameState == gp.inventoryState || gp.gameState == gp.giftSelectionState) {
            handleInventoryInput(code, gp.gameState == gp.giftSelectionState);
        }

        else if (gp.gameState == gp.sellState) {
            if (code == KeyEvent.VK_ESCAPE) {
                gp.player.hasUsedShippingBinToday = true;
                gp.gameState = gp.playState;
                if (gp.gameClock != null && gp.gameClock.isPaused()) {
                    gp.gameClock.resumeTime();
                }
                if (!gp.player.itemsInShippingBinToday.isEmpty()) {
                    gp.ui.showMessage("Items in bin will be sold overnight.");
                } else {
                    gp.ui.showMessage("Shipping bin closed.");
                }

            } else if (code == KeyEvent.VK_ENTER) {
                if (!gp.player.inventory.isEmpty() && gp.ui.commandNumber < gp.player.inventory.size()
                        && gp.ui.commandNumber >= 0) {
                    if (gp.player.itemsInShippingBinToday.size() < 16) {
                        Entity itemEntityToShip = gp.player.inventory.get(gp.ui.commandNumber);

                        if (itemEntityToShip instanceof OBJ_Item) {
                            OBJ_Item itemToShip = (OBJ_Item) itemEntityToShip;
                            if (itemToShip.getSellPrice() > 0) {
                                gp.player.itemsInShippingBinToday.add(itemToShip);
                                gp.player.inventory.remove(gp.ui.commandNumber);

                                gp.ui.showMessage(itemToShip.name + " moved to bin. ("
                                        + gp.player.itemsInShippingBinToday.size() + "/16)");

                                if (gp.ui.commandNumber >= gp.player.inventory.size()
                                        && !gp.player.inventory.isEmpty()) {
                                    gp.ui.commandNumber = gp.player.inventory.size() - 1;
                                } else if (gp.player.inventory.isEmpty()) {
                                    gp.ui.commandNumber = 0;
                                    gp.ui.showMessage("Inventory empty. Press Esc to close bin.");
                                }
                            } else {
                                gp.ui.showMessage(itemToShip.name + " cannot be sold.");

                            }
                        } else {

                            gp.ui.showMessage(itemEntityToShip.name + " is not a sellable item type.");

                        }
                    } else {
                        gp.ui.showMessage("Shipping bin is full (16 items max).");

                    }
                } else if (gp.player.inventory.isEmpty()) {
                    gp.ui.showMessage("Inventory is empty. Nothing to ship.");
                }
            }

            sellScreenControls(code);
        }

        else if (gp.gameState == gp.cookingState) {
            handleCookingInput(code);
        } else if (gp.gameState == gp.buyingState) {
            handleBuyingInput(code);
        }
    }

    private void handleBuyingInput(int code) {
        if (!(gp.currentInteractingNPC instanceof NPC_EMILY)) {
            gp.gameState = gp.playState; // Should not happen if state is managed correctly
            if (gp.gameClock != null && gp.gameClock.isPaused())
                gp.gameClock.resumeTime();
            return;
        }
        NPC_EMILY emily = (NPC_EMILY) gp.currentInteractingNPC;
        if (emily.shopInventory.isEmpty()) {
            gp.ui.showMessage("Emily has nothing to sell right now.");
            if (code == KeyEvent.VK_ESCAPE) {
                gp.gameState = gp.playState;
                if (gp.gameClock != null && gp.gameClock.isPaused())
                    gp.gameClock.resumeTime();
            }
            return;
        }

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            gp.ui.storeCommandNum--;
            if (gp.ui.storeCommandNum < 0) {
                gp.ui.storeCommandNum = emily.shopInventory.size() - 1;
            }
        } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            gp.ui.storeCommandNum++;
            if (gp.ui.storeCommandNum >= emily.shopInventory.size()) {
                gp.ui.storeCommandNum = 0;
            }
        } else if (code == KeyEvent.VK_ENTER) {
            if (gp.ui.storeCommandNum >= 0 && gp.ui.storeCommandNum < emily.shopInventory.size()) {
                OBJ_Item selectedShopItem = emily.shopInventory.get(gp.ui.storeCommandNum);
                String recipeIdToUnlock = null;
                if (selectedShopItem instanceof OBJ_Recipe) {
                    recipeIdToUnlock = ((OBJ_Recipe) selectedShopItem).recipeIdToUnlock;
                }
                gp.player.buyItem(selectedShopItem, recipeIdToUnlock);
            }
        } else if (code == KeyEvent.VK_ESCAPE) {
            gp.gameState = gp.playState;
            if (gp.gameClock != null && gp.gameClock.isPaused()) {
                gp.gameClock.resumeTime();
            }
            gp.ui.showMessage("Leaving the shop.");
        }
    }

    private void handleCookingInput(int code) {
        List<Recipe> availableRecipes = RecipeManager.getAllRecipes().stream()
                .filter(r -> Boolean.TRUE.equals(gp.player.recipeUnlockStatus.get(r.recipeId)))
                .toList();

        if (availableRecipes.isEmpty() && gp.ui.cookingSubState == 0) {
            gp.ui.showMessage("You don't know any recipes yet!");
            if (code == KeyEvent.VK_ESCAPE) {
                gp.gameState = gp.playState;
                if (gp.gameClock != null && gp.gameClock.isPaused())
                    gp.gameClock.resumeTime();
            }
            return;
        }

        if (gp.ui.cookingSubState == 0) { // Recipe Selection
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.cookingCommandNum--;
                if (gp.ui.cookingCommandNum < 0)
                    gp.ui.cookingCommandNum = availableRecipes.isEmpty() ? 0 : availableRecipes.size() - 1;
            } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.cookingCommandNum++;
                if (gp.ui.cookingCommandNum >= (availableRecipes.isEmpty() ? 1 : availableRecipes.size()))
                    gp.ui.cookingCommandNum = 0;
            } else if (code == KeyEvent.VK_ENTER) {
                if (!availableRecipes.isEmpty() && gp.ui.cookingCommandNum < availableRecipes.size()) {
                    gp.selectedRecipeForCooking = availableRecipes.get(gp.ui.cookingCommandNum);
                    if (canPlayerCookRecipe(gp.selectedRecipeForCooking, gp.player)) {
                        gp.ui.cookingSubState = 1; // Move to confirmation
                        gp.ui.showMessage(
                                "Cook " + gp.selectedRecipeForCooking.outputFoodName + "? (Cost: 10 Energy, 1hr)");
                    } else {
                        // gp.ui.showMessage("You lack ingredients or fuel for " +
                        // gp.selectedRecipeForCooking.outputFoodName + "."); // Message handled in
                        // canPlayerCookRecipe
                        gp.selectedRecipeForCooking = null;
                    }
                }
            } else if (code == KeyEvent.VK_ESCAPE) {
                gp.gameState = gp.playState;
                if (gp.gameClock != null && gp.gameClock.isPaused())
                    gp.gameClock.resumeTime();
                gp.selectedRecipeForCooking = null;
            }
        } else if (gp.ui.cookingSubState == 1) { // Confirmation
            if (code == KeyEvent.VK_ENTER) {
                if (gp.selectedRecipeForCooking != null) {
                    initiateCookingProcess(gp.selectedRecipeForCooking, gp.player);

                }
                gp.gameState = gp.playState;
                if (gp.gameClock != null && gp.gameClock.isPaused())
                    gp.gameClock.resumeTime();
                gp.selectedRecipeForCooking = null;
                gp.ui.cookingSubState = 0;
            } else if (code == KeyEvent.VK_ESCAPE) {
                gp.ui.cookingSubState = 0; // Back to recipe selection
                gp.selectedRecipeForCooking = null;
                gp.ui.showMessage("Recipe selection cancelled.");
            }
        }
    }

    private boolean canPlayerCookRecipe(Recipe recipe, Player player) {
        // Check for fuel (Firewood or Coal)
        boolean hasFuel = false;
        int firewoodQty = 0;
        int coalQty = 0;

        for (Entity itemInInventory : player.inventory) {
            if (itemInInventory instanceof OBJ_Misc) {
                OBJ_Misc miscItem = (OBJ_Misc) itemInInventory;
                if (miscItem.name != null && miscItem.name.startsWith("Firewood")) {
                    firewoodQty += miscItem.quantity;
                } else if (miscItem.name != null && miscItem.name.startsWith("Coal")) {
                    coalQty += miscItem.quantity;
                }
            }
        }
        if (firewoodQty > 0 || coalQty > 0) { // Need at least 1 of either
            hasFuel = true;
        }

        if (!hasFuel) {
            System.out.println("DEBUG Cook: No fuel (Firewood: " + firewoodQty + ", Coal: " + coalQty + ")");
            gp.ui.showMessage("You need Firewood or Coal to cook.");
            return false;
        }

        // Check for ingredients
        for (Map.Entry<String, Integer> ingredientEntry : recipe.ingredients.entrySet()) {
            String requiredIngredientBaseName = ingredientEntry.getKey();
            int requiredQty = ingredientEntry.getValue();
            int playerHasQty = 0;

            if (RecipeManager.ANY_FISH.equals(requiredIngredientBaseName)) {
                for (Entity itemInInventory : player.inventory) {
                    if (itemInInventory instanceof OBJ_Fish) { // Any fish will do
                        // If OBJ_Fish implements a quantity field (it should via OBJ_Item)
                        if (itemInInventory instanceof OBJ_Item) {
                            playerHasQty += ((OBJ_Item) itemInInventory).quantity;
                        } else {
                            playerHasQty++; // Fallback if not OBJ_Item (though it should be)
                        }
                    }
                }
            } else { // Specific ingredient
                for (Entity itemInInventory : player.inventory) {
                    if (itemInInventory instanceof OBJ_Item) {
                        OBJ_Item objItem = (OBJ_Item) itemInInventory;

                        // Normalize names: remove " crop", " food", " seed" suffix for comparison
                        String itemFullName = objItem.name;
                        String itemTypeString = objItem.getType() != null ? " " + objItem.getType().name().toLowerCase()
                                : "";
                        String currentItemBaseName = "";

                        if (itemFullName != null && !itemTypeString.isEmpty()
                                && itemFullName.toLowerCase().endsWith(itemTypeString)) {
                            currentItemBaseName = itemFullName.substring(0,
                                    itemFullName.length() - itemTypeString.length());
                        } else {
                            currentItemBaseName = itemFullName;
                        }

                        if (currentItemBaseName != null
                                && currentItemBaseName.equalsIgnoreCase(requiredIngredientBaseName)) {
                            playerHasQty += objItem.quantity;
                        }
                    }
                }
            }

            if (playerHasQty < requiredQty) {
                System.out.println("DEBUG Cook: Not enough " + requiredIngredientBaseName + ". Has: " + playerHasQty
                        + ", Needs: " + requiredQty);
                gp.ui.showMessage("You don't have enough " + requiredIngredientBaseName + ".");
                return false;
            }
        }
        return true;
    }

    private void initiateCookingProcess(Recipe recipe, Player player) {
        // 1. Deduct energy
        if (!player.tryDecreaseEnergy(10)) { // [cite: 216]
            gp.ui.showMessage("Not enough energy to cook!");
            return;
        }

        // 2. Consume ingredients
        for (Map.Entry<String, Integer> ingredientEntry : recipe.ingredients.entrySet()) {
            String requiredIngredientBaseName = ingredientEntry.getKey();
            int qtyToConsume = ingredientEntry.getValue();
            int qtyActuallyConsumed = 0;

            Iterator<Entity> invIterator = player.inventory.iterator();
            while (invIterator.hasNext() && qtyActuallyConsumed < qtyToConsume) {
                Entity itemInInventory = invIterator.next();
                int consumedFromThisStack = 0;

                if (RecipeManager.ANY_FISH.equals(requiredIngredientBaseName) && itemInInventory instanceof OBJ_Fish) {
                    OBJ_Item fishItem = (OBJ_Item) itemInInventory; // OBJ_Fish extends OBJ_Item
                    consumedFromThisStack = Math.min(qtyToConsume - qtyActuallyConsumed, fishItem.quantity);
                    fishItem.quantity -= consumedFromThisStack;
                    qtyActuallyConsumed += consumedFromThisStack;
                    if (fishItem.quantity <= 0) {
                        invIterator.remove();
                    }
                } else if (itemInInventory instanceof OBJ_Item) {
                    OBJ_Item objItem = (OBJ_Item) itemInInventory;
                    String itemFullName = objItem.name;
                    String itemTypeString = objItem.getType() != null ? " " + objItem.getType().name().toLowerCase()
                            : "";
                    String currentItemBaseName = "";

                    if (itemFullName != null && !itemTypeString.isEmpty()
                            && itemFullName.toLowerCase().endsWith(itemTypeString)) {
                        currentItemBaseName = itemFullName.substring(0,
                                itemFullName.length() - itemTypeString.length());
                    } else {
                        currentItemBaseName = itemFullName;
                    }

                    if (currentItemBaseName != null
                            && currentItemBaseName.equalsIgnoreCase(requiredIngredientBaseName)) {
                        consumedFromThisStack = Math.min(qtyToConsume - qtyActuallyConsumed, objItem.quantity);
                        objItem.quantity -= consumedFromThisStack;
                        qtyActuallyConsumed += consumedFromThisStack;

                        if (objItem.quantity <= 0) {
                            invIterator.remove();
                        }
                    }
                }
            }
            if (qtyActuallyConsumed < qtyToConsume) {
                System.err.println("CRITICAL COOKING ERROR: Failed to consume enough " + requiredIngredientBaseName +
                        ". Had: " + qtyActuallyConsumed + ", Needed: " + qtyToConsume
                        + ". Inventory might be corrupted or check logic failed.");
                gp.ui.showMessage(
                        "Error: Could not find enough " + requiredIngredientBaseName + " during consumption!");
                player.increaseEnergy(10); // Refund energy
                return;
            }
        }

        // 3. Consume fuel [cite: 189, 213]
        boolean fuelConsumed = false;
        // Prioritize Coal (1 coal = 2 items), then Firewood (1 firewood = 1 item)
        // For simplicity, one cooking action consumes one fuel item. More complex logic
        // could be added if needed.

        Iterator<Entity> fuelIterator = player.inventory.iterator();
        Entity fuelToConsume = null;
        String fuelNameUsed = "";

        // Try to use Coal first
        while (fuelIterator.hasNext()) {
            Entity item = fuelIterator.next();
            if (item instanceof OBJ_Misc && item.name != null && item.name.startsWith("Coal")) {
                fuelToConsume = item;
                fuelNameUsed = "Coal";
                break;
            }
        }
        // If no Coal, try Firewood
        if (fuelToConsume == null) {
            fuelIterator = player.inventory.iterator(); // Reset iterator
            while (fuelIterator.hasNext()) {
                Entity item = fuelIterator.next();
                if (item instanceof OBJ_Misc && item.name != null && item.name.startsWith("Firewood")) {
                    fuelToConsume = item;
                    fuelNameUsed = "Firewood";
                    break;
                }
            }
        }

        if (fuelToConsume != null) {
            OBJ_Misc fuelObjItem = (OBJ_Misc) fuelToConsume;
            fuelObjItem.quantity--; // Consume one unit of fuel
            fuelConsumed = true;
            System.out
                    .println("DEBUG Cook: Consumed 1 unit of " + fuelNameUsed + ". Remaining: " + fuelObjItem.quantity);
            if (fuelObjItem.quantity <= 0) {
                player.inventory.remove(fuelToConsume);
                System.out.println("DEBUG Cook: Removed empty fuel stack: " + fuelNameUsed);
            }
        }

        if (!fuelConsumed) { // Should have been caught by canPlayerCookRecipe
            System.err.println(
                    "CRITICAL COOKING ERROR: No fuel consumed. This should have been caught by canPlayerCookRecipe.");
            gp.ui.showMessage("Error: No fuel to cook with!");
            player.increaseEnergy(10); // Refund energy
            return;
        }

        // 4. Start cooking process
        spakborhills.Time startTime = gp.gameClock.getTime();
        int finishHour = startTime.getHour() + 1; // Cooking takes 1 hour [cite: 215]
        int finishMinute = startTime.getMinute();
        int finishDay = startTime.getDay();
        if (finishHour >= 24) { // Handle crossing midnight
            finishHour -= 24;
            finishDay += 1;
        }
        player.activeCookingProcesses.add(new ActiveCookingProcess(recipe.outputFoodName, recipe.outputFoodQuantity,
                finishDay, finishHour, finishMinute));
        gp.ui.showMessage(recipe.outputFoodName + " is cooking! Ready in 1 hour.");
        System.out.println("DEBUG Cook: Started cooking " + recipe.outputFoodName + ". Will finish on Day " + finishDay
                + " at " + finishHour + ":" + finishMinute);
    }

    private void sellScreenControls(int code) {
        if (gp.player.inventory.isEmpty()) {
            gp.ui.commandNumber = 0; // Or -1 to indicate no selection
            return;
        }
        int currentInventorySize = gp.player.inventory.size();
        int slotsPerRow = 6; // Example, should match UI.drawSellScreen logic if it has fixed rows
        int currentSlot = gp.ui.commandNumber;

        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            currentSlot++;
            if (currentSlot >= currentInventorySize)
                currentSlot = 0;
        } else if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            currentSlot--;
            if (currentSlot < 0)
                currentSlot = currentInventorySize - 1;
        } else if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
            currentSlot += slotsPerRow;
            if (currentSlot >= currentInventorySize) {
                // Wrap around to the same column in the first applicable row, or last item
                currentSlot = currentSlot % slotsPerRow;
                if (currentSlot >= currentInventorySize)
                    currentSlot = currentInventorySize - 1;
            }
        } else if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
            currentSlot -= slotsPerRow;
            if (currentSlot < 0) {
                // Wrap around to the same column in the last row
                int lastRowPotentialStart = ((currentInventorySize - 1) / slotsPerRow) * slotsPerRow;
                currentSlot = lastRowPotentialStart + (currentSlot % slotsPerRow + slotsPerRow) % slotsPerRow; // Ensure
                                                                                                               // positive
                                                                                                               // remainder
                if (currentSlot >= currentInventorySize)
                    currentSlot = currentInventorySize - 1; // If overshoot, go to last
                if (currentSlot < 0)
                    currentSlot = 0; // Safety for very small inventories
            }
        }

        // Final bounds check
        if (currentSlot < 0)
            currentSlot = 0;
        if (currentSlot >= currentInventorySize && currentInventorySize > 0)
            currentSlot = currentInventorySize - 1;
        else if (currentInventorySize == 0)
            currentSlot = 0;

        gp.ui.commandNumber = currentSlot;

    }

    private void handlePlayerNameInput(int keyCode, char keyChar) {
        if (keyCode == KeyEvent.VK_ENTER) {
            if (!gp.ui.playerNameInput.trim().isEmpty()) {

                gp.player.name = gp.ui.playerNameInput.trim();
                System.out.println("Player Name Set: " + gp.player.name);

                gp.gameState = gp.farmNameInputState;
                gp.ui.farmNameInput = "";

            } else {
                gp.ui.showMessage("Player name cannot be empty!");
            }
        } else if (keyCode == KeyEvent.VK_BACK_SPACE) {
            if (gp.ui.playerNameInput.length() > 0) {
                gp.ui.playerNameInput = gp.ui.playerNameInput.substring(0, gp.ui.playerNameInput.length() - 1);
            }
        } else {
            if (gp.ui.playerNameInput.length() < gp.ui.playerNameMaxLength) {
                // Check if the character is valid for a name
                if (Character.isLetterOrDigit(keyChar) || Character.isWhitespace(keyChar)) {
                    // Filter out non-printable chars or control keys that might slip through
                    // keyChar
                    if (keyChar != KeyEvent.CHAR_UNDEFINED && keyCode != KeyEvent.VK_ENTER
                            && keyCode != KeyEvent.VK_BACK_SPACE &&
                            keyCode != KeyEvent.VK_SHIFT && keyCode != KeyEvent.VK_CONTROL && keyCode != KeyEvent.VK_ALT
                            &&
                            keyCode != KeyEvent.VK_CAPS_LOCK && keyCode != KeyEvent.VK_TAB) {
                        gp.ui.playerNameInput += keyChar;
                    }
                }
            }
        }
    }

    private void handleNPCInteractionMenuInput(int code) {
        if (gp.currentInteractingNPC == null || !(gp.currentInteractingNPC instanceof NPC))
            return;
        NPC npc = (NPC) gp.currentInteractingNPC;

        ArrayList<String> options = new ArrayList<>();
        options.add("Chat");
        options.add("Give Gift");

        if (npc.isMarriageCandidate && !npc.marriedToPlayer && !npc.engaged && !gp.player.isMarried()) {
            options.add("Propose");
        }
        if (npc.isMarriageCandidate && npc.engaged && !npc.marriedToPlayer && !gp.player.isMarried()) {
            options.add("Marry");
        }
        if (npc.name.equals("Emily")) { // Add Shop option for Emily
            options.add("Shop");
        }
        options.add("Leave");
        int maxCommands = options.size();

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            gp.ui.npcMenuCommandNum--;
            if (gp.ui.npcMenuCommandNum < 0) {
                gp.ui.npcMenuCommandNum = maxCommands - 1;
            }
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            gp.ui.npcMenuCommandNum++;
            if (gp.ui.npcMenuCommandNum >= maxCommands) {
                gp.ui.npcMenuCommandNum = 0;
            }
        }
        if (code == KeyEvent.VK_ENTER) {
            enterPressed = false; // Consume enter
            String selectedOption = options.get(gp.ui.npcMenuCommandNum);

            switch (selectedOption) {
                case "Chat":
                    npc.chat();
                    break;
                case "Give Gift":
                    gp.ui.isSelectingGift = true;
                    gp.gameState = gp.giftSelectionState;
                    gp.ui.inventoryCommandNum = 0; // Reset inventory selection for gifting
                    break;
                case "Propose":
                    npc.getProposedTo();
                    break;
                case "Marry":
                    npc.getMarried();
                    break;
                case "Shop": // Handle Shop selection for Emily
                    if (npc.name.equals("Emily")) {
                        gp.gameState = gp.buyingState;
                        gp.ui.storeCommandNum = 0; // Reset store selection
                        gp.ui.showMessage("Welcome to Emily's Shop!");
                    }
                    break;
                case "Leave":
                    gp.gameState = gp.playState;
                    if (gp.gameClock != null && gp.gameClock.isPaused())
                        gp.gameClock.resumeTime();
                    break;
            }
        }
        if (code == KeyEvent.VK_ESCAPE) { // Allow Esc to leave interaction menu
            gp.gameState = gp.playState;
            if (gp.gameClock != null && gp.gameClock.isPaused())
                gp.gameClock.resumeTime();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
        if (code == KeyEvent.VK_ENTER) {
            // enterPressed is typically consumed in keyPressed for states like dialogue,
            // menu.
            // For playState, it's used for interaction, so it's reset there.
            if (gp.gameState == gp.playState || gp.gameState == gp.dialogueState ||
                    gp.gameState == gp.playerNameInputState || gp.gameState == gp.farmNameInputState ||
                    gp.gameState == gp.interactionMenuState || gp.gameState == gp.inventoryState ||
                    gp.gameState == gp.sellState || gp.gameState == gp.cookingState || gp.gameState == gp.buyingState) {
                enterPressed = false;
            }
        }
        if (code == KeyEvent.VK_E) { // For eating
            eatPressed = false;
        }
        if (code == KeyEvent.VK_I) { // For inventory toggle
            inventoryPressed = false; // Though it's consumed in keyPressed, good practice
        }
    }

    private void handleInventoryInput(int code, boolean isGifting) {
        int currentCommandNum = gp.ui.inventoryCommandNum;
        int newCommandNum = currentCommandNum;
        int maxInventoryItems = gp.player.inventory.size();

        if (maxInventoryItems == 0) { // No items to navigate
            gp.ui.inventoryCommandNum = 0; // Or -1
            if (code == KeyEvent.VK_ESCAPE || (code == KeyEvent.VK_I && !isGifting)) {
                if (isGifting) {
                    gp.gameState = gp.interactionMenuState; // Return to NPC menu
                } else {
                    gp.gameState = gp.playState;
                }
                if (gp.gameClock != null && gp.gameClock.isPaused())
                    gp.gameClock.resumeTime();
                gp.ui.isSelectingGift = false;
            }
            return;
        }
        // Assuming itemsPerRow is calculated or known (e.g., from UI's drawing logic)
        // For simplicity, let's use a fixed itemsPerRow or make it dynamic based on UI
        // constants if available
        int frameWidth_inv = gp.screenWidth - (gp.tileSize * 4); // from drawInventoryScreen
        int slotSize_inv = gp.tileSize + 10; // from drawInventoryScreen
        int slotGap_inv = 5; // from drawInventoryScreen
        int itemsPerRow = (frameWidth_inv - gp.tileSize) / (slotSize_inv + slotGap_inv);
        if (itemsPerRow <= 0)
            itemsPerRow = 1; // Avoid division by zero or negative

        int currentRow = currentCommandNum / itemsPerRow;
        int currentCol = currentCommandNum % itemsPerRow;
        int numRows = (maxInventoryItems + itemsPerRow - 1) / itemsPerRow; // Total number of rows

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            if (currentRow > 0) {
                newCommandNum = currentCommandNum - itemsPerRow;
            } else { // Wrap to last row, same column
                newCommandNum = ((numRows - 1) * itemsPerRow) + currentCol;
                if (newCommandNum >= maxInventoryItems) { // If wrapped to an invalid slot in last row
                    newCommandNum = maxInventoryItems - 1; // Go to the very last item
                }
            }
        } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            if (currentRow < numRows - 1) {
                newCommandNum = currentCommandNum + itemsPerRow;
                if (newCommandNum >= maxInventoryItems) { // If next row slot is out of bounds
                    newCommandNum = maxInventoryItems - 1; // Go to the very last item
                }
            } else { // Wrap to first row, same column
                newCommandNum = currentCol;
                if (newCommandNum >= maxInventoryItems) { // If the first row doesn't have this col
                    newCommandNum = (maxInventoryItems > 0) ? maxInventoryItems - 1 : 0; // Fallback to last item
                }
            }
        } else if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            if (currentCommandNum > 0) {
                newCommandNum = currentCommandNum - 1;
            } else { // Wrap to the last item
                newCommandNum = maxInventoryItems - 1;
            }
        } else if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            if (currentCommandNum < maxInventoryItems - 1) {
                newCommandNum = currentCommandNum + 1;
            } else { // Wrap to the first item
                newCommandNum = 0;
            }
        }
        // Ensure newCommandNum is within bounds
        if (maxInventoryItems > 0) {
            if (newCommandNum < 0) {
                newCommandNum = 0; // Should not happen with current logic but good for safety
            }
            if (newCommandNum >= maxInventoryItems) {
                newCommandNum = maxInventoryItems - 1; // Clamp to last valid index
            }
        } else {
            newCommandNum = 0; // Or -1 if no items
        }

        gp.ui.inventoryCommandNum = newCommandNum;

        if (code == KeyEvent.VK_ENTER) {
            enterPressed = false; // Consume enter
            if (isGifting) {
                NPC currentNPC = (NPC) gp.currentInteractingNPC;
                if (currentNPC != null && gp.ui.inventoryCommandNum >= 0
                        && gp.ui.inventoryCommandNum < maxInventoryItems) {
                    Entity selectedItem = gp.player.inventory.get(gp.ui.inventoryCommandNum);
                    System.out.println("DEBUG KeyHandler: Gifting item at index: " + gp.ui.inventoryCommandNum
                            + " which is: " + selectedItem.name);
                    currentNPC.receiveGift(selectedItem, gp.player); // receiveGift should handle inventory removal
                    gp.gameState = gp.interactionMenuState; // Return to NPC menu
                    gp.ui.isSelectingGift = false;
                } else if (currentNPC != null) {
                    System.out.println("DEBUG KeyHandler: Gifting attempt with invalid inventoryCommandNum: "
                            + gp.ui.inventoryCommandNum);
                    gp.gameState = gp.interactionMenuState; // Still return to NPC menu
                    gp.ui.isSelectingGift = false;
                }
            } else { // Not gifting, so it's standard inventory interaction (equip/use)
                if (gp.ui.inventoryCommandNum >= 0 && gp.ui.inventoryCommandNum < maxInventoryItems) {
                    // gp.player.selectItemAndUse(); // This method in Player handles equipping or
                    // using
                    gp.player.equipItem(gp.ui.inventoryCommandNum); // Or call equip directly
                } else {
                    System.out.println("DEBUG KeyHandler: Equip/Use attempt with invalid inventoryCommandNum: "
                            + gp.ui.inventoryCommandNum);
                }
            }
        }

        if (code == KeyEvent.VK_E && !isGifting) { // 'E' to eat from inventory screen
            if (gp.ui.inventoryCommandNum >= 0 && gp.ui.inventoryCommandNum < maxInventoryItems) {
                Entity selectedItem = gp.player.inventory.get(gp.ui.inventoryCommandNum);
                if (selectedItem instanceof Edible) {
                    System.out.println("DEBUG KeyHandler: Eating item from inventory screen: " + selectedItem.name);
                    ((Edible) selectedItem).eat(gp.player); // eat() handles consumption and inventory removal
                } else {
                    gp.ui.showMessage(selectedItem.name + " tidak bisa dimakan.");
                }
            } else {
                System.out.println("DEBUG KeyHandler: Eat attempt (inventory screen) with invalid commandNum: "
                        + gp.ui.inventoryCommandNum);
                gp.ui.showMessage("Tidak ada item valid yang dipilih untuk dimakan.");
            }
            eatPressed = false; // Consume E press
        }

        if (code == KeyEvent.VK_ESCAPE || (code == KeyEvent.VK_I && !isGifting)) { // I or ESC to close inventory
            if (isGifting) {
                gp.gameState = gp.interactionMenuState; // Return to NPC menu if was gifting
            } else {
                gp.gameState = gp.playState;
            }
            if (gp.gameClock != null && gp.gameClock.isPaused())
                gp.gameClock.resumeTime();
            gp.ui.isSelectingGift = false; // Ensure this is reset
        }
    }

    private void handleFarmNameInput(int keyCode, char keyChar) {
        if (keyCode == KeyEvent.VK_ENTER) {
            if (!gp.ui.farmNameInput.trim().isEmpty()) {
                // enterPressed = true; // This was causing immediate map load potentially
                gp.player.setFarmName(gp.ui.farmNameInput.trim());
                System.out.println("Farm Name Set: " + gp.player.getFarmName());

                // Transition to actual gameplay start
                resetCoreGameDataForNewGameAndLoadPlayerHouse(); // Helper method to encapsulate this logic
                gp.gameState = gp.playState;
                if (gp.gameClock != null && gp.gameClock.isPaused()) {
                    gp.gameClock.resumeTime();
                }

            } else {
                gp.ui.showMessage("Farm name cannot be empty!");
            }
        } else if (keyCode == KeyEvent.VK_BACK_SPACE) {
            if (gp.ui.farmNameInput.length() > 0) {
                gp.ui.farmNameInput = gp.ui.farmNameInput.substring(0, gp.ui.farmNameInput.length() - 1);
            }
        } else {
            if (gp.ui.farmNameInput.length() < gp.ui.farmNameMaxLength) {
                if (Character.isLetterOrDigit(keyChar) || Character.isWhitespace(keyChar)) {
                    if (keyChar != KeyEvent.CHAR_UNDEFINED && keyCode != KeyEvent.VK_ENTER
                            && keyCode != KeyEvent.VK_BACK_SPACE &&
                            keyCode != KeyEvent.VK_SHIFT && keyCode != KeyEvent.VK_CONTROL && keyCode != KeyEvent.VK_ALT
                            &&
                            keyCode != KeyEvent.VK_CAPS_LOCK && keyCode != KeyEvent.VK_TAB) {
                        gp.ui.farmNameInput += keyChar;
                    }
                }
            }
        }
    }

    private void resetCoreGameDataForNewGameAndLoadPlayerHouse() {
        gp.resetCoreGameDataForNewGame(); // Resets player, entities, npcs, gameClock, ui elements
        gp.loadMapbyIndex(gp.PLAYER_HOUSE_INDEX); // Load player's house map
        // Player's position within the house should be set by loadMapByIndex or
        // player.setDefaultValues
        // if there's a specific spawn point for the house.
    }
}