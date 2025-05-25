package spakborhills.cooking;

import spakborhills.GamePanel;
import spakborhills.object.OBJ_Food;
import spakborhills.enums.ItemType;

public class FoodFactory {
    public static OBJ_Food createFood(GamePanel gp, String foodName) {
        // Berdasarkan spesifikasi PDF hal. 18-19 [cite: 142, 145]
        switch (foodName) {
            case "Fish n' Chips":
                return new OBJ_Food(gp, ItemType.FOOD, "Fish n' Chips", true, 150, 135, 50);
            case "Baguette":
                return new OBJ_Food(gp, ItemType.FOOD, "Baguette", true, 100, 80, 25);
            case "Sashimi":
                return new OBJ_Food(gp, ItemType.FOOD, "Sashimi", true, 300, 275, 70);
            case "Fugu":
                return new OBJ_Food(gp, ItemType.FOOD, "Fugu", true, 0, 135, 50); // Harga beli tidak ada
            case "Wine":
                return new OBJ_Food(gp, ItemType.FOOD, "Wine", true, 100, 90, 20);
            case "Pumpkin Pie":
                return new OBJ_Food(gp, ItemType.FOOD, "Pumpkin Pie", true, 120, 100, 35);
            case "Veggie Soup":
                return new OBJ_Food(gp, ItemType.FOOD, "Veggie Soup", true, 140, 120, 40);
            case "Fish Stew":
                return new OBJ_Food(gp, ItemType.FOOD, "Fish Stew", true, 280, 260, 70);
            case "Spakbor Salad":
                return new OBJ_Food(gp, ItemType.FOOD, "Spakbor Salad", true, 0, 250, 70); // Harga beli tidak ada
            case "Fish Sandwich":
                return new OBJ_Food(gp, ItemType.FOOD, "Fish Sandwich", true, 200, 180, 50);
            case "The Legends of Spakbor":
                return new OBJ_Food(gp, ItemType.FOOD, "The Legends of Spakbor", true, 0, 2000, 100); // Harga beli tidak ada
            // Cooked Pig's Head tidak memiliki resep di PDF, jadi tidak dimasukkan di sini
            default:
                System.err.println("FoodFactory: Nama makanan tidak dikenal - " + foodName);
                return null;
        }
    }
}