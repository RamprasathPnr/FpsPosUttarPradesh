package com.omneagate.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.DTO.Product;
import com.omneagate.activity.R;
import com.omneagate.activity.TgProductListActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 10/3/17.
 */
public class ManualEntryDialog extends Dialog implements View.OnClickListener {
    private EditText edtSelectedCommodity;
    private Spinner spinnerQuantity;
    private Button btCancel,btSubmit;
    private Context context;
    private int position;
    private Product product;
    private List<String> quantityList;
    private QuantityAdapter quantityAdapter;
    private String quantity;
    private TextView tv_units;

    public ManualEntryDialog(Context context, Product product ,int position) {
        super(context);
        this.context=context;
        this.product=product;
        this.position=position;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_manual_entry);
        setCancelable(false);
        initView();
    }
    private void initView(){
        edtSelectedCommodity=(EditText)findViewById(R.id.edtSelectedCommodity);
        spinnerQuantity=(Spinner) findViewById(R.id.quantitySpinner);
        btCancel=(Button) findViewById(R.id.btCancel);
        btSubmit=(Button) findViewById(R.id.btSubmit);
        btCancel.setOnClickListener(this);
        btSubmit.setOnClickListener(this);
        tv_units=(TextView)findViewById(R.id.tv_unit);

        tv_units.setText(""+product.getUnitName());

        edtSelectedCommodity.setText(product.getDisplayName());

        quantityList =new ArrayList<>();

        if (product.isEntitlementQty()) {
            Double d = product.getProductBalanceQty();
            Integer number = d.intValue();
            for (int i = 0; i <= number; i++) {
             quantityList.add("" + i);

            }

        } else {
            if (product.getQuantityValues() != null) {
                for (int i = 0; i < product.getQuantityValues().length; i++) {
                    quantityList.add("" + product.getQuantityValues()[i]);

                }
            }
        }

        quantityAdapter =new QuantityAdapter();
        spinnerQuantity.setAdapter(quantityAdapter);

        spinnerQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                quantity = quantityList.get(position).toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btSubmit:
                if(quantity !=null && !quantity.equals("")) {

                    if(Double.parseDouble(quantity) > product.getProductBalanceQty()){
                        Toast.makeText(context, context.getString(R.string.quantity_exceeds), Toast.LENGTH_LONG).show();
                        return;
                    }
                    ((TgProductListActivity) context).setPurchasedQuantity(position, Double.parseDouble(quantity));
                    dismiss();
                }
                break;
            case R.id.btCancel:
                dismiss();
                break;
        }
    }

    public class QuantityAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return quantityList.size();
        }

        @Override
        public Object getItem(int position) {
            return quantityList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            LanguageHolder holder = null;
            holder = new LanguageHolder();

            if (convertView == null) {

                convertView = LayoutInflater.from(context).inflate(R.layout.adapter_language_type, viewGroup, false);
                holder.txturl = (TextView) convertView.findViewById(R.id.text_language_type);
                convertView.setTag(holder);
                convertView.setTag(R.id.text_language_type, holder.txturl);


            } else {
                holder = (LanguageHolder) convertView.getTag();
            }
            holder.txturl.setText(quantityList.get(position));


            return convertView;
        }

        class LanguageHolder {
            TextView txturl;

        }
    }
}
