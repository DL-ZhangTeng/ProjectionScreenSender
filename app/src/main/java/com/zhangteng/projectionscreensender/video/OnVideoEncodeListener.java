package com.zhangteng.projectionscreensender.video;

import android.media.MediaCodec;

import java.nio.ByteBuffer;

/**
 * Created by swing on 2018/8/21.
 */
public interface OnVideoEncodeListener {
    void onVideoEncode(ByteBuffer bb, MediaCodec.BufferInfo bi);
}
