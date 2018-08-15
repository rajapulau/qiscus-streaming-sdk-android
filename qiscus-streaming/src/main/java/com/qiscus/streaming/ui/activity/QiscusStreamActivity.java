package com.qiscus.streaming.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.pedro.rtplibrary.rtmp.RtmpCamera1;
import com.qiscus.streaming.R;
import com.qiscus.streaming.data.QiscusStreamParameter;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import java.io.File;

public class QiscusStreamActivity extends AppCompatActivity implements ConnectCheckerRtmp, View.OnClickListener {
    private static final String TAG = QiscusStreamActivity.class.getSimpleName();

    private String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static Context context;
    private static String streamUrl;
    private static QiscusStreamParameter streamParameter;
    private RtmpCamera1 rtmpCamera;
    private Button stopButton;
    private boolean toggleStart;

    public static Intent generateIntent(Context ctx, String url, QiscusStreamParameter parameter) {
        Intent intent = new Intent(ctx, QiscusStreamActivity.class);
        intent.putExtra("STREAM_PARAMETER", parameter);
        context = ctx;
        streamUrl = url;
        streamParameter = parameter;
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qiscus_stream);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        toggleStart = true;
        stopButton = (Button) findViewById(R.id.buttonStop);
        stopButton.setOnClickListener(this);
        rtmpCamera = new RtmpCamera1(getApplicationContext(), QiscusStreamActivity.this);
        rtmpCamera.disableVideo();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startStream();
            }
        }, 500);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopStream();
    }

    private void startStream() {

        if (!rtmpCamera.isStreaming()) {
            if (rtmpCamera.isRecording()|| rtmpCamera.prepareAudio()) {
                rtmpCamera.startStream(streamUrl);
                stopButton.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                stopButton.setTextColor(getResources().getColor(R.color.white));
                stopButton.setText("Stop");
            } else {
                Toast.makeText(QiscusStreamActivity.this, "Could not start RTMP stream.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void stopStream() {
        if (rtmpCamera.isStreaming()) {
            rtmpCamera.stopStream();
            rtmpCamera.stopPreview();
        }

        toggleStart = false;
    }

    public void restartStream() {
        if (!rtmpCamera.isStreaming()) {
            if (rtmpCamera.prepareAudio()) {
                rtmpCamera.startStream(streamUrl);
            } else {
                Toast.makeText(QiscusStreamActivity.this, "Could not start RTMP stream.", Toast.LENGTH_SHORT).show();
            }
        }

        toggleStart = true;
    }

    @Override
    public void onConnectionSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stopButton.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                stopButton.setTextColor(getResources().getColor(R.color.white));
                stopButton.setText("Stop");
            }
        });
    }

    @Override
    public void onConnectionFailedRtmp(String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(QiscusStreamActivity.this, "Could not connect to RTMP endpoint. Make sure you have internet connection or valid RTMP url.", Toast.LENGTH_SHORT).show();
                rtmpCamera.stopStream();
                rtmpCamera.stopPreview();
            }
        });
    }

    @Override
    public void onDisconnectRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stopButton.setBackground(getResources().getDrawable(R.drawable.round_button_white));
                stopButton.setTextColor(getResources().getColor(R.color.black));
                stopButton.setText("Start");
            }
        });
    }

    @Override
    public void onAuthErrorRtmp() {
        //
    }

    @Override
    public void onAuthSuccessRtmp() {
        //
    }

    @Override
    public void onClick(View v) {
        if (toggleStart) {
            stopButton.setBackground(getResources().getDrawable(R.drawable.round_button_white));
            stopButton.setTextColor(getResources().getColor(R.color.black));
            stopButton.setText("Start");
            stopStream();
        } else {
            stopButton.setBackground(getResources().getDrawable(R.drawable.round_button_red));
            stopButton.setTextColor(getResources().getColor(R.color.white));
            stopButton.setText("Stop");
            restartStream();
        }
    }
}
