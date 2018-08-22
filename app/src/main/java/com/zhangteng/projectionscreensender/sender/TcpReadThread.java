package com.zhangteng.projectionscreensender.sender;

import android.os.SystemClock;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * Created by swing on 2018/8/21.
 */
public class TcpReadThread extends Thread {
    private final static String TAG = "TcpReadThread";
    private BufferedInputStream bis;
    private OnTcpReadListener mListener;
    private volatile boolean startFlag;

    public TcpReadThread(BufferedInputStream bis, OnTcpReadListener listener) {
        this.bis = bis;
        this.mListener = listener;
        startFlag = true;
    }

    @Override
    public void run() {
        super.run();
        while (startFlag) {
            SystemClock.sleep(50);
            try {
                acceptMsg();
            } catch (IOException e) {
                startFlag = false;
                mListener.socketDisconnect();
//                Log.e(TAG, "read data Exception = " + e.toString());
            }
        }
    }

    public void shutDown() {
        startFlag = false;
        this.interrupt();
    }

    public void acceptMsg() throws IOException {
        if (mListener == null) {
            return;
        }
        if (bis.available() <= 0) {
            return;
        }
        byte[] bytes = new byte[2];
        bis.read(bytes);
        String s = new String(bytes);
        if (TextUtils.isEmpty(s)) {
            return;
        }
        if (TextUtils.equals(s, "OK")) {
            mListener.connectSuccess();
        }
    }
}
