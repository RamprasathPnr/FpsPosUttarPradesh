package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.RoleFeature;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.RoleFeatureDto;
import com.omneagate.DTO.RollMenuDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.fpsRollViewAdpter;

import java.util.ArrayList;
import java.util.List;

public class CardActivationActivity extends BaseActivity {
    GridView fps_rollview;

    List<RollMenuDto> rollMenuDto = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_card_activation);
        Util.LoggingQueue(CardActivationActivity.this, "CardActivationActivity", "onCreate() called ");

        RollViews();
        setUpInitialPage();


    }

    private void RollViews() {
        long id = FPSDBHelper.getInstance(this).retrieveId("CARD_ACTIVATION_MENU");
        List<RoleFeatureDto> retriveRollFeature = new ArrayList<>();
        retriveRollFeature = FPSDBHelper.getInstance(this).retrieveSalesOrderData(id, SessionId.getInstance().getUserId());
        int roll_Feature_Size = retriveRollFeature.size();
        for (int i = 0; i < roll_Feature_Size; i++) {
            String roll_Name = retriveRollFeature.get(i).getRollName();
            try {
                RoleFeature rolls = RoleFeature.valueOf(roll_Name);
                rollMenuDto.add(new RollMenuDto(getString(rolls.getRollName()), rolls.getBackground(), rolls.getColorCode(), rolls.getDescription()));
            } catch (Exception e) {
                Util.LoggingQueue(CardActivationActivity.this, "CardActivationActivity", "RollViews() Exception " +e);

            }
        }
        fps_rollview = (GridView) findViewById(R.id.fpsroll);
        fps_rollview.setAdapter(new fpsRollViewAdpter(this, rollMenuDto));
        fps_rollview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {


                    String myClass = "com.omneagate.activity." + rollMenuDto.get(i).getClassName();

                    Util.LoggingQueue(CardActivationActivity.this, "CardActivationActivity", "setOnItemClickListener() Moving to " + rollMenuDto.get(i).getClassName());

                    Intent myIntent = new Intent(getApplicationContext(), Class.forName(myClass));
                    startActivity(myIntent);
                    finish();
                }
//                catch (ClassNotFoundException e) {
                catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Error", "" + e.toString(), e);
                }


            }
        });


    }

    private void setUpInitialPage() {
        setUpPopUpPage();
        Util.LoggingQueue(this, "Card Activation", "Setting up card activation");



        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.card_activation);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SaleActivity.class));
        Util.LoggingQueue(this, "Card Activation", "Back press called");
        finish();
    }


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onDestroy() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        }
        catch(Exception e) {}
        super.onDestroy();
    }
}