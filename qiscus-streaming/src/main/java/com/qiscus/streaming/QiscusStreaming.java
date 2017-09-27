package com.qiscus.streaming;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.qiscus.streaming.data.QiscusStream;
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
    }

    public static void createStream(String title, final CreateStreamListener listener) {
        final QiscusStream stream = new QiscusStream();
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
        private static int width = 320;
        private static int height = 240;
        private static int fps = 25;
        private static int bitrate = 350 * 1024;

        private StreamActivityBuilder(String streamUrl) {
            this.streamUrl = streamUrl;
        }

        @Override
        public RequiredVideoQuality setVideoQuality(VideoQuality quality) {
            if (quality == VideoQuality.QVGA) {
                width = 320;
                height = 240;
                fps = 15;
                bitrate = 300 * 1024;
            } else if (quality == VideoQuality.LD) {
                width = 480;
                height = 360;
                fps = 20;
                bitrate = 500 * 1024;
            } else if (quality == VideoQuality.SD) {
                width = 640;
                height = 480;
                fps = 24;
                bitrate = 800 * 1024;
            } else if (quality == VideoQuality.HD) {
                width = 1280;
                height = 720;
                fps = 30;
                bitrate = 1800 * 1024;
            }

            return this;
        }

        @Override
        public QiscusStreaming start(Context context) {
            return null;
        }
    }
}
