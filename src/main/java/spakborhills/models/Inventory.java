package spakborhills.models;

import spakborhills.enums.itemType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set; // Untuk getAllItemNames

/**
 * Mengelola koleksi item yang dimiliki oleh Player.
 * Menyimpan item berdasarkan nama uniknya dan jumlah (quantity) masing-masing.
 */
public class Inventory {

    // Struktur data internal: Menyimpan nama item (String) sebagai kunci
    // dan jumlah (Integer) sebagai nilai.

    private final Map<String, Integer> itemsByName;
    /**
     * Membuat instance Inventory baru yang kosong.
     */
    public Inventory() {
        this.itemsByName = new HashMap<>();
    }
    /**
     * Menambahkan sejumlah item tertentu ke dalam inventory.
     * Jika item sudah ada, jumlahnya akan ditambahkan.
     *
     * @param item     Objek Item yang akan ditambahkan (tidak boleh null).
     * @param quantity Jumlah item yang akan ditambahkan (harus > 0).
     * @throws NullPointerException     jika item null.
     * @throws IllegalArgumentException jika quantity <= 0.
     */
    public void addItem(Items item, int quantity) {
        Objects.requireNonNull(item, "Item yang ditambahkan tidak boleh null");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity harus lebih besar dari 0. Diberikan: " + quantity);
        }
        String itemName = item.getItemName();
        // getOrDefault mengambil nilai saat ini (atau 0 jika belum ada), lalu tambahkan
        // quantity
        int newQuantity = this.itemsByName.getOrDefault(itemName, 0) + quantity;
        this.itemsByName.put(itemName, newQuantity);

        System.out.println("Inventory: Menambahkan " + quantity + "x " + itemName + ". Total sekarang: " + newQuantity); // Log
                                                                                                                         // untuk
                                                                                                                         // debug
    }

    /**
     * Menghapus sejumlah item tertentu dari inventory.
     *
     * @param item     Objek Item yang akan dihapus (tidak boleh null).
     * @param quantity Jumlah item yang akan dihapus (harus > 0).
     * @return true jika item berhasil dihapus (jumlah mencukupi), false jika tidak.
     * @throws NullPointerException     jika item null.
     * @throws IllegalArgumentException jika quantity <= 0.
     */
    public boolean removeItem(Items item, int quantity) {
        Objects.requireNonNull(item, "Item yang dihapus tidak boleh null");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity harus lebih besar dari 0. Diberikan: " + quantity);
        }

        String itemName = item.getItemName();
        int currentQuantity = this.itemsByName.getOrDefault(itemName, 0);

        if (currentQuantity >= quantity) {
            int newQuantity = currentQuantity - quantity;
            if (newQuantity > 0) {
                this.itemsByName.put(itemName, newQuantity); // Update jumlah
            } else {
                this.itemsByName.remove(itemName); // Hapus entri jika jumlah jadi 0
            }
            System.out.println("Inventory: Menghapus " + quantity + "x " + itemName + ". Sisa: " + newQuantity); // Log



            return true;
        } else {
            System.out.println(
                    "Inventory: Gagal menghapus " + quantity + "x " + itemName + ". Hanya ada: " + currentQuantity); // Log
            return false; // Jumlah tidak mencukupi
        }
    }

    /**
     * Mengecek apakah inventory memiliki setidaknya satu dari item tertentu.
     *
     * @param item Item yang akan dicek (tidak boleh null).
     * @return true jika item ada (jumlah > 0), false jika tidak.
     * @throws NullPointerException jika item null.
     */
    public boolean hasItem(Items item) {
        Objects.requireNonNull(item, "Item untuk dicek tidak boleh null");
        return this.itemsByName.getOrDefault(item.getItemName(), 0) > 0;
    }

    /**
     * Mengecek apakah inventory memiliki sejumlah item tertentu.
     *
     * @param item     Item yang akan dicek (tidak boleh null).
     * @param quantity Jumlah minimum yang harus ada (harus > 0).
     * @return true jika item ada dengan jumlah yang mencukupi, false jika tidak.
     * @throws NullPointerException     jika item null.
     * @throws IllegalArgumentException jika quantity <= 0.
     */
    public boolean hasItem(Items item, int quantity) {
        Objects.requireNonNull(item, "Item untuk dicek tidak boleh null");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity harus lebih besar dari 0. Diberikan: " + quantity);
        }
        return this.itemsByName.getOrDefault(item.getItemName(), 0) >= quantity;
    }

    /**
     * Mendapatkan jumlah (quantity) dari item tertentu dalam inventory.
     *
     * @param item Item yang jumlahnya ingin diketahui (tidak boleh null).
     * @return Jumlah item tersebut, atau 0 jika tidak ada.
     * @throws NullPointerException jika item null.
     */
    public int getItemCount(Items item) {
        Objects.requireNonNull(item, "Item untuk dihitung tidak boleh null");
        return this.itemsByName.getOrDefault(item.getItemName(), 0);
    }

    /**
     * Mendapatkan representasi Map dari isi inventory (nama item -> jumlah).
     * Mengembalikan map yang tidak bisa dimodifikasi untuk keamanan.
     * Berguna untuk menampilkan isi inventory atau iterasi.
     *
     * @return Map yang tidak bisa dimodifikasi berisi item dan jumlahnya.
     */
    public Map<String, Integer> getItems() {
        return Collections.unmodifiableMap(this.itemsByName);
    }

    /**
     * Mendapatkan Set berisi nama-nama semua item yang ada di inventory.
     * Berguna jika hanya perlu daftar nama item.
     *
     * @return Set yang tidak bisa dimodifikasi berisi nama item.
     */
    public Set<String> getAllItemNames() {
        return Collections.unmodifiableSet(this.itemsByName.keySet());
    }

    /**
     * Menampilkan isi inventory ke konsol (untuk debugging atau CLI sederhana).
     */
    public void displayInventory() {
        System.out.println("--- Inventory ---");
        if (itemsByName.isEmpty()) {
            System.out.println("   (Kosong)");
        } else {
            // Urutkan berdasarkan nama untuk tampilan yang konsisten (opsional)
            itemsByName.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> System.out.println(" - " + entry.getKey() + ": " + entry.getValue()));
        }
        System.out.println("-----------------");
    }

    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        Equipment kapakEmas = new Equipment("Kapak Emas", itemType.EQUIPMENT, false);
        Equipment paculKidal = new Equipment("Pacul Kidal", itemType.EQUIPMENT, false);
        inventory.addItem(kapakEmas, 2);
        inventory.addItem(paculKidal, 1);
        inventory.displayInventory();
    }
}
