'''
from flask import Flask, request, jsonify

app = Flask(__name__)

# Dummy prediction function
def predict_plant_health(file_path):
    # You can make this smarter or replace with actual model later
    import random
    statuses = ["Healthy", "Diseased", "Warning"]
    status = random.choice(statuses)
    confidence = round(random.uniform(0.7, 0.99), 2)
    return status, confidence

@app.route('/predict', methods=['POST'])
def predict():
    if 'image' not in request.files:
        return jsonify({"status": "Error", "confidence": 0.0})

    image_file = request.files['image']
    # Save temporarily
    file_path = f"./temp_{image_file.filename}"
    image_file.save(file_path)

    status, confidence = predict_plant_health(file_path)

    # Delete temp file if you want
    import os
    os.remove(file_path)

    return jsonify({"status": status, "confidence": confidence})

if __name__ == '__main__':
    app.run(host='127.0.0.1', port=5000, debug=True)
'''

from flask import Flask, request, jsonify
from flask_cors import CORS  # Important for cross-origin requests
import os
import random

app = Flask(__name__)
CORS(app)  # Enable CORS for all routes

# Dummy prediction function - replace with your actual ML model later
def predict_plant_health(file_path):
    try:
        # Simulate some processing
        statuses = ["Healthy", "Diseased", "Warning", "Needs Attention"]
        status = random.choice(statuses)
        confidence = round(random.uniform(0.7, 0.99), 2)
        
        print(f"✅ Processed image: {file_path}")
        print(f"✅ Prediction: {status} with {confidence} confidence")
        
        return status, confidence
    except Exception as e:
        print(f"❌ Prediction error: {e}")
        return "Error", 0.0

@app.route('/predict', methods=['POST'])
def predict():
    try:
        print("🎯 Received prediction request from Spring Boot")
        
        if 'image' not in request.files:
            print("❌ No image file in request")
            return jsonify({"status": "Error", "confidence": 0.0, "error": "No image file"})

        image_file = request.files['image']
        print(f"📁 Received file: {image_file.filename}, Size: {len(image_file.read())} bytes")
        
        # Reset file pointer after reading length
        image_file.seek(0)
        
        # Save temporarily with unique name
        temp_dir = "temp_uploads"
        if not os.path.exists(temp_dir):
            os.makedirs(temp_dir)
            
        file_path = os.path.join(temp_dir, f"temp_{random.randint(1000, 9999)}_{image_file.filename}")
        image_file.save(file_path)
        
        print(f"💾 Saved temporary file: {file_path}")

        # Get prediction
        status, confidence = predict_plant_health(file_path)

        # Clean up temp file
        try:
            os.remove(file_path)
            print(f"🗑️  Cleaned up temp file: {file_path}")
        except:
            pass

        response = {
            "status": status, 
            "confidence": confidence,
            "message": "Analysis completed successfully"
        }
        
        print(f"✅ Sending response: {response}")
        return jsonify(response)

    except Exception as e:
        print(f"❌ Flask server error: {e}")
        return jsonify({
            "status": "Error", 
            "confidence": 0.0, 
            "error": str(e)
        })

@app.route('/health', methods=['GET'])
def health_check():
    print("🔍 Health check received")
    return jsonify({"status": "Flask server is running!", "timestamp": "now"})

if __name__ == '__main__':
    print("🚀 Starting Flask ML Server on http://127.0.0.1:5000")
    print("📌 Endpoints:")
    print("   POST http://127.0.0.1:5000/predict")
    print("   GET  http://127.0.0.1:5000/health")
    app.run(host='127.0.0.1', port=5000, debug=True)