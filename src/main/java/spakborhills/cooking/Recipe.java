package spakborhills.cooking;

import java.util.Map;

public class Recipe {
    public String recipeId;
    public String outputFoodName; 
    public int outputFoodQuantity; 
    public Map<String, Integer> ingredients; 
    public String unlockConditionDescription; 
    public String unlockMechanismKey; 

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
