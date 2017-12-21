package com.omneagate.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.Product;
import com.omneagate.DTO.RCLastTransaction;
import com.omneagate.Util.EntitlementResponse;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.ProductMap;
import com.omneagate.Util.Util;
import com.omneagate.Util.XMLUtil;
import com.omneagate.printer.Usb_Printer;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.omneagate.activity.SplashActivity.context;

public class TgViewLastTransactionActivity extends BaseActivity implements View.OnClickListener {


    private ImageView imageViewBack;
    private TextView pageHeader;

    private Button btnPrintAllotment,btnBack;
    private RCLastTransaction rcLastTransaction;
    private List<Product> lastTxProductList;
    private List<Product> closingbalanceList;
    private List<Product> tempProductList;

    private static final String AFSC_CARD_TYPE = "4";
    private static final String FSC_CARD_TYPE = "5";
    private static final String AAP_CARD_TYPE = "9";

    private static final String RICEAAP_CODE = "106";
    private static final String RICEAFSC_CODE = "107";
    private static final String RICEFSC_CODE = "108";
    public static int fontsize = 20;
    private int string_len = 12;
    private boolean connecting = false;
    private Timer timer;
    private LogOutTimerTask logoutTimeTask;
    private String transactionID;
    private final String TAG=TgViewLastTransactionActivity.class.getCanonicalName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_view_last_transaction);
        initView();
    }


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    private void initView() {
        setPopUpPage();
        ((TextView) findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo() + " " + getResources().getString(R.string.welcome) + "  " + LoginData.getInstance().getFpsUsername());
        TextView fpsShopId;
        TextView rationCardNumber;
        TextView receiptNumber;
        TextView dateTime;
        RecyclerView listProducts;
        ((TextView) findViewById(R.id.top_textView)).setText(R.string.last_trensaction);
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);
        pageHeader = (TextView) findViewById(R.id.page_header);
        fpsShopId = (TextView) findViewById(R.id.fps_shop_id);
        rationCardNumber = (TextView) findViewById(R.id.rationCardNumber);
        receiptNumber = (TextView) findViewById(R.id.receiptNumber);
        dateTime = (TextView) findViewById(R.id.date_time);
        listProducts = (RecyclerView) findViewById(R.id.list_products);
        listProducts.setLayoutManager(new LinearLayoutManager(TgViewLastTransactionActivity.this));
        btnPrintAllotment = (Button) findViewById(R.id.btnPrintAllotment);
        btnPrintAllotment.setOnClickListener(this);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        String shop_id = getIntent().getStringExtra("rcNumber");
        String Fps_id = getIntent().getStringExtra("Fps_id");
        fpsShopId.setText(shop_id);
        rationCardNumber.setText(Fps_id);
        closingbalanceList = FPSDBHelper.getInstance(TgViewLastTransactionActivity.this).getAllClosingBalance();

        tempProductList =new ArrayList<>();
        rcLastTransaction = (RCLastTransaction) getIntent().getSerializableExtra("rcLastTransaction");
        lastTxProductList = rcLastTransaction.getProductList();
        transactionID=rcLastTransaction.getTransactionId();

        for (int i = 0; i < lastTxProductList.size(); i++) {
            Log.e(TAG,"balanceQuantity"+lastTxProductList.get(i).getProductBalanceQty());

            for (int j = 0; j < closingbalanceList.size(); j++) {

                if (lastTxProductList.get(i).getCode().equals(closingbalanceList.get(j).getCode())) {
                    lastTxProductList.get(i).setUnitRate(closingbalanceList.get(j).getUnitRate());
                    lastTxProductList.get(i).setClosingBalance(closingbalanceList.get(j).getClosingBalance());
                }
            }

        }

        for (int i = 0; i < lastTxProductList.size(); i++) {
            if (lastTxProductList.get(i).getCode().equals(ProductMap.RICE_CODE)) {
                String cardType = rcLastTransaction.getMemberId();
                Log.e("LastTransaction", "TgProductListActivity : " + cardType);
                if (AFSC_CARD_TYPE.equals(cardType)) {
                    lastTxProductList.get(i).setUnitRate(getProductPrice(RICEAFSC_CODE));
                    lastTxProductList.get(i).setClosingBalance(getClosingBalance(RICEAFSC_CODE));

                } else if (FSC_CARD_TYPE.equals(cardType)) {
                    lastTxProductList.get(i).setUnitRate(getProductPrice(RICEFSC_CODE));
                    lastTxProductList.get(i).setClosingBalance(getClosingBalance(RICEFSC_CODE));
                    //   Log.e(TAG,"RICEFSC_CODE : "+getProductPrice(RICEFSC_CODE));
                } else if (AAP_CARD_TYPE.equals(cardType)) {
                    lastTxProductList.get(i).setUnitRate(getProductPrice(RICEAAP_CODE));
                    lastTxProductList.get(i).setClosingBalance(getClosingBalance(RICEAAP_CODE));
                    // Log.e(TAG,"RICEAAP_CODE : "+getProductPrice(RICEAAP_CODE));
                }

            }

        }

        ((TextView) findViewById(R.id.total_amount)).setText("" + rcLastTransaction.getTotalAmt());

        for (Product product : lastTxProductList) {
            if ((product.getIssuedQuantity() != null) && (product.getIssuedQuantity() > 0.0)) {
                tempProductList.add(product);
            }

        }
        if (GlobalAppState.language.equalsIgnoreCase("te")) {
            TgFPSReportsAllotmentActivity.fontsize = 18;
            Usb_Printer.content = teluguPrintData(tempProductList);
        } else {
            TgFPSReportsAllotmentActivity.fontsize = 19;
            Usb_Printer.content = new_englishPrintData(tempProductList);
        }

        receiptNumber.setText(rcLastTransaction.getTransactionId());
        dateTime.setText(rcLastTransaction.getTransDate());
        ViewLastTransactionAdapter adapter = new ViewLastTransactionAdapter(TgViewLastTransactionActivity.this, tempProductList);
        listProducts.setAdapter(adapter);


    }

    public Double getProductPrice(String productCode) {
        Double value = 0.0;
        for (int i = 0; i < closingbalanceList.size(); i++) {
            if (closingbalanceList.get(i).getCode().equals(productCode)) {
                value = closingbalanceList.get(i).getUnitRate();
            }

        }
        System.out.println(value);
        return value;


    }

    public Double getClosingBalance(String productCode) {
        Double value = 0.0;
        for (int i = 0; i < closingbalanceList.size(); i++) {
            if (closingbalanceList.get(i).getCode().equals(productCode)) {
                value = closingbalanceList.get(i).getClosingBalance();
            }

        }
        System.out.println(value);
        return value;


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPrintAllotment:
                if (!connecting) {
                    Usb_Printer.getinstance(TgViewLastTransactionActivity.this).connectPrinter_new();
                } else {
                    Toast.makeText(TgViewLastTransactionActivity.this, R.string.usb_printer_connecting, Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.btnBack:
                onBackPressed();
                break;

            case R.id.imageViewBack:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(TgViewLastTransactionActivity.this, TgSalesActivity.class);
        startActivity(backIntent);
        finish();
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
        textData.append("<font size=\"5\">        చివరి వ్యవహారం</font>" + "\n");


        textData.append("</pre>");

        String tx_dt = "తేదీ                 :  " + date.trim() + "\n" + "సమయం         :  " + time.trim() + "\n";
        textData.append(tx_dt.replaceAll(" ", "&nbsp;"));
        textData.append("----------------------------------------------------------------------" + "\n");
        String shop_id = "షాపు ID           :  " + rcLastTransaction.getShopNo() + "\n";
        textData.append(shop_id.replaceAll(" ", "&nbsp;"));

        String rc_id = "రేషన్ కార్డు ID   :  " + rcLastTransaction.getRationCard() + "\n";
        textData.append(rc_id.replaceAll(" ", "&nbsp;"));

        String txn_id = "ట్రాన్స్  ID          :  " + transactionID + "\n";
        textData.append(txn_id.replaceAll(" ", "&nbsp;"));

        textData.append("<pre>");
        textData.append("----------------------------------------------------------------------" + "\n");
        // textData.append(fixedlenghth("వస్తువు", 6) + "       " + "పరిమాణం" +"\n");
        //textData.append("SL " + fixedlenghth("వస్తువు", 6) + "| " + "రేటు" + " | " + "పరిమాణం" + " | " + "మొత్తం" + "\n");
        textData.append("SL " + fixedlenghth("వస్తువు", 6) + " | " + "బాల" + "  | " + "లిఫ్ట్" + " | " + "రేటు" + " | " + "మొ" + "\n");
        textData.append("-------------------------------------------------------------------" + "\n");

        int i = 1;
        for (Product product : commodityList) {

            String productName = product.getDisplayName();
            String unitRate = "" + formatter.format(product.getUnitRate());
            String Amount = "" + formatter.format(product.getUnitRate() *product.getIssuedQuantity());
            double d_balanceQuatity=product.getProductBalanceQty();
            int i_balanceQuantity=(int)d_balanceQuatity;
            String balanceQuantity=""+i_balanceQuantity;
            double value;
            if (product.getIssuedQuantity() == null) {
                value = 0.0;
            } else {
                value = product.getIssuedQuantity();
            }
            String quantity = String.format("" + (int)value);
            if (quantity.length() == 3) {
                quantity = "" + quantity;
            } else if (quantity.length() == 2) {
                quantity = " " + quantity;
            } else if (quantity.length() == 1) {
                quantity = "  " + quantity;
            }


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
            textData.append(i + ") " + fixedlenghth(productName, 6) +" "+fixedWithOutTrim(balanceQuantity,5) + " " + fixedWithOutTrim(quantity,4)  + " " + fixedWithOutTrim(unitRate,5) + " " + fixedWithOutTrim(Amount,6)  +"\n");
           // textData.append(i + ") " + fixedlenghth(productName, 6) +" "+fixedWithOutTrim(balanceQuantity,4) + "  " + fixedWithOutTrim(quantity,4)  + "  " + fixedWithOutTrim(unitRate,5) + " " + fixedWithOutTrim(Amount,6)  +"\n");
            //textData.append(i + ") " + fixedlenghth(productName, 6) +"   "+ balanceQuantity + " " + quantity  + "  " + unitRate + "   " + Amount  +"\n");
            i++;
        }
        textData.append("------------------------------------------------------------\n");

        String totalAmount=formatter.format(rcLastTransaction.getTotalAmt());
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


    private String new_englishPrintData(List<Product> commodityList) {

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
        textData.append("<font size=\"5\">  Food & Civil Supplies Dept \n       Govt of Telangana</font>" + "\n");
        textData.append("<font size=\"5\">       FPS RECEIPT</font>" + "\n");
        textData.append("<font size=\"5\">     LAST TRANSACTION</font>" + "\n");
        textData.append("<p style=font-size:18px;>");
        textData.append(fixedlenght("Date") + date.trim() + "\n");
        textData.append(fixedlenght("Time") + time.trim() + "\n");
        textData.append("------------------------------------------------------------" + "\n");
        textData.append(fixedlenght("Shop ID") + rcLastTransaction.getShopNo() + "\n");
        textData.append(fixedlenght("RC ID") + rcLastTransaction.getRationCard() + "\n");
        textData.append(fixedlenght("Trans ID") + transactionID + "\n");

        // textData.append(fixedlenghth("Item", 6) + "       " + "Qty" +"\n");
        textData.append("------------------------------------------------------------" + "\n");
        textData.append("SL " + fixedlenghth("Items", 6) + "|" + "Bal" + " | " + "sal" + " | " + "Rte" + " | " + "Tot" + "\n");

        //  textData.append("SL " + fixedlenghth("Items", 6) + "| " + "Rate" + "  | " + "Qty" + "   | " + "Amt" + "\n");
        textData.append("------------------------------------------------------------" + "\n");
        int i = 1;
        for (Product product : commodityList) {

            String productName = product.getDisplayName();
            String unitRate = "" + formatter.format(product.getUnitRate());
            double d_balanceQuatity=product.getProductBalanceQty();
            int i_balanceQuantity=(int)d_balanceQuatity;
            String balanceQuantity=""+i_balanceQuantity;
            String Amount = "" + formatter.format(product.getUnitRate() * product.getIssuedQuantity());
            double value;
            if (product.getIssuedQuantity() == null) {
                value = 0.0;
            } else {
                value = product.getIssuedQuantity();
            }

            String quantity = String.format("" + (int)value);

            if (quantity.length() == 3) {
                quantity = "" + quantity;
            } else if (quantity.length() == 2) {
                quantity = " " + quantity;
            } else if (quantity.length() == 1) {
                quantity = "  " + quantity;
            }


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

         //   textData.append(i + ") " + fixedlenghth(productName, 6) +" "+ balanceQuantity + " " + quantity  + " " + unitRate + " " + Amount  +"\n");
            textData.append(i + ") " + fixedlenghth(productName, 6) +""+fixedWithOutTrim(balanceQuantity,5) + " " + fixedWithOutTrim(quantity,5)  + " " + fixedWithOutTrim(unitRate,5) + " " + fixedWithOutTrim(Amount,6)  +"\n");
            // textData.append(i + ") " + fixedlenghth(productName, 6) + "  " + unitRate + "  " + quantity + "     " + Amount + "\n\n");
            i++;
        }

        textData.append("------------------------------------------------------------\n");
        String totalAmount=formatter.format(rcLastTransaction.getTotalAmt());
        if (totalAmount.length() == 6) {
            totalAmount = "" + totalAmount;
        } else if (totalAmount.length() == 5) {
            totalAmount = " " + totalAmount;
        } else if(totalAmount.length() == 4){
            totalAmount = "  " + totalAmount;
        }
        textData.append(" " + context.getString(R.string.bill_total) + "               " + totalAmount + "\n");
        textData.append("------------------------------------------------------------\n");

        textData.append("</p>");
        textData.append("</pre>");
        textData.append(getResources().getString(R.string.call_issue) + "\n");
        textData.append("\n");
        textData.append("\n");
        textData.append("\n");
        textData.append("\n");


        return textData.toString();
    }

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


    public class ViewLastTransactionAdapter extends RecyclerView.Adapter<ViewLastTransactionAdapter.ViewLastTransactionHolder> {

        private Context context;
        private LayoutInflater mLayoutInflater;
      //  List<Product> tempProductList;
        private List<Product> productList;
        public ViewLastTransactionAdapter(Context context, List<Product> tempProductList) {
            this.context = context;
            this.productList = tempProductList;
            this.mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public ViewLastTransactionAdapter.ViewLastTransactionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = mLayoutInflater.inflate(R.layout.item_view_last_transaction, parent, false);
            return new ViewLastTransactionHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewLastTransactionAdapter.ViewLastTransactionHolder holder, int position) {

                holder.item.setText(productList.get(position).getDisplayName());
                holder.perchased_qty.setText("" + productList.get(position).getIssuedQuantity());
                Product product = ProductMap.getProductByCode(productList.get(position).getCode());
                holder.unit.setText(product.getUnitName());

                holder.unit_price.setText("" + productList.get(position).getUnitRate());
                double totalUnitRate = productList.get(position).getUnitRate()
                        * productList.get(position).getIssuedQuantity();
                holder.total_price.setText("" + totalUnitRate);

        }

        @Override
        public int getItemCount() {
            return productList.size();
        }


        public class ViewLastTransactionHolder extends RecyclerView.ViewHolder {
            private TextView item, perchased_qty, unit, unit_price, total_price;

            public ViewLastTransactionHolder(View v) {
                super(v);
                item = (TextView) v.findViewById(R.id.item_name);
                perchased_qty = (TextView) v.findViewById(R.id.purchased_qty);
                unit = (TextView) v.findViewById(R.id.unit);
                unit_price = (TextView) v.findViewById(R.id.unit_price);
                total_price = (TextView) v.findViewById(R.id.total_price);
            }
        }
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
            Intent i = new Intent(TgViewLastTransactionActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }



}
