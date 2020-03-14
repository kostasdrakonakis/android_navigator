package com.github.kostasdrakonakis.androidnavigator;

import android.content.Context;
import android.content.Intent;
import java.lang.String;

public final class IntentNavigator {
  public static final String EXTRA_MYACTIVITY_ID = "EXTRA_MYACTIVITY_ID";

  private IntentNavigator() {
    throw new UnsupportedOperationException("No instances");
  }

  public static void startMyActivity(Context context, int id) {
    Intent intent = new Intent(context, test.MyActivity.class);
    intent.putExtra(EXTRA_MYACTIVITY_ID, id);
    context.startActivity(intent);
  }
}
