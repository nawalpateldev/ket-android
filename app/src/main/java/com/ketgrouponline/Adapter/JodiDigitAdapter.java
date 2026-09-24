package com.ketgrouponline.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ketgrouponline.Activity.JodiDigitActivity;
import com.ketgrouponline.Bean.NumberBean;
import com.ketgrouponline.R;

import java.util.List;

public class JodiDigitAdapter extends RecyclerView.Adapter<JodiDigitAdapter.ViewHolder> {
    private static final String TAG = "SingleDigitAdapter";
    List<NumberBean> list;
    Context context;

    public JodiDigitAdapter(List<NumberBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.jodi_digit_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.idTv.setText(list.get(position).getDigit());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView idTv, valueEt;
        int total = 0;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            idTv = itemView.findViewById(R.id.idTv);
            valueEt = itemView.findViewById(R.id.valueEt);
            valueEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    if (cs.length() != 0) {
                        list.get(getAdapterPosition()).setValue(Integer.parseInt(cs + ""));
                        total = 0;
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getValue() != 0) {
                                total = total + list.get(i).getValue();
                            }
                        }
                    }else{
                        list.get(getAdapterPosition()).setValue(0);
                        total = 0;
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getValue() != 0) {
                                total = total + list.get(i).getValue();
                            }
                        }
                    }
                    if (JodiDigitActivity.totalEt != null) {
                        JodiDigitActivity.totalEt.setText(total + "");
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {

                }
            });
        }
    }
}
