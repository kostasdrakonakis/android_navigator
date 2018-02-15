package com.example.androidnavigator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.androidnavigator.annotation.Navigate;

import static android.text.TextUtils.isEmpty;

@Navigate({"int:promotionId", "String:promotionName", "String:title"})
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int promotionId = getIntent().getIntExtra(Navigator.EXTRA_MAINACTIVITY_PROMOTIONID, 0);
        String name = getIntent().getStringExtra(Navigator.EXTRA_MAINACTIVITY_PROMOTIONNAME);
        String title = getIntent().getStringExtra(Navigator.EXTRA_MAINACTIVITY_TITLE);
        TextView textView = findViewById(R.id.main_text);
        if (promotionId > 0 && !isEmpty(title) && !isEmpty(name)) {
            String text = "Promotion id: " + promotionId + " Title: " + title + " Name: " + name;
            textView.setText(text);
        }
        findViewById(R.id.main_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.startSecondActivity(MainActivity.this);
            }
        });
    }
}
