package com.zhangteng.projectionscreensender.sender;

import com.zhangteng.projectionscreensender.entity.Frame;
import com.zhangteng.projectionscreensender.sender.packets.Video;

import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * Created by swing on 2018/8/21.
 */
public class TcpWriteThread extends Thread {
    private BufferedOutputStream bos;
    private ISendQueue iSendQueue;
    private volatile boolean startFlag;
    private OnTcpWriteListener mListener;
    private final String TAG = "TcpWriteThread";

    public TcpWriteThread(BufferedOutputStream bos, ISendQueue sendQueue, OnTcpWriteListener listener) {
        this.bos = bos;
        startFlag = true;
        this.iSendQueue = sendQueue;
        this.mListener = listener;
    }

    @Override
    public void run() {
        super.run();
        while (startFlag) {
            Frame frame = iSendQueue.takeFrame();
            if (frame == null) {
                continue;
            }
            if (frame.data instanceof Video) {
                sendData(((Video) frame.data).getData());
//                Log.e(TAG,"send a msg" );
            }
        }
    }


    public void shutDown() {
        startFlag = false;
        this.interrupt();
    }

    public void sendData(byte[] buff) {
        try {
            Encode encode = new Encode(buff);
            bos.write(encode.buildSendContent());
            bos.flush();
        } catch (IOException e) {
            startFlag = false;
            mListener.socketDisconnect();
        }
    }


}
