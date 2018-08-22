package com.zhangteng.projectionscreensender.sender;

/**
 * Created by swing on 2018/8/21.
 */
public interface TcpConnectListener {
    void onSocketConnectSuccess();

    void onSocketConnectFail();

    void onTcpConnectSuccess();

    void onTcpConnectFail();

    void onPublishSuccess();

    void onPublishFail();

    void onSocketDisconnect();
}
