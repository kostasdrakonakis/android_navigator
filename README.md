Android Navigator

Removes the boilerplate code when it comes to create intents for navigating between Activities.

Usage
-----

You can use it with Intent extras like this:

```java

@Intent({
        @IntentExtra(type = IntentType.INT, typeValue = "id"),
        @IntentExtra(type = IntentType.STRING, typeValue = "name"),
        @IntentExtra(type = IntentType.STRING, typeValue = "title")
})
```

Or if you just want the Intent without extras like this:


```java
@Intent
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

License
-------

 Copyright 2017 Kostas Drakonakis

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
