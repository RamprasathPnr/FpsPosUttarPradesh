package com.omneagate.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.MySharedPreference;
import com.omneagate.Util.Util;
import com.omneagate.Util.XMLUtil;
import com.omneagate.activity.dialog.SettingUpdatedDialog;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TgServerSettingsActivity extends BaseActivity implements View.OnClickListener {


    private Spinner urlSpinner;
    private Button btSaveSettings,btCancel;
    private ImageView imageViewBack;
    private List<String> urlList;
    private UrlAdapter urladapter;
    private String strUrl;
    private Timer timer;
    private LogOutTimerTask logoutTimeTask;
    private EditText autoUpgradeURL;
    private final String TAG=TgServerSettingsActivity.class.getCanonicalName();

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_server_settings);
        ConfigureInitView();
    }

    private void ConfigureInitView() {
        setPopUpPage();
        updateDateTime();
        btCancel=(Button) findViewById(R.id.btCancel);
        btCancel.setOnClickListener(this);
        btSaveSettings = (Button) findViewById(R.id.btSaveSettings);
        btSaveSettings.setOnClickListener(this);
        imageViewBack=(ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);
        autoUpgradeURL=(EditText)findViewById(R.id.edt_autoUpgradeURL);
        ((TextView) findViewById(R.id.top_textView)).setText(getString(R.string.server_setting));
        urlSpinner =(Spinner)findViewById(R.id.serverUrlSpinner);

        imageViewUserProfile = (ImageView) findViewById(R.id.imageViewUserProfile);
        imageViewUserProfile.setVisibility(View.GONE);


        urlList =new ArrayList<>();
       // urlList.add("https://eposservices.telangana.gov.in/ePoSServicesUAT/epos?wsdl");//testing
          urlList.add("https://eposservices.telangana.gov.in/ePoSServices/epos?wsdl");//live
      //  urlList.add("https://eposservices.telangana.gov.in/ePoSServicesUAT99/epos?wsdl");
       // urlList.add("http://epos.telangana.gov.in/ePoSServicesTestURL/epos");
        //urlList.add("192.168.1.53:9099");
        urladapter = new UrlAdapter();

        urlSpinner.setAdapter(urladapter);
        urlSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strUrl = urlList.get(position).toString();
                if(strUrl.equalsIgnoreCase("https://eposservices.telangana.gov.in/ePoSServicesUAT/epos")){
                    Log.e(TAG,"https://eposservices.telangana.gov.in/ePoSServicesUAT/epos");
                    MySharedPreference.writeString(TgServerSettingsActivity.this,"SelectedUrl","0");

                }else{
                    Log.e(TAG,"http://epos.telangana.gov.in/ePoSServicesTestURL/epos");
                    MySharedPreference.writeString(TgServerSettingsActivity.this,"SelectedUrl","1");

                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String serverUrl = FPSDBHelper.getInstance(TgServerSettingsActivity.this).getMasterData("serverUrl");
        autoUpgradeURL.setText(serverUrl);

       /* int selectedUrl = 0;
        try {
            selectedUrl = Integer.parseInt(MySharedPreference.readString(getApplicationContext(),
                    "SelectedUrl", "0"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (selectedUrl == 0) {
            urlSpinner.setSelection(0);
        }else{
            urlSpinner.setSelection(1);
        }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btSaveSettings:
                if (storeInLocal()) {
                    new SettingUpdatedDialog(TgServerSettingsActivity.this,getResources().getString(R.string.setting_updated_success)).show();
                }

                break;
            case R.id.btCancel:
                onBackPressed();
                break;

            case R.id.imageViewBack:
                onBackPressed();
                break;
        }
    }
    @Override
    public void onBackPressed() {
        Intent backIntent =new Intent(TgServerSettingsActivity.this,TgLoginActivity.class);
        startActivity(backIntent);
        finish();

    }
    public class UrlAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return urlList.size();
        }

        @Override
        public Object getItem(int position) {
            return urlList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            LanguageHolder holder = null;
            holder = new LanguageHolder();

            if (convertView == null) {

                convertView = LayoutInflater.from(TgServerSettingsActivity.this).inflate(R.layout.adapter_language_type, viewGroup, false);
                holder.txturl = (TextView) convertView.findViewById(R.id.text_language_type);
                convertView.setTag(holder);
                convertView.setTag(R.id.text_language_type, holder.txturl);


            } else {
                holder = (LanguageHolder) convertView.getTag();
            }
            holder.txturl.setText(urlList.get(position));


            return convertView;
        }

        class LanguageHolder {
            TextView txturl;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (logoutTimeTask != null) {
            logoutTimeTask.cancel();
            logoutTimeTask = null;
        }


    }
    private boolean storeInLocal() {
       // EditText urlText = (EditText) findViewById(R.id.editTextUrl);
        String url = autoUpgradeURL.getText().toString().trim();
        if (StringUtils.isEmpty(url) || url.length() < 4) {
            return false;
        }
        FPSDBHelper.getInstance(TgServerSettingsActivity.this).updateMaserData("serverUrl", url);
        String serverUrl = FPSDBHelper.getInstance(TgServerSettingsActivity.this).getMasterData("serverUrl");
       /* SharedPreferences mySharedPreferences = context.getSharedPreferences("FPS_POS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("server_url", serverUrl);
        editor.apply();*/
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();

        timer = new Timer();
        Log.i("Main", "Invoking logout timer");
        logoutTimeTask = new LogOutTimerTask();
        timer.schedule(logoutTimeTask, XMLUtil.autologOutTime); //auto logout in 5 minutes

    }
    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            //redirect user to login screen
            Intent i = new Intent(TgServerSettingsActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }


}
