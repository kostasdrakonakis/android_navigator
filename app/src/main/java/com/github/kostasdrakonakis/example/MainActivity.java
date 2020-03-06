package com.github.kostasdrakonakis.example;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.kostasdrakonakis.androidnavigator.IntentNavigator;
import com.github.kostasdrakonakis.androidnavigator.IntentNavigatorBinder;
import com.github.kostasdrakonakis.annotation.Intent;
import com.github.kostasdrakonakis.annotation.IntentCategory;
import com.github.kostasdrakonakis.annotation.IntentCategoryType;
import com.github.kostasdrakonakis.annotation.IntentExtra;
import com.github.kostasdrakonakis.annotation.IntentFlag;
import com.github.kostasdrakonakis.annotation.IntentFlagType;
import com.github.kostasdrakonakis.annotation.IntentProperty;
import com.github.kostasdrakonakis.annotation.IntentType;

import static android.text.TextUtils.isEmpty;

@Intent(value = {
        @IntentExtra(type = IntentType.INT, parameter = "id"),
        @IntentExtra(type = IntentType.STRING, parameter = "name"),
        @IntentExtra(type = IntentType.CHAR, parameter = "mine"),
        @IntentExtra(type = IntentType.STRING, parameter = "title"),
        @IntentExtra(type = IntentType.LONG, parameter = "oneLong"),
        @IntentExtra(type = IntentType.BOOLEAN, parameter = "aBoolean"),
        @IntentExtra(type = IntentType.DOUBLE, parameter = "aDouble"),
        @IntentExtra(type = IntentType.FLOAT, parameter = "singleFloat")
}, categories = {
        @IntentCategory(IntentCategoryType.CATEGORY_DEFAULT)
}, flags = {
        @IntentFlag(IntentFlagType.FLAG_ACTIVITY_CLEAR_TOP)
}, type = "message/rfc822"
)
public class MainActivity extends AppCompatActivity {

    @IntentProperty(value = "id", intDefaultValue = 1)
    public int myId;
    @IntentProperty("name")
    String name;
    @IntentProperty("title")
    public String title;
    @IntentProperty("mine")
    public char myChar;
    @IntentProperty("oneLong")
    public long oneLong;
    @IntentProperty("aBoolean")
    public boolean aBoolean;
    @IntentProperty("aDouble")
    public double aDouble;
    @IntentProperty("singleFloat")
    public float singleFloat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentNavigatorBinder.bind(this);

        TextView textView = findViewById(R.id.main_text);

        if (myId > 0 && !isEmpty(title) && !isEmpty(name) && myChar != '\u0000') {
            String text = "Id: " + myId + " Title: " + title + " Name: " + name + " char: " + myChar;
            textView.setText(text);
        }

        textView.setOnClickListener(v -> IntentNavigator.startSecondActivity(MainActivity.this));
    }
}
