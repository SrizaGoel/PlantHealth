from flask import Flask, request, jsonify
import random, os

app = Flask(__name__)
os.makedirs("./uploads", exist_ok=True)

@app.route('/predict', methods=['POST'])
def predict():
    file = request.files['image']
    path = f"./uploads/{file.filename}"
    file.save(path)
    
    diseases = ["Healthy", "Leaf Spot", "Blight", "Rust"]
    status = random.choice(diseases)
    confidence = round(random.uniform(0.7, 0.99), 2)
    
    return jsonify({"status": status, "confidence": confidence})

if __name__ == '__main__':
    app.run(port=5000, debug=True)
