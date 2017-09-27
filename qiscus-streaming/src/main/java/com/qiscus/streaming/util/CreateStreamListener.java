package com.qiscus.streaming.util;

import com.qiscus.streaming.data.QiscusStream;

/**
 * Created by fitra on 27/09/17.
 */

public interface CreateStreamListener {
    void onCreateStreamSuccess(QiscusStream stream);
    void onCreateStreamError(String error);
}
