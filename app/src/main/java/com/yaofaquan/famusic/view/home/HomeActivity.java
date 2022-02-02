package com.yaofaquan.famusic.view.home;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.yaofaquan.famusic.R;
import com.yaofaquan.famusic.api.RequestCenter;
import com.yaofaquan.famusic.model.CHANNEL;
import com.yaofaquan.famusic.view.home.adpater.HomePagerAdapter;
import com.yaofaquan.famusic.view.login.LoginActivity;
import com.yaofaquan.famusic.view.login.LoginEvent;
import com.yaofaquan.famusic.view.login.UserManager;
import com.yaofaquan.lib_audio.app.AudioHelper;
import com.yaofaquan.lib_audio.mediaplayer.model.AudioBean;
import com.yaofaquan.lib_common_ui.base.BaseActivity;
import com.yaofaquan.lib_common_ui.pager_indictor.ScaleTransitionPagerTitleView;
import com.yaofaquan.lib_image_loader.app.ImageLoaderManager;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "HomeActivity";
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.FOREGROUND_SERVICE
    };
    private static final CHANNEL[] CHANNELS = new CHANNEL[]{
            CHANNEL.MY, CHANNEL.DISCORY, CHANNEL.FRIEND
    };

    /**
     * View;
     */
    private DrawerLayout mDrawerLayout;
    private View mToggleView;
    private View mSearchView;
    private ViewPager mVewPager;
    private HomePagerAdapter mAdapter;
    private LinearLayout mUnLoggingLayout;
    private ImageView mPhotoView;

    private ArrayList<AudioBean> mLocalDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_home);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initData() {
        Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
        Cursor cursor = AudioHelper.getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                , null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            Log.d(TAG, "Count = " + cursor.getCount());
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                String albumInfo = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));

                AudioBean bean = new AudioBean();
                bean.name = name;
                bean.id = Long.toString(id);
                bean.author = singer;
                bean.mUrl = path;
                bean.totalTime = Integer.toString(duration);
                bean.album = albumInfo;
                bean.albumInfo = albumInfo;
                Uri uri = ContentUris.withAppendedId(albumArtUri, albumId);
                bean.albumPic = uri.toString();
                Log.d(TAG, "Add audio bean " + bean.toString());
                mLocalDataList.add(bean);
            }
        }
        AudioHelper.startMusicService(mLocalDataList);
    }

    private void initView() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggleView = findViewById(R.id.toggle_view);
        mSearchView = findViewById(R.id.search_view);
        mVewPager = findViewById(R.id.view_pager);

        mToggleView.setOnClickListener(this);
        mSearchView.setOnClickListener(this);

        mAdapter = new HomePagerAdapter(getSupportFragmentManager(), CHANNELS);
        mVewPager.setAdapter(mAdapter);
        initMagicIndicator();

        //登录相关的UI
        mUnLoggingLayout = findViewById(R.id.unloggin_layout);
        mUnLoggingLayout.setOnClickListener(this);
        mPhotoView = findViewById(R.id.avatr_view);
    }

    private void initMagicIndicator() {
        MagicIndicator magicIndicator = findViewById(R.id.magic_indicator);
        magicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return CHANNELS == null ? 0 : CHANNELS.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setText(CHANNELS[index].getKey());
                simplePagerTitleView.setTextSize(19);
                simplePagerTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                simplePagerTitleView.setNormalColor(Color.parseColor("#999999"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#333333"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mVewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }

            @Override
            public float getTitleWeight(Context context, int index) {
                return 1.0f;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mVewPager);
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "view(" + view.getId() + ") is clicked!");
        switch (view.getId()) {
            case R.id.unloggin_layout:
                if (!UserManager.getInstance().hasLogin()) {
                    LoginActivity.start(this);
                } else {
                    onLoginEvent(null);
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                }
                break;
            case R.id.toggle_view:
                Log.d(TAG, "Toggle_view is clicked!");
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LoginEvent event) {
        Log.d(TAG, "onLoginEvent");
        mUnLoggingLayout.setVisibility(View.GONE);
        mPhotoView.setVisibility(View.VISIBLE);

//        ImageLoaderManager.getInstance()
//                .displayImageForCircle(mPhotoView, RequestCenter.getMediaUrl(UserManager.getInstance().getUser().avatar));
        ImageLoaderManager.getInstance()
                .displayImageForView(mPhotoView, RequestCenter.getMediaUrl(UserManager.getInstance().getUser().avatar));
    }

    private void checkPermission() {
        for (int i = 0; i < PERMISSIONS.length; i++) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, i);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults != null) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission " + PERMISSIONS[requestCode] + " denied.", Toast.LENGTH_LONG);
                    finish();
                }
            }
        }
    }
}