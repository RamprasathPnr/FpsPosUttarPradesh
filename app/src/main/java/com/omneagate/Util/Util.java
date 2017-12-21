package com.omneagate.Util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import com.mantra.mfs100.MFS100;
import com.omneagate.DTO.FindingCriteriaDto;
import com.omneagate.DTO.LoggingDto;
import com.omneagate.DTO.MessageDto;
import com.omneagate.DTO.POSStockAdjustmentDto;
import com.omneagate.UndoBar.UndoBar;
import com.omneagate.activity.BaseActivity;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Utility class for entire application
 */
public class Util {

    public static MFS100 mfs100 = null;

    public static String message = null;

    public static String deviceSerialNo;
//    public static String serverUrl = null;

    public static int oneTotal;
    public static int oneHalfTotal;
    public static int twoTotal;
    public static int twoHalfTotal;
    public static int threeTotal;
    public static int totalTotal;


    //
    public static boolean needUidmMsking = false;

    public static boolean needSpeakOutForFingerprint= true;

    public static boolean needAutoUpgrade = true;

    public static boolean allowImmediateSale =true;

    public static boolean needInternalClock = true;

    public static boolean needAadhaarAuth2 = true;

    public static List<POSStockAdjustmentDto> ackAdjustmentList;

    public static FindingCriteriaDto findingCriteriaDto = new FindingCriteriaDto();

//    public static FindingCriteriaDto findingCriteriaDto = new FindingCriteriaDto();
//    public static String fingerPrintAadhar = "";

    //simple messageBar for FPS
    public static void messageBar(Activity activity, String message) {
        if (StringUtils.isEmpty(message)) {
            message = "Internal Error";
        }
        UndoBar undoBar = new UndoBar.Builder(activity)//

                .setMessage(message)//
                .setStyle(UndoBar.Style.KITKAT)
                .setAlignParentBottom(true)
                .create();
        undoBar.show();

    }

    public static void setTamilText(TextView textName, int id) {
        if (GlobalAppState.language.equalsIgnoreCase("hi")) {
//            Typeface tfBamini = Typeface.createFromAsset(ct.getAssets(), "fonts/Bamini.ttf");
//            textName.setTypeface(tfBamini, Typeface.BOLD);
//            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, ct.getString(id)));
            textName.setText(BaseActivity.globalContext.getString(id));
        } else {
            textName.setText(BaseActivity.globalContext.getString(id));
        }
    }

    public static void setTamilText(TextView textName, String text) {
        if (GlobalAppState.language.equalsIgnoreCase("hi")) {
            /*Typeface tfBamini = Typeface.createFromAsset(getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini);
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, text));*/
            textName.setText(text);
        } else {
            textName.setText(text);
        }
    }

    public static String maskAadhaarNumber(String uid,String mask){
        int index = 0;
        StringBuilder maskedNumber = new StringBuilder();
        for (int i = 0; i < mask.length(); i++) {
            char c = mask.charAt(i);
            if (c == '#') {
                maskedNumber.append(uid.charAt(index));
                index++;
            } else if (c == 'X') {
                maskedNumber.append(c);
                index++;
            } else {
                maskedNumber.append(c);
            }
        }
      return maskedNumber.toString();
    }

