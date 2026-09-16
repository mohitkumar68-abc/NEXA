from flask import Flask, request, jsonify
import os
import requests

app = Flask(__name__)

conversation = []

SYSTEM_PROMPT = """
You are NEXA, Mohit's personal AI assistant.
Always talk to Mohit in simple Hindi/Hinglish.
Be friendly, helpful, and explain things step-by-step.
Remember useful information from the current conversation.
"""

@app.route("/chat", methods=["POST"])
def chat():
    data = request.get_json() or {}
    message = data.get("message", "").strip()

    if not message:
        return jsonify({"error": "Message required"}), 400

    conversation.append({
        "role": "user",
        "content": message
    })

    input_messages = [
        {
            "role": "system",
            "content": SYSTEM_PROMPT
        }
    ] + conversation

    try:
        response = requests.post(
            "https://api.openai.com/v1/responses",
            headers={
                "Authorization": f"Bearer {os.environ['OPENAI_API_KEY']}",
                "Content-Type": "application/json"
            },
            json={
                "model": "gpt-5.6-luna",
                "input": input_messages
            },
            timeout=60
        )

        result = response.json()

        if not response.ok:
            return jsonify(result), response.status_code

        reply = result["output"][0]["content"][0]["text"]

        conversation.append({
            "role": "assistant",
            "content": reply
        })

        return jsonify({
            "reply": reply
        })

    except Exception as e:
        return jsonify({
            "error": str(e)
        }), 500


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000)
