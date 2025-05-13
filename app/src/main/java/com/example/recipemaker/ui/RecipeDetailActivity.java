package com.example.recipemaker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.recipemaker.R;
import com.example.recipemaker.databinding.ActivityRecipeDetailBinding;
import com.example.recipemaker.model.Recipe;
import com.example.recipemaker.viewmodel.RecipeViewModel;

public class RecipeDetailActivity extends AppCompatActivity {
    
    public static final String EXTRA_RECIPE_ID = "com.example.recipemaker.EXTRA_RECIPE_ID";
    
    private ActivityRecipeDetailBinding binding;
    private RecipeViewModel viewModel;
    private Recipe recipe;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecipeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Set up toolbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Set up ViewModel
        viewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        
        // Get recipe ID from intent
        int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);
        if (recipeId == -1) {
            finish();
            return;
        }
        
        // Load recipe data
        viewModel.getRecipeById(recipeId).observe(this, this::displayRecipe);
        
        // Set up share button
        binding.shareButton.setOnClickListener(v -> shareRecipe());
    }
    
    private void displayRecipe(Recipe recipe) {
        if (recipe != null) {
            this.recipe = recipe;
            
            // Set title in toolbar
            getSupportActionBar().setTitle(recipe.getTitle());
            
            // Display recipe details
            binding.recipeTitleTextView.setText(recipe.getTitle());
            binding.ingredientsTextView.setText(recipe.getFormattedIngredients());
            binding.instructionsTextView.setText(recipe.getInstructions());
        }
    }
    
    private void shareRecipe() {
        if (recipe != null) {
            String shareText = recipe.getTitle() + "\n\n" + 
                    getString(R.string.label_ingredients) + "\n" + 
                    recipe.getFormattedIngredients() + "\n\n" + 
                    getString(R.string.label_instructions) + "\n" + 
                    recipe.getInstructions();
            
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Recipe: " + recipe.getTitle());
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Share Recipe"));
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 