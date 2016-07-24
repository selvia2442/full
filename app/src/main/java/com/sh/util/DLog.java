package com.sh.util;
 
import android.content.Context;
import android.util.Log;

 
/**
 * <b>Log 와 같은 기능을 지원하는 클래스</b>
 * 
 * <p>
 * 테스트시에는 BuildConfig.DEBUG가 true 상태여서 logcat으로 로그를 분석할 수 있고, apk 생성하여 배포시에는
 * 자동으로 false로 변경되어 로그가 출력되지 않는다.
 * </p>
 * 
 * @author dante2k
 * 
 */
public class DLog {
    public final class BuildConfig {
        public final static boolean DEBUG = true;
    }

    /** Log Level Error **/
    public static final void e(Context context, String message) {
        if (BuildConfig.DEBUG) Log.e(context.getClass().getSimpleName(), message);
    }
 
    /** Log Level Warning **/
    public static final void w(Context context, String message) {
        if (BuildConfig.DEBUG) Log.w(context.getClass().getSimpleName(), message);
    }
 
    /** Log Level Information **/
    public static final void i(Context context, String message) {
        if (BuildConfig.DEBUG) Log.i(context.getClass().getSimpleName(), message);
    }
 
    /** Log Level Debug **/
    public static final void d(Context context, String message) {
        if (BuildConfig.DEBUG) Log.d(context.getClass().getSimpleName(), message);
    }
 
    /** Log Level Verbose **/
    public static final void v(Context context, String message) {
        if (BuildConfig.DEBUG) Log.v(context.getClass().getSimpleName(), message);
    }
 
    /** Log Level Error **/
    public static final void e(String TAG, String message) {
        if (BuildConfig.DEBUG) Log.e(TAG, message);
    }
 
    /** Log Level Warning **/
    public static final void w(String TAG, String message) {
        if (BuildConfig.DEBUG) Log.w(TAG, message);
    }
 
    /** Log Level Information **/
    public static final void i(String TAG, String message) {
        if (BuildConfig.DEBUG) Log.i(TAG, message);
    }
 
    /** Log Level Debug **/
    public static final void d(String TAG, String message) {
        if (BuildConfig.DEBUG) Log.d(TAG, message);
    }
 
    /** Log Level Verbose **/
    public static final void v(String TAG, String message) {
        if (BuildConfig.DEBUG) Log.v(TAG, message);
    }
}