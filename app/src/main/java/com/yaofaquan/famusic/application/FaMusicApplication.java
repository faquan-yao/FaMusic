package com.yaofaquan.famusic.application;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.yaofaquan.famusic.BuildConfig;
import com.yaofaquan.lib_audio.app.AudioHelper;
import com.yaofaquan.lib_audio.mediaplayer.db.GreenDaoHelper;

public class FaMusicApplication extends Application {
    private static FaMusicApplication sInstance = null;
    @Override
    public void onCreate() {
        super.onCreate();
        AudioHelper.init(this);
        GreenDaoHelper.initDataBase();
        //AliveJobService.start(this);
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.debuggable();
        }
        ARouter.init(this);
    }

    public static FaMusicApplication getInstance() {
        return sInstance;
    }
}
