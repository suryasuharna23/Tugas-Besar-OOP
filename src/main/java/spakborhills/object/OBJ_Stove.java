// In spakborhills/object/OBJ_Stove.java
package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.enums.EntityType;

public class OBJ_Stove extends Entity {
    public OBJ_Stove(GamePanel gp) {
        super(gp); // Memanggil konstruktor Entity
        type = EntityType.INTERACTIVE_OBJECT;
        name = "Stove";
        collision = true; // Kompor biasanya solid

        // Sesuaikan ukuran visual kompor sesuai deskripsi (misalnya 1x1 tile)
        // Deskripsi furnitur menyebutkan Stove berukuran 1x1
        int tilesWide = 1;
        int tilesHigh = 1;
        String imagePath = "/objects/stove"; // Pastikan Anda memiliki gambar stove.png di folder objects

        this.imageWidth = tilesWide * gp.tileSize;
        this.imageHeight = tilesHigh * gp.tileSize;

        down1 = setup(imagePath); // Memuat gambar kompor
        if (down1 == null) {
            System.err.println("Gagal memuat gambar untuk: " + imagePath + ".png");
        }

        // Atur solidArea agar sesuai dengan ukuran visual objek
        solidArea.x = 0;
        solidArea.y = 0; // Bisa disesuaikan jika ingin area interaksi berbeda dari area visual
        solidArea.width = this.imageWidth;
        solidArea.height = this.imageHeight;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    @Override
    public void interact() {
        System.out.println("[OBJ_Stove] Interact method called."); // Untuk debugging

        // Cek apakah pemain berada di peta "Player's House"
        // gp.PLAYER_HOUSE_INDEX harus public final int di GamePanel.java
        if (gp.currentMapIndex == gp.PLAYER_HOUSE_INDEX) {
            // Pemain berada di rumah, lanjutkan untuk membuka UI memasak
            if (gp.gameState == gp.playState) { // Hanya ubah state jika sedang dalam playState
                gp.selectedRecipeForCooking = null; // Reset resep yang dipilih sebelumnya
                gp.gameState = gp.cookingState;   // Ubah gameState ke mode memasak
                gp.ui.cookingCommandNum = 0;      // Reset navigasi UI memasak
                gp.ui.cookingSubState = 0;        // Reset sub-state UI memasak (0: pilih resep)

                if (gp.gameClock != null && !gp.gameClock.isPaused()) {
                    gp.gameClock.pauseTime(); // Jeda waktu game saat memasak
                }
                gp.ui.showMessage("Saatnya memasak! Apa yang akan kamu buat?");
            }
        } else {
            // Pemain tidak di rumah atau tidak berinteraksi dengan kompor yang benar
            gp.ui.showMessage("Kamu hanya bisa memasak menggunakan kompor di rumahmu.");
        }
    }
}