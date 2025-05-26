package spakborhills.cooking;

import java.util.Map;

public class Recipe {
    public String recipeId;
    public String outputFoodName;       // Nama OBJ_Food yang dihasilkan
    public int outputFoodQuantity;      // Jumlah makanan yang dihasilkan (biasanya 1)
    public Map<String, Integer> ingredients; // Kunci: nama item atau "Any Fish", Nilai: jumlah
    public String unlockConditionDescription; // Deskripsi untuk ditampilkan ke pemain
    public String unlockMechanismKey;      // Kunci untuk mekanisme buka resep secara programatik

    public Recipe(String recipeId, String outputFoodName, int outputFoodQuantity,
                  Map<String, Integer> ingredients,
                  String unlockConditionDescription, String unlockMechanismKey) {
        this.recipeId = recipeId;
        this.outputFoodName = outputFoodName;
        this.outputFoodQuantity = outputFoodQuantity;
        this.ingredients = ingredients;
        this.unlockConditionDescription = unlockConditionDescription;
        this.unlockMechanismKey = unlockMechanismKey;
    }

    public boolean requiresAnyFish() {
        return ingredients.containsKey("Any Fish");
    }

    public int getRequiredQuantity(String ingredientName) {
        return ingredients.getOrDefault(ingredientName, 0);
    }
}
