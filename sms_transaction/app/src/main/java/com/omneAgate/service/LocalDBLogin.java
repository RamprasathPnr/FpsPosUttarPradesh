package com.omneagate.service;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.StringDigesterString;
import com.omneagate.Util.Util;
import com.omneagate.activity.R;
import com.omneagate.activity.SaleActivity;

import org.jasypt.digest.StringDigester;

/**
 * Created by user1 on 3/3/15.
 */
public class LocalDBLogin {

    Activity context;
    CustomProgressDialog progressBar;

    public LocalDBLogin(Activity context, CustomProgressDialog progressBar) {
        this.context = context;
        this.progressBar = progressBar;
    }

    /**
     * async task for login
     *
     * @param passwordUser and hash
     */
    public void setLoginProcess(String passwordUser, String hashDbPassword) {


        new LocalLoginProcess(passwordUser, hashDbPassword).execute();

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
                Log.e("localDb", "Interrupted", e);
                return false;

            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (progressBar != null) progressBar.dismiss();
            if (result) {
                context.startActivity(new Intent(context, SaleActivity.class));
                context.finish();
            } else {
                Util.messageBar(context, context.getString(R.string.loginInvalidUserPassword));
            }


        }
    }

    private boolean localDbPassword(String passwordUser, String passwordDbHash) {

        StringDigester stringDigester = StringDigesterString.getPasswordHash(context);

        return stringDigester.matches(passwordUser, passwordDbHash);
    }
}
