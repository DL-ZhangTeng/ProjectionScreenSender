package com.zhangteng.projectionscreensender.sender;

import android.util.Log;

import com.zhangteng.projectionscreensender.configuration.VideoConfiguration;
import com.zhangteng.projectionscreensender.entity.Frame;
import com.zhangteng.projectionscreensender.packer.TcpPacker;
import com.zhangteng.projectionscreensender.sender.packets.Chunk;
import com.zhangteng.projectionscreensender.sender.packets.Video;

/**
 * Created by swing on 2018/8/21.
 */
public class TcpSender implements Sender, SendQueueListener {
    private ISendQueue mSendQueue = new NormalSendQueue();
    private static final String TAG = "TcpSender";
    private OnSenderListener sendListener;
    private TcpConnection mTcpConnection;
    private WeakHandler weakHandler = new WeakHandler();
    private String ip;
    private int port;


    public TcpSender(String ip, int port) {
        mTcpConnection = new TcpConnection();
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void start() {
        mSendQueue.setSendQueueListener(this);
        mSendQueue.start();
    }

    public void setVideoParams(VideoConfiguration videoConfiguration) {
        mTcpConnection.setVideoParams(videoConfiguration);
    }

    @Override
    public void onData(byte[] data, int type) {
        Frame<Chunk> frame = null;
        Video video = new Video();
        video.setData(data);
        if (type == TcpPacker.FIRST_VIDEO) {
            frame = new Frame(video, type, Frame.FRAME_TYPE_CONFIGURATION);
        } else if (type == TcpPacker.KEY_FRAME) {
            frame = new Frame(video, type, Frame.FRAME_TYPE_KEY_FRAME);
        } else if (type == TcpPacker.INTER_FRAME) {
            frame = new Frame(video, type, Frame.FRAME_TYPE_INTER_FRAME);
        }
        if (frame == null) return;
        mSendQueue.putFrame(frame);
    }

    @Override
    public void stop() {
        mTcpConnection.stop();
        mSendQueue.stop();
    }

    public void connect() {
        mTcpConnection.setSendQueue(mSendQueue);
        new Thread(new Runnable() {
            @Override
            public void run() {
                connectNotInUi();
            }
        }).start();

    }

    private synchronized void connectNotInUi() {
        mTcpConnection.setConnectListener(mTcpListener);
        mTcpConnection.connect(ip, port);
    }

    @Override
    public void good() {
        weakHandler.post(new Runnable() {
            @Override
            public void run() {
                sendListener.onNetGood();
            }
        });
    }

    @Override
    public void bad() {
        weakHandler.post(new Runnable() {
            @Override
            public void run() {
                sendListener.onNetBad();
            }
        });
    }

    private TcpConnectListener mTcpListener = new TcpConnectListener() {
        @Override
        public void onSocketConnectSuccess() {
            Log.e(TAG, "onSocketConnectSuccess");
        }

        @Override
        public void onSocketConnectFail() {
            Log.e(TAG, "onSocketConnectFail");
            disConnected();
        }

        @Override
        public void onTcpConnectSuccess() {
            Log.e(TAG, "onTcpConnectSuccess");
        }

        @Override
        public void onTcpConnectFail() {
            Log.e(TAG, "onTcpConnectFail");
            disConnected();
        }

        @Override
        public void onPublishSuccess() {
            Log.e(TAG, "onPublishSuccess");
            weakHandler.post(new Runnable() {
                @Override
                public void run() {
                    sendListener.onConnected();
                }
            });
        }

        @Override
        public void onPublishFail() {
            Log.e(TAG, "onPublishFail");
            weakHandler.post(new Runnable() {
                @Override
                public void run() {
                    sendListener.onPublishFail();
                }
            });
        }

        @Override
        public void onSocketDisconnect() {
            Log.e(TAG, "onSocketDisconnect");
            disConnected();
        }

    };

    private void disConnected() {
        weakHandler.post(new Runnable() {
            @Override
            public void run() {
                sendListener.onDisConnected();
            }
        });
    }


    public void setSenderListener(OnSenderListener listener) {
        this.sendListener = listener;
    }

    /**
     * add by xu.wang 为解决首次黑屏而加
     */
    public void setSpsPps(byte[] spsPps) {
        if (mTcpConnection != null) mTcpConnection.setSpsPps(spsPps);
    }

}
