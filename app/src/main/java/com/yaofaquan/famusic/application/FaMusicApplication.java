package com.yaofaquan.famusic.application;

import android.app.Application;

import com.yaofaquan.lib_audio.app.AudioHelper;
import com.yaofaquan.lib_audio.mediaplayer.db.GreenDaoHelper;
import com.yaofaquan.lib_pullalive.app.AliveJobService;

public class FaMusicApplication extends Application {
    private static FaMusicApplication sInstance = null;
    @Override
    public void onCreate() {
        super.onCreate();
        AudioHelper.init(this);
        GreenDaoHelper.initDataBase();
        AliveJobService.start(this);
    }

    public static FaMusicApplication getInstance() {
        return sInstance;
    }
}
