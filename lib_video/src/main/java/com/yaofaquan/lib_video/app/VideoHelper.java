package com.yaofaquan.lib_video.app;

import android.content.Context;

public final class VideoHelper {

    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }
}
