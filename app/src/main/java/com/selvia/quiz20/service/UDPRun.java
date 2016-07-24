package com.selvia.quiz20.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;

import com.selvia.quiz20.PopupActivity;
import com.selvia.quiz20.game.ReadyGameActivity;
import com.selvia.quiz20.game.StartGameActivity;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by ds on 2016-07-24.
 */
public class UDPRun implements Runnable {
    //public static final int SERVERPORT = 19201;
    public static final int SERVERPORT = 19797;
    public static DatagramSocket socket = null;
    public InetAddress host = null;
    public Context context;

    public String uData;

    public UDPRun(Context context){
        try {
            this.context = context;
            Log.e("UDPRun", "log");
        } catch (Exception e) {
            Log.e("UDPRun", "UDPRun", e);
        }
    }

    public void setContext(Context context){
        this.context = context;
    }

    public void InitUDP() {
        try {
            if(socket == null)  socket = new DatagramSocket();
            if(host == null) 	host = InetAddress.getByName( Global.server_domain_udp );
        } catch ( SocketException e ) {
            Log.e("InitUDP error", "socket create");
        } catch ( UnknownHostException e ) {
            Log.e("InitUDP error", "UnknownHostException");
        }
    }

    public void UDPSend(String dst, String abcd, String key, String input) throws Exception
    {
        InitUDP();
        Log.e("Send Server", Global.server_domain_udp);
        String buff = Global.user.user_id+"|"+ dst+"|"+abcd+"|"+key+"|"+input+"|";
        Log.e("UDPSend", "send data : " + buff);
        byte[] sendData = buff.getBytes();
        try {
            DatagramPacket packet = new DatagramPacket( sendData, sendData.length, host, SERVERPORT );
            socket.send(packet);

        }catch (Exception e) {
            throw e;
        }
    }


    @Override
    public void run() {
        // TODO Auto-generated method stub
        while(true) {
            try {
                InitUDP();
                Log.e("Recv from Server", Global.server_domain_udp);

                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, host, SERVERPORT);
                    socket.receive(packet);

                uData = new String(packet.getData()).trim();
                Log.e("Recv from Server", uData);
                AnalysisPacket();
            } catch (Exception e) {
                Log.e("UDPRun", "Error", e);
            }
        }
    }


    public String getRealData()
    {
        int idx=uData.indexOf("|");
        if(idx < 0) return uData;
        return uData.substring(idx+1);
    }

    public String getFirstData()
    {
        int idx=uData.indexOf("|");
        if(idx < 0) return uData;
        return uData.substring(0, idx);
    }

    // 서버에 클라이언트 존재 전달
    static public void SendtoServer(final String dst, final String commend, final String key, final String data) {
        Thread thread = new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    UDPRun udp = Global.sendUDP;
                    if(udp != null) {
                        udp.UDPSend( dst, commend, key, data );
                    } else {
                        Log.e("sendUDP", "null+");
                    }
                    Log.e("Say to Server", data);
                } catch ( Exception e ) {
                    Log.e( "Say to Server", "send error", e );
                    return;
                }
            }
        } );
        thread.start();
    }

    public void AnalysisPacket() {
        try {
            byte[] abcd = getFirstData().getBytes();
            String data = getRealData();
            String firstdata = getFirstData();
            Log.e("firstdata", firstdata);
            Log.e("data", data);

            MyTokenizer mt = new MyTokenizer(data, "|");
            String key = mt.nextToken();

            Message msg = Message.obtain();
            msg.obj = mt.nextToken();

            switch (firstdata) {
                case "ROOM":
                    switch (key) {
                        case "0":
                            msg.what = ReadyGameActivity.RET_ENTERED;
                            AlertMessageServerNotice(0);
                            break;

                        case "1":
                            msg.what = ReadyGameActivity.RET_QUIT_P;
                            AlertMessageServerNotice(2);
                            break;
                    }
                    ReadyGameActivity.resultHandler.sendMessage( msg );
                    break;

                case "READY":
                    switch (key) {
                        case "0":
                            msg.what = ReadyGameActivity.RET_MAKEANSWER;
                            break;

                        case "1":
                            msg.what = ReadyGameActivity.RET_START_Q;
                            AlertMessageServerNotice(1);
                            break;

                        case "2":
                            msg.what = ReadyGameActivity.RET_SWITCH;
                            break;

                    }
                    ReadyGameActivity.resultHandler.sendMessage( msg );
                    break;

                case "GAME":
                    switch (key) {
                        case "0":
                            msg.what = StartGameActivity.RET_TEXT;
                            break;

                        case "1":
                            msg.what = StartGameActivity.RET_QUIT_OTHER;
                            AlertMessageServerNotice(3);
                            break;

                        case "3":
                            msg.what = StartGameActivity.RET_QUIT_OTHER;
                            break;

                        case "5":
                            msg.what = StartGameActivity.RET_FIN;
                            AlertMessageServerNotice(5);
                            break;

                        case "6":
                            msg.what = StartGameActivity.RET_FIN;
                            AlertMessageServerNotice(6);
                            break;
                    }
                    StartGameActivity.resultHandler.sendMessage( msg );
                    break;
            }
        } catch ( Exception e ) {
            Log.e( "SrvReceiver", "GetMsgFromSever");
        }
    }


    private void AlertMessageServerNotice(int way) {
        Intent intent = new Intent(context, PopupActivity.class);
        switch (way) {
            case 0:
                intent.putExtra("data","플레이어가 참여합니다.");
                break;
            case 1:
                intent.putExtra("data","게임을 시작합니다.");
                break;
            case 2:
                intent.putExtra("data","방장이 방을 닫았습니다.");
                break;
            case 3:
                intent.putExtra("data","새로운 게임을 위해 돌아갑니다.");
                break;
            case 5:
                intent.putExtra("data","승리!! 게임에서 승리하셨습니다!");
                break;
            case 6:
                intent.putExtra("data","패배.. 다음에는 더 잘할 수 있을거에요.");
                break;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + 1 * 1000, pendingIntent);
    }

}
