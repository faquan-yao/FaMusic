package com.yaofaquan.famusic.api;

import com.yaofaquan.famusic.model.User.User;
import com.yaofaquan.famusic.model.discory.BaseRecommandModel;
import com.yaofaquan.famusic.model.discory.BaseRecommandMoreModel;
import com.yaofaquan.famusic.model.friend.BaseFriendModel;
import com.yaofaquan.lib_network.okhttp.CommonOkHttpClient;
import com.yaofaquan.lib_network.okhttp.listener.DisposeDataHandle;
import com.yaofaquan.lib_network.okhttp.listener.DisposeDataListener;
import com.yaofaquan.lib_network.okhttp.request.CommonRequest;
import com.yaofaquan.lib_network.okhttp.request.RequestParams;

public class RequestCenter {

    static class HttpConstants {
        private static final String ROOT_URL = " https://mock.mengxuegu.com/mock/6190b7cef126df7bfd5b75ab/FaMusic";

        /**
         * 首页请求接口
         */
        private static String HOME_RECOMMAND = ROOT_URL + "/module_voice/home_recommand";

        private static String HOME_RECOMMAND_MORE = ROOT_URL + "/module_voice/home_recommand_more";

        private static String HOME_FRIEND = ROOT_URL + "/module_voice/home_friend";

        /**
         * 登陆接口
         */
        public static String LOGIN = ROOT_URL + "/login";
    }

    //根据参数发送所有post请求
    public static void getRequest(String url, RequestParams params, DisposeDataListener listener,
                                  Class<?> clazz) {
        CommonOkHttpClient.post(CommonRequest.
                createPostRequest(url, params, null), new DisposeDataHandle(listener, clazz));
    }

    public static void requestRecommandData(DisposeDataListener listener) {
        RequestCenter.getRequest(HttpConstants.HOME_RECOMMAND, null, listener,
                BaseRecommandModel.class);
    }

    public static void requestRecommandMore(DisposeDataListener listener) {
        RequestCenter.getRequest(HttpConstants.HOME_RECOMMAND_MORE, null, listener,
                BaseRecommandMoreModel.class);
    }

    public static void requestFriendData(DisposeDataListener listener) {
        RequestCenter.getRequest(HttpConstants.HOME_FRIEND, null, listener, BaseFriendModel.class);
    }

    /**
     * 用户登陆请求
     */
    public static void login(DisposeDataListener listener) {

        RequestParams params = new RequestParams();
        params.put("mb", "123456789");
        params.put("pwd", "123456");
        RequestCenter.getRequest(HttpConstants.LOGIN, params, listener, User.class);
    }
}
