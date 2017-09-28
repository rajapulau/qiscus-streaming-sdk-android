package com.qiscus.streaming.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fitra on 28/09/17.
 */

public class QiscusStreamParameter implements Parcelable {
    public int videoWidth = 320;
    public int videoHeight = 240;
    public int videoFps = 25;
    public int videoBitrate = 350 * 1024;

    public QiscusStreamParameter() {
        //
    }

    protected QiscusStreamParameter(Parcel in) {
        videoWidth = in.readInt();
        videoHeight = in.readInt();
        videoFps = in.readInt();
        videoBitrate = in.readInt();
    }

    public void setVideoWidth(int width) {
        videoWidth = width;
    }

    public void setVideoHeight(int height) {
        videoHeight = height;
    }

    public void setVideoFps(int fps) {
        videoFps = fps;
    }

    public void setVideoBitrate(int bitrate) {
        videoBitrate = bitrate;
    }

    public static final Creator<QiscusStreamParameter> CREATOR = new Creator<QiscusStreamParameter>() {
        @Override
        public QiscusStreamParameter createFromParcel(Parcel in) {
            return new QiscusStreamParameter(in);
        }

        @Override
        public QiscusStreamParameter[] newArray(int size) {
            return new QiscusStreamParameter[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(videoWidth);
        dest.writeInt(videoHeight);
        dest.writeInt(videoFps);
        dest.writeInt(videoBitrate);
    }
}
