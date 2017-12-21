package com.omneagate.Util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.mantra.mfs100.FingerData;
import com.mantra.mfs100.MFS100;
import com.mantra.mfs100.MFS100Event;

/**
 * Created by user1 on 30/11/17.
 */
public class Mantra_intialization implements MFS100Event {

    private final Context context;

    public Mantra_intialization(Context context) {
        this.context=context;
        if (Util.mfs100 == null) {
            int mfsVer = 41;
            Util.mfs100 = new MFS100(this, mfsVer);
        }

        if (Util.mfs100 != null) {
            Util.mfs100.SetApplicationContext(context);
        }
    }

    @Override
    public void OnDeviceAttached(int vid, int pid, boolean hasPermission) {
        int ret = 0;
        if (!hasPermission) {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show();
            return;
        }
        if (vid == 1204 || vid == 11279) {
            if (pid == 34323) {
                ret = Util.mfs100.LoadFirmware();
                if (ret != 0) {
                    Toast.makeText(context, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Loadfirmware success", Toast.LENGTH_SHORT).show();
                }
            } else if (pid == 4101) {
                ret = Util.mfs100.Init();
                if (ret != 0) {
                    Toast.makeText(context, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
                } else {
//                    DeviceInfo deviceIfo = Util.mfs100.GetDeviceInfo();
//                    String details = new Gson().toJson(deviceIfo);
//                    Log.e("mantra device details",details);

//                    Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void OnPreview(FingerData fingerData) {

    }

    @Override
    public void OnCaptureCompleted(boolean b, int i, String s, FingerData fingerData) {

    }

    @Override
    public void OnDeviceDetached() {
        UnInitScanner();
    }

    @Override
    public void OnHostCheckFailed(String s) {

    }

    public void UnInitScanner() {
        try {
            if (Util.mfs100 != null) {
                int ret = Util.mfs100.UnInit();
                if (ret != 0) {
//                    Toast.makeText(BenefBfdScanActivity.context, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(BenefBfdScanActivity.context, "Uninit Success", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e("UnInitScanner.EX", e.toString());
        }
    }
}
