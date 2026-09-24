package com.ketgrouponline.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ketgrouponline.Bean.NoticBean;
import com.ketgrouponline.R;

import java.util.List;

public class NoticAdapter extends RecyclerView.Adapter<NoticAdapter.ViewHolder> {
    private static final String TAG = "NoticAdapter";
    List<NoticBean> list;
    Context context;

    public NoticAdapter(List<NoticBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.notic_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.titleTv.setText(list.get(position).getTitle());
        holder.detailTv.setText(list.get(position).getDetails());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTv, detailTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.titleTv);
            detailTv = itemView.findViewById(R.id.detailTv);
        }
    }
}
