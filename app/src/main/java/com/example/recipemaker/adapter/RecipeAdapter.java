package com.example.recipemaker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipemaker.R;
import com.example.recipemaker.databinding.ItemRecipeBinding;
import com.example.recipemaker.model.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    
    private List<Recipe> recipes;
    private final OnRecipeClickListener listener;
    
    public RecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecipeBinding binding = ItemRecipeBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new RecipeViewHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe, listener);
    }
    
    @Override
    public int getItemCount() {
        return recipes.size();
    }
    
    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }
    
    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        
        private final ItemRecipeBinding binding;
        
        public RecipeViewHolder(ItemRecipeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        
        public void bind(Recipe recipe, OnRecipeClickListener listener) {
            binding.recipeTitleTextView.setText(recipe.getTitle());
            binding.ingredientsPreviewTextView.setText(recipe.getIngredientsPreview());
            
            // Set click listener for the whole item
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRecipeClick(recipe);
                }
            });
            
            // Set long click listener for context menu
            itemView.setOnLongClickListener(v -> {
                showPopupMenu(v, recipe, listener);
                return true;
            });
        }
        
        private void showPopupMenu(View view, Recipe recipe, OnRecipeClickListener listener) {
            PopupMenu popup = new PopupMenu(view.getContext(), view);
            popup.inflate(R.menu.menu_recipe_item);
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_delete && listener != null) {
                    listener.onDeleteClick(recipe);
                    return true;
                }
                return false;
            });
            popup.show();
        }
    }
    
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
        void onDeleteClick(Recipe recipe);
    }
} 