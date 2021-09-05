package com.yaofaquan.lib_network.okhttp.response;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.yaofaquan.lib_network.okhttp.exception.OkHttpException;
import com.yaofaquan.lib_network.okhttp.listener.DisposeDataHandle;
import com.yaofaquan.lib_network.okhttp.listener.DisposeDataListener;
import com.yaofaquan.lib_network.okhttp.listener.DisposeDownloadListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CommonFileCallback implements Callback {
    protected  final String EMPTY_MSG = "";

    protected final int NETWORK_ERROR = -1;
    protected final int IO_ERROR = -2;
    protected final int OTHER_ERROR = -3;

    private static final int PROGRESS_MESSAGE = 0x01;

    private DisposeDownloadListener mListener;
    private Class<?> mClass;
    private Handler mDeliverHandler;
    private String mFilePath;
    private int mProgress;

    public CommonFileCallback(DisposeDataHandle handle) {
        this.mListener = (DisposeDownloadListener) handle.mListener;
        this.mFilePath = handle.mSource;
        this.mDeliverHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case PROGRESS_MESSAGE:
                        mListener.onProgress((int)msg.obj);
                        break;
                }
            }
        };
    }


    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        mDeliverHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFailure(new OkHttpException(NETWORK_ERROR, e));
            }
        });
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        final File file = handleResponse(response);
        mDeliverHandler.post(new Runnable() {
            @Override
            public void run() {
                if (file != null) {
                    mListener.onSuccess(file);
                } else {
                    mListener.onFailure(new OkHttpException(IO_ERROR, EMPTY_MSG));
                }
            }
        });
    }

    private File handleResponse(Response response) {
        if (response == null) {
            return null;
        }
        InputStream inputStream = null;
        File file = null;
        FileOutputStream fos = null;
        byte[] buffer = new byte[2048];
        int length;
        int sumLength;
        int currentLeng = 0;
        try {

            file = new File(mFilePath);
            fos = new FileOutputStream(file);
            inputStream = response.body().byteStream();
            sumLength = (int) response.body().contentLength();
            while ((length = inputStream.read(buffer)) != -1) {
                fos.write(buffer, currentLeng, buffer.length);
                currentLeng += length;
                mProgress = (int) ((currentLeng) / sumLength * 100);
                mDeliverHandler.obtainMessage(PROGRESS_MESSAGE, mProgress);
            }
            fos.close();
            inputStream.close();
            fos = null;
            inputStream = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {

                }
                fos = null;
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {

                }
                inputStream = null;
            }
        }
        return file;
    }
}
