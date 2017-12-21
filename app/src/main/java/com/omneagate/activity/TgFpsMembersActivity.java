package com.omneagate.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.Adapter.FPSDealerAdapter;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSDealer;
import com.omneagate.DTO.FPSDealerDetails;
import com.omneagate.DTO.FPSLogout;
import com.omneagate.DTO.MenuDataDto;
import com.omneagate.Util.FpsMemberData;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.PullToRefresh.LoadMoreListView;
import com.omneagate.Util.Util;
import com.omneagate.Util.XMLUtil;
import com.omneagate.activity.dialog.MenuAdapter;
import com.omneagate.activity.dialog.TgGenericErrorDialog;
import com.omneagate.exception.FPSException;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TgFpsMembersActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    FPSDealerDetails fpsdetail;

    List<FPSDealer> fpsdealer;

    private ListPopupWindow popupWindow;

    private ImageView imageViewBack;

    private LinearLayout scan_finger_print_layout, best_finger_detection_layout, scan_iris_layout;

    String data, searchType;

    TextView page_header;

    LoadMoreListView billSearch;

    int loadMore = 0;

    FPSDealerAdapter adapter;
    long id;
    private Timer timer;
    private LogOutTimerTask logoutTimeTask;
    private Button btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_fps_members);
        setPopUpPage();
        updateDateTime();
        ((TextView) findViewById(R.id.top_textView)).setText(getString(R.string.fps_members));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);

        scan_finger_print_layout = (LinearLayout) findViewById(R.id.scan_finger_print_layout);
        scan_finger_print_layout.setOnClickListener(this);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);


        best_finger_detection_layout = (LinearLayout) findViewById(R.id.best_finger_detection_layout);
        best_finger_detection_layout.setOnClickListener(this);

        scan_iris_layout = (LinearLayout) findViewById(R.id.scan_iris_layout);
        scan_iris_layout.setOnClickListener(this);

        page_header = (TextView) findViewById(R.id.page_header);
//        ((TextView) findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo() + " " + getResources().getString(R.string.welcome) + "  " + LoginData.getInstance().getFpsUsername());
        page_header.setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo());

      /*  if(getIntent().getExtras().containsKey("fpsdetail")){
            fpsdetail = (FPSDealerDetails) getIntent().getSerializableExtra("fpsdetail");
        }*/
        fpsdetail= FpsMemberData.getInstance().getFpsDealerDetails();

        fpsdealer = new ArrayList<>();
        billSearch = (LoadMoreListView) findViewById(R.id.listView_fps_member_detail);
        adapter = new FPSDealerAdapter(this, fpsdetail.getFpsDealerList());
        billSearch.setAdapter(adapter);

        billSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for (FPSDealer fpsdealer : fpsdetail.getFpsDealerList()) {
                    fpsdealer.setSelectedItem(false);
                }

                fpsdetail.getFpsDealerList().get(position).setSelectedItem(true);

                adapter = new FPSDealerAdapter(TgFpsMembersActivity.this, fpsdetail.getFpsDealerList());
                billSearch.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
        });
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
            Intent i = new Intent(TgFpsMembersActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.exit_txt))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new GetLogout().execute();
                     /*   Intent backIntent = new Intent(TgFpsMembersActivity.this, TgLoginActivity.class);
                        startActivity(backIntent);
                        finish();*/

                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                onBackPressed();
                break;

            case R.id.btnBack:
                onBackPressed();
                break;

            case R.id.scan_finger_print_layout:
                moveToNextActivity("TgScanFingerPrintActivity");
                break;
            case R.id.best_finger_detection_layout:
                moveToNextActivity("TgTenFingerFpsMemberRegistrationActivity");
                break;
            case R.id.scan_iris_layout:
                moveToNextActivity("TgIrisScanActivity");
                break;
            default:
                break;

        }

    }

    private void moveToNextActivity(String activityName) {
        try {
            boolean memberSelected = false;
            int position = 0;

            for (int i = 0; i < fpsdetail.getFpsDealerList().size(); i++) {

                FPSDealer fpsDealer = fpsdetail.getFpsDealerList().get(i);

                if (fpsDealer.isSelectedItem()) {
                    memberSelected = true;
                    position = i;
                }

            }

            if (memberSelected) {
                if (activityName.equals("TgIrisScanActivity")) {
                    Intent in = new Intent(TgFpsMembersActivity.this, TgIrisScanActivity.class);
                    in.putExtra("MemberName", fpsdetail.getFpsDealerList().get(position).getDealerOrNomine());
                    in.putExtra("MemberUid", fpsdetail.getFpsDealerList().get(position).getDealerOrNomineUidNo());
                    in.putExtra("DealerType", fpsdetail.getFpsDealerList().get(position).getDealerType());
                    startActivity(in);
                    finish();
                } else if (activityName.equals("TgTenFingerFpsMemberRegistrationActivity")) {
                    Intent inBfd = new Intent(TgFpsMembersActivity.this, TgTenFingerFpsMemberRegistrationActivity.class);
                    inBfd.putExtra("memberName", fpsdetail.getFpsDealerList().get(position).getDealerOrNomine());
                    inBfd.putExtra("ActivityName", "TgFpsMembersActivity");
                    inBfd.putExtra("uid", fpsdetail.getFpsDealerList().get(position).getDealerOrNomineUidNo());
                    startActivity(inBfd);
                    finish();
                } else {
                    Intent in = new Intent(TgFpsMembersActivity.this, TgScanFingerPrintActivity.class);
                    in.putExtra("MemberName", fpsdetail.getFpsDealerList().get(position).getDealerOrNomine());
                    in.putExtra("MemberUid", fpsdetail.getFpsDealerList().get(position).getDealerOrNomineUidNo());
                    in.putExtra("DealerType", fpsdetail.getFpsDealerList().get(position).getDealerType());
                    startActivity(in);
                    finish();
                }
            } else {
                Toast.makeText(TgFpsMembersActivity.this, R.string.Please_Select_Fps_Member, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
   /* class GetLogout extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... arg0) {
            return getLogoutTest();
        }

        protected void onPostExecute(String result) {

        }
    }*/
   /* public String getLogoutTest() {
      //  Log.e(TAG, "<====Request Started=====>");
        Map<String, Object> inputMap = new LinkedHashMap<String, Object>();

        inputMap.put("distCode",LoginData.getInstance().getDistCode());
        inputMap.put("shopNo",LoginData.getInstance().getShopNo());

        inputMap.put("transactionId", LoginData.getInstance().getTransactionId());
        inputMap.put("password", "1159abbb8b6c0b8a8964210af6954b17");

        try {
            FPSLogout fpslogout = XMLUtil.doFPSLogout(inputMap);
            if (fpslogout.getRespMsgCode().contains("0")) {
                Intent backIntent = new Intent(TgFpsMembersActivity.this, TgLoginActivity.class);
                startActivity(backIntent);
                finish();

            }
            return fpslogout.toString();
        } catch (Exception e) {

            return e.getMessage();
        }

    }*/
}
