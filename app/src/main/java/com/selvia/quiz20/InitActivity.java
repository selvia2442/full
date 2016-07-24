package com.selvia.quiz20;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.selvia.quiz20.service.Global;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Administrator on 2016-07-14.
 */

public class InitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            TwitterAuthConfig authConfig = new TwitterAuthConfig(Global.T_API_KEY, Global.T_API_SECRET);
            Fabric.with(this, new Twitter(authConfig));
        } catch (Exception e) {
            Log.e("InitActivity","Exception");
            Toast.makeText(getApplicationContext(),
                    "트위터가 존재하지 않습니다. 트위터를 설치하고 시도해주세요.",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        startLoginActivity();
    }

    private void startLoginActivity() {
        startActivity(new Intent(this, IntroActivity.class));
        finish();
    }
}