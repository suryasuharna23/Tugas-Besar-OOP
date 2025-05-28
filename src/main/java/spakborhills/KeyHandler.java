// File: Tugas-Besar-OOP/src/main/java/spakborhills/KeyHandler.java
package spakborhills;

import java.awt.AWTKeyStroke; // Anda menambahkan ini, pastikan memang digunakan atau hapus.
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
import spakborhills.entity.Entity;
import spakborhills.entity.NPC;
import spakborhills.entity.Player;
import spakborhills.interfaces.Edible;
import spakborhills.object.OBJ_Fish;
import spakborhills.object.OBJ_Item;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed, inventoryPressed, eatPressed;
    GamePanel gp;
    // private AWTKeyStroke e; // Variabel ini sepertinya tidak terpakai dan bisa menyebabkan kebingungan. Dihapus.

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode(); // Deklarasi 'code' SATU KALI di sini

        // Tombol 'K' untuk memulai memancing, sebaiknya di dalam playState
        // if (code == KeyEvent.VK_K) { // Dipindahkan ke dalam playState
        //     gp.player.startFishing();
        // }

        if (gp.gameState == gp.titleState) {
            if (gp.ui.mapSelectionState == 0) { // Menu utama
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
                    enterPressed = true; // Ditangani oleh UI atau GamePanel
                }
            } else if (gp.ui.mapSelectionState == 1) { // Pemilihan Peta
                if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                    gp.ui.commandNumber--;
                    if (gp.ui.commandNumber < 0) {
                        gp.ui.commandNumber = gp.mapInfos.size() - 1; // Jumlah peta dinamis
                    }
                } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                    gp.ui.commandNumber++;
                    if (gp.ui.commandNumber >= gp.mapInfos.size()) { // Jumlah peta dinamis
                        gp.ui.commandNumber = 0;
                    }
                } else if (code == KeyEvent.VK_ENTER) {
                    enterPressed = true; // Ditangani oleh GamePanel untuk load map
                    if (gp.ui.commandNumber >= 0 && gp.ui.commandNumber < gp.mapInfos.size()) {
                        System.out.println("DEBUG: KeyHandler - Map Selected Index: " + gp.ui.commandNumber + " (" + gp.mapInfos.get(gp.ui.commandNumber).getMapName() + ")");
                        gp.loadMapbyIndex(gp.ui.commandNumber);
                        // gp.gameState = gp.playState; // loadMapbyIndex akan mengatur gameState
                        if (gp.gameClock != null && gp.gameClock.isPaused()) {
                            gp.gameClock.resumeTime();
                        }
                    }
                }
            } else if (gp.ui.mapSelectionState == 2) { // Setelah input nama farm
                // Seharusnya transisi ke Player's House dilakukan oleh GamePanel.update setelah enterPressed di farmNameInputState
                // Atau jika ingin langsung:
                // gp.loadMapbyIndex(gp.PLAYER_HOUSE_INDEX); // PLAYER_HOUSE_INDEX adalah indeks untuk Player's House
                // gp.gameState = gp.playState;
                // if (gp.gameClock != null && gp.gameClock.isPaused()) {
                //    gp.gameClock.resumeTime();
                // }
            }
        }
        // PLAYER NAME INPUT STATE
        else if (gp.gameState == gp.playerNameInputState) {
            handlePlayerNameInput(code, e.getKeyChar());
        }
        // FARM NAME INPUT STATE
        else if (gp.gameState == gp.farmNameInputState) {
            handleFarmNameInput(code, e.getKeyChar());
        }
        // NPC INTERACTION MENU STATE
        else if (gp.gameState == gp.interactionMenuState) {
            handleNPCInteractionMenuInput(code);
        }
        // PLAY STATE
        else if (gp.gameState == gp.playState) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) upPressed = true;
            else if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) leftPressed = true;
            else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) downPressed = true;
            else if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = true;
            else if (code == KeyEvent.VK_P) {
                gp.gameState = gp.pauseState;
                if (gp.gameClock != null) gp.gameClock.pauseTime();
            } else if (code == KeyEvent.VK_ENTER) enterPressed = true;
            else if (code == KeyEvent.VK_M) {
                gp.gameState = gp.titleState;
                gp.ui.mapSelectionState = 1; // Langsung ke pemilihan peta
                gp.ui.commandNumber = 0;
                if (gp.gameClock != null && !gp.gameClock.isPaused()) {
                    gp.gameClock.pauseTime();
                }
            } else if (code == KeyEvent.VK_E) eatPressed = true;
            else if (code == KeyEvent.VK_R) new TillingCommand(gp.player).execute(gp);
            else if (code == KeyEvent.VK_T) new RecoverLandCommand(gp.player).execute(gp);
            else if (code == KeyEvent.VK_G) new WateringCommand(gp.player).execute(gp);
            else if (code == KeyEvent.VK_F) new PlantingCommand(gp.player).execute(gp); // Tombol F untuk menanam
            else if (code == KeyEvent.VK_I) {
                if (gp.player != null) {
                    gp.gameState = gp.inventoryState;
                    System.out.println("DEBUG: KeyHandler - GameState changed to inventoryState via 'I'.");
                    // GameClock akan di-pause oleh GamePanel.update()
                }
            }
            // Tombol K untuk memulai memancing HANYA saat di playState
            else if (code == KeyEvent.VK_K) { // Sebelumnya if (e.getKeyCode() == java.awt.event.KeyEvent.VK_F)
                if (gp.player != null) {
                    gp.player.startFishing();
                }
            }
            // Tombol C untuk Cooking HANYA saat di playState
            else if (code == KeyEvent.VK_C) {
                gp.selectedRecipeForCooking = null;
                gp.gameState = gp.cookingState;
                gp.ui.cookingCommandNum = 0;
                gp.ui.cookingSubState = 0;
                gp.ui.showMessage("Welcome to the kitchen! Select a recipe.");
                // GameClock akan di-pause oleh GamePanel.update()
            }
            // else if (code == KeyEvent.VK_M){ // Ini duplikat dengan yang di atas untuk 'M'
            //     gp.ui.mapSelectionState = 1;
            //     gp.gameState = gp.titleState;
            // }

        }
        // PAUSE STATE
        else if (gp.gameState == gp.pauseState) {
            if (code == KeyEvent.VK_P) {
                gp.gameState = gp.playState;
                if (gp.gameClock != null) gp.gameClock.resumeTime();
            }
        }
        // DIALOGUE STATE
        else if (gp.gameState == gp.dialogueState) {
            if (code == KeyEvent.VK_ENTER) {
                // Logika pernikahan
                if (gp.player.justGotMarried && gp.currentInteractingNPC != null &&
                        gp.ui.currentDialogue.equals(gp.currentInteractingNPC.marriageDialogue)) {
                    // GamePanel.update akan menangani event setelah dialog ini,
                    // tapi kita perlu keluar dari dialogueState dulu.
                    gp.gameState = gp.playState; // Atau state transisi khusus
                    // gp.player.justGotMarried akan tetap true agar GamePanel bisa memprosesnya
                    if (gp.gameClock != null && gp.gameClock.isPaused()) {
                        gp.gameClock.resumeTime(); // Lanjutkan waktu setelah dialog pernikahan
                    }
                    return; // Langsung keluar agar tidak menjalankan logika dialog biasa
                }

                // Logika dialog biasa
                Entity currentNpc = gp.currentInteractingNPC;
                if (currentNpc != null && currentNpc.dialogues != null && !currentNpc.dialogues.isEmpty()) {
                    if (currentNpc.dialogueIndex < currentNpc.dialogues.size() - 1) {
                        currentNpc.dialogueIndex++;
                        gp.ui.currentDialogue = currentNpc.dialogues.get(currentNpc.dialogueIndex);
                    } else { // Dialog terakhir dari NPC ini
                        gp.gameState = gp.playState;
                        currentNpc.dialogueIndex = 0; // Reset untuk interaksi berikutnya
                        gp.currentInteractingNPC = null; // Penting: reset NPC yang sedang diajak bicara
                        gp.ui.currentDialogue = ""; // Kosongkan dialog UI
                        if (gp.gameClock != null && gp.gameClock.isPaused()) {
                            gp.gameClock.resumeTime();
                        }
                    }
                } else { // Tidak ada NPC atau dialog, kembali ke play state (fallback)
                    gp.gameState = gp.playState;
                    gp.currentInteractingNPC = null;
                    gp.ui.currentDialogue = "";
                    if (gp.gameClock != null && gp.gameClock.isPaused()) {
                        gp.gameClock.resumeTime();
                    }
                }
            }
        }
        // INVENTORY STATE / GIFT SELECTION STATE
        else if (gp.gameState == gp.inventoryState || gp.gameState == gp.giftSelectionState) {
            handleInventoryInput(code, gp.gameState == gp.giftSelectionState);
        }
        // SELL STATE
        else if (gp.gameState == gp.sellState) {
            handleSellScreenInput(code); // Buat metode terpisah jika kompleks
        }
        // COOKING STATE
        else if (gp.gameState == gp.cookingState) { // Ini adalah blok yang benar
            handleCookingInput(code);
        }
        // FISHING MINIGAME STATE  <--- Ini adalah blok yang benar dan terpisah
        else if (gp.gameState == gp.fishingMinigameState) {
            handleFishingMinigameInput(e); // Melewatkan objek KeyEvent 'e'
        }
        // Hapus blok 'else if (gp.gameState == gp.eatState)' jika eatState tidak lagi digunakan
        // atau jika logika makan sudah terintegrasi di Player.update()
    }


    // --- METODE-METODE HELPER (handlePlayerNameInput, handleFarmNameInput, dll.) ---
    // Pastikan semua metode ini didefinisikan di bawah atau di tempat yang sesuai.

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
                    // Filter tombol non-karakter tambahan yang mungkin tidak diinginkan
                    if (keyChar != KeyEvent.CHAR_UNDEFINED && keyCode != KeyEvent.VK_ENTER && keyCode != KeyEvent.VK_BACK_SPACE &&
                            !(keyCode >= KeyEvent.VK_F1 && keyCode <= KeyEvent.VK_F12) && // Abaikan F-keys
                            keyCode != KeyEvent.VK_SHIFT && keyCode != KeyEvent.VK_CONTROL && keyCode != KeyEvent.VK_ALT &&
                            keyCode != KeyEvent.VK_CAPS_LOCK && keyCode != KeyEvent.VK_TAB && keyCode != KeyEvent.VK_ESCAPE &&
                            keyCode != KeyEvent.VK_PAUSE && keyCode != KeyEvent.VK_PRINTSCREEN && keyCode != KeyEvent.VK_INSERT &&
                            keyCode != KeyEvent.VK_DELETE && keyCode != KeyEvent.VK_HOME && keyCode != KeyEvent.VK_END &&
                            keyCode != KeyEvent.VK_PAGE_UP && keyCode != KeyEvent.VK_PAGE_DOWN &&
                            // Biarkan tombol arah tidak menambahkan karakter, mereka untuk navigasi di state lain
                            keyCode != KeyEvent.VK_UP && keyCode != KeyEvent.VK_DOWN && keyCode != KeyEvent.VK_LEFT && keyCode != KeyEvent.VK_RIGHT
                    ) {
                        gp.ui.playerNameInput += keyChar;
                    }
                }
            }
        }
    }

    private void handleFarmNameInput(int keyCode, char keyChar) {
        if (keyCode == KeyEvent.VK_ENTER) {
            if (!gp.ui.farmNameInput.trim().isEmpty()) {
                enterPressed = true; // Flag ini akan dibaca oleh GamePanel.update() untuk transisi
                gp.player.setFarmName(gp.ui.farmNameInput.trim());
                System.out.println("Farm Name Confirmed: " + gp.player.getFarmName());
                // GamePanel.update() akan mengubah gameState ke titleState dan mapSelectionState = 2
                // yang kemudian akan memicu loadMapbyIndex(PLAYER_HOUSE_INDEX) di titleState.
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
                    if (keyChar != KeyEvent.CHAR_UNDEFINED && keyCode != KeyEvent.VK_ENTER && keyCode != KeyEvent.VK_BACK_SPACE &&
                            !(keyCode >= KeyEvent.VK_F1 && keyCode <= KeyEvent.VK_F12) &&
                            keyCode != KeyEvent.VK_SHIFT && keyCode != KeyEvent.VK_CONTROL && keyCode != KeyEvent.VK_ALT &&
                            keyCode != KeyEvent.VK_CAPS_LOCK && keyCode != KeyEvent.VK_TAB && keyCode != KeyEvent.VK_ESCAPE &&
                            keyCode != KeyEvent.VK_PAUSE && keyCode != KeyEvent.VK_PRINTSCREEN && keyCode != KeyEvent.VK_INSERT &&
                            keyCode != KeyEvent.VK_DELETE && keyCode != KeyEvent.VK_HOME && keyCode != KeyEvent.VK_END &&
                            keyCode != KeyEvent.VK_PAGE_UP && keyCode != KeyEvent.VK_PAGE_DOWN &&
                            keyCode != KeyEvent.VK_UP && keyCode != KeyEvent.VK_DOWN && keyCode != KeyEvent.VK_LEFT && keyCode != KeyEvent.VK_RIGHT
                    ) {
                        gp.ui.farmNameInput += keyChar;
                    }
                }
            }
        }
    }

    private void handleNPCInteractionMenuInput(int code) { // 'code' sudah merupakan keyCode
        if (gp.currentInteractingNPC == null) return;
        NPC npc = (NPC) gp.currentInteractingNPC;
        ArrayList<String> options = new ArrayList<>();
        options.add("Chat");
        options.add("Give Gift");
        if (npc.isMarriageCandidate && !npc.marriedToPlayer && !npc.engaged && !gp.player.isMarried()) options.add("Propose");
        if (npc.isMarriageCandidate && npc.engaged && !npc.marriedToPlayer && !gp.player.isMarried()) options.add("Marry");
        options.add("Leave");
        int maxCommands = options.size();

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            gp.ui.npcMenuCommandNum = (gp.ui.npcMenuCommandNum - 1 + maxCommands) % maxCommands;
        } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            gp.ui.npcMenuCommandNum = (gp.ui.npcMenuCommandNum + 1) % maxCommands;
        } else if (code == KeyEvent.VK_ENTER) {
            // enterPressed = false; // Tidak perlu di-set false di sini, karena aksi langsung
            String selectedOption = options.get(gp.ui.npcMenuCommandNum);
            switch (selectedOption) {
                case "Chat": npc.chat(); break;
                case "Give Gift":
                    gp.ui.isSelectingGift = true;
                    gp.gameState = gp.giftSelectionState;
                    gp.ui.inventoryCommandNum = 0; // Reset kursor inventory untuk hadiah
                    break;
                case "Propose": npc.getProposedTo(); break;
                case "Marry": npc.getMarried(); break;
                case "Leave":
                    gp.gameState = gp.playState;
                    if (gp.gameClock != null && gp.gameClock.isPaused()) gp.gameClock.resumeTime();
                    gp.currentInteractingNPC = null; // Penting untuk mereset NPC yang diajak bicara
                    break;
            }
        } else if (code == KeyEvent.VK_ESCAPE) {
            gp.gameState = gp.playState;
            if (gp.gameClock != null && gp.gameClock.isPaused()) gp.gameClock.resumeTime();
            gp.currentInteractingNPC = null; // Penting untuk mereset
        }
        // Tombol E untuk makan tidak relevan di menu interaksi NPC, jadi dihapus dari sini.
    }

    private void handleInventoryInput(int code, boolean isGifting) { // 'code' sudah merupakan keyCode
        int maxInventoryItems = gp.player.inventory.size();
        if (maxInventoryItems == 0) {
            if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_I || (code == KeyEvent.VK_E && !isGifting)) {
                gp.gameState = isGifting ? gp.interactionMenuState : gp.playState;
                if (gp.gameClock != null && gp.gameClock.isPaused()) gp.gameClock.resumeTime();
                gp.ui.isSelectingGift = false;
            }
            return;
        }

        // Navigasi inventory (asumsi linear, sesuaikan jika UI grid)
        int slotsPerRow = (gp.screenWidth - (gp.tileSize * 4) - gp.tileSize) / (gp.tileSize + 10 + 5);
        slotsPerRow = Math.max(1, slotsPerRow); // Sama seperti di UI.drawInventoryScreen

        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            gp.ui.inventoryCommandNum++;
            if (gp.ui.inventoryCommandNum >= maxInventoryItems) gp.ui.inventoryCommandNum = 0;
        } else if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            gp.ui.inventoryCommandNum--;
            if (gp.ui.inventoryCommandNum < 0) gp.ui.inventoryCommandNum = maxInventoryItems - 1;
        } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            if (gp.ui.inventoryCommandNum + slotsPerRow < maxInventoryItems) {
                gp.ui.inventoryCommandNum += slotsPerRow;
            } else {
                int currentCol = gp.ui.inventoryCommandNum % slotsPerRow;
                // Coba ke item terakhir di kolom yg sama jika ada, atau ke item terakhir saja
                int targetIndex = (( (maxInventoryItems -1) / slotsPerRow ) * slotsPerRow) + currentCol;
                gp.ui.inventoryCommandNum = Math.min(targetIndex, maxInventoryItems -1);
                if (gp.ui.inventoryCommandNum < currentCol && maxInventoryItems > 0) { // Jika targetIndex tidak valid, coba ke item terakhir
                    gp.ui.inventoryCommandNum = maxInventoryItems -1;
                }
            }
        } else if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            if (gp.ui.inventoryCommandNum - slotsPerRow >= 0) {
                gp.ui.inventoryCommandNum -= slotsPerRow;
            } else { // Jika di baris atas, pindah ke item pertama di kolom yg sama (atau item pertama jika kolom tidak ada)
                gp.ui.inventoryCommandNum = gp.ui.inventoryCommandNum % slotsPerRow;
                if (gp.ui.inventoryCommandNum >= maxInventoryItems && maxInventoryItems > 0) gp.ui.inventoryCommandNum = 0;
            }
        }
        // Pastikan commandNum selalu valid setelah navigasi
        if (gp.ui.inventoryCommandNum < 0) gp.ui.inventoryCommandNum = 0;
        if (gp.ui.inventoryCommandNum >= maxInventoryItems && maxInventoryItems > 0) gp.ui.inventoryCommandNum = maxInventoryItems - 1;


        if (code == KeyEvent.VK_ENTER) {
            // enterPressed = false; // Tidak perlu
            if (gp.ui.inventoryCommandNum >= 0 && gp.ui.inventoryCommandNum < maxInventoryItems) {
                if (isGifting) {
                    NPC currentNPC = (NPC) gp.currentInteractingNPC;
                    if (currentNPC != null) {
                        Entity selectedItem = gp.player.inventory.get(gp.ui.inventoryCommandNum);
                        currentNPC.receiveGift(selectedItem, gp.player);
                        gp.gameState = gp.interactionMenuState; // Kembali ke menu NPC
                        gp.ui.isSelectingGift = false; // Reset flag gifting
                    }
                } else { // Aksi equip/use dari inventory
                    gp.player.equipItem(gp.ui.inventoryCommandNum);
                }
            }
        } else if (code == KeyEvent.VK_E && !isGifting) { // Tombol E untuk makan dari inventory
            if (gp.ui.inventoryCommandNum >= 0 && gp.ui.inventoryCommandNum < maxInventoryItems) {
                Entity selectedItem = gp.player.inventory.get(gp.ui.inventoryCommandNum);
                if (selectedItem instanceof Edible edibleItem) {
                    edibleItem.eat(gp.player); // Metode eat akan mengkonsumsi item
                } else {
                    gp.ui.showMessage(selectedItem.name + " cannot be eaten.");
                }
            }
        } else if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_I) {
            gp.gameState = isGifting ? gp.interactionMenuState : gp.playState;
            if (gp.gameClock != null && gp.gameClock.isPaused()) gp.gameClock.resumeTime();
            gp.ui.isSelectingGift = false;
        }
    }

    private void handleCookingInput(int code) { // 'code' adalah keyCode
        List<Recipe> availableRecipes = RecipeManager.getAllRecipes().stream()
                .filter(r -> Boolean.TRUE.equals(gp.player.recipeUnlockStatus.get(r.recipeId)))
                .collect(Collectors.toList());

        if (availableRecipes.isEmpty() && gp.ui.cookingSubState == 0) {
            if (code == KeyEvent.VK_ESCAPE) {
                gp.gameState = gp.playState;
                if (gp.gameClock != null && gp.gameClock.isPaused()) gp.gameClock.resumeTime();
                gp.selectedRecipeForCooking = null;
            }
            // Tidak perlu gp.ui.showMessage di sini karena UI.drawCookingScreen sudah menangani
            return;
        }

        if (gp.ui.cookingSubState == 0) { // 0: Pemilihan Resep
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.cookingCommandNum--;
                if (gp.ui.cookingCommandNum < 0) gp.ui.cookingCommandNum = availableRecipes.isEmpty() ? 0 : availableRecipes.size() - 1;
            } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.cookingCommandNum++;
                if (gp.ui.cookingCommandNum >= (availableRecipes.isEmpty() ? 0 : availableRecipes.size())) gp.ui.cookingCommandNum = 0;
            } else if (code == KeyEvent.VK_ENTER) {
                if (!availableRecipes.isEmpty() && gp.ui.cookingCommandNum >= 0 && gp.ui.cookingCommandNum < availableRecipes.size()) {
                    gp.selectedRecipeForCooking = availableRecipes.get(gp.ui.cookingCommandNum);
                    if (canPlayerCookRecipe(gp.selectedRecipeForCooking, gp.player)) {
                        gp.ui.cookingSubState = 1; // Pindah ke konfirmasi
                        // Pesan konfirmasi akan digambar oleh UI
                    } else {
                        gp.ui.showMessage("You lack ingredients or fuel for " + gp.selectedRecipeForCooking.outputFoodName + ".");
                        gp.selectedRecipeForCooking = null;
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
                }
                gp.gameState = gp.playState;
                if (gp.gameClock != null && gp.gameClock.isPaused()) gp.gameClock.resumeTime();
                gp.selectedRecipeForCooking = null;
                gp.ui.cookingSubState = 0; // Reset substate
            } else if (code == KeyEvent.VK_ESCAPE) {
                gp.ui.cookingSubState = 0; // Kembali ke pemilihan resep
                gp.selectedRecipeForCooking = null;
                // gp.ui.showMessage("Recipe selection cancelled."); // Pesan bisa di UI
            }
        }
    }

    private void handleFishingMinigameInput(KeyEvent e) { // Menerima objek KeyEvent e
        int keyCode = e.getKeyCode(); // Ambil keyCode dari e
        char keyChar = e.getKeyChar(); // Ambil keyChar dari e (untuk input angka)
        Player player = gp.player;

        if (player == null) {
            System.err.println("[KeyHandler.handleFishingMinigameInput] CRITICAL: Player is null! Returning to playState.");
            gp.gameState = gp.playState;
            if(gp.gameClock != null && gp.gameClock.isPaused()) gp.gameClock.resumeTime();
            return;
        }
        // Jika bukan ESCAPE, dan ikan target null, ini kondisi aneh, akhiri minigame.
        if (keyCode != KeyEvent.VK_ESCAPE && player.fishToCatchInMinigame == null) {
            System.out.println("[KeyHandler.handleFishingMinigameInput] WARNING: fishToCatchInMinigame is null (not escaping). Ending minigame.");
            player.endFishingMinigame(false); // Panggil metode di Player untuk mengakhiri (gagal)
            return;
        }

        if (keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_9) {
            if (player.fishingPlayerInput.length() < 4) { // Batasi panjang input (misal 4 digit)
                player.fishingPlayerInput += keyChar; // Gunakan keyChar
                player.fishingFeedbackMessage = ""; // Hapus feedback lama saat input baru
            }
        } else if (keyCode == KeyEvent.VK_BACK_SPACE) {
            if (player.fishingPlayerInput.length() > 0) {
                player.fishingPlayerInput = player.fishingPlayerInput.substring(0, player.fishingPlayerInput.length() - 1);
            }
        } else if (keyCode == KeyEvent.VK_ENTER) {
            if (!player.fishingPlayerInput.isEmpty()) {
                try {
                    int guessedNumber = Integer.parseInt(player.fishingPlayerInput);
                    player.processFishingAttempt(guessedNumber); // Panggil metode di Player
                } catch (NumberFormatException nfe) {
                    player.fishingFeedbackMessage = "Input angka tidak valid!";
                }
                player.fishingPlayerInput = ""; // Reset input setelah mencoba
            } else {
                player.fishingFeedbackMessage = "Masukkan angka terlebih dahulu!";
            }
        } else if (keyCode == KeyEvent.VK_ESCAPE) {
            // Pesan menyerah akan dihandle oleh player.endFishingMinigame() atau UI.draw()
            player.endFishingMinigame(false); // Panggil metode di Player untuk mengakhiri (gagal)
        }
    }

    private void handleSellScreenInput(int code) {
        if (code == KeyEvent.VK_ESCAPE) {
            gp.player.hasUsedShippingBinToday = true;
            gp.gameState = gp.playState;
            if (gp.gameClock != null && gp.gameClock.isPaused()) {
                gp.gameClock.resumeTime();
            }
            gp.ui.showMessage(!gp.player.itemsInShippingBinToday.isEmpty() ? "Items in bin will be sold overnight." : "Shipping bin closed.");
        } else if (code == KeyEvent.VK_ENTER) {
            if (!gp.player.inventory.isEmpty() && gp.ui.commandNumber < gp.player.inventory.size() && gp.ui.commandNumber >=0) {
                if (gp.player.itemsInShippingBinToday.size() < 16) {
                    Entity itemEntityToShip = gp.player.inventory.get(gp.ui.commandNumber);
                    if (itemEntityToShip instanceof OBJ_Item itemToShip) { // Pattern variable
                        if (itemToShip.getSellPrice() > 0) {
                            gp.player.itemsInShippingBinToday.add(itemToShip);
                            gp.player.inventory.remove(gp.ui.commandNumber);
                            gp.ui.showMessage(itemToShip.getName() + " moved to bin. (" + gp.player.itemsInShippingBinToday.size() + "/16)");
                            if (gp.ui.commandNumber >= gp.player.inventory.size() && !gp.player.inventory.isEmpty()) {
                                gp.ui.commandNumber = gp.player.inventory.size() - 1;
                            } else if (gp.player.inventory.isEmpty()) {
                                gp.ui.commandNumber = 0;
                                // gp.ui.showMessage("Inventory empty. Press Esc to close bin."); // Pesan ini mungkin berlebihan
                            }
                        } else {
                            gp.ui.showMessage(itemToShip.getName() + " cannot be sold.");
                        }
                    } else {
                        gp.ui.showMessage(itemEntityToShip.name + " is not a sellable item type.");
                    }
                } else {
                    gp.ui.showMessage("Shipping bin is full (16 items max).");
                }
            } else if (gp.player.inventory.isEmpty()){
                gp.ui.showMessage("Inventory is empty. Nothing to ship.");
            }
        }
        sellScreenControls(code); // Navigasi
    }


    // Metode canPlayerCookRecipe dan initiateCookingProcess tetap sama seperti sebelumnya
    private boolean canPlayerCookRecipe(Recipe recipe, Player player) {
        for (Map.Entry<String, Integer> ingredientEntry : recipe.ingredients.entrySet()) {
            String itemName = ingredientEntry.getKey();
            int requiredQty = ingredientEntry.getValue();
            long playerHasQty = 0;
            if (RecipeManager.ANY_FISH.equals(itemName)) {
                playerHasQty = player.inventory.stream().filter(item -> item instanceof OBJ_Fish).count();
            } else {
                String lowerItemName = itemName.toLowerCase();
                playerHasQty = player.inventory.stream()
                        .filter(item -> item.name != null && item.name.toLowerCase().startsWith(lowerItemName))
                        .count();
            }
            if (playerHasQty < requiredQty) {
                // System.out.println("[KeyHandler.canPlayerCookRecipe] DEBUG: Bahan kurang untuk " + itemName);
                return false;
            }
        }
        boolean hasFuel = player.inventory.stream().anyMatch(item ->
                item.name != null && (item.name.startsWith("Firewood") || item.name.startsWith("Coal")));
        if (!hasFuel) {
            // System.out.println("[KeyHandler.canPlayerCookRecipe] DEBUG: Tidak ada bahan bakar.");
            return false;
        }
        return true;
    }

    private void initiateCookingProcess(Recipe recipe, Player player) {
        if (!player.tryDecreaseEnergy(10)) {
            return;
        }
        for (Map.Entry<String, Integer> ingredientEntry : recipe.ingredients.entrySet()) {
            String itemName = ingredientEntry.getKey();
            int requiredQty = ingredientEntry.getValue();
            for (int i = 0; i < requiredQty; i++) {
                Iterator<Entity> invIterator = player.inventory.iterator();
                boolean consumedOne = false;
                while (invIterator.hasNext()) {
                    Entity item = invIterator.next();
                    boolean match = false;
                    if (RecipeManager.ANY_FISH.equals(itemName) && item instanceof OBJ_Fish) {
                        match = true;
                    } else if (item.name != null && item.name.toLowerCase().startsWith(itemName.toLowerCase())) {
                        match = true;
                    }
                    if (match) {
                        invIterator.remove();
                        consumedOne = true;
                        break;
                    }
                }
                if (!consumedOne) {
                    System.err.println("Error: Gagal mengkonsumsi bahan " + itemName);
                    player.increaseEnergy(10); // Kembalikan energi
                    return;
                }
            }
        }
        Entity fuelToConsume = player.inventory.stream()
                .filter(item -> item.name != null && item.name.startsWith("Coal"))
                .findFirst()
                .orElse(player.inventory.stream()
                        .filter(item -> item.name != null && item.name.startsWith("Firewood"))
                        .findFirst()
                        .orElse(null));
        if (fuelToConsume != null) {
            player.inventory.remove(fuelToConsume);
        } else {
            System.err.println("Error: Gagal mengkonsumsi bahan bakar.");
            player.increaseEnergy(10); // Kembalikan energi
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
        player.activeCookingProcesses.add(new ActiveCookingProcess(recipe.outputFoodName, recipe.outputFoodQuantity, finishDay, finishHour, finishMinute));
        gp.ui.showMessage(recipe.outputFoodName + " is cooking! Ready in 1 hour.");
    }

    private void sellScreenControls(int code) {
        if (gp.player.inventory.isEmpty()) {
            gp.ui.commandNumber = 0;
            return;
        }
        int currentInventorySize = gp.player.inventory.size();
        int slotsPerRow = (gp.screenWidth - (gp.tileSize * 4) - gp.tileSize) / (gp.tileSize + 8);
        slotsPerRow = Math.max(1, slotsPerRow);
        int currentSlot = gp.ui.commandNumber;

        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            currentSlot++;
            if (currentSlot >= currentInventorySize) currentSlot = 0;
        } else if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            currentSlot--;
            if (currentSlot < 0) currentSlot = currentInventorySize - 1;
        } else if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
            if (currentSlot + slotsPerRow < currentInventorySize) {
                currentSlot += slotsPerRow;
            } else {
                currentSlot = currentSlot % slotsPerRow;
                if (currentSlot >= currentInventorySize) currentSlot = currentInventorySize > 0 ? currentInventorySize - 1 : 0;
            }
        } else if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
            if (currentSlot - slotsPerRow >= 0) {
                currentSlot -= slotsPerRow;
            } else {
                int targetCol = currentSlot % slotsPerRow;
                int lastRowTotalItems = currentInventorySize % slotsPerRow;
                if (lastRowTotalItems == 0 && currentInventorySize > 0) lastRowTotalItems = slotsPerRow;
                int numRows = (int)Math.ceil((double)currentInventorySize / slotsPerRow);
                if (numRows > 0) {
                    int lastFullOrPartialRowStart = (numRows - 1) * slotsPerRow;
                    currentSlot = lastFullOrPartialRowStart + targetCol;
                    if (currentSlot >= currentInventorySize) { // Jika target di kolom itu tidak ada di baris terakhir
                        currentSlot = currentInventorySize > 0 ? currentInventorySize -1 : 0;
                    }
                } else {
                    currentSlot = 0;
                }
            }
        }
        if(currentSlot < 0) currentSlot = 0;
        if(currentSlot >= currentInventorySize && currentInventorySize > 0) currentSlot = currentInventorySize -1;
        else if (currentInventorySize == 0) currentSlot = 0;
        gp.ui.commandNumber = currentSlot;
    }


    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode(); // Deklarasi 'code' sekali
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) upPressed = false;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) leftPressed = false;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) downPressed = false;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = false;

        if (code == KeyEvent.VK_ENTER) {
            // enterPressed direset langsung di dalam handler keyPressed untuk state yang membutuhkannya
            // atau dihandle oleh GamePanel.update untuk transisi state.
            // Tidak perlu reset umum di sini lagi untuk menghindari konflik.
            if (gp.gameState == gp.playState || gp.gameState == gp.dialogueState) { // Hanya contoh state yg mungkin butuh reset di sini
                enterPressed = false;
            }
        }
        if (code == KeyEvent.VK_E && gp.gameState == gp.playState) {
            eatPressed = false;
        }
    }
}