package com.example.smart.test1.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Smart on 2018-03-29.
 */

public class AppVersion extends BmobObject {
    private int version_i;
    private String platform;
    private String update_log;
    private String version;
    private String android_url;

    public int getVersion_i() {
        return version_i;
    }

    public void setVersion_i(int version_i) {
        this.version_i = version_i;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUpdate_log() {
        return update_log;
    }

    public void setUpdate_log(String update_log) {
        this.update_log = update_log;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAndroid_url() {
        return android_url;
    }

    public void setAndroid_url(String android_url) {
        this.android_url = android_url;
    }
}
