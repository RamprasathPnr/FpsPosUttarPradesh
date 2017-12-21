package com.omneagate.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.EnumDTO.FpsStockInwardSelect;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.GodownStockOutwardDto;
import com.omneagate.DTO.ProductDto;
import com.omneagate.DTO.StockRequestDto;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.FpsStockInwardSubmit;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.StockInwardDialog;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created for StockInwardConfirmActivity.
 */
public class StockInwardConfirmActivity extends BaseActivity {

    // Godown Dto list
    List<GodownStockOutwardDto> fpsStockInwardDetailList;

    String fpsStockInward, godownNameReceived;

    FpsStockInwardSelect fpsStockInward_Select;

    long timeOnClick = 0l;

    StockInwardDialog stockInwardDialog;


    public static StockRequestDto stockRequestDto;

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case FPS_INTENT_REQUEST:
                getStockResponse(message);
                break;

            case ERROR_MSG:

                Util.messageBar(this, getString(R.string.connectionRefused));

                break;
            default:
                dismissDialog();
                String error = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(12012));
                Util.messageBar(this, error);
             /*   StockInwardDialog stockInwardDialog = new StockInwardDialog(this);
                stockInwardDialog.show();*/
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fpsstockinward_confirm);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        fpsStockInward = getIntent().getExtras().getString("stockInwardList");
        godownNameReceived = getIntent().getExtras().getString("godownName");
        Log.e("fpsStockInward", fpsStockInward);
        fpsStockInward_Select = new Gson().fromJson(fpsStockInward, FpsStockInwardSelect.class);
        fpsStockInwardDetailList = fpsStockInward_Select.getFpsStockInwardconformList();
        networkConnection = new NetworkConnection(StockInwardConfirmActivity.this);
        OpeningPage();
        findViewById(R.id.confirmBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.confirmBtn).setOnClickListener(null);
                findViewById(R.id.confirmBtn).setBackgroundColor(Color.LTGRAY);
