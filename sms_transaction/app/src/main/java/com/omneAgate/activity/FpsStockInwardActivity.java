package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.GodownStockOutwardDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.Util;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FpsStockInwardActivity extends BaseActivity {

    //List of GodownStockDto for fps stock inward
    private List<GodownStockOutwardDto> fpsStockInwardList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fps_stock_inward);
        appState = (GlobalAppState) getApplication();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        actionBarCreation();
        Util.setTamilText((TextView) findViewById(R.id.fpsInvardchellanIdLabel), getString(R.string.chellanId));
        Util.setTamilText((TextView) findViewById(R.id.fpsInvardoutwardDateLabel), getString(R.string.outwardDate));
        Util.setTamilText((TextView) findViewById(R.id.fpsInvardactionLabel), getString(R.string.action));
    }

    @Override
    protected void onStart() {
        super.onStart();
        configureData();
    }

    /*Data from server has been set inside this function*/
    private void configureData() {
        try {
            LinearLayout fpsInwardLinearLayout = (LinearLayout) findViewById(R.id.listView_fps_stock_inward);
            fpsStockInwardList = FPSDBHelper.getInstance(this).showFpsStockInvard(appState.getFpsId());

            if (fpsStockInwardList.size() == 0) {
                TextView t = (TextView) findViewById(R.id.tvNoRecords);
                t.setVisibility(View.VISIBLE);
                return;
            }
            fpsInwardLinearLayout.removeAllViews();
            for (int position = 0; position < fpsStockInwardList.size(); position++) {
                LayoutInflater lin = LayoutInflater.from(this);
                fpsInwardLinearLayout.addView(returnView(lin, fpsStockInwardList.get(position), position));
            }


        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
        }
    }

    /*User Fps Inward view*/
    private View returnView(LayoutInflater entitle, GodownStockOutwardDto data, int position) {
        View convertView = entitle.inflate(R.layout.adapter_fps_stock_inward, null);
        TextView deliveryChellanId = (TextView) convertView.findViewById(R.id.tvDeliveryChellanId);
        TextView outwardDate = (TextView) convertView.findViewById(R.id.tvOutwardDate);
        final Button actionButton = (Button) convertView.findViewById(R.id.btnAction);
        Util.setTamilText(actionButton, getString(R.string.viewString));

        deliveryChellanId.setText("" + data.getDeliveryChallanId());
        SimpleDateFormat df2 = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String dateText = df2.format(data.getOutwardDate());
        outwardDate.setText(dateText);
        actionButton.setId(position);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FpsStockInwardActivity.this, FpsStockInwardDetailActivity.class);
                int pos = actionButton.getId();
                i.putExtra("fid", fpsStockInwardList.get(pos).getFpsId());
                i.putExtra("cid", fpsStockInwardList.get(pos).getDeliveryChallanId());
                startActivity(i);
                finish();

            }
        });
        return convertView;

    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }


}
