package com.github.kostasdrakonakis.example;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.kostasdrakonakis.androidnavigator.IntentNavigator;
import com.github.kostasdrakonakis.annotation.Intent;
import com.github.kostasdrakonakis.annotation.IntentService;
import com.github.kostasdrakonakis.annotation.ServiceType;

@Intent
@IntentService(ServiceType.FOREGROUND)
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
        intent.addCategory("1");
        findViewById(R.id.second_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentNavigator.startMainActivity(SecondActivity.this, 2, "Hello", "Nope");
            }
        });
    }
}
