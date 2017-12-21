package com.omneagate.DTO;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.mantra.mfs100.DeviceInfo;
import com.omneagate.Util.Util;
import com.omneagate.activity.LoginActivity;
import com.omneagate.activity.TgLoginActivity;

import java.lang.reflect.Method;

import lombok.Data;

/**
 * Created by user1 on 29/11/17.
 */
@Data
public class MantraDeviceDetailsDto {
    Integer id;

    String deviceNo;

    String mantraSerialNo;

    String imeiSlotNo1;

    String imeiSlotNo2;

    String macId;

    private String TAG = "Mantra device detail";

    public void setDeviceDetails(Context context) {
//        String IMEI1 = null;
//        String IMEI2 = null;
//        String deviceID = null;
//        String macAddress =null;
        try {

            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class<?> telephonyClass = Class.forName(tm.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getFirstMethod = telephonyClass.getMethod("getDeviceId", parameter);
            Log.e("SimData", getFirstMethod.toString());
            Object[] obParameter = new Object[1];

            obParameter[0] = 0;
            imeiSlotNo1 = (String) getFirstMethod.invoke(tm, obParameter);
            Log.e(TAG, "IMEI1 :" + imeiSlotNo1);

            obParameter[0] = 1;
            imeiSlotNo2 = (String) getFirstMethod.invoke(tm, obParameter);
            Log.e(TAG, "IMEI2 :" + imeiSlotNo2);

            deviceNo = Settings.Secure.getString(
                    context.getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
            Log.e(TAG, "Device ID " + deviceNo);

            macId = TgLoginActivity.mac_id;

            Log.e(TAG, "mac Address " + "" + macId);
            if (Util.mfs100 != null) {
                DeviceInfo deviceinfo = Util.mfs100.GetDeviceInfo();
                if (deviceinfo != null)
                    mantraSerialNo = deviceinfo.SerialNo();
                else
                    mantraSerialNo = "";
            }
            Log.e(TAG, "mantraSerialNo " + "" + mantraSerialNo);
//            MantraDeviceDetailsDto mantraDeviceDetailsDto =new MantraDeviceDetailsDto();
//            mantraDeviceDetailsDto.setDeviceNo(deviceID);
//            mantraDeviceDetailsDto.setImeiSlotNo1(IMEI1);
//            mantraDeviceDetailsDto.setImeiSlotNo2(IMEI2);
//            mantraDeviceDetailsDto.setMacId(macAddress);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
