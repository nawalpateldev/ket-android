package com.ketgrouponline.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.ketgrouponline.Activity.TicketDetailsActivity;
import com.ketgrouponline.Bean.TicketBean;
import com.ketgrouponline.Network.MyApplication;
import com.ketgrouponline.R;
import com.ketgrouponline.Storage.SPCsnstants;
import com.ketgrouponline.Utils.Endpoints;
import com.ketgrouponline.Utils.MyUtils;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {
    private static final String TAG = "SingleDigitAdapter";
    List<TicketBean> list;
    Context context;
    int newpos = 0;

    public TicketAdapter(List<TicketBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.ticket_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        newpos = position + 1;
        holder.sNoTv.setText(newpos + "");
        holder.ticket_number.setText(list.get(position).getTicket_number());
        holder.name.setText(list.get(position).getGame_type() + "-" + list.get(position).getType() + "-" + list.get(position).getName());
        holder.date_time.setText(MyUtils.convertDate(list.get(position).getTime()));
        holder.amount.setText(list.get(position).getPoints());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView sNoTv, ticket_number, date_time, name, amount, viewTv, deleteTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sNoTv = itemView.findViewById(R.id.sNoTv);
            ticket_number = itemView.findViewById(R.id.ticket_number);
            date_time = itemView.findViewById(R.id.date_time);
            name = itemView.findViewById(R.id.name);
            amount = itemView.findViewById(R.id.amount);
            viewTv = itemView.findViewById(R.id.viewTv);
            deleteTv = itemView.findViewById(R.id.deleteTv);

            viewTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && pos < list.size()) {
                        Intent intent = new Intent(context, TicketDetailsActivity.class);
                        intent.putExtra("ticket_number", list.get(pos).getTicket_number());
                        intent.putExtra("name", list.get(pos).getName());
                        intent.putExtra("game_type", list.get(pos).getGame_type());
                        intent.putExtra("date", list.get(pos).getTime());
                        context.startActivity(intent);
                    }
                }
            });

            deleteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && pos < list.size()) {
                        deleteTicket(pos);
                    }
                }
            });
        }
    }

    private void deleteTicket(int pos) {
        if (pos < 0 || pos >= list.size()) return;
        final String ticketNumber = list.get(pos).getTicket_number();
        MyUtils.showProgressDialog(context, false);
        StringRequest request = new StringRequest(Request.Method.POST, Endpoints.cancel_ticket, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MyUtils.dismisProgressDialog();
                Log.i(TAG, "deleteTicket " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("return")) {
                        if (pos >= 0 && pos < list.size()) {
                            list.remove(pos);
                            notifyDataSetChanged();
                        }
                        if (context != null) {
                            Intent in = new Intent("wallet");
                            context.sendBroadcast(in);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.dismisProgressDialog();
                MyUtils.showVolleyError(error, TAG, context);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                String token = MyApplication.sharedPreferences.getKey(SPCsnstants.api_token);
                if (token != null) {
                    params.put("Authorization", token);
                }
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("ticket_number", ticketNumber);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.mRequestQue.add(request);
    }
}