    /**
     * Registration store in local preference
     *
     * @param context context passing
     */
    public static void storePreferenceRegister(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("FPS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("register", true);
        editor.apply();
    }

    /**
     * Registration store in local preference
     *
     * @param context context passing
     */
    public static void storePreferenceApproved(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("FPS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("register", true);
        editor.putBoolean("approved", true);
        editor.apply();
    }

    public static String messageSelection(MessageDto messages) {
        String errorMessage = BaseActivity.globalContext.getString(R.string.genericDatabaseError);
        if(messages != null) {
            errorMessage = messages.getDescription();
            if (GlobalAppState.language.equalsIgnoreCase("hi")) {
                errorMessage = messages.getLocalDescription();
            }
        }
        return errorMessage;
    }

    public static String getTransactionId(Context context) {
        Date todayDate = new Date();
        SimpleDateFormat toDate = new SimpleDateFormat("MMyy", Locale.getDefault());
        String maxId = FPSDBHelper.getInstance(context).lastBillToday();
        Util.LoggingQueue(context, "Util", ">>>getTransactionId() called  maxId-> " +maxId);
        String transactionId = toDate.format(todayDate);
        if (StringUtils.isNotEmpty(maxId) && maxId != null) {
            maxId = StringUtils.substring(maxId, 4);
            Long userTransactionId = Long.parseLong(maxId);
            userTransactionId = userTransactionId + 1;
            String billNumber = Long.toString(userTransactionId);
            if (billNumber.length() <= 6) {
                DecimalFormat formatter = new DecimalFormat("000000");
                billNumber = formatter.format(userTransactionId);
            }
            transactionId = transactionId + billNumber;
        } else {
            transactionId = transactionId + "000001";
        }
        Log.e("transactionId ", transactionId);
        return transactionId;
    }

    public static String getInspectionReportTransactionId(Context context) {
        Date todayDate = new Date();
        SimpleDateFormat toDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String maxId = FPSDBHelper.getInstance(context).lastInspectionReportToday();
        Util.LoggingQueue(context, "Util", ">>>getTransactionId() called  maxId-> " +maxId);
        String transactionId = toDate.format(todayDate);
        if (StringUtils.isNotEmpty(maxId) && maxId != null) {
            maxId = StringUtils.substring(maxId, 14);
            Long userTransactionId = Long.parseLong(maxId);
            userTransactionId = userTransactionId + 1;
            String billNumber = Long.toString(userTransactionId);
            if (billNumber.length() <= 6) {
                DecimalFormat formatter = new DecimalFormat("000000");
                billNumber = formatter.format(userTransactionId);
            }
            transactionId = transactionId + billNumber;
        } else {
            transactionId = transactionId + "000001";
        }
        Log.e("transactionId ", transactionId);
        return transactionId;
    }

    public static String getInspectionStockTransactionId(Context context) {
        Date todayDate = new Date();
        SimpleDateFormat toDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String maxId = FPSDBHelper.getInstance(context).lastInspectionStockToday();
        Util.LoggingQueue(context, "Util", ">>>getTransactionId() called  maxId-> " +maxId);
        String transactionId = toDate.format(todayDate);
        if (StringUtils.isNotEmpty(maxId) && maxId != null) {
            maxId = StringUtils.substring(maxId, 14);
            Long userTransactionId = Long.parseLong(maxId);
            userTransactionId = userTransactionId + 1;
            String billNumber = Long.toString(userTransactionId);
            if (billNumber.length() <= 6) {
                DecimalFormat formatter = new DecimalFormat("000000");
                billNumber = formatter.format(userTransactionId);
            }
            transactionId = transactionId + billNumber;
        } else {
            transactionId = transactionId + "000001";
        }
        Log.e("transactionId ", transactionId);
        return transactionId;
    }


    /**
     * Change language in android
     *
     * @param languageCode for language selection,context for context passing
     */
    public static void changeLanguage(Context context, String languageCode) {
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(languageCode);
        res.updateConfiguration(conf, dm);
        FPSDBHelper.getInstance(context).updateMaserData("language", languageCode);
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
        try {
            GlobalAppState appState = (GlobalAppState) context.getApplicationContext();
            Log.e(errorType, error);
            if (GlobalAppState.isLoggingEnabled && NetworkUtil.getConnectivityStatus(context) != 0)
                appState.queue.enqueue(logging(context, errorType, error));
        }
        catch(Exception e) {}
    }

    /**
     * Add log to queue
     *
     * @param context,errorType and error string
     */
    public static String DecryptedBeneficiary(Context context, String encryptedString) {
        SharedPreferences prefs = context.getSharedPreferences("FPS", Context.MODE_PRIVATE);
        String key = prefs.getString("keyEncrypt", "");
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        Log.e("Key", encryptedString);
        BouncyCastleProvider bouncy = new BouncyCastleProvider();
        encryptor.setProvider(bouncy);
        encryptor.setAlgorithm("PBEWITHSHA-256AND256BITAES-CBC-BC");
        encryptor.setPassword(key);
        String encrypted = encryptor.decrypt(encryptedString);
        encrypted = StringUtils.substring(encrypted, 0, encrypted.length() - 4) + "****";
        return encrypted;

    }

    public static String DecryptPassword(String encryptedString) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        BouncyCastleProvider bouncy = new BouncyCastleProvider();
        encryptor.setProvider(bouncy);
        encryptor.setAlgorithm("PBEWITHSHA-256AND256BITAES-CBC-BC");
        encryptor.setPassword("fpspos");
        return encryptor.decrypt(encryptedString);
    }

    public static String EncryptPassword(String password) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setProvider(new BouncyCastleProvider());
        encryptor.setAlgorithm("PBEWITHSHA-256AND256BITAES-CBC-BC");
        encryptor.setPassword("fpspos");
        return encryptor.encrypt(password);
    }

