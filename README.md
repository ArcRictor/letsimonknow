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
5. Create a `.env` file with your ElevenLabs API key:
   ```
   ELEVENLABS_API_KEY=your_api_key_here
   PASSCODE=your_desired_passcode
   ```
6. Run the server:
   ```
   python server.py
   ```

### Web Interface
The web interface will be available at `http://localhost:5000` after starting the server.

### Android App
The Android app project is in the `android_app` directory. Open it with Android Studio to build and run. 