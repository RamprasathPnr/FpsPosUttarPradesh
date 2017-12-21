package com.omneagate.process;

import android.content.Context;
import android.util.Log;

import com.omneagate.activity.GlobalAppState;
import com.omneagate.service.BaseSchedulerService;

import java.io.Serializable;

/**
 * Created by root on 29/5/17.
 */
public class ServerTimeSetProcess implements BaseSchedulerService, Serializable {
    private String TAG=ServerTimeSetProcess.class.getCanonicalName();
    @Override
    public void process(Context context) {
        Log.e(TAG,"ServerTimeSetProcess");
        GlobalAppState.setServerTime();
    }
}