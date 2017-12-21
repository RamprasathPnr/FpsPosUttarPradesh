package com.omneagate.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.BillDto;
import com.omneagate.DTO.BillItemDto;
import com.omneagate.DTO.ProductDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.TamilUtil;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ViewBillDialog extends Dialog implements View.OnClickListener {
    Context dialogContext;
    Button okButton;
    BillDto bill;
    Long beneficiaryId;

    // Ufc
    TextView tvUfc;

    //Bill Date
    TextView tvBillDate;

    //Address
    TextView tvAddress;

    //Head of the Family
    TextView tvHeadOfTheFamily;

    //Total amount
    TextView tvTotalAmount;

    //Total ledger amount
    TextView tvBillDetailTotal;

    List<ProductDto> products;

    double totalCost = 0.0;

    public ViewBillDialog(Context context, Long beneficiaryId, BillDto bill) {
        super(context);
        dialogContext = context;
        this.bill = bill;
        this.beneficiaryId = beneficiaryId;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_bill_details);
        setCancelable(false);
        tvUfc = (TextView) findViewById(R.id.tvBillDetailUfc);
        tvBillDate = (TextView) findViewById(R.id.tvBillDetailDate);
        tvAddress = (TextView) findViewById(R.id.tvBillDetailAddress);
        tvHeadOfTheFamily = (TextView) findViewById(R.id.tvBillDetailHeadOfTheFamily);
        tvTotalAmount = (TextView) findViewById(R.id.tvBillDetailAmount);
        tvBillDetailTotal = (TextView) findViewById(R.id.tvBillDetailTotal);
        okButton = (Button) findViewById(R.id.summarySubmit);
        setBeneficiaryDetails();
        okButton.setOnClickListener(this);

    }

    private void setBeneficiaryDetails() {
        BeneficiaryDto beneficiaryDto = FPSDBHelper.getInstance(dialogContext).retrieveBeneficiary(bill.getBeneficiaryId());
        String head = "";
       /* if (GlobalAppState.language.equals("ta")) {
            head = beneficiaryDto.getFamilyMembers().get(0).getLname();
        } else {
            head = beneficiaryDto.getFamilyMembers().get(0).getName();
        }*/
        tvHeadOfTheFamily.setText(head);
        /*Util.setTamilText(tvHeadOfTheFamily, head);*/
        String ufc = Util.DecryptedBeneficiary(dialogContext, beneficiaryDto.getEncryptedUfc());
        tvBillDate.setText(ufc);
        tvUfc.setText(bill.getTransactionId());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(bill.getBillDate());
        } catch (ParseException e) {
            Log.e("Error", "Date Parse Error");
        }
        dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss", Locale.getDefault());
        tvTotalAmount.setText(dateFormat.format(convertedDate));
        configureData();
        ((TextView) findViewById(R.id.tvBillDetailProductPriceLabel)).setText(dialogContext.getString(R.string.billDetailProductPrice) + "(" + dialogContext.getString(R.string.rs) + ")");

    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) dialogContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(
                getWindow().getDecorView().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        switch (v.getId()) {
            case R.id.summarySubmit:
                dismiss();
                break;
            default:
                break;
        }

    }


    /*Data from server has been set inside this function*/
    private void configureData() {
        try {
            BillDto bills = FPSDBHelper.getInstance(dialogContext).getBill(bill.getBillLocalRefId());
            LinearLayout fpsInwardLinearLayout = (LinearLayout) findViewById(R.id.listView_bill_detail);
            fpsInwardLinearLayout.removeAllViews();
            products = FPSDBHelper.getInstance(dialogContext).getAllProductDetails();
            List<BillItemDto> billItems = new ArrayList<>(bills.getBillItemDto());
            for (int position = 0; position < billItems.size(); position++) {
                LayoutInflater lin = LayoutInflater.from(dialogContext);
                fpsInwardLinearLayout.addView(returnView(lin, billItems.get(position), position));
            }
            NumberFormat formatter = new DecimalFormat("#0.00");
            tvBillDetailTotal.setText(formatter.format(totalCost));
        } catch (Exception e) {
            Util.LoggingQueue(dialogContext, "Error", e.toString());
            Log.e("Error", e.toString(), e);
        }
    }


    /*User Bill Detail view*/
    private View returnView(LayoutInflater entitle, BillItemDto data, int position) {
        View convertView = entitle.inflate(R.layout.adapter_bill_detail_activity, null);

        TextView serialNo = (TextView) convertView.findViewById(R.id.tvBillDetailSerailNo);
        TextView productName = (TextView) convertView.findViewById(R.id.tvBillDetailProductName);
        TextView quantity = (TextView) convertView.findViewById(R.id.tvBillDetailQuantity);
        TextView price = (TextView) convertView.findViewById(R.id.tvBillDetailPrice);

        serialNo.setText("" + (position + 1));
        Log.e("ID", "" + data.getProductId());
        ProductDto product = getProduct(data.getProductId());
        NumberFormat formatter = new DecimalFormat("#0.000");
        String unit = product.getProductUnit();
        if (GlobalAppState.language.equals("ta") && StringUtils.isNotEmpty(product.getLocalProductUnit())) {
            unit = product.getLocalProductUnit();
        }
        if (GlobalAppState.language.equals("ta") && StringUtils.isNotEmpty(product.getLocalProductName()))
            Util.setTamilText(productName, product.getLocalProductName());
        else
            productName.setText(product.getName());

        if (data.getQuantity() != null)
            quantity.setText(formatter.format(data.getQuantity()) + " (" + unit + ")");
        else
            quantity.setText("0.000" + " (" + unit + ")");
        formatter = new DecimalFormat("#0.00");
        double amountPerItem = data.getCost() * data.getQuantity();
        totalCost = totalCost + amountPerItem;
        price.setText(formatter.format(amountPerItem));
        return convertView;

    }

    private ProductDto getProduct(long productId) {
        for (ProductDto productDto : products) {
            if (productDto.getId() == productId) {
                return productDto;
            }
        }
        return new ProductDto();
    }

    /**
     * Tamil text textView typeface
     * input  textView name and text string input
     */
    public void setTamilText(TextView textName, String text) {
        if (GlobalAppState.language.equals("ta")) {
            Typeface tfBamini = Typeface.createFromAsset(dialogContext.getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini);
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, text));
        } else {
            textName.setText(text);
        }
    }
}
