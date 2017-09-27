package com.qiscus.streaming.sample;

import android.app.Application;

import com.qiscus.streaming.QiscusStreaming;

/**
 * Created by fitra on 27/09/17.
 */

public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        QiscusStreaming.init(this, "elR1SnZGVElndUpDV2gxcDBuRFFhcGFmc3l0bFdSbENmOTlLQ1ZDTA==");
    }
}
