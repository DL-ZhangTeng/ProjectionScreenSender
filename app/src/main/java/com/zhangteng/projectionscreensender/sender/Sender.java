package com.zhangteng.projectionscreensender.sender;

/**
 * Created by swing on 2018/8/21.
 */
public interface Sender {

    void start();

    void onData(byte[] data, int type);

    void stop();
}
