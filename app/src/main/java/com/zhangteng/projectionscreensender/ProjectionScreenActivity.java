package com.zhangteng.projectionscreensender;

import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zhangteng.projectionscreensender.R;
import com.zhangteng.projectionscreensender.constant.Constant;
import com.zhangteng.projectionscreensender.configuration.VideoConfiguration;
import com.zhangteng.projectionscreensender.packer.TcpPacker;
import com.zhangteng.projectionscreensender.record.ScreenRecordActivity;
import com.zhangteng.projectionscreensender.sender.OnSenderListener;
import com.zhangteng.projectionscreensender.sender.TcpSender;

import java.io.File;
import java.io.IOException;

/**
 * Created by swing on 2018/8/21.
 */
public class ProjectionScreenActivity extends ScreenRecordActivity implements OnSenderListener {
    private Button btn_start;
    private String ip;
    private VideoConfiguration mVideoConfiguration;
    private TcpSender tcpSender;
    private final static String TAG = "LaifengScreenRecord";
    private boolean isRecord = false;
    private EditText et_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projection_screen);
        ip = Constant.ip;
        initialView();
    }

    private void initialView() {
        btn_start = findViewById(R.id.btn_start);
        et_main = findViewById(R.id.et_main);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRecord) {
                    if (!TextUtils.isEmpty(et_main.getText().toString())) {
                        ip = btn_start.getText().toString();
                    }
                    Log.e("Test", "" + ip);
                    requestRecording();
                    Log.e("Test", "start record");
                    btn_start.setText("start record");
                } else {
                    stopRecording();
                    Log.e("Test", "stop record");
                    btn_start.setText("stop record");
                }
            }
        });
    }

    private void initialData() {
        String path = Environment.getExternalStorageDirectory() + "/test";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file1 = new File(file, "test.h264");
        if (file1.exists()) {
            file1.delete();
        }
        try {
            file1.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Test", "" + e.toString());
        }
    }

    @Override
    protected void requestRecordSuccess() {
        super.requestRecordSuccess();
        isRecord = true;
        startRecord();
    }

    @Override
    protected void requestRecordFail() {
        super.requestRecordFail();
    }

    private void startRecord() {
        TcpPacker packer = new TcpPacker();
        mVideoConfiguration = new VideoConfiguration.HighBuilder().build();
        setVideoConfiguration(mVideoConfiguration);
        setRecordPacker(packer);

        tcpSender = new TcpSender(ip, Constant.port);
        tcpSender.setSenderListener(this);
        tcpSender.setVideoParams(mVideoConfiguration);
        tcpSender.connect();
        setRecordSender(tcpSender);
        startRecording();
    }

    @Override
    public void onConnecting() {
        Log.e(TAG, "onConnecting ...");
    }

    @Override
    public void onConnected() {
        Log.e(TAG, "onConnected");
    }

    @Override
    public void onDisConnected() {
        Log.e(TAG, "onDisConnected");
    }

    @Override
    public void onPublishFail() {
        Log.e(TAG, "onPublishFail");
    }

    @Override
    public void onNetGood() {

    }

    @Override
    public void onNetBad() {

    }


    @Override
    protected void onDestroy() {
        tcpSender.stop();
        super.onDestroy();
    }
}
