package com.yaofaquan.lib_video.videoplayer.core.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.yaofaquan.lib_video.R;
import com.yaofaquan.lib_video.videoplayer.core.VideoAdSlot;
import com.yaofaquan.lib_video.videoplayer.utils.Utils;

public class VideoFullDialog extends Dialog implements CustomVideoView.ADVideoPlayerListener {

    private static final String TAG = VideoFullDialog.class.getSimpleName();
    private CustomVideoView mVideoView;

    private RelativeLayout mRootView;
    private ViewGroup mParentView;

    private int mPosition;
    private FullToSmallListener mListener;
    private boolean isFirst = true;

    private int deltaY;
    private VideoAdSlot.SDKSlotListener mSlotListener;
    private Bundle mStartBundle;
    private Bundle mEndBundle;

    public VideoFullDialog(Context context, CustomVideoView videoView, String instance, int position) {
        super(context, R.style.dialog_full_screen);

        mPosition = position;
        mVideoView = videoView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_video_layout);
        initVideoView();
    }

    public void setViewBundle(Bundle bundle) {
        mStartBundle = bundle;
    }

    public void setListener(FullToSmallListener listener) {
        mListener = listener;
    }

    public void setSlotListener(VideoAdSlot.SDKSlotListener slotListener) {
        mSlotListener = slotListener;
    }

    private void initVideoView() {
        mParentView = (RelativeLayout) findViewById(R.id.content_layout);
        mRootView = findViewById(R.id.root_view);
        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickVideo();
            }
        });
        mRootView.setVisibility(View.INVISIBLE);

        mVideoView.setListener(this);
        mVideoView.mute(false);
        mParentView.addView(mVideoView);
        mParentView.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        mParentView.getViewTreeObserver().removeOnPreDrawListener(this::onPreDraw);
                        prepareScene();
                        runEnterAnimation();
                        return true;
                    }
                });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        mVideoView.isShowFullBtn(false);
        if (!hasFocus) {
            mPosition = mVideoView.getCurrentPosition();
            mVideoView.pauseForFullScreen();
        } else {
            if (isFirst) {
                mVideoView.seekAndResume(mPosition);
            } else {
                mVideoView.resume();
            }
        }
        isFirst = false;
    }

    @Override
    public void dismiss() {
        mParentView.removeView(mVideoView);
        super.dismiss();
    }

    @Override
    public void onBackPressed() {
        onClickBackBtn();
    }

    @Override
    public void onBufferUpdate(int time) {

    }

    @Override
    public void onClickFullScreenBtn() {
        onClickVideo();
    }

    @Override
    public void onClickVideo() {

    }

    @Override
    public void onClickBackBtn() {
        runExitAnimator();
    }

    @Override
    public void onClickPlay() {

    }

    @Override
    public void onAdVideoLoadSuccess() {
        if (mVideoView != null) {
            mVideoView.resume();
        }
    }

    @Override
    public void onAdVideoLoadFailed() {

    }

    @Override
    public void onAdVideoLoadComplete() {
        dismiss();
        if (mListener != null) {
            mListener.playComplete();
        }
    }

    private void prepareScene() {
        mEndBundle = Utils.getViewProperty(mVideoView);

        deltaY = (mStartBundle.getInt(Utils.PROPNAME_SCREENLOCATION_TOP) - mEndBundle.getInt(Utils.PROPNAME_SCREENLOCATION_TOP));
        mVideoView.setTranslationY(deltaY);
    }

    private void runEnterAnimation() {
        mVideoView.animate()
                .setDuration(200)
                .setInterpolator(new LinearInterpolator())
                .translationY(0)
                .withStartAction(() -> {
                    mRootView.setVisibility(View.VISIBLE);
                })
                .start();
    }

    private void runExitAnimator() {
        mVideoView.animate()
                .setDuration(200)
                .setInterpolator(new LinearInterpolator())
                .translationY(deltaY)
                .withEndAction(() -> {
                    dismiss();
                    if (mListener != null) {
                        mListener.getCurrentPlayPosition(mVideoView.getCurrentPosition());
                    }
                })
                .start();
    }

    public interface FullToSmallListener {

        void getCurrentPlayPosition(int position);

        void playComplete();
    }
}
