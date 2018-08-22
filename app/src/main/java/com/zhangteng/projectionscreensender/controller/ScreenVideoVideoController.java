package com.zhangteng.projectionscreensender.controller;

import android.annotation.TargetApi;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import com.zhangteng.projectionscreensender.configuration.VideoConfiguration;
import com.zhangteng.projectionscreensender.mediacodec.VideoMediaCodec;
import com.zhangteng.projectionscreensender.record.ScreenRecordEncoder;
import com.zhangteng.projectionscreensender.video.OnVideoEncodeListener;

/**
 * Created by swing on 2018/8/21.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ScreenVideoVideoController implements IVideoController {

    private MediaProjectionManager mManager;
    private int resultCode;
    private Intent resultData;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjection mMediaProjection;
    private VideoConfiguration mVideoConfiguration = VideoConfiguration.getDefaultVideoConfiguration();
    private ScreenRecordEncoder mEncoder;
    private OnVideoEncodeListener mListener;

    public ScreenVideoVideoController(MediaProjectionManager manager, int resultCode, Intent resultData) {
        this.mManager = manager;
        this.resultCode = resultCode;
        this.resultData = resultData;
    }

    @Override
    public void start() {
        mEncoder = new ScreenRecordEncoder(mVideoConfiguration);
        final Surface surface = mEncoder.getSurface();
        mEncoder.start();
        mEncoder.setOnVideoEncodeListener(mListener);
        mMediaProjection = mManager.getMediaProjection(resultCode, resultData);
        int width = VideoMediaCodec.getVideoSize(mVideoConfiguration.width);
        int height = VideoMediaCodec.getVideoSize(mVideoConfiguration.height);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenRecoder",
                width, height, 1, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, surface, null, null);
    }

    @Override
    public void stop() {
        if (mEncoder != null) {
            mEncoder.setOnVideoEncodeListener(null);
            mEncoder.stop();
            mEncoder = null;
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
    }

    @Override
    public void pause() {
        if (mEncoder != null) {
            mEncoder.setPause(true);
        }
    }

    @Override
    public void resume() {
        if (mEncoder != null) {
            mEncoder.setPause(false);
        }
    }

    @Override
    public boolean setVideoBps(int bps) {
        //重新设置硬编bps，在低于19的版本需要重启编码器
        boolean result = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            //由于重启硬编编码器效果不好，此次不做处理
            Log.d("ScreenVideoVideoController", "Bps need change, but MediaCodec do not support.");
        } else {
            if (mEncoder != null) {
                Log.d("ScreenVideoVideoController", "Bps change, current bps: " + bps);
                mEncoder.setRecorderBps(bps);
                result = true;
            }
        }
        return result;
    }

    @Override
    public void setVideoEncoderListener(OnVideoEncodeListener listener) {
        mListener = listener;
    }

    @Override
    public void setVideoConfiguration(VideoConfiguration configuration) {
        mVideoConfiguration = configuration;
    }
}
