package com.ketgrouponline.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ketgrouponline.Model.TicketDetaislBean;
import com.ketgrouponline.R;

import java.util.List;

public class TicketDetailsAdapter extends RecyclerView.Adapter<TicketDetailsAdapter.ViewHolder> {
    private static final String TAG = "SingleDigitAdapter";
    List<TicketDetaislBean> list;
    Context context;
    int newpos = 0;

    public TicketDetailsAdapter(List<TicketDetaislBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.ticket_detail_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.numbers.setText(list.get(position).getNumber());
        holder.amount.setText(list.get(position).getPoints());
        holder.bat_total.setText(list.get(position).getPoints());
        holder.win.setText(list.get(position).getWin_price());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView numbers, amount, bat_total, win;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            numbers = itemView.findViewById(R.id.numbers);
            amount = itemView.findViewById(R.id.amount);
            bat_total = itemView.findViewById(R.id.bat_total);
            win = itemView.findViewById(R.id.win);
        }
    }
}
