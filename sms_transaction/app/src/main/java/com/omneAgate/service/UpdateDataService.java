package com.omneagate.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.omneagate.Util.DownloadDataProcessor;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;


/**
 * Service for checking connection of server
 */
public class UpdateDataService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();

        Thread notifyingThread = new Thread(null, serviceTask, "UpdateData");
        notifyingThread.start();
        Util.LoggingQueue(UpdateDataService.this, "Info", "Service DB sync manager created");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /*Connection device Thread*/
    private final Runnable serviceTask = new Runnable() {
        public void run() {
            Looper.prepare();
            while (true) {
                try {
                    Util.LoggingQueue(UpdateDataService.this, "Info", "Service DB sync manager started");
                    if (SessionId.getInstance().getSessionId().length() > 0) {
                        NetworkConnection network = new NetworkConnection(UpdateDataService.this);
                        if (network.isNetworkAvailable()) {
                            DownloadDataProcessor dataDownload = new DownloadDataProcessor(UpdateDataService.this);
                            dataDownload.processor();
                        }
                    }
                    Util.LoggingQueue(UpdateDataService.this, "Info", "Service DB sync manager ended");
                    long timeout = 15 * 60 * 1000;
                    Thread.sleep(timeout);
                    Util.LoggingQueue(UpdateDataService.this, "Info", "Service DB sync manager wakeup");
                } catch (Exception e) {
                    Util.LoggingQueue(UpdateDataService.this, "Error", e.getMessage());
                    Log.e("Error", e.toString(), e);
                }
            }
        }

    };


}
