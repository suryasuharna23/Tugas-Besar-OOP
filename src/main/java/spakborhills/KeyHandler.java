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
                        gp.ui.commandNumber = 3;
                    }
                } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                    gp.ui.commandNumber++;
                    if (gp.ui.commandNumber > 3) {
                        gp.ui.commandNumber = 0;
                    }
                } else if (code == KeyEvent.VK_ENTER) {
                    if (gp.ui.commandNumber == 0) {
                        gp.gameState = gp.playerNameInputState;
                        gp.ui.playerNameInput = "";
                        if (gp.gameClock != null && !gp.gameClock.isPaused()) {
                            gp.gameClock.pauseTime();
                        }
                    } else if (gp.ui.commandNumber == 1) {
                        gp.ui.showMessage("Load Game (Not Implemented Yet)");
                    } else if (gp.ui.commandNumber == 2) {
                        gp.gameState = gp.creditPageState;
                        enterPressed = true;
                    } else if (gp.ui.commandNumber == 3) {
                        System.exit(0);
                    }
                }
            }
            else if (gp.ui.mapSelectionState == 1) {
                if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                    gp.ui.commandNumber--;
                    if (gp.ui.commandNumber < 0) {
                        gp.ui.commandNumber = gp.mapInfos.size() - 1;
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
                        if (gp.gameClock != null && gp.gameClock.isPaused()) {
                            gp.gameClock.resumeTime();
                        }
                    }
                } else if (code == KeyEvent.VK_ESCAPE) {
                    gp.ui.mapSelectionState = 0;
                    gp.ui.commandNumber = 0;
                }
            }

        } else if (gp.gameState == gp.creditPageState) {
            if (code == KeyEvent.VK_ESCAPE) {
                enterPressed = false;
                gp.gameState = gp.titleState;
                gp.ui.commandNumber = 0;;
            }
        }

        else if (gp.gameState == gp.helpPageState) {
            if (code == KeyEvent.VK_ESCAPE) {
                //enterPressed = false;
                gp.gameState = gp.playState;
                //gp.ui.commandNumber = 0;
            }
        }

        else if (gp.gameState == gp.playerNameInputState) {
            handlePlayerNameInput(code, e.getKeyChar());
        }

        else if (gp.gameState == gp.endGameState) {
            System.out.println("[KeyHandler] DEBUG - In endGameState, key pressed: " + code);
            System.out.println("[KeyHandler] DEBUG - endGameState constant value: " + gp.endGameState);
            System.out.println("[KeyHandler] DEBUG - Current gameState value: " + gp.gameState);
            System.out.println("[KeyHandler] DEBUG - enterPressed status: " + enterPressed);

            if (code == KeyEvent.VK_ENTER) {
                System.out.println("[KeyHandler] DEBUG - ENTER key detected in endGameState");


                enterPressed = false;


                if (gp.previousGameState != -1) {
                    System.out.println("[KeyHandler] DEBUG - Returning to previousGameState: " + gp.previousGameState);
                    gp.gameState = gp.previousGameState;
                    if (gp.gameClock != null && gp.gameClock.isPaused()) {
                        gp.gameClock.resumeTime();
                        System.out.println("[KeyHandler] DEBUG - GameClock resumed");
                    }
                } else {

                    System.out.println("[KeyHandler] DEBUG - Fallback to playState");
                    gp.gameState = gp.playState;
                    if (gp.gameClock != null && gp.gameClock.isPaused()) {
                        gp.gameClock.resumeTime();
                        System.out.println("[KeyHandler] DEBUG - GameClock resumed (fallback)");
                    }
                }

                System.out.println("[KeyHandler] DEBUG - New gameState after ENTER: " + gp.gameState);

            } else if (code == KeyEvent.VK_ESCAPE) {
                System.out.println("[KeyHandler] DEBUG - ESC key detected in endGameState");

                gp.gameState = gp.titleState;
                gp.ui.mapSelectionState = 0;
                gp.ui.commandNumber = 0;
                if (gp.gameClock != null && !gp.gameClock.isPaused()) {
                    gp.gameClock.pauseTime();
                }
                System.out.println("[KeyHandler] ESC pressed in endGameState. Returning to titleState.");
            }
        }

        else if (gp.gameState == gp.farmNameInputState) {
            handleFarmNameInput(code, e.getKeyChar());
        }
        else if (gp.gameState == gp.interactionMenuState) {
            handleNPCInteractionMenuInput(code);
        }
        else if (gp.gameState == gp.playState) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) upPressed = true;
            else if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) leftPressed = true;
            else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) downPressed = true;
            else if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = true;
            else if (code == KeyEvent.VK_P) {
                gp.gameState = gp.pauseState;
                if (gp.gameClock != null)
                    gp.gameClock.pauseTime();
            } else if (code == KeyEvent.VK_ENTER) {
                enterPressed = true;
            } else if (code == KeyEvent.VK_M) {
                gp.gameState = gp.titleState;
                gp.ui.mapSelectionState = 1;
                gp.ui.commandNumber = gp.currentMapIndex != -1 ? gp.currentMapIndex : 0;
                if (gp.gameClock != null && !gp.gameClock.isPaused()) {
                    gp.gameClock.pauseTime();
                }
            }else if (code == KeyEvent.VK_K) {
                gp.player.startFishing();
            } else if (code == KeyEvent.VK_E) {
                eatPressed = true;
            } else if (code == KeyEvent.VK_R) {
                new TillingCommand(gp.player).execute(gp);
            } else if (code == KeyEvent.VK_T) {
                new RecoverLandCommand(gp.player).execute(gp);
            } else if (code == KeyEvent.VK_G) {
                new WateringCommand(gp.player).execute(gp);
            } else if (code == KeyEvent.VK_F) {
                new PlantingCommand(gp.player).execute(gp);
            } else if (code == KeyEvent.VK_I) {
                if (gp.player != null) {
                    gp.gameState = gp.inventoryState;
                    gp.ui.inventoryCommandNum = 0;
                    System.out.println("DEBUG: KeyHandler - GameState changed to inventoryState via 'I'.");
                    if (gp.gameClock != null && !gp.gameClock.isPaused()) {
                        gp.gameClock.pauseTime();
                    }
                }
            }
            else if (code == KeyEvent.VK_U) {
                System.out.println("[KeyHandler] TEST - Force triggering endgame");
                gp.previousGameState = gp.gameState;
                gp.gameState = gp.endGameState;
                if (gp.gameClock != null && !gp.gameClock.isPaused()) {
                    gp.gameClock.pauseTime();
                }
            }
            else if (code == KeyEvent.VK_H) {
                gp.gameState = gp.helpPageState;
                //enterPressed = true;


            }
        }
        else if (gp.gameState == gp.pauseState) {
            if (code == KeyEvent.VK_P) {
                gp.gameState = gp.playState;
                if (gp.gameClock != null) gp.gameClock.resumeTime();
            }
        }
        else if (gp.gameState == gp.dialogueState) {

            if (code == KeyEvent.VK_UP) {

                if (gp.ui.getDialogueCurrentPage() > 0) {
                    gp.ui.setDialogueCurrentPage(gp.ui.getDialogueCurrentPage() - 1);
                    return;
                }
            } else if (code == KeyEvent.VK_DOWN) {

                int totalPages = (int) Math
                        .ceil((double) gp.ui.getCurrentDialogueLines().size() / gp.ui.getDialogueLinesPerPage());
                if (gp.ui.getDialogueCurrentPage() < totalPages - 1) {
                    gp.ui.setDialogueCurrentPage(gp.ui.getDialogueCurrentPage() + 1);
                    return;
                }
            } else if (code == KeyEvent.VK_ENTER) {

                int totalPages = (int) Math
                        .ceil((double) gp.ui.getCurrentDialogueLines().size() / gp.ui.getDialogueLinesPerPage());
                if (gp.ui.getDialogueCurrentPage() < totalPages - 1) {

                    gp.ui.setDialogueCurrentPage(gp.ui.getDialogueCurrentPage() + 1);
                    return;
                }


                System.out.println("[KeyHandler - dialogueState] ENTER pressed. CurrentDialogue: \""
                        + gp.ui.currentDialogue + "\", CurrentInteractingNPC: "
                        + (gp.currentInteractingNPC != null ? gp.currentInteractingNPC.name : "null"));

                boolean isOneShotDialogue = false;
                if (gp.ui.currentDialogue != null) {
                    if (gp.ui.currentDialogue.contains("(HP:") ||
                            gp.ui.currentDialogue.contains("Thank you") ||
                            gp.ui.currentDialogue.contains("Makasih") ||
                            gp.ui.currentDialogue.contains("sangat suka") ||
                            (gp.currentInteractingNPC != null && gp.currentInteractingNPC instanceof NPC &&
                                    (gp.ui.currentDialogue
                                            .equals(((NPC) gp.currentInteractingNPC).proposalAcceptedDialogue) ||
                                            gp.ui.currentDialogue.startsWith(
                                                    ((NPC) gp.currentInteractingNPC).proposalRejectedDialogue_LowHearts)
                                            ||
                                            gp.ui.currentDialogue
                                                    .equals(((NPC) gp.currentInteractingNPC).marriageDialogue)
                                            ||
                                            gp.ui.currentDialogue
                                                    .equals(((NPC) gp.currentInteractingNPC).alreadyMarriedDialogue)
                                            ||
                                            gp.ui.currentDialogue
                                                    .equals(((NPC) gp.currentInteractingNPC).notEngagedDialogue)
                                            ||
                                            gp.ui.currentDialogue
                                                    .equals(((NPC) gp.currentInteractingNPC).alreadyGiftedDialogue)))) {
                        isOneShotDialogue = true;
                    }
                }

                if (gp.currentInteractingNPC != null && isOneShotDialogue) {
                    System.out.println(
                            "[KeyHandler - dialogueState] Detected one-shot NPC dialogue. Returning to interactionMenuState.");
                    if (gp.currentInteractingNPC instanceof NPC) {
                        ((NPC) gp.currentInteractingNPC).dialogueIndex = 0;
                    }
                    gp.gameState = gp.interactionMenuState;
                } else if (gp.currentInteractingNPC != null && gp.currentInteractingNPC instanceof NPC) {
                    NPC npc = (NPC) gp.currentInteractingNPC;

                    if (npc.dialogues != null && !npc.dialogues.isEmpty()) {
                        if (npc.dialogueIndex < npc.dialogues.size() - 1
                                && npc.dialogues.contains(gp.ui.currentDialogue)) {
                            npc.dialogueIndex++;
                            gp.ui.currentDialogue = npc.dialogues.get(npc.dialogueIndex);
                            System.out.println(
                                    "[KeyHandler - dialogueState] Advanced NPC chat to: " + gp.ui.currentDialogue);

                            gp.ui.resetDialoguePagination();
                        } else {
                            npc.dialogueIndex = 0;
                            System.out.println(
                                    "[KeyHandler - dialogueState] End of NPC chat. Returning to interactionMenuState.");
                            gp.gameState = gp.interactionMenuState;
                        }
                    } else {
                        npc.dialogueIndex = 0;
                        System.out.println(
                                "[KeyHandler - dialogueState] NPC has no generic dialogues. Returning to interactionMenuState.");
                        gp.gameState = gp.interactionMenuState;
                    }
                } else {
                    System.out.println(
                            "[KeyHandler - dialogueState] General dialogue. Returning to playState.");
                    gp.gameState = gp.playState;
                }


                if (gp.gameState != gp.dialogueState) {
                    if (gp.gameClock != null && gp.gameClock.isPaused()) {
                        gp.gameClock.resumeTime();
                    }
                    gp.ui.currentDialogue = "";
                    gp.ui.resetDialoguePagination();
                }
                enterPressed = false;

            } else if (code == KeyEvent.VK_ESCAPE) {
                System.out.println("[KeyHandler - dialogueState] ESC pressed. Returning to playState.");
                gp.gameState = gp.playState;
                if (gp.gameClock != null && gp.gameClock.isPaused()) {
                    gp.gameClock.resumeTime();
                }
                gp.ui.currentDialogue = "";
                gp.ui.resetDialoguePagination();
                enterPressed = false;
            }
        }
        else if (gp.gameState == gp.inventoryState || gp.gameState == gp.giftSelectionState) {
            handleInventoryInput(code, gp.gameState == gp.giftSelectionState);
        }
        else if (gp.gameState == gp.sellState) {
            handleSellScreenInput(code);
        }
        else if (gp.gameState == gp.cookingState) {
            handleCookingInput(code);
        }
        else if (gp.gameState == gp.buyingState){
            handleBuyingInput(code);
        }
        else if (gp.gameState == gp.fishingMinigameState) {
            handleFishingMinigameInput(e);
        }
    }

    private void handleSellScreenInput(int code) {
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

    private void handleFishingMinigameInput(KeyEvent e) {
        int keyCode = e.getKeyCode();
        char keyChar = e.getKeyChar();
        Player player = gp.player;

        if (player == null) {
            System.err.println("[KeyHandler] ERROR: Player is null in fishing minigame.");
            gp.gameState = gp.playState;
            if (gp.gameClock != null && gp.gameClock.isPaused()) gp.gameClock.resumeTime();
            return;
        }

        if (player.fishToCatchInMinigame == null && keyCode != KeyEvent.VK_ESCAPE) {
            System.err.println("[KeyHandler] ERROR: fishToCatchInMinigame is null during active fishing minigame input (not Esc).");
            player.endFishingMinigame(false);
            return;
        }


        if (keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_9) {
            if (player.fishingPlayerInput.length() < 4) {
                player.fishingPlayerInput += keyChar;
                player.fishingFeedbackMessage = "";
            }
        } else if (keyCode == KeyEvent.VK_BACK_SPACE) {
            if (player.fishingPlayerInput.length() > 0) {
                player.fishingPlayerInput = player.fishingPlayerInput.substring(0, player.fishingPlayerInput.length() - 1);
            }
        } else if (keyCode == KeyEvent.VK_ENTER) {
            if (!player.fishingPlayerInput.isEmpty()) {
                try {
                    int guessedNumber = Integer.parseInt(player.fishingPlayerInput);
                    player.processFishingAttempt(guessedNumber);
                } catch (NumberFormatException nfe) {
                    player.fishingFeedbackMessage = "Input angka tidak valid!";
                }
                player.fishingPlayerInput = "";
            } else {
                player.fishingFeedbackMessage = "Masukkan angka terlebih dahulu!";
            }
        } else if (keyCode == KeyEvent.VK_ESCAPE) {
            player.endFishingMinigame(false);
        }
    }

    private void handleBuyingInput(int code) {
        if (!(gp.currentInteractingNPC instanceof NPC_EMILY)) {
            gp.gameState = gp.playState;
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

        if (gp.ui.cookingSubState == 0) {
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
                        gp.ui.cookingSubState = 1;
                        gp.ui.showMessage(
                                "Cook " + gp.selectedRecipeForCooking.outputFoodName + "? (Cost: 10 Energy, 1hr)");
                    } else {
                        gp.selectedRecipeForCooking = null;
                    }
                }
            } else if (code == KeyEvent.VK_ESCAPE) {
                gp.gameState = gp.playState;
                if (gp.gameClock != null && gp.gameClock.isPaused())
                    gp.gameClock.resumeTime();
                gp.selectedRecipeForCooking = null;
            }
        } else if (gp.ui.cookingSubState == 1) {
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
                gp.ui.cookingSubState = 0;
                gp.selectedRecipeForCooking = null;
                gp.ui.showMessage("Recipe selection cancelled.");
            }
        }
    }

    private boolean canPlayerCookRecipe(Recipe recipe, Player player) {
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
        if (firewoodQty > 0 || coalQty > 0) {
            hasFuel = true;
        }

        if (!hasFuel) {
            System.out.println("DEBUG Cook: No fuel (Firewood: " + firewoodQty + ", Coal: " + coalQty + ")");
            gp.ui.showMessage("You need Firewood or Coal to cook.");
            return false;
        }

        for (Map.Entry<String, Integer> ingredientEntry : recipe.ingredients.entrySet()) {
            String requiredIngredientBaseName = ingredientEntry.getKey();
            int requiredQty = ingredientEntry.getValue();
            int playerHasQty = 0;

            if (RecipeManager.ANY_FISH.equals(requiredIngredientBaseName)) {
                for (Entity itemInInventory : player.inventory) {
                    if (itemInInventory instanceof OBJ_Fish) {
                        if (itemInInventory instanceof OBJ_Item) {
                            playerHasQty += ((OBJ_Item) itemInInventory).quantity;
                        } else {
                            playerHasQty++;
                        }
                    }
                }
            } else {
                for (Entity itemInInventory : player.inventory) {
                    if (itemInInventory instanceof OBJ_Item) {
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
        if (!player.tryDecreaseEnergy(10)) {
            gp.ui.showMessage("Not enough energy to cook!");
            return;
        }

        for (Map.Entry<String, Integer> ingredientEntry : recipe.ingredients.entrySet()) {
            String requiredIngredientBaseName = ingredientEntry.getKey();
            int qtyToConsume = ingredientEntry.getValue();
            int qtyActuallyConsumed = 0;

            Iterator<Entity> invIterator = player.inventory.iterator();
            while (invIterator.hasNext() && qtyActuallyConsumed < qtyToConsume) {
                Entity itemInInventory = invIterator.next();
                int consumedFromThisStack = 0;

                if (RecipeManager.ANY_FISH.equals(requiredIngredientBaseName) && itemInInventory instanceof OBJ_Fish) {
                    OBJ_Item fishItem = (OBJ_Item) itemInInventory;
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
                player.increaseEnergy(10);
                return;
            }
        }

        boolean fuelConsumed = false;

        Iterator<Entity> fuelIterator = player.inventory.iterator();
        Entity fuelToConsume = null;
        String fuelNameUsed = "";

        while (fuelIterator.hasNext()) {
            Entity item = fuelIterator.next();
            if (item instanceof OBJ_Misc && item.name != null && item.name.startsWith("Coal")) {
                fuelToConsume = item;
                fuelNameUsed = "Coal";
                break;
            }
        }

        if (fuelToConsume == null) {
            fuelIterator = player.inventory.iterator();
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
            fuelObjItem.quantity--;
            fuelConsumed = true;
            System.out
                    .println("DEBUG Cook: Consumed 1 unit of " + fuelNameUsed + ". Remaining: " + fuelObjItem.quantity);
            if (fuelObjItem.quantity <= 0) {
                player.inventory.remove(fuelToConsume);
                System.out.println("DEBUG Cook: Removed empty fuel stack: " + fuelNameUsed);
            }
        }

        if (!fuelConsumed) {
            System.err.println(
                    "CRITICAL COOKING ERROR: No fuel consumed. This should have been caught by canPlayerCookRecipe.");
            gp.ui.showMessage("Error: No fuel to cook with!");
            player.increaseEnergy(10);
            return;
        }

        spakborhills.Time startTime = gp.gameClock.getTime();
        int finishHour = startTime.getHour() + 1;
        int finishMinute = startTime.getMinute();
        int finishDay = startTime.getDay();
        if (finishHour >= 24) {
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
            gp.ui.commandNumber = 0;
            return;
        }
        int currentInventorySize = gp.player.inventory.size();
        int slotsPerRow = 6;
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

                currentSlot = currentSlot % slotsPerRow;
                if (currentSlot >= currentInventorySize)
                    currentSlot = currentInventorySize - 1;
            }
        } else if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
            currentSlot -= slotsPerRow;
            if (currentSlot < 0) {

                int lastRowPotentialStart = ((currentInventorySize - 1) / slotsPerRow) * slotsPerRow;
                currentSlot = lastRowPotentialStart + (currentSlot % slotsPerRow + slotsPerRow) % slotsPerRow;


                if (currentSlot >= currentInventorySize)
                    currentSlot = currentInventorySize - 1;
                if (currentSlot < 0)
                    currentSlot = 0;
            }
        }


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

                if (Character.isLetterOrDigit(keyChar) || Character.isWhitespace(keyChar)) {


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
        if (npc.name.equals("Emily")) {
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
            enterPressed = false;
            String selectedOption = options.get(gp.ui.npcMenuCommandNum);

            switch (selectedOption) {
                case "Chat":
                    npc.chat();
                    break;
                case "Give Gift":
                    gp.ui.isSelectingGift = true;
                    gp.gameState = gp.giftSelectionState;
                    gp.ui.inventoryCommandNum = 0;
                    break;
                case "Propose":
                    npc.getProposedTo();
                    break;
                case "Marry":
                    npc.getMarried();
                    break;
                case "Shop":
                    if (npc.name.equals("Emily")) {
                        gp.gameState = gp.buyingState;
                        gp.ui.storeCommandNum = 0;
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
        if (code == KeyEvent.VK_ESCAPE) {
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
            if (gp.gameState == gp.playState || gp.gameState == gp.dialogueState ||
                    gp.gameState == gp.playerNameInputState || gp.gameState == gp.farmNameInputState ||
                    gp.gameState == gp.interactionMenuState || gp.gameState == gp.inventoryState ||
                    gp.gameState == gp.sellState || gp.gameState == gp.cookingState || gp.gameState == gp.buyingState) {
                enterPressed = false;
            }
        }
        if (code == KeyEvent.VK_E) {
            eatPressed = false;
        }
        if (code == KeyEvent.VK_I) {
            inventoryPressed = false;
        }
    }

    private void handleInventoryInput(int code, boolean isGifting) {
        int maxInventoryItems = gp.player.inventory.size();
        if (maxInventoryItems == 0) {
            if (code == KeyEvent.VK_ESCAPE || (code == KeyEvent.VK_I && !isGifting)) {
                if (isGifting)
                    gp.gameState = gp.interactionMenuState;
                else
                    gp.gameState = gp.playState;
                if (gp.gameClock != null && gp.gameClock.isPaused())
                    gp.gameClock.resumeTime();
                gp.ui.isSelectingGift = false;
            }
            enterPressed = false;
            return;
        }


        int currentCommandNum = gp.ui.inventoryCommandNum;
        int newCommandNum = currentCommandNum;
        int frameWidth_inv = gp.screenWidth - (gp.tileSize * 4);
        int slotSize_inv = gp.tileSize + 10;
        int slotGap_inv = 5;
        int itemsPerRow = Math.max(1, (frameWidth_inv - gp.tileSize) / (slotSize_inv + slotGap_inv));
        int currentRow = currentCommandNum / itemsPerRow;
        int currentCol = currentCommandNum % itemsPerRow;
        int numRows = (maxInventoryItems + itemsPerRow - 1) / itemsPerRow;

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            if (currentRow > 0)
                newCommandNum = currentCommandNum - itemsPerRow;
            else {
                newCommandNum = ((numRows - 1) * itemsPerRow) + currentCol;
                if (newCommandNum >= maxInventoryItems)
                    newCommandNum = maxInventoryItems - 1;
            }
        } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            if (currentRow < numRows - 1) {
                newCommandNum = currentCommandNum + itemsPerRow;
                if (newCommandNum >= maxInventoryItems)
                    newCommandNum = maxInventoryItems - 1;
            } else {
                newCommandNum = currentCol;
                if (newCommandNum >= maxInventoryItems)
                    newCommandNum = (maxInventoryItems > 0) ? maxInventoryItems - 1 : 0;
            }
        } else if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            if (currentCommandNum > 0)
                newCommandNum = currentCommandNum - 1;
            else
                newCommandNum = maxInventoryItems - 1;
        } else if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            if (currentCommandNum < maxInventoryItems - 1)
                newCommandNum = currentCommandNum + 1;
            else
                newCommandNum = 0;
        }

        if (maxInventoryItems > 0) {
            if (newCommandNum < 0)
                newCommandNum = 0;
            if (newCommandNum >= maxInventoryItems)
                newCommandNum = maxInventoryItems - 1;
        } else
            newCommandNum = 0;
        gp.ui.inventoryCommandNum = newCommandNum;


        if (code == KeyEvent.VK_ENTER) {
            enterPressed = false;
            if (isGifting) {
                if (gp.currentInteractingNPC instanceof NPC) {
                    NPC currentNPC = (NPC) gp.currentInteractingNPC;
                    if (gp.ui.inventoryCommandNum >= 0 && gp.ui.inventoryCommandNum < maxInventoryItems) {
                        Entity selectedItem = gp.player.inventory.get(gp.ui.inventoryCommandNum);
                        System.out
                                .println("[KeyHandler] Gifting item: " + selectedItem.name + " to " + currentNPC.name);


                        gp.ui.isSelectingGift = false;


                        currentNPC.receiveGift(selectedItem, gp.player);


                        System.out.println("[KeyHandler] After receiveGift, gameState should be: " + gp.gameState
                                + ", dialogue: " + gp.ui.currentDialogue);

                    } else {
                        gp.ui.showMessage("Invalid item selection for gift.");
                        gp.gameState = gp.interactionMenuState;
                        gp.ui.isSelectingGift = false;
                    }
                } else {
                    gp.ui.showMessage("Cannot give a gift: no valid NPC selected.");
                    gp.gameState = gp.playState;
                    if (gp.gameClock != null && gp.gameClock.isPaused())
                        gp.gameClock.resumeTime();
                    gp.ui.isSelectingGift = false;
                }
            } else {
                if (gp.ui.inventoryCommandNum >= 0 && gp.ui.inventoryCommandNum < maxInventoryItems) {
                    gp.player.equipItem(gp.ui.inventoryCommandNum);
                }
            }
            enterPressed = false;
        } else if (code == KeyEvent.VK_E && !isGifting) {
            if (gp.ui.inventoryCommandNum >= 0 && gp.ui.inventoryCommandNum < maxInventoryItems) {
                Entity selectedItem = gp.player.inventory.get(gp.ui.inventoryCommandNum);
                if (selectedItem instanceof Edible)
                    ((Edible) selectedItem).eat(gp.player);
                else
                    gp.ui.showMessage(selectedItem.name + " tidak bisa dimakan.");
            } else {
                gp.ui.showMessage("Tidak ada item valid yang dipilih untuk dimakan.");
            }
            eatPressed = false;
        } else if (code == KeyEvent.VK_ESCAPE || (code == KeyEvent.VK_I && !isGifting)) {
            if (isGifting)
                gp.gameState = gp.interactionMenuState;
            else
                gp.gameState = gp.playState;
            if (gp.gameClock != null && gp.gameClock.isPaused())
                gp.gameClock.resumeTime();
            gp.ui.isSelectingGift = false;
        }
    }

    private void handleFarmNameInput(int keyCode, char keyChar) {
        if (keyCode == KeyEvent.VK_ENTER) {
            if (!gp.ui.farmNameInput.trim().isEmpty()) {

                gp.player.setFarmName(gp.ui.farmNameInput.trim());
                System.out.println("Farm Name Set: " + gp.player.getFarmName());


                resetCoreGameDataForNewGameAndLoadPlayerHouse();
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
        gp.resetCoreGameDataForNewGame();
        gp.loadMapbyIndex(gp.PLAYER_HOUSE_INDEX);



    }
}