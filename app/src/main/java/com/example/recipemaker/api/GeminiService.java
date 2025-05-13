package com.example.recipemaker.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.recipemaker.model.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service class for interacting with Gemini AI API using direct HTTP calls
 * instead of the Gemini client library to avoid version compatibility issues
 */
public class GeminiService {
    private static final String TAG = "GeminiService";
    private static final String API_KEY = "AIzaSyDuPU2aTXp9rNUvf7rYBCG6Zk7uNsgJdYg"; // Replace with your actual API key
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;
    
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    
    public GeminiService(Context context) {
        // No initialization needed for direct HTTP approach
    }
    
    public void generateRecipe(List<String> ingredients, RecipeGenerationCallback callback) {
        executor.execute(() -> {
            try {
                String requestBody = buildRequestBody(ingredients);
                String responseJson = makeHttpRequest(API_URL, requestBody);
                
                JSONObject responseObj = new JSONObject(responseJson);
                if (!responseObj.has("candidates")) {
                    throw new IOException("Invalid API response format");
                }
                
                JSONArray candidates = responseObj.getJSONArray("candidates");
                if (candidates.length() == 0) {
                    throw new IOException("No candidates in response");
                }
                
                JSONObject candidate = candidates.getJSONObject(0);
                if (!candidate.has("content")) {
                    throw new IOException("No content in first candidate");
                }
                
                JSONObject content = candidate.getJSONObject("content");
                if (!content.has("parts")) {
                    throw new IOException("No parts in content");
                }
                
                JSONArray parts = content.getJSONArray("parts");
                if (parts.length() == 0) {
                    throw new IOException("Empty parts array");
                }
                
                JSONObject part = parts.getJSONObject(0);
                if (!part.has("text")) {
                    throw new IOException("No text in first part");
                }
                
                String responseText = part.getString("text");
                Recipe recipe = parseResponse(responseText);
                
                if (recipe != null) {
                    mainHandler.post(() -> callback.onRecipeGenerated(recipe));
                } else {
                    mainHandler.post(() -> callback.onError("Failed to parse response"));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error during API call", e);
                mainHandler.post(() -> callback.onError("API error: " + e.getMessage()));
            }
        });
    }
    
    private String buildRequestBody(List<String> ingredients) {
         String prompt = "You are a friendly and creative cooking assistant 🍳. Create a delicious, easy-to-follow recipe using ONLY the following ingredients: " 
            + String.join(", ", ingredients) 
            + ".\n\n"
            + "Respond in the following **JSON format**:\n"
            + "{\n"
            + "  \"title\": \"Recipe title\",\n"
            + "  \"ingredients\": [\"ingredient 1\", \"ingredient 2\", ...],\n"
            + "  \"instructions\": \"Step-by-step instructions\"\n"
            + "}\n\n"
            + "✅ Include ALL the ingredients in the recipe.\n"
            + "✅ The instructions should be clear, beginner-friendly, and concise.\n"
            + "✅ Add fun and relevant emojis in both the title and instructions to make the recipe engaging and visually appealing.\n"
            + "✅ Avoid any extra commentary outside the JSON response.\n"
            + "✅ Response in Hinglish if user inputs or types in hinglish or hindi.\n"
            + "Let’s get cooking! 🍽️";
                
        try {
            JSONObject requestJson = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject part = new JSONObject();
            
            part.put("text", prompt);
            parts.put(part);
            content.put("parts", parts);
            contents.put(content);
            requestJson.put("contents", contents);
            
            // Add generation parameters
            JSONObject generationConfig = new JSONObject();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("topK", 40);
            generationConfig.put("topP", 0.95);
            generationConfig.put("maxOutputTokens", 2048);
            requestJson.put("generationConfig", generationConfig);
            
            return requestJson.toString();
        } catch (JSONException e) {
            Log.e(TAG, "Error building request body", e);
            return "{}";
        }
    }
    
    private String makeHttpRequest(String apiUrl, String requestBody) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        int responseCode = connection.getResponseCode();
        Log.d(TAG, "HTTP Response Code: " + responseCode);
        
        if (responseCode != HttpURLConnection.HTTP_OK) {
            // Read error stream if available
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                String errorMsg = response.toString();
                Log.e(TAG, "HTTP Error Response: " + errorMsg);
                throw new IOException("HTTP error code: " + responseCode + ", Response: " + errorMsg);
            } catch (Exception e) {
                throw new IOException("HTTP error code: " + responseCode);
            }
        }
        
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }
    
    private Recipe parseResponse(String response) {
        try {
            // Extract JSON from the response if needed
            String jsonString = extractJsonFromResponse(response);
            
            JSONObject jsonObject = new JSONObject(jsonString);
            String title = jsonObject.getString("title");
            
            List<String> ingredients = new ArrayList<>();
            JSONArray ingredientsArray = jsonObject.getJSONArray("ingredients");
            for (int i = 0; i < ingredientsArray.length(); i++) {
                ingredients.add(ingredientsArray.getString(i));
            }
            
            String instructions = jsonObject.getString("instructions");
            
            return new Recipe(title, ingredients, instructions);
        } catch (JSONException e) {
            Log.e(TAG, "JSON parsing error", e);
            return null;
        }
    }
    
    private String extractJsonFromResponse(String response) {
        // Sometimes the AI might wrap the JSON in code blocks or add extra text
        if (response.contains("{") && response.contains("}")) {
            int start = response.indexOf("{");
            int end = response.lastIndexOf("}") + 1;
            return response.substring(start, end);
        }
        return response;
    }
    
    public interface RecipeGenerationCallback {
        void onRecipeGenerated(Recipe recipe);
        void onError(String errorMessage);
    }
} 