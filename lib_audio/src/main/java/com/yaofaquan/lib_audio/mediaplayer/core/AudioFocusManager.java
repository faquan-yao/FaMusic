package com.yaofaquan.lib_audio.mediaplayer.core;

import android.content.Context;
import android.media.AudioManager;

public class AudioFocusManager implements AudioManager.OnAudioFocusChangeListener{

    private static final String TAG = AudioFocusManager.class.getSimpleName();

    private AudioFocusListener mAudioFocusListener;
    private AudioManager mAudioManager;

    public AudioFocusManager(Context context, AudioFocusListener listener) {
        mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        mAudioFocusListener = listener;
    }

    public boolean requestAudioFocus() {
        return mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public void abandonAudioFocus() {
        mAudioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(int i) {
        switch (i) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (mAudioFocusListener != null) {
                    mAudioFocusListener.audioFocusGrant();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if (mAudioFocusListener != null) {
                    mAudioFocusListener.audioFocusLoss();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (mAudioFocusListener != null) {
                    mAudioFocusListener.audioFocusLossTransient();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (mAudioFocusListener != null) {
                    mAudioFocusListener.audioFocusLossDuck();
                }
        }
    }

    public interface AudioFocusListener {
        //获得焦点回调处理
        void audioFocusGrant();
        //永久失去焦点回调处理
        void audioFocusLoss();
        //短暂失去焦点回调处理
        void audioFocusLossTransient();
        //瞬间失去焦点回调处理
        void audioFocusLossDuck();
    }
}
