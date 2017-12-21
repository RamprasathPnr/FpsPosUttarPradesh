package com.omneagate.activity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.StatisticsDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.LocationId;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class
        StatisticsActivity extends BaseActivity {
    StatisticsDto statisticsDto;
    int scale, health, level, plugged, status, temperature, voltage;
    String technology;
    boolean present;
    private int batteryLevel = 0;
    //Broadcast receiver for battery
    private final BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            context.unregisterReceiver(this);
            int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            present = intent.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT);
            status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
            technology = intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
            temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
            voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
            if (currentLevel >= 0 && scale > 0) {
                batteryLevel = (currentLevel * 100) / scale;
                Log.e("Heart beat", "Current:" + currentLevel + "::" + "scale:" + scale + "::" + batteryLevel);
            }
            statisticsDto.setScale(scale);
            statisticsDto.setHealth(health);
            statisticsDto.setLevel(level);
            statisticsDto.setPlugged(plugged);
            statisticsDto.setStatus(status);
            statisticsDto.setTemperature(temperature);
            statisticsDto.setVoltage(voltage);
            statisticsDto.setTechnology(technology);
            statisticsDto.setPresent(present);
            statisticsDto.setBatteryLevel(batteryLevel);
            changeData();
            Log.e("statisticsDto", statisticsDto.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
        configureInitialPage();
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
    }

    private void configureInitialPage() {
        try {
            setUpPopUpPageForAdmin();
            /** 11-07-2016
             * MSFixes
             * Added to fix tamil content for system statistics
             *
             */
            Util.setTamilText((TextView) findViewById(R.id.deviceNumLabel), R.string.device_number);
            Util.setTamilText((TextView) findViewById(R.id.healthLabel), R.string.health);
            Util.setTamilText((TextView) findViewById(R.id.scaleLabel), R.string.scale);
            Util.setTamilText((TextView) findViewById(R.id.levelLabel), R.string.level);
            Util.setTamilText((TextView) findViewById(R.id.pluggedLabel), R.string.plugged);
            Util.setTamilText((TextView) findViewById(R.id.technologyLabel), R.string.technology);
            Util.setTamilText((TextView) findViewById(R.id.temperatureLabel), R.string.temparature);
            Util.setTamilText((TextView) findViewById(R.id.voltageLabel), R.string.voltage);
            Util.setTamilText((TextView) findViewById(R.id.presentLabel), R.string.present);
            Util.setTamilText((TextView) findViewById(R.id.latlongLabel), R.string.latlong);
            Util.setTamilText((TextView) findViewById(R.id.noOfBeneficiaryLabel), R.string.noofBeneficiary);
            Util.setTamilText((TextView) findViewById(R.id.versionNumberLabel), R.string.versionNumber);
            Util.setTamilText((TextView) findViewById(R.id.noofUnsyncBillLabel), R.string.noofunsyncbill);
            Util.setTamilText((TextView) findViewById(R.id.unsyncInwardLabel), R.string.unsyncedInward);
            Util.setTamilText((TextView) findViewById(R.id.versionNameLabel), R.string.versionName);
            Util.setTamilText((TextView) findViewById(R.id.unsyncAdjustmentLabel), R.string.unsyncAdjustment);
            Util.setTamilText((TextView) findViewById(R.id.cpuUtilizatonLabel), R.string.cpuUtilization);
            Util.setTamilText((TextView) findViewById(R.id.appInstalledTimeLabel), R.string.appInstalledTime);
            Util.setTamilText((TextView) findViewById(R.id.freeMemoryLabel), R.string.freeMemory);
            Util.setTamilText((TextView) findViewById(R.id.appUpdatedTimeLabel), R.string.appUpdatedTime);
            Util.setTamilText((TextView) findViewById(R.id.totalMemoryLabel), R.string.totalMemory);
            Util.setTamilText((TextView) findViewById(R.id.statusLabel), R.string.status);
            Util.setTamilText((TextView) findViewById(R.id.memoryUsedLabel), R.string.memoryUsed);
            Util.setTamilText((TextView) findViewById(R.id.hardDiskSizeLabel), R.string.harddiskSize);
            Util.setTamilText((TextView) findViewById(R.id.networkTypeLabel), R.string.networkType);
            Util.setTamilText((TextView) findViewById(R.id.simIdLabel), R.string.simId);
            Util.setTamilText((TextView) findViewById(R.id.unsyncLoginCountLabel), R.string.unsyncLogin);
            // label setting by ramesh
            Util.setTamilText((TextView) findViewById(R.id.title_entitlementMasterRulesCount), R.string.title_entitlementMasterRulesCount);
//            Util.setTamilText((TextView) findViewById(R.id.title_aadhaarSeedingCount), R.string.title_aadhaarSeedingCount);
            Util.setTamilText((TextView) findViewById(R.id.title_regionBasedRulesCount), R.string.title_regionBasedRulesCount);
            Util.setTamilText((TextView) findViewById(R.id.title_personBasedRulesCount), R.string.title_personBasedRulesCount);
            Util.setTamilText((TextView) findViewById(R.id.title_specialBasedRulesCount), R.string.title_specialBasedRulesCount);
            Util.setTamilText((TextView) findViewById(R.id.title_beneficiaryMemberCount), R.string.title_beneficiaryMemberCount);
            Util.setTamilText((TextView) findViewById(R.id.title_nfsaPosDataCount), R.string.title_nfsaPosDataCount);

/*
            if (com.omneagate.activity.GlobalAppState.language.equalsIgnoreCase("ta")) {
                ((TextView) findViewById(R.id.noofUnsyncBillLabel)).setTextSize(20);
            }else{

            }*/
            statisticsDto = new StatisticsDto();
            Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.statistics);
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            statisticsDto.setVersionNum(pInfo.versionCode);
            statisticsDto.setApkInstalledTime(pInfo.firstInstallTime);
            statisticsDto.setLastUpdatedTime(pInfo.lastUpdateTime);
            statisticsDto.setVersionName(pInfo.versionName);
            long totalFreeMemory = getAvailableInternalMemorySize() + getAvailableExternalMemorySize();
            statisticsDto.setHardDiskSizeFree(formatSize(totalFreeMemory));
            statisticsDto.setUserId(String.valueOf(SessionId.getInstance().getUserId()));
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                statisticsDto.setSimId(telephonyManager.getSimSerialNumber());
            } catch (Exception e) {
            }
            try {
                statisticsDto.setDeviceNum((Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)).toUpperCase());
            } catch (Exception e) {
            }
            try {
                statisticsDto.setBeneficiaryCount(FPSDBHelper.getInstance(this).getBeneficiaryCount());
            } catch (Exception e) {
            }
            try {
                statisticsDto.setRegistrationCount(FPSDBHelper.getInstance(this).getBeneficiaryUnSyncCount());
            } catch (Exception e) {
            }
            try {
                statisticsDto.setUnSyncBillCount(FPSDBHelper.getInstance(this).getBillUnSyncCount());
            } catch (Exception e) {
            }
            try {
                statisticsDto.setUnsyncInwardCount(FPSDBHelper.getInstance(this).getInwardUnSyncCount());
            } catch (Exception e) {
            }
            try {
                statisticsDto.setUnsyncAdjustmentCount(FPSDBHelper.getInstance(this).getAdjustmentUnSyncCount());
            } catch (Exception e) {
            }
