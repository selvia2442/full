package com.selvia.quiz20;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.selvia.quiz20.Adpt.ItemUserDetail;
import com.selvia.quiz20.game.MainActivity;
import com.selvia.quiz20.service.Global;
import com.sh.net.DataContainer;
import com.sh.net.SHJSONClient;
import com.sh.net.SHWebClient;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.*;
import com.twitter.sdk.android.core.identity.*;
import com.twitter.sdk.android.core.models.User;

import java.lang.ref.WeakReference;


public class IntroActivity extends AppCompatActivity {
    public static final int RET_ERROR = 0;
    public static final int RET_USER = 1;

    public static ResultHandler resultHandler;
    ProgressDialog loadingdialog = null;
    DataContainer contUser;

    private TwitterLoginButton twitterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        resultHandler = new ResultHandler(this);
        setUpViews();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("twitterButton","onActivityResult");
        twitterButton.onActivityResult(requestCode, resultCode, data);
    }

    private void setUpViews() {
        try {
            setUpTwitterButton();
        }catch (Exception e) {
            Log.e("setUpViews","Exception");
            Toast.makeText(getApplicationContext(),
                    "트위터가 존재하지 않습니다. 트위터를 설치하고 시도해주세요.",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setUpTwitterButton() {
        twitterButton = (TwitterLoginButton) findViewById(R.id.twitter_button);
        twitterButton.setCallback(new Callback() {
            @Override
            public void success(Result result) {
                Log.e("twitterButton","onActivityResult");
                setUpViewsForTweetComposer();
            }

            @Override
            public void failure(TwitterException exception) {
                Log.e("twitterButton","onActivityResult");
                finish();
                /*Toast.makeText(getApplicationContext(),
                        "트위터 접속에 실패하였습니다.",
                        Toast.LENGTH_SHORT).show();*/
            }
        });


    }

    private void setUpViewsForTweetComposer() {
        //TweetComposer.Builder builder = new TweetComposer.Builder(this)
        //        .text("Just setting up my Fabric!");
        //builder.show();

        TwitterSession session =
                Twitter.getSessionManager().getActiveSession();
        Twitter.getApiClient(session).getAccountService()
                .verifyCredentials(true, false, new Callback<User>() {


                    @Override
                    public void success(Result<User> userResult) {

                        User user = userResult.data;
                        Global.user = new ItemUserDetail(user);
                        set_user_data();
                    }

                    @Override
                    public void failure(TwitterException e) {

                    }

                });
    }


    private void set_user_data() {
        loadingdialog = Global.getProgDlg(this);
        loadingdialog.show();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                try {
                    SHJSONClient client = new SHJSONClient();
                    client.setURL(Global.url_longin_in_db);
                    client.setMethod(SHWebClient.Method.POST);
                    client.addParam("user_id", Global.user.user_id);
                    client.addParam("user_name", Global.user.user_name);
                    client.addParam("profileImgUrl", Global.user.profileImgUrl);
                    contUser = client.Request();
                    if (contUser.isError()) {
                        msg.what = RET_ERROR;
                        msg.obj = Global.getErrorMessage(contUser);
                    } else {
                        msg.what = RET_USER;
                    }
                } catch (Exception e) {
                    msg.what = RET_ERROR;
                    msg.obj = e.getLocalizedMessage() + "";
                }
                resultHandler.sendMessage(msg);
            }
        });
        t.start();
    }


    public void set_user_data_complete() {
        String win_count = contUser.getMapItem( "win_count" ).getValue().toString();
        Global.user.win_count = Integer.parseInt(win_count);

        String lose_count = contUser.getMapItem( "lose_count" ).getValue().toString();
        Global.user.lose_count = Integer.parseInt(lose_count);

        Log.e("set_user_data_complete","sendUDP");

        Intent itt = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(itt);
        finish();
    }

    public static class ResultHandler extends Handler {
        WeakReference<IntroActivity> mAcvMain;

        public ResultHandler(IntroActivity mAcvMainRef) {
            mAcvMain = new WeakReference<>(mAcvMainRef);
        }

        public void handleMessage(Message msg) {
            IntroActivity localAcv = mAcvMain.get();
            if (localAcv == null) return;
            switch (msg.what) {
                case RET_ERROR:
                    if (localAcv.loadingdialog != null) {
                        localAcv.loadingdialog.dismiss();
                        localAcv.loadingdialog = null;
                    }
                    Toast.makeText( localAcv, (String) msg.obj, Toast.LENGTH_SHORT ).show();
                    break;

                case RET_USER:
                    if (localAcv.loadingdialog != null) {
                        localAcv.loadingdialog.dismiss();
                        localAcv.loadingdialog = null;
                    }
                    localAcv.set_user_data_complete();
                    break;
            }
        }
    }
}
