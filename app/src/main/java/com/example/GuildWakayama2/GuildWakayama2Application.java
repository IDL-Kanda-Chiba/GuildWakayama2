package com.example.GuildWakayama2;

import android.app.Application;

public class GuildWakayama2Application extends Application {
    private static GuildWakayama2Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static GuildWakayama2Application getInstance() {
        return instance;
    }
}
