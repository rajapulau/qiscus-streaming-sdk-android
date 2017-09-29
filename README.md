# Qiscus Streaming SDK Android

Qiscus Streaming SDK is a product that makes adding voice calling and/or instant messaging to mobile apps easy. It handles all the complexity of signaling and audio management while providing you the freedom to create a stunning user interface.


# Quick Start

### FIRST TIME SETUP

Below is a step-by-step guide on setting up the Qiscus Streaming SDK for the first time

### ADD QISCUS STREAMING SDK LIBRARY

Add to your project build.gradle

```groovy
dependencies {
    compile project(path: ':qiscus-streaming')
}
```

# Authentication

### Init with APP ID

Init Qiscus at your application class with your application ID

```java
public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        QiscusStreaming.init(this, "elR1SnZGVElndUpDV2gxcDBuRFFhcGFmc3l0bFdSbENmOTlLQ1ZDTA==");
    }
}
```

Before user can start streaming each other, they must create link streaming

```java
QiscusStreaming.createStream("Stream " + (System.currentTimeMillis() / 1000L), new CreateStreamListener() {
    @Override
    public void onCreateStreamSuccess(final QiscusStream stream) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtRtmpUrl.setText(stream.streamUrl);
            }
        });
    }

    @Override
    public void onCreateStreamError(final String error) {
        Log.e(TAG, "Create stream error: " + error);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BasicStreamActivity.this, "Create stream error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
});
```

### START STREAM

Start video streaming from android

```java
QiscusStreaming.buildStream(stream.streamUrl)
                        .setVideoQuality(VideoQuality.QVGA)
                        .start(getContext());
```

### IMPLEMENTATION

Start video streaming from android

```java
QiscusStreaming.createStream("Stream " + (System.currentTimeMillis() / 1000L), new CreateStreamListener() {
    @Override
    public void onCreateStreamSuccess(final QiscusStream stream) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                QiscusStreaming.buildStream(stream.streamUrl)
                                        .setVideoQuality(VideoQuality.QVGA)
                                        .start(getContext());
            }
        });
    }

    @Override
    public void onCreateStreamError(final String error) {
        Log.e(TAG, "Create stream error: " + error);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BasicStreamActivity.this, "Create stream error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
});
```