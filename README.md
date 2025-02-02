# Voice Message System

This project consists of three main components:
1. Web Interface - For submitting messages
2. Backend Server - For handling messages and authentication
3. Android App - For receiving and playing messages

## Setup Instructions

### Backend Setup
1. Install Python 3.8 or higher
2. Create a virtual environment:
   ```
   python -m venv venv
   ```
3. Activate the virtual environment:
   - Windows:
     ```
     .\venv\Scripts\activate
     ```
   - Mac/Linux:
     ```
     source venv/bin/activate
     ```
4. Install dependencies:
   ```
   pip install -r requirements.txt
   ```
5. Copy `.env.template` to `.env` and fill in your values:
   ```
   cp .env.template .env
   ```
   Then edit `.env` with your actual values.

6. Run the server:
   ```
   python server.py
   ```

### Android App Setup
1. Open the `android_app` folder in Android Studio
2. Make sure the `.env` file exists in the project root with the correct PASSCODE
3. Sync project with Gradle files
4. Update `NetworkModule.kt` with your server's IP address if using a physical device
5. Build and run the app

### Web Interface
The web interface will be available at `http://localhost:5000` after starting the server.

## Development Notes
- The Android app uses Jetpack Compose for the UI
- Authentication is handled via a shared passcode
- Network communication uses Retrofit and OkHttp
- Messages are stored in `messages.json`

## Security Notes
- Keep your `.env` file secure and never commit it to version control
- The app uses cleartext traffic for local development - additional security measures needed for production 