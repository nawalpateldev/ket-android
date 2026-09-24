package com.ketgrouponline.Adapter;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ketgrouponline.Bean.ContactBean;
import com.ketgrouponline.R;


import java.util.List;

public class AboutUsAdapter extends RecyclerView.Adapter<AboutUsAdapter.ViewHolder> {
    private static final String TAG = "SingleDigitAdapter";
    List<ContactBean> list;
    Context context;

    public AboutUsAdapter(List<ContactBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.about_us_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.aboutUsTv.setText(Html.fromHtml(list.get(position).getAbout_details(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.aboutUsTv.setText(Html.fromHtml(list.get(position).getAbout_details()));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView aboutUsTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            aboutUsTv = itemView.findViewById(R.id.aboutUsTv);
        }
    }
}
