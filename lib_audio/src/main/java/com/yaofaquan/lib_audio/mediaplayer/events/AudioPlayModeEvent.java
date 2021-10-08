package com.yaofaquan.lib_audio.mediaplayer.events;

import com.yaofaquan.lib_audio.mediaplayer.core.AudioController;

public class AudioPlayModeEvent {
    public AudioController.PlayMode mPlayMode;

    public AudioPlayModeEvent(AudioController.PlayMode playMode) {
        this.mPlayMode = playMode;
    }
}
