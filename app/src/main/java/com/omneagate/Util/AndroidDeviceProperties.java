package com.omneagate.Util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;

import com.omneagate.DTO.DeviceDetailsDto;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Getting android device serviceProperties
 */
public class AndroidDeviceProperties {
    //Context for current activity
    private final Activity context;

    //Constructor
    public AndroidDeviceProperties(Activity context) {
        this.context = context;
    }

    //Android device total properties
    public DeviceDetailsDto getDeviceProperties() {
        DeviceDetailsDto androidDevice = new DeviceDetailsDto();
        androidDevice.setBoard(Build.BOARD);
        androidDevice.setBootLoader(Build.BOOTLOADER);
        androidDevice.setBrand(Build.BRAND);
        androidDevice.setDevice(Build.DEVICE);
        androidDevice.setDisplay(Build.DISPLAY);
        androidDevice.setFingerprint(Build.FINGERPRINT);
        androidDevice.setHardware(Build.HARDWARE);
        androidDevice.setHost(Build.HOST);
        androidDevice.setAndroidId(Build.ID);
        androidDevice.setManufacturer(Build.MANUFACTURER);
        androidDevice.setDeviceName(Build.MODEL);
        androidDevice.setProduct(Build.PRODUCT);
        androidDevice.setRadio(Build.getRadioVersion());
        androidDevice.setSerial(Build.SERIAL);
        androidDevice.setTags(Build.TAGS);
        androidDevice.setType(Build.TYPE);
        androidDevice.setSerialNumber(Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
        androidDevice.setAndroidVersion(Build.VERSION.RELEASE);
        androidDevice.setSdkVersion(Build.VERSION.SDK_INT);
        androidDevice.setMemory(getMemoryInfo());
        androidDevice.setScreenResolution(getScreenResolution());
        androidDevice.setImeiNo(IMEIno());
        androidDevice.setCpuSpeed(getMaxCPUFreqMHz() + " M");
        androidDevice = apkDetails(androidDevice);
        androidDevice.setWifiMac(wiFiMac());
        androidDevice.setBlueToothMac(getBluetoothMacAddress());
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(context).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                androidDevice.setUserName(possibleEmail);
            }
        }
        return androidDevice;
    }

    /**
     * Used to get bluetooth mac
     */
    private String getBluetoothMacAddress() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // if device does not support Bluetooth
        if (mBluetoothAdapter == null) {
            return null;
        }

        return mBluetoothAdapter.getAddress();
    }

    /**
     * Used to get wiFi mac
     */
    private String wiFiMac() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        return wInfo.getMacAddress();
    }


    /**
     * Used to get IMEI number
     */
    private String IMEIno() {
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String identifier = null;
        if (manager != null)
            identifier = manager.getDeviceId();
        if (StringUtils.isEmpty(identifier))
            identifier = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return identifier;
    }

    //This package return the package details of the given apk
    private DeviceDetailsDto apkDetails(DeviceDetailsDto androidDevice) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            androidDevice.setVersionName(packageInfo.versionName);
            androidDevice.setVersionCode(packageInfo.versionCode);
            Long firstInstallTime = packageInfo.firstInstallTime;
            Long lastUpdateTime = packageInfo.lastUpdateTime;
            Date date = new Date(firstInstallTime);
            Date date2 = new Date(lastUpdateTime);
            SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault());
            androidDevice.setFirstInstallTime(df2.format(date));
            androidDevice.setLastUpdateTime(df2.format(date2));
        } catch (Exception e) {
            Util.LoggingQueue(context, "Name Error", e.toString());
            Log.e("Name Error", "Name Error", e);
        }
        return androidDevice;
    }

    //This function return the screen resolution of the device
    private String getScreenResolution() {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels + "X" + metrics.widthPixels;
    }

    //This function return the CPU frequency
    private int getMaxCPUFreqMHz() {

        int maxFreq = -1;
        try {
            RandomAccessFile reader = new RandomAccessFile(
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq",
                    "r");

            boolean done = false;
            while (!done) {
                String line = reader.readLine();
                if (line == null) {
                    done = true;
                    break;
                }
                maxFreq = Integer.parseInt(line) / 1000;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return maxFreq;
    }

    // This function return the memory details of the device
    private String getMemoryInfo() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Activity.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.availMem / 1048576L;
        return availableMegs + "M";
    }
}
