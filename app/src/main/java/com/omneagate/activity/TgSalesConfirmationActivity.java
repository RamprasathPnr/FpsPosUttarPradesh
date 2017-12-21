package com.omneagate.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;

import com.omneagate.DTO.GeneralResponse;
import com.omneagate.DTO.Product;

import com.omneagate.Util.EntitlementResponse;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.LoginData;

import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.Util;
import com.omneagate.Util.XMLUtil;

import com.omneagate.activity.dialog.RationCardDetailDialog;
import com.omneagate.activity.dialog.SaleTransactionCompleted;
import com.omneagate.activity.dialog.TgGenericErrorDialog;
import com.omneagate.exception.FPSException;
import com.omneagate.printer.Usb_Printer;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.omneagate.activity.SplashActivity.context;

public class TgSalesConfirmationActivity extends BaseActivity implements MediaPlayer.OnCompletionListener {

    private List<Product> commodityList;
    private RecyclerView recyclerView;
    private List<Product> tempProductList;
    private ProductViewAdapter productViewAdapter;
    private Button btSubmit, btPrint, btExit,btContinue,btnBack;
    private final String TAG = TgSalesConfirmationActivity.class.getCanonicalName();
    private TextView totalAmount;
    private double amount;
    private int string_len = 12;
    RationCardDetailDialog rationCardDetailDialog;
    private boolean connecting = false;
    TextToSpeech textToSpeech;
    private boolean isSaleCompleted = false;
    ImageView imageViewBack;
    List<Integer> trackList;
    int currentTrack = 0;
    private MediaPlayer mediaPlayer = null;
    private LinearLayout linCashMode;
    private LinearLayout lintWallet;
    private LinearLayout linauthType;
    private RadioGroup radioAuthType;
    private RadioButton radioFingerPrint;
    private RadioButton radioIris;
    private String transaction_id;
    private Timer timer;
    private LogOutTimerTask logoutTimeTask;

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_sales_confirmation);
        trackList= new ArrayList<Integer>();
        commodityList = EntitlementResponse.getInstance().getRcAuthResponse().getItemsAllotedList();
        InitView();

    }


    private void InitView() {
        try {
            setPopUpPage();
            ((TextView) findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo() + " " + getResources().getString(R.string.welcome) + "  " + LoginData.getInstance().getFpsUsername());
            ((TextView) findViewById(R.id.ration_number)).setText(getString(R.string.rc_number) + " " + LoginData.getInstance().getRationCardNo());
            updateDateTime();
            Usb_Printer.getinstance(TgSalesConfirmationActivity.this).check_usb_permission();
            tempProductList = new ArrayList<Product>();
            ((TextView) findViewById(R.id.top_textView)).setText(getString(R.string.sales_top_heading));
            totalAmount = (TextView) findViewById(R.id.totalAmount);
            imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
            imageViewBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            recyclerView = (RecyclerView) findViewById(R.id.products);
            recyclerView.setLayoutManager(new LinearLayoutManager(TgSalesConfirmationActivity.this));
            linCashMode=(LinearLayout)findViewById(R.id.cashMode);
            lintWallet=(LinearLayout)findViewById(R.id.tWalletMode);
            linauthType=(LinearLayout)findViewById(R.id.authType);
            radioAuthType=(RadioGroup)findViewById(R.id.radioAuthType);
            radioFingerPrint=(RadioButton)findViewById(R.id.radioFp);
            radioIris=(RadioButton)findViewById(R.id.radioIris);
            networkConnection = new NetworkConnection(TgSalesConfirmationActivity.this);


            for (Product product : commodityList) {
                if ((product.getQuantityEntered() != null) && (product.getQuantityEntered() > 0.0)) {
                    amount = amount + product.getAmount();
                    tempProductList.add(product);
                }

            }
            totalAmount.setText("" + amount);
            setVoiceFiles(amount);


            Log.e(TAG, "Total Product size : " + commodityList.size());
            productViewAdapter = new ProductViewAdapter(TgSalesConfirmationActivity.this, tempProductList);
            recyclerView.setAdapter(productViewAdapter);

            btContinue =(Button)findViewById(R.id.btContinue);
            btContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int selectedId = radioAuthType.getCheckedRadioButtonId();

                   RadioButton radioAuthType = (RadioButton) findViewById(selectedId);
                    if(radioAuthType.equals(radioFingerPrint)){
                       Intent in =new Intent(TgSalesConfirmationActivity.this,TgWalletPaymentFingerPrintActivity.class);
                       startActivity(in);
                       finish();
                    }else{
                        Intent in =new Intent(TgSalesConfirmationActivity.this,TgWalletPaymentIrisActivity.class);
                        startActivity(in);
                        finish();
                    }

                }
            });

            btnBack= (Button) findViewById(R.id.btnBack);
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            btPrint = (Button) findViewById(R.id.btPrint);
            btPrint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Print();
                }
            });
            btExit = (Button) findViewById(R.id.btExit);
            btExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EntitlementResponse.getInstance().setItemEntitlementList(null);
                    EntitlementResponse.getInstance().setRcAuthResponse(null);
                    EntitlementResponse.getInstance().clear();
                    Intent in = new Intent(TgSalesConfirmationActivity.this, TgDashBoardActivity.class);
                    startActivity(in);
                    finish();

                }
            });

            btSubmit = (Button) findViewById(R.id.btSubmit);
            btSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (networkConnection.isNetworkAvailable()) {
                            new PostRCSale().execute();
                        } else {
                            displayNoInternetDailog();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });
            if(LoginData.getInstance().getCashMode().equalsIgnoreCase("Cash")){
                linCashMode.setVisibility(View.VISIBLE);
                lintWallet.setVisibility(View.GONE);
                linauthType.setVisibility(View.GONE);
                btContinue.setVisibility(View.GONE);
                btSubmit.setVisibility(View.VISIBLE);
                btnBack.setVisibility(View.VISIBLE);

            }else{
               /* linCashMode.setVisibility(View.GONE);
                lintWallet.setVisibility(View.VISIBLE);
                linauthType.setVisibility(View.VISIBLE);
                btContinue.setVisibility(View.VISIBLE);
                btSubmit.setVisibility(View.GONE);
                btnBack.setVisibility(View.GONE);*/
                linCashMode.setVisibility(View.GONE);
                lintWallet.setVisibility(View.VISIBLE);
                linauthType.setVisibility(View.GONE);
                btContinue.setVisibility(View.GONE);
                btSubmit.setVisibility(View.VISIBLE);
                btnBack.setVisibility(View.VISIBLE);
            }

            textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        textToSpeech.setLanguage(Locale.UK);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    void displayNoInternetDailog(){
        TgGenericErrorDialog tgGenericErrorDialog = new TgGenericErrorDialog(TgSalesConfirmationActivity.this, getString(R.string.noNetworkConnection));
        tgGenericErrorDialog.setCanceledOnTouchOutside(false);
        tgGenericErrorDialog.show();
        return;
    }
    void setVoiceFiles(double total){
        try{
            trackList.add(R.raw.motham);
            //int num =(int)total;
            String number = String.valueOf(total);
            for(int i = 0; i < number.length(); i++) {
                //int j = Character.digit(number.charAt(i), 10);
                Log.e(TAG,"digit: " + number.charAt(i));
                switch (number.charAt(i)) {
                    case '1':
                        trackList.add(R.raw.one);
                        break;
                    case '2':
                        trackList.add(R.raw.two);
                        break;
                    case '3':
                        trackList.add(R.raw.three);
                        break;
                    case '4':
                        trackList.add(R.raw.four);
                        break;
                    case '5':
                        trackList.add(R.raw.five);
                        break;
                    case '6':
                        trackList.add(R.raw.six);
                        break;
                    case '7':
                        trackList.add(R.raw.seven);
                        break;
                    case '8':
                        trackList.add(R.raw.eight);
                        break;
                    case '9':
                        trackList.add(R.raw.nine);
                        break;
                    case '.':
                        trackList.add(R.raw.rupee);
                        break;
                    case '0':
                        trackList.add(R.raw.zero);
                        break;
                    default:
                        System.out.println("default");
                        break;

                }
            }
            trackList.add(R.raw.paisa);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void playTeluguVoice(){
        mediaPlayer = MediaPlayer.create(getApplicationContext(), trackList.get(currentTrack));
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.start();
    }


    @Override
    public void onBackPressed() {
        if (!isSaleCompleted) {
            Intent backIntent = new Intent(TgSalesConfirmationActivity.this, TgProductListActivity.class);
            startActivity(backIntent);
            finish();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.release();

        Log.e(TAG, "current Track size : " + currentTrack + " trackList.size : " + (trackList.size() - 1));

        if (currentTrack < trackList.size() - 1) {
            currentTrack++;
            mp = MediaPlayer.create(getApplicationContext(), trackList.get(currentTrack));
            mp.setOnCompletionListener(this);
            mp.start();

        } else {
          /*  rationCardDetailDialog.cancel();
            SaleTransactionCompleted saleTransactionCompleted = new SaleTransactionCompleted(TgSalesConfirmationActivity.this);
            saleTransactionCompleted.setCanceledOnTouchOutside(false);
            saleTransactionCompleted.show();*/
        }

    }

    private class PostRCSale extends AsyncTask<String, GeneralResponse, GeneralResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rationCardDetailDialog = new RationCardDetailDialog(TgSalesConfirmationActivity.this, getString(R.string.loading_please_wait));
            rationCardDetailDialog.setCanceledOnTouchOutside(false);
            rationCardDetailDialog.show();

        }

        @Override
        protected GeneralResponse doInBackground(String... params) {

            return PostRcSale();
        }

        @Override
        protected void onPostExecute(GeneralResponse generalResponse) {
            super.onPostExecute(generalResponse);
          //  rationCardDetailDialog.cancel();
            rationCardDetailDialog.cancel();

            if (generalResponse != null && generalResponse.getRespMsgCode().equals("0")) {
                Log.e("ScanFingerPrintActivity", "dealerAuthResponse :" + generalResponse);
                isSaleCompleted = true;
                btPrint.setVisibility(View.GONE);
                btSubmit.setVisibility(View.GONE);
                btnBack.setVisibility(View.GONE);
                btExit.setVisibility(View.VISIBLE);

             //   new BackgroundSound().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                playVoice();
                SharedPreferences.Editor editor = getSharedPreferences("FPS_TELANGANA", MODE_PRIVATE).edit();
                editor.putString("LastSaleRCNumber", LoginData.getInstance().getRationCardNo());
                editor.commit();


                FPSDBHelper.getInstance(TgSalesConfirmationActivity.this).updateClosingbalance(commodityList);
                //if (GlobalAppState.language != null && !GlobalAppState.language.equalsIgnoreCase("te")) {

                    SaleTransactionCompleted saleTransactionCompleted = new SaleTransactionCompleted(TgSalesConfirmationActivity.this);
                    saleTransactionCompleted.setCanceledOnTouchOutside(false);
                    saleTransactionCompleted.show();
               // }
            }

        }
    }

    public class BackgroundSound extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            playVoice();
            return null;
        }

    }

    public void playVoice(){
        if (GlobalAppState.language != null && GlobalAppState.language.equalsIgnoreCase("te")) {
            playTeluguVoice();
        }else{
            String toSpeak = "Total " + amount + " Rupees";
            textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    public void PrintData() {

        if (GlobalAppState.language.equalsIgnoreCase("te")) {
            TgFPSReportsAllotmentActivity.fontsize = 18;
            Usb_Printer.content = teluguPrintData(tempProductList);
        } else {
            TgFPSReportsAllotmentActivity.fontsize = 20;
            Usb_Printer.content = englishPrintData_siva(tempProductList);
        }
    }

    public void Print() {
        if (!connecting) {
            Usb_Printer.getinstance(TgSalesConfirmationActivity.this).connectPrinter_new();
        } else {
            Toast.makeText(TgSalesConfirmationActivity.this, R.string.usb_printer_connecting, Toast.LENGTH_SHORT).show();
        }
    }

    private GeneralResponse PostRcSale() {
        GeneralResponse generalResponse;
        try {
            Map<String, Object> inputMap = new LinkedHashMap<String, Object>();

            GregorianCalendar gc = new GregorianCalendar();

         /*   int month = gc.get(Calendar.MONTH) + 1;
            int year = gc.get(Calendar.YEAR);*/

            inputMap.put("distCode", LoginData.getInstance().getDistCode());
            inputMap.put("shopNo", LoginData.getInstance().getShopNo());
            inputMap.put("uidNo", LoginData.getInstance().getUid());
            inputMap.put("rationCard", LoginData.getInstance().getRationCardNo());
            inputMap.put("memberId", LoginData.getInstance().getMemberId());
            //inputMap.put("currYear", year);
            //inputMap.put("currMonth", month);
            inputMap.put("currYear","" + LoginData.getInstance().getCurrentYear());
            inputMap.put("currMonth", "" + LoginData.getInstance().getCurrentMonth());
            inputMap.put("transactionId", LoginData.getInstance().getTransactionId());
            inputMap.put("payType","" + LoginData.getInstance().getPayType());
            inputMap.put("password", "1159abbb8b6c0b8a8964210af6954b17");
            inputMap.put("totalAmt", amount);
            inputMap.put("responseTime", LoginData.getInstance().getResponseTime());


            generalResponse = XMLUtil.postRCSaleDetails(inputMap, EntitlementResponse.getInstance().getRcAuthResponse().getCommBDetails().getTypeId(), commodityList);
            transaction_id = generalResponse.getTransactionId();
            PrintData();
        } catch (final FPSException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TgGenericErrorDialog tgGenericErrorDialog =new TgGenericErrorDialog(TgSalesConfirmationActivity.this,""+e.getMessage());
                    tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                    tgGenericErrorDialog.show();
                }
            });
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return generalResponse;
    }

    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
        if (timer != null) {
            timer.cancel();
            Log.i("Main", "cancel timer");
            timer = null;
        }

    }


    private String englishPrintData_siva(List<Product> commodityList) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        NumberFormat formatter = new DecimalFormat("#0.00");
        Date currentData;
        if(Util.needInternalClock && GlobalAppState.serverDate !=null){
            currentData=GlobalAppState.serverDate;
        }else{
            currentData = new Date();
        }
        String date = dateFormat.format(currentData);
        dateFormat = new SimpleDateFormat("kk:mm:ss", Locale.getDefault());
        String time = dateFormat.format(currentData);

        StringBuilder textData = new StringBuilder();
        textData.append("<pre>");
        textData.append("<font size=\"5\">          RECEIPT</font>" + "\n");
        textData.append("<font size=\"5\">Food & Civil Supplies Dept \n    Govt of Telangana</font>" + "\n");
        textData.append("<font size=\"5\">       FPS RECEIPT</font>" + "\n");
        textData.append("<p style=font-size:18px;>");
        textData.append(fixedlenght("Date") + date.trim() + "\n");
        textData.append(fixedlenght("Time") + time.trim() + "\n");
        textData.append("------------------------------------------------------------" + "\n");
        textData.append(fixedlenght("Shop ID") + LoginData.getInstance().getShopNo() + "\n");
        textData.append(fixedlenght("Card Holder") + LoginData.getInstance().getMemberName() + "\n");
        textData.append(fixedlenght("RC ID") + LoginData.getInstance().getRationCardNo() + "\n");
        textData.append(fixedlenght("Trans ID") + transaction_id + "\n");
        textData.append("------------------------------------------------------------" + "\n");
        textData.append("SL " + fixedlenghth("Items", 6) + "| " + "Bal" + " | " + "Sal" + " | " + "Rte" + " | " + "Tot" + "\n");


