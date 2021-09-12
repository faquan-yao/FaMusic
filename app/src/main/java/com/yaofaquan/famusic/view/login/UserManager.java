package com.yaofaquan.famusic.view.login;

import com.yaofaquan.famusic.model.User.User;

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

    }

    public User getUser() {
        if (mUser == null) {
            mUser = getLocalUser();
        }
        return mUser;
    }

    private User getLocalUser() {
        return null;
    }

    public boolean hasLogin() {
        return getUser() != null;
    }

    public void clear() {
        mUser = null;
        clearLocal();
    }

    private void clearLocal() {

    }
}
