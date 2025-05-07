package items;

import java.util.ArrayList;
import java.util.List;

public class Recipe extends Items {
    private String recipeID;
    private List<Items> ingredients;
    private boolean isUnlock;

    public  Recipe (String itemName, itemType type, boolean isEdible, String recipeID, List<Items> ingredients, boolean isUnlock) {
        super(itemName, type.RECIPE, false);
        this.recipeID = recipeID;
        this.ingredients = new ArrayList<Items>();
        this.isUnlock = isUnlock;
    }

    public String getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(String recipeID) {
        this.recipeID = recipeID;
    }

    public List<Items> getIngredients() {
        return ingredients;
    }

    public void addIngredient(Items item) {
        this.ingredients.add(item);
    }

    public void removeIngredient(Items item) {
        this.ingredients.remove(item);
    }

    public boolean isUnlock() {
        return isUnlock;
    }

    public void setIsUnlock(boolean isUnlock) {
        this.isUnlock = isUnlock;
    }
}