package com.example.recipemaker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.recipemaker.adapter.RecipeAdapter;
import com.example.recipemaker.databinding.ActivitySavedRecipesBinding;
import com.example.recipemaker.model.Recipe;
import com.example.recipemaker.viewmodel.RecipeViewModel;

import java.util.ArrayList;

public class SavedRecipesActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {
    
    private ActivitySavedRecipesBinding binding;
    private RecipeViewModel viewModel;
    private RecipeAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            binding = ActivitySavedRecipesBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            
            // Set up toolbar
            if (binding.toolbar != null) {
                setSupportActionBar(binding.toolbar);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setTitle("Saved Recipes");
                }
            }
            
            // Set up ViewModel
            viewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
            
            // Set up RecyclerView
            setupRecyclerView();
            
            // Observe LiveData from ViewModel
            viewModel.getAllRecipes().observe(this, recipes -> {
                if (adapter != null && binding != null) {
                    adapter.setRecipes(recipes != null ? recipes : new ArrayList<>());
                    binding.emptyTextView.setVisibility(recipes != null && recipes.isEmpty() ? View.VISIBLE : View.GONE);
                    binding.recyclerView.setVisibility(recipes != null && !recipes.isEmpty() ? View.VISIBLE : View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            // Handle initialization error gracefully
            finish();
        }
    }
    
    private void setupRecyclerView() {
        adapter = new RecipeAdapter(new ArrayList<>(), this);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    
    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
    
    @Override
    public void onDeleteClick(Recipe recipe) {
        viewModel.deleteRecipe(recipe);
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