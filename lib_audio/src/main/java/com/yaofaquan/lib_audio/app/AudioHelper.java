package com.yaofaquan.lib_audio.app;

import android.content.Context;

public final class AudioHelper {
    private static Context mContext;
    public static void init(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }
}
