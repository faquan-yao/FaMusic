package com.yaofaquan.lib_audio.app;

import android.content.Context;

import com.yaofaquan.lib_audio.mediaplayer.core.MusicService;
import com.yaofaquan.lib_audio.mediaplayer.model.AudioBean;

import java.util.ArrayList;

public final class AudioHelper {
    private static Context mContext;
    public static void init(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }

    public static void startMusicService(ArrayList<AudioBean> mLocalDataList) {
        MusicService.startMusicService(mLocalDataList);
    }
}
