package com.nexa.ai;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView text = new TextView(this);
        text.setText("NEXA\n\nYour Personal AI Assistant");
        text.setTextSize(24);
        text.setPadding(40, 40, 40, 40);

        setContentView(text);
    }
}
