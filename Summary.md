# Project Summary

## Project Overview
RecipeMaker is an Android application that helps users generate and manage recipes using artificial intelligence. The app allows users to input ingredients they have on hand, and it generates creative and personalized recipes using the Gemini AI API. Users can save their favorite recipes, view recipe details, and share recipes with others.

## Tech Stack
- **Programming Language**: Java
- **Android SDK**: Target SDK 34, Minimum SDK 24
- **Architecture Components**:
  - ViewModel
  - LiveData
  - Room Database
  - ViewBinding
- **Dependencies**:
  - AndroidX libraries (AppCompat, ConstraintLayout, Material Design)
  - Room Database for local storage
  - Retrofit for network calls
  - Google AI (Gemini API) for recipe generation
  - Guava library (required for Gemini API)
- **Tools**:
  - Android Studio
  - Gradle build system

## Features & Functionalities

### 1. Recipe Generation
- Users can input ingredients through text or voice input
- The app uses Gemini AI to generate creative recipes based on the provided ingredients
- Generated recipes include a title, list of ingredients, and step-by-step instructions
- Recipes are formatted with emojis for better visual appeal

### 2. Recipe Management
- Save generated recipes to local storage
- View saved recipes in a list format
- Delete unwanted recipes
- View detailed recipe information
- Share recipes with others

### 3. User Interface
- Modern Material Design interface
- Voice input support for ingredient entry
- Responsive layout with proper error handling
- Loading indicators for async operations
- Empty state handling for saved recipes

### 4. Data Persistence
- Local storage using Room Database
- Efficient data access patterns
- Background thread operations for database tasks

## Execution Flow

1. **User Input**:
   - User enters ingredients through text or voice input
   - Input is validated and processed

2. **Recipe Generation**:
   - Ingredients are sent to Gemini AI API
   - API generates a recipe in JSON format
   - Response is parsed and converted to Recipe object

3. **Recipe Display**:
   - Generated recipe is displayed in the main activity
   - User can view recipe details
   - User can save the recipe

4. **Recipe Management**:
   - Saved recipes are stored in Room Database
   - Recipes can be viewed in SavedRecipesActivity
   - Recipes can be deleted or shared

5. **Data Flow**:
   - ViewModel handles business logic
   - Repository pattern for data access
   - LiveData for reactive UI updates
   - Background threads for database operations

## FAQs

### Q: How do I generate a recipe?
A: You can generate a recipe by entering ingredients in the main screen. You can either type them in or use the voice input button to speak your ingredients.

### Q: Can I save recipes for later?
A: Yes, you can save any generated recipe by clicking the save button. Saved recipes can be accessed from the "Saved Recipes" screen.

### Q: How do I share a recipe?
A: You can share a recipe by clicking the share button on either the main screen or the recipe detail screen. This will open your device's sharing options.

### Q: Can I delete saved recipes?
A: Yes, you can delete saved recipes by long-pressing on a recipe in the saved recipes list and selecting the delete option.

## Q&A Section

### Q: What happens if the AI fails to generate a recipe?
A: The app will display an error message and allow you to try again. Make sure you have a stable internet connection and try with different ingredients.

### Q: Can I edit generated recipes?
A: Currently, the app doesn't support editing generated recipes. You can only save, view, and share them.

### Q: How are recipes stored locally?
A: Recipes are stored using Room Database, which is a SQLite abstraction layer provided by Android. This ensures efficient and reliable local storage.

### Q: What permissions does the app require?
A: The app requires internet permission for API calls and microphone permission for voice input. These permissions are requested when needed.

### Q: How does the voice input feature work?
A: The app uses Android's built-in SpeechRecognizer to convert speech to text. The recognized text is then used as ingredients for recipe generation.

### Q: Can I use the app offline?
A: You can view saved recipes offline, but recipe generation requires an internet connection to access the Gemini AI API.

### Q: How are ingredients processed?
A: Ingredients are split by commas and trimmed of whitespace. The app ensures that empty ingredients are filtered out before sending them to the API.

### Q: What happens if I try to save a duplicate recipe?
A: The app uses Room's REPLACE conflict strategy, which means if you try to save a recipe with the same title, it will be updated rather than creating a duplicate.