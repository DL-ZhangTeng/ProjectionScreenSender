package com.zhangteng.projectionscreensender.packer;

import android.media.MediaCodec;

import java.nio.ByteBuffer;

/**
 * Created by swing on 2018/8/21.
 */
public class TcpPacker implements Packer, AnnexbHelper.AnnexbNaluListener {

    public static final int HEADER = 0;
    public static final int METADATA = 1;
    public static final int FIRST_VIDEO = 2;
    public static final int KEY_FRAME = 5;
    public static final int INTER_FRAME = 6;

    private OnPacketListener packetListener;
    private boolean isHeaderWrite;
    private boolean isKeyFrameWrite;

    private AnnexbHelper mAnnexbHelper;

    private byte[] mSpsPps;
    private byte[] header = {0x00, 0x00, 0x00, 0x01};

    public TcpPacker() {
        mAnnexbHelper = new AnnexbHelper();
    }

    @Override
    public void setPacketListener(OnPacketListener listener) {
        packetListener = listener;
    }


    @Override
    public void start() {
        mAnnexbHelper.setAnnexbNaluListener(this);
    }

    @Override
    public void onVideoData(ByteBuffer bb, MediaCodec.BufferInfo bi) {
        mAnnexbHelper.analyseVideoDataonlyH264(bb, bi);
    }

    @Override
    public void stop() {
        isHeaderWrite = false;
        isKeyFrameWrite = false;
        mAnnexbHelper.stop();
    }

    @Override
    public void onSpsPps(byte[] sps, byte[] pps) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(sps.length + 4);
        byteBuffer.put(header);
        byteBuffer.put(sps);
        mSpsPps = byteBuffer.array();

        packetListener.onPacket(mSpsPps, FIRST_VIDEO);
        ByteBuffer byteBuffer1 = ByteBuffer.allocate(pps.length + 4);
        byteBuffer1.put(header);
        byteBuffer1.put(pps);
        packetListener.onPacket(byteBuffer1.array(), FIRST_VIDEO);
        isHeaderWrite = true;
    }


    @Override
    public void onVideo(byte[] video, boolean isKeyFrame) {
        if (packetListener == null || !isHeaderWrite) {
            return;
        }
        int packetType = INTER_FRAME;
        if (isKeyFrame) {
            isKeyFrameWrite = true;
            packetType = KEY_FRAME;
        }
        //确保第一帧是关键帧，避免一开始出现灰色模糊界面
        if (!isKeyFrameWrite) {
            return;
        }
        ByteBuffer bb;
        if (isKeyFrame) {
            bb = ByteBuffer.allocate(video.length);
            bb.put(video);
        } else {
            bb = ByteBuffer.allocate(video.length);
            bb.put(video);
        }
        packetListener.onPacket(bb.array(), packetType);
    }

}
