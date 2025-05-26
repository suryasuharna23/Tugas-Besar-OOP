package spakborhills.object;

import spakborhills.GamePanel;
import spakborhills.entity.Entity;
import spakborhills.enums.EntityType;

public class OBJ_Bed extends Entity {

    public OBJ_Bed(GamePanel gp) { // Tambahkan parameter bedType
        super(gp);// Simpan tipe ranjangnya
        type = EntityType.INTERACTIVE_OBJECT;
        name = "Single Bed"; // Nama objek menjadi dinamis, misal "Single Bed"
        collision = true; // Ranjang biasanya solid
        int tilesWide = 2; // Lebar 2 tile
        int tilesHigh = 4;
        String imagePath = "/objects/single_bed"; // Gambar default jika tipe tidak dikenal
        this.imageWidth = tilesWide * gp.tileSize;
        this.imageHeight = tilesHigh * gp.tileSize;
        down1 = setup(imagePath);
        if (down1 == null) {
            System.err.println("Gagal memuat gambar untuk: " + imagePath);
            // Anda mungkin ingin memuat gambar placeholder di sini
        }
        // Atur solidArea agar sesuai dengan ukuran visual objek
        // Biasanya, solidArea untuk objek besar mencakup keseluruhan objeknya.
        solidArea.x = 0; // Offset X dari worldX objek
        solidArea.y = 0; // Offset Y dari worldY objek
        solidArea.width = this.imageWidth;
        solidArea.height = this.imageHeight;

        // solidAreaDefaultX dan Y adalah offset dari worldX/Y ke pojok kiri atas solidArea.
        // Jika solidArea.x dan solidArea.y adalah 0, maka defaultnya juga 0.
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    @Override
    public void interact() {
        System.out.println("[OBJ_Bed] Interact method called for " + name);
        if (gp.currentMapIndex == gp.PLAYER_HOUSE_INDEX) { // PLAYER_HOUSE_INDEX harus public di GamePanel
            if (gp.player != null && !gp.player.isCurrentlySleeping()) {
                gp.player.sleep("Waktunya tidur di " + name.toLowerCase() + "...");
            }
        } else {
            gp.ui.showMessage("Ranjang ini bukan milikmu!");
        }
    }
}