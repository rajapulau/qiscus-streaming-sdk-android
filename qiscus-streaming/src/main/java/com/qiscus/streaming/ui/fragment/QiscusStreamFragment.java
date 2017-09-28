package com.qiscus.streaming.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.qiscus.streaming.R;
import com.qiscus.streaming.data.QiscusStreamParameter;

/**
 * Created by fitra on 28/09/17.
 */

public abstract class QiscusStreamFragment extends Fragment {
    private static final String TAG = QiscusStreamFragment.class.getSimpleName();

    private QiscusStreamParameter streamParameter;
    private StreamListener streamListener;
    private boolean toggleStart;

    @Nullable
    private Button startButton;

    public static QiscusStreamFragment newInstance(QiscusStreamParameter parameter) {
        Bundle args = new Bundle();
        args.putParcelable("STREAM_PARAMETER", parameter);
        QiscusStreamFragment fragment = new StreamingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseArgumentData();
        streamListener = (StreamListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        initView(view);
        onParentViewCreated(view);

        return view;
    }

    private void parseArgumentData() {
        streamParameter = getArguments().getParcelable("STREAM_PARAMETER");
    }

    private void initView(View view) {
        toggleStart = false;
        startButton = (Button) view.findViewById(R.id.buttonStart);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleStart = !toggleStart;

                if (toggleStart) {
                    streamListener.onStartStream();
                    startButton.setText("Stop");
                } else {
                    streamListener.onStopStream();
                    startButton.setText("Start");
                }
            }
        });
    }

    public interface StreamListener {
        void onStartStream();
        void onStopStream();
    }

    protected abstract void onParentViewCreated(View view);
    protected abstract int getLayout();

    public void showStreamingStarted() {
        startButton.setBackgroundColor(getResources().getColor(R.color.red));
        startButton.setTextColor(getResources().getColor(R.color.white));
    }

    public void showStreamingStopped() {
        startButton.setBackgroundColor(getResources().getColor(R.color.white));
        startButton.setTextColor(getResources().getColor(R.color.black));
    }
}
