package com.zhangteng.projectionscreensender.sender;

import java.nio.ByteBuffer;

/**
 * Created by swing on 2018/8/21.
 */
public class Encode {
    private byte[] buff;    //要发送的内容

    public Encode(byte[] buff) {
        this.buff = buff;
    }

    public byte[] buildSendContent() {
        if (buff == null || buff.length == 0) {
            return null;
        }
        ByteBuffer bb = ByteBuffer.allocate(4 + buff.length);
        bb.put(int2Bytes(buff.length));
        bb.put(buff);
        return bb.array();
    }

    /**
     * 将int转为长度为4的byte数组
     *
     * @param length
     * @return
     */
    public static byte[] int2Bytes(int length) {
        byte[] result = new byte[4];
        result[0] = (byte) length;
        result[1] = (byte) (length >> 8);
        result[2] = (byte) (length >> 16);
        result[3] = (byte) (length >> 24);
        return result;
    }
}
