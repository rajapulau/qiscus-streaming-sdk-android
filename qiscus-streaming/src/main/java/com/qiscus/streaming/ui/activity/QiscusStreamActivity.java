package com.qiscus.streaming.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qiscus.streaming.R;
import com.qiscus.streaming.data.QiscusStreamParameter;
import com.qiscus.streaming.ui.fragment.CameraResolutionsFragment;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.antmedia.android.broadcaster.ILiveVideoBroadcaster;
import io.antmedia.android.broadcaster.LiveVideoBroadcaster;
import io.antmedia.android.broadcaster.utils.Resolution;


public class QiscusStreamActivity extends AppCompatActivity {
    private static final String TAG = QiscusStreamActivity.class.getSimpleName();

    private ILiveVideoBroadcaster liveVideoBroadcaster;
    private Intent liveVideoBroadcasterServiceIntent;
    private CameraResolutionsFragment mCameraResolutionsDialog;
    private ViewGroup rootView;
    private GLSurfaceView glView;
    private Button broadcastControlButton;
    private ImageButton settingsButton;
    private TextView streamLiveStatus;
    private TimerHandler timerHandler;
    private Timer timer;
    private String streamUrl;
    private long elapsedTime;
    private boolean isRecording = false;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            LiveVideoBroadcaster.LocalBinder binder = (LiveVideoBroadcaster.LocalBinder) service;

            if (liveVideoBroadcaster == null) {
                liveVideoBroadcaster = binder.getService();
                liveVideoBroadcaster.init(QiscusStreamActivity.this, glView);
                liveVideoBroadcaster.setAdaptiveStreaming(true);
            }

            liveVideoBroadcaster.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            liveVideoBroadcaster = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        streamUrl = extras.getString("STREAM_URL");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        liveVideoBroadcasterServiceIntent = new Intent(this, LiveVideoBroadcaster.class);
        startService(liveVideoBroadcasterServiceIntent);

        setContentView(R.layout.activity_qiscus_stream);

        timerHandler = new TimerHandler();
        rootView = (ViewGroup) findViewById(R.id.root_layout);
        settingsButton = (ImageButton) findViewById(R.id.settings_button);
        streamLiveStatus = (TextView) findViewById(R.id.stream_live_status);
        broadcastControlButton = (Button) findViewById(R.id.toggle_broadcasting);
        glView = (GLSurfaceView) findViewById(R.id.cameraPreview_surfaceView);

