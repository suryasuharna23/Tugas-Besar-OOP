package spakborhills.cooking;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import spakborhills.GamePanel;
import spakborhills.enums.ItemType;
import spakborhills.object.OBJ_Food;

public class FoodRegistry {
    private static final FoodRegistry foodRegistry = new FoodRegistry();

    private static Map<String, Function<GamePanel, OBJ_Food>> registry;


    private FoodRegistry(){
        registry = new HashMap<>();
    }

    public static void registerFood(String foodName, Function<GamePanel, OBJ_Food> food) {
        registry.put(foodName, food);
    }


    public OBJ_Food getFoodName(String key) {
        return (OBJ_Food) registry.get(key);
    }

    public static OBJ_Food createFood(GamePanel gp, String foodName) {
        return switch (foodName) {
            case "Fish n' Chips" -> new OBJ_Food(gp, ItemType.FOOD, "Fish n' Chips", true, 150, 135, 50);
            case "Baguette" -> new OBJ_Food(gp, ItemType.FOOD, "Baguette", true, 100, 80, 25);
            case "Sashimi" -> new OBJ_Food(gp, ItemType.FOOD, "Sashimi", true, 300, 275, 70);
            case "Fugu" -> new OBJ_Food(gp, ItemType.FOOD, "Fugu", true, 0, 135, 50); // Harga beli tidak ada
            case "Wine" -> new OBJ_Food(gp, ItemType.FOOD, "Wine", true, 100, 90, 20);
            case "Pumpkin Pie" -> new OBJ_Food(gp, ItemType.FOOD, "Pumpkin Pie", true, 120, 100, 35);
            case "Veggie Soup" -> new OBJ_Food(gp, ItemType.FOOD, "Veggie Soup", true, 140, 120, 40);
            case "Fish Stew" -> new OBJ_Food(gp, ItemType.FOOD, "Fish Stew", true, 280, 260, 70);
            case "Spakbor Salad" ->
                    new OBJ_Food(gp, ItemType.FOOD, "Spakbor Salad", true, 0, 250, 70); // Harga beli tidak ada
            case "Fish Sandwich" -> new OBJ_Food(gp, ItemType.FOOD, "Fish Sandwich", true, 200, 180, 50);
            case "The Legends of Spakbor" ->
                    new OBJ_Food(gp, ItemType.FOOD, "The Legends of Spakbor", true, 0, 2000, 100); // Harga beli tidak ada
            // Cooked Pig's Head tidak memiliki resep di PDF, jadi tidak dimasukkan di sini
            default -> {
                System.err.println("FoodRegistry: Nama makanan tidak dikenal - " + foodName);
                yield null;
            }
        };
    }

    public void addFood (String newFood, Function<GamePanel, OBJ_Food> function) {
        registry.put(newFood, function);
    }

    public void removeFood (String oldFood) {
        if (!registry.isEmpty()) {
            registry.remove(oldFood);
        }
    }
}