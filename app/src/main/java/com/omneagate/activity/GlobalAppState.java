package com.omneagate.activity;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.omneagate.TransactionController.SMSForCardListener;
import com.omneagate.TransactionController.SMSListener;
import com.omneagate.Util.Queue;
import com.omneagate.Util.Util;

import java.util.Date;

import lombok.Data;

/**
 * Used to create application variable all over application
 */
@Data
public class GlobalAppState extends Application {
    public static boolean isLoggingEnabled = false; //Check logging enabled
    public static String language; //language of user
    public static SMSListener listener;
    public static SMSForCardListener smsListener;
    public static boolean smsAvailable = true;
    public static boolean localLogin = false;
    private static com.omneagate.activity.GlobalAppState sInstance;
    public final Queue queue = new Queue();//Queue used for log in entire application
    public String refId;
    public  static Date serverDate;

    private String weighGaugeName=null;

    private String weighGaugeBTDeviceAddress=null;


    public synchronized static com.omneagate.activity.GlobalAppState getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Util.LoggingQueue(com.omneagate.activity.GlobalAppState.this, "GlobalAppState ", "--------   onCreate() Started    --------");
//        FontsOverride.setDefaultFont(this, "monospace", "fonts/Bamini.ttf");
        try {

            AnalyticsTrackers.initialize(this);
            AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);

            String deviceid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
            if (!deviceid.equals("U2420161101447") && !deviceid.equals("F6C3D49ECFEAEE76")) {
                Log.e("deviceid equals", "deviceid equals");
                Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread thread, Throwable e) {
                        handleUncaughtException(thread, e);
                    }
                });
            } else {
                Log.e("deviceid not  equals", "deviceid not equals");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleUncaughtException(Thread thread, Throwable e) {
        Util.LoggingQueue(com.omneagate.activity.GlobalAppState.this, "GlobalAppState ",
                "--------   handleUncaughtException() Started    --------");
        e.printStackTrace(); // not all Android versions will print the stack trace automatically
        // Log.e("Error", e.toString(), e);
        Util.LoggingQueue(com.omneagate.activity.GlobalAppState.this, "GlobalAppState", "handleUncaughtException() Error = " + e);
        Intent intent = new Intent();
        intent.setAction("com.omneagate.SEND_LOG"); // see step 5.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
        startActivity(intent);
        System.exit(1); // kill off the crashed app
        Util.LoggingQueue(com.omneagate.activity.GlobalAppState.this, "GlobalAppState ",
                "--------   handleUncaughtException() Finished    --------");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        Util.LoggingQueue(com.omneagate.activity.GlobalAppState.this, "GlobalAppState", "attachBaseContext() called ");
    }

    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }
    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }
    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(
                            new StandardExceptionParser(this, null)
                                    .getDescription(Thread.currentThread().getName(), e))
                    .setFatal(false)
                    .build()
            );
        }
    }
    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }

    public String getWeighGaugeName(){
        return this.weighGaugeName;
    }

    public void setWeighGaugeName(String weighGaugeName){
        this.weighGaugeName=weighGaugeName;
    }

    public String getWeighGaugeBTDeviceAddress(){
        return this.weighGaugeBTDeviceAddress;
    }

    public void setWeighGaugeBTDevice(String weighGaugeBTDeviceAddress){
        this.weighGaugeBTDeviceAddress=weighGaugeBTDeviceAddress;
    }

    public static void setServerTime(){
        if(serverDate!=null){
            Date newDate = new Date(serverDate.getTime() +
                    (60L * 1000));
            GlobalAppState.serverDate = newDate;
        }



    }

    public static void getServerTime(){

    }

}
