package com.omneagate.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.TamilUtil;
import com.omneagate.Util.Util;
import com.omneagate.service.HttpClientWrapper;


/**
 * BaseActivity is the base class for all activities
 */
public abstract class BaseActivity extends Activity {

    //Network connectivity
    NetworkConnection networkConnection;

    //HttpConnection service
    HttpClientWrapper httpConnection;

    //Global application context for this application
    GlobalAppState appState;

    //Progressbar for waiting
    CustomProgressDialog progressBar;

    /*Action member variable to create Action Bar */
    ActionBar actionBar;

    /*
     * abstract method for all activity
     * */
    protected abstract void processMessage(Bundle message, ServiceListenerType what);


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
    }


    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilText(TextView textName, int id) {
        if (GlobalAppState.language.equals("ta")) {
            Typeface tfBamini = Typeface.createFromAsset(getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini);
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, getString(id)));
        } else {
            textName.setText(getString(id));
        }
    }

    /**
     * Tamil text textView typeface
     * input  textView name and text string input
     */
    public void setTamilText(TextView textName, String text) {
        if (GlobalAppState.language.equals("hi")) {
            /*Typeface tfBamini = Typeface.createFromAsset(getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini);
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, text));*/
            textName.setText(text);
        } else {
            textName.setText(text);
        }
    }

    /*
     * actionbar creation function
     *
     * change action bar color and text*/

    public void actionBarCreation() {
        try {
            actionBar = getActionBar();
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000ff")));
            actionBar.setTitle("");
            actionBar.setIcon(R.drawable.logo);
            LayoutInflater mInflater = LayoutInflater.from(this);
            View titleText = mInflater.inflate(R.layout.actionbar_custom,
                    null);
            actionBar.setCustomView(titleText);
            Util.setTamilText((TextView) titleText.findViewById(R.id.login_actionbar), R.string.headerAllPage);
            actionBar.setDisplayShowCustomEnabled(true);
            Util.changeLanguage(this, GlobalAppState.language);
        } catch (Exception e) {
            android.util.Log.e("Error", e.toString(), e);
        }

    }


    /*Handler used to get response from server*/
    protected final Handler SyncHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ServiceListenerType type = (ServiceListenerType) msg.obj;
            switch (type) {
                case LOGIN_USER:
                    processMessage(msg.getData(), ServiceListenerType.LOGIN_USER);
                    break;
                case LOGOUT_USER:
                    processMessage(msg.getData(), ServiceListenerType.LOGOUT_USER);
                    break;
                case FPS_INTENT_REQUEST:
                    processMessage(msg.getData(), ServiceListenerType.FPS_INTENT_REQUEST);
                    break;
                case CARD_OTP_REGENERATE:
                    processMessage(msg.getData(), ServiceListenerType.CARD_OTP_REGENERATE);
                    break;
                case QR_CODE:
                    processMessage(msg.getData(), ServiceListenerType.QR_CODE);
                    break;
                case CARD_ACTIVATION:
                    processMessage(msg.getData(), ServiceListenerType.CARD_ACTIVATION);
                    break;
                case CARD_REGISTRATION:
                    processMessage(msg.getData(), ServiceListenerType.CARD_REGISTRATION);
                    break;
                case DEVICE_STATUS:
                    processMessage(msg.getData(), ServiceListenerType.DEVICE_STATUS);
                    break;
                case UPGRADE_RESPONSE:
                    processMessage(msg.getData(), ServiceListenerType.UPGRADE_RESPONSE);
                    break;
                case BENEFICIARY_UPDATION:
                    processMessage(msg.getData(), ServiceListenerType.BENEFICIARY_UPDATION);
                    break;
                case SEND_BILL:
                    processMessage(msg.getData(), ServiceListenerType.SEND_BILL);
                    break;
                case DEVICE_REGISTER:
                    processMessage(msg.getData(), ServiceListenerType.DEVICE_REGISTER);
                    break;
                case CARD_OTP:
                    processMessage(msg.getData(), ServiceListenerType.CARD_OTP);
                    break;
                default:
                    processMessage(msg.getData(), ServiceListenerType.ERROR_MSG);
                    break;
            }
        }

    };
}
