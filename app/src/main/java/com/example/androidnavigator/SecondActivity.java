package com.example.androidnavigator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.androidnavigator.annotation.Navigate;

@Navigate
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        findViewById(R.id.second_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.startMainActivity(SecondActivity.this, 1, "Hello", "Nope");
            }
        });
    }
}
