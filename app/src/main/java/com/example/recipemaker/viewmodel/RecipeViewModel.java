package com.example.recipemaker.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.recipemaker.api.GeminiService;
import com.example.recipemaker.data.RecipeRepository;
import com.example.recipemaker.model.Recipe;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ViewModel for handling recipe operations
 */
public class RecipeViewModel extends AndroidViewModel {
    
    private final RecipeRepository repository;
    private final GeminiService geminiService;
    
    private final MutableLiveData<Recipe> currentRecipe = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    public RecipeViewModel(@NonNull Application application) {
        super(application);
        repository = new RecipeRepository(application);
        geminiService = new GeminiService(application);
    }
    
    public LiveData<List<Recipe>> getAllRecipes() {
        return repository.getAllRecipes();
    }
    
    public LiveData<Recipe> getRecipeById(int id) {
        return repository.getRecipeById(id);
    }
    
    public LiveData<Recipe> getCurrentRecipe() {
        return currentRecipe;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public void generateRecipe(String ingredientsText) {
        if (ingredientsText == null || ingredientsText.trim().isEmpty()) {
            errorMessage.setValue("Please enter some ingredients");
            return;
        }
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        // Parse ingredients from comma-separated text
        List<String> ingredients = Arrays.stream(ingredientsText.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        
        geminiService.generateRecipe(ingredients, new GeminiService.RecipeGenerationCallback() {
            @Override
            public void onRecipeGenerated(Recipe recipe) {
                currentRecipe.postValue(recipe);
                isLoading.postValue(false);
            }
            
            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
                isLoading.postValue(false);
            }
        });
    }
    
    public void saveRecipe(RecipeRepository.InsertCallback callback) {
        Recipe recipe = currentRecipe.getValue();
        if (recipe != null) {
            repository.insert(recipe, callback);
        }
    }
    
    public void deleteRecipe(Recipe recipe) {
        repository.delete(recipe);
    }
} 