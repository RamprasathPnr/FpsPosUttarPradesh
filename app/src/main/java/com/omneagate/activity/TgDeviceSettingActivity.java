package com.omneagate.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.omneagate.Util.AndroidDeviceProperties;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.MySharedPreference;
import com.omneagate.Util.Util;
import com.omneagate.Util.XMLUtil;
import com.omneagate.activity.dialog.SettingUpdatedDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TgDeviceSettingActivity extends BaseActivity implements View.OnClickListener {

    private Spinner languageSpinner;
    private EditText edtDeviceId,edtImeiNumber,edtBuildNumber,edtSimSerialNo;
    private Button btSaveSettings,btCancel;
    List<String> languagesList;
    private ImageView imageViewBack;
    private LanguageAdapter language;
    private String strlanguage,strConnectionType,strWeightingScale;
    private int i_language;
    private Timer timer;
    private LogOutTimerTask logoutTimeTask;
    List<String> connectionTypeList;
    List<String> weighingScaleList;
    private LanguageAdapter connTypeAdapter;
    private LanguageAdapter weighingScaleAdapter;
    private Spinner connectTypeSpinner,weighingScaleSpinner;
    private String TAG=TgDeviceSettingActivity.class.getCanonicalName();

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_device_setting);
        configureInitView();
    }

    private void configureInitView() {
        setPopUpPage();

        updateDateTime();
        languageSpinner = (Spinner) findViewById(R.id.languageSpinner);
        connectTypeSpinner=(Spinner)findViewById(R.id.connectionType);
        weighingScaleSpinner=(Spinner)findViewById(R.id.productType);

        edtSimSerialNo=(EditText)findViewById(R.id.edtSimSerialNo);
        edtDeviceId = (EditText) findViewById(R.id.edtDeviceId);
        edtImeiNumber = (EditText) findViewById(R.id.edtImeiNumber);
        edtBuildNumber=(EditText)findViewById(R.id.edtBuildNumber);
        btSaveSettings = (Button) findViewById(R.id.btSaveSettings);
        btSaveSettings.setOnClickListener(this);
        imageViewBack=(ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);

        imageViewUserProfile = (ImageView) findViewById(R.id.imageViewUserProfile);
        imageViewUserProfile.setVisibility(View.GONE);

        btCancel=(Button)findViewById(R.id.btCancel);
        btCancel.setOnClickListener(this);

        edtBuildNumber.setText(""+XMLUtil.NIC_BUILD_NUMBER);

        AndroidDeviceProperties props = new AndroidDeviceProperties(TgDeviceSettingActivity.this);
        edtImeiNumber.setText("" + props.getDeviceProperties().getImeiNo());
        edtDeviceId.setText(props.getDeviceProperties().getSerialNumber());

        languagesList=new ArrayList<String>();
        languagesList.add("తెలుగు");
        languagesList.add("English");

        connectionTypeList = new ArrayList<>();
        connectionTypeList.add("Bluetooth");
        connectionTypeList.add("RJ11");


        weighingScaleList = new ArrayList<>();
        weighingScaleList.add("Phonix Scale");
        weighingScaleList.add("Naal Cowin Scale");
        weighingScaleList.add("LND");
        language = new LanguageAdapter(languagesList);
        languageSpinner.setAdapter(language);
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strlanguage = languagesList.get(position).toString();

                if (strlanguage.equals("English")) {
                    i_language = 0;
                }
                else {
                    i_language = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        connTypeAdapter =new LanguageAdapter(connectionTypeList);
        connectTypeSpinner.setAdapter(connTypeAdapter);
        connectTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strConnectionType = connectionTypeList.get(position).toString();

                if(strConnectionType.equalsIgnoreCase("Bluetooth")){
                    Log.e(TAG,"Bluetooth");
                    MySharedPreference.writeString(TgDeviceSettingActivity.this,"ConnectionType","0");

                }else{
                    Log.e(TAG,"RJ11");
                    MySharedPreference.writeString(TgDeviceSettingActivity.this,"ConnectionType","1");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        weighingScaleAdapter =new LanguageAdapter(weighingScaleList);
        weighingScaleSpinner.setAdapter(weighingScaleAdapter);
        weighingScaleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strWeightingScale = weighingScaleList.get(position).toString();
                if(strWeightingScale.equalsIgnoreCase("Phonix Scale")){
                    Log.e(TAG,"Phonix Scale");
                    MySharedPreference.writeString(TgDeviceSettingActivity.this,"weighingScale","0");

                }else if(strWeightingScale.equalsIgnoreCase("LND")){
                    Log.e(TAG,"LND");
                    MySharedPreference.writeString(TgDeviceSettingActivity.this,"weighingScale","2");
                } else{
                    Log.e(TAG,"Naal Cowin Scale");
                    MySharedPreference.writeString(TgDeviceSettingActivity.this,"weighingScale","1");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ((TextView) findViewById(R.id.top_textView)).setText(getString(R.string.device_setting));

        if(GlobalAppState.language.equals("te")){
            languageSpinner.setSelection(0);
        }else{
            languageSpinner.setSelection(1);
        }

        int connectionType = 0;
        try {
            connectionType = Integer.parseInt(MySharedPreference.readString(getApplicationContext(),
                    "ConnectionType", "0"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        int weighingScaleType = 0;
        try {
            weighingScaleType = Integer.parseInt(MySharedPreference.readString(getApplicationContext(),
                    "weighingScale", "0"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (weighingScaleType == 0) {
            weighingScaleSpinner.setSelection(0);
        }else if(weighingScaleType == 2){
            weighingScaleSpinner.setSelection(2);
        } else{
            weighingScaleSpinner.setSelection(1);
        }

        if (connectionType == 0) {
            connectTypeSpinner.setSelection(0);
        }else{
            connectTypeSpinner.setSelection(1);
        }

        //  weighingScaleSpinner.setSelection(0);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if (telephonyManager.getSimSerialNumber() != null && !telephonyManager.getSimSerialNumber().equalsIgnoreCase("")) {
                edtSimSerialNo.setText(telephonyManager.getSimSerialNumber());
            } else {
                edtSimSerialNo.setText("NA");
            }

        } catch (Exception e) {
            edtSimSerialNo.setText("NA");
        }
    }

    private void changeLanguage() {
        if (i_language == 1) {
            Util.LoggingQueue(TgDeviceSettingActivity.this, "Selected", "Telugu");
            Util.changeLanguage(TgDeviceSettingActivity.this, "te");
        } else if (i_language == 0) {
            Util.LoggingQueue(TgDeviceSettingActivity.this, "Selected", "English");
            Util.changeLanguage(TgDeviceSettingActivity.this, "en");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btSaveSettings:
                changeLanguage();
                new SettingUpdatedDialog(TgDeviceSettingActivity.this,getResources().getString(R.string.setting_updated_success)).show();
                break;

            case R.id.imageViewBack:
                onBackPressed();
                break;

            case R.id.btCancel:
                onBackPressed();
                break;
            default:
                break;
        }

    }

    @Override
    public void onBackPressed() {
        Intent backIntent =new Intent(TgDeviceSettingActivity.this,TgLoginActivity.class);
        startActivity(backIntent);
        finish();

    }

    public class LanguageAdapter extends BaseAdapter {
        List<String> spinnerList;

        public LanguageAdapter(List<String> spinnnerList) {
            this.spinnerList=spinnnerList;

        }

        @Override
        public int getCount() {
            return spinnerList.size();
        }

        @Override
        public Object getItem(int position) {
            return spinnerList.get(position);
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

                convertView = LayoutInflater.from(TgDeviceSettingActivity.this).inflate(R.layout.adapter_language_type, viewGroup, false);
                holder.txtLanguage = (TextView) convertView.findViewById(R.id.text_language_type);
                convertView.setTag(holder);
                convertView.setTag(R.id.text_language_type, holder.txtLanguage);


            } else {
                holder = (LanguageHolder) convertView.getTag();
            }
            holder.txtLanguage.setText(spinnerList.get(position));


            return convertView;
        }

        class LanguageHolder {
            TextView txtLanguage;

        }
    }
    protected void onPause() {
        super.onPause();




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (logoutTimeTask != null) {
            logoutTimeTask.cancel();
            logoutTimeTask = null;
        }
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
            Intent i = new Intent(TgDeviceSettingActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
}
