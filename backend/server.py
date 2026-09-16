from flask import Flask, request, jsonify
import os
import requests
import json

app = Flask(__name__)

MEMORY_FILE = "memory.json"

SYSTEM_PROMPT = """
You are NEXA, Mohit's personal AI assistant.
Always talk to Mohit in simple Hindi/Hinglish.
Be friendly, helpful, and explain things step-by-step.
Use the conversation history to remember useful information.
"""

def load_memory():
    try:
        with open(MEMORY_FILE, "r", encoding="utf-8") as f:
            return json.load(f)
    except:
        return []

def save_memory(memory):
    with open(MEMORY_FILE, "w", encoding="utf-8") as f:
        json.dump(memory, f, ensure_ascii=False, indent=2)

@app.route("/chat", methods=["POST"])
def chat():
    data = request.get_json() or {}
    message = data.get("message", "").strip()

    if not message:
        return jsonify({"error": "Message required"}), 400

    memory = load_memory()

    memory.append({
        "role": "user",
        "content": message
    })

    input_messages = [
        {
            "role": "system",
            "content": SYSTEM_PROMPT
        }
    ] + memory

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

        memory.append({
            "role": "assistant",
            "content": reply
        })

        save_memory(memory)

        return jsonify({"reply": reply})

    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000)
