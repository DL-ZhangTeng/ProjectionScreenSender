package com.zhangteng.projectionscreensender.sender;

/**
 * Created by swing on 2018/8/21.
 */
public interface OnTcpReadListener {
    void socketDisconnect();    //断开连接

    void connectSuccess();  //收到server消息,连接成功.
}
