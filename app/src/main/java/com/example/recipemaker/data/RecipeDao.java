package com.example.recipemaker.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.recipemaker.model.Recipe;

import java.util.List;

/**
 * Data Access Object (DAO) for Recipe operations
 */
@Dao
public interface RecipeDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertRecipe(Recipe recipe);
    
    @Update
    void updateRecipe(Recipe recipe);
    
    @Delete
    void deleteRecipe(Recipe recipe);
    
    @Query("SELECT * FROM recipes ORDER BY id DESC")
    LiveData<List<Recipe>> getAllRecipes();
    
    @Query("SELECT * FROM recipes WHERE id = :id")
    LiveData<Recipe> getRecipeById(int id);
    
    @Query("SELECT COUNT(*) FROM recipes")
    int getRecipeCount();
} 