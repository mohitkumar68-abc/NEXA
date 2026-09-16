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

public class MainActivity extends Activity {

    LinearLayout messagesLayout;
    EditText input;

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
        welcome.setText("Hello! 👋\\nMain NEXA hoon.\\n\\nTum mujhe message bhej sakte ho.");
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

                if (!message.isEmpty()) {
                    TextView userMessage = new TextView(MainActivity.this);
                    userMessage.setText("You: " + message);
                    userMessage.setTextSize(17);
                    userMessage.setTextColor(Color.BLACK);
                    userMessage.setPadding(20, 16, 20, 16);

                    messagesLayout.addView(userMessage);

                    input.setText("");
                }
            }
        });

        setContentView(root);
    }
}
