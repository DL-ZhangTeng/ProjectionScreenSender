package com.zhangteng.projectionscreensender.controller;

import android.media.MediaCodec;
import android.os.Looper;

import com.zhangteng.projectionscreensender.configuration.VideoConfiguration;
import com.zhangteng.projectionscreensender.packer.Packer;
import com.zhangteng.projectionscreensender.sender.Sender;
import com.zhangteng.projectionscreensender.sender.TcpSender;
import com.zhangteng.projectionscreensender.video.OnVideoEncodeListener;

import java.nio.ByteBuffer;

/**
 * Created by swing on 2018/8/21.
 */
public class StreamController implements OnVideoEncodeListener, Packer.OnPacketListener {
    private Packer mPacker;
    private Sender mSender;
    private IVideoController mVideoController;

    public StreamController(IVideoController videoController) {
        mVideoController = videoController;
    }

    public void setVideoConfiguration(VideoConfiguration videoConfiguration) {
        mVideoController.setVideoConfiguration(videoConfiguration);
    }

    public void setPacker(Packer packer) {
        mPacker = packer;
        mPacker.setPacketListener(this);
    }

    public void setSender(Sender sender) {
        mSender = sender;
    }

    public synchronized void start() {
        executeTask(new Task() {
            @Override
            public void run() {
                if (mPacker == null) {
                    return;
                }
                if (mSender == null) {
                    return;
                }
                mPacker.start();
                mSender.start();
                mVideoController.setVideoEncoderListener(StreamController.this);
                mVideoController.start();
            }
        });

    }

    public synchronized void stop() {
        executeTask(new Task() {
            @Override
            public void run() {
                mVideoController.setVideoEncoderListener(null);
                mVideoController.stop();
                if (mSender != null) {
                    mSender.stop();
                }
                if (mPacker != null) {
                    mPacker.stop();
                }
            }
        });
    }

    public synchronized void pause() {
        executeTask(new Task() {
            @Override
            public void run() {
                mVideoController.pause();
            }
        });
    }

    public synchronized void resume() {
        executeTask(new Task() {
            @Override
            public void run() {
                mVideoController.resume();
            }
        });
    }

    public boolean setVideoBps(int bps) {
        return mVideoController.setVideoBps(bps);
    }

    @Override
    public void onVideoEncode(ByteBuffer bb, MediaCodec.BufferInfo bi) {
        if (mPacker != null) {
            mPacker.onVideoData(bb, bi);
        }
    }

    @Override
    public void onPacket(byte[] data, int packetType) {
        if (mSender != null) {
            mSender.onData(data, packetType);
        }
    }

    @Override
    public void onSpsPps(byte[] mSpsPps) {
        if (mSender != null && mSender instanceof TcpSender) {
            ((TcpSender) mSender).setSpsPps(mSpsPps);
        }
    }

    private interface Task {
        void run();
    }

    private void executeTask(final Task task) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    task.run();
                }
            }).start();
        } else {
            task.run();
        }
    }
}