//                Log.e("Inward confirm page","confirmBtn onclick...");
                addToServer();
            }
        });
        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    private void addToServer() {
        if (SystemClock.elapsedRealtime() - timeOnClick < 4000) {
            return;
        }
        progressBar = new CustomProgressDialog(this);
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.show();
        timeOnClick = SystemClock.elapsedRealtime();
        findViewById(R.id.cancel_button).setEnabled(false);
        findViewById(R.id.confirmBtn).setEnabled(false);
        findViewById(R.id.cancel_button).setClickable(false);
        findViewById(R.id.confirmBtn).setClickable(false);
        findViewById(R.id.cancel_button).setOnClickListener(null);
        findViewById(R.id.confirmBtn).setOnClickListener(null);
        for (GodownStockOutwardDto gowDownStock : fpsStockInwardDetailList) {
            FPSDBHelper.getInstance(this).getStockExists(gowDownStock);
        }
        FpsStockInwardSubmit fpsStockInwardSubmit = new FpsStockInwardSubmit(StockInwardConfirmActivity.this, fpsStockInwardDetailList);
        fpsStockInwardSubmit.submitServer(httpConnection, SyncHandler);

    }


    public void dismissDialog() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        }
        catch(Exception e) {}
        findViewById(R.id.cancel_button).setEnabled(true);
        findViewById(R.id.confirmBtn).setEnabled(true);
        findViewById(R.id.cancel_button).setClickable(true);
        findViewById(R.id.confirmBtn).setClickable(true);
        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.confirmBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.confirmBtn).setOnClickListener(null);
                findViewById(R.id.confirmBtn).setBackgroundColor(Color.LTGRAY);
                addToServer();
            }
        });
    }

    private void OpeningPage() {
        TextView topTv = (TextView) findViewById(R.id.top_textView);
        Util.setTamilText(topTv, R.string.inward_transit_confirm);
        setUpPopUpPage();

        Util.setTamilText(((TextView) findViewById(R.id.productlabel)), R.string.product);
        Util.setTamilText(((TextView) findViewById(R.id.productlabel2)), R.string.product);
        Util.setTamilText(((TextView) findViewById(R.id.productlabel3)), R.string.product);
        Util.setTamilText(((TextView) findViewById(R.id.productlabel4)), R.string.product);

        Util.setTamilText((TextView) findViewById(R.id.quantity), R.string.receivedQuantity);
        Util.setTamilText((TextView) findViewById(R.id.quantity2), R.string.receivedQuantity);
        Util.setTamilText((TextView) findViewById(R.id.quantity3), R.string.receivedQuantity);
        Util.setTamilText((TextView) findViewById(R.id.quantity4), R.string.receivedQuantity);

        Util.setTamilText((TextView) findViewById(R.id.currentMonths), R.string.monthDatas);
        Util.setTamilText((TextView) findViewById(R.id.nextMonths), R.string.monthDatas);
        Util.setTamilText((TextView) findViewById(R.id.previousMonths), R.string.monthDatas);
        Util.setTamilText((TextView) findViewById(R.id.twoPreviousMonths), R.string.monthDatas);

        Util.setTamilText((TextView) findViewById(R.id.nextMonthTotal), R.string.totalPrds);
        Util.setTamilText((TextView) findViewById(R.id.currentMonthTotal), R.string.totalPrds);
        Util.setTamilText((TextView) findViewById(R.id.previousMonthTotal), R.string.totalPrds);
        Util.setTamilText((TextView) findViewById(R.id.twoPreviousMonthTotal), R.string.totalPrds);

        LinearLayout fpsInwardLayoutNext = (LinearLayout) findViewById(R.id.listView_fps_stock_inward_received_next);
        LinearLayout fpsInwardLayoutCurrent = (LinearLayout) findViewById(R.id.listView_fps_stock_inward_received_current);
        LinearLayout fpsInwardLayoutPrevious = (LinearLayout) findViewById(R.id.listView_fps_stock_inward_received_previous);
        LinearLayout fpsInwardLayoutTwoPrevious = (LinearLayout) findViewById(R.id.listView_fps_stock_inward_received_two_previous);

        Util.setTamilText((TextView) findViewById(R.id.cancel_button), R.string.cancel);
        Util.setTamilText((TextView) findViewById(R.id.confirmBtn), R.string.confirm);

        getMonthAndYear();
        boolean currentMonth = false;
        boolean nextMonth = false;
        boolean previousMonth = false;
        boolean twoPreviousMonth = false;
        LayoutInflater lin = LayoutInflater.from(this);
        DateTime dateTime = new DateTime();
        int currentCount = 0;
        int nextCount = 0;
        int previousCount = 0;
        int twoPreviousCount = 0;
        for (GodownStockOutwardDto stockList : fpsStockInwardDetailList) {

            if (dateTime.getMonthOfYear() == stockList.getMonth()) {
                fpsInwardLayoutCurrent.addView(returnView(lin, stockList));
                currentCount++;
                currentMonth = true;
            }

            else if (dateTime.getMonthOfYear() > stockList.getMonth()) {
                if (dateTime.getMonthOfYear() - stockList.getMonth() == 1) {
                    fpsInwardLayoutPrevious.addView(returnView(lin, stockList));
                    previousCount++;
                    previousMonth = true;
                } else if (dateTime.getMonthOfYear() - stockList.getMonth() == 2) {
                    fpsInwardLayoutTwoPrevious.addView(returnView(lin, stockList));
                    twoPreviousCount++;
                    twoPreviousMonth = true;
                }
                // Year end advance stock for 1 month
                else {
                    fpsInwardLayoutNext.addView(returnView(lin, stockList));
                    nextCount++;
                    nextMonth = true;
                }
            }

            else if (dateTime.getMonthOfYear() < stockList.getMonth()) {
                Log.e("next month year..",""+dateTime.getYear());
                if (dateTime.getYear() == stockList.getYear()) {
                    fpsInwardLayoutNext.addView(returnView(lin, stockList));
                    nextCount++;
                    nextMonth = true;
                }
                // Year end previous stock
                else if (dateTime.getYear() > stockList.getYear()) {
                    if(stockList.getMonth() == 12) {
                        if(dateTime.getMonthOfYear() == 1) {
                            fpsInwardLayoutPrevious.addView(returnView(lin, stockList));
                            previousCount++;
                            previousMonth = true;
                        }
                        else if(dateTime.getMonthOfYear() == 2) {
                            fpsInwardLayoutTwoPrevious.addView(returnView(lin, stockList));
                            twoPreviousCount++;
                            twoPreviousMonth = true;
                        }
                    } else if(stockList.getMonth() == 11) {
                        if(dateTime.getMonthOfYear() == 1) {
                            fpsInwardLayoutTwoPrevious.addView(returnView(lin, stockList));
                            twoPreviousCount++;
                            twoPreviousMonth = true;
                        }
                    }
                }
            }

        }

        ((TextView)findViewById(R.id.currentMonthCount)).setText(String.valueOf(currentCount));
        ((TextView)findViewById(R.id.nextMonthCount)).setText(String.valueOf(nextCount));
        ((TextView)findViewById(R.id.previousMonthCount)).setText(String.valueOf(previousCount));
        ((TextView)findViewById(R.id.twoPreviousMonthCount)).setText(String.valueOf(twoPreviousCount));

        if (!currentMonth) {
            findViewById(R.id.current_month).setVisibility(View.GONE);
        }
        if (!nextMonth) {
            findViewById(R.id.next_month).setVisibility(View.GONE);
        }
        if (!previousMonth) {
            findViewById(R.id.previous_month).setVisibility(View.GONE);
        }
        if (!twoPreviousMonth) {
            findViewById(R.id.two_previous_month).setVisibility(View.GONE);
        }
    }


    @Override
    public void onBackPressed() {
        /*Intent intent = new Intent(StockInwardConfirmActivity.this, AdvancedStockConfirmActivity.class);
        intent.putExtra("stockInwardList", fpsStockInward);
        startActivity(intent);
        finish();*/
        Intent intent = new Intent(StockInwardConfirmActivity.this, FpsStockInwardDetailActivity.class);
        intent.putExtra("stockInwardList", fpsStockInward);
        intent.putExtra("godown", godownNameReceived);
        intent.putExtra("submitBoolean", true);
        startActivity(intent);
        finish();
    }

    private View returnView(LayoutInflater entitle, GodownStockOutwardDto fpsStockInwardData) {
        View convertView = entitle.inflate(R.layout.adapter_fpsinward_confirm, null);
        TextView productName = (TextView) convertView.findViewById(R.id.fpsInvardDetailProductId);
        TextView productUnit = (TextView) convertView.findViewById(R.id.fpsInvardDetailUnitId);
        TextView quantity = (TextView) convertView.findViewById(R.id.fpsInvardDetailReceivedQuantity);
        ProductDto productDetail = FPSDBHelper.getInstance(this).getProductDetails(fpsStockInwardData.getProductId());
        Log.i("Product", productDetail.toString());
        /*NumberFormat format = new DecimalFormat("#0.00");
        format.setRoundingMode(RoundingMode.CEILING);*/
        if (productDetail != null) {
            productName.setText(productDetail.getName());
            if (GlobalAppState.language.equalsIgnoreCase("hi") && StringUtils.isNotEmpty(productDetail.getLocalProductUnit())) {
//                Util.setTamilText(productName, productDetail.getLocalProductName());
                productName.setText(productDetail.getLocalProductName());
            }

            productUnit.setText(productDetail.getProductUnit());
            if (GlobalAppState.language.equalsIgnoreCase("hi") && StringUtils.isNotEmpty(productDetail.getLocalProductUnit())) {
                Util.setTamilText(productUnit, productDetail.getLocalProductUnit());
            }
        }

        String qty = Util.quantityRoundOffFormat(fpsStockInwardData.getQuantity());
        quantity.setText(qty);
        return convertView;
    }


    private void getMonthAndYear() {
        DateTime dateTwoPrev = new DateTime().minusMonths(2);
        DateTime datePrev = new DateTime().minusMonths(1);
        DateTime dateNext = new DateTime().plusMonths(1);
        DateTime dateNow = new DateTime();
        String[] monthArray = getResources().getStringArray(R.array.month_list);
        String currentMonth = monthArray[dateNow.getMonthOfYear() - 1];
        String nextMonth = monthArray[dateNext.getMonthOfYear() - 1];
        String prevMonth = monthArray[datePrev.getMonthOfYear() - 1];
        String twoPrevMonth = monthArray[dateTwoPrev.getMonthOfYear() - 1];
        Util.setTamilText((TextView) findViewById(R.id.currentMonthName), currentMonth);
        Util.setTamilText((TextView) findViewById(R.id.nextMonthName), nextMonth);
        Util.setTamilText((TextView) findViewById(R.id.previousMonthName), prevMonth);
        Util.setTamilText((TextView) findViewById(R.id.twoPreviousMonthName), twoPrevMonth);
        ((TextView) findViewById(R.id.currentMonthYear)).setText(String.valueOf(dateNow.getYear()));
        ((TextView) findViewById(R.id.nextMonthYear)).setText(String.valueOf(dateNext.getYear()));
        ((TextView) findViewById(R.id.previousMonthYear)).setText(String.valueOf(datePrev.getYear()));
        ((TextView) findViewById(R.id.twoPreviousMonthYear)).setText(String.valueOf(dateTwoPrev.getYear()));
    }

    // After response received from server successfully in android
    public void getStockResponse(Bundle message) {
        dismissDialog();
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            Log.e("response _data", response);
            Util.LoggingQueue(this, "Stock Inward detail activity", "Response request:" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();


            String stock_validation = "" + FPSDBHelper.getInstance(this).getMasterData("stock_validation");
            if (stock_validation != null && StringUtils.isNotEmpty(stock_validation) && (!stock_validation.equalsIgnoreCase("null"))) {
                if (stock_validation.equalsIgnoreCase("0")) {
                    FPSDBHelper.getInstance(this).updateReceivedQuantityTwo(stockRequestDto, true);
                } else if (stock_validation.equalsIgnoreCase("1")) {
                    FPSDBHelper.getInstance(this).updateReceivedQuantity(stockRequestDto, true);
                }
            }
            else {
                FPSDBHelper.getInstance(this).updateReceivedQuantityTwo(stockRequestDto, true);
            }

            StockRequestDto stockRequestDto = gson.fromJson(response, StockRequestDto.class);
            List<StockRequestDto.ProductList> productLists = stockRequestDto.getProductLists();
            for (StockRequestDto.ProductList prodList : productLists) {
                try {
                    if (prodList.getStatusCode() == 0 || prodList.getStatusCode() == 12011) {
                        FPSDBHelper.getInstance(this). updateStockInward(stockRequestDto.getReferenceNo());
                        StockInwardDialog stockInwardDialog = new StockInwardDialog(this);
                        stockInwardDialog.show();
                    } else {
                        String error = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(prodList.getStatusCode()));
                        if (StringUtils.isEmpty(error)) {
                            error =  getString(R.string.connectionRefused);
                        }
                        Util.messageBar(this, error);
                    }
                }
                catch(Exception e) {}
            }





            /*GodownStockOutwardDto godownStockOutwardDto = gson.fromJson(response, GodownStockOutwardDto.class);
            int statusCode = godownStockOutwardDto.getStatusCode();




            if (statusCode == 0 || statusCode == 12011) {
                FPSDBHelper.getInstance(this).updateReceivedQuantity(stockRequestDto, true);
                FPSDBHelper.getInstance(this).updateStockInward(fpsStockInwardDetailList.get(0).getReferenceNo());
                Util.LoggingQueue(this, "Stock Inward Detail activity", "Inserting into Database");
                StockInwardDialog stockInwardDialog = new StockInwardDialog(this);
                stockInwardDialog.show();
            } else {
                Util.LoggingQueue(this, "Stock Inward detail activity", "Error in insertion");
                String error = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(godownStockOutwardDto.getStatusCode()));
                if(StringUtils.isEmpty(error)){
                    error = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(12012));
                }
                Util.messageBar(this, error);
            }*/


        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
            Log.e("ServerResponseError", e.toString(), e);
//            String error = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(12012));
            String error = getString(R.string.internalError);
            Util.messageBar(this, error);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeAllDialogs();
    }

    private void removeAllDialogs() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }

        try {
            if ((stockInwardDialog != null) && stockInwardDialog.isShowing()) {
                stockInwardDialog.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            stockInwardDialog = null;
        }
    }

}


