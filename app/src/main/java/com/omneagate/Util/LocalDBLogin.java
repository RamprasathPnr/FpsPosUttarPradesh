package com.omneagate.Util;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

import com.omneagate.DTO.LoginHistoryDto;
import com.omneagate.DTO.RoleFeatureDto;
import com.omneagate.DTO.UserDetailDto;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.StringDigesterString;
import com.omneagate.Util.Util;
import com.omneagate.activity.AdminActivity;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.LoginActivity;
import com.omneagate.activity.R;
import com.omneagate.activity.SaleActivity;
import com.omneagate.activity.dialog.GpsAlertDialog;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.digest.StringDigester;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created for LocalDBLogin
 */
public class LocalDBLogin {

    Activity context;
    CustomProgressDialog progressBar;
    UserDetailDto profile;

    public LocalDBLogin(Activity context, CustomProgressDialog progressBar, UserDetailDto profile) {
        this.context = context;
        this.progressBar = progressBar;
        this.profile = profile;
    }

    /**
     * async task for login
     *
     * @param passwordUser and hash
     */
    public void setLoginProcess(String passwordUser, String hashDbPassword) {
        new LocalLoginProcess(passwordUser, hashDbPassword).execute();

    }

    private boolean localDbPassword(String passwordUser, String passwordDbHash) {

        StringDigester stringDigester = StringDigesterString.getPasswordHash(context);

        return stringDigester.matches(passwordUser, passwordDbHash);
    }

    private void insertLoginHistoryDetails() {
        LoginHistoryDto loginHistoryDto = new LoginHistoryDto();
        if (profile.getFpsStore() != null)
            loginHistoryDto.setFpsId(profile.getFpsStore().getId());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        loginHistoryDto.setLoginTime(df.format(new Date()));
        loginHistoryDto.setLoginType("OFFLINE_LOGIN");
        loginHistoryDto.setUserId(profile.getId());
        df = new SimpleDateFormat("ddMMyyHHmmss", Locale.getDefault());
        String transactionId = df.format(new Date());
        loginHistoryDto.setTransactionId(transactionId);
        SessionId.getInstance().setTransactionId(transactionId);
        FPSDBHelper.getInstance(context).insertLoginHistory(loginHistoryDto);
    }

    //Local login Process
    private class LocalLoginProcess extends AsyncTask<String, Void, Boolean> {
        // user password and local db password
        String passwordUser, hashDbPassword;


        // LocalLoginProcess Constructor
        LocalLoginProcess(String passwordUser, String hashDbPassword) {
            this.passwordUser = passwordUser;
            this.hashDbPassword = hashDbPassword;


        }

        /**
         * Local login Background Process
         * return true if user hash and dbhash equals else false
         */
        protected Boolean doInBackground(String... params) {
            try {
                return localDbPassword(passwordUser, hashDbPassword);
            } catch (Exception e) {
                Log.e("loca lDb", "Interrupted", e);
                return false;

            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (progressBar != null) progressBar.dismiss();
            if (result) {
                GlobalAppState.localLogin = true;
                insertLoginHistoryDetails();
                String lastModifiedDate = FPSDBHelper.getInstance(context).getMasterData("syncTime");
                if (StringUtils.isNotEmpty(lastModifiedDate)) {
                    LocationManager manager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
                    boolean enabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (!enabled) {
                        new GpsAlertDialog(context).show();
                    }
                    else {
                        if ("ADMIN".equalsIgnoreCase(profile.getProfile())) {
                            context.startActivity(new Intent(context, AdminActivity.class));
                            context.finish();
                        } else {
                            List<RoleFeatureDto> retrieveRoleFeature = FPSDBHelper.getInstance(context).retrieveData(SessionId.getInstance().getUserId());
                            if(retrieveRoleFeature.size() == 0) {
                                Util.messageBar(context, context.getString(R.string.firstUserLoginAlert));
                                return;
                            }
                            context.startActivity(new Intent(context, SaleActivity.class));
                            context.finish();
                        }
                    }
                } else {
                    Util.messageBar(context, context.getString(R.string.loginInvalidUserPassword));
                }
            }else{
                Util.messageBar(context, context.getString(R.string.inCorrectUnamePword));
            }
        }
    }
}
