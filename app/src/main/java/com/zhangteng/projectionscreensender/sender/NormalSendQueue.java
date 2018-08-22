package com.zhangteng.projectionscreensender.sender;

import android.util.Log;

import com.zhangteng.projectionscreensender.entity.Frame;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.zhangteng.projectionscreensender.entity.Frame.FRAME_TYPE_INTER_FRAME;
import static com.zhangteng.projectionscreensender.entity.Frame.FRAME_TYPE_KEY_FRAME;

/**
 * Created by swing on 2018/8/21.
 */
public class NormalSendQueue implements ISendQueue {
    /**
     * 缓存区大小
     */
    private static final int SCAN_MAX_TIME = 5;
    /**
     * 仲裁次数,每循环SCAN_MAX_TIME 次,每次sleep(DEFAULT_SLEEP_TIME),会执行一次检查网速的代码
     */
    private static final int NORMAL_FRAME_BUFFER_SIZE = 150;
    private static final int DEFAULT_SLEEP_TIME = 200;
    /**
     * 循环SCAN_MAX_TIME 次,有 DEFAULT_NEGATIVE_COUNT 次输入queue的帧小于取走的帧
     */
    private static final int DEFAULT_NEGATIVE_COUNT = 3;


    private ArrayBlockingQueue<Frame> mFrameBuffer;
    private int mFullQueueCount = NORMAL_FRAME_BUFFER_SIZE;
    /**
     * 总个数
     */
    private AtomicInteger mTotalFrameCount = new AtomicInteger(0);
    /**
     * 总个数
     */
    private AtomicInteger mGiveUpFrameCount = new AtomicInteger(0);
    /**
     * 队列里Key帧的总个数...
     */
    private AtomicInteger mKeyFrameCount = new AtomicInteger(0);

    /**
     * 进入总个数
     */
    private AtomicInteger mInFrameCount = new AtomicInteger(0);
    /**
     * 输出总个数
     */
    private AtomicInteger mOutFrameCount = new AtomicInteger(0);
    private volatile boolean mScanFlag;
    private SendQueueListener mSendQueueListener;
    private ScanThread mScanThread;
    private boolean isDebug = false;
    ;

    public NormalSendQueue() {
        mFrameBuffer = new ArrayBlockingQueue<>(mFullQueueCount, true);
    }

    @Override
    public void start() {
        mScanFlag = true;
        mScanThread = new ScanThread();
        mScanThread.start();
    }

    @Override
    public void stop() {
        mScanFlag = false;
        mInFrameCount.set(0);
        mOutFrameCount.set(0);
        mTotalFrameCount.set(0);
        mGiveUpFrameCount.set(0);
        mFrameBuffer.clear();
    }

    @Override
    public void setSendQueueListener(SendQueueListener listener) {
        mSendQueueListener = listener;
    }

    @Override
    public void setBufferSize(int size) {
        mFullQueueCount = size;
    }

    public int getBufferFrameCount() {
        return mTotalFrameCount.get();
    }

    public int getGiveUpFrameCount() {
        return mGiveUpFrameCount.get();
    }

