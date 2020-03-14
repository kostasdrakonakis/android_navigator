package test;

import com.github.kostasdrakonakis.androidnavigator.IntentNavigator;

public final class MyActivity_INTENT_PROPERTY_BINDING {
  private MyActivity_INTENT_PROPERTY_BINDING() {
    throw new UnsupportedOperationException("No instances");
  }

  MyActivity_INTENT_PROPERTY_BINDING(MyActivity activity) {
    activity.myId = activity.getIntent().getIntExtra(IntentNavigator.EXTRA_MYNACTIVITY_ID, 1);
  }
}
