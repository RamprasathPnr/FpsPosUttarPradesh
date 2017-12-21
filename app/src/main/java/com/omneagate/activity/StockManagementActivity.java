package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.GridMenuDto;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.StockManagementAdapter;

import java.util.ArrayList;
import java.util.List;

public class StockManagementActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_stock_management);
        setUpPopUpPage();
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.stock_management);
        final List<GridMenuDto> GridMenuDtoList = loadMenuList();
        GridView miscellaneousMenu = (GridView) findViewById(R.id.fpsroll);
        miscellaneousMenu.setAdapter(new StockManagementAdapter(this, GridMenuDtoList));
        miscellaneousMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    String myClass = "com.omneagate.activity." + GridMenuDtoList.get(i).getClassName();
                    Intent myIntent = new Intent(getApplicationContext(), Class.forName(myClass));
                    startActivity(myIntent);
                } catch (Exception e) {
                }
            }
        });
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private List<GridMenuDto> loadMenuList() {
        List<GridMenuDto> GridMenuDtoList = new ArrayList<>();
        GridMenuDto GridMenuDto1 = new GridMenuDto(getResources().getString(R.string.stock_inward_tv), R.drawable.icon_inward, "FpsStockInwardActivity");
        GridMenuDto GridMenuDto2 = new GridMenuDto(getResources().getString(R.string.advanced_stock_title), R.drawable.icon_advance_stock, "AdvanceStockActvity");
        GridMenuDto GridMenuDto3 = new GridMenuDto(getResources().getString(R.string.correction_history_tv), R.drawable.icon_stock_adj, "StockAdjustmentPage");
        GridMenuDto GridMenuDto4 = new GridMenuDto(getResources().getString(R.string.stock_status_tv), R.drawable.icon_stock_check, "StockCheckActivity");
        GridMenuDto GridMenuDto5 = new GridMenuDto(getResources().getString(R.string.monthly_inventory_summary), R.drawable.stock_status_summary, "MonthlyInventoryReportActivity");
        GridMenuDto GridMenuDto6 = new GridMenuDto(getResources().getString(R.string.stock_allocation_title), R.drawable.icon_stock_allocation, "StockAllocationActivity");
        GridMenuDto GridMenuDto7 = new GridMenuDto(getResources().getString(R.string.opening_stock), R.drawable.icon_opening_balance, "MissedOpenStockActivity");
        GridMenuDtoList.add(GridMenuDto1);
        GridMenuDtoList.add(GridMenuDto2);
        GridMenuDtoList.add(GridMenuDto3);
        GridMenuDtoList.add(GridMenuDto4);
        GridMenuDtoList.add(GridMenuDto5);
        GridMenuDtoList.add(GridMenuDto6);
        GridMenuDtoList.add(GridMenuDto7);
        return  GridMenuDtoList;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SaleActivity.class));
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