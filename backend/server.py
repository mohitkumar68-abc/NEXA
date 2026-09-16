from flask import Flask, request, jsonify
import os
import requests

app = Flask(__name__)

@app.route("/chat", methods=["POST"])
def chat():
    data = request.get_json()
    message = data.get("message", "").strip()

    if not message:
        return jsonify({"error": "Message required"}), 400

    response = requests.post(
        "https://api.openai.com/v1/responses",
        headers={
            "Authorization": f"Bearer {os.environ['OPENAI_API_KEY']}",
            "Content-Type": "application/json"
        },
        json={
            "model": "gpt-5.6-luna",
            "input": message
        }
    )

    result = response.json()

    if not response.ok:
        return jsonify(result), response.status_code

    return jsonify({
        "reply": result["output"][0]["content"][0]["text"]
    })

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000)