    @Override
    public void putFrame(Frame frame) {
        if (mFrameBuffer == null) {
            return;
        }
        if (frame.frameType == FRAME_TYPE_KEY_FRAME) {
            mKeyFrameCount.getAndIncrement();
        }
        abandonData();
        try {
            mFrameBuffer.put(frame);
            showLog("put frame total = " + mFrameBuffer.size());
            mInFrameCount.getAndIncrement();
            mTotalFrameCount.getAndIncrement();
        } catch (InterruptedException e) {
            showLog("put Frame exception" + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public Frame takeFrame() {
        if (mFrameBuffer == null) {
            return null;
        }
        Frame frame = null;
        try {
            showLog("take frame total size = " + mFrameBuffer.size());
            frame = mFrameBuffer.take();
            if (frame.frameType == FRAME_TYPE_KEY_FRAME) {
                mKeyFrameCount.getAndDecrement();
            }
            mOutFrameCount.getAndIncrement();
            mTotalFrameCount.getAndDecrement();
        } catch (InterruptedException e) {
            //do nothing
            showLog("take Frame exception" + e.toString());
        }
        return frame;
    }

    private void abandonData() {
        if (mTotalFrameCount.get() >= (mFullQueueCount / 3)) {
            showLog("队列里的帧数太多,开始丢帧..");
            //从队列头部开始搜索，删除最早发现的连续P帧
            boolean pFrameDelete = false;
            boolean start = false;
            for (Frame frame : mFrameBuffer) {
                if (!start) {
                    showLog("丢掉了下一个KEY_FRAME前的所有INTER_FRAME..");
                }
                if (frame.frameType == FRAME_TYPE_INTER_FRAME) {
                    start = true;
                }
                if (start) {
                    if (frame.frameType == FRAME_TYPE_INTER_FRAME) {
                        mFrameBuffer.remove(frame);
                        mTotalFrameCount.getAndDecrement();
                        mGiveUpFrameCount.getAndIncrement();
                        pFrameDelete = true;
                    } else if (frame.frameType == FRAME_TYPE_KEY_FRAME) {
                        if (mKeyFrameCount.get() > 5) {
                            showLog("丢掉了一个关键帧.. total" + mKeyFrameCount.get());
                            mFrameBuffer.remove(frame);
                            mKeyFrameCount.getAndDecrement();
                            continue;
                        }
                        break;
                    }
                }
            }
            //从队列头部开始搜索，删除最早发现的I帧
            if (!pFrameDelete) {
                for (Frame frame : mFrameBuffer) {
                    if (frame.frameType == FRAME_TYPE_KEY_FRAME) {
                        mFrameBuffer.remove(frame);
                        showLog("丢掉了一个关键帧..");
                        mTotalFrameCount.getAndDecrement();
                        mGiveUpFrameCount.getAndIncrement();
                        mKeyFrameCount.getAndDecrement();
                        break;
                    }
                }
            }
        }
    }

    private class ScanThread extends Thread {

        private int mCurrentScanTime = 0;
        private ArrayList<ScanSnapShot> mScanSnapShotList = new ArrayList<>();

        @Override
        public void run() {
            while (mScanFlag) {
                //达到仲裁次数了
                if (mCurrentScanTime == SCAN_MAX_TIME) {
                    int averageDif = 0;
                    int negativeCounter = 0;
                    String strLog = "";
                    for (int i = 0; i < SCAN_MAX_TIME; i++) {
                        int dif = mScanSnapShotList.get(i).outCount - mScanSnapShotList.get(i).inCount;
                        if (dif < 0) {
                            negativeCounter++;
                        }
                        averageDif += dif;
                        strLog = strLog + String.format("n%d:%d  ", i, dif);
                    }
                    Log.d("NormalSendQueue", strLog);
                    if (negativeCounter >= DEFAULT_NEGATIVE_COUNT || averageDif < -100) {
                        //坏
                        Log.d("NormalSendQueue", "Bad Send Speed.");
                        if (mSendQueueListener != null) {
                            mSendQueueListener.bad();
                        }
                    } else {
                        //好
                        Log.d("NormalSendQueue", "Good Send Speed.");
                        if (mSendQueueListener != null) {
                            mSendQueueListener.good();
                        }
                    }
                    //清空
                    mScanSnapShotList.clear();
                    mCurrentScanTime = 0;
                }
                mScanSnapShotList.add(new ScanSnapShot(mInFrameCount.get(), mOutFrameCount.get()));
                mInFrameCount.set(0);
                mOutFrameCount.set(0);
                mCurrentScanTime++;
                try {
                    Thread.sleep(DEFAULT_SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ScanSnapShot {
        public int inCount;
        public int outCount;

        public ScanSnapShot(int inCount, int outCount) {
            this.inCount = inCount;
            this.outCount = outCount;
        }
    }

    private void showLog(String msg) {
        if (isDebug) {
            Log.e("NormalSendQueue", "" + msg);
        }
    }
}
