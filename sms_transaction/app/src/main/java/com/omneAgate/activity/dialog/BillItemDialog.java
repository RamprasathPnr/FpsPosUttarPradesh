package com.omneagate.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.omneagate.DTO.BillItemDto;
import com.omneagate.DTO.EntitlementDTO;
import com.omneagate.Util.EntitlementResponse;
import com.omneagate.Util.Util;
import com.omneagate.activity.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class BillItemDialog extends Dialog implements View.OnClickListener {
    Context dialogContext;
    Button okButton;
    String productName;
    List<BillItemDto> billList;
    String ufc;
    Long beneficiaryId;

    public BillItemDialog(Context context, List<BillItemDto> billList, String productName, String ufc, Long beneficiaryId) {
        super(context);
        dialogContext = context;
        this.billList = billList;
        this.productName = productName;
        this.ufc = ufc;
        this.beneficiaryId = beneficiaryId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.listforbillitemcust);
        setCancelable(false);
        okButton = (Button) findViewById(R.id.changetable);
        TextView alertTitle = (TextView) findViewById(R.id.alerttitle);
        TextView textViewProduct = (TextView) findViewById(R.id.textViewProduct);
        TextView textViewMonth = (TextView) findViewById(R.id.textViewMonth);
        TextView textViewUfc = (TextView) findViewById(R.id.textViewufc);
        alertTitle.setText("Monthly Commodity Purchase History");


        TextView entitledQuantity = (TextView) findViewById(R.id.entitledAmount);
        TextView purchasedAmount = (TextView) findViewById(R.id.purchasedAmount);
        TextView remainingAmount = (TextView) findViewById(R.id.remainingAmount);
        TextView entitledData = (TextView) findViewById(R.id.entitledData);
        TextView purchasedData = (TextView) findViewById(R.id.purchasedData);
        TextView remainingData = (TextView) findViewById(R.id.remainingData);
        TextView textViewQty = (TextView) findViewById(R.id.textVieword);
        TextView textViewDetails = (TextView) findViewById(R.id.textViewDetails);
        NumberFormat formatter = new DecimalFormat("#0.000");
        EntitlementDTO entitle = entitleList();
        entitledQuantity.setText(formatter.format(entitle.getEntitledQuantity()));
        purchasedAmount.setText(formatter.format(entitle.getHistory()));
        remainingAmount.setText(formatter.format(entitle.getCurrentQuantity()));
        textViewProduct.setText("Commodity : " + productName);
        entitledData.setText("Monthly Entitlement (" + entitle.getProductUnit() + ")");
        purchasedData.setText("Purchased Quantity (" + entitle.getProductUnit() + ")");
        remainingData.setText("Remaining Quantity (" + entitle.getProductUnit() + ")");
        textViewQty.setText("Qty (" + entitle.getProductUnit() + ")");
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
        textViewMonth.setText("Month           : " + dateFormat.format(date));
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ListView list = (ListView) findViewById(R.id.listView1);
        list.setAdapter(new BillItemsAdapter(dialogContext, billList));
        okButton.setOnClickListener(this);
        textViewDetails.setOnClickListener(this);
        try{
            textViewUfc.setText("UFC               : " + Util.DecryptedBeneficiary(dialogContext,ufc));
        }catch (Exception e){
            Log.e("error",e.toString(),e);
        }


    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) dialogContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(
                getWindow().getDecorView().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        switch (v.getId()) {
            case R.id.changetable:
                dismiss();
                break;
            case R.id.textViewDetails:
                new BillDialog(dialogContext, ufc, beneficiaryId).show();
                dismiss();
                break;
            default:
                break;
        }

    }

    private EntitlementDTO entitleList() {
        List<EntitlementDTO> entitle = EntitlementResponse.getInstance().getQrcodeTransactionResponseDto().getEntitlementList();
        for (EntitlementDTO entitlementDTO : entitle) {
            if (entitlementDTO.getProductName().equalsIgnoreCase(productName)) {
                return entitlementDTO;
            }
        }
        return new EntitlementDTO();
    }

}
