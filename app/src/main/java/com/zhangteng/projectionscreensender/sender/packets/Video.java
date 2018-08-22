package com.zhangteng.projectionscreensender.sender.packets;

/**
 * Video data packet
 *
 */
public class Video extends ContentData {

    public Video(ChunkHeader header) {
        super(header);
    }

    public Video() {
        super(new ChunkHeader(ChunkType.TYPE_0_FULL, SessionInfo.RTMP_VIDEO_CHANNEL, MessageType.VIDEO));
    }

    @Override
    public String toString() {
        return "Video";
    }
}
