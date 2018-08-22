package com.zhangteng.projectionscreensender.sender;

import com.zhangteng.projectionscreensender.entity.Frame;

/**
 * Created by swing on 2018/8/21.
 */
public interface ISendQueue {
    void start();
    void stop();
    void setBufferSize(int size);
    void putFrame(Frame frame);
    Frame takeFrame();
    void setSendQueueListener(SendQueueListener listener);
}
