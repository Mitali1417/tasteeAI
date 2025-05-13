package com.example.recipemaker.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.recipemaker.model.Recipe;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository that handles data operations for recipes
 */
public class RecipeRepository {
    
    private final RecipeDao recipeDao;
    private final LiveData<List<Recipe>> allRecipes;
    private final ExecutorService executorService;
    
    public RecipeRepository(Application application) {
        RecipeDatabase database = RecipeDatabase.getInstance(application);
        recipeDao = database.recipeDao();
        allRecipes = recipeDao.getAllRecipes();
        executorService = Executors.newFixedThreadPool(4);
    }
    
    public LiveData<List<Recipe>> getAllRecipes() {
        return allRecipes;
    }
    
    public LiveData<Recipe> getRecipeById(int id) {
        return recipeDao.getRecipeById(id);
    }
    
    public void insert(Recipe recipe, InsertCallback callback) {
        executorService.execute(() -> {
            long id = recipeDao.insertRecipe(recipe);
            if (callback != null) {
                callback.onRecipeInserted((int) id);
            }
        });
    }
    
    public void update(Recipe recipe) {
        executorService.execute(() -> recipeDao.updateRecipe(recipe));
    }
    
    public void delete(Recipe recipe) {
        executorService.execute(() -> recipeDao.deleteRecipe(recipe));
    }
    
    public interface InsertCallback {
        void onRecipeInserted(int recipeId);
    }
} 