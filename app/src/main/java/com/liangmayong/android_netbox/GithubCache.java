package com.liangmayong.android_netbox;

import android.util.Log;

import com.liangmayong.netbox2.interfaces.NetboxCache;

/**
 * Created by LiangMaYong on 2016/9/13.
 */
public class GithubCache implements NetboxCache {

    @Override
    public void saveCache(String key, String body) {

        Log.d("TAG", key + "    -------------    " + body);
    }

    @Override
    public String getCache(String key) {
        return null;
    }

}
