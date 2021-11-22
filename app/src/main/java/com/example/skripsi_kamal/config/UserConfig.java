package com.example.skripsi_kamal.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.example.skripsi_kamal.BaseApp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

import timber.log.Timber;

public class UserConfig {
    private static final Object OBJ_SYNC = new Object();
    private static volatile UserConfig Instance = new UserConfig();

    private String authToken;

    private String authEmail;

    private boolean configLoaded;

    public static UserConfig getInstance() {
        UserConfig localInstance = Instance;
        if (localInstance == null) {
            synchronized (UserConfig.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new UserConfig();
                }
            }
        }
        return localInstance;
    }

    private SharedPreferences getPreferences() {
        return BaseApp
                .applicationContext
                .getSharedPreferences("USERPREF", Context.MODE_PRIVATE);
    }

    /**
     * Object Customer.
     */
    private UserObject currentUser;

    public boolean isClientActivated() {
        synchronized (OBJ_SYNC) {
            return currentUser != null;
        }
    }

    public String getUserEmail() {
        synchronized (OBJ_SYNC) {
            return currentUser != null ? currentUser.getUserEmail() : "";
        }
    }

    public String getUserToken() {
        synchronized (OBJ_SYNC) {
            return currentUser != null ? currentUser.getUserToken() : "";
        }
    }

    public String getUserId() {
        synchronized (OBJ_SYNC) {
            return currentUser != null ? currentUser.getUserId() : "";
        }
    }

    public void setCurrentUser(UserObject user) {
        synchronized (OBJ_SYNC) {
            currentUser = user;
            authToken = user.getUserToken();
            authEmail = user.getUserEmail();
        }
    }

    public void loadConfig() {
        synchronized (OBJ_SYNC) {
            if (configLoaded) {
                return;
            }
            SharedPreferences preferences = getPreferences();
            authToken = preferences.getString("auth_token", "");
            authEmail = preferences.getString("auth_email", "");

            String myCurrentUser = preferences.getString("user", null);

            if (myCurrentUser != null) {
                byte[] bytes = Base64.decode(myCurrentUser, Base64.DEFAULT);
                String decryptedText = new String(bytes);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                currentUser = gson.fromJson(decryptedText, UserObject.class);
            }
            configLoaded = true;
        }
    }

    public void saveConfig(boolean withFile) {
        saveConfig(withFile, null);
    }

    public void saveConfig(boolean withFile, File oldFile) {
        synchronized (OBJ_SYNC) {
            try {
                SharedPreferences.Editor editor = getPreferences().edit();
                editor.putString("auth_token", authToken);
                editor.putString("auth_email", authEmail);
                SharedConfig.saveConfig();

                // Jika user telah login.
                if (currentUser != null) {
                    if (withFile) {
                        Gson gson = new GsonBuilder().create();
                        String data = gson.toJson(currentUser);
                        String string = Base64
                                .encodeToString(data.getBytes(), Base64.DEFAULT);
                        Timber.d("ENCODED_CONFIG %s", string);
                        editor.putString("user", string);
                    }
                } else {
                    editor.remove("user");
                }

                editor.commit();
                if (oldFile != null) {
                    oldFile.delete();
                }
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    public void clearConfig() {
        getPreferences().edit().clear().commit();
        currentUser = null;
        authToken = "";
        authEmail = "";
        SharedConfig.clearConfig();
        saveConfig(true);
    }
}
