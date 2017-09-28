package com.qiscus.streaming.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceView;

import com.pedro.rtplibrary.rtmp.RtmpCamera1;
import com.qiscus.streaming.R;
import com.qiscus.streaming.data.QiscusStreamParameter;
import com.qiscus.streaming.ui.fragment.QiscusStreamFragment;

import net.ossrs.rtmp.ConnectCheckerRtmp;

public class QiscusStreamActivity extends BaseActivity implements ConnectCheckerRtmp, QiscusStreamFragment.StreamListener {
    private static final String TAG = QiscusStreamActivity.class.getSimpleName();

    private String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private QiscusStreamParameter streamParameter;
    private QiscusStreamFragment streamFragment;
    private RtmpCamera1 rtmpCamera;
    private SurfaceView surfaceView;

    public static Intent generateIntent(Context context, QiscusStreamParameter parameter) {
        Intent intent = new Intent(context, QiscusStreamActivity.class);
        intent.putExtra("STREAM_PARAMETER", parameter);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseIntentData();
        requestPermission(permissions);
        initView();
        setAlwaysOn();
        setFullscreen();
    }

    @Override
    public int getLayout() {
        return R.layout.activity_qiscus_stream;
    }

    @Override
    protected void onPermissionGranted() {
        streamFragment = QiscusStreamFragment.newInstance(streamParameter);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.stream_fragment_container, streamFragment).commit();
    }

    private void parseIntentData() {
        streamParameter = getIntent().getParcelableExtra("STREAM_PARAMETER");
    }

    private void initView() {
        surfaceView = (SurfaceView) findViewById(R.id.cameraPreview);
        rtmpCamera = new RtmpCamera1(surfaceView, this);
    }

    @Override
    public void onConnectionSuccessRtmp() {

    }

    @Override
    public void onConnectionFailedRtmp() {

    }

    @Override
    public void onDisconnectRtmp() {

    }

    @Override
    public void onAuthErrorRtmp() {

    }

    @Override
    public void onAuthSuccessRtmp() {

    }

    @Override
    public void onStartStream() {

    }

    @Override
    public void onStopStream() {

    }
}
