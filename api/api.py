from flask import Flask, request, jsonify
from flask_cors import CORS
import google.generativeai as genai
from dotenv import load_dotenv
import os

# Load .env if available
load_dotenv()

app = Flask(__name__)
CORS(app)

# Set your Gemini API key
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY", "AIzaSyAoGoKYw9Tvtl1yC-g6Q1Me4ZSeSdAQEnM")

genai.configure(api_key=GEMINI_API_KEY)
model = genai.GenerativeModel('gemini-2.0-flash')

# Actual Gemini Integration
def get_gemini_advice(user_summary):
    try:
        prompt = f"financial tips based on this summary: {user_summary}"
        response = model.generate_content([prompt])
        return response.text
    except Exception as e:
        return f"Error: {str(e)}"

# Route to get advice
@app.route('/financial-advice', methods=['POST'])
def get_financial_advice():
    try:
        data = request.json
        user_summary = data.get("summary", "")

        if not user_summary:
            return jsonify({"error": "Summary is required"}), 400

        advice = get_gemini_advice(user_summary)
        print(advice)
        return advice

    except Exception as e:
        print(e)
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)
