package com.qiscus.streaming.data;

/**
 * Created by fitra on 27/09/17.
 */

public class QiscusStream {
    private String streamName;
    private String streamToken;
    private String streamUrl;
    private String playUrl;
    private String hlsUrl;

    public void setStreamName(String name) {
        streamName = name;
    }

    public void setStreamToken(String token) {
        streamToken = token;
    }

    public void setStreamUrl(String url) {
        streamUrl = url;
    }

    public void setPlayUrl(String url) {
        playUrl = url;
    }

    public void setHlsUrl(String url) {
        hlsUrl = url;
    }

    public String getStreamName() {
        return streamName;
    }

    public String getStreamToken() {
        return streamToken;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public String getHlsUrl() {
        return hlsUrl;
    }
}
