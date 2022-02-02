package com.yaofaquan.famusic.db;

import android.database.sqlite.SQLiteDatabase;

import com.yaofaquan.famusic.application.FaMusicApplication;
import com.yaofaquan.famusic.model.User.DaoMaster;
import com.yaofaquan.famusic.model.User.DaoSession;
import com.yaofaquan.famusic.model.User.User;
import com.yaofaquan.famusic.model.User.UserDao;

import java.util.List;

public class UserGreenDaoHelper {
    private static final String DB_NAME = "famusic_user_db";
    private static DaoMaster.DevOpenHelper mHelper;
    private static SQLiteDatabase mDb;
    private static DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;

    public static void initDataBase() {
        mHelper = new DaoMaster.DevOpenHelper(FaMusicApplication.getInstance(), DB_NAME, null);
        mDb = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(mDb);
        mDaoSession = mDaoMaster.newSession();
    }

    public static void saveUser(User user) {
        UserDao dao = mDaoSession.getUserDao();
        dao.insertOrReplace(user);
    }

    public static User getUser() {
        UserDao dao = mDaoSession.getUserDao();
        List<User> users = dao.queryBuilder().where(UserDao.Properties.Username.notEq(""))
                .build().list();
        if (users != null && users.size() > 0) {
            return users.get(0);
        } else {
            return null;
        }
    }

    public static void clearUsers() {
        UserDao dao = mDaoSession.getUserDao();
        dao.deleteAll();
    }
}
