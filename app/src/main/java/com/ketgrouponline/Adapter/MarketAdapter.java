package com.ketgrouponline.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ketgrouponline.Activity.BrowserActivity;
import com.ketgrouponline.Bean.MarketBean;
import com.ketgrouponline.R;

import java.util.List;

public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.ViewHolder> {
    private static final String TAG = "MarketAdapter";
    List<MarketBean> list;
    Context context;

    public MarketAdapter(List<MarketBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.market_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameTv.setText(list.get(position).getVendor_name());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.nameTv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BrowserActivity.class);
                    intent.putExtra("link", "https://sattamatkatv.com/index.php/panel-chart-api/" + list.get(getAdapterPosition()).getVendor_name().replace(" ", "-") + "/" + list.get(getAdapterPosition()).getVendor_id());
                    context.startActivity(intent);
                }
            });
        }
    }
}
