package com.ketgrouponline.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ketgrouponline.Adapter.JodiDigitAdapter;
import com.ketgrouponline.Bean.NumberBean;
import com.ketgrouponline.Network.MyApplication;
import com.ketgrouponline.R;
import com.ketgrouponline.Storage.GameCacheManager;
import com.ketgrouponline.Storage.SPCsnstants;
import com.ketgrouponline.Utils.Endpoints;
import com.ketgrouponline.Utils.MyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JodiDigitActivity extends AppCompatActivity {
    private static final String TAG = "JodiDigitActivity";
    String vendor_id;
    Spinner spinner;
    String[] gameTypeArray;
    ArrayAdapter arrayAdapter;
    TextView timeTv;
    List<NumberBean> list;
    RecyclerView recyclerView;
    JodiDigitAdapter adapter;
    public static EditText totalEt;
    String type;
    TextView typeTv;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jodi_digit);
        initToolBar();
        vendor_id = getIntent().getStringExtra("vendor_id");
        type = getIntent().getStringExtra("type");
        TextView titleTv2 = findViewById(R.id.titleTv2);
        titleTv2.setVisibility(View.VISIBLE);
        titleTv2.setText("Market Name - " + getIntent().getStringExtra("market"));
        spinner = findViewById(R.id.spinner);
        timeTv = findViewById(R.id.timeTv);
        totalEt = findViewById(R.id.totalEt);
        typeTv = findViewById(R.id.typeTv);
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        list = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, gameTypeArray);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        timeTv.setText(MyUtils.convertDate(System.currentTimeMillis() / 1000 + ""));
        adapter = new JodiDigitAdapter(list, JodiDigitActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(JodiDigitActivity.this, 10);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        getData("open");

        findViewById(R.id.submitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (spinner.getSelectedItem().toString()) {
                    case "Select Game":
                        Toast.makeText(JodiDigitActivity.this, "Select Game Type", Toast.LENGTH_SHORT).show();
                        break;
                    case "Open":
                        checkWallet("open");
                        break;
                    case "Closed":
                        checkWallet("closed");
                        break;
                }
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TextView walletTv = findViewById(R.id.walletTv);
                wallet(walletTv);
                list.clear();
                adapter.notifyDataSetChanged();
                getData("open");
            }
        });

    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private void getData(String game_type) {
        String url = "";
        if (type.equals("jodi")) {
            url = Endpoints.jodi_digit;
            typeTv.setText("Jodi");
        } else if (type.equals("single_pana")) {
            url = Endpoints.single_pana;
            typeTv.setText("Single Pana");
        } else if (type.equals("double_pana")) {
            url = Endpoints.double_pana;
            typeTv.setText("Double Pana");
        } else if (type.equals("triple_pana")) {
            url = Endpoints.triple_pana;
            typeTv.setText("Triple Pana");
        }

        final String cacheKey = vendor_id + "_" + game_type + "_" + type;
        List<NumberBean> cachedData = GameCacheManager.getInstance().get(cacheKey);

        if (cachedData != null && !cachedData.isEmpty()) {
            list.clear();
            list.addAll(cachedData);
            adapter.notifyDataSetChanged();
            if (!GameCacheManager.getInstance().isExpired(cacheKey)) {
                return;
            }
        } else {
            MyUtils.showProgressDialog(JodiDigitActivity.this, false);
        }

        final String finalType = type;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        final List<NumberBean> parsedList = new ArrayList<>();
                        try {
                            JSONObject rootObject = new JSONObject(response);
                            if (rootObject.getBoolean("return")) {
                                JSONObject data = rootObject.getJSONObject("data");
                                JSONArray numbers = data.getJSONArray("numbers");
                                if (numbers.length() > 0) {
                                    for (int i = 0; i < numbers.length(); i++) {
                                        NumberBean bean = new NumberBean();
                                        JSONObject jsonObject = numbers.getJSONObject(i);
                                        bean.setSid(jsonObject.getString("sid"));
                                        if (finalType.equals("triple_pana")) {
                                            if (jsonObject.getString("digit").equals("0")) {
                                                bean.setDigit("000");
                                            } else {
                                                bean.setDigit(jsonObject.getString("digit"));
                                            }
                                        } else {
                                            bean.setDigit(jsonObject.getString("digit"));
                                        }
                                        parsedList.add(bean);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyUtils.dismisProgressDialog();
                                if (!parsedList.isEmpty()) {
                                    GameCacheManager.getInstance().put(cacheKey, parsedList);
                                    list.clear();
                                    list.addAll(parsedList);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.dismisProgressDialog();
                MyUtils.showVolleyError(error, TAG, JodiDigitActivity.this);
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
                params.put("vendor_id", vendor_id != null ? vendor_id : "");
                params.put("game_type", game_type != null ? game_type : "");
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.mRequestQue.add(request);
    }

    private void submitChal(String game_type) {
        int checkCount = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getValue() != 0) {
                checkCount++;
            }
        }
        if (checkCount == 0) {
            MyUtils.dismisProgressDialog();
            Toast.makeText(this, "Enter at least one value.", Toast.LENGTH_SHORT).show();
        }
        StringRequest request = new StringRequest(Request.Method.POST, Endpoints.save_chaal, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MyUtils.dismisProgressDialog();
                Log.i(TAG, "submitChal " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("return")) {
                        TextView walletTv = findViewById(R.id.walletTv);
                        wallet(walletTv);
                        Toast.makeText(JodiDigitActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.dismisProgressDialog();
                MyUtils.showVolleyError(error, TAG, JodiDigitActivity.this);
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
                params.put("vendor_id", vendor_id);
                params.put("game_type", game_type);
                params.put("type", type);
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getValue() != 0) {
                        params.put("numbers[" + i + "]", list.get(i).getDigit() + "");
                    }
                }

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getValue() != 0) {
                        params.put("points[" + i + "]", list.get(i).getValue() + "");
                    }
                }
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
        titleTv.setText(getIntent().getStringExtra("name"));
        if (!getIntent().getStringExtra("type").equalsIgnoreCase("jodi")) {
            gameTypeArray = new String[]{"Select Game", "Open", "Closed"};
            Log.e(TAG, "initToolBar: 1");
        } else {
            Log.e(TAG, "initToolBar: 2");
            gameTypeArray = new String[]{"Select Game", "Open"};
        }
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
        StringRequest request = new StringRequest(Request.Method.GET, Endpoints.wallet, new Response.Listener<String>() {
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.showVolleyError(error, TAG, JodiDigitActivity.this);
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

    private void checkWallet(String status) {
        MyUtils.showProgressDialog(JodiDigitActivity.this, false);
        StringRequest request = new StringRequest(Request.Method.GET, Endpoints.wallet, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "wallet " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("return")) {
                        float total = 0;
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getValue() != 0) {
                                total = total + Float.parseFloat(list.get(i).getValue() + "");
                            }
                        }
                        if (Float.parseFloat(object.getString("wallet")) > total) {
                            submitChal(status);
                        } else {
                            MyUtils.dismisProgressDialog();
                            Toast.makeText(JodiDigitActivity.this, "You have insufficient point in your wallet.", Toast.LENGTH_SHORT).show();
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
                MyUtils.showVolleyError(error, TAG, JodiDigitActivity.this);
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

    @Override
    protected void onResume() {
        super.onResume();
        getContactData();
        TextView walletTv = findViewById(R.id.walletTv);
        wallet(walletTv);
    }

    public void getContactData() {
        StringRequest request = new StringRequest(Request.Method.GET, Endpoints.getContactDetails, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "getData " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("return")) {
                        JSONObject data = object.getJSONObject("data");
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
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + mobile + "&text=" + "Hello"));
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
        }, new Response.ErrorListener() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        totalEt = null;
    }
}