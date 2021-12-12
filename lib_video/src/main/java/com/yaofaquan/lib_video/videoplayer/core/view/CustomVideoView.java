package com.yaofaquan.lib_video.videoplayer.core.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.yaofaquan.lib_video.R;

import java.io.IOException;

public class CustomVideoView extends RelativeLayout
        implements View.OnClickListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
        TextureView.SurfaceTextureListener {

    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PLAYING = 1;
    private static final int STATE_PAUSING = 2;
    private static final int LOAD_TOTAL_COUNT = 3;

    private Context mContext;

    private RelativeLayout mPlayerView;
    private TextureView mVideoView;
    private Button mMiniPlayBtn;
    private ImageView mFullBtn;
    private ImageView mLoadingBar;
    private AudioManager mAudioManager;
    private Surface mVideoSurface;

    private String mUrl;
    private boolean mIsMute;
    private int mScreenWith;
    private int mDestationHeight;

    private boolean mCanPlay = true;
    private boolean mIsRealPause;
    private boolean mIsComplete;
    private int mCurrentCount;
    private int mPlayerState = STATE_IDLE;

    private MediaPlayer mMediaPlayer;
    private ADVideoPlayerListener mADVideoPlayerListener;
    private ScreenEventReceiver mScreenRecriver;

    public CustomVideoView(Context context) {
        super(context);
        mContext = context;

        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        initData();
        initView();
        registerBroadcastReceiver();
    }

    private void registerBroadcastReceiver() {
        if (mScreenRecriver == null) {
            mScreenRecriver = new ScreenEventReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            mContext.registerReceiver(mScreenRecriver, filter);
        }
    }

    private void unRegisterBroadcastReceiver() {
        if (mScreenRecriver != null) {
            mContext.unregisterReceiver(mScreenRecriver);
            mScreenRecriver = null;
        }
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mPlayerView = (RelativeLayout) inflater.inflate(R.layout.video_player_layout, this);
        mVideoView = mPlayerView.findViewById(R.id.xadsdk_player_video_textureView);
        mVideoView.setOnClickListener(this);
        mVideoView.setKeepScreenOn(true);
        mVideoView.setSurfaceTextureListener(this);
        initSmallLayoutMode();
    }

    private void initData() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWith = dm.widthPixels;
        mDestationHeight = (int) (mScreenWith * (9 / 16.0f));
    }

    private void initSmallLayoutMode() {
        LayoutParams params = new LayoutParams(mScreenWith, mDestationHeight);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mPlayerView.setLayoutParams(params);

        mMiniPlayBtn = mPlayerView.findViewById(R.id.xadsdk_small_play_btn);
        mFullBtn = mPlayerView.findViewById(R.id.xadsdk_to_full_view);
        mLoadingBar = mPlayerView.findViewById(R.id.loading_bar);
        mMiniPlayBtn.setOnClickListener(this);
        mFullBtn.setOnClickListener(this);
    }

    private synchronized void checkMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    private void showPauseView(boolean show) {
        mFullBtn.setVisibility(show ? View.VISIBLE : View.GONE);
        mMiniPlayBtn.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(View.GONE);
    }

    private void showLoadingView() {
        mFullBtn.setVisibility(View.GONE);
        mLoadingBar.setVisibility(View.VISIBLE);
        AnimationDrawable animationDrawable = (AnimationDrawable) mLoadingBar.getBackground();
        animationDrawable.start();
        mMiniPlayBtn.setVisibility(View.GONE);
    }

    private void showPlayView() {
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(View.GONE);
        mMiniPlayBtn.setVisibility(View.GONE);
    }

    private void entryResumeState() {
        mCanPlay = true;
        setCurrentPlayState(STATE_PLAYING);
        setIsRealPause(false);
        setIsComplete(false);
    }

    public void setCurrentPlayState(int state) {
        mPlayerState = state;
    }

    public void setIsRealPause(boolean b) {
        mIsRealPause = b;
    }

    public void setIsComplete(boolean b) {
        mIsComplete = b;
    }

    public int getCurrentPlayState() {
        return mPlayerState;
    }

    public boolean getIsRealPause() {
        return mIsRealPause;
    }

    public boolean getIsComplete() {
        return mIsComplete;
    }

    public void showFullBtn(boolean isShow) {
        mFullBtn.setImageResource(isShow ? R.drawable.xadsdk_ad_mini : R.drawable.xadsdk_ad_mini_null);
        mFullBtn.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public void mute(boolean mute) {
        mIsMute = mute;
        if (mMediaPlayer != null && mAudioManager != null) {
            float volume = mIsMute ? 0.0f : 1.0f;
            mMediaPlayer.setVolume(volume, volume);
        }
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            return true;
        }
        return false;
    }

    public void setDataSource(String url) {
        mUrl = url;
    }

    public void setListener(ADVideoPlayerListener listener) {
         mADVideoPlayerListener = listener;
    }

    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE && mPlayerState == STATE_PAUSING) {
            if (getIsRealPause() || getIsComplete()) {
                pause();
            } else {
                resume();
            }
        } else {
            pause();
        }
    }

    public void resume() {
        if (mPlayerState != STATE_PAUSING) {
            return;
        }
        if (!isPlaying()) {
            entryResumeState();
            mMediaPlayer.setOnSeekCompleteListener(null);
            mMediaPlayer.start();
            showPauseView(true);
        } else {
            showPauseView(false);
        }
    }

    private void pause() {
        if (mPlayerState != STATE_PLAYING) {
            return;
        }
        setCurrentPlayState(STATE_PAUSING);
        if(isPlaying()) {
            mMediaPlayer.pause();
            if (!mCanPlay) {
                mMediaPlayer.seekTo(0);
            }
        }
        showPauseView(false);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (mADVideoPlayerListener != null) {
            mADVideoPlayerListener.onAdVideoLoadComplete();
        }
        playBack();
        setIsComplete(true);
        setIsRealPause(true);
    }

    public void playBack() {
        setCurrentPlayState(STATE_PAUSING);
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnSeekCompleteListener(null);
            mMediaPlayer.seekTo(0);
            mMediaPlayer.pause();
        }
        showPauseView(false);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mPlayerState = STATE_ERROR;
        if (mCurrentCount >= LOAD_TOTAL_COUNT) {
            showPauseView(false);
            if (mADVideoPlayerListener != null) {
                mADVideoPlayerListener.onAdVideoLoadFailed();
            }
        }
        stop();
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        showPlayView();
        mMediaPlayer = mediaPlayer;
        if (mMediaPlayer != null) {
            mCurrentCount = 0;
            if (mADVideoPlayerListener != null) {
                mADVideoPlayerListener.onAdVideoLoadSuccess();
            }
            setCurrentPlayState(STATE_PAUSING);
            resume();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        mVideoSurface = new Surface(surfaceTexture);
        checkMediaPlayer();
        mMediaPlayer.setSurface(mVideoSurface);
        load();
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

    }

    @Override
    public void onClick(View view) {
        if (view == mMiniPlayBtn) {
            if (mPlayerState == STATE_PAUSING) {
                resume();
                mADVideoPlayerListener.onClickPlay();
            } else {
                load();
            }
        } else if (view == mFullBtn) {
            mADVideoPlayerListener.onClickFullScreenBtn();
        } else if (view == mVideoView) {
            mADVideoPlayerListener.onClickVideo();
        }
    }

    public void load() {
        if (mPlayerState != STATE_IDLE) {
            return;
        }
        showLoadingView();
        try {
            setCurrentPlayState(STATE_IDLE);
            checkMediaPlayer();
            mute(true);
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mUrl);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            stop();
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.setOnSeekCompleteListener(null);
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        setCurrentPlayState(STATE_IDLE);
        mCurrentCount = 0;
        setIsComplete(false);
        setIsRealPause(false);
        unRegisterBroadcastReceiver();
        showPauseView(false);
    }

    public void pauseForFullScreen() {
        if (mPlayerState != STATE_PLAYING) {
            return;
        }
        setCurrentPlayState(STATE_PAUSING);
        if (isPlaying()) {
            mMediaPlayer.pause();
            if (!mCanPlay) {
                mMediaPlayer.seekTo(0);
            }
        }
    }

    public void seekAndPause(int position) {
        if (mPlayerState != STATE_PLAYING) {
            return;
        }
        showPauseView(false);
        setCurrentPlayState(STATE_PAUSING);
        if (isPlaying()) {
            mMediaPlayer.seekTo(position);
            mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mMediaPlayer.pause();
                }
            });
        }
    }

    public void seekAndResume(int position) {
        if (mMediaPlayer != null) {
            showPauseView(true);
            entryResumeState();
            mMediaPlayer.seekTo(position);
            mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
        }
    }

    public void destroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnSeekCompleteListener(null);
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        setCurrentPlayState(STATE_IDLE);
        mCurrentCount = 0;
        setIsComplete(false);
        setIsRealPause(false);
        unRegisterBroadcastReceiver();
        showPauseView(false);
    }

    public void isShowFullBtn(boolean isShow) {
        mFullBtn.setImageResource(isShow ? R.drawable.xadsdk_ad_mini : R.drawable.xadsdk_ad_mini_null);
        mFullBtn.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public interface ADVideoPlayerListener {
        void onBufferUpdate(int time);
        void onClickFullScreenBtn();
        void onClickVideo();
        void onClickBackBtn();
        void onClickPlay();
        void onAdVideoLoadSuccess();
        void onAdVideoLoadFailed();
        void onAdVideoLoadComplete();
    }

    private class ScreenEventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_USER_PRESENT:
                    if (mPlayerState == STATE_PAUSING) {
                        if (mIsRealPause) {
                            pause();
                        } else {
                            resume();
                        }
                    }
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    if (mPlayerState == STATE_PLAYING) {
                        pause();
                    }
                    break;
            }
        }
    }


    public interface ADFrameImageLoadListener {

        void onStartFrameLoad(String url, ImageLoaderListener listener);
    }

    public interface ImageLoaderListener {

        void onLoadingComplete(Bitmap loadedImage);
    }
}