    public static String priceRoundOffFormat(Double priceValue) {
//        Log.e("util", "before rounding priceValue..." + priceValue);
        BigDecimal currQuantity = new BigDecimal(priceValue);
        currQuantity.setScale(2, RoundingMode.HALF_EVEN);
        priceValue = (double) Math.round(priceValue * 100);
        priceValue = priceValue / 100;
        NumberFormat formatter = new DecimalFormat("#0.00");
        String pr = formatter.format(priceValue);
//        Log.e("util", "after rounding priceValue..." + pr);
        return pr;
    }

    public static String quantityRoundOffFormat(Double quantityValue) {
//        Log.e("util", "before rounding quantityValue..." + quantityValue);
        BigDecimal currQuantity = new BigDecimal(quantityValue);
        currQuantity.setScale(3,RoundingMode.HALF_EVEN);
        quantityValue = (double) Math.round(quantityValue * 1000);
        quantityValue = quantityValue / 1000;
        NumberFormat formatter = new DecimalFormat("#0.000");
        String qty = formatter.format(quantityValue);
//        Log.e("util", "after rounding quantityValue..." + qty);
        return qty;
    }

    public static String latLngRoundOffFormat(Double latLngValue) {
        Log.e("util", "latLngRoundOffFormat() called latLngValue = " + latLngValue);


        BigDecimal currQuantity = new BigDecimal(latLngValue);
        currQuantity.setScale(3,RoundingMode.HALF_EVEN);
        latLngValue = (double) Math.round(latLngValue * 1000);
        latLngValue = latLngValue / 1000;
        NumberFormat formatter = new DecimalFormat("#0.0000");
        String LatLng = formatter.format(latLngValue);

        Log.e("util", "latLngRoundOffFormat() called LatLng = " + LatLng);

//        Log.e("util", "after rounding quantityValue..." + qty);
        return LatLng;
    }
    public static String recupAdresseMAC(WifiManager wifiMan) {
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String ret = null;
            try {
                ret = getAdressMacByInterface();
                if (ret != null) {
                    return ret;
                } else {
                    ret = getAddressMacByFile(wifiMan);
                    return ret;
                }
            } catch (IOException e) {
                Log.e("MobileAccess", "Erreur lecture propriete Adresse MAC");
            } catch (Exception e) {
                Log.e("MobileAcces", "Erreur lecture propriete Adresse MAC ");
            }
        } else {
            return wifiInf.getMacAddress();
        }
        return "";
    }
    private static final String fileAddressMac = "/sys/class/net/wlan0/address";
    private static String getAdressMacByInterface() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (nif.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }
                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02X:", b));
                    }
                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            }
        } catch (Exception e) {
            Log.e("MobileAcces", "Erreur lecture propriete Adresse MAC ");
        }
        return null;
    }

    private static String getAddressMacByFile(WifiManager wifiMan) throws Exception {
        String ret;
        int wifiState = wifiMan.getWifiState();
        wifiMan.setWifiEnabled(true);
        File fl = new File(fileAddressMac);
        FileInputStream fin = new FileInputStream(fl);
        StringBuilder builder = new StringBuilder();
        int ch;
        while ((ch = fin.read()) != -1) {
            builder.append((char) ch);
        }
        ret = builder.toString();
        fin.close();
        boolean enabled = WifiManager.WIFI_STATE_ENABLED == wifiState;
        wifiMan.setWifiEnabled(enabled);
        return ret;
    }
}
