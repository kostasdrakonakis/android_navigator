Android Navigator [ ![Download](https://api.bintray.com/packages/kdrakonakis/maven/android-navigator/images/download.svg) ](https://bintray.com/kdrakonakis/maven/android-navigator/_latestVersion)


Removes the boilerplate code when it comes to create intents for navigating between Activities.

Download
--------

Download the latest JAR or grab via Maven:
```xml
<dependency>
  <groupId>com.github.kostasdrakonakis</groupId>
  <artifactId>android-navigator</artifactId>
  <version>1.2.3</version>
</dependency>

<dependency>
  <groupId>com.github.kostasdrakonakis</groupId>
  <artifactId>android-navigator-compiler</artifactId>
  <version>1.2.3</version>
</dependency>
```
or Gradle:
```groovy
implementation 'com.github.kostasdrakonakis:android-navigator:1.2.3'
annotationProcessor 'com.github.kostasdrakonakis:android-navigator-compiler:1.2.3'
```

Usage
-----

You can use it with Intent extras like this:

```java

@Intent({
        @IntentExtra(type = IntentType.INT, parameter = "id"),
        @IntentExtra(type = IntentType.STRING, parameter = "name"),
        @IntentExtra(type = IntentType.STRING, parameter = "title")
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
    }
}
```

Or if you just want the Intent without extras like this:

```java
@Intent
public class MainActivity extends AppCompatActivity {
}
```

You can also bind data members to parameters like this:

```java

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        findViewById(R.id.second_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentNavigator.startMainActivity(SecondActivity.this, 2, "Hello", "Nope");
            }
        });
    }
}


@Intent({
        @IntentExtra(type = IntentType.INT, parameter = "id"),
        @IntentExtra(type = IntentType.STRING, parameter = "name"),
        @IntentExtra(type = IntentType.STRING, parameter = "title")
})
public class MainActivity extends AppCompatActivity {

	@IntentProperty("id")
	public int myId;
	@IntentProperty("name")
	String name;
	@IntentProperty("title")
	public String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		// Here you just bind
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
```

You can also add Category or Flags to @Intent like this:

```java
@Intent(
    categories = {
        @IntentCategory(IntentCategoryType.CATEGORY_DEFAULT)
    },
    flags = {
        @IntentFlag(IntentFlagType.FLAG_ACTIVITY_CLEAR_TOP)
    }
)
```

You can also set type to @Intent like this:

```java
@Intent(type = "message/rfc822")
```

You can also start Service as foreground or background and also add all the properties applied to @Intent as well like this:

```java
@IntentService(ServiceType.FOREGROUND) or @IntentService(ServiceType.BACKGROUND)

@IntentService(value = ServiceType.FOREGROUND, extras = {
        @IntentExtra(type = IntentType.INT, parameter = "id"),
        @IntentExtra(type = IntentType.STRING, parameter = "name"),
        @IntentExtra(type = IntentType.STRING, parameter = "title")
})
```

You can see the currently supported IntentTypes here:

```java
public enum IntentType {
    INT,
    LONG,
    FLOAT,
    SHORT,
    STRING,
    BOOLEAN,
    BYTE,
    CHAR,
    DOUBLE,
    BOOLEAN_ARRAY,
    BYTE_ARRAY,
    CHAR_ARRAY,
    CHAR_SEQUENCE,
    CHAR_SEQUENCE_ARRAY,
    LONG_ARRAY,
    INT_ARRAY,
    SHORT_ARRAY,
    STRING_ARRAY,
    SERIALIZABLE
}
```

CHANGELOG
----
**v1.2.3**:
* Fix issue with char intent binding

**v1.2.2**:
* Add support for property binding for public, protected, package-private visibility modifiers
* Add support for modifying default value when property binding

**v1.2.1**:
* Disabled instantiation of IntentNavigator class and disabled inheritance

**v1.2.0**:
* Add support for Intent types

**v1.1.0**:
* Add support for Android classes
* Add support for Intent flags, Intent Categories

TODO
----

+ Add Actions
+ Add support for startActivityForResult

Feel free to submit PR's. Also open to suggestions!

License
-------

 Copyright 2018 Kostas Drakonakis

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
