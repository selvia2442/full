package com.selvia.quiz20.game;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.selvia.quiz20.Adpt.AdptRoom;
import com.selvia.quiz20.Adpt.ItemRoom;
import com.selvia.quiz20.service.Global;
import com.selvia.quiz20.R;
import com.selvia.quiz20.service.UDPRun;
import com.sh.net.DataContainer;
import com.sh.net.SHJSONClient;
import com.sh.net.SHWebClient;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int RET_ERROR = 0;
    public static final int RET_ROOM = 1;
    public static final int RET_ENTERROOM = 2;
    public static final int RET_QUIT = 3;

    public static ResultHandler resultHandler;
    ProgressDialog loadingdialog = null;
    DataContainer contRoom, contEnter, contQuit;
    ArrayList<ItemRoom> arrayList = new ArrayList<ItemRoom>();
    AdptRoom adptRoom;

    String nowtitle;
    int nowRoomno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Global.sendUDP = new UDPRun( this );
        new Thread(Global.sendUDP).start();
        UDPRun.SendtoServer("0", "Dummy", "0", "");

        resultHandler = new ResultHandler(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        InitView(navigationView);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itt = new Intent(getApplicationContext(), CreateRoomActivity.class);
                startActivity(itt);
            }
        });

        Button btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load_room_list();
            }
        });

        ListView listView = (ListView) findViewById(R.id.listView);
        adptRoom = new AdptRoom(arrayList);
        listView.setAdapter(adptRoom);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("onItemSelected", i+"/");
                ItemRoom item = arrayList.get(i);
                nowtitle = item.title;
                nowRoomno = item.no;
                enter_room();
            }
        });

        load_room_list();
    }

    public void InitView(NavigationView nav_view)
    {
        View view = nav_view.inflateHeaderView(R.layout.nav_header_main);
        ImageView imageView_head = (ImageView) view.findViewById(R.id.imageView_head);

        TextView text_headname = (TextView) view.findViewById(R.id.text_headname);
        TextView text_headid = (TextView) view.findViewById(R.id.text_headid);

        AQuery aq = new AQuery( this );
        aq.id( imageView_head ).image( Global.user.profileImgUrl );

        text_headname.setText(Global.user.user_name);
        text_headid.setText("@"+Global.user.user_id);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            createDlgQuit().show();
            //super.onBackPressed();
        }
    }

    private AlertDialog createDlgQuit() {
        final AlertDialog dial = new AlertDialog.Builder(this).create();
        dial.setCancelable(false);
        dial.setTitle("어플을 종료하시겠습니까?");
        dial.setButton(DialogInterface.BUTTON_NEGATIVE, "취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dial.cancel();
            }
        });
        dial.setButton(DialogInterface.BUTTON_POSITIVE, "종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                quit_app();
            }
        });
        return dial;
    }


    private void quit_app() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                try {
                    SHJSONClient client = new SHJSONClient();
                    client.setURL(Global.url_quit_app);
                    client.setMethod(SHWebClient.Method.POST);
                    client.addParam("user_id", Global.user.user_id);
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

    public void quit_app_complete() {
        finish();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else return true;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void load_room_list() {
        loadingdialog = Global.getProgDlg(this);
        loadingdialog.show();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                try {
                    SHJSONClient client = new SHJSONClient();
                    client.setURL(Global.url_room_list_get);
                    client.setMethod(SHWebClient.Method.POST);
                    contRoom = client.Request();
                    if (contRoom.isError()) {
                        msg.what = RET_ERROR;
                        msg.obj = Global.getErrorMessage(contRoom);
                    } else {
                        msg.what = RET_ROOM;
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


    public void load_room_list_complete() {
        arrayList.clear();

        int size = contRoom.getDataTable(0).getRowsSize();
        for (int i = 0; i < size; i++) {
            String room_no = contRoom.getDataTable(0).getRowColumnData(i, "room_no");
            String title = contRoom.getDataTable(0).getRowColumnData(i, "title");
            String people_cnt = contRoom.getDataTable(0).getRowColumnData(i, "people_cnt");
            String state = contRoom.getDataTable(0).getRowColumnData(i, "state");
            String create_date = contRoom.getDataTable(0).getRowColumnData(i, "create_date");

            ItemRoom item = new ItemRoom(this, room_no, title, people_cnt, state, create_date);
            arrayList.add(item);
        }
        adptRoom.notifyDataSetChanged();
    }



    private void enter_room() {
        loadingdialog = Global.getProgDlg(this);
        loadingdialog.show();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                try {
                    SHJSONClient client = new SHJSONClient();
                    client.setURL(Global.url_room_entered);
                    client.setMethod(SHWebClient.Method.POST);
                    client.addParam("user_id", Global.user.user_id);
                    client.addParam("room_no", nowRoomno+"");
                    contEnter = client.Request();
                    if (contEnter.isError()) {
                        msg.what = RET_ERROR;
                        msg.obj = Global.getErrorMessage(contRoom);
                    } else {
                        msg.what = RET_ENTERROOM;
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


    public void enter_room_complete() {
        Bundle b = new Bundle();
        b.putBoolean("master", false);
        b.putInt("roomNo", nowRoomno);
        b.putString("roomName", nowtitle);
        String user_id = contEnter.getMapItem( "user_id" ).getValue().toString();
        String user_name = contEnter.getMapItem( "user_name" ).getValue().toString();
        String user_img = contEnter.getMapItem( "user_img" ).getValue().toString();
        b.putString("user_id", user_id);
        b.putString("user_name", user_name);
        b.putString("user_img", user_img);

        Log.e("SendtoServer","SendtoServer");
        UDPRun.SendtoServer(user_id, "ROOM", "0", Global.user.user_id+","+Global.user.user_name+","+Global.user.profileImgUrl);

        Intent itt = new Intent(getApplicationContext(), ReadyGameActivity.class);
        itt.putExtras(b);
        startActivityForResult(itt, 1);

    }


    public static class ResultHandler extends Handler {
        WeakReference<MainActivity> mAcvMain;

        public ResultHandler(MainActivity mAcvMainRef) {
            mAcvMain = new WeakReference<>(mAcvMainRef);
        }

        public void handleMessage(Message msg) {
            MainActivity localAcv = mAcvMain.get();
            if (localAcv == null) return;
            switch (msg.what) {
                case RET_ERROR:
                    if (localAcv.loadingdialog != null) {
                        localAcv.loadingdialog.dismiss();
                        localAcv.loadingdialog = null;
                    }
                    Toast.makeText( localAcv, (String) msg.obj, Toast.LENGTH_SHORT ).show();
                    break;

                case RET_ROOM:
                    if (localAcv.loadingdialog != null) {
                        localAcv.loadingdialog.dismiss();
                        localAcv.loadingdialog = null;
                    }
                    localAcv.load_room_list_complete();
                    break;

                case RET_ENTERROOM:
                    if (localAcv.loadingdialog != null) {
                        localAcv.loadingdialog.dismiss();
                        localAcv.loadingdialog = null;
                    }
                    localAcv.enter_room_complete();
                    break;

                case RET_QUIT:
                    localAcv.quit_app_complete();
                    break;
            }
        }
    }
}
