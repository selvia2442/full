package com.selvia.quiz20.service;

import android.app.ProgressDialog;
import android.content.Context;

import com.selvia.quiz20.Adpt.ItemUserDetail;
import com.sh.net.DataContainer;

/**
 * Created by Administrator on 2016-07-14.
 */
public class Global {
    // base info
    public static String now_ver 		      = "1.0.0";
    public static String mobile_uri 	      = "details?id=com.selvia.quiz20";
    public static String market_uri 	      = "market://"+mobile_uri;
    public static String server_domain	  = "www.codepia.kr";
    public static String server_domain_udp	  = server_domain;
    //public static String server_domain_udp	  = "www.wemoney.co.kr";
    public static String server_url 	      = "http://" + server_domain+ "/";
    public static String app_name           = "quiz20";


    public static String T_API_KEY = "yktKRDTIsIJstufA8hYH0vRVB";
    public static String T_API_SECRET = "hV4AxdmH5z5aeeYOKwnQaGcyuCDKur9xhhvJQeXVqRcEqDCyet";
    public static ItemUserDetail user;

    public static UDPRun sendUDP;


    // 기본 URL
    public static String base_url = server_url + "Battle20q/mobile/";
    public static String ver_url = "1_0_0/";
    public static String img_url = server_url + "/";
    public static String url_image_upload = server_url + "Battle20q/ImageUpload.jsp"; // 이미지는//


    public static String url_longin_in_db           = base_url + ver_url + "LoginInDB.jsp";
    public static String url_room_list_get           = base_url + ver_url + "RoomLstGet.jsp";
    public static String url_new_room_set           = base_url + ver_url + "RoomNewSet.jsp";
    public static String url_room_entered           = base_url + ver_url + "RoomEntered.jsp";
    public static String url_room_quit           = base_url + ver_url + "RoomQuit.jsp";
    public static String url_start_game           = base_url + ver_url + "StartGame.jsp";
    public static String url_quit_app           = base_url + ver_url + "QuitApp.jsp";
    public static String url_finish_game           = base_url + ver_url + "FinishGame.jsp";



    public static ProgressDialog getProgDlg(Context context ) {
        if ( context != null ) {
            ProgressDialog dlg = new ProgressDialog( context );
            dlg.setMessage( "잠시만 기다려 주세요" );
            dlg.setCancelable( false );
            return dlg;
        } else {
            return null;
        }
    }


    public static String getErrorMessage(DataContainer container) {
        return container.getMessage();
    }

}
