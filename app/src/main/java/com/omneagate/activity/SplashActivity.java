package com.omneagate.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.AndroidDeviceProperties;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.InsertIntoDatabase;
import com.omneagate.Util.Util;
import com.omneagate.receiver.AdjustmentAlarmReceiver;
import com.omneagate.receiver.AdvanceStockAlarmReceiver;
import com.omneagate.receiver.AllocationAlarmReceiver;
import com.omneagate.receiver.BillAlarmReceiver;
import com.omneagate.receiver.BiometricAlarmReceiver;
import com.omneagate.receiver.CloseSaleAlarmReceiver;
import com.omneagate.receiver.HeartBeatAlarmReceiver;
import com.omneagate.receiver.InspectionCriteriaAlarmReceiver;
import com.omneagate.receiver.InspectionReportAckAlarmReceiver;
import com.omneagate.receiver.InspectionReportAlarmReceiver;
import com.omneagate.receiver.InwardAlarmReceiver;
import com.omneagate.receiver.LoginReceiver;
import com.omneagate.receiver.MigrationAlarmReceiver;
import com.omneagate.receiver.RegularSyncAlarmReceiver;
import com.omneagate.receiver.RemoteLogReceiver;
import com.omneagate.receiver.StatisticsAlarmReceiver;
import com.omneagate.receiver.SyncExceptionAlarmReceiver;

import org.joda.time.DateTime;

import java.util.TimeZone;


//SplashActivity initial activity of this App

public class SplashActivity extends BaseActivity {

    public static Context context;
    String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Log.e(TAG, "current year..."+new DateTime().getYear());

        /*setTamilHeader(((TextView) findViewById(R.id.tamilHeader)), R.string.headerAllPageEnglish);
        setTamil(((TextView) findViewById(R.id.tamilHeader2)), R.string.headerAllPage);
        setTamil(((TextView) findViewById(R.id.tamilHeader3)), R.string.fpsposapplication);*/
        context = SplashActivity.this;
        Log.e("SplashActivity", "SplashActivity");
        Log.e(TAG, "current timezone..."+ TimeZone.getDefault());

        if (FPSDBHelper.getInstance(this).getFirstSync()) {
            Log.e("SplashActivity", "getFirstSync");
            InsertIntoDatabase db = new InsertIntoDatabase(this);
            db.insertIntoDatabase();
            Log.e("getFirstSync", "SplashAct");
            FPSDBHelper.getInstance(this).insertValues();
        }
        try {
            SQLiteDatabase db = new FPSDBHelper(this).getWritableDatabase();
            SharedPreferences sharedpreferences = getSharedPreferences("DBData", Context.MODE_PRIVATE);
            Log.e("splash activity........","db.getVersion()...."+db.getVersion());
//            Util.LoggingQueue(this, "Error", "db.getVersion()...."+db.getVersion());
            int oldVersion = sharedpreferences.getInt("version", db.getVersion());
            Log.e("splash activity........","oldVersion...."+oldVersion);
//            Util.LoggingQueue(this, "Error", "oldVersion...." + oldVersion);
            Log.e("splash activity........", "newVersion....1");
//            Util.LoggingQueue(this, "Error", "newVersion....24");
            FPSDBHelper.getInstance(this).onUpgrade(db, oldVersion, 2);
            TelephonyManager telMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            int simState = telMgr.getSimState();
            switch (simState) {
                case TelephonyManager.SIM_STATE_ABSENT:
                    GlobalAppState.smsAvailable = false;
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    GlobalAppState.smsAvailable = false;
                    break;
            }

            String languageCode = FPSDBHelper.getInstance(this).getMasterData("language");
            if (languageCode == null) {
                languageCode = "en";
            }
            Util.changeLanguage(this, languageCode);
            GlobalAppState.language = languageCode;
        } catch (Exception e) {
            Log.e("SplashActivity", e.toString(), e);
        }


