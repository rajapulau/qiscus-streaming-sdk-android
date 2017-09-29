package com.qiscus.streaming.sample;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.qiscus.sdk.Qiscus;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Qiscus.setUser("ganjar2@ganjar.com", "12345678")
                .withUsername("Ganjar2")
                .save()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(qiscusAccount -> {
                    Log.i("MainActivity", "Login with account: " + qiscusAccount);
                }, throwable -> {
                    throwable.printStackTrace();
                    showError(throwable.getMessage());
                });

        overridePendingTransition(R.transition.slide_in, R.transition.slide_out);

        Button one = (Button) findViewById(R.id.basic);
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BasicStreamActivity.class));
            }
        });
        Button two = (Button) findViewById(R.id.integration);
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSimpleCustomChat(v);
            }
        });
    }

    public void openSimpleCustomChat(View view) {
        showLoading();
        Qiscus.buildChatRoomWith("guest2@gg.com")
                .withTitle("Guest 2")
                .build()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(qiscusChatRoom -> SimpleCustomChatActivity.generateIntent(this, qiscusChatRoom))
                .subscribe(intent -> {
                    startActivity(intent);
                    dismissLoading();
                }, throwable -> {
                    throwable.printStackTrace();
                    showError(throwable.getMessage());
                    dismissLoading();
                });
    }

    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    public void showLoading() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Please wait...");
        }
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    public void dismissLoading() {
        mProgressDialog.dismiss();
    }
}