//            starting
            try {
                statisticsDto.setEntitlementMasterRulesCount(FPSDBHelper.getInstance(this).getEntitlementRulesCount());
            } catch (Exception e) {
            }
            try {
                statisticsDto.setPersonBasedRulesCount(FPSDBHelper.getInstance(this).getPersonRulesCount());
            } catch (Exception e) {
            }
            try {
                statisticsDto.setRegionBasedRulesCount(FPSDBHelper.getInstance(this).getRegionRulesCount());
            } catch (Exception e) {
            }
            try {
                statisticsDto.setSpecialBasedRulesCount(FPSDBHelper.getInstance(this).getSpecialRulesCount());
            } catch (Exception e) {
            }
            /*try {
                statisticsDto.setAadhaarSeedingCount(FPSDBHelper.getInstance(this).getMembersAadhaarTableCount());
            } catch (Exception e) {
            }*/
            try {
                statisticsDto.setBeneficiaryMemberCount(FPSDBHelper.getInstance(this).getBeneficiaryMemberTableCount());
            } catch (Exception e) {
            }
            try {
                statisticsDto.setNfsaPosDataCount(FPSDBHelper.getInstance(this).getNfscaTableCount());
            } catch (Exception e) {
            }
            statisticsDto.setCpuUtilisation(String.valueOf(readUsage()));
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(mi);
            long availableMegs = mi.availMem / 1048576L;
            long totalMegs = mi.totalMem / 1048576L;
            statisticsDto.setMemoryRemaining(String.valueOf(availableMegs));
            statisticsDto.setTotalMemory(String.valueOf(totalMegs));
            statisticsDto.setMemoryUsed(String.valueOf(totalMegs - availableMegs));
            statisticsDto.setBatteryLevel(batteryLevel);
            statisticsDto.setLatitude(LocationId.getInstance().getLatitude());
            statisticsDto.setLongtitude(LocationId.getInstance().getLongitude());
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null)
                statisticsDto.setNetworkInfo(cm.getActiveNetworkInfo().getTypeName());
            Log.e("Statics", statisticsDto.toString());
        } catch (Exception e) {
            Log.e("statistics error", e.toString(), e);
        } finally {
            setData();
            ((ImageView) findViewById(R.id.imageViewBack)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(com.omneagate.activity.StatisticsActivity.this, AdminActivity.class));
                    finish();
                }
            });
        }
    }

    private void setData() {
        ((TextView) findViewById(R.id.deviceNum)).setText(statisticsDto.getDeviceNum());
        ((TextView) findViewById(R.id.latitudeDevice)).setText(statisticsDto.getLatitude() + " / " + statisticsDto.getLongtitude());
//        ((TextView) findViewById(R.id.longitudeData)).setText(statisticsDto.getLongtitude());
        ((TextView) findViewById(R.id.cpuUtil)).setText(statisticsDto.getCpuUtilisation());
        ((TextView) findViewById(R.id.versionNo)).setText(statisticsDto.getVersionNum() + "");
        ((TextView) findViewById(R.id.noOfBeneficiary)).setText(statisticsDto.getBeneficiaryCount() + "");
        ((TextView) findViewById(R.id.unSyncBill)).setText(statisticsDto.getUnSyncBillCount() + "");
        ((TextView) findViewById(R.id.versionName)).setText(statisticsDto.getVersionName() + "");
//        ((TextView) findViewById(R.id.regCount)).setText(statisticsDto.getRegistrationCount() + "");
        ((TextView) findViewById(R.id.unsyncedInward)).setText(statisticsDto.getUnsyncInwardCount() + "");
        ((TextView) findViewById(R.id.unsyncedAdjustment)).setText(statisticsDto.getUnsyncAdjustmentCount() + "");
        try {
            String SimID = statisticsDto.getSimId();
            if (SimID != null && !SimID.isEmpty())
                ((TextView) findViewById(R.id.simId)).setText(SimID + "");
            else
                ((TextView) findViewById(R.id.simId)).setText("");
            Util.LoggingQueue(this, "StatisticsActivity", " SIM Details = " + SimID);
        } catch (Exception e) {
        }
        ((TextView) findViewById(R.id.memUsed)).setText(statisticsDto.getMemoryUsed() + " MB");
        ((TextView) findViewById(R.id.memoryRemain)).setText(statisticsDto.getMemoryRemaining() + " MB");
        ((TextView) findViewById(R.id.totMemory)).setText(statisticsDto.getTotalMemory() + " MB");
        ((TextView) findViewById(R.id.hardDiskSize)).setText(statisticsDto.getHardDiskSizeFree());
        ((TextView) findViewById(R.id.networkType)).setText(statisticsDto.getNetworkInfo());
        ((TextView) findViewById(R.id.unSyncLoginCount)).setText(FPSDBHelper.getInstance(this).getAllLoginHistory().size() + "");
        SimpleDateFormat dateApp = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.getDefault());
        ((TextView) findViewById(R.id.appInstalledTime)).setText(dateApp.format(new Date(statisticsDto.getApkInstalledTime())));
        ((TextView) findViewById(R.id.appUpdateTime)).setText(dateApp.format(new Date(statisticsDto.getLastUpdatedTime())));
        // New setting by ramesh
        ((TextView) findViewById(R.id.entitlementMasterRulesCount)).setText(""+statisticsDto.getEntitlementMasterRulesCount());
        ((TextView) findViewById(R.id.personBasedRulesCount)).setText(""+statisticsDto.getPersonBasedRulesCount());
        ((TextView) findViewById(R.id.regionBasedRulesCount)).setText(""+statisticsDto.getRegionBasedRulesCount());
