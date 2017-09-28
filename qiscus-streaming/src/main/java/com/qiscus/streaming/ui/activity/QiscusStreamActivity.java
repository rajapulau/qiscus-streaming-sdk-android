package com.qiscus.streaming.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pedro.rtplibrary.rtmp.RtmpCamera1;
import com.qiscus.streaming.R;
import com.qiscus.streaming.data.QiscusStreamParameter;
import com.qiscus.streaming.ui.fragment.QiscusStreamFragment;

import net.ossrs.rtmp.ConnectCheckerRtmp;

public class QiscusStreamActivity extends AppCompatActivity implements ConnectCheckerRtmp, View.OnClickListener {
    private static final String TAG = QiscusStreamActivity.class.getSimpleName();

    private String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static String streamUrl;
    private QiscusStreamParameter streamParameter;
    private QiscusStreamFragment streamFragment;
    private RtmpCamera1 rtmpCamera;
    private SurfaceView surfaceView;
    private Button stopButton;
    private boolean toggleStart;

    public static Intent generateIntent(Context context, String url, QiscusStreamParameter parameter) {
        Intent intent = new Intent(context, QiscusStreamActivity.class);
        intent.putExtra("STREAM_PARAMETER", parameter);
        streamUrl = url;
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qiscus_stream);

        toggleStart = true;
        surfaceView = (SurfaceView) findViewById(R.id.cameraPreview);
        stopButton = (Button) findViewById(R.id.buttonStop);
        stopButton.setOnClickListener(this);
        rtmpCamera = new RtmpCamera1(surfaceView, QiscusStreamActivity.this);

        parseIntentData();
        startStream();
    }

    private void parseIntentData() {
        streamParameter = getIntent().getParcelableExtra("STREAM_PARAMETER");
    }

    private void startStream() {
        if (!rtmpCamera.isStreaming()) {
            if (rtmpCamera.prepareAudio() && rtmpCamera.prepareVideo()) {
                rtmpCamera.startStream(streamUrl);
                stopButton.setBackgroundColor(getResources().getColor(R.color.red));
                stopButton.setTextColor(getResources().getColor(R.color.white));
            } else {
                Toast.makeText(QiscusStreamActivity.this, "Could not start RTMP stream.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void stopStream() {
        if (rtmpCamera.isStreaming()) {
            rtmpCamera.stopStream();
        }
        toggleStart = false;
    }

    public void restartStream() {
        if (!rtmpCamera.isStreaming()) {
            if (rtmpCamera.prepareAudio() && rtmpCamera.prepareVideo()) {
                rtmpCamera.startStream(streamUrl);
            } else {
                Toast.makeText(QiscusStreamActivity.this, "Could not start RTMP stream.", Toast.LENGTH_SHORT).show();
            }
        }
        toggleStart = true;
    }

    @Override
    public void onConnectionSuccessRtmp() {
        stopButton.setBackgroundColor(getResources().getColor(R.color.green));
        stopButton.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    public void onConnectionFailedRtmp() {
        Toast.makeText(QiscusStreamActivity.this, "Could not connect to RTMP endpoint. Make sure you have internet connection or valid RTMP url.", Toast.LENGTH_SHORT).show();
        stopButton.setBackgroundColor(getResources().getColor(R.color.white));
        stopButton.setTextColor(getResources().getColor(R.color.black));
    }

    @Override
    public void onDisconnectRtmp() {
        stopButton.setBackgroundColor(getResources().getColor(R.color.white));
        stopButton.setTextColor(getResources().getColor(R.color.black));
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
            stopButton.setBackgroundColor(getResources().getColor(R.color.white));
            stopButton.setTextColor(getResources().getColor(R.color.black));
            stopStream();
        } else {
            stopButton.setBackgroundColor(getResources().getColor(R.color.red));
            stopButton.setTextColor(getResources().getColor(R.color.white));
            restartStream();
        }
    }
}
