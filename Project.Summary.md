# Recipe Maker Android App

## Project Overview
Recipe Maker is a Java-based Android application that helps users generate recipes based on the ingredients they have available. The app leverages Google's Gemini AI to create personalized recipes, allowing users to input ingredients either by typing or using voice recognition. Users can save their favorite recipes for future reference, view detailed instructions, and share recipes with friends.

## Tech Stack
- **Programming Language**: Java for Android
- **Architecture Pattern**: MVVM (Model-View-ViewModel)
- **UI Components**: Material Design Components
- **Database**: Room for local storage of saved recipes
- **API Integration**: Google Gemini AI for recipe generation
- **Data Binding**: ViewBinding for view references
- **Reactive Programming**: LiveData for reactive UI updates
- **Voice Recognition**: Android Speech Recognition API
- **Networking**: Direct HTTP calls to Gemini API

## Features & Functionalities

### Core Features
- **Ingredient Input**: Users can enter ingredients via text or voice input
- **Recipe Generation**: AI-powered recipe creation based on available ingredients
- **Recipe Saving**: Ability to save favorite recipes to local database
- **Saved Recipes**: View and manage all saved recipes
- **Recipe Details**: Detailed view of recipe title, ingredients, and instructions
- **Recipe Sharing**: Share recipes with others via standard Android sharing options

### User Interface
- **Main Screen**: Input ingredients, generate recipes, and view results
- **Recipe Detail Screen**: View complete recipe information
- **Saved Recipes Screen**: Browse and manage saved recipes
- **Voice Input**: Microphone button for hands-free ingredient entry

### Data Management
- **Local Storage**: Room database for persistent storage of recipes
- **Repository Pattern**: Centralized data management
- **Asynchronous Operations**: Background processing for database and network operations

## Execution Flow

### Recipe Generation Process
1. User inputs ingredients (text or voice)
2. App sends ingredient list to Gemini AI via HTTP request
3. Gemini AI processes the ingredients and generates a recipe
4. App parses the JSON response and extracts recipe details
5. Recipe is displayed to the user with title, ingredients, and instructions

### Data Flow Architecture
- **UI Layer**: Activities and adapters handle user interaction
- **ViewModel Layer**: Manages UI-related data and communicates with repository
- **Repository Layer**: Coordinates data operations between local database and remote API
- **Data Sources**: Room database (local) and Gemini API (remote)

### Component Interaction
- **MainActivity**: Handles user input, displays generated recipes
- **RecipeViewModel**: Processes user actions, manages LiveData objects
- **RecipeRepository**: Coordinates data operations
- **GeminiService**: Communicates with Gemini AI API
- **RecipeDatabase/RecipeDao**: Manages local storage operations

## Libraries Used
- AndroidX AppCompat and Core KTX
- Google Material Design Components
- Room for database operations
- Retrofit and OkHttp for network calls
- Google Gemini AI client
- Android Speech Recognition API

## FAQs

### How does the app generate recipes?
The app uses Google's Gemini AI to analyze the ingredients you provide and generate appropriate recipes. The AI considers common cooking techniques and flavor combinations to create recipes that use your available ingredients effectively.

### Can I use voice to input ingredients?
Yes, the app includes voice recognition functionality. Tap the "Voice Input" button and speak your ingredients clearly. The app will convert your speech to text and use those ingredients for recipe generation.

### How do I save a recipe?
After generating a recipe, tap the "Save Recipe" button to store it in your local database. You can access all saved recipes by tapping the "Saved Recipes" button.

### Can I share recipes with friends?
Yes, both generated and saved recipes can be shared. Use the share button to send the recipe via any sharing-capable app on your device (email, messaging apps, social media, etc.).

### Does the app require an internet connection?
Yes, an internet connection is required for generating new recipes as the app needs to communicate with the Gemini AI API. However, you can view previously saved recipes without an internet connection.

## Q&A Section

### What happens if the app can't generate a recipe with my ingredients?
If the AI cannot generate a suitable recipe with your provided ingredients, you'll receive a notification suggesting you add more ingredients or try different combinations.

### Is there a limit to how many recipes I can save?
No, you can save as many recipes as your device storage allows. Recipes are stored efficiently in the local database.

### How accurate is the voice recognition?
The voice recognition uses Android's built-in speech recognition system, which works well in most environments. For best results, speak clearly in a quiet environment and review the recognized text before generating a recipe.

### Can I edit a saved recipe?
Currently, the app doesn't support editing saved recipes. If you want to modify a recipe, you'll need to generate a new one and save it.

### Does the app support multiple languages?
The app includes support for English and Spanish localization for UI elements. Recipe generation works best with English ingredient lists.

### What permissions does the app require?
The app requires internet permission for API communication and microphone permission for voice input functionality.