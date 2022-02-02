package com.yaofaquan.lib_audio.mediaplayer.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.yaofaquan.lib_audio.R;
import com.yaofaquan.lib_audio.app.AudioHelper;
import com.yaofaquan.lib_audio.mediaplayer.core.AudioController;
import com.yaofaquan.lib_audio.mediaplayer.core.MusicService;
import com.yaofaquan.lib_audio.mediaplayer.db.FavouriteGreenDaoHelper;
import com.yaofaquan.lib_audio.mediaplayer.model.AudioBean;
import com.yaofaquan.lib_image_loader.app.ImageLoaderManager;

public class NotificationHelper {
    private static final String TAG = "NotificationHelper";

    public static final String CHANNEL_ID = "channel_id_audio";
    public static final String CHANNEL_NAME = "channel_name_audio";
    public static final int NOTIFICATION_ID = 0x111;

    private Notification mNotification;
    private RemoteViews mRemoteViews;
    private RemoteViews mSmallRemoteViews;
    private NotificationManager mNotificationManager;

    private NotificationHelperListener mListener;
    private String mPackageName;
    private AudioBean mAudioBean;

    public static NotificationHelper getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static NotificationHelper instance = new NotificationHelper();
    }

    public void init(NotificationHelperListener listener) {
        mNotificationManager = (NotificationManager) AudioHelper.getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mPackageName = AudioHelper.getContext().getPackageName();
        mAudioBean = AudioController.getInstance().getNowPlaying();
        initNotification();
        mListener = listener;
        if (mListener != null) {
            mListener.onNotificationInit();
        }
    }

    public Notification getNotification() {
        return mNotification;
    }

    private void initNotification() {
        if (mNotification == null) {
            initRemoteViews();
            Intent intent = new Intent(AudioHelper.getContext(), MusicPlayerActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(AudioHelper.getContext(),
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel =
                        new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(false);
                channel.enableVibration(false);
                mNotificationManager.createNotificationChannel(channel);
            }
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(AudioHelper.getContext(), CHANNEL_ID)
                            .setContentIntent(pendingIntent)
                            .setSmallIcon(R.mipmap.ic_fa)
                            .setCustomBigContentView(mRemoteViews)
                            .setContent(mSmallRemoteViews);
            mNotification = builder.build();

            showLoadStatus(mAudioBean);
        }
    }

    private void initRemoteViews() {
        int layoutId = R.layout.notification_big_layout;
        mRemoteViews = new RemoteViews(mPackageName, layoutId);
        mRemoteViews.setTextViewText(R.id.title_view, mAudioBean.name);
        mRemoteViews.setTextViewText(R.id.tip_view, mAudioBean.album);
        if (FavouriteGreenDaoHelper.selectFavourite(mAudioBean) != null) {
            mRemoteViews.setImageViewResource(R.id.favourite_view, R.mipmap.note_btn_loved);
        } else {
            mRemoteViews.setImageViewResource(R.id.favourite_view, R.mipmap.note_btn_love_white);
        }

        int smalllayoutId = R.layout.notification_small_layout;
        mSmallRemoteViews = new RemoteViews(mPackageName, smalllayoutId);
        mSmallRemoteViews.setTextViewText(R.id.title_view, mAudioBean.name);
        mSmallRemoteViews.setTextViewText(R.id.tip_view, mAudioBean.album);

        //点击播放按钮要发送的广播
        Intent playIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        playIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_PLAY);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(
                AudioHelper.getContext(), 1, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d(TAG, "onInit mRemoteViews onClick.");
        mRemoteViews.setOnClickPendingIntent(R.id.play_view, playPendingIntent);
        mRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white);
        mSmallRemoteViews.setOnClickPendingIntent(R.id.play_view, playPendingIntent);
        mSmallRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white);

        //点击上一首需要发送的广播
        Intent preIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        preIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_PRE);
        PendingIntent prePendingIntent = PendingIntent.getBroadcast(
                AudioHelper.getContext(), 2, preIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.previous_view, prePendingIntent);
        mRemoteViews.setImageViewResource(R.id.previous_view, R.mipmap.note_btn_pre_white);

        //点击下一首按钮要发送的广播
        Intent nextIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        nextIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(
                AudioHelper.getContext(), 3, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.next_view, nextPendingIntent);
        mRemoteViews.setImageViewResource(R.id.next_view, R.mipmap.note_btn_next_white);
        mSmallRemoteViews.setOnClickPendingIntent(R.id.next_view, nextPendingIntent);
        mSmallRemoteViews.setImageViewResource(R.id.next_view, R.mipmap.note_btn_next_white);

        //点击收藏按钮要发送的广播
        Intent favouriteIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        favouriteIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_FAV);
        PendingIntent favouritePendingIntent = PendingIntent.getBroadcast(
                AudioHelper.getContext(), 4, favouriteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.favourite_view, favouritePendingIntent);
        mRemoteViews.setImageViewResource(R.id.favourite_view, R.mipmap.note_btn_love_white);
    }

    public void showLoadStatus(AudioBean bean) {
        mAudioBean = bean;
        if (mRemoteViews != null) {
            mRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
            mRemoteViews.setTextViewText(R.id.title_view, mAudioBean.name);
            mRemoteViews.setTextViewText(R.id.tip_view, mAudioBean.album);
            ImageLoaderManager.getInstance()
                    .displayImageForNotification(AudioHelper.getContext(), R.id.image_view,
                            mRemoteViews, mNotification, NOTIFICATION_ID, mAudioBean.albumPic);
            if (FavouriteGreenDaoHelper.selectFavourite(mAudioBean) != null) {
                mRemoteViews.setImageViewResource(R.id.favourite_view, R.mipmap.note_btn_love_white);
            } else {
                mRemoteViews.setImageViewResource(R.id.favourite_view, R.mipmap.note_btn_loved);
            }
        }
        if (mSmallRemoteViews != null) {
            mSmallRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
            mSmallRemoteViews.setTextViewText(R.id.title_view, mAudioBean.name);
            mSmallRemoteViews.setTextViewText(R.id.tip_view, mAudioBean.album);
            ImageLoaderManager.getInstance()
                    .displayImageForNotification(AudioHelper.getContext(), R.id.image_view,
                            mSmallRemoteViews, mNotification, NOTIFICATION_ID, mAudioBean.albumPic);
        }
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) AudioHelper.getContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
        }
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    public void showPlayStatus() {
        if (mRemoteViews != null) {
            mRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
        }
        if (mSmallRemoteViews != null) {
            mSmallRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
        }
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    public void showPauseStatus() {
        if (mRemoteViews != null) {
            mRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white);
        }
        if (mSmallRemoteViews != null) {
            mSmallRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white);
        }
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    public void changeFavouriteStatus(boolean isFavourite) {
        if (mRemoteViews != null) {
            mRemoteViews.setImageViewResource(R.id.favourite_view,
                    isFavourite ? R.mipmap.note_btn_loved : R.mipmap.note_btn_love_white);
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        }
    }

    /**
     * 与音乐service的回调通信
     */
    public interface NotificationHelperListener {
        void onNotificationInit();
    }
}
