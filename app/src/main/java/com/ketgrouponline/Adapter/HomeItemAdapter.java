package com.ketgrouponline.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ketgrouponline.Activity.GameTypeActivity;
import com.ketgrouponline.Bean.VendorBean;
import com.ketgrouponline.R;

import java.util.ArrayList;
import java.util.List;

public class HomeItemAdapter extends RecyclerView.Adapter<HomeItemAdapter.HomeItemVioewHolder> implements Filterable {
    private static final String TAG = "HomeItemAdapter";
    List<VendorBean> list;
    Context context;
    private List<VendorBean> listFiltered;

    public HomeItemAdapter(List<VendorBean> list, Context context) {
        this.list = list;
        this.listFiltered = list;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeItemVioewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.home_item_layout, parent, false);
        return new HomeItemVioewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeItemVioewHolder holder, int position) {
        int index = position % 5;
        switch (index) {
            case 0:
                holder.layout.setBackgroundResource(R.drawable.background1);
                break;
            case 1:
                holder.layout.setBackgroundResource(R.drawable.background2);
                break;
            case 2:
                holder.layout.setBackgroundResource(R.drawable.background3);
                break;
            case 3:
                holder.layout.setBackgroundResource(R.drawable.background4);
                break;
            case 4:
                holder.layout.setBackgroundResource(R.drawable.background5);
                break;
        }
        holder.name.setText(listFiltered.get(position).getShop_name());
        holder.open_close.setText(listFiltered.get(position).getOpening_time() + " - " + listFiltered.get(position).getClosing_time());
        holder.date.setText(listFiltered.get(position).getTodays_result());
    }

    @Override
    public int getItemCount() {
        return listFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    //contactListFiltered = contactList;
                    listFiltered = list;
                } else {
                    List<VendorBean> filteredList = new ArrayList<>();
                    for (VendorBean row : list) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getShop_name().toLowerCase().contains(charString.toLowerCase()) || row.getShop_name().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }
                    listFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = listFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listFiltered = (ArrayList<VendorBean>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactsAdapterListener {
        void onContactSelected(VendorBean contact);
    }

    class HomeItemVioewHolder extends RecyclerView.ViewHolder {

        TextView open_close, name, date;
        LinearLayout layout;

        public HomeItemVioewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            open_close = itemView.findViewById(R.id.open_close);
            name = itemView.findViewById(R.id.name);
            layout = itemView.findViewById(R.id.layout);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, GameTypeActivity.class);
                    intent.putExtra("vendor_id", list.get(getAdapterPosition()).getId());
                    intent.putExtra("market", list.get(getAdapterPosition()).getShop_name());
                    context.startActivity(intent);
                }
            });
        }

    }
}
