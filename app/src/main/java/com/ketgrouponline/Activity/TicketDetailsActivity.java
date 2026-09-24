package com.ketgrouponline.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ketgrouponline.Adapter.TicketDetailsAdapter;
import com.ketgrouponline.Model.TicketDetaislBean;
import com.ketgrouponline.Network.MyApplication;
import com.ketgrouponline.R;
import com.ketgrouponline.Storage.SPCsnstants;
import com.ketgrouponline.Utils.Endpoints;
import com.ketgrouponline.Utils.MyUtils;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketDetailsActivity extends AppCompatActivity {
    private static final String TAG = "TicketDetailsActivity";
    String ticket_number;
    TicketDetailsAdapter adapter;
    List<TicketDetaislBean> list;
    RecyclerView recyclerView;
    TextView ticketNumberTv, marketNameTv, dateTimeTv, totalAmountWinTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_details);
        initToolBar();
        initView();
        getContactData();
    }

    public void initView() {
        ticket_number = getIntent().getStringExtra("ticket_number");
        recyclerView = findViewById(R.id.recyclerView);
        ticketNumberTv = findViewById(R.id.ticketNumberTv);
        marketNameTv = findViewById(R.id.marketNameTv);
        dateTimeTv = findViewById(R.id.dateTimeTv);
        totalAmountWinTv = findViewById(R.id.totalAmountWinTv);
        ticketNumberTv.setText("Ticket Number - " + ticket_number);
        marketNameTv.setText(getIntent().getStringExtra("name") + "(" + getIntent().getStringExtra("game_type") + ")");
        dateTimeTv.setText(MyUtils.convertDateTime(getIntent().getStringExtra("date")));
        list = new ArrayList<>();
        adapter = new TicketDetailsAdapter(list, TicketDetailsActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(TicketDetailsActivity.this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        getTicketDetails();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView walletTv = findViewById(R.id.walletTv);
        wallet(walletTv);
    }

    private void getTicketDetails() {
        MyUtils.showProgressDialog(TicketDetailsActivity.this, false);
        StringRequest request = new StringRequest(Request.Method.POST, Endpoints.ticket_detail, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MyUtils.dismisProgressDialog();
                Log.i(TAG, "getTicketDetails " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("return")) {
                        JSONArray data = object.getJSONArray("data");
                        if (data.length() > 0) {
                            float win = 0;
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject object1 = data.getJSONObject(i);
                                TicketDetaislBean bean = new TicketDetaislBean();
                                bean.setId(object1.getString("id"));
                                bean.setTicket_number(object1.getString("ticket_number"));
                                bean.setGame_type(object1.getString("game_type"));
                                bean.setUser_id(object1.getString("user_id"));
                                bean.setDate(object1.getString("date"));
                                bean.setVendor_id(object1.getString("vendor_id"));
                                bean.setType(object1.getString("type"));
                                bean.setNumber(object1.getString("number"));
                                bean.setPoints(object1.getString("points"));
                                bean.setStatus(object1.getString("status"));
                                bean.setTime(object1.getString("time"));
                                String winPriceStr = object1.optString("win_price", "0");
                                bean.setWin_price(winPriceStr);
                                bean.setVendor_name(object1.optString("vendor_name", ""));
                                try {
                                    if (winPriceStr != null && !winPriceStr.isEmpty() && !winPriceStr.equalsIgnoreCase("null")) {
                                        win = win + Float.parseFloat(winPriceStr);
                                    }
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                list.add(bean);
                            }
                            adapter.notifyDataSetChanged();
                            totalAmountWinTv.setText(win + "");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.dismisProgressDialog();
                MyUtils.showVolleyError(error, TAG, TicketDetailsActivity.this);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", MyApplication.sharedPreferences.getKey(SPCsnstants.api_token));
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("ticket_number", ticket_number);
                Log.e("ticket_number", ticket_number);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.mRequestQue.add(request);
    }

    public void initToolBar() {
        TextView titleTv = findViewById(R.id.titleTv);
        TextView walletTv = findViewById(R.id.walletTv);
        wallet(walletTv);
        ImageView whatsAppIv = findViewById(R.id.whatsAppIv);
        titleTv.setText("Ticket Details");
        whatsAppIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + MyApplication.sharedPreferences.getKey(SPCsnstants.whatsAppNumber) + "&text=" + "Hello"));
                startActivity(intent);
            }
        });

        ImageView menuIv = findViewById(R.id.backIv);
        menuIv.setVisibility(View.VISIBLE);
        menuIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //drawer.openDrawer(Gravity.LEFT | GravityCompat.START);
            }
        });
    }

    private void wallet(TextView walletTv) {
        StringRequest request = new StringRequest(Request.Method.GET, Endpoints.wallet, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "wallet " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("return")) {
                        walletTv.setText(object.getString("wallet"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.showVolleyError(error, TAG, TicketDetailsActivity.this);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", MyApplication.sharedPreferences.getKey(SPCsnstants.api_token));
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.mRequestQue.add(request);
    }

    String name = "", mobile = "";

    public void getContactData() {
        StringRequest request = new StringRequest(Request.Method.GET, Endpoints.getContactDetails, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "getContactData " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("return")) {
                        JSONObject data = object.getJSONObject("data");
                        TextView numberTv = findViewById(R.id.numberTv);
                        numberTv.setText(data.getString("mobile"));
                        name = data.getString("name");
                        mobile = data.getString("mobile");

                        TextView nameTv = findViewById(R.id.nameTv);
                        nameTv.setText(name);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.showVolleyError(error, TAG, getApplicationContext());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", MyApplication.sharedPreferences.getKey(SPCsnstants.api_token));
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.mRequestQue.add(request);


        ImageView whatsAppIv = findViewById(R.id.whatsAppIv1);
        ImageView callIv = findViewById(R.id.callIv);
        whatsAppIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: " + mobile);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + mobile + "&text=" + "Hi ketgrouponline"));
                startActivity(intent);
            }
        });

        callIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mobile));
                startActivity(intent);
            }
        });
    }
}