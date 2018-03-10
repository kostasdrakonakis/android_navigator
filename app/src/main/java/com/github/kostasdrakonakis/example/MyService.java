package com.github.kostasdrakonakis.example;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.github.kostasdrakonakis.annotation.IntentService;
import com.github.kostasdrakonakis.annotation.ServiceType;

@IntentService(ServiceType.FOREGROUND)
public class MyService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
