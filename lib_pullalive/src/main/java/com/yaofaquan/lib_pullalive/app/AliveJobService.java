package com.yaofaquan.lib_pullalive.app;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

@TargetApi(value = Build.VERSION_CODES.LOLLIPOP)
public class AliveJobService extends JobService {
    private static final String TAG = "AliveJobService";

    private static final int MSG_PULL_ALIVE = 0x01;
    private JobScheduler mJobScheduler = null;
    private HandlerThread mThread = null;
    private Handler mHandler = null;

    public static void start(Context context) {
        Intent intent = new Intent(context, AliveJobService.class);
        context.startService(intent);
    }

    public AliveJobService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mThread = new HandlerThread(getPackageName() + "_AliveJobService");
        mThread.start();
        mHandler = new Handler(mThread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case MSG_PULL_ALIVE:
                        Log.d(TAG, "pull alive.");
                        jobFinished((JobParameters) msg.obj, true);
                        break;
                    default:
                        break;
                }
            }
        };
        JobInfo jobInfo = initJonInfo(startId);
        if (mJobScheduler.schedule(jobInfo) <= 0) {
            Log.d(TAG, "AliveJobService schedule failed.");
        } else {
            Log.d(TAG, "AliveJobService schedule sucess.");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private JobInfo initJonInfo(int startId) {
        JobInfo.Builder builder = new JobInfo.Builder(0,
                new ComponentName(getPackageName(),AliveJobService.class.getName()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMinimumLatency(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS)
                    .setOverrideDeadline(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS)
                    .setBackoffCriteria(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS, JobInfo.BACKOFF_POLICY_LINEAR);
        } else {
            builder.setPeriodic(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS);
        }
        builder.setPersisted(false);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
        builder.setRequiresCharging(false);
        return builder.build();
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_PULL_ALIVE, jobParameters));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
