package com.selvia.quiz20.game;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.selvia.quiz20.Adpt.ItemUserDetail;
import com.selvia.quiz20.service.Global;
import com.selvia.quiz20.R;
import com.selvia.quiz20.service.MyTokenizer;
import com.selvia.quiz20.service.UDPRun;
import com.sh.net.DataContainer;
import com.sh.net.SHJSONClient;
import com.sh.net.SHWebClient;

import java.lang.ref.WeakReference;

public class ReadyGameActivity extends AppCompatActivity {
    public static final int RET_ERROR = 0;
    public static final int RET_ENTERED = 1;
    public static final int RET_MAKEANSWER = 2;
    public static final int RET_START_Q = 3;
    public static final int RET_START_A = 4;
    public static final int RET_SWITCH = 5;
    public static final int RET_QUIT = 6;
    public static final int RET_QUIT_P = 7;

    public static ResultHandler resultHandler;
    ProgressDialog loadingdialog = null;
    DataContainer contAnswer, contQuit;

    public boolean master_flag = false;
    public boolean tagger_flag = false;
    int roomNo;
    String strRoomName, strAnswer;
    Button btnStart, btnSwitch;

    //master
    ItemUserDetail otherPlayer;
    ImageView imageView_p2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready_game);

        Bundle b = getIntent().getExtras();
        master_flag = b.getBoolean("master");
        tagger_flag = master_flag;
        roomNo = b.getInt("roomNo");
        strRoomName = b.getString("roomName");

        if(!master_flag) {
            String master_id = b.getString("user_id");
            String master_name = b.getString("user_name");
            String master_img = b.getString("user_img");
            otherPlayer = new ItemUserDetail(master_id, master_name, master_img);
        }

        resultHandler = new ResultHandler(this);
        InitView();
    }


    private void InitView()
    {
        Button btnGiveLike = (Button) findViewById( R.id.btnGiveLike );
        btnGiveLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //알람
            }
        });

        Button btnAddFriend = (Button) findViewById( R.id.btnAddFriend );
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //알람
            }
        });

        btnStart = (Button) findViewById( R.id.btnStart );
        btnStart.setVisibility(View.GONE);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tagger_flag)
                    createDlgGetAnwser().show();
                else
                    UDPRun.SendtoServer(otherPlayer.user_id, "READY", "0", "");
            }
        });

        btnSwitch = (Button) findViewById( R.id.btnSwitch );
        btnSwitch.setVisibility(View.GONE);
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagger_flag = false;
                UDPRun.SendtoServer(otherPlayer.user_id, "READY", "2", "");
            }
        });

        ImageView imageView_p1 = (ImageView) findViewById( R.id.imageView_p1 );
        imageView_p2 = (ImageView) findViewById( R.id.imageView_p2 );

        AQuery aq = new AQuery( this );
        aq.id( imageView_p1 ).image( Global.user.profileImgUrl );

        if(!master_flag) {
            aq.id( imageView_p2 ).image( otherPlayer.profileImgUrl );
            btnStart.setVisibility(View.INVISIBLE);
        } else {
            btnStart.setVisibility(View.VISIBLE);
        }

        set_tagger();
    }

    public void set_tagger()
    {/*
        if(tagger_flag) {
            btnSwitch.setVisibility(View.VISIBLE);
        } else {
            btnSwitch.setVisibility(View.INVISIBLE);
        }*/
    }


    public void other_user_entered(String data) {
        btnStart.setVisibility(View.VISIBLE);
        MyTokenizer mt = new MyTokenizer(data, ",");
        otherPlayer = new ItemUserDetail(mt.nextToken(), mt.nextToken(), mt.nextToken());

        AQuery aq = new AQuery( this );
        aq.id( imageView_p2 ).image( otherPlayer.profileImgUrl );
    }

    private AlertDialog createDlgGetAnwser() {
        final AlertDialog dial = new AlertDialog.Builder(this).create();
        dial.setMessage("사진을 보고 사물의 이름을 하나 입력해주세요.");
        View v = this.getLayoutInflater().inflate(R.layout.dlg_answer, null);
        final EditText editAnswer = (EditText) v.findViewById(R.id.editAnswer);

        dial.setButton(DialogInterface.BUTTON_NEGATIVE, "취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dial.cancel();
            }
        });
        dial.setButton(DialogInterface.BUTTON_POSITIVE, "확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                strAnswer = editAnswer.getText().toString();
                game_start(strAnswer);
            }
        });
        return dial;
    }

    private void game_start(final String answer) {
        loadingdialog = Global.getProgDlg(this);
        loadingdialog.show();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                try {
                    SHJSONClient client = new SHJSONClient();
                    client.setURL(Global.url_start_game);
                    client.setMethod(SHWebClient.Method.POST);
                    client.addParam("room_no", roomNo+"");
                    client.addParam("answer", answer);
                    if(tagger_flag) {
                        client.addParam("player_q", Global.user.user_id);
                        client.addParam("player_a", otherPlayer.user_id);
                    } else {
                        client.addParam("player_a", Global.user.user_id);
                        client.addParam("player_q", otherPlayer.user_id);
                    }
                    contAnswer = client.Request();
                    if (contAnswer.isError()) {
                        msg.what = RET_ERROR;
                        msg.obj = Global.getErrorMessage(contAnswer);
                    } else {
                        msg.what = RET_START_A;
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

    public void tagger_started() {
        String strGameIdx = contAnswer.getMapItem( "game_idx" ).getValue().toString();
        UDPRun.SendtoServer(otherPlayer.user_id, "READY", "1", strGameIdx+","+strAnswer);

        Bundle bb = new Bundle();
        bb.putBoolean("tagger_flag", tagger_flag);
        bb.putInt("game_idx", Integer.parseInt(strGameIdx));
        bb.putString("Answer", strAnswer);
        bb.putString("other_id", otherPlayer.user_id);
        bb.putString("other_name", otherPlayer.user_name);
        bb.putString("other_img", otherPlayer.profileImgUrl);

        Intent itt = new Intent(getApplicationContext(), StartGameActivity.class);
        itt.putExtras(bb);
        startActivityForResult(itt, 1);
    }


    public void q_started(String data) {
        MyTokenizer mt = new MyTokenizer(data, ",");
        String strGameIdx = mt.nextToken();
        String Answer = mt.nextToken();

        Bundle bb = new Bundle();
        bb.putBoolean("tagger_flag", tagger_flag);
        bb.putInt("game_idx", Integer.parseInt(strGameIdx));
        bb.putString("Answer", Answer);
        bb.putString("other_id", otherPlayer.user_id);
        bb.putString("other_name", otherPlayer.user_name);
        bb.putString("other_img", otherPlayer.profileImgUrl);

        Intent itt = new Intent(getApplicationContext(), StartGameActivity.class);
        itt.putExtras(bb);
        startActivityForResult(itt, 1);
    }

    public void switch_tagger() {
        set_tagger();
    }


    public static class ResultHandler extends Handler {
        WeakReference<ReadyGameActivity> mAcvMain;

        public ResultHandler(ReadyGameActivity mAcvMainRef) {
            mAcvMain = new WeakReference<>(mAcvMainRef);
        }

        public void handleMessage(Message msg) {
            ReadyGameActivity localAcv = mAcvMain.get();
            if (localAcv == null) return;
            switch (msg.what) {
                case RET_ERROR:
                    Toast.makeText( localAcv, (String) msg.obj, Toast.LENGTH_SHORT ).show();
                    break;

                case RET_ENTERED:
                    localAcv.other_user_entered((String) msg.obj);
                    break;

                case RET_START_A:
                    if (localAcv.loadingdialog != null) {
                        localAcv.loadingdialog.dismiss();
                        localAcv.loadingdialog = null;
                    }
                    localAcv.tagger_started();
                    break;

                case RET_START_Q:
                    localAcv.q_started((String) msg.obj);
                    break;

                case RET_MAKEANSWER:
                    localAcv.createDlgGetAnwser().show();

                case RET_SWITCH:
                    localAcv.switch_tagger();
                    break;

                case RET_QUIT:
                    localAcv.quit_room_complete();
                    break;

                case RET_QUIT_P:
                    localAcv.quit_room();
                    break;
            }
        }
    }


    @Override
    public void onBackPressed() {
        createDlgQuit().show();
    }

    private AlertDialog createDlgQuit() {
        final AlertDialog dial = new AlertDialog.Builder(this).create();
        dial.setCancelable(false);
        dial.setTitle("방을 나가시겠습니까?");

        dial.setButton(DialogInterface.BUTTON_NEGATIVE, "취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dial.cancel();
            }
        });
        dial.setButton(DialogInterface.BUTTON_POSITIVE, "확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                quit_room();
            }
        });
        return dial;
    }

    private void quit_room() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                try {
                    SHJSONClient client = new SHJSONClient();
                    client.setURL(Global.url_room_quit);
                    client.setMethod(SHWebClient.Method.POST);
                    client.addParam("user_id", Global.user.user_id);
                    client.addParam("room_no", roomNo+"");
                    if(master_flag) {
                        UDPRun.SendtoServer(otherPlayer.user_id, "ROOM", "1", "");
                        client.addParam("master_flag", "y");
                    } else {
                        client.addParam("master_flag", "n");
                    }
                    contQuit = client.Request();
                    if (contQuit.isError()) {
                        msg.what = RET_ERROR;
                        msg.obj = Global.getErrorMessage(contQuit);
                    } else {
                        msg.what = RET_QUIT;
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

    public void quit_room_complete() {
        finish();
    }

}
