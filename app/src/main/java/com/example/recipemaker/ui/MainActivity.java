package com.example.recipemaker.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.recipemaker.R;
import com.example.recipemaker.data.RecipeRepository;
import com.example.recipemaker.databinding.ActivityMainBinding;
import com.example.recipemaker.model.Recipe;
import com.example.recipemaker.viewmodel.RecipeViewModel;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {



    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    
    private ActivityMainBinding binding;
    private RecipeViewModel viewModel;
    private SpeechRecognizer speechRecognizer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Set up ViewModel
        viewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        
        // Set up speech recognizer
        initSpeechRecognizer();
        
        // Set up click listeners
        setupClickListeners();
        
        // Observe LiveData from ViewModel
        observeViewModel();
    }
    
    private void setupClickListeners() {
        binding.generateButton.setOnClickListener(v -> {
            String ingredients = binding.ingredientsEditText.getText().toString().trim();
            viewModel.generateRecipe(ingredients);
        });
        
        binding.voiceInputButton.setOnClickListener(v -> {
            if (checkPermission()) {
                startVoiceInput();
            } else {
                requestPermission();
            }
        });
        
        binding.viewSavedButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SavedRecipesActivity.class);
            startActivity(intent);
        });
        
        binding.saveButton.setOnClickListener(v -> {
            viewModel.saveRecipe(recipeId -> {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, 
                        R.string.success_recipe_saved, Toast.LENGTH_SHORT).show());
            });
        });
        
        binding.shareButton.setOnClickListener(v -> shareRecipe());
    }
    
    private void observeViewModel() {
        viewModel.getCurrentRecipe().observe(this, this::displayRecipe);
        
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
        
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void displayRecipe(Recipe recipe) {
        if (recipe != null) {
            binding.recipeCardView.setVisibility(View.VISIBLE);
            binding.recipeTitleTextView.setText(recipe.getTitle());
            binding.recipeIngredientsTextView.setText(recipe.getFormattedIngredients());
            binding.recipeInstructionsTextView.setText(recipe.getInstructions());
        }
    }
    
    private void initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle bundle) {
                    binding.voiceInputButton.setText("Listening...");
                }
                
                @Override
                public void onBeginningOfSpeech() {}
                
                @Override
                public void onRmsChanged(float v) {}
                
                @Override
                public void onBufferReceived(byte[] bytes) {}
                
                @Override
                public void onEndOfSpeech() {
                    binding.voiceInputButton.setText(R.string.btn_voice_input);
                }
                
                @Override
                public void onError(int i) {
                    binding.voiceInputButton.setText(R.string.btn_voice_input);
                    Toast.makeText(MainActivity.this, "Speech recognition error", Toast.LENGTH_SHORT).show();
                }
                
                @Override
                public void onResults(Bundle bundle) {
                    ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (results != null && !results.isEmpty()) {
                        String currentText = binding.ingredientsEditText.getText().toString().trim();
                        if (!currentText.isEmpty()) {
                            currentText += ", ";
                        }
                        binding.ingredientsEditText.setText(currentText + results.get(0));
                    }
                    binding.voiceInputButton.setText(R.string.btn_voice_input);
                }
                
                @Override
                public void onPartialResults(Bundle bundle) {}
                
                @Override
                public void onEvent(int i, Bundle bundle) {}
            });
        } else {
            binding.voiceInputButton.setEnabled(false);
        }
    }
    
    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        
        speechRecognizer.startListening(intent);
    }
    
    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
                == PackageManager.PERMISSION_GRANTED;
    }
    
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.RECORD_AUDIO}, 
                REQUEST_RECORD_AUDIO_PERMISSION);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startVoiceInput();
            } else {
                Toast.makeText(this, R.string.permission_required, Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void shareRecipe() {
        Recipe recipe = viewModel.getCurrentRecipe().getValue();
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
    
    public void openLinkedIn(View view) {
        String linkedInUrl = "https://www.linkedin.com/in/mitali-25372722b/";
        Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(linkedInUrl));
        startActivity(intent);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
} 