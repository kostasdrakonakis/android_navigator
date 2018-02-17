package com.github.kostasdrakonakis.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.github.kostasdrakonakis.androidnavigator.IntentNavigator;
import com.github.kostasdrakonakis.androidnavigator.IntentNavigatorBinder;
import com.github.kostasdrakonakis.annotation.Intent;
import com.github.kostasdrakonakis.annotation.IntentCategory;
import com.github.kostasdrakonakis.annotation.IntentCategoryType;
import com.github.kostasdrakonakis.annotation.IntentExtra;
import com.github.kostasdrakonakis.annotation.IntentProperty;
import com.github.kostasdrakonakis.annotation.IntentType;

import static android.text.TextUtils.isEmpty;

@Intent(
        value = {
                @IntentExtra(type = IntentType.INT, parameter = "id"),
                @IntentExtra(type = IntentType.STRING, parameter = "name"),
                @IntentExtra(type = IntentType.STRING, parameter = "title")
        }, categories = {
                @IntentCategory(IntentCategoryType.CATEGORY_DEFAULT)
})
public class MainActivity extends AppCompatActivity {

    @IntentProperty("id")
    public int myId;
    @IntentProperty("name")
    public String name;
    @IntentProperty("title")
    public String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentNavigatorBinder.bind(this);

        TextView textView = findViewById(R.id.main_text);

        if (myId > 0 && !isEmpty(title) && !isEmpty(name)) {
            String text = "Id: " + myId + " Title: " + title + " Name: " + name;
            textView.setText(text);
        }

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentNavigator.startSecondActivity(MainActivity.this);
            }
        });
    }
}
