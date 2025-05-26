// File: Tugas-Besar-OOP/src/main/java/spakborhills/KeyHandler.java
package spakborhills;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import spakborhills.action.PlantingCommand;
import spakborhills.action.RecoverLandCommand;
import spakborhills.action.TillingCommand;
import spakborhills.action.WateringCommand;
import spakborhills.cooking.ActiveCookingProcess;
import spakborhills.cooking.Recipe;
import spakborhills.cooking.RecipeManager;
import spakborhills.entity.Entity; // Pastikan import ini ada jika Entity direferensikan
import spakborhills.entity.NPC;
import spakborhills.entity.Player;
import spakborhills.interfaces.Edible;
import spakborhills.object.OBJ_Fish;
import spakborhills.object.OBJ_Item;
import spakborhills.object.OBJ_Misc;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed, inventoryPressed, eatPressed;
    GamePanel gp;
    // GameClock gameClock; // Tidak perlu instance terpisah, akses via gp.gameClock

    public KeyHandler(GamePanel gp){
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e){}

    @Override
    public void keyPressed(KeyEvent e){
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_K) {
            gp.player.startFishing();
        }
        if (gp.gameState == gp.titleState) {

            if (gp.ui.mapSelectionState == 0) {
                if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                    gp.ui.commandNumber--;
                    if (gp.ui.commandNumber < 0) {
                        gp.ui.commandNumber = 2; // Asumsi 3 menu: 0, 1, 2
                    }
                } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                    gp.ui.commandNumber++;
                    if (gp.ui.commandNumber > 2) { // Asumsi 3 menu: 0, 1, 2
                        gp.ui.commandNumber = 0;

                    }
                } else if (code == KeyEvent.VK_ENTER) {
                    enterPressed = true;
                    //gp.gameState = gp.playerNameInputState;
                    //gp.ui.mapSelectionState = 1;
                    //gp.playMusic(0);

                }
            }

            else if (gp.ui.mapSelectionState == 1) {
                if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                    gp.ui.commandNumber--;
                    if (gp.ui.commandNumber < 0) {
                        gp.ui.commandNumber = 10; // Asumsi 3 menu: 0, 1, 2
                    }
                } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                    gp.ui.commandNumber++;
                    if (gp.ui.commandNumber > 10) { // Asumsi 3 menu: 0, 1, 2
                        gp.ui.commandNumber = 0;

                    }
                } else if (code == KeyEvent.VK_ENTER) {
                    enterPressed = true;
//                    gp.ui.mapSelectionState = 1;
//                    gp.playMusic(0);
                    //gp.playMusic(0);

                    if (gp.ui.commandNumber == 0) {
                        System.out.println("DEBUG: KeyHandler - Map Selected: Abigail's House");
                        gp.loadMapbyIndex(gp.ui.commandNumber);
                        gp.gameState = gp.playState;

                    }
                    else if (gp.ui.commandNumber == 1) {
                        System.out.println("DEBUG: KeyHandler - Map Selected: Caroline's House");
                        gp.loadMapbyIndex(gp.ui.commandNumber);
                        gp.gameState = gp.playState;
                    }

                    else if (gp.ui.commandNumber == 2) {
                        System.out.println("DEBUG: KeyHandler - Map Selected: Dasco's House");
                        gp.loadMapbyIndex(gp.ui.commandNumber);
                        gp.gameState = gp.playState;
                    }

                    else if (gp.ui.commandNumber == 3) {
                        System.out.println("DEBUG: KeyHandler - Map Selected: Mayor Tadi's House");
                        gp.loadMapbyIndex(gp.ui.commandNumber);
                        gp.gameState = gp.playState;
                    }

                    else if (gp.ui.commandNumber == 4) {
                        System.out.println("DEBUG: KeyHandler - Map Selected: Perry's House");
                        gp.loadMapbyIndex(gp.ui.commandNumber);
                        gp.gameState = gp.playState;
                    }

                    else if (gp.ui.commandNumber == 5) {
                        System.out.println("DEBUG: KeyHandler - Map Selected: Store");
                        gp.loadMapbyIndex(gp.ui.commandNumber);
                        gp.gameState = gp.playState;
                    }

                    else if (gp.ui.commandNumber == 6) {
                        System.out.println("DEBUG: KeyHandler - Map Selected: Farm");
                        gp.loadMapbyIndex(gp.ui.commandNumber);
                        gp.gameState = gp.playState;
                    }

                    else if (gp.ui.commandNumber == 7) {
                        System.out.println("DEBUG: KeyHandler - Map Selected: Forest River");
                        gp.loadMapbyIndex(gp.ui.commandNumber);
                        gp.gameState = gp.playState;
                    }

                    else if (gp.ui.commandNumber == 8) {
                        System.out.println("DEBUG: KeyHandler - Map Selected: Mountain Lake");
                        gp.loadMapbyIndex(gp.ui.commandNumber);
                        gp.gameState = gp.playState;
                    }

                    else if (gp.ui.commandNumber == 9) {
                        System.out.println("DEBUG: KeyHandler - Map Selected: Ocean");
                        gp.loadMapbyIndex(gp.ui.commandNumber);
                        gp.gameState = gp.playState;
                    }

                    else if (gp.ui.commandNumber == 10) {
                        System.out.println("DEBUG: KeyHandler - Map Selected: Player's House");
                        gp.loadMapbyIndex(gp.ui.commandNumber);
                        gp.gameState = gp.playState;
                    }
                }

            }
            else if (gp.ui.mapSelectionState == 2) {
                gp.loadMapbyIndex(10);
                gp.gameState = gp.playState;
            }
        }
        // PLAYER NAME INPUT STATE
        else if (gp.gameState == gp.playerNameInputState){
            handlePlayerNameInput(code, e.getKeyChar());

        }
        // FARM NAME INPUT STATE
        else if (gp.gameState == gp.farmNameInputState) {
            handleFarmNameInput(code, e.getKeyChar());

        }
        else if (gp.gameState == gp.interactionMenuState){
            handleNPCInteractionMenuInput(code);
        }
        // PLAY STATE
        else if (gp.gameState == gp.playState){
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP){
                upPressed = true;
            } else if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT){
                leftPressed = true;
            } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){
                downPressed = true;
            } else if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT){
                rightPressed = true;
            } else if (code == KeyEvent.VK_P){
                gp.gameState = gp.pauseState;
                if(gp.gameClock != null) gp.gameClock.pauseTime(); // Panggil metode GameClock yang benar
            } else if (code == KeyEvent.VK_ENTER){
                enterPressed = true;
            }
            else if (code == KeyEvent.VK_M) {
                gp.gameState = gp.titleState;
                gp.ui.mapSelectionState = 1;
                gp.ui.commandNumber = 0;

                if (gp.gameClock != null && !gp.gameClock.isPaused()) {
                    gp.gameClock.pauseTime();
                }
            }
            else if (code == KeyEvent.VK_E) {
                eatPressed = true; // Flag ini akan ditangani di Player.update()
            } else if (code == KeyEvent.VK_R) {
                new TillingCommand(gp.player).execute(gp);
            } else if (code == KeyEvent.VK_T) {
                new RecoverLandCommand(gp.player).execute(gp);
            } else if (code == KeyEvent.VK_G) {
                new WateringCommand(gp.player).execute(gp);
            } else if (code == KeyEvent.VK_F) {
                new PlantingCommand(gp.player).execute(gp);
            } else if (code == KeyEvent.VK_I){
                if (gp.player != null) { // Selalu baik untuk null check
                    gp.gameState = gp.inventoryState;
                    System.out.println("DEBUG: KeyHandler - GameState changed to inventoryState via 'I'.");
                }
                } else if (code == KeyEvent.VK_E) { // Tombol 'E' untuk makan item yang di-equip
                    gp.gameState = gp.eatState;
                    eatPressed = true;
                }
            if (e.getKeyCode() == java.awt.event.KeyEvent.VK_F) {
                if (gp.player != null) {
                    gp.player.startFishing();
                }
            }
            }
            else if (code == KeyEvent.VK_M){
                gp.ui.mapSelectionState = 1;
                gp.gameState = gp.titleState;
        }

        // PAUSE STATE
        else if (gp.gameState == gp.pauseState){
            if (code == KeyEvent.VK_P){
                gp.gameState = gp.playState;
                if(gp.gameClock != null) gp.gameClock.resumeTime(); // Panggil metode GameClock yang benar
            }
        }
        // DIALOGUE STATE
        else if (gp.gameState == gp.dialogueState){
            if (code == KeyEvent.VK_ENTER){
                Entity currentNpc = gp.currentInteractingNPC;
                if(currentNpc != null && currentNpc.dialogueIndex < currentNpc.dialogues.size() -1 ){
                    currentNpc.dialogueIndex++;
                    gp.ui.currentDialogue = currentNpc.dialogues.get(currentNpc.dialogueIndex);
                } else {
                    gp.gameState = gp.playState;
                    if(currentNpc != null) currentNpc.dialogueIndex = 0;
                    if(gp.gameClock != null && gp.gameClock.isPaused()){
                        gp.gameClock.resumeTime();
                    }
                }
            }
        }
        // INVENTORY STATE
        else if (gp.gameState == gp.inventoryState || gp.gameState == gp.giftSelectionState) {
            handleInventoryInput(code, gp.gameState == gp.giftSelectionState);
        }
        // SELL STATE
        else if (gp.gameState == gp.sellState) {
            if (code == KeyEvent.VK_ESCAPE) {
                gp.player.hasUsedShippingBinToday = true; // Mark bin as used for the day
                gp.gameState = gp.playState;
                if (gp.gameClock != null && gp.gameClock.isPaused()) { // Resume game clock
                    gp.gameClock.resumeTime();
                }
                if (!gp.player.itemsInShippingBinToday.isEmpty()) {
                    gp.ui.showMessage("Items in bin will be sold overnight.");
                } else {
                    gp.ui.showMessage("Shipping bin closed.");
                }
                // gp.playSE(cancelSound); // Suara jika ada
            } else if (code == KeyEvent.VK_ENTER) { // Aksi "Pindahkan ke Bin"
                if (!gp.player.inventory.isEmpty() && gp.ui.commandNumber < gp.player.inventory.size() && gp.ui.commandNumber >=0) {
                    if (gp.player.itemsInShippingBinToday.size() < 16) { // Check max 16 items in bin
                        Entity itemEntityToShip = gp.player.inventory.get(gp.ui.commandNumber);

                        // Ensure it's an OBJ_Item and has a sell price
                        if (itemEntityToShip instanceof OBJ_Item) {
                            OBJ_Item itemToShip = (OBJ_Item) itemEntityToShip;
                            if (itemToShip.getSellPrice() > 0) {
                                gp.player.itemsInShippingBinToday.add(itemToShip);
                                gp.player.inventory.remove(gp.ui.commandNumber); // Remove from player's inventory

                                gp.ui.showMessage(itemToShip.name + " moved to bin. (" + gp.player.itemsInShippingBinToday.size() + "/16)");
                                // gp.playSE(someSound); // Suara pindah item

                                // Adjust commandNum if item removed
                                if (gp.ui.commandNumber >= gp.player.inventory.size() && !gp.player.inventory.isEmpty()) {
                                    gp.ui.commandNumber = gp.player.inventory.size() - 1;
                                } else if (gp.player.inventory.isEmpty()) {
                                    gp.ui.commandNumber = 0;
                                    gp.ui.showMessage("Inventory empty. Press Esc to close bin.");
                                }
                            } else {
                                gp.ui.showMessage(itemToShip.name + " cannot be sold.");
                                // gp.playSE(errorSound);
                            }
                        } else {
                            // This case should ideally not happen if inventory only contains sellable OBJ_Items or similar
                            gp.ui.showMessage(itemEntityToShip.name + " is not a sellable item type.");
                            // gp.playSE(errorSound);
                        }
                    } else {
                        gp.ui.showMessage("Shipping bin is full (16 items max).");
                        // gp.playSE(errorSound);
                    }
                } else if (gp.player.inventory.isEmpty()){
                    gp.ui.showMessage("Inventory is empty. Nothing to ship.");
                }
            }
            // Navigation controls for sell screen
            sellScreenControls(code);
        }
        // COOKING STATE
        else if (gp.gameState == gp.cookingState) {
            handleCookingInput(code);
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
                if (gp.gameClock != null && gp.gameClock.isPaused()) gp.gameClock.resumeTime();
            }
            return;
        }

        if (gp.ui.cookingSubState == 0) { // 0: Pemilihan Resep
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.cookingCommandNum--;
                if (gp.ui.cookingCommandNum < 0) gp.ui.cookingCommandNum = availableRecipes.size() - 1;
            } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.cookingCommandNum++;
                if (gp.ui.cookingCommandNum >= availableRecipes.size()) gp.ui.cookingCommandNum = 0;
            } else if (code == KeyEvent.VK_ENTER) {
                if (!availableRecipes.isEmpty() && gp.ui.cookingCommandNum < availableRecipes.size()) {
                    gp.selectedRecipeForCooking = availableRecipes.get(gp.ui.cookingCommandNum);
                    if (canPlayerCookRecipe(gp.selectedRecipeForCooking, gp.player)) {
                        gp.ui.cookingSubState = 1; // Pindah ke konfirmasi
                        gp.ui.showMessage("Cook " + gp.selectedRecipeForCooking.outputFoodName + "? (Cost: 10 Energy, 1hr)");
                    } else {
                        gp.ui.showMessage("You lack ingredients or fuel for " + gp.selectedRecipeForCooking.outputFoodName + ".");
                        gp.selectedRecipeForCooking = null; // Kosongkan pilihan jika tidak bisa masak
                    }
                }
            } else if (code == KeyEvent.VK_ESCAPE) {
                gp.gameState = gp.playState;
                if (gp.gameClock != null && gp.gameClock.isPaused()) gp.gameClock.resumeTime();
                gp.selectedRecipeForCooking = null;
            }
        } else if (gp.ui.cookingSubState == 1) { // 1: Konfirmasi Memasak
            if (code == KeyEvent.VK_ENTER) {
                if (gp.selectedRecipeForCooking != null) {
                    initiateCookingProcess(gp.selectedRecipeForCooking, gp.player);
                    // Pesan sukses sudah diatur di initiateCookingProcess atau akan dari proses selesai
                }
                gp.gameState = gp.playState; // Kembali ke play state
                if (gp.gameClock != null && gp.gameClock.isPaused()) gp.gameClock.resumeTime();
                gp.selectedRecipeForCooking = null;
                gp.ui.cookingSubState = 0;
            } else if (code == KeyEvent.VK_ESCAPE) {
                gp.ui.cookingSubState = 0; // Kembali ke pemilihan resep
                gp.selectedRecipeForCooking = null;
                gp.ui.showMessage("Recipe selection cancelled.");
            }
        }
    }

    // In spakborhills/KeyHandler.java
    private boolean canPlayerCookRecipe(Recipe recipe, Player player) {
        // Cek Bahan Bakar (asumsi Firewood dan Coal adalah OBJ_Misc dengan quantity)
        boolean hasFuel = false;
        int firewoodQty = 0;
        int coalQty = 0;

        for (Entity itemInInventory : player.inventory) {
            if (itemInInventory instanceof OBJ_Misc) {
                OBJ_Misc miscItem = (OBJ_Misc) itemInInventory;
                if (miscItem.name != null && miscItem.name.startsWith("Firewood")) { // Nama dari OBJ_Misc adalah "Firewood misc"
                    firewoodQty += miscItem.quantity;
                } else if (miscItem.name != null && miscItem.name.startsWith("Coal")) { // Nama dari OBJ_Misc adalah "Coal misc"
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

        // Cek Bahan Resep
        for (Map.Entry<String, Integer> ingredientEntry : recipe.ingredients.entrySet()) {
            String requiredIngredientBaseName = ingredientEntry.getKey(); // Ini adalah nama dasar, mis. "Wheat"
            int requiredQty = ingredientEntry.getValue();
            int playerHasQty = 0;

            if (RecipeManager.ANY_FISH.equals(requiredIngredientBaseName)) { //
                for (Entity itemInInventory : player.inventory) {
                    if (itemInInventory instanceof OBJ_Fish) {
                        // Asumsi setiap OBJ_Fish adalah 1 unit ikan, tidak menggunakan quantity untuk tumpukan ikan sejenis
                        playerHasQty++;
                    }
                }
            } else {
                for (Entity itemInInventory : player.inventory) {
                    if (itemInInventory instanceof OBJ_Item) {
                        OBJ_Item objItem = (OBJ_Item) itemInInventory;
                        // Ekstrak nama dasar dari item di inventaris
                        // Nama item di inventaris: "NamaDasar tipedataitem" (mis. "Wheat crop")
                        String itemFullName = objItem.name;
                        String itemTypeString = " " + objItem.getType().name().toLowerCase();
                        String currentItemBaseName = "";

                        if (itemFullName != null && itemFullName.toLowerCase().endsWith(itemTypeString)) {
                            currentItemBaseName = itemFullName.substring(0, itemFullName.length() - itemTypeString.length());
                        } else {
                            currentItemBaseName = itemFullName; // Jika tidak ada akhiran tipe, anggap nama penuh adalah nama dasar
                        }

                        if (currentItemBaseName != null && currentItemBaseName.equalsIgnoreCase(requiredIngredientBaseName)) {
                            playerHasQty += objItem.quantity;
                        }
                    }
                }
            }

            if (playerHasQty < requiredQty) {
                System.out.println("DEBUG Cook: Not enough " + requiredIngredientBaseName + ". Has: " + playerHasQty + ", Needs: " + requiredQty);
                gp.ui.showMessage("You don't have enough " + requiredIngredientBaseName + ".");
                return false;
            }
        }
        return true; // Semua bahan dan bahan bakar tersedia
    }
    // In spakborhills/KeyHandler.java
    private void initiateCookingProcess(Recipe recipe, Player player) {
        // Cek energi (sudah ada dan sepertinya benar)
        if (!player.tryDecreaseEnergy(10)) { //
            gp.ui.showMessage("Not enough energy to cook!");
            return;
        }

        // Konsumsi Bahan Resep
        for (Map.Entry<String, Integer> ingredientEntry : recipe.ingredients.entrySet()) {
            String requiredIngredientBaseName = ingredientEntry.getKey();
            int qtyToConsume = ingredientEntry.getValue();
            int qtyActuallyConsumed = 0;

            Iterator<Entity> invIterator = player.inventory.iterator();
            while (invIterator.hasNext() && qtyActuallyConsumed < qtyToConsume) {
                Entity itemInInventory = invIterator.next();

                if (RecipeManager.ANY_FISH.equals(requiredIngredientBaseName) && itemInInventory instanceof OBJ_Fish) {
                    invIterator.remove(); // Hapus instance ikan
                    qtyActuallyConsumed++;
                } else if (itemInInventory instanceof OBJ_Item) {
                    OBJ_Item objItem = (OBJ_Item) itemInInventory;
                    String itemFullName = objItem.name;
                    String itemTypeString = " " + objItem.getType().name().toLowerCase();
                    String currentItemBaseName = "";

                    if (itemFullName != null && itemFullName.toLowerCase().endsWith(itemTypeString)) {
                        currentItemBaseName = itemFullName.substring(0, itemFullName.length() - itemTypeString.length());
                    } else {
                        currentItemBaseName = itemFullName;
                    }

                    if (currentItemBaseName != null && currentItemBaseName.equalsIgnoreCase(requiredIngredientBaseName)) {
                        int canConsumeFromThisStack = Math.min(qtyToConsume - qtyActuallyConsumed, objItem.quantity);

                        objItem.quantity -= canConsumeFromThisStack;
                        qtyActuallyConsumed += canConsumeFromThisStack;

                        if (objItem.quantity <= 0) {
                            invIterator.remove(); // Hapus item dari inventaris jika kuantitas habis
                        }
                    }
                }
            }

            // Jika setelah iterasi, bahan yang dikonsumsi kurang (seharusnya tidak terjadi jika canPlayerCookRecipe benar)
            if (qtyActuallyConsumed < qtyToConsume) {
                System.err.println("CRITICAL COOKING ERROR: Failed to consume enough " + requiredIngredientBaseName +
                        ". Had: " + qtyActuallyConsumed + ", Needed: " + qtyToConsume + ". Inventory might be corrupted or check logic failed.");
                gp.ui.showMessage("Error: Could not find enough " + requiredIngredientBaseName + " during consumption!");
                player.increaseEnergy(10); // Kembalikan energi karena proses gagal
                return; // Hentikan proses memasak
            }
        }

        // Konsumsi Bahan Bakar
        boolean fuelConsumed = false;
        // Prioritaskan Coal
        Iterator<Entity> fuelIterator = player.inventory.iterator();
        Entity fuelToConsume = null;
        while (fuelIterator.hasNext()) {
            Entity item = fuelIterator.next();
            if (item instanceof OBJ_Misc && item.name != null && item.name.startsWith("Coal")) {
                fuelToConsume = item;
                break;
            }
        }
        if (fuelToConsume == null) { // Jika tidak ada Coal, cari Firewood
            fuelIterator = player.inventory.iterator(); // Perlu iterator baru atau reset jika didukung
            while (fuelIterator.hasNext()) {
                Entity item = fuelIterator.next();
                if (item instanceof OBJ_Misc && item.name != null && item.name.startsWith("Firewood")) {
                    fuelToConsume = item;
                    break;
                }
            }
        }

        if (fuelToConsume != null) {
            OBJ_Misc fuelObjItem = (OBJ_Misc) fuelToConsume; // Asumsi fuel adalah OBJ_Misc
            fuelObjItem.quantity--;
            fuelConsumed = true;
            System.out.println("DEBUG Cook: Consumed 1 unit of " + fuelObjItem.name + ". Remaining: " + fuelObjItem.quantity);
            if (fuelObjItem.quantity <= 0) {
                player.inventory.remove(fuelToConsume); // Hapus dari inventory jika habis
                System.out.println("DEBUG Cook: Removed empty fuel stack: " + fuelObjItem.name);
            }
        }

        if (!fuelConsumed) {
            System.err.println("CRITICAL COOKING ERROR: No fuel consumed. This should have been caught by canPlayerCookRecipe.");
            gp.ui.showMessage("Error: No fuel to cook with!");
            player.increaseEnergy(10); // Kembalikan energi
            return;
        }
        spakborhills.Time startTime = gp.gameClock.getTime();
        int finishHour = startTime.getHour() + 1; // Durasi 1 jam
        int finishMinute = startTime.getMinute();
        int finishDay = startTime.getDay();
        if (finishHour >= 24) {
            finishHour -= 24;
            finishDay += 1;
        }
        player.activeCookingProcesses.add(new ActiveCookingProcess(recipe.outputFoodName, recipe.outputFoodQuantity, finishDay, finishHour, finishMinute));
        gp.ui.showMessage(recipe.outputFoodName + " is cooking! Ready in 1 hour.");
        System.out.println("DEBUG Cook: Started cooking " + recipe.outputFoodName);
    }
    private void sellScreenControls(int code) {
        if (gp.player.inventory.isEmpty()) {
            gp.ui.commandNumber = 0;

            return;
        }
        int currentInventorySize = gp.player.inventory.size();
        int slotsPerRow = 6; // Sesuai drawSellScreen
        int currentSlot = gp.ui.commandNumber;

        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            currentSlot++;
            if (currentSlot >= currentInventorySize) currentSlot = 0; // Wrap
        } else if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            currentSlot--;
            if (currentSlot < 0) currentSlot = currentInventorySize - 1; // Wrap
        } else if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
            currentSlot += slotsPerRow;
            if (currentSlot >= currentInventorySize) { // Jika melebihi, coba ke item terakhir atau wrap ke atas
                currentSlot = currentSlot % slotsPerRow; // Ke kolom yang sama di baris pertama
                if (currentSlot >= currentInventorySize) currentSlot = currentInventorySize -1; // Jika masih diluar
            }
        } else if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
            currentSlot -= slotsPerRow;
            if (currentSlot < 0) { // Jika melebihi ke atas, coba ke item terakhir atau wrap ke bawah
                int lastRowPotentialStart = ( (currentInventorySize-1) / slotsPerRow) * slotsPerRow;
                currentSlot = lastRowPotentialStart + (currentSlot % slotsPerRow + slotsPerRow)%slotsPerRow;
                if (currentSlot >= currentInventorySize) currentSlot = currentInventorySize -1;
                if (currentSlot < 0) currentSlot = 0; // Fallback
            }
        }

        if(currentSlot < 0) currentSlot = 0;
        if(currentSlot >= currentInventorySize && currentInventorySize > 0) currentSlot = currentInventorySize -1;
        else if (currentInventorySize == 0) currentSlot = 0;

        gp.ui.commandNumber = currentSlot;
        // gp.playSE(cursorSound);
    }

    private void handlePlayerNameInput(int keyCode, char keyChar) {
        if (keyCode == KeyEvent.VK_ENTER) {
            if (!gp.ui.playerNameInput.trim().isEmpty()) {
                // Simpan nama pemain ke objek Player
                gp.player.name = gp.ui.playerNameInput.trim(); // atau gp.player.setPlayerName(...)
                System.out.println("Player Name Set: " + gp.player.name); // Untuk Debug

                // Pindah ke state input nama farm
                gp.gameState = gp.farmNameInputState;
                gp.ui.farmNameInput = ""; // Reset input nama farm
                // enterPressed tidak perlu di-set true di sini karena transisi state langsung
            } else {
                gp.ui.showMessage("Player name cannot be empty!"); // Tampilkan pesan jika kosong
            }
        } else if (keyCode == KeyEvent.VK_BACK_SPACE) {
            if (gp.ui.playerNameInput.length() > 0) {
                gp.ui.playerNameInput = gp.ui.playerNameInput.substring(0, gp.ui.playerNameInput.length() - 1);
            }
        } else {
            if (gp.ui.playerNameInput.length() < gp.ui.playerNameMaxLength) { // Gunakan playerNameMaxLength
                // Cek karakter yang valid (mirip dengan farmNameInput)
                if (Character.isLetterOrDigit(keyChar) || Character.isWhitespace(keyChar)) {
                    if(keyChar != KeyEvent.CHAR_UNDEFINED && keyCode != KeyEvent.VK_ENTER && keyCode != KeyEvent.VK_BACK_SPACE &&
                            keyCode != KeyEvent.VK_SHIFT && keyCode != KeyEvent.VK_CONTROL && keyCode != KeyEvent.VK_ALT &&
                            keyCode != KeyEvent.VK_CAPS_LOCK && keyCode != KeyEvent.VK_TAB) {
                        gp.ui.playerNameInput += keyChar;
                    }
                }
            }
        }
    }


    private void handleNPCInteractionMenuInput(int code) {
        if (gp.currentInteractingNPC == null) return;
        NPC npc = (NPC) gp.currentInteractingNPC;

        // Buat daftar opsi yang dinamis sesuai kondisi NPC (HARUS SAMA DENGAN DI UI)
        ArrayList<String> options = new ArrayList<>();
        options.add("Chat");
        options.add("Give Gift");

        if (npc.isMarriageCandidate && !npc.marriedToPlayer && !npc.engaged && !gp.player.isMarried()) {
            options.add("Propose");
        }
        if (npc.isMarriageCandidate && npc.engaged && !npc.marriedToPlayer && !gp.player.isMarried()) {
            options.add("Marry");
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
                case "Marry": // <-- TAMBAHKAN CASE UNTUK MARRY
                    npc.getMarried();
                    break;
                case "Leave":
                    gp.gameState = gp.playState;
                    if (gp.gameClock != null && gp.gameClock.isPaused()) gp.gameClock.resumeTime();
                    break;
            }
        }
        if (code == KeyEvent.VK_ESCAPE) {
            gp.gameState = gp.playState;
            if (gp.gameClock != null && gp.gameClock.isPaused()) gp.gameClock.resumeTime();
        }

        if (code == KeyEvent.VK_E) { // Tombol untuk makan
            if (gp.player.inventory.isEmpty()) return;

            int itemIndex = gp.ui.inventoryCommandNum;
            if (itemIndex >= 0 && itemIndex < gp.player.inventory.size()) {
                Entity selectedItem = gp.player.inventory.get(itemIndex);
                if (selectedItem instanceof Edible edibleItem) { // Cek apakah item bisa dimakan
                    // Tidak perlu lagi memanggil use() yang hanya menampilkan pesan.
                    // Langsung panggil method makan.
                    edibleItem.eat(gp.player);

                    // Setelah makan, mungkin ingin menutup inventory atau tetap terbuka
                    // gp.gameState = gp.playState;
                    // if (gp.gameClock != null && gp.gameClock.isPaused()) {
                    //     gp.gameClock.resumeTime();
                    // }
                    // Atau biarkan UI yang mengatur state selanjutnya jika perlu
                } else {
                    gp.ui.showMessage(selectedItem.name + " cannot be eaten.");
                }
            }
            enterPressed = false; // Konsumsi 'E' jika dianggap sebagai "aksi"
        }
    }

    @Override
    public void keyReleased(KeyEvent e){
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP){
            upPressed = false;
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT){
            leftPressed = false;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){
            downPressed = false;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT){
            rightPressed = false;
        }

        // Reset enterPressed untuk state tertentu setelah diproses.
        // UI.drawTitleScreen akan mereset enterPressed untuk titleState.
        // GamePanel.update akan mereset enterPressed untuk farmNameInputState.
        if (code == KeyEvent.VK_ENTER){
            if(gp.gameState == gp.playState || gp.gameState == gp.dialogueState || gp.gameState == gp.playerNameInputState || gp.gameState == gp.farmNameInputState) { // Hanya reset untuk state ini di sini
                enterPressed = false;
            }
            else if (gp.gameState == gp.eatState) {
                eatPressed = false;
            }
        }
    }

    // In spakborhills/KeyHandler.java

    private void handleInventoryInput(int code, boolean isGifting) {
        int currentCommandNum = gp.ui.inventoryCommandNum;
        int newCommandNum = currentCommandNum;
        int maxInventoryItems = gp.player.inventory.size();

        if (maxInventoryItems == 0) {
            gp.ui.inventoryCommandNum = 0;
            if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_I) {
                // Kembali ke playState jika inventaris kosong dan Esc/I ditekan
                if (isGifting) {
                    gp.gameState = gp.interactionMenuState;
                } else {
                    gp.gameState = gp.playState;
                }
                if (gp.gameClock != null && gp.gameClock.isPaused()) gp.gameClock.resumeTime();
                gp.ui.isSelectingGift = false;
            }
            return; // Tidak ada navigasi atau aksi lain jika inventaris kosong
        }

        // Perhitungan itemsPerRow (pastikan ini akurat dan konsisten dengan UI.java)
        int frameWidth_inv = gp.screenWidth - (gp.tileSize * 4);
        int slotSize_inv = gp.tileSize + 10;
        int slotGap_inv = 5;
        int itemsPerRow = (frameWidth_inv - gp.tileSize) / (slotSize_inv + slotGap_inv);
        if (itemsPerRow <= 0) itemsPerRow = 1;

        int currentRow = currentCommandNum / itemsPerRow;
        int currentCol = currentCommandNum % itemsPerRow;
        // Hitung jumlah baris aktual yang terisi item
        int numRows = (maxInventoryItems + itemsPerRow - 1) / itemsPerRow;


        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            if (currentRow > 0) { // Jika tidak di baris pertama
                newCommandNum = currentCommandNum - itemsPerRow;
            } else { // Di baris pertama, wrap ke baris terakhir, kolom yang sama
                newCommandNum = ((numRows - 1) * itemsPerRow) + currentCol;
                // Jika kolom target di baris terakhir (yang mungkin pendek) tidak valid
                if (newCommandNum >= maxInventoryItems) {
                    newCommandNum = maxInventoryItems - 1; // Pindah ke item paling terakhir
                }
            }
        } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            if (currentRow < numRows - 1) { // Jika tidak di baris terakhir
                newCommandNum = currentCommandNum + itemsPerRow;
                // Jika target di baris berikutnya melebihi jumlah item (karena baris target pendek)
                if (newCommandNum >= maxInventoryItems) {
                    newCommandNum = maxInventoryItems - 1; // Pindah ke item paling terakhir
                }
            } else { // Di baris terakhir, wrap ke baris pertama, kolom yang sama
                newCommandNum = currentCol;
                // Jika kolom target di baris pertama ternyata melebihi jumlah item (inventaris sangat kecil)
                if (newCommandNum >= maxInventoryItems) {
                    // Ini seharusnya tidak terjadi jika maxInventoryItems >= 1 dan currentCol valid
                    // Jika terjadi, berarti ada sangat sedikit item, kurang dari currentCol
                    newCommandNum = (maxInventoryItems > 0) ? maxInventoryItems - 1 : 0;
                }
            }
        } else if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            if (currentCommandNum > 0) {
                newCommandNum = currentCommandNum - 1;
            } else { // currentCommandNum adalah 0 (item pertama)
                newCommandNum = maxInventoryItems - 1; // Wrap ke item terakhir
            }
        } else if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            if (currentCommandNum < maxInventoryItems - 1) {
                newCommandNum = currentCommandNum + 1;
            } else { // currentCommandNum adalah item terakhir
                newCommandNum = 0; // Wrap ke item pertama
            }
        }

        // Clamping akhir untuk memastikan newCommandNum selalu dalam batas yang valid
        // Ini seharusnya menjadi redundan jika logika di atas sudah benar, tetapi sebagai pengaman.
        if (maxInventoryItems > 0) { // Hanya lakukan jika ada item
            if (newCommandNum < 0) {
                newCommandNum = 0; // Tidak boleh kurang dari 0
            }
            if (newCommandNum >= maxInventoryItems) {
                // Jika logika navigasi sudah benar, ini seharusnya berarti wrap ke 0 atau maxInventoryItems-1.
                // Untuk mencegah "memilih di luar", kita clamp ke item terakhir atau wrap ke item pertama.
                // Berdasarkan "kembali ke item pertama" saat overshoot, 0 lebih baik.
                // Namun, logika individual KANAN/BAWAH sudah harusnya melakukan wrap ke 0.
                // Jadi, jika masih >= maxInventoryItems di sini, itu aneh. Kita clamp ke terakhir saja.
                newCommandNum = maxInventoryItems - 1;
            }
        } else { // Jika maxInventoryItems adalah 0 (inventaris kosong)
            newCommandNum = 0;
        }

        gp.ui.inventoryCommandNum = newCommandNum;
        // Hapus atau beri komentar pada System.out.println setelah debugging selesai
        System.out.println("KeyHandler: Nav current=" + currentCommandNum + " itemsPerRow=" + itemsPerRow +
                " maxItems=" + maxInventoryItems + " numRows=" + numRows +
                " -> new invCmdNum=" + gp.ui.inventoryCommandNum);


        // Logika untuk tombol ENTER (Pilih/Equip) dan E (Makan) tetap sama,
        // karena mereka menggunakan gp.ui.inventoryCommandNum yang sudah diperbarui.
        if (code == KeyEvent.VK_ENTER) {
            enterPressed = false;
            if (isGifting) {
                NPC currentNPC = (NPC) gp.currentInteractingNPC;
                // Pastikan gp.ui.inventoryCommandNum valid sebelum mengakses inventaris
                if (currentNPC != null && gp.ui.inventoryCommandNum >= 0 && gp.ui.inventoryCommandNum < maxInventoryItems) {
                    Entity selectedItem = gp.player.inventory.get(gp.ui.inventoryCommandNum);
                    System.out.println("DEBUG KeyHandler: Gifting item at index: " + gp.ui.inventoryCommandNum + " which is: " + selectedItem.name);
                    currentNPC.receiveGift(selectedItem, gp.player);
                    // Setelah memberi hadiah, kembali ke menu interaksi NPC, bukan playState langsung
                    gp.gameState = gp.interactionMenuState;
                    gp.ui.isSelectingGift = false;
                } else if (currentNPC != null) {
                    System.out.println("DEBUG KeyHandler: Gifting attempt with invalid inventoryCommandNum: " + gp.ui.inventoryCommandNum);
                    // Mungkin tampilkan pesan atau kembali ke menu NPC
                    gp.gameState = gp.interactionMenuState; // Kembali jika tidak ada item valid terpilih
                    gp.ui.isSelectingGift = false;
                }
            } else { // Bukan gifting, berarti equip/use dari inventory biasa
                // Pastikan gp.ui.inventoryCommandNum valid
                if (gp.ui.inventoryCommandNum >= 0 && gp.ui.inventoryCommandNum < maxInventoryItems) {
                    Entity selectedItemToEquip = gp.player.inventory.get(gp.ui.inventoryCommandNum);
                    System.out.println("DEBUG KeyHandler: Equipping item at index: " + gp.ui.inventoryCommandNum + " which is: " + selectedItemToEquip.name);
                    gp.player.equipItem(gp.ui.inventoryCommandNum);
                } else {
                    System.out.println("DEBUG KeyHandler: Equip attempt with invalid inventoryCommandNum: " + gp.ui.inventoryCommandNum);
                }
            }
        }

        if (code == KeyEvent.VK_E && !isGifting) {
            // Pastikan gp.ui.inventoryCommandNum valid
            if (gp.ui.inventoryCommandNum >= 0 && gp.ui.inventoryCommandNum < maxInventoryItems) {
                Entity selectedItem = gp.player.inventory.get(gp.ui.inventoryCommandNum);
                if (selectedItem instanceof Edible) {
                    System.out.println("DEBUG KeyHandler: Eating item at index: " + gp.ui.inventoryCommandNum + " which is: " + selectedItem.name);
                    ((Edible) selectedItem).eat(gp.player);
                } else {
                    gp.ui.showMessage(selectedItem.name + " tidak bisa dimakan.");
                }
            } else {
                System.out.println("DEBUG KeyHandler: Eat attempt with invalid inventoryCommandNum: " + gp.ui.inventoryCommandNum);
                gp.ui.showMessage("Tidak ada item valid yang dipilih untuk dimakan.");
            }
            eatPressed = false;
        }

        if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_I ) {
            if (isGifting) {
                gp.gameState = gp.interactionMenuState; // Kembali ke menu NPC jika sedang memilih hadiah
            } else {
                gp.gameState = gp.playState;
            }
            if (gp.gameClock != null && gp.gameClock.isPaused()) gp.gameClock.resumeTime();
            gp.ui.isSelectingGift = false;
        }
    }

    private void handleFarmNameInput(int keyCode, char keyChar) {
        if (keyCode == KeyEvent.VK_ENTER) {
            if (!gp.ui.farmNameInput.trim().isEmpty()) {
                enterPressed = true; // Flag ini akan dibaca oleh GamePanel.update()
                //gp.gameState = gp.titleState;
                gp.ui.mapSelectionState = 2;
                gp.ui.commandNumber = 0;
            } else {
                gp.ui.showMessage("Farm name cannot be empty!");
            }
        } else if (keyCode == KeyEvent.VK_BACK_SPACE) {
            if (gp.ui.farmNameInput.length() > 0) {
                gp.ui.farmNameInput = gp.ui.farmNameInput.substring(0, gp.ui.farmNameInput.length() - 1);
            }
        } else {
            if (gp.ui.farmNameInput.length() < gp.ui.farmNameMaxLength) { // farmNameMaxLength dari UI
                if (Character.isLetterOrDigit(keyChar) || Character.isWhitespace(keyChar)) {

                    // Hindari menambahkan karakter kontrol atau tombol aksi seperti Enter/Backspace sebagai karakter input
                    if(keyChar != KeyEvent.CHAR_UNDEFINED && keyCode != KeyEvent.VK_ENTER && keyCode != KeyEvent.VK_BACK_SPACE &&
                            keyCode != KeyEvent.VK_SHIFT && keyCode != KeyEvent.VK_CONTROL && keyCode != KeyEvent.VK_ALT &&
                            keyCode != KeyEvent.VK_CAPS_LOCK && keyCode != KeyEvent.VK_TAB) { // Tambahkan tombol lain yang ingin diabaikan
                        gp.ui.farmNameInput += keyChar;
                    }
                }
            }

        }

    }
}