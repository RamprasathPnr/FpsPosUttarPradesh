/*
package com.omneagate.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omneagate.DTO.Product;
import com.omneagate.activity.R;
import com.omneagate.activity.dialog.QuantityEnterDailog;

import java.util.List;

*/
/**
 * Created by root on 28/2/17.
 *//*

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
        View itemView = mLayoutInflater.inflate(R.layout.list_item_products, parent, false);
        return new ProductHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductHolder holder, int position) {
        holder.tvItemName.setText(fpsProductList.get(position).getDisplayName());
        holder.tvItemAvailQty.setText("" + fpsProductList.get(position).getProductBalanceQty());
        holder.tvItemQtyRequested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuantityEnterDailog quantityEnterDailog =new QuantityEnterDailog(context);
                quantityEnterDailog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return fpsProductList.size();
    }

    public class ProductHolder extends RecyclerView.ViewHolder {
        private TextView tvItemName;
        private TextView tvItemAvailQty;
        private TextView tvItemunits;
        private TextView tvItemUnitRate;
        private TextView tvItemQtyRequested;
        private TextView tvItemQtyAmount;
        private TextView tvClosingBalance;

        public ProductHolder(View v) {
            super(v);
            tvItemName = (TextView) v.findViewById(R.id.tvItemName);
            tvItemAvailQty = (TextView) v.findViewById(R.id.tvItemAvailQty);
            tvItemunits = (TextView) v.findViewById(R.id.tvItemUnit);
            tvItemUnitRate = (TextView) v.findViewById(R.id.tvItemUnitRate);
            tvItemQtyRequested = (TextView) v.findViewById(R.id.tvItemQtyRequested);
            tvItemQtyAmount = (TextView) v.findViewById(R.id.tvItemQtyAmount);
            tvClosingBalance = (TextView) v.findViewById(R.id.tvClosingBalance);

        }
    }
}
*/
