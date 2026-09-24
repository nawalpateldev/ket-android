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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ketgrouponline.Adapter.AboutUsAdapter;
import com.ketgrouponline.Bean.ContactBean;
import com.ketgrouponline.Network.MyApplication;
import com.ketgrouponline.R;
import com.ketgrouponline.Storage.SPCsnstants;
import com.ketgrouponline.Utils.Endpoints;
import com.ketgrouponline.Utils.MyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AboutUsActivity extends AppCompatActivity {
    private static final String TAG = "AboutUsActivity";
    RecyclerView recyclerView;
    List<ContactBean> list;
    AboutUsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initToolBar();
        initView();
    }

    public void initView() {
        recyclerView = findViewById(R.id.recyclerView);
        list = new ArrayList<>();

        adapter = new AboutUsAdapter(list, AboutUsActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(AboutUsActivity.this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        getContactData();
    }

    public void initToolBar() {
        TextView titleTv = findViewById(R.id.titleTv);
        TextView walletTv = findViewById(R.id.walletTv);
        wallet(walletTv);
        ImageView whatsAppIv = findViewById(R.id.whatsAppIv);
        titleTv.setText("About Us");
        whatsAppIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + "+15dp" + "&text=" + "Hello"));
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
                MyUtils.showVolleyError(error, TAG, AboutUsActivity.this);
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

    public void getContactData() {
        StringRequest request = new StringRequest(Request.Method.GET, Endpoints.getContactDetails, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "getData " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("return")) {
                        JSONObject data = object.getJSONObject("data");
                        ContactBean bean = new ContactBean();
                        bean.setId(data.getString("id"));
                        bean.setMobile(data.getString("mobile"));
                        bean.setEmail(data.getString("email"));
                        bean.setName(data.getString("name"));
                        bean.setYoutube(data.getString("youtube"));
                        bean.setFacebook(data.getString("facebook"));
                        bean.setTwitter(data.getString("twitter"));
                        bean.setInstagram(data.getString("instagram"));
                        bean.setYoutube_help_video(data.getString("youtube_help_video"));
                        bean.setAbout_details(data.getString("about_details"));
                        list.add(bean);
                        adapter.notifyDataSetChanged();


                        TextView numberTv = findViewById(R.id.numberTv);
                        numberTv.setText(data.getString("mobile"));
                        String name = data.getString("name");
                        String mobile = data.getString("mobile");

                        TextView nameTv = findViewById(R.id.nameTv);
                        nameTv.setText(name);

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
    }
}