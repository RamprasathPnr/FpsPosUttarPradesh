package com.omneagate.activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.ProductDto;
import com.omneagate.DTO.StockAllotmentDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.Util;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class StockAllotmentDetailActivity extends BaseActivity {

    int yearCurrent;
    int monthSelected;
    List<StockAllotmentDto> list = null;
    Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_stock_allotment_detail);

        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        c = Calendar.getInstance();

        yearCurrent = c.get(Calendar.YEAR);
        monthSelected = c.get(Calendar.MONTH);

        configureData();

    }


    /*Data from server has been set inside this function*/
    private void configureData() {

        try {
            setUpPopUpPage();
            Util.LoggingQueue(this, "Stock Allotment activity", "Main page Called");

            Util.setTamilText((TextView) findViewById(R.id.select_month), R.string.select_month);
            Util.setTamilText((TextView) findViewById(R.id.previous_month), R.string.previous_month);
            Util.setTamilText((TextView) findViewById(R.id.current_month), R.string.current_month);
            Util.setTamilText((TextView) findViewById(R.id.next_month), R.string.next_month);

            Util.setTamilText((TextView) findViewById(R.id.commodity), R.string.commodity);
            Util.setTamilText((TextView) findViewById(R.id.alloted_qty), R.string.alloted_qty);
            Util.setTamilText((TextView) findViewById(R.id.issued_qty), R.string.issued_qty);
            Util.setTamilText((TextView) findViewById(R.id.balance_qty), R.string.balance_qty);

            Util.setTamilText((TextView) findViewById(R.id.btnClose), R.string.close);

            TextView topTv = (TextView) findViewById(R.id.top_textView);
            Util.setTamilText(topTv, R.string.stock_allotment_available);

            TextView subTitleTv = (TextView) findViewById(R.id.tvSubTitle);


            SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
            String formattedDate = df.format(c.getTime());

            subTitleTv.setText(getString(R.string.stock_allotment_available) + " " + formattedDate.toUpperCase());


            findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(com.omneagate.activity.StockAllotmentDetailActivity.this, StockManagementActivity.class));
                    finish();
                }
            });
            findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            ((RadioButton) findViewById(R.id.radioCurrent)).setChecked(true);

          /*  list = FPSDBHelper.getInstance(StockAllotmentDetailActivity.this).getReceivedQuantityStockInwardMonthYear(yearCurrent, monthSelected + 1);

            if (list != null) {
                poulateAdapter();
            }

            ((RadioGroup) findViewById(R.id.radioGroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    if (checkedId == R.id.radioPrevious) {

                        list = FPSDBHelper.getInstance(StockAllotmentDetailActivity.this).getReceivedQuantityStockInwardMonthYear(yearCurrent, monthSelected);
                        Log.i("AllotList", list.toString());


                    }
                    if (checkedId == R.id.radioCurrent) {

                        list = FPSDBHelper.getInstance(StockAllotmentDetailActivity.this).getReceivedQuantityStockInwardMonthYear(yearCurrent, monthSelected + 1);

                    }
                    if (checkedId == R.id.radioNext) {

                        list = FPSDBHelper.getInstance(StockAllotmentDetailActivity.this).getReceivedQuantityStockInwardMonthYear(yearCurrent, monthSelected + 2);
                    }


                    if (list != null) {
                        poulateAdapter();
                    }


                }
            });*/


        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", "Allotment:" + e.getStackTrace().toString());
        }

    }


    public void poulateAdapter() {
        Util.LoggingQueue(this, "Stock Allotment Detail activity", "Stock allot current month" + list.toString());

        LinearLayout fpsInwardLinearLayout = (LinearLayout) findViewById(R.id.listView_linearLayout_stock_alloted);
        Log.i("Detail", list.toString());
        fpsInwardLinearLayout.removeAllViews();
        for (int position = 0; position < list.size(); position++) {
            LayoutInflater lin = LayoutInflater.from(this);
            fpsInwardLinearLayout.addView(returnView(lin, list.get(position), position));

        }

    }

    /**
     * User allotment view
     */
    private View returnView(LayoutInflater entitle, StockAllotmentDto data, int position) {
        View convertView = entitle.inflate(R.layout.adapter_stock_allotment, null);

        Log.i("Adapter", data.toString());

        TextView productNameTv = (TextView) convertView.findViewById(R.id.allotment_name);
        TextView unitTv = (TextView) convertView.findViewById(R.id.allotment_unit);
        TextView allotmentAlloted = (TextView) convertView.findViewById(R.id.allotment_alloted);
        TextView allotmentIssued = (TextView) convertView.findViewById(R.id.allotment_issued);
        TextView balance = (TextView) convertView.findViewById(R.id.allotment_balance);

        /*NumberFormat format = new DecimalFormat("#0.000");
        format.setRoundingMode(RoundingMode.CEILING);*/
        LinearLayout adapterLayout = (LinearLayout) convertView.findViewById(R.id.linearLayoutTitleAdapter);
        LinearLayout adapterComodityAndUnit = (LinearLayout) convertView.findViewById(R.id.commodityBackground);
        RelativeLayout unitTvLinearLayout = (RelativeLayout) convertView.findViewById(R.id.unitAllotment);
        int[] subColor = getResources().getIntArray(R.array.subColor);
        adapterComodityAndUnit.setBackgroundColor(subColor[position % 6]);
        int[] mainColor = getResources().getIntArray(R.array.mainColor);
        adapterLayout.setBackgroundColor(mainColor[position % 6]);
        int[] unitColor = getResources().getIntArray(R.array.unitColor);
        unitTvLinearLayout.setBackgroundColor(unitColor[position % 6]);


        /*ImageView imageEntitlement = (ImageView) convertView.findViewById(R.id.imageAllotment);
        imageEntitlement.setBackgroundResource(backImg[imageSelection(position)]);
        imageEntitlement.setVisibility(View.INVISIBLE);
        adapterLayout.setId(position);
*/

        ProductDto product = FPSDBHelper.getInstance(this).getProductDetails(data.getProductId());
        Log.i("Product", product.toString());

        if (product != null) {
            productNameTv.setText(product.getName());

            if (GlobalAppState.language.equalsIgnoreCase("hi") && StringUtils.isNotEmpty(product.getLocalProductUnit())) {
//                Util.setTamilText(productNameTv, product.getLocalProductName());
                productNameTv.setText(product.getLocalProductName());
            }


            unitTv.setText(product.getProductUnit());
            if (GlobalAppState.language.equalsIgnoreCase("hi") && StringUtils.isNotEmpty(product.getLocalProductUnit())) {
                Util.setTamilText(unitTv, product.getLocalProductUnit());
            }

        }
        String qty1 = Util.quantityRoundOffFormat(data.getAllotedQuantity());
        allotmentAlloted.setText("" + qty1);

//        Log.i("ReceiveQuantity", "" + data.getRecivQuantity() + "    " + format.format(data.getRecivQuantity()));

        String qty2 = Util.quantityRoundOffFormat(data.getRecivQuantity());
        allotmentIssued.setText("" + qty2);

        double balanceQuantity = data.getAllotedQuantity() - data.getRecivQuantity();
        String qty3 = Util.quantityRoundOffFormat(balanceQuantity);
        balance.setText("" + qty3);
        return convertView;

    }


    private int imageSelection(int position) {
        int colorPosition = position % 4;
        return colorPosition;
    }



   /* //Request to Server
    public void getTransactionRequest() {
        try {

            if (networkConnection.isNetworkAvailable()) {
                Log.e("Transactionrequest", beneficiarySearchDto.toString());
                String login = new Gson().toJson(beneficiarySearchDto);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                String url = "/helpdesk/app/transaction";
                progressBar = new CustomProgressDialog(this);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.STOCK_ALLOTMENT_DETAILS_REQUEST, SyncHandler, RequestType.POST, se, this);
            } else {
                Util.messageBar(this, "No connection is available");
            }

        } catch (Exception e) {

            Log.e("Error", e.toString(), e);
        }

    }
*/


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {

            case STOCK_ALLOTMENT_DETAILS_REQUEST:
                //getStockAllotmentDetailsResponse(message);
                break;
            case ERROR_MSG:
                try {
                    if (progressBar != null) {
                        progressBar.dismiss();
                    }
                }
                catch(Exception e) {}
                Util.messageBar(this, getString(R.string.connectionRefused));

                break;
            default:
                try {
                    if (progressBar != null) {
                        progressBar.dismiss();
                    }
                }
                catch(Exception e) {}
                Util.messageBar(this, "Service Error.Please Try again");
                break;
        }
    }

   /* // After response received from server successfully in android
    private void getStockAllotmentDetailsResponse(Bundle message) {
        try {

            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            Log.e("TransactionInfo", response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            transactionListDto = gson.fromJson(response, TransactionListDto.class);
            int statusCode = transactionListDto.getStatusCode();


            if (statusCode == 0) {
                configureData(transactionListDto.getList());
            }
             else{
                Util.messageBar(this,""+ ErrorCodeDescription.values()[statusCode].getErrorDescription());
            }

            if(progressBar!=null)progressBar.dismiss();

        } catch (Exception e) {
            if(progressBar!=null) progressBar.dismiss();
            Log.e("Error", e.toString(), e);

        }

    }
*/

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, StockManagementActivity.class));
        Util.LoggingQueue(this, "Stock Status activity", "Back pressed Called");
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
