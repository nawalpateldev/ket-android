package com.ketgrouponline.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ketgrouponline.Adapter.TicketAdapter;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketActivity extends AppCompatActivity {
    private static final String TAG = "TicketActivity";
    List<TicketBean> list;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    TicketAdapter adapter;
    BroadcastReceiver broadcastReceiver;
    int requestCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        cancelTiming();
        initToolBar();
        initView();
        getContactData();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                TextView walletTv = findViewById(R.id.walletTv);
                wallet(walletTv);
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            this.unregisterReceiver(broadcastReceiver);
        }
    }

    public void initView() {
        list = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new TicketAdapter(list, TicketActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(TicketActivity.this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        getTickts(requestCount);
        recyclerView.setOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                        if (MyUtils.isLastItemDisplaying(recyclerView)) {
                            requestCount++;
                            getTickts(requestCount);
                        }
                        super.onScrolled(recyclerView, dx, dy);
                    }
                });
    }

    public void initToolBar() {
        TextView titleTv = findViewById(R.id.titleTv);
        TextView walletTv = findViewById(R.id.walletTv);
        wallet(walletTv);
        ImageView whatsAppIv = findViewById(R.id.whatsAppIv);
        titleTv.setText("View Tickets");
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

    @Override
    protected void onResume() {
        super.onResume();
        ContextCompat.registerReceiver(this, broadcastReceiver, new IntentFilter("wallet"), ContextCompat.RECEIVER_NOT_EXPORTED);
        TextView walletTv = findViewById(R.id.walletTv);
        wallet(walletTv);
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
                MyUtils.showVolleyError(error, TAG, TicketActivity.this);
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

    private void getTickts(int requestCount) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.GET, Endpoints.tickets + "/" + requestCount, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                Log.i(TAG, "wallet " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("return")) {
                        JSONArray tickets = object.getJSONArray("tickets");
                        if (tickets.length() > 0) {
                            for (int i = 0; i < tickets.length(); i++) {
                                JSONObject object1 = tickets.getJSONObject(i);
                                TicketBean bean = new TicketBean();
                                bean.setTicket_number(object1.getString("ticket_number"));
                                bean.setGame_type(object1.getString("game_type"));
                                bean.setUser_id(object1.getString("user_id"));
                                bean.setVendor_id(object1.getString("vendor_id"));
                                bean.setType(object1.getString("type"));
                                bean.setStatus(object1.getString("status"));
                                bean.setTime(object1.getString("time"));
                                bean.setPoints(object1.getString("points"));
                                bean.setName(object1.getString("name"));
                                list.add(bean);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                MyUtils.showVolleyError(error, TAG, TicketActivity.this);
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
                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + mobile + "&text=" + "Hi KET GROUP"));
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

    private void cancelTiming() {
        StringRequest request = new StringRequest(Request.Method.GET, Endpoints.cancelTiming, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "wallet " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("return")) {
                        JSONObject object1 = object.getJSONObject("data");
                        TextView noteTv = findViewById(R.id.noteTv);
                        noteTv.setText("Note - Ticket can be cancelled only before " + object1.getString("value") + " Mins prior to result time.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.showVolleyError(error, TAG, TicketActivity.this);
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
}