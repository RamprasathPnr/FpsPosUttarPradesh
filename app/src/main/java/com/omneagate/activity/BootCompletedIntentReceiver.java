package com.omneagate.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.omneagate.Util.Util;

/**
 * Created by user1 on 1/2/16.
 */
public class BootCompletedIntentReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        /*if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            try {
                // Alarm manager to call purge for every 24 hours
                AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent _intent = new Intent(context, AlarmReceiver.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, _intent, 0);
                alarmMgr.cancel(alarmIntent); // cancel any existing alarms
                alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 86400000, alarmIntent);
            }
            catch(Exception e) {
                Util.LoggingQueue(context, "bootcompleted exception android....", "" + e);
            }
        }*/
    }
}