//        textData.append("SL " + fixedlenghth("Items", 6) + "|" + "Rate" + "|" + "Qty" + "|" + "Amt" + "|" + "Bal.Qty" + "\n");
        textData.append("------------------------------------------------------------" + "\n");
        double value = 0;

        int i = 1;
        for (Product product : commodityList) {

            String productName = product.getDisplayName();
            String unitRate = "" + formatter.format(product.getUnitRate());
            String Amount = "" + formatter.format(product.getAmount());

            if (product.getQuantityEntered() == null) {
                value = 0.0;
            } else {
                value = product.getQuantityEntered();
            }

            String quantity = String.format("" +(int)value);
            if (quantity.length() == 3) {
                quantity = "" + quantity;
            } else if (quantity.length() == 2) {
                quantity = " " + quantity;
            } else if (quantity.length() == 1) {
                quantity = "  " + quantity;
            }

            double balanced = product.getProductBalanceQty() - value;
            int i_balanceQuantity=(int)balanced;
            String balanceQuantity=""+i_balanceQuantity;

            if (balanceQuantity.length() == 3) {
                balanceQuantity = "" + balanceQuantity;
            } else if (balanceQuantity.length() == 2) {
                balanceQuantity = " " + balanceQuantity;
            } else if (balanceQuantity.length() == 1) {
                balanceQuantity = "  " + balanceQuantity;
            }

            if (unitRate.length() == 6) {
                unitRate = "" + unitRate;
            } else if (unitRate.length() == 5) {
                unitRate = "" + unitRate;
            } else if (unitRate.length() == 4) {
                unitRate = " " + unitRate;
            }

            if (Amount.length() == 6) {
                Amount = "" + Amount;
            } else if (Amount.length() == 5) {
                Amount = "" + Amount;
            } else if(Amount.length() == 4){
                Amount = " " + Amount;
            }


            textData.append(i + ") " + fixedlenghth(productName, 6) +""+fixedWithOutTrim(balanceQuantity,5) + " " + fixedWithOutTrim(quantity,5)  + " " + fixedWithOutTrim(unitRate,5) + " " + fixedWithOutTrim(Amount,6)  +"\n");
           // textData.append(i + ") " + fixedlenghth(productName, 6) +"   "+ balanced + " " + quantity  + "  " + unitRate + "  " + Amount  +"\n");
            i++;
        }

        textData.append("------------------------------------------------------------\n");

        String totalAmount=formatter.format(amount);
        if (totalAmount.length() == 6) {
            totalAmount = "" + totalAmount;
        } else if (totalAmount.length() == 5) {
            totalAmount = " " + totalAmount;
        } else if(totalAmount.length() == 4){
            totalAmount = "  " + totalAmount;
        }
        textData.append(" " + context.getString(R.string.bill_total) + "                 " + totalAmount + "\n");

        textData.append("------------------------------------------------------------\n");

        textData.append("</p>");
        textData.append("</pre>");
        textData.append(getResources().getString(R.string.call_issue) + "\n");
        textData.append("\n");
        textData.append("\n");

        return textData.toString();
    }


    private String teluguPrintData(List<Product> commodityList) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        NumberFormat formatter = new DecimalFormat("#0.00");
        Date currentData;
        if(Util.needInternalClock && GlobalAppState.serverDate !=null){
            currentData=GlobalAppState.serverDate;
        }else{
            currentData = new Date();
        }
        String date = dateFormat.format(currentData);
        dateFormat = new SimpleDateFormat("kk:mm:ss", Locale.getDefault());
        String time = dateFormat.format(currentData);

        StringBuilder textData = new StringBuilder();


        textData.append("<pre>");
        textData.append("<font size=\"5\">            స్వీకరణపై</font>" + "\n");
        textData.append("<font size=\"5\">    ఆహార & పౌరసరఫరాల విభాగం \n        తెలంగాణ ప్రభుత్వము </font>" + "\n");
        textData.append("<font size=\"5\">         FPS రసీదు </font>" + "\n");
        textData.append("</pre>");

        String tx_dt = "తేదీ                            :  " + date.trim() + "\n" + "సమయం                    :  " + time.trim() + "\n";
        textData.append(tx_dt.replaceAll(" ", "&nbsp;"));
        textData.append("----------------------------------------------------------------------" + "\n");
        String shop_id = "షాపు ID                       :  " + LoginData.getInstance().getShopNo() + "\n";
        textData.append(shop_id.replaceAll(" ", "&nbsp;"));

        String member_name = "కార్డు కలిగిన వారి పేరు  :  " + LoginData.getInstance().getMemberName() + "\n";
        textData.append(member_name.replaceAll(" ", "&nbsp;"));

        String rc_id = "రేషన్ కార్డు ID               :  " + LoginData.getInstance().getRationCardNo() + "\n";
        textData.append(rc_id.replaceAll(" ", "&nbsp;"));


        String txn_id = "ట్రాన్స్  ID                      :  " + transaction_id + "\n";
        textData.append(txn_id.replaceAll(" ", "&nbsp;"));
        textData.append("<pre>");
        textData.append("-----------------------------------------------------------------------" + "\n");
        textData.append("SL " + fixedlenghth("వస్తువు", 6) + " | " + "బాల" + "  | " + "లిఫ్ట్" + " | " + "రేటు" + " | " + "మొ" + "\n");
