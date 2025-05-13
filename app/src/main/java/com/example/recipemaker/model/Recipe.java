package com.example.recipemaker.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.List;

/**
 * Model class for a recipe
 */
@Entity(tableName = "recipes")
@TypeConverters(StringListConverter.class)
public class Recipe implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private List<String> ingredients;
    private String instructions;

    public Recipe() {
        // Required empty constructor for Room
    }

    public Recipe(String title, List<String> ingredients, String instructions) {
        this.title = title;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    /**
     * Returns a formatted version of the ingredients list
     */
    public String getFormattedIngredients() {
        if (ingredients == null || ingredients.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (String ingredient : ingredients) {
            builder.append("• ").append(ingredient).append("\n");
        }
        return builder.toString().trim();
    }

    /**
     * Returns a comma-separated preview of the first few ingredients
     */
    public String getIngredientsPreview() {
        if (ingredients == null || ingredients.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder("Main ingredients: ");
        int maxToShow = Math.min(ingredients.size(), 3);
        
        for (int i = 0; i < maxToShow; i++) {
            builder.append(ingredients.get(i));
            if (i < maxToShow - 1) {
                builder.append(", ");
            }
        }
        
        if (ingredients.size() > 3) {
            builder.append(", ...");
        }
        
        return builder.toString();
    }
} 