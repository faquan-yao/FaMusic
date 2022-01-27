package com.yaofaquan.lib_network.okhttp.response;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.yaofaquan.lib_network.okhttp.exception.OkHttpException;
import com.yaofaquan.lib_network.okhttp.listener.DisposeDataHandle;
import com.yaofaquan.lib_network.okhttp.listener.DisposeDataListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 处理json类型的响应
 */
public class CommonJsonCallback implements Callback {
    private final String TAG = this.getClass().getSimpleName();

    protected  final String EMPTY_MSG = "";

    protected final int NETWORK_ERROR = -1;
    protected final int JSON_ERROR = -2;
    protected final int OTHER_ERROR = -3;

    private DisposeDataListener mListener;
    private Class<?> mClass;
    private Handler mDeliverHandler;

    public CommonJsonCallback(DisposeDataHandle handle) {
        mListener = handle.mListener;
        mClass = handle.mClass;
        mDeliverHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull final IOException e) {
        mDeliverHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFailure(new OkHttpException(NETWORK_ERROR, e));
            }
        });
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        final String result = response.body().string();
        mDeliverHandler.post(new Runnable() {
            @Override
            public void run() {
                handleResponse(result);
            }
        });
    }


    private void handleResponse(String result) {
        if (result == null || result.trim().equals("")) {
            mListener.onFailure(new OkHttpException(NETWORK_ERROR, EMPTY_MSG));
            return;
        }
        Log.d(TAG, "result = " + result);
        try {
            JSONObject objectString = new JSONObject(result);
            if (mClass == null) {
                mListener.onSuccess(result);
            } else {
                Object obj = new Gson().fromJson(objectString.toString(), mClass);
                if (obj != null) {
                    mListener.onSuccess(obj);
                } else {
                    mListener.onFailure(new OkHttpException(JSON_ERROR, result));
                }
            }
        } catch (Exception e) {
            mListener.onFailure(new OkHttpException(JSON_ERROR, result));
        }
    }
}
