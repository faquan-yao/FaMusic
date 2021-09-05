package com.yaofaquan.lib_common_ui.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.yaofaquan.lib_common_ui.utils.StatusBarUtil;

public class BaseActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.statusBarLightMode(this);
    }
}