        if (glView != null) {
            glView.setEGLContextClientVersion(2);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(liveVideoBroadcasterServiceIntent, mConnection, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mCameraResolutionsDialog != null && mCameraResolutionsDialog.isVisible()) {
            mCameraResolutionsDialog.dismiss();
        }

        liveVideoBroadcaster.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LiveVideoBroadcaster.PERMISSIONS_REQUEST: {
                if (liveVideoBroadcaster.isPermissionGranted()) {
                    liveVideoBroadcaster.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CAMERA) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                        liveVideoBroadcaster.requestPermission();
                    } else {
                        new AlertDialog.Builder(QiscusStreamActivity.this)
                                .setTitle(R.string.permission)
                                .setMessage(getString(R.string.app_doesnot_work_without_permissions))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                            startActivity(intent);
                                        } catch (ActivityNotFoundException e) {
                                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                            startActivity(intent);
                                        }
                                    }
                                })
                                .show();
                    }
                }

                return;
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            liveVideoBroadcaster.setDisplayOrientation();
        }
    }

    public void showSetResolutionDialog(View v) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragmentDialog = getSupportFragmentManager().findFragmentByTag("dialog");

        if (fragmentDialog != null) {
            ft.remove(fragmentDialog);
        }

        ArrayList<Resolution> sizeList = liveVideoBroadcaster.getPreviewSizeList();

        if (sizeList != null && sizeList.size() > 0) {
            mCameraResolutionsDialog = new CameraResolutionsFragment();
            mCameraResolutionsDialog.setCameraResolutions(sizeList, liveVideoBroadcaster.getPreviewSize());
            mCameraResolutionsDialog.show(ft, "resolutiton_dialog");
        } else {
            Snackbar.make(rootView, "No resolution available.", Snackbar.LENGTH_LONG).show();
        }
    }

    public void changeCamera(View v) {
        if (liveVideoBroadcaster != null) {
            liveVideoBroadcaster.changeCamera();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void toggleBroadcasting(View v) {
        if (!isRecording) {
            if (liveVideoBroadcaster != null) {
                if (!liveVideoBroadcaster.isConnected()) {
                    new AsyncTask<String, String, Boolean>() {
                        ContentLoadingProgressBar progressBar;

                        @Override
                        protected void onPreExecute() {
                            progressBar = new ContentLoadingProgressBar(QiscusStreamActivity.this);
                            progressBar.show();
                        }

                        @Override
                        protected Boolean doInBackground(String... url) {
                            return liveVideoBroadcaster.startBroadcasting(url[0]);

                        }

                        @Override
                        protected void onPostExecute(Boolean result) {
                            progressBar.hide();
                            isRecording = result;

                            if (result) {
                                streamLiveStatus.setVisibility(View.VISIBLE);
                                broadcastControlButton.setText("Stop");
                                broadcastControlButton.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                                broadcastControlButton.setTextColor(getResources().getColor(R.color.white));
                                settingsButton.setVisibility(View.GONE);
                                startTimer();
                            } else {
                                Snackbar.make(rootView, "Failed to start. Please check server url and security credentials. ", Snackbar.LENGTH_LONG).show();
                                triggerStopRecording();
                            }
                        }
                    }.execute(streamUrl);
                } else {
                    Snackbar.make(rootView, "Your previous broadcast still sends packets due to slow internet speed.", Snackbar.LENGTH_LONG).show();
                }
            } else {
                Snackbar.make(rootView, "Unknown error.", Snackbar.LENGTH_LONG).show();
            }
        } else {
            triggerStopRecording();
        }
    }

    public void triggerStopRecording() {
        if (isRecording) {
            broadcastControlButton.setText("Start");
            broadcastControlButton.setBackground(getResources().getDrawable(R.drawable.round_button_white));
            broadcastControlButton.setTextColor(getResources().getColor(R.color.black));
            streamLiveStatus.setVisibility(View.GONE);
            streamLiveStatus.setText("Offline");
            streamLiveStatus.setVisibility(View.VISIBLE);
            liveVideoBroadcaster.stopBroadcasting();
            stopTimer();
        }

        isRecording = false;
    }

    public void startTimer() {
        if (timer == null) {
            timer = new Timer();
        }

        elapsedTime = 0;
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                elapsedTime += 1; //increase every sec
                timerHandler.obtainMessage(TimerHandler.INCREASE_TIMER).sendToTarget();

                if (liveVideoBroadcaster == null || !liveVideoBroadcaster.isConnected()) {
                    timerHandler.obtainMessage(TimerHandler.CONNECTION_LOST).sendToTarget();
                }
            }
        }, 0, 1000);
    }

    public void stopTimer() {
        if (timer != null) {
            this.timer.cancel();
        }
        this.timer = null;
        this.elapsedTime = 0;
    }

    public void setResolution(Resolution size) {
        liveVideoBroadcaster.setResolution(size);
    }

    private class TimerHandler extends Handler {
        static final int CONNECTION_LOST = 2;
        static final int INCREASE_TIMER = 1;

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INCREASE_TIMER:
                    streamLiveStatus.setText("Live - " + getDurationString((int) elapsedTime));
                    break;
                case CONNECTION_LOST:
                    triggerStopRecording();

                    try {
                        new AlertDialog.Builder(QiscusStreamActivity.this)
                                .setMessage("Connection to RTMP server is lost.")
                                .setPositiveButton(android.R.string.yes, null)
                                .show();
                    } catch (Exception e) {
                        //
                    }

                    break;
            }
        }
    }

    public static String getDurationString(int seconds) {
        if (seconds < 0 || seconds > 2000000) {
            seconds = 0;
        }

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        if (hours == 0) {
            return twoDigitString(minutes) + " : " + twoDigitString(seconds);
        } else {
            return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
        }
    }

    public static String twoDigitString(int number) {
        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }
}
