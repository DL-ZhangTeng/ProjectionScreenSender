package com.zhangteng.projectionscreensender.record;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.util.Log;

import com.zhangteng.projectionscreensender.configuration.VideoConfiguration;
import com.zhangteng.projectionscreensender.controller.ScreenVideoVideoController;
import com.zhangteng.projectionscreensender.controller.StreamController;
import com.zhangteng.projectionscreensender.packer.Packer;
import com.zhangteng.projectionscreensender.sender.Sender;

/**
 * Created by swing on 2018/8/21.
 */
public class ScreenRecordActivity extends Activity {
    private static final int RECORD_REQUEST_CODE = 101;
    private StreamController mStreamController;
    private MediaProjectionManager mMediaProjectionManage;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void requestRecording() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            Log.d("Pro..Sc..Activity", "Device don't support screen recording.");
            return;
        }
        mMediaProjectionManage = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent captureIntent = mMediaProjectionManage.createScreenCaptureIntent();
        startActivityForResult(captureIntent, RECORD_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECORD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ScreenVideoVideoController videoController = new ScreenVideoVideoController(mMediaProjectionManage, resultCode, data);
                mStreamController = new StreamController(videoController);
                requestRecordSuccess();
            } else {
                requestRecordFail();
            }
        }
    }

    protected void requestRecordSuccess() {

    }

    protected void requestRecordFail() {

    }

    public void setVideoConfiguration(VideoConfiguration videoConfiguration) {
        if (mStreamController != null) {
            mStreamController.setVideoConfiguration(videoConfiguration);
        }
    }

    protected void startRecording() {
        if (mStreamController != null) {
            mStreamController.start();
        }
    }

    protected void stopRecording() {
        if (mStreamController != null) {
            mStreamController.stop();
        }
    }

    protected void pauseRecording() {
        if (mStreamController != null) {
            mStreamController.pause();
        }
    }


    protected void resumeRecording() {
        if (mStreamController != null) {
            mStreamController.resume();
        }
    }

    protected boolean setRecordBps(int bps) {
        if (mStreamController != null) {
            return mStreamController.setVideoBps(bps);
        } else {
            return false;
        }
    }

    protected void setRecordPacker(Packer packer) {
        if (mStreamController != null) {
            mStreamController.setPacker(packer);
        }
    }

    protected void setRecordSender(Sender sender) {
        if (mStreamController != null) {
            mStreamController.setSender(sender);
        }
    }

    @Override
    protected void onDestroy() {
        stopRecording();
        super.onDestroy();
    }
}
