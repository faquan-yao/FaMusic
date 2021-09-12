package com.yaofaquan.famusic.view.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.yaofaquan.famusic.R;
import com.yaofaquan.famusic.api.RequestCenter;
import com.yaofaquan.famusic.model.User.User;
import com.yaofaquan.lib_common_ui.base.BaseActivity;
import com.yaofaquan.lib_network.okhttp.listener.DisposeDataListener;

import org.greenrobot.eventbus.EventBus;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "FaMusic_LoginActivity";

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);
        findViewById(R.id.login_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onLogin Clicked.");
                RequestCenter.login(new DisposeDataListener() {
                    @Override
                    public void onSuccess(Object responseObj) {
                        Log.d(TAG, "Login success.");
                        User user = (User) responseObj;
                        UserManager.getInstance().saveUser(user);
                        EventBus.getDefault().post(new LoginEvent());
                        finish();
                    }

                    @Override
                    public void onFailure(Object reasonObj) {
                        Log.d(TAG, "Login failure.");
                        Toast.makeText(getApplication(), "登录失败", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }
}
