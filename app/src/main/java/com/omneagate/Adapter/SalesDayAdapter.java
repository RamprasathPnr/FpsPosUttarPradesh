package com.omneagate.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omneagate.DTO.Product;
import com.omneagate.activity.R;

import java.util.List;


/**
 * Created by user on 23/2/17.
 */
public class SalesDayAdapter extends RecyclerView.Adapter<SalesDayAdapter.SalesHolder> {

    private Context context;

    private LayoutInflater mLayoutInflater;
    private List<Product> fpsSalesDayProductList;


    public SalesDayAdapter(Context context, List<Product> fpsSalesDayProductList) {
        this.context = context;
        this.fpsSalesDayProductList = fpsSalesDayProductList;
        this.mLayoutInflater = LayoutInflater.from(context);
    }


    @Override
    public SalesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_sales_list, parent, false);
        return new SalesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SalesHolder holder, int position) {
        holder.name.setText(fpsSalesDayProductList.get(position).getDisplayName());
        holder.qty.setText(""+fpsSalesDayProductList.get(position).getIssuedQuantity());
    }

    @Override
    public int getItemCount() {
        return fpsSalesDayProductList.size();
    }

    public class SalesHolder extends RecyclerView.ViewHolder {
        private TextView name, qty;

        public SalesHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.tvItemName);
            qty = (TextView) v.findViewById(R.id.tvItemQty);

        }
    }
}
