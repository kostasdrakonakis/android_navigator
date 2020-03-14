package test;

import com.github.kostasdrakonakis.annotation.Intent;
import android.app.Activity;
import com.github.kostasdrakonakis.annotation.IntentExtra;
import com.github.kostasdrakonakis.annotation.IntentProperty;
import com.github.kostasdrakonakis.annotation.IntentType;

@Intent(value = {
        @IntentExtra(type = IntentType.INT, parameter = "id")
})
public class MyActivity extends Activity {
    @IntentProperty("id")
    int myId;
}