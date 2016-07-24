package com.selvia.quiz20.game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.selvia.quiz20.Adpt.AdptChat;
import com.selvia.quiz20.Adpt.ItemChat;
import com.selvia.quiz20.Adpt.ItemUserDetail;
import com.selvia.quiz20.service.Global;
import com.selvia.quiz20.R;
import com.selvia.quiz20.service.UDPRun;
import com.sh.net.DataContainer;
import com.sh.net.SHJSONClient;
import com.sh.net.SHWebClient;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class StartGameActivity extends AppCompatActivity {
    public static final int RET_ERROR = 0;
    public static final int RET_TEXT = 1;
    public static final int RET_QUIT = 2;
    public static final int RET_QUIT_OTHER = 3;
    public static final int RET_FIN = 4;

    public Context context = StartGameActivity.this;

    public static ResultHandler resultHandler;
    DataContainer contQuit;

    LinearLayout roComm, roYesno;
    ArrayList<ItemChat> arrayChat = new ArrayList<ItemChat>();
    AdptChat adptChat;

    public boolean tagger_flag; // 술래=yes/no만 가능
    EditText editContents;
    ItemUserDetail otherPlayer;

    String answer;
    int game_idx;

    TextView textCount;
    public int count_down=20;
    boolean final_text = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        Bundle b = getIntent().getExtras();
        tagger_flag = b.getBoolean("tagger_flag");
        game_idx = b.getInt("game_idx");
        answer = b.getString("Answer");

        String other_id = b.getString("other_id");
        String other_name = b.getString("other_name");
        String other_img = b.getString("other_img");
        otherPlayer = new ItemUserDetail(other_id,other_name,other_img);

        resultHandler = new ResultHandler(this);
        InitView();
    }


    private void InitView() {
        ListView listView = (ListView) findViewById(R.id.listView);
        adptChat = new AdptChat(arrayChat);
        listView.setAdapter(adptChat);

        roComm = (LinearLayout) findViewById(R.id.roComm);
        roYesno = (LinearLayout) findViewById(R.id.roYesno);

        TextView textAnswer = (TextView) findViewById(R.id.textAnswer);
        textAnswer.setText("정답 : "+answer);
        textCount = (TextView) findViewById(R.id.textCount);
        textCount.setText(count_down+"");

        Log.i("isQ",!tagger_flag+"..");
        if(!tagger_flag)
        {
            roYesno.setVisibility(View.GONE);
            roComm.setVisibility(View.VISIBLE);
            textAnswer.setVisibility(View.INVISIBLE);

            editContents = (EditText) findViewById(R.id.editContents);
            Button buttonUp = (Button) findViewById(R.id.buttonUp);
            buttonUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = editContents.getText().toString();
                    roComm.setVisibility(View.GONE);
                    UDPRun.SendtoServer(otherPlayer.user_id, "GAME", "0", text);

                    arrayChat.add(new ItemChat(context, true, text, Global.user.profileImgUrl));
                    adptChat.notifyDataSetChanged();

                    if(final_text) {
                        if(answer.equals(text)) {
                            UDPRun.SendtoServer(Global.user.user_id, "GAME", "5", "승리");
                            UDPRun.SendtoServer(otherPlayer.user_id, "GAME", "6", "패배");
                            finish_game(true);
                        } else {
                            UDPRun.SendtoServer(Global.user.user_id, "GAME", "6", "패배");
                            UDPRun.SendtoServer(otherPlayer.user_id, "GAME", "5", "승리");
                            finish_game(false);
                        }
                    }
                }
            });
        } else
        {
            roComm.setVisibility(View.GONE);
            roYesno.setVisibility(View.VISIBLE);
            textAnswer.setVisibility(View.VISIBLE);

            Button buttonYes = (Button) findViewById(R.id.buttonYes);
            buttonYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = "Yes";
                    UDPRun.SendtoServer(otherPlayer.user_id, "GAME", "0", text);

                    arrayChat.add(new ItemChat(context, true, text, Global.user.profileImgUrl));
                    adptChat.notifyDataSetChanged();

                    set_count_text();
                }
            });
            Button buttonNo = (Button) findViewById(R.id.buttonNo);
            buttonNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = "No";
                    UDPRun.SendtoServer(otherPlayer.user_id, "GAME", "0", text);

                    arrayChat.add(new ItemChat(context, true, text, Global.user.profileImgUrl));
                    adptChat.notifyDataSetChanged();

                    set_count_text();
                }
            });
        }
    }

    public void set_count_text()
    {
        if(tagger_flag) {
            count_down--;
            textCount.setText(count_down+"");

            if(count_down == 0) {
                String text = "[ 기회는 한번! 정답은? ]";
                UDPRun.SendtoServer(otherPlayer.user_id, "GAME", "0", text);

                arrayChat.add(new ItemChat(context, true, text, Global.user.profileImgUrl));
                adptChat.notifyDataSetChanged();
                roYesno.setVisibility(View.GONE);
                count_down++;
            }
        } else {
            if (count_down == 0) {
                final_text=true;
            } else {
                count_down--;
                textCount.setText(count_down + "");
            }
        }
    }

    public void add_text(String data)
    {
        arrayChat.add(new ItemChat(context, true, data, otherPlayer.profileImgUrl));
        adptChat.notifyDataSetChanged();

        if(!tagger_flag) {
            set_count_text();
        }
    }


    private void finish_game(final boolean winflag) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                try {
                    SHJSONClient client = new SHJSONClient();
                    client.setURL(Global.url_finish_game);
                    client.setMethod(SHWebClient.Method.POST);
                    client.addParam("game_idx", game_idx+"");
                    if(winflag) {
                        client.addParam("win_id", Global.user.user_id);
                        client.addParam("lose_id", otherPlayer.user_id);
                        client.addParam("winner_qa", "q");
                    } else {
                        client.addParam("lose_id", Global.user.user_id);
                        client.addParam("win_id", otherPlayer.user_id);
                        client.addParam("winner_qa", "a");
                    }
                    contQuit = client.Request();
                    if (contQuit.isError()) {
                        msg.what = RET_ERROR;
                        msg.obj = Global.getErrorMessage(contQuit);
                    } else {
                        msg.what = RET_FIN;
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



    public static class ResultHandler extends Handler {
        WeakReference<StartGameActivity> mAcvMain;

        public ResultHandler(StartGameActivity mAcvMainRef) {
            mAcvMain = new WeakReference<>(mAcvMainRef);
        }

        public void handleMessage(Message msg) {
            StartGameActivity localAcv = mAcvMain.get();
            if (localAcv == null) return;
            switch (msg.what) {
                case RET_ERROR:
                    Toast.makeText(localAcv, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;

                case RET_TEXT:
                    localAcv.add_text((String) msg.obj);
                    break;

                case RET_FIN:
                    break;

                case RET_QUIT_OTHER:
                    localAcv.quit_room_complete();
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
        dial.setTitle("방으로 돌아갑니다");

        dial.setButton(DialogInterface.BUTTON_NEGATIVE, "취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dial.cancel();
            }
        });
        dial.setButton(DialogInterface.BUTTON_POSITIVE, "확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                UDPRun.SendtoServer(otherPlayer.user_id, "GAME", "3", "");
                quit_room_complete();
            }
        });
        return dial;
    }

    public void quit_room_complete() {
        finish();
    }
}
