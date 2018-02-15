package com.github.kostasdrakonakis.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.github.kostasdrakonakis.androidnavigator.IntentNavigator;
import com.github.kostasdrakonakis.annotation.Intent;
import com.github.kostasdrakonakis.annotation.IntentExtra;
import com.github.kostasdrakonakis.annotation.IntentType;

import static android.text.TextUtils.isEmpty;

@Intent({
        @IntentExtra(type = IntentType.INT, typeValue = "id"),
        @IntentExtra(type = IntentType.STRING, typeValue = "name"),
        @IntentExtra(type = IntentType.STRING, typeValue = "title")
})
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int id = getIntent().getIntExtra(IntentNavigator.EXTRA_MAINACTIVITY_ID, 0);
        String name = getIntent().getStringExtra(IntentNavigator.EXTRA_MAINACTIVITY_NAME);
        String title = getIntent().getStringExtra(IntentNavigator.EXTRA_MAINACTIVITY_TITLE);

        TextView textView = findViewById(R.id.main_text);

        if (id > 0 && !isEmpty(title) && !isEmpty(name)) {
            String text = "Id: " + id + " Title: " + title + " Name: " + name;
            textView.setText(text);
        }

        findViewById(R.id.main_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentNavigator.startSecondActivity(MainActivity.this);
            }
        });
    }
}
