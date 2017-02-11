package com.tec.zhang;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SimpleDisplay extends AppCompatActivity {
    private TextView detail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_display);
        detail = (TextView) findViewById(R.id.textView10);
        String data = getIntent().getExtras().getString("pro");
        detail.setText(data);
    }
}
