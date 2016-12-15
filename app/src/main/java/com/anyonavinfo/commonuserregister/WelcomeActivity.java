package com.anyonavinfo.commonuserregister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class WelcomeActivity extends Activity {
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        intent = new Intent();
        intent.setClass(WelcomeActivity.this, MainActivity.class);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
            }
        }).start();

    }

}
