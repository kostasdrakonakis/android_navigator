package com.github.kostasdrakonakis.example;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.kostasdrakonakis.androidnavigator.IntentNavigator;
import com.github.kostasdrakonakis.annotation.Intent;

@Intent
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
        intent.addCategory("1");
        findViewById(R.id.second_text).setOnClickListener(v -> {
            char ch = 'a';
            IntentNavigator.startMainActivity(SecondActivity.this, 2, "Hello", ch, "Nope", 1L, false, 10.0, 4F);
        });
    }
}
