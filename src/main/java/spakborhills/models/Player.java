package spakborhills.models;
import spakborhills.enums.*; // Impor semua enum Anda

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

public class Player {

    // --- Constants ---
    private static final int MAX_ENERGY = 100;
    private static final int MIN_ENERGY = -20;
    private static final int STARTING_GOLD = 500; // Sesuai spesifikasi? Atau 0? Cek lagi.
    private static final int STARTING_LOCATION_X = 16; // Contoh titik tengah map
    private static final int STARTING_LOCATION_Y = 16;

    // --- Attributes ---
    private final String name;
    private final Gender gender;
    private int energy;
    private final String farmName;
    private NPC partner; // Bisa null
    private int gold;
    private final Inventory inventory;
    private int locationX;
    private int locationY;
    private Location currentLocation; // Untuk World Map context

    // --- MVC Support ---
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * Konstruktor untuk membuat Player baru.
     * 
     * @param name     Nama pemain.
     * @param gender   Jenis kelamin pemain.
     * @param farmName Nama pertanian.
     */
    public Player(String name, Gender gender, String farmName) {
        this.name = Objects.requireNonNull(name, "Nama player tidak boleh null");
        this.gender = Objects.requireNonNull(gender, "Gender player tidak boleh null");
        this.farmName = Objects.requireNonNull(farmName, "Nama farm tidak boleh null");

        this.energy = MAX_ENERGY;
        this.partner = null;
        this.gold = STARTING_GOLD;
        this.inventory = new Inventory();
        initializeStartingInventory();

        this.locationX = STARTING_LOCATION_X;
        this.locationY = STARTING_LOCATION_Y;
        this.currentLocation = Location.FARM; // Selalu mulai di farm
    }

