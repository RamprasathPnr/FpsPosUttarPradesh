package com.omneagate.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSPortablityMonthWiseReport;
import com.omneagate.DTO.GeneralResponse;
import com.omneagate.DTO.Product;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.XMLUtil;
import com.omneagate.activity.dialog.ManualEntryDialog;
import com.omneagate.activity.dialog.PortabilityEntryDialog;
import com.omneagate.activity.dialog.PortabilitySuccessDialog;
import com.omneagate.activity.dialog.QuantityEnterDailog;
import com.omneagate.activity.dialog.RationCardDetailDialog;
import com.omneagate.activity.dialog.RoSucessDialog;
import com.omneagate.activity.dialog.TgGenericErrorDialog;
import com.omneagate.exception.FPSException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class TgPortabilityRequestActivity extends BaseActivity{
    private RationCardDetailDialog rationCardDetailDialog;
    private Map<String, Object> inputMap;
    private List<Product> itemList;
    private List<Product> itemTempList;
    private RecyclerView recyclerViewProdcuts;
    private Button btnBack,btnSubmit;
    private ImageView imageViewBack;
    private ProductListAdapter productListAdapter;
    private final String TAG =TgPortabilityRequestActivity.class.getCanonicalName();
    NetworkConnection networkConnection;
    private LinearLayout llNoDataFound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_portability_request);

        itemList= new ArrayList<>();
        itemTempList = new ArrayList<>();

        networkConnection = new NetworkConnection(TgPortabilityRequestActivity.this);

        recyclerViewProdcuts =(RecyclerView)findViewById(R.id.rv_portability_products);
        recyclerViewProdcuts.setLayoutManager(new LinearLayoutManager(TgPortabilityRequestActivity.this));

        llNoDataFound=(LinearLayout)findViewById(R.id.ll_no_record_found);

        btnBack=(Button)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSubmit=(Button)findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (networkConnection.isNetworkAvailable()) {
                        boolean valueEntered = false;
                        boolean isvalueExceeded = false;
                        if (itemTempList != null) {
                            for (Product product : itemTempList) {
                                Log.e(TAG, "Issued Quantity " + product.getIssuedQuantity() + " Quantity Entered " + product.getQuantityEntered());
                                if ((product.getQuantityEntered() != null) && (product.getQuantityEntered() > 0.0)) {
                                    valueEntered = true;
                                    if (product.getQuantityEntered() > product.getIssuedQuantity() * 2) {
                                        isvalueExceeded = true;
                                        break;
                                    }
                                }
                            }
                        }

                        if (valueEntered) {
                            if (!isvalueExceeded) {
                                for (int i = 0; i < itemTempList.size(); i++) {
                                    for (int j = 0; j < itemList.size(); j++) {
                                        if (itemList.get(j).getCode().equals(itemTempList.get(i).getCode())) {
                                            itemList.get(j).setQuantityEntered(itemTempList.get(i).getQuantityEntered());
                                        }
                                    }
                                }
                                new PostPortablityRequest().execute();
                            } else {
                                TgGenericErrorDialog tgGenericErrorDialog = new TgGenericErrorDialog(TgPortabilityRequestActivity.this, "" + getString(R.string.invalid_quantity));
                                tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                                tgGenericErrorDialog.show();
                            }

                        } else {
                            TgGenericErrorDialog tgGenericErrorDialog = new TgGenericErrorDialog(TgPortabilityRequestActivity.this, "" + getString(R.string.productName));
                            tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                            tgGenericErrorDialog.show();
                        }

                    } else {
                        displayNoInternetDailog();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Exception while submiting portability : " + e.toString());
                }
            }
        });

        updateDateTime();

        setPopUpPage();

        ((TextView) findViewById(R.id.top_textView)).setText(getString(R.string.portability));

        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        ((TextView) findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo() + " " + getResources().getString(R.string.welcome) + "  " + LoginData.getInstance().getFpsUsername());

        new GetPortablityRequest().execute();

    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    class GetPortablityRequest extends AsyncTask<String, String, FPSPortablityMonthWiseReport> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rationCardDetailDialog = new RationCardDetailDialog(TgPortabilityRequestActivity.this, getString(R.string.Downloading));
            rationCardDetailDialog.setCanceledOnTouchOutside(false);
            rationCardDetailDialog.show();
        }

        protected FPSPortablityMonthWiseReport doInBackground(String... arg0) {
            try {
                return getPortability();
            } catch (Exception e) {
                e.printStackTrace();
                return new FPSPortablityMonthWiseReport();
            }
        }

        @Override
        protected void onPostExecute(FPSPortablityMonthWiseReport fpsPortabilityRequest) {
            super.onPostExecute(fpsPortabilityRequest);
            rationCardDetailDialog.cancel();
            if(fpsPortabilityRequest !=null && fpsPortabilityRequest.getRespMsgCode() !=null && fpsPortabilityRequest.getRespMsgCode().equals("0")){
                btnSubmit.setVisibility(View.VISIBLE);
            }else{
                recyclerViewProdcuts.setVisibility(View.GONE);
                llNoDataFound.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.INVISIBLE);
            }
            setValues(fpsPortabilityRequest);
        }
    }

    private void setValues(FPSPortablityMonthWiseReport fpsPortablityMonthWiseReport) {
        itemList = fpsPortablityMonthWiseReport.getProductList();
        for (int i = 0; i <itemList.size(); i++) {
            if (itemList.get(i).getIssuedQuantity() > 0) {
                itemTempList.add(itemList.get(i));
            }
        }
        productListAdapter = new ProductListAdapter(TgPortabilityRequestActivity.this, itemTempList);
        recyclerViewProdcuts.setAdapter(productListAdapter);
    }

    private FPSPortablityMonthWiseReport getPortability() {
        FPSPortablityMonthWiseReport fpsPortabilityRequest = new FPSPortablityMonthWiseReport();
        try {

            inputMap = new LinkedHashMap<String, Object>();
            inputMap.put("distCode", LoginData.getInstance().getDistCode());
            inputMap.put("shopNo", LoginData.getInstance().getShopNo());
            inputMap.put("currYear","" + LoginData.getInstance().getCurrentYear());
            inputMap.put("currMonth", "" + LoginData.getInstance().getCurrentMonth());
           // inputMap.put("currMonth", "" + 7);
            inputMap.put("transactionId", LoginData.getInstance().getTransactionId());
            inputMap.put("password", "1159abbb8b6c0b8a8964210af6954b17");
            fpsPortabilityRequest = XMLUtil.getFPSPortablilityMonthWiseReport(inputMap);

            return fpsPortabilityRequest;
        } catch (final FPSException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TgGenericErrorDialog tgGenericErrorDialog =new TgGenericErrorDialog(TgPortabilityRequestActivity.this,""+e.getMessage());
                    tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                    tgGenericErrorDialog.show();
                }
            });
            return fpsPortabilityRequest;
        }catch (Exception e) {
            e.printStackTrace();
            return new FPSPortablityMonthWiseReport();
        }

    }
    public void setPurchasedQuantity(int position, double qtyRequested) {
        try {
            itemTempList.get(position).setQuantityEntered(qtyRequested);

            productListAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(TgPortabilityRequestActivity.this,TgDashBoardActivity.class);
        startActivity(backIntent);
        finish();
    }
    void displayNoInternetDailog(){
        TgGenericErrorDialog tgGenericErrorDialog = new TgGenericErrorDialog(TgPortabilityRequestActivity.this, getString(R.string.noNetworkConnection));
        tgGenericErrorDialog.setCanceledOnTouchOutside(false);
        tgGenericErrorDialog.show();
        return;
    }

    class PostPortablityRequest extends AsyncTask<String, String, GeneralResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rationCardDetailDialog = new RationCardDetailDialog(TgPortabilityRequestActivity.this, getString(R.string.Downloading));
            rationCardDetailDialog.setCanceledOnTouchOutside(false);
            rationCardDetailDialog.show();
        }

        protected GeneralResponse doInBackground(String... arg0) {
            try {
                return postPortabilityRequest();
            } catch (Exception e) {
                e.printStackTrace();
                return new GeneralResponse();
            }
        }

        @Override
        protected void onPostExecute(GeneralResponse generalResponse) {
            super.onPostExecute(generalResponse);
            rationCardDetailDialog.cancel();

            if(generalResponse !=null && generalResponse.getRespMsgCode()!=null && generalResponse.getRespMsgCode().equals("0")){
                 btnSubmit.setVisibility(View.INVISIBLE);
                PortabilitySuccessDialog portabilitySuccessDialog =new PortabilitySuccessDialog(TgPortabilityRequestActivity.this);
                portabilitySuccessDialog.setCanceledOnTouchOutside(false);
                portabilitySuccessDialog.show();

            }

        }
    }

    private GeneralResponse postPortabilityRequest(){
        GeneralResponse generalResponse = new GeneralResponse();
        try {
            inputMap = new LinkedHashMap<String, Object>();
            inputMap.put("distCode", LoginData.getInstance().getDistCode());
            inputMap.put("shopNo", LoginData.getInstance().getShopNo());
            inputMap.put("currYear","" + LoginData.getInstance().getCurrentYear());
            inputMap.put("currMonth", "" + LoginData.getInstance().getCurrentMonth());
          //  inputMap.put("currMonth", "" + 7);
            inputMap.put("transactionId", LoginData.getInstance().getTransactionId());
            inputMap.put("password", "1159abbb8b6c0b8a8964210af6954b17");
            generalResponse = XMLUtil.postPortabilityRequest(inputMap,itemList);

            return generalResponse;
        } catch (final FPSException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TgGenericErrorDialog tgGenericErrorDialog =new TgGenericErrorDialog(TgPortabilityRequestActivity.this,""+e.getMessage());
                    tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                    tgGenericErrorDialog.show();
                }
            });
            return generalResponse;
        }catch (Exception e) {
            e.printStackTrace();
            return new GeneralResponse();
        }

    }
    public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductHolder> {
        private Context context;

        private LayoutInflater mLayoutInflater;
        private List<Product> fpsProductList;


        public ProductListAdapter(Context context, List<Product> fpsProductList) {
            this.context = context;
            this.fpsProductList = fpsProductList;
            this.mLayoutInflater = LayoutInflater.from(context);
        }


        @Override
        public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = mLayoutInflater.inflate(R.layout.list_item_portability, parent, false);
            return new ProductHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ProductHolder holder, final int position) {
            holder.tvItemName.setText(fpsProductList.get(position).getDisplayName());
            holder.tvSno.setText("" + (position + 1) );

            if(fpsProductList.get(position).getIssuedQuantity()!=null) {
                holder.tvItemSale.setText("" + fpsProductList.get(position).getIssuedQuantity());
            }else{
                holder.tvItemSale.setText("");
            }

            if(fpsProductList.get(position).getQuantityEntered() !=null){
                holder.edtItemRequest.setText(""+fpsProductList.get(position).getQuantityEntered());
            }else{
                holder.edtItemRequest.setText("");
            }

            if(fpsProductList.get(position).getIssuedQuantity()==null ||fpsProductList.get(position).getIssuedQuantity() <=0 ) {
                holder.llTopLayout.setVisibility(View.GONE);
            }else{
                holder.llTopLayout.setVisibility(View.VISIBLE);
            }

           /* holder.llItemrequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Product product = fpsProductList.get(position);
                    //new PortabilityEntryDialog(TgPortabilityRequestActivity.this, product, position).show();
                }
            });*/

            holder.edtItemRequest.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if(s.toString()!=null && !s.toString().equalsIgnoreCase("")){
                        fpsProductList.get(position).setQuantityEntered(Double.parseDouble(s.toString()));
                    }else if(s.toString()!=null && s.toString().equalsIgnoreCase("")){
                        fpsProductList.get(position).setQuantityEntered(0.0);
                    }

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return fpsProductList.size();
        }

        public class ProductHolder extends RecyclerView.ViewHolder {
            private TextView tvSno;
            private TextView tvItemName;
            private TextView tvItemSale;
            private EditText edtItemRequest;
            private LinearLayout llItemrequest;
            private LinearLayout llTopLayout;


            public ProductHolder(View v) {
                super(v);
                tvItemName = (TextView) v.findViewById(R.id.tvItemName);
                tvSno = (TextView) v.findViewById(R.id.tvSno);
                tvItemSale = (TextView) v.findViewById(R.id.tvItemSale);
                edtItemRequest = (EditText) v.findViewById(R.id.tvItemRequest);
                llItemrequest =(LinearLayout)v.findViewById(R.id.llItemRequest);
                llTopLayout =(LinearLayout)v.findViewById(R.id.title_layout);
            }
        }
    }
}
