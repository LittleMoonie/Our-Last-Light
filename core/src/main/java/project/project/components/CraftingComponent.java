package project.project.components;

import java.util.HashMap;
import java.util.Map;

public class CraftingComponent implements Component {
    private final Map<String, Map<String, Integer>> recipes;

    public CraftingComponent() {
        this.recipes = new HashMap<>();
    }

    public void addRecipe(String outputItem, Map<String, Integer> requirements) {
        recipes.put(outputItem, requirements);
    }

    public Map<String, Integer> getRecipe(String outputItem) {
        return recipes.get(outputItem);
    }

    public boolean hasRecipe(String outputItem) {
        return recipes.containsKey(outputItem);
    }

    public Map<String, Map<String, Integer>> getAllRecipes() {
        return recipes;
    }
}
