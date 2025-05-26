package spakborhills.cooking;

import spakborhills.GamePanel; // Perlu GamePanel jika resep bergantung pada instance gp
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeManager {
    private static List<Recipe> allRecipes = null;

    // Daftar nama item yang akan digunakan sebagai kunci di Map ingredients
    // Ini untuk konsistensi. Sesuaikan dengan nama item di kelas OBJ_Crop, OBJ_Fish, dll.
    public static final String ANY_FISH = "Any Fish";
    public static final String WHEAT = "Wheat"; // Asumsi nama Crop/Item
    public static final String POTATO = "Potato";
    public static final String SALMON = "Salmon";
    public static final String PUFFERFISH = "Pufferfish";
    public static final String GRAPE = "Grape";
    public static final String EGG = "Egg"; // Perlu didefinisikan sebagai item
    public static final String PUMPKIN = "Pumpkin";
    public static final String CAULIFLOWER = "Cauliflower";
    public static final String PARSNIP = "Parsnip";
    public static final String TOMATO = "Tomato";
    public static final String HOT_PEPPER = "Hot Pepper";
    public static final String MELON = "Melon";
    public static final String CRANBERRY = "Cranberry";
    public static final String BLUEBERRY = "Blueberry";
    public static final String LEGEND = "Legend"; // Nama ikan legendaris
    public static final String EGGPLANT = "Eggplant"; // Perlu didefinisikan sebagai item

    public static List<Recipe> getAllRecipes() {
        if (allRecipes == null) {
            allRecipes = new ArrayList<>();

            // Mengisi resep berdasarkan PDF (hal. 30-31) [cite: 218, 221]
            Map<String, Integer> ingredients;

            // recipe_1: Fish n' Chips
            ingredients = new HashMap<>();
            ingredients.put(ANY_FISH, 2);
            ingredients.put(WHEAT, 1);
            ingredients.put(POTATO, 1);
            allRecipes.add(new Recipe("recipe_1", "Fish n' Chips", 1, ingredients, "Beli di store", "STORE_BOUGHT"));

            // recipe_2: Baguette
            ingredients = new HashMap<>();
            ingredients.put(WHEAT, 3);
            allRecipes.add(new Recipe("recipe_2", "Baguette", 1, ingredients, "Default/Bawaan", "DEFAULT"));

            // recipe_3: Sashimi
            ingredients = new HashMap<>();
            ingredients.put(SALMON, 3); // Perhatikan, ini "Salmon", bukan "Any Fish"
            allRecipes.add(new Recipe("recipe_3", "Sashimi", 1, ingredients, "Setelah memancing 10 ikan", "FISH_COUNT_10"));

            // recipe_4: Fugu
            ingredients = new HashMap<>();
            ingredients.put(PUFFERFISH, 1);
            allRecipes.add(new Recipe("recipe_4", "Fugu", 1, ingredients, "Memancing pufferfish", "FISH_SPECIFIC_PUFFERFISH"));

            // recipe_5: Wine
            ingredients = new HashMap<>();
            ingredients.put(GRAPE, 2);
            allRecipes.add(new Recipe("recipe_5", "Wine", 1, ingredients, "Default/Bawaan", "DEFAULT"));

            // recipe_6: Pumpkin Pie
            ingredients = new HashMap<>();
            ingredients.put(EGG, 1);
            ingredients.put(WHEAT, 1);
            ingredients.put(PUMPKIN, 1);
            allRecipes.add(new Recipe("recipe_6", "Pumpkin Pie", 1, ingredients, "Default/Bawaan", "DEFAULT"));

            // recipe_7: Veggie Soup
            ingredients = new HashMap<>();
            ingredients.put(CAULIFLOWER, 1);
            ingredients.put(PARSNIP, 1);
            ingredients.put(POTATO, 1);
            ingredients.put(TOMATO, 1);
            allRecipes.add(new Recipe("recipe_7", "Veggie Soup", 1, ingredients, "Memanen (apapun) untuk pertama kalinya", "HARVEST_ANY_FIRST"));

            // recipe_8: Fish Stew
            ingredients = new HashMap<>();
            ingredients.put(ANY_FISH, 2);
            ingredients.put(HOT_PEPPER, 1);
            ingredients.put(CAULIFLOWER, 2);
            allRecipes.add(new Recipe("recipe_8", "Fish Stew", 1, ingredients, "Dapatkan \"Hot Pepper\" terlebih dahulu", "OBTAIN_HOT_PEPPER"));

            // recipe_9: Spakbor Salad
            ingredients = new HashMap<>();
            ingredients.put(MELON, 1);
            ingredients.put(CRANBERRY, 1);
            ingredients.put(BLUEBERRY, 1);
            ingredients.put(TOMATO, 1);
            allRecipes.add(new Recipe("recipe_9", "Spakbor Salad", 1, ingredients, "Default/Bawaan", "DEFAULT"));

            // recipe_10: Fish Sandwich
            ingredients = new HashMap<>();
            ingredients.put(ANY_FISH, 1);
            ingredients.put(WHEAT, 2);
            ingredients.put(TOMATO, 1);
            ingredients.put(HOT_PEPPER, 1);
            allRecipes.add(new Recipe("recipe_10", "Fish Sandwich", 1, ingredients, "Beli di store", "STORE_BOUGHT"));

            // recipe_11: The Legends of Spakbor
            ingredients = new HashMap<>();
            ingredients.put(LEGEND, 1);
            ingredients.put(POTATO, 2);
            ingredients.put(PARSNIP, 1);
            ingredients.put(TOMATO, 1);
            ingredients.put(EGGPLANT, 1);
            allRecipes.add(new Recipe("recipe_11", "The Legends of Spakbor", 1, ingredients, "Memancing \"Legend\"", "FISH_SPECIFIC_LEGEND"));
        }
        return allRecipes;
    }
}