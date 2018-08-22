package com.zhangteng.projectionscreensender.sender;

import android.util.Log;

import com.zhangteng.projectionscreensender.configuration.VideoConfiguration;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by swing on 2018/8/21.
 */
public class TcpConnection implements OnTcpReadListener, OnTcpWriteListener {
    private TcpConnectListener listener;
    private static final String TAG = "TcpConnection";
    private Socket socket;
    private State state = State.INIT;
    private ISendQueue mSendQueue;
    private TcpWriteThread mWrite;
    private TcpReadThread mRead;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private int width, height;
    private int maxBps;
    private int fps;
    private byte[] mSpsPps;

    public enum State {
        INIT,
        CONNECTING,
        LIVING
    }

    public void setConnectListener(TcpConnectListener listener) {
        this.listener = listener;
    }

    public void setSendQueue(ISendQueue sendQueue) {
        mSendQueue = sendQueue;
    }

    public void connect(String ip, int port) {
        socket = new Socket();
        SocketAddress socketAddress = new InetSocketAddress(ip, port);
        try {
            socket.connect(socketAddress, 20000);
            socket.setSoTimeout(60000);
        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onSocketConnectFail();
                return;
            }
        }
        listener.onSocketConnectSuccess();
        if (listener == null || socket == null || !socket.isConnected()) {
            listener.onSocketConnectFail();
            return;
        }
        try {
            in = new BufferedInputStream(socket.getInputStream());
            out = new BufferedOutputStream(socket.getOutputStream());
            mWrite = new TcpWriteThread(out, mSendQueue, this);
            mRead = new TcpReadThread(in, this);
            //todo 需要播放端发送准备确认字段
//            mRead.start();
            mWrite.start();
            state = State.LIVING;
            listener.onTcpConnectSuccess();
        } catch (IOException e) {
            e.printStackTrace();
            listener.onTcpConnectFail();
        }
    }

    public void setVideoParams(VideoConfiguration videoConfiguration) {
        width = videoConfiguration.width;
        height = videoConfiguration.height;
        fps = videoConfiguration.fps;
        maxBps = videoConfiguration.maxBps;
    }

    public void setSpsPps(byte[] spsPps) {
        this.mSpsPps = spsPps;
    }

    @Override
    public void socketDisconnect() {
        listener.onSocketDisconnect();
    }

    @Override
    public void connectSuccess() {
        Log.e(TAG, "connect success");
        mWrite.start();
    }


    private void clearSocket() {
        if (socket != null && socket.isConnected()) {
            try {
                socket.close();
                socket = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (mWrite != null) {
                    mWrite.shutDown();
                }
                if (mRead != null) {
                    mRead.shutDown();
                }
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                clearSocket();
            }
        }.start();
        state = State.INIT;
    }

    public State getState() {
        return state;
    }

}
