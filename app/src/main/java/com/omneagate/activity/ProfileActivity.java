package com.omneagate.activity;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FpsStoreDto;
import com.omneagate.DTO.LoginResponseDto;
import com.omneagate.DTO.POSOperatingHoursDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ProfileActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_profile);
        Util.LoggingQueue(this, "ProfileActivity ", "onCreate() called ");


        appState = (GlobalAppState) getApplication();
        setUpDashBoard();


    }


    public void getPOSOperatingHours() {

        try {
            Util.LoggingQueue(this, "ProfileActivity ", "POSOperatingHoursDto start calculate time difference");

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dayDateFormat = new SimpleDateFormat("EEE");
            SimpleDateFormat currentHoursDateFormat = new SimpleDateFormat("HH:mm:ss");

            String currentHoursStr = "" + currentHoursDateFormat.format(calendar.getTime());
            String currentDayStr = "" + dayDateFormat.format(calendar.getTime());

            POSOperatingHoursDto posOperatingHoursDto = FPSDBHelper.getInstance(this).getPOSOperatingHoursForToday(currentDayStr);

            String firstSessionOpeningTimeStr = posOperatingHoursDto.getFirstSessionOpeningTime();
            String firstSessionClosingTimeStr = posOperatingHoursDto.getFirstSessionClosingTime();
            String secondSessionOpeningTimeStr = posOperatingHoursDto.getSecondSessionOpeningTime();
            String secondSessionClosingTimeStr = posOperatingHoursDto.getSecondSessionClosingTime();
            Util.LoggingQueue(this, "currentHoursStr ", "" + currentHoursStr);
            Util.LoggingQueue(this, "firstSessionOpeningTimeStr ", "" + firstSessionOpeningTimeStr);


           /* Date currentHoursDate = currentHoursDateFormat.parse(currentHoursStr);
            Date firstSessionOpeningTimeDate = currentHoursDateFormat.parse(firstSessionOpeningTimeStr);
            long difference = currentHoursDate.getTime() - firstSessionOpeningTimeDate.getTime();

            long diffSeconds = difference / 1000 % 60;
            long diffMinutes = difference / (60 * 1000) % 60;
            long diffHours = difference / (60 * 60 * 1000) % 24;
            String differnceStr = "diffHours = " + diffHours + " & diffMinutes = " + diffMinutes + " & diffSeconds = " + diffSeconds;


            Util.LoggingQueue(this, "ProfileActivity ", "Time difference = " + differnceStr);*/
            //isInRange(Integer.parseInt(currentHoursStr.replace(":","")),9*100,12*100);

            String[] openingHour1 = StringUtils.split(firstSessionOpeningTimeStr, ":");
            String[] closingHour1 = StringUtils.split(firstSessionClosingTimeStr, ":");
            String opHours1 = " AM";
            int opens1 = Integer.parseInt(openingHour1[0]);
            if (opens1 > 12) {
                opHours1 = " PM";
                opens1 = opens1 % 12;
            }

            String closeHours1 = " AM";
            int closes1 = Integer.parseInt(closingHour1[0]);
            if (closes1 > 12) {
                closeHours1 = " PM";
                closes1 = closes1 % 12;
            }
            String operationHour1 = opens1 + ":" + openingHour1[1] + opHours1 + " - " + closes1 + ":" + closingHour1[1] + closeHours1;
            ((TextView) findViewById(R.id.morning_session_value)).setText("" + operationHour1);


            String[] openingHour = StringUtils.split(secondSessionOpeningTimeStr, ":");
            String[] closingHour = StringUtils.split(secondSessionClosingTimeStr, ":");
            String opHours = " AM";
            int opens = Integer.parseInt(openingHour[0]);
            if (opens > 12) {
                opHours = " PM";
                opens = opens % 12;
            }

            String closeHours = " AM";
            int closes = Integer.parseInt(closingHour[0]);
            if (closes > 12) {
                closeHours = " PM";
                closes = closes % 12;
            }
            String operationHour = opens + ":" + openingHour[1] + opHours + " - " + closes + ":" + closingHour[1] + closeHours;

            ((TextView) findViewById(R.id.after_noon_session_value)).setText("" + operationHour);


            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                if (telephonyManager.getSimSerialNumber() != null)
                    ((TextView) findViewById(R.id.sim_id_value)).setText("" + telephonyManager.getSimSerialNumber());
                else
                    Util.LoggingQueue(this, "No Sim Found", "");
                //statisticsDto.setSimId(telephonyManager.getSimSerialNumber());
            } catch (Exception e) {
                Util.LoggingQueue(this, "getSimSerialNumber Exception  ", "" + e.toString());


            }

        } catch (Exception e) {
            Util.LoggingQueue(this, "Time Compare Exception  ", "" + e.toString());


        }

    }

    /**
     * Used to set the dashboard page
     * <p>
     * user name and onCLickListeners are in this function
     */
    private void setUpDashBoard() {

        Util.LoggingQueue(this, "Profile page", "Profile page opened");
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.fps_profile);


        Util.setTamilText((TextView) findViewById(R.id.fps_code_label), R.string.fps_code1);
        Util.setTamilText((TextView) findViewById(R.id.store_type_label), R.string.store_type);
        Util.setTamilText((TextView) findViewById(R.id.agency_label), R.string.agency_name);
        Util.setTamilText((TextView) findViewById(R.id.location_label), R.string.location);
        Util.setTamilText((TextView) findViewById(R.id.morning_session_label), R.string.morning_session);
        Util.setTamilText((TextView) findViewById(R.id.after_noon_session_label), R.string.after_noon_session);
        Util.setTamilText((TextView) findViewById(R.id.sim_id_label), R.string.sim_number);


        Util.setTamilText((TextView) findViewById(R.id.button_cancel), R.string.close);
        try {
            Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "setUpDashBoard() LoginData...getFpsStore " + LoginData.getInstance().getLoginData().getUserDetailDto().getFpsStore());

            FpsStoreDto fpsStore = LoginData.getInstance().getLoginData().getUserDetailDto().getFpsStore();

            Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "setUpDashBoard() called FpsStoreDto " +

                    fpsStore);


            if (fpsStore != null) {

                try {

                    Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "setUpDashBoard() get All values from FpsStoreDto  ");


                    String fpsCode = "";
                    String category = "full time";
                    String location;
                    String agency_name;
                    String simNumber;


                    if (StringUtils.isNotEmpty(SessionId.getInstance().getFpsCode()) && SessionId.getInstance().getFpsCode() != null) {
                        Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "setUpDashBoard() SessionId.getFpsCode()  " + SessionId.getInstance().getFpsCode());

                        fpsCode = SessionId.getInstance().getFpsCode().toUpperCase();
                    }

                    if (fpsStore.getCode() != null) {
                        Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "setUpDashBoard() I FpsStoreDto.getCode()  " + fpsStore.getCode());

                        ((TextView) findViewById(R.id.fps_code_value)).setText(fpsStore.getCode() + " / " + fpsCode);

                    } else {

                        Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "setUpDashBoard() II SessionId.getFpsCode()  " + fpsCode);

                        ((TextView) findViewById(R.id.fps_code_value)).setText("" + fpsCode);

                    }


                    if (fpsStore.getFpsCategoryType() != null && StringUtils.isNotEmpty(fpsStore.getFpsCategoryType())) {
                        category = fpsStore.getFpsCategoryType().toString();
                        Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "setUpDashBoard() FpsStoreDto.getFpsCategoryType()  " + fpsStore.getFpsCategoryType());
                        category = StringUtils.replace(category, "_", " ");
                        ((TextView) findViewById(R.id.store_type_value)).setText(category.toUpperCase());

                    }else{


                        Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "NULL FpsStoreDto.getFpsCategoryType()  " + fpsStore.getFpsCategoryType());
                        LoginResponseDto loginResponseDto = FPSDBHelper.getInstance(ProfileActivity.this).getUserDetails(SessionId.getInstance().getUserId());
                        Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "NULL LoginResponseDto =  " + loginResponseDto);


                        if (loginResponseDto.getUserDetailDto().getFpsStore().getFpsCategoryType() != null && StringUtils.isNotEmpty(loginResponseDto.getUserDetailDto().getFpsStore().getFpsCategoryType())) {
                            category = loginResponseDto.getUserDetailDto().getFpsStore().getFpsCategoryType().toString();
                            Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "NULL LoginResponseDto.getFpsCategoryType()  " + loginResponseDto.getUserDetailDto().getFpsStore().getFpsCategoryType());
                            category = StringUtils.replace(category, "_", " ");
                            ((TextView) findViewById(R.id.store_type_value)).setText(category.toUpperCase());

                        }

                    }
                    if (fpsStore.getVillageName() != null && StringUtils.isNotEmpty(fpsStore.getVillageName())) {
                        location = fpsStore.getVillageName();
                        Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "setUpDashBoard() FpsStoreDto.getVillageName()  " + fpsStore.getVillageName());
                        ((TextView) findViewById(R.id.location_value)).setText(location.toUpperCase());
                    }else{
                        Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "NULL FpsStoreDto.getVillageName()  " + fpsStore.getVillageName());
                        LoginResponseDto loginResponseDto = FPSDBHelper.getInstance(ProfileActivity.this).getUserDetails(SessionId.getInstance().getUserId());
                        Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "NULL LoginResponseDto =  " + loginResponseDto);


                        if (loginResponseDto.getUserDetailDto().getFpsStore().getVillageName() != null &&
                                StringUtils.isNotEmpty(loginResponseDto.getUserDetailDto().getFpsStore().getVillageName())) {
                            String village = loginResponseDto.getUserDetailDto().getFpsStore().getVillageName();
                            Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "NULL LoginResponseDto.getVillageName()  " +
                                    loginResponseDto.getUserDetailDto().getFpsStore().getVillageName());
                            ((TextView) findViewById(R.id.location_value)).setText(village.toUpperCase());

                        }
                    }


                    if (fpsStore.getAgencyName() != null && StringUtils.isNotEmpty(fpsStore.getAgencyName())) {
                        agency_name = fpsStore.getAgencyName();
                        Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "setUpDashBoard() FpsStoreDto.getAgencyName()  " + fpsStore.getAgencyName());
                        ((TextView) findViewById(R.id.agency_value)).setText(agency_name.toUpperCase());
                    }else{
                        Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "NULL FpsStoreDto.getAgencyName()  " + fpsStore.getAgencyName());
                        LoginResponseDto loginResponseDto = FPSDBHelper.getInstance(ProfileActivity.this).getUserDetails(SessionId.getInstance().getUserId());
                        Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "NULL LoginResponseDto =  " + loginResponseDto);


                        if (loginResponseDto.getUserDetailDto().getFpsStore().getAgencyName() != null &&
                                StringUtils.isNotEmpty(loginResponseDto.getUserDetailDto().getFpsStore().getAgencyName())) {
                            String agency = loginResponseDto.getUserDetailDto().getFpsStore().getAgencyName();
                            Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "NULL LoginResponseDto.getAgencyName()  " +
                                    loginResponseDto.getUserDetailDto().getFpsStore().getAgencyName());
                            ((TextView) findViewById(R.id.location_value)).setText(agency.toUpperCase());

                        }
                    }

                    if (fpsStore.getDeviceSimNo() != null && StringUtils.isNotEmpty(fpsStore.getDeviceSimNo())) {
                        simNumber = fpsStore.getDeviceSimNo();
                        Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "setUpDashBoard() FpsStoreDto.getDeviceSimNo()  " + fpsStore.getDeviceSimNo());
                        ((TextView) findViewById(R.id.sim_id_value)).setText(simNumber.toUpperCase());
                    }else{
                        Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "NULL FpsStoreDto.getDeviceSimNo()  " + fpsStore.getDeviceSimNo());
                        LoginResponseDto loginResponseDto = FPSDBHelper.getInstance(ProfileActivity.this).getUserDetails(SessionId.getInstance().getUserId());
                        Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "NULL LoginResponseDto =  " + loginResponseDto);


                        if (loginResponseDto.getUserDetailDto().getFpsStore().getDeviceSimNo() != null &&
                                StringUtils.isNotEmpty(loginResponseDto.getUserDetailDto().getFpsStore().getDeviceSimNo())) {
                            String sim = loginResponseDto.getUserDetailDto().getFpsStore().getDeviceSimNo();
                            Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "NULL LoginResponseDto.getDeviceSimNo()  " +
                                    loginResponseDto.getUserDetailDto().getFpsStore().getDeviceSimNo());
                            ((TextView) findViewById(R.id.location_value)).setText(sim.toUpperCase());

                        }
                    }

                } catch (Exception e) {
                    Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "setUpDashBoard() FpsStoreDto  Exception =  " + e);

                }

            } else {


                try {
                    Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "setUpDashBoard() get All values from FPSDBHelper for SessionID =  " + SessionId.getInstance().getUserId());
                    LoginResponseDto loginResponseDto = FPSDBHelper.getInstance(ProfileActivity.this).getUserDetails(SessionId.getInstance().getUserId());
                    Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "setUpDashBoard() LoginResponseDto =  " + loginResponseDto);

                    //String entitlementClassification = loginResponseDto.getUserDetailDto().getFpsStore().getEntitlementClassification();

                    String fpsCode = "";
                    String category = "full time";
                    String location;
                    String agency_name;
                    String simNumber;


                    if (StringUtils.isNotEmpty(SessionId.getInstance().getFpsCode()) && SessionId.getInstance().getFpsCode() != null) {
                        Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "setUpDashBoard() LoginResponseDto SessionId.getFpsCode()  " + SessionId.getInstance().getFpsCode());

                        fpsCode = SessionId.getInstance().getFpsCode().toUpperCase();
                    }

                    if (loginResponseDto.getUserDetailDto().getFpsStore().getCode() != null) {
                        Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "setUpDashBoard() I LoginResponseDto.getCode()  " + loginResponseDto.getUserDetailDto().getFpsStore().getCode());

                        ((TextView) findViewById(R.id.fps_code_value)).setText(loginResponseDto.getUserDetailDto().getFpsStore().getCode() + " / " + fpsCode);

                    } else {

                        Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "setUpDashBoard() II LoginResponseDto SessionId.getFpsCode()  " + fpsCode);

                        ((TextView) findViewById(R.id.fps_code_value)).setText("" + fpsCode);

                    }


                    if (loginResponseDto.getUserDetailDto().getFpsStore().getFpsCategoryType() != null && StringUtils.isNotEmpty(loginResponseDto.getUserDetailDto().getFpsStore().getFpsCategoryType())) {
                        category = loginResponseDto.getUserDetailDto().getFpsStore().getFpsCategoryType().toString();
                        Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "setUpDashBoard() LoginResponseDto.getFpsCategoryType()  " + loginResponseDto.getUserDetailDto().getFpsStore().getFpsCategoryType());
                        category = StringUtils.replace(category, "_", " ");
                        ((TextView) findViewById(R.id.store_type_value)).setText(category.toUpperCase());

                    }
                    if (loginResponseDto.getUserDetailDto().getFpsStore().getVillageName() != null && StringUtils.isNotEmpty(loginResponseDto.getUserDetailDto().getFpsStore().getVillageName())) {
                        location = loginResponseDto.getUserDetailDto().getFpsStore().getVillageName();
                        Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "setUpDashBoard() LoginResponseDto.getVillageName()  " + loginResponseDto.getUserDetailDto().getFpsStore().getVillageName());
                        ((TextView) findViewById(R.id.location_value)).setText(location.toUpperCase());
                    }


                    if (loginResponseDto.getUserDetailDto().getFpsStore().getAgencyName() != null && StringUtils.isNotEmpty(loginResponseDto.getUserDetailDto().getFpsStore().getAgencyName())) {
                        agency_name = loginResponseDto.getUserDetailDto().getFpsStore().getAgencyName();
                        Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "setUpDashBoard() LoginResponseDto.getAgencyName()  " + loginResponseDto.getUserDetailDto().getFpsStore().getAgencyName());
                        ((TextView) findViewById(R.id.agency_value)).setText(agency_name.toUpperCase());
                    }

                    if (loginResponseDto.getUserDetailDto().getFpsStore().getDeviceSimNo() != null && StringUtils.isNotEmpty(loginResponseDto.getUserDetailDto().getFpsStore().getDeviceSimNo())) {
                        simNumber = loginResponseDto.getUserDetailDto().getFpsStore().getDeviceSimNo();
                        Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "setUpDashBoard() LoginResponseDto.getDeviceSimNo()  " + loginResponseDto.getUserDetailDto().getFpsStore().getDeviceSimNo());
                        ((TextView) findViewById(R.id.sim_id_value)).setText(simNumber.toUpperCase());
                    }




                } catch (Exception e) {
                    Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", "setUpDashBoard() FPSDBHelper LoginResponseDto  Exception =  " + e);

                }


            }
        } catch (Exception e) {
            Util.LoggingQueue(ProfileActivity.this, "ProfileActivity", ".getFpsStore() Exception : " + e);


        }

        setUpPopUpPage();
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* startActivity(new Intent(com.omneagate.activity.ProfileActivity.this, SaleActivity.class));
                finish();*/
                onBackPressed();
            }
        });
        getPOSOperatingHours();

    }


    //Called when user press back button
    @Override
    public void onBackPressed() {
//        startActivity(new Intent(this, SaleActivity.class));
        Util.LoggingQueue(this, "Profile page", "Back pressed Called");
        finish();
    }

    //Concrete method
    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {

    }

    public void backArrowPressed() {

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
