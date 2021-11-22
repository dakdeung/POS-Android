package com.example.skripsi_kamal;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.example.skripsi_kamal.config.SharedConfig;
import com.example.skripsi_kamal.config.UserConfig;

import de.hdodenhof.circleimageview.BuildConfig;
import timber.log.Timber;

public class BaseApp extends Application {
    @SuppressLint("StaticFieldLeak")
    public static volatile Context applicationContext;

    @SuppressLint("StaticFieldLeak")
    public static volatile Handler applicationHandler;

    @SuppressLint("StaticFieldLeak")
    private static volatile boolean applicationInited = false;


    public BaseApp() {
        super();
    }

    public static void postInitApplication() {
        if (applicationInited) {
            return;
        }
        applicationInited = true;
    }

    @Override
    public void onCreate() {
        try {
            applicationContext = getApplicationContext();
        } catch (Throwable ignore) {
            Timber.e(ignore);
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        super.onCreate();
        if (applicationContext == null) {
            applicationContext = getApplicationContext();
        }
        // Start Configuration
        SharedConfig.loadConfig();
        UserConfig.getInstance().loadConfig();
        applicationHandler = new Handler(applicationContext.getMainLooper());
    }
}

