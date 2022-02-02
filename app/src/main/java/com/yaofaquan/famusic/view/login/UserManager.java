package com.yaofaquan.famusic.view.login;

import com.yaofaquan.famusic.api.RequestCenter;
import com.yaofaquan.famusic.db.UserGreenDaoHelper;
import com.yaofaquan.famusic.model.User.User;
import com.yaofaquan.lib_network.okhttp.listener.DisposeDataListener;

public class UserManager {
    private static UserManager mInstance;
    private User mUser;
    private UserManager() {

    }

    public static UserManager getInstance() {
        if (mInstance == null) {
            synchronized (UserManager.class) {
                if (mInstance == null) {
                    mInstance = new UserManager();
                }
            }
        }
        return mInstance;
    }

    public void saveUser(User user) {
        mUser = user;
        saveLocal(user);
    }

    private void saveLocal(User user) {
        UserGreenDaoHelper.saveUser(user);
    }

    public User getUser() {
        if (mUser == null) {
            mUser = getLocalUser();
        }
        return mUser;
    }

    private User getLocalUser() {
        return UserGreenDaoHelper.getUser();
    }

    public boolean hasUserInfo() {
        return getUser() != null;
    }

    public boolean hasLogin() {
        if (getUser() == null) {
            return false;
        } else {
            return RequestCenter.testLogin();
        }
    }

    public void clear() {
        mUser = null;
        clearLocal();
    }

    private void clearLocal() {
        UserGreenDaoHelper.clearUsers();
    }

    public boolean hasLogined() {
        return true;
    }
}
