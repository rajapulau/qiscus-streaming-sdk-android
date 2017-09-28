package com.qiscus.streaming;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.qiscus.streaming.data.QiscusStream;
import com.qiscus.streaming.data.QiscusStreamParameter;
import com.qiscus.streaming.data.VideoQuality;
import com.qiscus.streaming.util.AsyncHttpUrlConnection;
import com.qiscus.streaming.util.CreateStreamListener;

/**
 * Created by fitra on 27/09/17.
 */

public class QiscusStreaming {
    private static final String TAG = QiscusStreaming.class.getSimpleName();

    private static Application application;
    private static volatile Context context;
    private static AsyncHttpUrlConnection httpConnection;
    private static QiscusStream stream;
    private static String apiKey;

    public static Application getAppInstance() {
        return application;
    }

    public static Context getAppContext() {
        return context;
    }

    public static void init(Application instance, String api_key) {
        application = instance;
        context = application.getApplicationContext();
        apiKey = api_key;
        stream = new QiscusStream();
    }

    public static void createStream(String title, final CreateStreamListener listener) {
        httpConnection = new AsyncHttpUrlConnection("POST", "/stream/create", "{\"title\": \"" + title + "\"}", new AsyncHttpUrlConnection.AsyncHttpEvents() {
            @Override
            public void onHttpError(String errorMessage) {
                Log.e(TAG, "API connection error: " + errorMessage);
                listener.onCreateStreamError(errorMessage);
            }

            @Override
            public void onHttpComplete(String response) {
                Log.d(TAG, "API connection success: " + response);
                listener.onCreateStreamSuccess(stream);
            }
        });
        httpConnection.send();
    }

    public static RequiredStreamUrl buildStream(String streamUrl) {
        return new StreamActivityBuilder(streamUrl);
    }

    public interface RequiredStreamUrl {
        RequiredVideoQuality setVideoQuality(VideoQuality quality);
    }

    public interface RequiredVideoQuality {
        QiscusStreaming start(Context context);
    }

    public static class StreamActivityBuilder extends QiscusStreaming implements RequiredStreamUrl, RequiredVideoQuality {
        private String streamUrl;
        private static QiscusStreamParameter streamParameter;

        private StreamActivityBuilder(String streamUrl) {
            this.streamUrl = streamUrl;
        }

        @Override
        public RequiredVideoQuality setVideoQuality(VideoQuality quality) {
            streamParameter =  new QiscusStreamParameter();

            if (quality == VideoQuality.QVGA) {
                streamParameter.videoWidth = 320;
                streamParameter.videoHeight = 240;
                streamParameter.videoFps = 15;
                streamParameter.videoBitrate = 300 * 1024;
            } else if (quality == VideoQuality.LD) {
                streamParameter.videoWidth = 480;
                streamParameter.videoHeight = 360;
                streamParameter.videoFps = 20;
                streamParameter.videoBitrate = 500 * 1024;
            } else if (quality == VideoQuality.SD) {
                streamParameter.videoWidth = 640;
                streamParameter.videoHeight = 480;
                streamParameter.videoFps = 24;
                streamParameter.videoBitrate = 800 * 1024;
            } else if (quality == VideoQuality.HD) {
                streamParameter.videoWidth = 1280;
                streamParameter.videoHeight = 720;
                streamParameter.videoFps = 30;
                streamParameter.videoBitrate = 1800 * 1024;
            }

            return this;
        }

        @Override
        public QiscusStreaming start(Context context) {
            Intent intent = new Intent(QiscusCallActivity.generateIntent(context, streamUrl, streamParameter));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