    /** Mengisi inventory dengan item awal sesuai spesifikasi. */
    private void initializeStartingInventory() {
        System.out.println("Menginisialisasi inventory awal Player...");
        // TODO: Dapatkan instance item awal (perlu cara membuat/mendapatkan item)
        // Misal, dari sebuah ItemRegistry atau Factory
        // Item hoe = ItemRegistry.get("Hoe");
        // Item wateringCan = ItemRegistry.get("Watering Can");
        // Item pickaxe = ItemRegistry.get("Pickaxe");
        // Item fishingRod = ItemRegistry.get("Fishing Rod");
        // Seed parsnipSeeds = (Seed) ItemRegistry.get("Parsnip Seeds");

        // Placeholder sementara:
        Items hoe = new Equipment("Hoe", itemType.EQUIPMENT, false);
        Items wateringCan = new Equipment("Watering Can", itemType.EQUIPMENT, false);
        Items pickaxe = new Equipment("Pickaxe", itemType.EQUIPMENT, false); // Spec tidak detail
        Items fishingRod = new Equipment("Fishing Rod", itemType.EQUIPMENT, false);
        Seeds parsnipSeeds = new Seeds("Parsnip Seeds", itemType.SEEDS, false, Season.SPRING, 5, 100, 150, 5 ); // Contoh harga

        if (hoe != null)
            inventory.addItem(hoe, 1);
        if (wateringCan != null)
            inventory.addItem(wateringCan, 1);
        if (pickaxe != null)
            inventory.addItem(pickaxe, 1);
        if (fishingRod != null)
            inventory.addItem(fishingRod, 1);
        if (parsnipSeeds != null)
            inventory.addItem(parsnipSeeds, 15);

        System.out.println("Inventory awal selesai diisi.");
    }
//
//    // --- Observer Pattern Methods ---
//    public void addPropertyChangeListener(PropertyChangeListener listener) {
//        support.addPropertyChangeListener(listener);
//    }
//
//    public void removePropertyChangeListener(PropertyChangeListener listener) {
//        support.removePropertyChangeListener(listener);
//    }
//
//    // --- Action Methods ---
//
//    /**
//     * Mencoba memindahkan player ke arah yang ditentukan di FarmMap.
//     *
//     * @param direction Arah pergerakan.
//     * @param map       FarmMap saat ini.
//     * @return true jika berhasil bergerak, false jika gagal.
//     */
//    public boolean move(Direction direction, FarmMap map) {
//        if (this.currentLocation != Location.FARM) {
//            System.out.println("Tidak bisa bergerak di peta saat tidak di Farm.");
//            return false;
//        }
//
//        int targetX = this.locationX;
//        int targetY = this.locationY;
//
//        switch (direction) {
//            case UP:
//                targetY--;
//                break;
//            case DOWN:
//                targetY++;
//                break;
//            case LEFT:
//                targetX--;
//                break;
//            case RIGHT:
//                targetX++;
//                break;
//        }
//
//        // Validasi batas peta
//        if (!map.isWithinBounds(targetX, targetY)) {
//            // System.out.println("Tidak bisa bergerak keluar batas peta."); // Bisa jadi
//            // trigger visit?
//            return false;
//        }
//
//        // Validasi tile tujuan
//        Tile targetTile = map.getTile(targetX, targetY);
//        if (targetTile != null && targetTile.isPassable()) {
//            int oldX = this.locationX;
//            int oldY = this.locationY;
//            this.locationX = targetX;
//            this.locationY = targetY;
//            // Fire event untuk perubahan lokasi
//            support.firePropertyChange("locationX", oldX, this.locationX);
//            support.firePropertyChange("locationY", oldY, this.locationY);
//            // System.out.println("Player bergerak ke (" + locationX + "," + locationY +
//            // ")"); // Log opsional
//            return true;
//        } else {
//            // System.out.println("Tidak bisa bergerak ke tile tersebut.");
//            return false;
//        }
//    }
//
//    /**
//     * Mencoba mencangkul tile target.
//     *
//     * @param targetTile Tile yang akan dicangkul.
//     * @return true jika berhasil, false jika gagal.
//     */
//    public boolean till(Tile targetTile) {
//        final int energyCost = 5; // Sesuai spec Action Tilling
//        // TODO: Cek apakah player punya Hoe di inventory
//        // boolean hasHoe = inventory.hasItem(ItemRegistry.get("Hoe"));
//        boolean hasHoe = true; // Placeholder
//
//        if (targetTile != null && targetTile.isTillable() && hasHoe && canPerformAction(energyCost)) {
//            useEnergy(energyCost);
//            targetTile.setType(TileType.TILLED);
//            // TODO: Panggil TimeManager untuk advance time 5 menit
//            // timeManager.advanceTime(5);
//            support.firePropertyChange("mapStateChanged", null, targetTile); // Beri tahu view map berubah
//            System.out.println("Berhasil mencangkul!");
//            return true;
//        }
//        System.out.println("Gagal mencangkul.");
//        return false;
//    }
//
//    /**
//     * Mencoba menanam bibit di tile target.
//     *
//     * @param seed       Bibit yang akan ditanam.
//     * @param targetTile Tile tujuan penanaman.
//     * @return true jika berhasil, false jika gagal.
//     */
//    public boolean plant(Seed seed, Tile targetTile) {
//        final int energyCost = 5; // Sesuai spec Action Planting
//        // TODO: Cek musim saat ini vs seed.getAllowedSeason()
//        boolean seasonOk = true; // Placeholder
//
//        if (targetTile != null && targetTile.isPlantable() && inventory.hasItem(seed) && seasonOk
//                && canPerformAction(energyCost)) {
//            useEnergy(energyCost);
//            inventory.removeItem(seed, 1);
//            PlantedCrop crop = new PlantedCrop(seed);
//            targetTile.setGrowingCrop(crop); // setGrowingCrop akan set type ke PLANTED
//            // TODO: Panggil TimeManager untuk advance time 5 menit
//            support.firePropertyChange("mapStateChanged", null, targetTile);
//            System.out.println("Berhasil menanam " + seed.getName());
//            return true;
//        }
//        System.out.println("Gagal menanam " + seed.getName());
//        return false;
//    }
//
//    /**
//     * Mencoba menyiram tile target.
//     *
//     * @param targetTile Tile yang akan disiram.
//     * @return true jika berhasil, false jika gagal.
//     */
//    public boolean water(Tile targetTile) {
//        final int energyCost = 5; // Sesuai spec Action Watering
//        // TODO: Cek punya Watering Can
//        boolean hasCan = true; // Placeholder
//
//        if (targetTile != null && (targetTile.getType() == TileType.TILLED || targetTile.getType() == TileType.PLANTED)
//                && hasCan && canPerformAction(energyCost)) {
//            if (!targetTile.isWatered()) { // Hanya siram jika belum basah
//                useEnergy(energyCost);
//                targetTile.setWatered(true);
//                // Update tipe tile jika perlu (opsional, tergantung rendering)
//                if (targetTile.getType() == TileType.TILLED)
//                    targetTile.setType(TileType.WATERED_TILLED);
//                if (targetTile.getType() == TileType.PLANTED)
//                    targetTile.setType(TileType.WATERED_PLANTED);
//
//                // TODO: Panggil TimeManager untuk advance time 5 menit
//                support.firePropertyChange("mapStateChanged", null, targetTile);
//                System.out.println("Berhasil menyiram!");
//                return true;
//            } else {
//                System.out.println("Tile sudah disiram.");
//                return false; // Tidak melakukan apa-apa jika sudah disiram
//            }
//        }
//        System.out.println("Gagal menyiram.");
//        return false;
//    }
//
//    /**
//     * Mencoba memanen tanaman dari tile target.
//     *
//     * @param targetTile Tile yang akan dipanen.
//     * @return true jika berhasil, false jika gagal.
//     */
//    public boolean harvest(Tile targetTile) {
//        final int energyCost = 5; // Sesuai spec Action Harvesting
//
//        if (targetTile != null && targetTile.isHarvestable() && canPerformAction(energyCost)) {
//            PlantedCrop plantedCrop = targetTile.getGrowingCrop();
//            Crop harvestedCrop = plantedCrop.harvest(); // Dapatkan hasil panen
//
//            if (harvestedCrop != null) {
//                useEnergy(energyCost);
//                inventory.addItem(harvestedCrop, 1); // TODO: Handle jumlah panen > 1 jika ada
//                targetTile.setGrowingCrop(null); // Hapus tanaman dari tile (otomatis set type ke TILLED)
//                // TODO: Panggil TimeManager untuk advance time 5 menit
//                support.firePropertyChange("mapStateChanged", null, targetTile);
//                System.out.println("Berhasil memanen " + harvestedCrop.getName());
//                return true;
//            }
//        }
//        System.out.println("Gagal memanen.");
//        return false;
//    }
//
//    /**
//     * Memakan item yang bisa dimakan dari inventory.
//     *
//     * @param food Item makanan (harus implement EdibleItem).
//     * @return true jika berhasil makan, false jika gagal.
//     */
//    public boolean eat(Item food) {
//        if (food instanceof EdibleItem && inventory.hasItem(food)) {
//            if (inventory.removeItem(food, 1)) {
//                addEnergy(((EdibleItem) food).getEnergyBoost());
//                // TODO: Panggil TimeManager untuk advance time 5 menit
//                System.out.println("Berhasil memakan " + food.getName());
//                return true;
//            }
//        }
//        System.out.println("Gagal memakan " + (food != null ? food.getName() : "item null"));
//        return false;
//    }
//
//    /** Memulai aksi memancing. */
//    public void fish() {
//        final int energyCost = 5; // Per attempt
//        // TODO: Cek lokasi valid (FARM (Pond), RIVER, LAKE, OCEAN)
//        boolean validLocation = (currentLocation == Location.FARM /* && dekat pond? */)
//                || currentLocation == Location.FOREST_RIVER
//                || currentLocation == Location.MOUNTAIN_LAKE
//                || currentLocation == Location.OCEAN;
//        // TODO: Cek punya Fishing Rod
//        boolean hasRod = true; // Placeholder
//
//        if (validLocation && hasRod && canPerformAction(energyCost)) {
//            useEnergy(energyCost);
//            System.out.println("Memulai memancing...");
//            // TODO: Implementasi logika RNG dan tebakan memancing
//            // Jika berhasil:
//            // Fish caughtFish = ...;
//            // inventory.addItem(caughtFish, 1);
//            // TODO: Panggil TimeManager untuk advance time (15 menit?)
//        } else {
//            System.out.println("Tidak bisa memancing di sini atau tidak punya alat/energi.");
//        }
//    }
//
//    /**
//     * Mencoba memasak resep.
//     *
//     * @param recipe Resep yang akan dimasak.
//     * @return true jika berhasil, false jika gagal.
//     */
//    public boolean cook(Recipe recipe) {
//        final int energyCost = 10; // Per percobaan
//        // TODO: Cek lokasi (harus di House?)
//        boolean atHome = currentLocation == Location.FARM; // Asumsi bisa masak jika di farm
//
//        // TODO: Cek punya semua bahan di inventory
//        boolean hasIngredients = true; // Placeholder
//        // for (Map.Entry<String, Integer> entry :
//        // recipe.getRequiredIngredients().entrySet()) {
//        // Item ingredient = ItemRegistry.get(entry.getKey());
//        // if (!inventory.hasItem(ingredient, entry.getValue())) {
//        // hasIngredients = false;
//        // break;
//        // }
//        // }
//
//        // TODO: Cek punya fuel (Coal/Firewood) jika resep membutuhkannya
//        boolean hasFuel = true; // Placeholder
//
//        if (atHome && hasIngredients && hasFuel && canPerformAction(energyCost)) {
//            useEnergy(energyCost);
//            // TODO: Kurangi bahan dari inventory
//            // TODO: Kurangi fuel dari inventory
//            // TODO: Buat instance Food hasil masakan
//            // Food cookedFood = ItemRegistry.getFood(recipe.getResultFoodName());
//            // inventory.addItem(cookedFood, 1);
//            // TODO: Panggil TimeManager untuk advance time (1 jam)
//            System.out.println("Berhasil memasak " + recipe.getResultFoodName());
//            return true;
//        }
//        System.out.println("Gagal memasak " + recipe.getResultFoodName());
//        return false;
//    }
//
//    /** Tidur untuk mengakhiri hari. */
//    public void sleep() {
//        System.out.println("Player pergi tidur...");
//        int oldEnergy = this.energy;
//        // Logika pemulihan energi
//        if (oldEnergy <= MIN_ENERGY) { // Jika pingsan sebelumnya
//            this.energy = MAX_ENERGY / 2; // Hanya setengah
//            System.out.println("   Energi pulih setengah karena pingsan.");
//        } else {
//            this.energy = MAX_ENERGY; // Pulih penuh
//            System.out.println("   Energi pulih penuh.");
//        }
//        support.firePropertyChange("energy", oldEnergy, this.energy);
//        // Time skip dan update hari/musim ditangani oleh TimeManager/Farm
//        // yang dipanggil SETELAH metode sleep ini selesai.
//    }
//
//    /**
//     * Berpindah ke lokasi World Map lain.
//     *
//     * @param destination Lokasi tujuan.
//     */
//    public void visit(Location destination) {
//        final int energyCost = 10;
//        final int timeCost = 15;
//
//        if (destination == Location.FARM) {
//            System.out.println("Kembali ke Farm...");
//        } else {
//            System.out.println("Mengunjungi " + destination + "...");
//        }
//
//        if (canPerformAction(energyCost)) {
//            useEnergy(energyCost);
//            Location oldLocation = this.currentLocation;
//            this.currentLocation = destination;
//            support.firePropertyChange("currentLocation", oldLocation, this.currentLocation);
//            // TODO: Panggil TimeManager untuk advance time
//            // timeManager.advanceTime(timeCost);
//        } else {
//            System.out.println("Tidak cukup energi untuk bepergian.");
//        }
//    }
//
//    /**
//     * Berbicara dengan NPC.
//     *
//     * @param targetNPC NPC yang diajak bicara.
//     * @return true jika berhasil.
//     */
//    public boolean chatWith(NPC targetNPC) {
//        final int energyCost = 10;
//        // TODO: Cek lokasi player == lokasi NPC?
//        boolean canChat = this.currentLocation == targetNPC.getHomeLocation(); // Contoh cek lokasi
//
//        if (targetNPC != null && canChat && canPerformAction(energyCost)) {
//            useEnergy(energyCost);
//            // TODO: Tingkatkan heart points NPC sedikit? Sesuai spec?
//            // targetNPC.addHeartPoints(1); // Contoh
//            System.out.println("[" + name + "]: Halo, " + targetNPC.getName() + "!");
//            System.out.println("[" + targetNPC.getName() + "]: " + targetNPC.getDialogue());
//            // TODO: Panggil TimeManager untuk advance time (10 menit?)
//            return true;
//        }
//        System.out.println(
//                "Tidak bisa bicara dengan " + (targetNPC != null ? targetNPC.getName() : "NPC null") + " saat ini.");
//        return false;
//    }
//
//    /**
//     * Memberikan item ke NPC.
//     *
//     * @param targetNPC NPC penerima.
//     * @param item      Item yang diberikan.
//     * @return true jika berhasil.
//     */
//    public boolean giftTo(NPC targetNPC, Item item) {
//        final int energyCost = 5;
//        // TODO: Cek lokasi player == lokasi NPC?
//        boolean canGift = this.currentLocation == targetNPC.getHomeLocation(); // Contoh
//
//        if (targetNPC != null && item != null && inventory.hasItem(item) && canGift && canPerformAction(energyCost)) {
//            if (inventory.removeItem(item, 1)) {
//                useEnergy(energyCost);
//                int points = targetNPC.receiveGift(item); // NPC menghitung & update heart points
//                System.out.println("Memberikan " + item.getName() + " ke " + targetNPC.getName() + ". Respon: " + points
//                        + " poin.");
//                // TODO: Panggil TimeManager untuk advance time (10 menit?)
//                return true;
//            }
//        }
//        System.out.println("Gagal memberikan hadiah.");
//        return false;
//    }
//
//    /**
//     * Mencoba melamar NPC.
//     *
//     * @param targetNPC NPC yang akan dilamar.
//     * @return true jika lamaran diterima.
//     */
//    public boolean proposeTo(NPC targetNPC) {
//        final int energyCost = 10; // Biaya energi melamar? Spec tidak sebut, asumsi ada.
//        // TODO: Cek punya Proposal Ring
//        boolean hasRing = true; // Placeholder
//        // Item proposalRing = ItemRegistry.get("Proposal Ring");
//        // boolean hasRing = inventory.hasItem(proposalRing);
//
//        // TODO: Cek lokasi player == lokasi NPC?
//        boolean canPropose = this.currentLocation == targetNPC.getHomeLocation(); // Contoh
//
//        if (targetNPC != null && hasRing && targetNPC.canBeProposedTo() && canPropose && canPerformAction(energyCost)) {
//            useEnergy(energyCost);
//            // inventory.removeItem(proposalRing, 1); // Hapus cincin
//            NPC oldPartner = this.partner;
//            this.partner = targetNPC;
//            targetNPC.setRelationshipStatus(RelationshipStatus.FIANCE);
//            support.firePropertyChange("partner", oldPartner, this.partner);
//            System.out.println("Lamaran diterima! " + targetNPC.getName() + " sekarang tunanganmu.");
//            // TODO: Panggil TimeManager untuk advance time (1 jam?)
//            return true;
//        }
//        System.out.println("Lamaran ditolak atau tidak bisa dilakukan.");
//        return false;
//    }
//
//    /**
//     * Menikahi tunangan.
//     *
//     * @param fianceNPC NPC yang berstatus tunangan.
//     */
//    public void marry(NPC fianceNPC) {
//        final int energyCost = 80; // Biaya energi menikah? Spec tidak sebut.
//
//        if (fianceNPC != null && this.partner == fianceNPC
//                && fianceNPC.getRelationshipStatus() == RelationshipStatus.FIANCE && canPerformAction(energyCost)) {
//            useEnergy(energyCost);
//            fianceNPC.setRelationshipStatus(RelationshipStatus.SPOUSE);
//            // Partner sudah di-set saat propose, statusnya saja yang berubah
//            support.firePropertyChange("partnerStatus", RelationshipStatus.FIANCE, RelationshipStatus.SPOUSE); // Event
//                                                                                                               // custom?
//            System.out.println("Selamat! Kamu sekarang menikah dengan " + fianceNPC.getName() + "!");
//            // TODO: Trigger event pernikahan (skip waktu seharian, kembali jam 22:00)
//            // Ini mungkin perlu dikoordinasikan oleh Controller/GameManager
//        } else {
//            System.out.println("Tidak bisa menikah saat ini.");
//        }
//    }
//
//    // --- Resource Management ---
//
//    /** Cek apakah energi cukup untuk melakukan aksi (di atas batas pingsan). */
//    private boolean canPerformAction(int energyCost) {
//        // Bisa melakukan aksi meskipun energi akan habis setelahnya
//        return this.energy > MIN_ENERGY;
//    }
//
//    /** Mengurangi energi dan cek batas bawah. */
//    private void useEnergy(int cost) {
//        if (cost <= 0)
//            return; // Tidak ada biaya
//
//        int oldEnergy = this.energy;
//        this.energy -= cost;
//        System.out.println("Energi: " + oldEnergy + " -> " + this.energy); // Log
//
//        // Cek jika energi habis total (pingsan)
//        if (this.energy <= MIN_ENERGY) {
//            this.energy = MIN_ENERGY; // Jangan sampai lebih rendah
//            System.out.println("Energi habis! Player pingsan...");
//            // TODO: Trigger mekanisme pingsan (misal, flag atau event)
//            // yang akan ditangani oleh game loop/controller untuk force sleep.
//            // Contoh: support.firePropertyChange("playerState", "WORKING", "PASSED_OUT");
//        }
//        support.firePropertyChange("energy", oldEnergy, this.energy);
//    }
//
//    /** Menambah energi, dibatasi oleh MAX_ENERGY. */
//    public void addEnergy(int amount) {
//        if (amount <= 0)
//            return;
//        int oldEnergy = this.energy;
//        this.energy += amount;
//        if (this.energy > MAX_ENERGY) {
//            this.energy = MAX_ENERGY;
//        }
//        System.out.println("Energi pulih: " + oldEnergy + " -> " + this.energy);
//        support.firePropertyChange("energy", oldEnergy, this.energy);
//    }
//
//    /** Menambah gold. */
//    public void addGold(int amount) {
//        if (amount <= 0)
//            return;
//        int oldGold = this.gold;
//        this.gold += amount;
//        System.out.println("Gold: " + oldGold + "g -> " + this.gold + "g (+" + amount + "g)");
//        support.firePropertyChange("gold", oldGold, this.gold);
//    }
//
//    /** Mengurangi gold jika cukup. */
//    public boolean spendGold(int amount) {
//        if (amount <= 0)
//            return true; // Tidak ada biaya
//        if (this.gold >= amount) {
//            int oldGold = this.gold;
//            this.gold -= amount;
//            System.out.println("Gold: " + oldGold + "g -> " + this.gold + "g (-" + amount + "g)");
//            support.firePropertyChange("gold", oldGold, this.gold);
//            return true;
//        } else {
//            System.out.println("Gold tidak cukup (" + this.gold + "g) untuk membayar " + amount + "g.");
//            return false;
//        }
//    }

    // --- Getters ---
    public String getName() {
        return name;
    }

    public Gender getGender() {
        return gender;
    }

    public int getEnergy() {
        return energy;
    }

    public int getMaxEnergy() {
        return MAX_ENERGY;
    }

    public int getMinEnergy() {
        return MIN_ENERGY;
    } // Berguna untuk cek pingsan

    public String getFarmName() {
        return farmName;
    }

    public NPC getPartner() {
        return partner;
    }

    public int getGold() {
        return gold;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getX() {
        return locationX;
    }

    public int getY() {
        return locationY;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    // --- Setters (Gunakan dengan Hati-hati) ---
    // Biasanya hanya dipanggil oleh aksi internal atau Controller
    public void setPartner(NPC npc) {
        NPC oldPartner = this.partner;
        this.partner = npc;
        support.firePropertyChange("partner", oldPartner, this.partner);
    }

    public void setCurrentLocation(Location location) {
        Location oldLocation = this.currentLocation;
        this.currentLocation = location;
        support.firePropertyChange("currentLocation", oldLocation, this.currentLocation);
    }

    // Setter untuk X, Y sebaiknya tidak publik, diubah via move()
    // Setter untuk Energy, Gold sebaiknya tidak publik, diubah via add/use/spend
}