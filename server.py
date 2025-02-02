from flask import Flask, request, jsonify, render_template, session
from flask_cors import CORS
from dotenv import load_dotenv
import os
import json
from datetime import datetime

# Load environment variables
load_dotenv()

app = Flask(__name__)
CORS(app)
app.secret_key = os.urandom(24)  # For session management

# Configuration
PASSCODE = os.getenv('PASSCODE', 'default_passcode')
MESSAGES_FILE = 'messages.json'

def save_message(message):
    messages = []
    if os.path.exists(MESSAGES_FILE):
        with open(MESSAGES_FILE, 'r') as f:
            messages = json.load(f)
    
    messages.append({
        'message': message,
        'timestamp': datetime.now().isoformat(),
        'status': 'new'
    })
    
    with open(MESSAGES_FILE, 'w') as f:
        json.dump(messages, f)

@app.route('/')
def home():
    if 'authenticated' not in session:
        return render_template('login.html')
    return render_template('index.html')

@app.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    if data.get('passcode') == PASSCODE:
        session['authenticated'] = True
        return jsonify({'success': True})
    return jsonify({'success': False, 'error': 'Invalid passcode'}), 401

@app.route('/submit', methods=['POST'])
def submit_message():
    if 'authenticated' not in session:
        return jsonify({'success': False, 'error': 'Not authenticated'}), 401
    
    data = request.get_json()
    message = data.get('message')
    
    if not message:
        return jsonify({'success': False, 'error': 'No message provided'}), 400
    
    save_message(message)
    return jsonify({'success': True})

@app.route('/get_messages', methods=['GET'])
def get_messages():
    if not os.path.exists(MESSAGES_FILE):
        return jsonify([])
    
    with open(MESSAGES_FILE, 'r') as f:
        messages = json.load(f)
    return jsonify(messages)

@app.route('/mark_processed', methods=['POST'])
def mark_processed():
    data = request.get_json()
    timestamp = data.get('timestamp')
    
    if not timestamp:
        return jsonify({'success': False, 'error': 'No timestamp provided'}), 400
    
    if os.path.exists(MESSAGES_FILE):
        with open(MESSAGES_FILE, 'r') as f:
            messages = json.load(f)
        
        for message in messages:
            if message['timestamp'] == timestamp:
                message['status'] = 'processed'
        
        with open(MESSAGES_FILE, 'w') as f:
            json.dump(messages, f)
    
    return jsonify({'success': True})

@app.route('/test_message', methods=['GET'])
def test_message():
    return jsonify([{
        'message': 'Test message',
        'timestamp': datetime.now().isoformat(),
        'status': 'new'
    }])

if __name__ == '__main__':
    port = int(os.environ.get("PORT", 5000))  # Default to 5000 if PORT not set
    app.run(debug=True, host='0.0.0.0', port=port) 