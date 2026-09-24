package com.ketgrouponline.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ketgrouponline.Model.TransactionBean;
import com.ketgrouponline.R;
import com.ketgrouponline.Utils.MyUtils;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.HomeItemVioewHolder> {
    private static final String TAG = "TransactionAdapter";
    List<TransactionBean> list;
    Context context;

    public TransactionAdapter(List<TransactionBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeItemVioewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.transaction_item_layout, parent, false);
        return new HomeItemVioewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeItemVioewHolder holder, int position) {

        holder.reason.setText(list.get(position).getReason());
        if (list.get(position).getType().equals("c")) {
            holder.type.setText("CREDIT");
            holder.type.setTextColor(Color.GREEN);
        } else {
            holder.type.setText("DEBIT");
            holder.type.setTextColor(Color.RED);
        }
        holder.points.setText("Points - " + list.get(position).getPoints());
        holder.time.setText(MyUtils.convertDateTime(list.get(position).getTime()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class HomeItemVioewHolder extends RecyclerView.ViewHolder {

        TextView reason, type, points, time;

        public HomeItemVioewHolder(@NonNull View itemView) {
            super(itemView);

            reason = itemView.findViewById(R.id.reason);
            type = itemView.findViewById(R.id.type);
            points = itemView.findViewById(R.id.points);
            time = itemView.findViewById(R.id.time);
        }
    }
}
