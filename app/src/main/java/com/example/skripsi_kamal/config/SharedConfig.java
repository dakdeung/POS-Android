package com.example.skripsi_kamal.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.skripsi_kamal.BaseApp;

import timber.log.Timber;

public class SharedConfig {
    private static final Object OBJ_SYNC = new Object();
    /**
     * Cek apakah configuration sudah ter-load.
     */
    private static boolean configLoaded;

    private static String deviceOs = "Android";

    static {
        loadConfig();
    }

    public static void loadConfig() {
        synchronized (OBJ_SYNC) {
            if (configLoaded) {
                return;
            }
            SharedPreferences preferences = BaseApp.applicationContext
                    .getSharedPreferences("USERPREF", Context.MODE_PRIVATE);
            deviceOs = preferences.getString("device_os", "");
            // Flag bahwa configurasi sudah diload
            configLoaded = true;
        }
    }
    @SuppressLint("ApplySharedPref")
    public static void saveConfig() {

        synchronized (OBJ_SYNC) {
            try {
                SharedPreferences preferences = BaseApp
                        .applicationContext
                        .getSharedPreferences("USERPREF", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putString("device_os", deviceOs);

                //Save config
                editor.commit();

            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    @SuppressLint("ApplySharedPref")
    public static void clearConfig() {
        saveConfig();
    }
}
