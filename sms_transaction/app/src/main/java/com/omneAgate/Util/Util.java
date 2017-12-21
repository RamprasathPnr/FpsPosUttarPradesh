package com.omneagate.Util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;

import com.omneagate.DTO.LoggingDto;
import com.omneagate.UndoBar.UndoBar;
import com.omneagate.activity.GlobalAppState;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.util.Locale;

/**
 * Utility class for entire application
 */
public class Util {

    //simple messageBar for FPS
    public static void messageBar(Activity activity, String message) {
        UndoBar undoBar = new UndoBar.Builder(activity)//
                .setMessage(message)//
                .setStyle(UndoBar.Style.KITKAT)
                .setAlignParentBottom(true)
                .create();
        undoBar.show();

    }

    /**
     * Change language in android
     *
     * @param languageCode,context
     */
    public static void changeLanguage(Context context, String languageCode) {
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(languageCode);
        res.updateConfiguration(conf, dm);
        storePreference(context, languageCode);
    }

    /**
     * Language preference store in local storage
     *
     * @param languageCode,context
     */
    private static void storePreference(Context context, String languageCode) {
        SharedPreferences prefs = context.getSharedPreferences("FPS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lCode", languageCode);
        editor.commit();
    }

    /**
     * Registration store in local preference
     *
     * @param context
     */
    public static void storePreferenceRegister(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("FPS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("register", true);
        editor.commit();
    }

    /**
     * Registration store in local preference
     *
     * @param context
     */
    public static void storePreferenceApproved(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("FPS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("register", true);
        editor.putBoolean("approved", true);
        editor.commit();
    }


    /**
     * get log data and set device id in log
     *
     * @param context,errorType and error string
     */
    private static LoggingDto logging(Context context, String errorType, String error) {
        LoggingDto log = new LoggingDto();
        log.setErrorType(errorType);
        log.setLogMessage(error);
        log.setDeviceId(Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID));
        return log;
    }

    /**
     * Add log to queue
     *
     * @param context,errorType and error string
     */
    public static void LoggingQueue(Context context, String errorType, String error) {

        GlobalAppState appState = (GlobalAppState) context.getApplicationContext();
        if (appState.isLoggingEnabled)
            appState.queue.enqueue(logging(context, errorType, error));

    }

    /**
     * Add log to queue
     *
     * @param context,errorType and error string
     */
    public static String DecryptedBeneficiary(Context context, String encryptedString) {
        SharedPreferences prefs = context.getSharedPreferences("FPS", Context.MODE_PRIVATE);
        String key = prefs.getString("keyEncrypt", "");
        Log.e("Error", key + ":" + encryptedString);
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        BouncyCastleProvider bouncy = new BouncyCastleProvider();
        encryptor.setProvider(bouncy);
        encryptor.setAlgorithm("PBEWITHSHA-256AND256BITAES-CBC-BC");
        encryptor.setPassword(key);
        return encryptor.decrypt(encryptedString);


    }
}
