package com.yaofaquan.lib_audio.mediaplayer.core;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.yaofaquan.lib_audio.app.AudioHelper;
import com.yaofaquan.lib_audio.mediaplayer.events.AudioFavouriteEvent;
import com.yaofaquan.lib_audio.mediaplayer.events.AudioLoadEvent;
import com.yaofaquan.lib_audio.mediaplayer.events.AudioPauseEvent;
import com.yaofaquan.lib_audio.mediaplayer.events.AudioReleaseEvent;
import com.yaofaquan.lib_audio.mediaplayer.events.AudioStartEvent;
import com.yaofaquan.lib_audio.mediaplayer.model.AudioBean;
import com.yaofaquan.lib_audio.mediaplayer.view.NotificationHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import static com.yaofaquan.lib_audio.mediaplayer.view.NotificationHelper.NOTIFICATION_ID;

public class MusicService extends Service implements NotificationHelper.NotificationHelperListener {

    private static final String TAG = "MusicService";
    private static String DATA_AUDIOS = "AUDIOS";
    private static String ACTION_START = "ACTION_START";

    private ArrayList<AudioBean> mAudioBeans = new ArrayList<>();
    private NotificationReceiver mReceiver = null;

    public static void startMusicService(ArrayList<AudioBean> audioBeans) {
        Intent intent = new Intent(AudioHelper.getContext(), MusicService.class);
        intent.setAction(ACTION_START);
        intent.putExtra(DATA_AUDIOS, audioBeans);
        AudioHelper.getContext().startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        registerBroadcastReceiver();
    }

    private void registerBroadcastReceiver() {
        if (mReceiver == null) {
            mReceiver = new NotificationReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(NotificationReceiver.ACTION_STATUS_BAR);
            registerReceiver(mReceiver, filter);
        }
    }

    private void unRegisterBroadcastReceiver() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mAudioBeans = (ArrayList<AudioBean>) intent.getSerializableExtra(DATA_AUDIOS);
        if (intent.getAction().equals(ACTION_START)) {
            playMusic();
            NotificationHelper.getInstance().init(this);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void playMusic() {
        AudioController.getInstance().setQueue(mAudioBeans);
        AudioController.getInstance().play();
    }

    @Override
    public void onNotificationInit() {
        startForeground(NOTIFICATION_ID, NotificationHelper.getInstance().getNotification());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unRegisterBroadcastReceiver();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        //??????notifacation???load??????
        NotificationHelper.getInstance().showLoadStatus(event.mAudioBean);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        //??????notifacation???????????????
        NotificationHelper.getInstance().showPauseStatus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        //??????notifacation???????????????
        NotificationHelper.getInstance().showPlayStatus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioFavouriteEvent(AudioFavouriteEvent event) {
        //??????notifacation????????????
        NotificationHelper.getInstance().changeFavouriteStatus(event.isFavourite);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioReleaseEvent(AudioReleaseEvent event) {
        //??????notifacation
    }

    public static class NotificationReceiver extends BroadcastReceiver {
        public static final String ACTION_STATUS_BAR = AudioHelper.getContext().getPackageName()
                + ".NOTIFICATION_ACTIONS";
        public static final String EXTRA = "extra";
        public static final String EXTRA_PLAY = "play_pause";
        public static final String EXTRA_NEXT = "play_next";
        public static final String EXTRA_PRE = "play_previous";
        public static final String EXTRA_FAV = "play_favourite";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive " + intent.getAction());
            if (intent == null || TextUtils.isEmpty(intent.getAction())) {
                return;
            }
            String extra = intent.getStringExtra(EXTRA);
            Log.d(TAG, "extra = " + extra);
            switch (extra) {
                case EXTRA_PLAY:
                    //????????????????????????,????????????AudioController???
                    AudioController.getInstance().playOrPause();
                    break;
                case EXTRA_PRE:
                    AudioController.getInstance().previous(); //?????????????????????????????????
                    break;
                case EXTRA_NEXT:
                    AudioController.getInstance().next();
                    break;
                case EXTRA_FAV:
                    AudioController.getInstance().changeFavouriteStatus();
                    break;
            }
        }
    }
}
