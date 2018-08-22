package com.zhangteng.projectionscreensender.sender;

/**
 * Created by swing on 2018/8/21.
 */
public interface OnSenderListener {

    void onConnecting();

    void onConnected();

    void onDisConnected();

    void onPublishFail();

    void onNetGood();

    void onNetBad();
}
