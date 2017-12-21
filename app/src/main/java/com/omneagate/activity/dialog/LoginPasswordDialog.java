package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.StringDigesterString;
import com.omneagate.Util.TamilUtil;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;
import com.omneagate.activity.RegistrationConfirmActivity;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.digest.StringDigester;

/**
 * This dialog will appear on the time of user logout
 */
public class LoginPasswordDialog extends Dialog implements
        View.OnClickListener {


    private final Activity context;  //    Context from the user

    /*Constructor class for this dialog*/
    public LoginPasswordDialog(Activity _context) {
        super(_context);
        context = _context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_close_sale_pass);
        setCancelable(false);
        Util.setTamilText((TextView) findViewById(R.id.textViewNwTitle), R.string.password);
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        Util.setTamilText(okButton, R.string.ok);
        okButton.setOnClickListener(this);
        Button cancelButton = (Button) findViewById(R.id.buttonNwCancel);
        Util.setTamilText(cancelButton, R.string.cancel);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwOk:
                if (checkPassword()) {
                    passwordChecking(((EditText) findViewById(R.id.editTextUrl)).getText().toString().trim());
                }
                break;
            case R.id.buttonNwCancel:
                InputMethodManager imm = (InputMethodManager) context.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                dismiss();
                break;
        }
    }

    public void passwordChecking(String password) {
        String passwordHash = FPSDBHelper.getInstance(context).getUserDetails(SessionId.getInstance().getUserId()).getUserDetailDto().getPassword();
        new LocalPasswordProcessCheck().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, password, passwordHash);
    }


    private boolean localDbPassword(String passwordUser, String passwordDbHash) {
        StringDigester stringDigester = StringDigesterString.getPasswordHash(context);
        return stringDigester.matches(passwordUser, passwordDbHash);
    }

    //Local login Process
    private class LocalPasswordProcessCheck extends AsyncTask<String, Void, Boolean> {

        CustomProgressDialog progressBar ;

        @Override
        protected void onPreExecute() {
            progressBar = new CustomProgressDialog(context);
            progressBar.setCanceledOnTouchOutside(false);
            progressBar.show();
        }

        /**
         * Local login Background Process
         * return true if user hash and dbhash equals else false
         */
        protected Boolean doInBackground(String... params) {
            try {
                return localDbPassword(params[0], params[1]);
            } catch (Exception e) {
                Log.e("loca lDb", "Interrupted", e);
                return false;

            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try {
                if (progressBar != null)
                    progressBar.dismiss();
            }
            catch(Exception e) {}

            if (result) {
                dismiss();
                ((RegistrationConfirmActivity)context).loginDevice(((EditText) findViewById(R.id.editTextUrl)).getText().toString().trim());
            } else {
                Util.messageBar(context, context.getString(R.string.loginInvalidUserPassword));
            }
        }
    }

    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilText(TextView textName, int id) {
        if (GlobalAppState.language.equals("hi")) {
            Typeface tfBamini = Typeface.createFromAsset(context.getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini);
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, context.getString(id)));
        } else {
            textName.setText(context.getString(id));
        }
    }

    /**
     * Store changed ip in shared preference
     * returns true if value present else false
     */
    private boolean checkPassword() {
        EditText urlText = (EditText) findViewById(R.id.editTextUrl);
        String url = urlText.getText().toString().trim();
        if (StringUtils.isEmpty(url) ) {
            return false;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(urlText.getWindowToken(), 0);
        return true;
    }
}