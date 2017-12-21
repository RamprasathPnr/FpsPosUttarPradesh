package com.omneagate.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.omneagate.activity.TgPortabilityRequestActivity;
import com.omneagate.activity.TgProductListActivity;

import java.util.ArrayList;
import java.util.List;


public class PortabilityEntryDialog extends Dialog implements View.OnClickListener {
    private Button btCancel,btSubmit;
    private Context context;
    private int position;
    private Product product;
    private List<String> quantityList;
    private String quantity;
    private QuantityAdapter quantityAdapter;
    private Spinner spinnerQuantity;
    private TextView txtProduct;
    private EditText editTextQuantity;
    public PortabilityEntryDialog(Context context, Product product ,int position) {
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
        setContentView(R.layout.dialog_portability_entry);
        setCancelable(false);
        initView();
    }
    private void initView(){
        btCancel=(Button) findViewById(R.id.btCancel);
        btSubmit=(Button) findViewById(R.id.btSubmit);
        btCancel.setOnClickListener(this);
        btSubmit.setOnClickListener(this);

        txtProduct=(TextView)findViewById(R.id.txtProduct);

        txtProduct.setText(""+product.getDisplayName());

        editTextQuantity=(EditText)findViewById(R.id.editTextQuantity);

       /* quantityList =new ArrayList<>();
        spinnerQuantity=(Spinner) findViewById(R.id.quantitySpinner);

            Double d = product.getIssuedQuantity();
            Integer number = d.intValue();
            for (int i = 0; i <= number * 2; i++) {
                quantityList.add("" + i);

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
*/

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btSubmit:
                if (editTextQuantity.getText().toString() != null && !editTextQuantity.getText().toString().equals("")) {

                    Double d = product.getIssuedQuantity();
                    Integer issueQuantity = d.intValue();

                    String number = editTextQuantity.getText().toString();
                    Double d1 = new Double(number);
                    int enterQuantity = d1.intValue();

                    if(enterQuantity > (issueQuantity * 2)){
                        Toast.makeText(context, context.getString(R.string.invalid_quantity), Toast.LENGTH_LONG).show();
                        return;
                    }
                    ((TgPortabilityRequestActivity) context).setPurchasedQuantity(position, Double.parseDouble(number));
                }

                dismiss();
               /* if(quantity !=null && !quantity.equals("")) {

                    if(Double.parseDouble(quantity) <= 0){
                        Toast.makeText(context, ""+context.getString(R.string.productName), Toast.LENGTH_LONG).show();
                        return;
                    }
                    ((TgPortabilityRequestActivity) context).setPurchasedQuantity(position, Double.parseDouble(quantity));
                    dismiss();
                }*/
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

