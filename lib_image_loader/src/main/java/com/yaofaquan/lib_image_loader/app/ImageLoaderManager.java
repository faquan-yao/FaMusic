package com.yaofaquan.lib_image_loader.app;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.yaofaquan.lib_image_loader.R;
import com.yaofaquan.lib_image_loader.image.Utils;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class ImageLoaderManager {
    private ImageLoaderManager() {

    }

    private static class SingletonHolder {
        private static ImageLoaderManager instance = new ImageLoaderManager();
    }

    public static ImageLoaderManager getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * 为ImageView加载图片
     */
    public void displayImageForView(ImageView view, String url) {
        Glide.with(view.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .transition(withCrossFade())
                .into(view);
    }

    /**
     * 为view加载圆形图片
     * @param view
     * @param url
     */
    public void displayImageForCircle(ImageView view, String url) {
        Glide.with(view.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .transition(withCrossFade())
                .into(new BitmapImageViewTarget(view){

                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(view.getResources(), resource);
                        drawable.setCircular(true);
                        view.setImageDrawable(drawable);
                    }
                });
    }

    public void displayImageForViewGroup(final ViewGroup group, String url) {
        Glide.with(group.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .into(new SimpleTarget<Bitmap>() {//设置宽高
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource,
                                                @Nullable Transition<? super Bitmap> transition) {
                        final Bitmap res = resource;
                        Observable.just(resource)
                                .map(new Function<Bitmap, Drawable>() {
                                    @Override
                                    public Drawable apply(Bitmap bitmap) {
                                        Drawable drawable = new BitmapDrawable(
                                                Utils.doBlur(res, 100, true)
                                        );
                                        return drawable;
                                    }
                                })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Drawable>() {
                                    @Override
                                    public void accept(Drawable drawable) throws Exception {
                                        group.setBackground(drawable);
                                    }
                                });
                    }
                });
    }

    public void displayImageForNotification(Context context, int rv_id, RemoteViews rv,
                                            Notification notification, int notification_id,
                                            String url) {
        this.displayImageForTarget(context, initNotificationTarget(context, rv_id, rv,
                notification, notification_id), url);
    }

    private NotificationTarget initNotificationTarget(Context context, int rv_id, RemoteViews rv,
                                                      Notification notification, int notification_id) {
        NotificationTarget target = new NotificationTarget(context, rv_id, rv,
                notification, notification_id);
        return target;
    }

    private void displayImageForTarget(Context context, Target target, String url) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .transition(withCrossFade())
                .fitCenter()
                .into(target);
    }

    private RequestOptions initCommonRequestOption() {
        RequestOptions options = new RequestOptions();
        options.placeholder(android.R.drawable.btn_default)
                .error(android.R.drawable.stat_notify_error)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .priority(Priority.NORMAL);
        return options;
    }
}