//        ((TextView) findViewById(R.id.aadhaarSeedingCount)).setText(""+statisticsDto.getAadhaarSeedingCount());
        ((TextView) findViewById(R.id.specialBasedRulesCount)).setText(""+statisticsDto.getSpecialBasedRulesCount());
        ((TextView) findViewById(R.id.beneficiaryMemberCount)).setText(""+statisticsDto.getBeneficiaryMemberCount());
        ((TextView) findViewById(R.id.nfsaPosDataCount)).setText(""+statisticsDto.getNfsaPosDataCount());
    }

    private void changeData() {
        ((TextView) findViewById(R.id.deviceScale)).setText(statisticsDto.getScale() + "");
//        ((TextView) findViewById(R.id.batteryLevel)).setText(statisticsDto.getBatteryLevel() + "");
        ((TextView) findViewById(R.id.batteryPlugged)).setText(statisticsDto.getPlugged() + "");
        ((TextView) findViewById(R.id.batteryTech)).setText(statisticsDto.getTechnology());
        ((TextView) findViewById(R.id.batteryVoltage)).setText(statisticsDto.getVoltage() + "V");
        ((TextView) findViewById(R.id.batteryHealth)).setText(statisticsDto.getHealth() + "");
        ((TextView) findViewById(R.id.batteryLvl)).setText(statisticsDto.getLevel() + "");
        ((TextView) findViewById(R.id.batteryStatus)).setText(statisticsDto.getStatus() + "");
        ((TextView) findViewById(R.id.batteryTemp)).setText(statisticsDto.getTemperature() + " C");
        ((TextView) findViewById(R.id.batteryPresent)).setText(statisticsDto.isPresent() + "");
    }

    private float readUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();
            String[] toks = load.split(" ");
            long idle1 = Long.parseLong(toks[5]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
            try {
                Thread.sleep(360);
            } catch (Exception e) {
            }
            reader.seek(0);
            load = reader.readLine();
            reader.close();
            toks = load.split(" ");
            long idle2 = Long.parseLong(toks[5]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
            return (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    private boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    private long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return availableBlocks * blockSize;
    }

    private long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getAvailableBlocksLong();
            long availableBlocks = stat.getBlockSizeLong();
            return availableBlocks * blockSize;
        } else {
            return 0;
        }
    }

    private String formatSize(long size) {
        String suffix = null;
        if (size >= 1024) {
            suffix = " KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = " MB";
                size /= 1024;
            }
        }
        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));
        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }
        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    @Override
    public void onBackPressed() {
//        unregisterReceiver(batteryLevelReceiver);
        startActivity(new Intent(this, AdminActivity.class));
        finish();
    }

    public void setTamilText(TextView textName, int id) {
        // Log.e("BaseActivity", "Util.setTamilText  id passing, "+getString(id));
        if (com.omneagate.activity.GlobalAppState.language.equalsIgnoreCase("ta")) {
            /*Typeface tfBamini = Typeface.createFromAsset(getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini, Typeface.BOLD);
            // textName.setText(getString(id));
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, getString(id)));*/
            textName.setText(getString(id));
        } else {
            textName.setText(getString(id));
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }
        super.onDestroy();
    }
}
