package com.yaofaquan.lib_audio.mediaplayer.core;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.yaofaquan.lib_audio.app.AudioHelper;
import com.yaofaquan.lib_audio.mediaplayer.events.AudioCompleteEvent;
import com.yaofaquan.lib_audio.mediaplayer.events.AudioErrorEvent;
import com.yaofaquan.lib_audio.mediaplayer.events.AudioLoadEvent;
import com.yaofaquan.lib_audio.mediaplayer.events.AudioPauseEvent;
import com.yaofaquan.lib_audio.mediaplayer.events.AudioReleaseEvent;
import com.yaofaquan.lib_audio.mediaplayer.events.AudioStartEvent;
import com.yaofaquan.lib_audio.mediaplayer.model.AudioBean;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class AudioPlayer implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        AudioFocusManager.AudioFocusListener {

    private static final String TAG = "AudioPlayer";
    private static final int TIME_MSG = 0X01;
    private static final int TIME_INVAL = 100;

    private CustomMediaPlayer mMediaPlayer;
    private WifiManager.WifiLock mWifiLock;

    private AudioFocusManager mAudioFocusManager;
    private boolean mIsPausedByFocusLossTransient = false;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case TIME_MSG:
                    break;
            }
        }
    };

    public AudioPlayer() {
        init();
    }

    private void init() {
        mMediaPlayer = new CustomMediaPlayer();
        mMediaPlayer.setWakeMode(AudioHelper.getContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnErrorListener(this);

        mWifiLock = ((WifiManager)AudioHelper.getContext()
                .getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL_LOW_LATENCY, TAG);

        mAudioFocusManager = new AudioFocusManager(AudioHelper.getContext(), this);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        //缓存进度的回调
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        //播放完毕之后的回调
        EventBus.getDefault().post(new AudioCompleteEvent());
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        //播放出错时回调
        EventBus.getDefault().post(new AudioErrorEvent());
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        //准备完毕
        start();
    }

    @Override
    public void audioFocusGrant() {
        //再次获得音频焦点
        setVolumn(1.0f, 1.0f);
        if (mIsPausedByFocusLossTransient) {
            resume();
        }
        mIsPausedByFocusLossTransient = false;
    }

    @Override
    public void audioFocusLoss() {
        //永久失去焦点
        pause();
    }

    @Override
    public void audioFocusLossTransient() {
        //短暂性失去焦点
        pause();
        mIsPausedByFocusLossTransient = true;
    }

    @Override
    public void audioFocusLossDuck() {
        //瞬间失去焦点
        setVolumn(0.5f, 0.5f);
    }

    public void load(AudioBean bean) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(bean.mUrl);
            mMediaPlayer.prepareAsync();
            //对外发送load事件
            EventBus.getDefault().post(new AudioLoadEvent(bean));
        } catch (IOException e) {
            e.printStackTrace();
            //对外发送Error事件
            EventBus.getDefault().post(new AudioErrorEvent() );
        }
    }

    /**
     * 对外提供暂停功能
     */
    public void pause() {
        if (getStatus() == CustomMediaPlayer.Status.STARTED) {
            mMediaPlayer.pause();
            if (mWifiLock.isHeld()) {
                mWifiLock.release();
            }
            if (mAudioFocusManager != null) {
                mAudioFocusManager.abandonAudioFocus();
            }
            //发送暂停事件
            EventBus.getDefault().post(new AudioPauseEvent());
        }
    }

    /**
     * 对外提供恢复功能
     */
    public void resume() {
        if (getStatus() == CustomMediaPlayer.Status.PAUSED) {
            start();
        }
    }

    /**
     * 对外提供释放播放器所占用资源的功能
     */
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mAudioFocusManager != null) {
            mAudioFocusManager.abandonAudioFocus();
        }
        if (mWifiLock != null && mWifiLock.isHeld()) {
            mWifiLock.release();
            mWifiLock = null;
        }
        //发送release事件
        EventBus.getDefault().post(new AudioReleaseEvent());
    }

    private void start() {
        if (mAudioFocusManager.requestAudioFocus()) {
            Log.d(TAG, "获取音频焦点失败");
        }
        mMediaPlayer.start();
        mWifiLock.acquire();
        //对外发送start事件
        EventBus.getDefault().post(new AudioStartEvent());
    }

    private void setVolumn(float leftVol, float rightVol) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(leftVol, rightVol);
        }
    }

    private CustomMediaPlayer.Status getStatus() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getState();
        } else {
            return CustomMediaPlayer.Status.STOPPED;
        }
    }

    public boolean isPauseState() {
        return mMediaPlayer.getState() == CustomMediaPlayer.Status.PAUSED;
    }

    public boolean isStartState() {
        return mMediaPlayer.getState() == CustomMediaPlayer.Status.STARTED;
    }
}
