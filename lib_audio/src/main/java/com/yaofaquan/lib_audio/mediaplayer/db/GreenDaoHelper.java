package com.yaofaquan.lib_audio.mediaplayer.db;

import android.database.sqlite.SQLiteDatabase;

import com.yaofaquan.lib_audio.app.AudioHelper;
import com.yaofaquan.lib_audio.mediaplayer.model.AudioBean;
import com.yaofaquan.lib_audio.mediaplayer.model.Favourite;

public class GreenDaoHelper {
    private static final String DB_NAME = "famusic_db";
    //数据库管理类，用来创建数据库，升级数据库
    private static DaoMaster.DevOpenHelper mHelper;
    //最终创建好的数据库
    private static SQLiteDatabase mDb;
    //管理数据库
    private static DaoMaster mDaoMaster;
    //管理各种实体Dao
    private static DaoSession mDaoSession;

    public static void initDataBase() {
        mHelper = new DaoMaster.DevOpenHelper(AudioHelper.getContext(), DB_NAME, null);
        mDb = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(mDb);
        mDaoSession = mDaoMaster.newSession();
    }

    public static void addFavourite(AudioBean bean) {
        FavouriteDao dao = mDaoSession.getFavouriteDao();
        Favourite favourite = new Favourite();
        favourite.setAudioId(bean.id);
        favourite.setAudioBean(bean);
        dao.insert(favourite);
    }

    public static void removeFavourite(AudioBean bean) {
        FavouriteDao dao = mDaoSession.getFavouriteDao();
        Favourite favourite = dao.queryBuilder().where(
                FavouriteDao.Properties.AudioId.eq(bean.id)
        ).unique();
        dao.delete(favourite);
    }

    public static Favourite selectFavourite(AudioBean bean) {
        FavouriteDao dao = mDaoSession.getFavouriteDao();
        Favourite favourite = dao.queryBuilder().where(
                FavouriteDao.Properties.AudioId.eq(bean.id)
        ).unique();
        return favourite;
    }
}