        int secondsDelayed = 3;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(SplashActivity.this, TgLoginActivity.class));
                finish();
            }
        }, secondsDelayed * 1000);

    }

    /*
    *  Database helper called as writable database
    *  Starting services for android application
    *  */
    @Override
    protected void onStart() {
        super.onStart();
        // Getting device id
        AndroidDeviceProperties deviceProperties = new AndroidDeviceProperties(this);
        Util.deviceSerialNo = deviceProperties.getDeviceProperties().getSerialNumber();
        // Scheduling alarms for background process
        String serverUrl = FPSDBHelper.getInstance(context).getMasterData("serverUrl");
        SharedPreferences mySharedPreferences = context.getSharedPreferences("FPS_POS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("server_url", serverUrl);
        editor.apply();
    //    scheduleAlarms();
        // Purging background service history table
  //      new purgeBackgroundServiceHistory().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        // Navigating to Login activity
//        startActivity(new Intent(this, TgLoginActivity.class));
//        finish();
    }

    private void scheduleAlarms() {
        scheduleHeartBeatAlarm();
        scheduleStatisticsAlarm();
        scheduleAllocationAlarm();
        scheduleRegularSyncAlarm();
        scheduleInwardAlarm();
        scheduleAdjustmentAlarm();
        scheduleBillAlarm();
        scheduleAdvanceStockAlarm();
        scheduleCloseSaleAlarm();
        scheduleLoginAlarm();
        scheduleRemoteLogAlarm();
        scheduleSyncExceptionAlarm();
        scheduleMigrationAlarm();
        scheduleBiometricAlarm();
        scheduleInspectionReportServiceAlarm();
        scheduleInspectionCriteriaServiceAlarm();
        scheduleInspectionReportAckServiceAlarm();
    }

    // Setup a recurring alarm for every five minutes
    public void scheduleHeartBeatAlarm() {
        Log.e(TAG, "scheduleHeartBeatAlarm called...");
        Intent intent = new Intent(getApplicationContext(), HeartBeatAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, HeartBeatAlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 900000, pIntent);
    }

    // Setup a recurring alarm for every fifteen minutes
    public void scheduleStatisticsAlarm() {
        Log.e(TAG, "scheduleStatisticsAlarm called...");
        Intent intent = new Intent(getApplicationContext(), StatisticsAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, StatisticsAlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 3600000, pIntent);

    }

    // Setup a recurring alarm for every fifteen minutes
    public void scheduleAllocationAlarm() {
        Log.e(TAG, "scheduleAllocationAlarm called...");
        Intent intent = new Intent(getApplicationContext(), AllocationAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AllocationAlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 900000, pIntent);
    }

    // Setup a recurring alarm for every fifteen minutes
    public void scheduleRegularSyncAlarm() {
        Log.e(TAG, "scheduleRegularSyncAlarm called...");
        Intent intent = new Intent(getApplicationContext(), RegularSyncAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, RegularSyncAlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 900000, pIntent);
    }

    // Setup a recurring alarm for every fifteen minutes
    public void scheduleInwardAlarm() {
        Log.e(TAG, "scheduleInwardAlarm called...");
        Intent intent = new Intent(getApplicationContext(), InwardAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, InwardAlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 900000, pIntent);
    }

    // Setup a recurring alarm for every fifteen minutes
    public void scheduleAdjustmentAlarm() {
        Log.e(TAG, "scheduleAdjustmentAlarm called...");
        Intent intent = new Intent(getApplicationContext(), AdjustmentAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, InwardAlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 900000, pIntent);
    }

    // Setup a recurring alarm for every fifteen minutes
    public void scheduleBillAlarm() {
        Log.e(TAG, "scheduleBillAlarm called...");
        Intent intent = new Intent(getApplicationContext(), BillAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, BillAlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 900000, pIntent);
    }

    // Setup a recurring alarm for every fifteen minutes
    public void scheduleAdvanceStockAlarm() {
        Log.e(TAG, "scheduleAdvanceStockAlarm called...");
        Intent intent = new Intent(getApplicationContext(), AdvanceStockAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AdvanceStockAlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 900000, pIntent);
    }

    // Setup a recurring alarm for every fifteen minutes
    public void scheduleCloseSaleAlarm() {
        Log.e(TAG, "scheduleCloseSaleAlarm called...");
        Intent intent = new Intent(getApplicationContext(), CloseSaleAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, CloseSaleAlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 900000, pIntent);
    }

    // Setup a recurring alarm for every fifteen minutes
    public void scheduleLoginAlarm() {
        Log.e(TAG, "scheduleLoginAlarm called...");
        Intent intent = new Intent(getApplicationContext(), LoginReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, LoginReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 900000, pIntent);
    }

    // Setup a recurring alarm for every fifteen minutes
    public void scheduleRemoteLogAlarm() {
        Log.e(TAG, "scheduleRemoteLogAlarm called...");
        Intent intent = new Intent(getApplicationContext(), RemoteLogReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, RemoteLogReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 900000, pIntent);
    }

    // Setup a recurring alarm for every fifteen minutes
    public void scheduleSyncExceptionAlarm() {
        Log.e(TAG, "scheduleSyncExceptionAlarm called...");
        Intent intent = new Intent(getApplicationContext(), SyncExceptionAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, RemoteLogReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 900000, pIntent);
    }

    // Setup a recurring alarm for every fifteen minutes
    public void scheduleMigrationAlarm() {
        Log.e(TAG, "scheduleMigrationAlarm called...");
        Intent intent = new Intent(getApplicationContext(), MigrationAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, RemoteLogReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 900000, pIntent);
    }

    // Setup a recurring alarm for every fifteen minutes
    public void scheduleBiometricAlarm() {
        Log.e(TAG, "scheduleBiometricAlarm called...");
        Intent intent = new Intent(getApplicationContext(), BiometricAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, RemoteLogReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 900000, pIntent);
    }

    // Setup a recurring alarm for every fifteen minutes
    public void scheduleInspectionReportServiceAlarm() {
        Log.e(TAG, "scheduleInspectionReportServiceAlarm called...");
        Intent intent = new Intent(getApplicationContext(), InspectionReportAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, RemoteLogReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 900000, pIntent);
    }

    // Setup a recurring alarm for every fifteen minutes
    public void scheduleInspectionReportAckServiceAlarm() {
        Log.e(TAG, "scheduleInspectionReportAckServiceAlarm called...");
        Intent intent = new Intent(getApplicationContext(), InspectionReportAckAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, RemoteLogReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 900000, pIntent);
    }

    // Setup a recurring alarm for every fifteen minutes
    public void scheduleInspectionCriteriaServiceAlarm() {
        Log.e(TAG, "scheduleInspectionCriteriaServiceAlarm called...");
        Intent intent = new Intent(getApplicationContext(), InspectionCriteriaAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, RemoteLogReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 900000, pIntent);
    }

    private class purgeBackgroundServiceHistory extends AsyncTask<String, Void, Void> {
        protected void onPreExecute() {}
        protected Void doInBackground(final String... args) {
            purgeBackgroundServiceTables();
            purgeTables();
            return null;
        }
        protected void onPostExecute() {}
    }

    private void purgeBackgroundServiceTables() {
        String heartBeatPurgeDays = "";
        try {
            heartBeatPurgeDays = FPSDBHelper.getInstance(SplashActivity.this).getMasterData("POS_HEARTBEAT_PURGE_DAYS");
        } catch(Exception e) {}
        if((heartBeatPurgeDays != null) && (!heartBeatPurgeDays.equalsIgnoreCase(""))) {
            int days = Integer.parseInt(heartBeatPurgeDays);
            FPSDBHelper.getInstance(SplashActivity.this).purgeHeartBeatHistory(days);
            FPSDBHelper.getInstance(SplashActivity.this).purgeStatisticsHistory(days);
            FPSDBHelper.getInstance(SplashActivity.this).purgeAllocationHistory(days);
            FPSDBHelper.getInstance(SplashActivity.this).purgeRegularSyncHistory(days);
            FPSDBHelper.getInstance(SplashActivity.this).purgeInwardHistory(days);
            FPSDBHelper.getInstance(SplashActivity.this).purgeAdjustmentHistory(days);
            FPSDBHelper.getInstance(SplashActivity.this).purgeAdvanceStockHistory(days);
            FPSDBHelper.getInstance(SplashActivity.this).purgeBillHistory(days);
            FPSDBHelper.getInstance(SplashActivity.this).purgeCloseSaleHistory(days);
            FPSDBHelper.getInstance(SplashActivity.this).purgeLoginHistory(days);
            FPSDBHelper.getInstance(SplashActivity.this).purgeRemoteLogHistory(days);
            FPSDBHelper.getInstance(SplashActivity.this).purgeSyncExceptionHistory(days);
            FPSDBHelper.getInstance(SplashActivity.this).purgeBifurcationHistory(days);
            FPSDBHelper.getInstance(SplashActivity.this).purgeMigrationOutHistory(days);
            FPSDBHelper.getInstance(SplashActivity.this).purgeMigrationInHistory(days);
            FPSDBHelper.getInstance(SplashActivity.this).purgeBiometricHistory(days);
        }
    }

    private void purgeTables() {
        String billPurgeDays = "";
        try {
            billPurgeDays = FPSDBHelper.getInstance(SplashActivity.this).getMasterData("purgeBill");
        } catch(Exception e) {}
        if((billPurgeDays != null) && (!billPurgeDays.equalsIgnoreCase(""))) {
            int days = Integer.parseInt(billPurgeDays);
            FPSDBHelper.getInstance(SplashActivity.this).purge(days);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("On Stop", "splash On Stop");
    }


    /*Concrete method*/
    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onDestroy() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        }
        catch(Exception e) {}
        super.onDestroy();
    }
}