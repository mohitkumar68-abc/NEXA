package com.nexa.ai;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {

    LinearLayout messagesLayout;
    EditText input;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static final String BACKEND_URL =
            "https://nexa-yjyv.onrender.com/chat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);

        TextView header = new TextView(this);
        header.setText("NEXA");
        header.setTextSize(24);
        header.setTypeface(null, Typeface.BOLD);
        header.setTextColor(Color.BLACK);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(32, 20, 32, 20);

        root.addView(header, new LinearLayout.LayoutParams(
                -1, 70
        ));

        ScrollView scrollView = new ScrollView(this);

        messagesLayout = new LinearLayout(this);
        messagesLayout.setOrientation(LinearLayout.VERTICAL);
        messagesLayout.setPadding(24, 24, 24, 24);

        TextView welcome = new TextView(this);
        welcome.setText("Hello! 👋\nMain NEXA hoon.\n\nTum mujhe message bhej sakte ho.");
        welcome.setTextSize(18);
        welcome.setTextColor(Color.DKGRAY);
        welcome.setPadding(20, 20, 20, 20);

        messagesLayout.addView(welcome);
        scrollView.addView(messagesLayout);

        root.addView(scrollView, new LinearLayout.LayoutParams(
                -1, 0, 1
        ));

        LinearLayout inputLayout = new LinearLayout(this);
        inputLayout.setOrientation(LinearLayout.HORIZONTAL);
        inputLayout.setPadding(16, 12, 16, 16);

        input = new EditText(this);
        input.setHint("Message NEXA...");
        input.setTextSize(16);
        input.setSingleLine(false);

        Button send = new Button(this);
        send.setText("Send");

        inputLayout.addView(input, new LinearLayout.LayoutParams(
                0, -2, 1
        ));

        inputLayout.addView(send, new LinearLayout.LayoutParams(
                -2, -2
        ));

        root.addView(inputLayout);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = input.getText().toString().trim();

                if (message.isEmpty()) {
                    return;
                }

                addMessage("You: " + message, Color.BLACK);
                input.setText("");

                send.setEnabled(false);

                executor.execute(() -> {

                    String reply = sendMessage(message);

                    runOnUiThread(() -> {
                        addMessage("NEXA: " + reply, Color.DKGRAY);
                        send.setEnabled(true);
                    });
                });
            }
        });

        setContentView(root);
    }

    private String sendMessage(String message) {

        HttpURLConnection connection = null;

        try {

            URL url = new URL(BACKEND_URL);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty(
                    "Content-Type",
                    "application/json; charset=UTF-8"
            );
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(30000);
            connection.setDoOutput(true);

            JSONObject requestJson = new JSONObject();
            requestJson.put("message", message);

            byte[] data = requestJson.toString()
                    .getBytes(StandardCharsets.UTF_8);

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();

            BufferedReader reader;

            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(
                        new InputStreamReader(
                                connection.getInputStream(),
                                StandardCharsets.UTF_8
                        )
                );
            } else {
                reader = new BufferedReader(
                        new InputStreamReader(
                                connection.getErrorStream(),
                                StandardCharsets.UTF_8
                        )
                );
            }

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            if (responseCode >= 200 && responseCode < 300) {

                JSONObject json = new JSONObject(response.toString());

                return json.optString(
                        "reply",
                        "NEXA ko response nahi mila."
                );

            } else {

                return "Backend error: HTTP " + responseCode;
            }

        } catch (Exception e) {

            return "Connection error: " + e.getMessage();

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void addMessage(String text, int color) {

        TextView messageView = new TextView(this);

        messageView.setText(text);
        messageView.setTextSize(17);
        messageView.setTextColor(color);
        messageView.setPadding(20, 16, 20, 16);

        messagesLayout.addView(messageView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}


