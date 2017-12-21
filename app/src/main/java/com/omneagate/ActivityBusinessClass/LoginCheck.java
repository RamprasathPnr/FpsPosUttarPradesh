package com.omneagate.ActivityBusinessClass;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.omneagate.DTO.LoginDto;
import com.omneagate.DTO.LoginResponseDto;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.R;
import com.omneagate.Util.LocalDBLogin;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Created for local Login
 */
public class LoginCheck {

    Activity context;
    CustomProgressDialog progressBar;
    Boolean isShopInactive;
    String TAG = "LoginCheck";

    public LoginCheck(Activity context, CustomProgressDialog progressBar){
        this.context = context;
        this.progressBar = progressBar;
    }

    /**
     * sending login details to server if network connection available
     *
     * @params loginDto
     */

    /**
     * IF NO NETWORK AVAILABLE LOGIN WILL DONE USING LOCAL DATABASE
     */
    public void localLogin(LoginDto loginCredentials) {
        try {
            SharedPreferences prefs = context.getSharedPreferences("FPS", Context.MODE_PRIVATE);
            if (!prefs.getBoolean("approved", false)) {
                Util.messageBar(context, context.getString(R.string.noNetworkConnection));
                return;
            }
            progressBar.show();
            LoginResponseDto hashDbPassword = FPSDBHelper.getInstance(context).retrieveData(loginCredentials.getUserName());
            if (hashDbPassword == null) {
                Log.e(TAG,"hashDbPassword == null");
//                Util.messageBar(context, context.getString(R.string.inCorrectUnamePword));
//              This case will occur for status code 5095 in online login
                String messageData = Util.messageSelection(FPSDBHelper.getInstance(context).retrieveLanguageTable(5095));
                if (messageData == null) {
                    messageData = context.getString(R.string.internalError);
                }
                Util.messageBar(context, messageData);
                dismissProgress();
                return;
            }

            LoginData.getInstance().setLoginData(hashDbPassword);
            String hashPassword = hashDbPassword.getUserDetailDto().getPassword();
            if (StringUtils.isNotEmpty(hashPassword)) {

                // Checking device status
                try {
                    if (hashDbPassword.getUserDetailDto().getProfile().equalsIgnoreCase("FPS")) {
                        String deviceStatus = FPSDBHelper.getInstance(context).getMasterData("status");
                        if (deviceStatus.equalsIgnoreCase("UNASSOCIATED")) {
                            dismissProgress();
                            Util.messageBar(context, context.getString(R.string.unassociated));
                            return;
                        } else if (deviceStatus.equalsIgnoreCase("INACTIVE")) {
                            dismissProgress();
                            Util.messageBar(context, context.getString(R.string.deviceInvalid));
                            return;
                        }
                    }
                }
                catch(Exception e) {}

                // Checking user status
                try {
                    if (!hashDbPassword.getUserDetailDto().getActive()) {
                        dismissProgress();
                        Util.messageBar(context, context.getString(R.string.userInactive));
                        return;
                    }
                }
                catch(Exception e) {
                    dismissProgress();
                    Util.messageBar(context, context.getString(R.string.userInactive));
                    return;
                }

                // Checking fps store status
                try {
                    if (!hashDbPassword.getUserDetailDto().getProfile().equalsIgnoreCase("ADMIN")) {
                        if (!hashDbPassword.getUserDetailDto().getFpsStore().isActive()) {
                            dismissProgress();
                            Util.messageBar(context, context.getString(R.string.storeInactive));
                            return;
                        }
                    }
                }
                catch(Exception e) {}


                SessionId.getInstance().setUserId(hashDbPassword.getUserDetailDto().getId());


                Log.e("LoginCheck", " FpsStore = "+hashDbPassword.getUserDetailDto().getFpsStore());


                SessionId.getInstance().setFpsId(hashDbPassword.getUserDetailDto().getFpsStore().getId());
                SessionId.getInstance().setFpsCode(hashDbPassword.getUserDetailDto().getFpsStore().getGeneratedCode());
                SessionId.getInstance().setUserName(hashDbPassword.getUserDetailDto().getUserId());
                SessionId.getInstance().setLocalpasword(loginCredentials.getPassword());
                String lastLoginTime = FPSDBHelper.getInstance(context).getLastLoginTime(hashDbPassword.getUserDetailDto().getId());
                if (StringUtils.isNotEmpty(lastLoginTime)) {
                    SessionId.getInstance().setLastLoginTime(new Date(Long.parseLong(lastLoginTime)));
                } else {
                    SessionId.getInstance().setLastLoginTime(new Date());
                }
                FPSDBHelper.getInstance(context).setLastLoginTime(hashDbPassword.getUserDetailDto().getId());
                SessionId.getInstance().setLoginTime(new Date());
                LocalDBLogin localDBLogin = new LocalDBLogin(context, progressBar, hashDbPassword.getUserDetailDto());
                localDBLogin.setLoginProcess(loginCredentials.getPassword(), hashPassword);

            } else {
                dismissProgress();
                Log.e(TAG, "hashDbPassword != null");
                Util.messageBar(context, context.getString(R.string.inCorrectUnamePword));

            }
        } catch (Exception e) {
            Log.e("LoginActivity", "Error in Local Login", e);
        }
    }

    private void dismissProgress() {
        if (progressBar != null) {
            if (progressBar.isShowing()) {
                progressBar.dismiss();
            }
        }
    }
}
