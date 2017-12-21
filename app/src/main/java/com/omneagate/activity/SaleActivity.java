package com.omneagate.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.BaseDto;
import com.omneagate.DTO.EnumDTO.CommonStatuses;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.RoleFeature;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSStockDto;
import com.omneagate.DTO.RoleFeatureDto;
import com.omneagate.DTO.RollMenuDto;
import com.omneagate.DTO.UpgradeDetailsDto;
import com.omneagate.DTO.UserDto.BillItemProductDto;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.LogoutDialog;
import com.omneagate.activity.dialog.fpsRollViewAdpter;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class SaleActivity extends BaseActivity {

    final ArrayList<String> fpsRoleName = new ArrayList<>();
    GridView fpsRollView;
    List<RollMenuDto> roleMenus = new ArrayList<>();
    LogoutDialog logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_scan);
        appState = (GlobalAppState) getApplication();
        Util.LoggingQueue(this, "->SaleActivity", "Inside onCreate");

        if (!checkLocationDetails()) {
            return;
        }
        upgradeSuccessMessage();
        setUpDashBoard();


        try {
            String maxBillDate = FPSDBHelper.getInstance(SaleActivity.this).getMaxBillDate();
            if ((maxBillDate == null) || (maxBillDate.equalsIgnoreCase("")) || (maxBillDate.equalsIgnoreCase("null"))) {
                new advanceStockProcess().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String currentDeviceDate = simpleDateFormat.format(new Date());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date d1 = sdf.parse(maxBillDate);
                Date d2 = sdf.parse(currentDeviceDate);
                if (d1.compareTo(d2) > 0) {
                    List<BillItemProductDto> items = FPSDBHelper.getInstance(SaleActivity.this).userAdvanceStock();
                    FPSDBHelper.getInstance(SaleActivity.this).insertInvalidDateException("advance_stock_inward", "Advance_Processing", items.toString(), "MaxBillDate = " + maxBillDate + "\n" + "DeviceDate = " + currentDeviceDate);
                }
                else {
                    new advanceStockProcess().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        }
        catch(Exception e) {
            new advanceStockProcess().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }



    }

    public void rollFeatureView() {
        //Log.e("rollFeatureView", "rollFeatureView");
        List<RoleFeatureDto> retrieveRoleFeature = FPSDBHelper.getInstance(this).retrieveData(SessionId.getInstance().getUserId());
        for (int i = 0; i < retrieveRoleFeature.size(); i++) {
            String roleName = retrieveRoleFeature.get(i).getRollName();
            //  Log.e("->roleFeatureName",""+roleName);
            try {
                if (roleName.equalsIgnoreCase("SALES_ORDER_MENU") || roleName.equalsIgnoreCase("CARD_ACTIVATION_MENU") || roleName.equalsIgnoreCase("STOCK_MANAGEMENT_MENU")
                        || roleName.equalsIgnoreCase("TRANSACTIONS_MENU") || roleName.equalsIgnoreCase("CLOSE_SALES_MENU") || roleName.equalsIgnoreCase("OTHER_MENUS")) {
                    RoleFeature rolls = RoleFeature.valueOf(roleName);

                    roleMenus.add(new RollMenuDto(getString(rolls.getRollName()), rolls.getBackground(), rolls.getColorCode(), rolls.getDescription()));
                    fpsRoleName.add(roleName);
                }
            } catch (Exception e) {
                Log.e("->Excep", e.toString(), e);
            }
        }
        fpsRollView = (GridView) findViewById(R.id.fpsroll);
        fpsRollView.setAdapter(new fpsRollViewAdpter(this, roleMenus));
        fpsRollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Util.LoggingQueue(com.omneagate.activity.SaleActivity.this, "->SaleActivity...", "-->Moving gird view setting up");
                    String myClass = "com.omneagate.activity." + roleMenus.get(i).getClassName();


                    Log.e("myClass", "" + myClass);
//                    Toast.makeText(getBaseContext(),"class name..."+ roleMenus.get(i).getClassName(),Toast.LENGTH_SHORT).show();
                    /*if(roleMenus.get(i).getClassName().equalsIgnoreCase("SaleOrderActivity")) {
//                        Toast.makeText(getBaseContext(),"inisde class name...",Toast.LENGTH_SHORT).show();
                        int count = FPSDBHelper.getInstance(SaleActivity.this).getAllMissedProductCount();
                        if(count == 1) {
                            startActivity(new Intent(SaleActivity.this, MissedOpenStockActivity.class));
                        }
                        else if (count == 0) {
                            Intent myIntent = new Intent(getApplicationContext(), Class.forName(myClass));
                            startActivity(myIntent);
                        }
                    }
                    else {*/
                    Intent myIntent = new Intent(getApplicationContext(), Class.forName(myClass));
                    startActivity(myIntent);
//                    }



                    /*Intent myIntent = new Intent(getApplicationContext(), MonthlySalesReportActivity.class);
                    startActivity(myIntent);
                    finish();*/


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Error", "" + e.toString(), e);
                }
            }
        });
    }

    /**
     * Used to set the dashboard page
     * <p/>
     * user name and onCLickListeners are in this function
     */
    private void setUpDashBoard() {
        Util.LoggingQueue(this, "->SaleActivity", "->setUpDashBoard");
        if (StringUtils.isEmpty(SessionId.getInstance().getUserName())) {
            SessionId.getInstance().setUserName("");
        }
        //((TextView) findViewById(R.id.user_fps_store)).setText(getString(R.string.fps_code) + SessionId.getInstance().getFpsCode());
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.dashboard);
        setUpPopUpPage();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SessionId.getInstance().setQrOTPEnabled(pref.getBoolean("isActive", false));
        rollFeatureView();
    }

    //After user give logout this method will call dialog
    private void userLogoutResponse() {
        logout = new LogoutDialog(this);
        Util.LoggingQueue(this, "Logout", "Logout called");
        logout.show();

    }

    //Called when user press back button
    @Override
    public void onBackPressed() {
        userLogoutResponse();
    }

    //Concrete method
    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            default:
                changeValue(message);
                break;
        }
    }

    private void changeValue(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            if ((response != null) && (!response.contains("<html>")) && !response.contains("Empty")) {
                BaseDto base = gson.fromJson(response, BaseDto.class);
                if (base.getStatusCode() == 0) {
                    FPSDBHelper.getInstance(this).updateUpgradeExec();
                    Cursor cursor = FPSDBHelper.getInstance(this).getCurerntVersonExec();
                    cursor.moveToFirst();
                    FPSDBHelper.getInstance(this).insertTableUpgrade(cursor.getInt(cursor.getColumnIndex("android_old_version")), "Upgrade completed successfully", "success", "UPGRADE_END", cursor.getInt(cursor.getColumnIndex("android_new_version")),
                            cursor.getString(cursor.getColumnIndex("ref_id")), cursor.getString(cursor.getColumnIndex("refer_id")));
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.e("SaleActivity", e.toString(), e);
        }

    }

    private void upgradeSuccessMessage() {
        try {
            NetworkConnection netStatus = new NetworkConnection(this);
            if (FPSDBHelper.getInstance(this).checkUpgradeFinished() && netStatus.isNetworkAvailable()) {
                httpConnection = new HttpClientWrapper();
                UpgradeDetailsDto upgradeDto = FPSDBHelper.getInstance(this).getUpgradeData();
                upgradeDto.setCreatedTime(new Date().getTime());
                upgradeDto.setStatus(CommonStatuses.UPDATE_COMPLETE);
                Cursor cursor = FPSDBHelper.getInstance(this).getCurerntVersonExec();
                upgradeDto.setPreviousVersion(cursor.getInt(cursor.getColumnIndex("android_old_version")));
                upgradeDto.setCurrentVersion(cursor.getInt(cursor.getColumnIndex("android_new_version")));
                upgradeDto.setReferenceNumber(cursor.getString(cursor.getColumnIndex("refer_id")));
                upgradeDto.setDeviceNum(Settings.Secure.getString(
                        getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
                String checkVersion = new Gson().toJson(upgradeDto);
                StringEntity se = new StringEntity(checkVersion, HTTP.UTF_8);
                String url = "/upgradedetails/adddetails";
                Util.LoggingQueue(this, "Device Register Version", "Checking version of apk in device");
                httpConnection.sendRequest(url, null, ServiceListenerType.CHECKVERSION,
                        SyncHandler, RequestType.POST, se, this);
            }
        } catch (Exception e) {
            Log.e("SaleActivity", e.toString(), e);
        }
    }

    private class advanceStockProcess extends AsyncTask<String, Void, Void> {
        protected void onPreExecute() {}

        protected Void doInBackground(final String... args) {
            boolean updateStock = false;
            String stock_validation = "" + FPSDBHelper.getInstance(SaleActivity.this).getMasterData("stock_validation");
            if (stock_validation != null && StringUtils.isNotEmpty(stock_validation) && (!stock_validation.equalsIgnoreCase("null"))) {
                if (stock_validation.equalsIgnoreCase("1")) {
                    updateStock = true;
                }
            }

            if (FPSDBHelper.getInstance(SaleActivity.this).userCount() > 0) {
                List<BillItemProductDto> items = FPSDBHelper.getInstance(SaleActivity.this).userAdvanceStock();
                //     Log.e("items","items"+items.toString());
                for (BillItemProductDto billItemProductDto : items) {
                    try {
                        FPSStockDto stockList = FPSDBHelper.getInstance(SaleActivity.this).getAllProductStockDetails(billItemProductDto.getProductId());
                        if(stockList != null) {
                            double openingQuantity = stockList.getQuantity();
                            double closing = stockList.getQuantity() + billItemProductDto.getQuantity();
                            stockList.setQuantity(closing);
                            if (updateStock) {
                                FPSDBHelper.getInstance(SaleActivity.this).stockUpdateAdvance(stockList, openingQuantity, billItemProductDto.getQuantity());
                            }
                        }
                        else {
                            double openingQuantity = 0.0;
                            double closing = 0.0 + billItemProductDto.getQuantity();
                            stockList.setQuantity(closing);
                            if (updateStock) {
                                FPSDBHelper.getInstance(SaleActivity.this).stockUpdateAdvance(stockList, openingQuantity, billItemProductDto.getQuantity());
                            }
                        }
                        FPSDBHelper.getInstance(SaleActivity.this).stockUpdateAdvance(billItemProductDto.getProductId());
//                    FPSDBHelper.getInstance(this).stockUpdateInward(billItemProductDto.getProductId());
                    } catch (Exception e) {
                    }
                }
            }
            return  null;
        }

        protected void onPostExecute() {}
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeAllDialogs();
    }

    private void removeAllDialogs() {
        try {
            if ((logout != null) && logout.isShowing()) {
                logout.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            logout = null;
        }
    }

}


