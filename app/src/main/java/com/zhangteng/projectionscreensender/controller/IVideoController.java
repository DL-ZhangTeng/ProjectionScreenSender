package com.zhangteng.projectionscreensender.controller;

import com.zhangteng.projectionscreensender.configuration.VideoConfiguration;
import com.zhangteng.projectionscreensender.video.OnVideoEncodeListener;

/**
 * Created by swing on 2018/8/21.
 */
public interface IVideoController {

    void start();

    void stop();

    void pause();

    void resume();

    boolean setVideoBps(int bps);

    void setVideoEncoderListener(OnVideoEncodeListener listener);

    void setVideoConfiguration(VideoConfiguration configuration);
}
