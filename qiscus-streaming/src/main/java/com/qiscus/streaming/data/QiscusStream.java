package com.qiscus.streaming.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fitra on 27/09/17.
 */

public class QiscusStream implements Parcelable {
    public String streamName;
    public String streamToken;
    public String streamUrl;
    public String playUrl;
    public String hlsUrl;

    protected QiscusStream(Parcel in) {
        streamName = in.readString();
        streamToken = in.readString();
        streamUrl = in.readString();
        playUrl = in.readString();
        hlsUrl = in.readString();
    }

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

    public static final Creator<QiscusStream> CREATOR = new Creator<QiscusStream>() {
        @Override
        public QiscusStream createFromParcel(Parcel in) {
            return new QiscusStream(in);
        }

        @Override
        public QiscusStream[] newArray(int size) {
            return new QiscusStream[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(streamName);
        dest.writeString(streamToken);
        dest.writeString(streamUrl);
        dest.writeString(playUrl);
        dest.writeString(hlsUrl);
    }
}