//        textData.append("SL " + fixedlenghth("వస్తువు", 6) + "| " + "రేటు" + " | " + "పరిమాణం" + " | " + "మొత్తం" + "\n");
        textData.append("-------------------------------------------------------------------" + "\n");

        int i = 1;
        for (Product product : commodityList) {

            String productName = product.getDisplayName();
            String unitRate = "" + formatter.format(product.getUnitRate());
            String Amount = "" + formatter.format(product.getAmount());
            double value;
            if (product.getQuantityEntered() == null) {
                value = 0.0;
            } else {
                value = product.getQuantityEntered();
            }

            String quantity = String.format("" +(int)value);
            if (quantity.length() == 3) {
                quantity = "" + quantity;
            } else if (quantity.length() == 2) {
                quantity = " " + quantity;
            } else if (quantity.length() == 1) {
                quantity = "  " + quantity;
            }

            double balanced = product.getProductBalanceQty() - value;
            int i_balanceQuantity=(int)balanced;
            String balanceQuantity=""+i_balanceQuantity;

            if (balanceQuantity.length() == 3) {
                balanceQuantity = "" + balanceQuantity;
            } else if (balanceQuantity.length() == 2) {
                balanceQuantity = " " + balanceQuantity;
            } else if (balanceQuantity.length() == 1) {
                balanceQuantity = "  " + balanceQuantity;
            }

            if (unitRate.length() == 6) {
                unitRate = "" + unitRate;
            } else if (unitRate.length() == 5) {
                unitRate = "" + unitRate;
            } else if (unitRate.length() == 4) {
                unitRate = " " + unitRate;
            }

            if (Amount.length() == 6) {
                Amount = "" + Amount;
            } else if (Amount.length() == 5) {
                Amount = "" + Amount;
            } else if(Amount.length() == 4){
                Amount = " " + Amount;
            }

            textData.append(i + ") " + fixedlenghth(productName, 6) +""+fixedWithOutTrim(balanceQuantity,5) + " " + fixedWithOutTrim(quantity,5)  + " " + fixedWithOutTrim(unitRate,5) + " " + fixedWithOutTrim(Amount,6)  +"\n");
          //  textData.append(i + ") " + fixedlenghth(productName, 6) +" "+fixedWithOutTrim(balanceQuantity,4) + " " + fixedWithOutTrim(quantity,4)  + "  " + fixedWithOutTrim(unitRate,5) + " " + fixedWithOutTrim(Amount,6)  +"\n");
//            textData.append(i + ") " + fixedlenghth(productName, 6) +"   "+ balanced + " " + quantity  + "  " + unitRate + "   " + Amount  +"\n");
            i++;
        }
        textData.append("------------------------------------------------------------\n");

        String totalAmount=formatter.format(amount);
        if (totalAmount.length() == 6) {
            totalAmount = "" + totalAmount;
        } else if (totalAmount.length() == 5) {
            totalAmount = " " + totalAmount;
        } else if(totalAmount.length() == 4){
            totalAmount = "  " + totalAmount;
        }
        textData.append(" " + context.getString(R.string.bill_total) + "                 " + totalAmount + "\n");

        textData.append("------------------------------------------------------------" + "\n");
        textData.append("</pre>");
        textData.append(getResources().getString(R.string.call_issue) + "\n");
        textData.append("\n");
        textData.append("\n");
        textData.append("\n");
        textData.append("\n");


        return textData.toString();
    }

    private String fixedWithOutTrim(String text, int length) {
        if (text.length() < length) {
            while (text.length() < length) {
                text = text + " ";
            }
        } else {
            text = text.substring(0, length);
        }
        return text;
    }
   /* private String new_englishPrintData(List<Product> commodityList) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        Date currentData = new Date();
        String date = dateFormat.format(currentData);
        dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String time = dateFormat.format(currentData);

        StringBuilder textData = new StringBuilder();
        textData.append("<pre>");
        textData.append("<font size=\"5\">          RECEIPT</font>" + "\n");
        textData.append("<font size=\"5\">Food & Civil Supplies Dept \n    Govt of Telangana</font>" + "\n");
        textData.append("<font size=\"5\">       FPS RECEIPT</font>" + "\n");
        textData.append("<p style=font-size:18px;>");
        textData.append(fixedlenght("Date") + date.trim() + "\n");
        textData.append(fixedlenght("Time") + time.trim() + "\n");
        textData.append("------------------------------------------------------------" + "\n");
        textData.append(fixedlenght("Shop ID") + LoginData.getInstance().getShopNo() + "\n");
        textData.append(fixedlenght("RC ID") + LoginData.getInstance().getRationCardNo() + "\n");
        textData.append(fixedlenght("Transaction ID") + transaction_id + "\n");
        textData.append("------------------------------------------------------------" + "\n");
        textData.append("SL " + fixedlenghth("Items", 6) + "|" + "Rate" + "|" + "Qty" + "|" + "Amt" + "|" + "Bal.Qty" + "\n");
        textData.append("------------------------------------------------------------" + "\n");
        int i = 1;
        for (Product product : commodityList) {
            String productName = product.getDisplayName();
            String unitRate = "" + product.getUnitRate();
            String Amount = "" + product.getAmount();
            String balanced_qty = "" + product.getProductBalanceQty();
            double value;
            if (product.getQuantityEntered() == null) {
                value = 0.0;
            } else {
                value = product.getQuantityEntered();
            }

            String quantity = String.format("" + value);
            if (quantity.length() == 3) {
                quantity = "  " + quantity;
            } else if (quantity.length() == 4) {
                quantity = " " + quantity;
            }
            //   textData.append(fixedlenghth(productName, 6) + "       " + quantity +"\n");
            textData.append(i + ") " + fixedlenghth(productName, 6) + "  " + unitRate + "  " + quantity + "   " + Amount + " " + balanced_qty +"\n");
            i++;
        }

        textData.append("------------------------------------------------------------\n");
        textData.append(" " + context.getString(R.string.bill_total) + "              " + amount + "\n");
        textData.append("------------------------------------------------------------\n");

        textData.append("</p>");
        textData.append("</pre>");
        textData.append(getResources().getString(R.string.call_issue) + "\n");
        textData.append("\n");
        textData.append("\n");

        return textData.toString();
    }*/


    private String fixedlenght(String text) {

        text = text.trim();
        if (text.length() < string_len) {

            while (text.length() < string_len) {
                text = text + " ";
            }
            text = text.substring(0, string_len - 1) + " : ";
        }
        Log.e("text", text);
        return text/*.replaceAll(" ", "&nbsp;")*/;
    }

    private String fixedlenghth(String text, int length) {

        text = text.trim();
        if (text.length() < length) {
            while (text.length() < length) {
                text = text + " ";
            }
        } else {
            text = text.substring(0, length);
        }
        return text;
    }

    public class ProductViewAdapter extends RecyclerView.Adapter<ProductViewAdapter.ProductHolder> {
        private Context context;

        private LayoutInflater mLayoutInflater;
        private List<Product> fpsProductList;


        public ProductViewAdapter(Context context, List<Product> fpsProductList) {
            this.context = context;
            this.fpsProductList = fpsProductList;
            this.mLayoutInflater = LayoutInflater.from(context);
        }


        @Override
        public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = mLayoutInflater.inflate(R.layout.list_item_products_view, parent, false);
            return new ProductHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ProductHolder holder, final int position) {
            holder.tvItemName.setText(fpsProductList.get(position).getDisplayName());
            // holder.tvItemQtyRequested.setText("" + fpsProductList.get(position).getProductBalanceQty);

            if (fpsProductList.get(position).getQuantityEntered() != null) {
                holder.tvItemQtyRequested.setText("" + fpsProductList.get(position).getQuantityEntered());
            } else {
                holder.tvItemQtyRequested.setText("");
            }
            holder.tvItemUnits.setText(fpsProductList.get(position).getUnitName());


            if (fpsProductList.get(position).getAmount() != null) {
                holder.tvItemQtyAmount.setText("" + fpsProductList.get(position).getAmount());
            }


        }

        @Override
        public int getItemCount() {
            return fpsProductList.size();
        }

        public class ProductHolder extends RecyclerView.ViewHolder {
            private TextView tvItemName;
            private TextView tvItemUnits;
            private TextView tvItemQtyRequested;
            private TextView tvItemQtyAmount;

            public ProductHolder(View v) {
                super(v);
                tvItemName = (TextView) v.findViewById(R.id.tvItemName);
                tvItemQtyRequested = (TextView) v.findViewById(R.id.tvquantityRequested);
                tvItemUnits = (TextView) v.findViewById(R.id.tvItemUnit);
                tvItemQtyAmount = (TextView) v.findViewById(R.id.tvItemAmount);

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        timer = new Timer();
        Log.i("Main", "Invoking logout timer");
        logoutTimeTask = new LogOutTimerTask();
        timer.schedule(logoutTimeTask, XMLUtil.autologOutTime);

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

    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            //redirect user to login screen
//            Intent i = new Intent(TgSalesConfirmationActivity.this, TgLoginActivity.class);
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(i);
//            finish();
        }
    }
}
