package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.GridMenuDto;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.MiscellaneousAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created for update mobile
 */
public class TransactionsSubmenuActivity extends BaseActivity {

    RelativeLayout beneficiary_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_transactions_submenu);
        setUpPopUpPage();
        Util.setTamilText((TextView) findViewById(R.id.top_textView), getString(R.string.transactions));
        final List<GridMenuDto> GridMenuDtoList = loadMenuList();
        GridView miscellaneousMenu = (GridView) findViewById(R.id.fpsroll);
        miscellaneousMenu.setAdapter(new MiscellaneousAdapter(this, GridMenuDtoList));
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

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
    }

    private List<GridMenuDto> loadMenuList() {
        List<GridMenuDto> GridMenuDtoList = new ArrayList<>();
        GridMenuDto GridMenuDto1 = new GridMenuDto(getResources().getString(R.string.monthly_sales_summary), R.drawable.icon_history_sale, "MonthlySalesReportActivity");
        GridMenuDto GridMenuDto2 = new GridMenuDto(getResources().getString(R.string.bill_search), R.drawable.icon_search_bills, "BillSearchActivity");
        GridMenuDtoList.add(GridMenuDto1);
        GridMenuDtoList.add(GridMenuDto2);
        return  GridMenuDtoList;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SaleActivity.class));
        finish();
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