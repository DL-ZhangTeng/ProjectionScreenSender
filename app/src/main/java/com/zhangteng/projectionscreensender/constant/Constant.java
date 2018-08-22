package com.zhangteng.projectionscreensender.constant;

import android.media.MediaFormat;

/**
 * Created by xu.wang
 * Date on  2018/5/28 11:00:09.
 *
 * @Desc
 */

public class Constant {
    public static String ip = "10.4.162.50";
//    public static String ip = "10.3.140.216";
    public static int port = 8888;


    public static String MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_AVC;
    public static int KEY_BIT_RATE = 2000000;
    public static int KEY_FRAME_RATE = 20;
    public static int KEY_I_FRAME_INTERVAL = 1;
    public static int VIDEO_FPS = 30;

    // 横屏
    //            byte[] header_sps = {0, 0, 0, 1, 103, 66, -128, 31, -38, 1, 64, 22, -24, 6, -48, -95, 53};
    //            byte[] header_pps = {0, 0 ,0, 1, 104, -50, 6, -30};

    // 竖屏
    public static byte[] header_sps = {0, 0, 0, 1, 103, 66, -128, 31, -38, 2, -48, 40, 104, 6, -48, -95, 53};
    public static byte[] header_pps = {0, 0, 0, 1, 104, -50, 6, -30};
}
