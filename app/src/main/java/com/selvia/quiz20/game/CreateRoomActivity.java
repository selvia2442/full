package com.selvia.quiz20.game;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.selvia.quiz20.service.Global;
import com.selvia.quiz20.R;
import com.sh.net.DataContainer;
import com.sh.net.SHJSONClient;
import com.sh.net.SHWebClient;

import java.lang.ref.WeakReference;


public class CreateRoomActivity extends AppCompatActivity {
    public static final int RET_ERROR = 0;
    public static final int RET_NEWROOM = 1;

    public static ResultHandler resultHandler;
    ProgressDialog loadingdialog = null;
    DataContainer contRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        final EditText editName = (EditText) findViewById( R.id.editName );
        Button btnNew = (Button) findViewById( R.id.btnNew );
        Button btnCancel = (Button) findViewById( R.id.btnCancel );

        resultHandler = new ResultHandler(this);

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_room_data(editName.getText().toString());
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void set_room_data(final String title) {
        loadingdialog = Global.getProgDlg(this);
        loadingdialog.show();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                try {
                    SHJSONClient client = new SHJSONClient();
                    client.setURL(Global.url_new_room_set);
                    client.setMethod(SHWebClient.Method.POST);
                    client.addParam("title", title);
                    client.addParam("user_id", Global.user.user_id);
                    contRoom = client.Request();
                    if (contRoom.isError()) {
                        msg.what = RET_ERROR;
                        msg.obj = Global.getErrorMessage(contRoom);
                    } else {
                        msg.what = RET_NEWROOM;
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


    public void set_room_data_complete() {
        String str_room_no = contRoom.getMapItem( "room_no" ).getValue().toString();
        String title = contRoom.getMapItem( "title" ).getValue().toString();
        Log.e("title","."+title);

        Bundle b = new Bundle();
        b.putBoolean("master", true);
        b.putInt("roomNo", Integer.parseInt(str_room_no));
        b.putString("roomName", title );

        Intent itt = new Intent(CreateRoomActivity.this, ReadyGameActivity.class);
        itt.putExtras(b);
        startActivity(itt);

        finish();
    }

    public static class ResultHandler extends Handler {
        WeakReference<CreateRoomActivity> mAcvMain;

        public ResultHandler(CreateRoomActivity mAcvMainRef) {
            mAcvMain = new WeakReference<>(mAcvMainRef);
        }

        public void handleMessage(Message msg) {
            CreateRoomActivity localAcv = mAcvMain.get();
            if (localAcv == null) return;
            switch (msg.what) {
                case RET_ERROR:
                    if (localAcv.loadingdialog != null) {
                        localAcv.loadingdialog.dismiss();
                        localAcv.loadingdialog = null;
                    }
                    Toast.makeText( localAcv, (String) msg.obj, Toast.LENGTH_SHORT ).show();
                    break;

                case RET_NEWROOM:
                    if (localAcv.loadingdialog != null) {
                        localAcv.loadingdialog.dismiss();
                        localAcv.loadingdialog = null;
                    }
                    localAcv.set_room_data_complete();
                    break;
            }
        }
    }
}
