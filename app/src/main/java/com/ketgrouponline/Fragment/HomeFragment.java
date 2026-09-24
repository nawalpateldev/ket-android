package com.ketgrouponline.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ketgrouponline.Adapter.HomeItemAdapter;
import com.ketgrouponline.Adapter.NoticAdapter;
import com.ketgrouponline.Bean.NoticBean;
import com.ketgrouponline.Bean.VendorBean;
import com.ketgrouponline.Network.MyApplication;
import com.ketgrouponline.R;
import com.ketgrouponline.Storage.SPCsnstants;
import com.ketgrouponline.Utils.Endpoints;
import com.ketgrouponline.Utils.MyUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    List<VendorBean> list;
    HomeItemAdapter adapter;
    View view;
    SwipeRefreshLayout swipeRefreshLayout;
    int requestCount = 0;
    RecyclerView recyclerview, recyclerview2;
    ProgressBar progressBar, progressBar2;
    EditText searchEt;
    NoticAdapter adapter2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerview = view.findViewById(R.id.recyclerview);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerview2 = view.findViewById(R.id.recyclerview2);
        progressBar2 = view.findViewById(R.id.progressBar2);
        recyclerview2.setNestedScrollingEnabled(false);
        noticBeanList = new ArrayList<>();
        adapter2 = new NoticAdapter(noticBeanList, getActivity());
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        recyclerview2.setLayoutManager(linearLayoutManager2);
        recyclerview2.setAdapter(adapter2);
        getNotice();

        searchEt = view.findViewById(R.id.searchEt);
        list = new ArrayList<>();
        adapter = new HomeItemAdapter(list, getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setNestedScrollingEnabled(false);
        recyclerview.setAdapter(adapter);
        getData(requestCount);

        searchEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });


        recyclerview.setOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                        if (MyUtils.isLastItemDisplaying(recyclerView)) {
                            requestCount++;
                            getData(requestCount);
                        }
                        super.onScrolled(recyclerView, dx, dy);
                    }
                });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                adapter.notifyDataSetChanged();
                requestCount = 0;
                getData(requestCount);
                if (getActivity() != null) {
                    Intent in = new Intent("wallet");
                    getActivity().sendBroadcast(in);
                }
            }
        });

        getContactData();
        return view;
    }

    boolean isLoading = false;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public void getData(int requestCount) {
        if (isLoading) return;
        isLoading = true;
        Log.e(TAG, "getData: " + Endpoints.get_vendors + "/" + requestCount);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.GET, Endpoints.get_vendors + "/" + requestCount, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        final List<VendorBean> parsedVendors = new ArrayList<>();
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("return")) {
                                JSONArray array = object.getJSONArray("data");
                                if (array.length() > 0) {
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object1 = array.getJSONObject(i);
                                        VendorBean bean = new VendorBean();
                                        bean.setId(object1.optString("id", ""));
                                        bean.setShop_name(object1.optString("shop_name", ""));
                                        bean.setOpening_time(object1.optString("opening_time", ""));
                                        bean.setClosing_time(object1.optString("closing_time", ""));
                                        bean.setTodays_result(object1.optString("todays_result", ""));
                                        parsedVendors.add(bean);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    isLoading = false;
                                    swipeRefreshLayout.setRefreshing(false);
                                    progressBar.setVisibility(View.GONE);
                                    if (!parsedVendors.isEmpty()) {
                                        list.addAll(parsedVendors);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                if (getActivity() != null) {
                    MyUtils.showVolleyError(error, TAG, getActivity());
                }
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
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.mRequestQue.add(request);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void getContactData() {
        StringRequest request = new StringRequest(Request.Method.GET, Endpoints.getContactDetails, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "getContactData " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("return")) {
                        JSONObject data = object.getJSONObject("data");
                        String mobile = data.optString("mobile", "");

                        if (view != null) {
                            TextView numberTv = view.findViewById(R.id.numberTv);
                            if (numberTv != null) {
                                numberTv.setText(mobile);
                            }

                            ImageView whatsAppIv = view.findViewById(R.id.whatsAppIv1);
                            ImageView callIv = view.findViewById(R.id.callIv);
                            if (whatsAppIv != null) {
                                whatsAppIv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.e(TAG, "onClick: " + mobile);
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + mobile + "&text=" + "Hi ketgrouponline"));
                                        startActivity(intent);
                                    }
                                });
                            }

                            if (callIv != null) {
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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (getActivity() != null) {
                    MyUtils.showVolleyError(error, TAG, getActivity());
                }
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
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.mRequestQue.add(request);
    }

    List<NoticBean> noticBeanList;

    public void getNotice() {
        progressBar2.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.GET, Endpoints.getNotice, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        final List<NoticBean> parsedNotices = new ArrayList<>();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("return")) {
                                JSONArray array = jsonObject.getJSONArray("data");
                                if (array.length() > 0) {
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = array.getJSONObject(i);
                                        NoticBean bean = new NoticBean();
                                        bean.setId(object.optString("id", ""));
                                        bean.setTitle(object.optString("title", ""));
                                        bean.setDetails(object.optString("details", ""));
                                        parsedNotices.add(bean);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar2.setVisibility(View.GONE);
                                    if (!parsedNotices.isEmpty()) {
                                        noticBeanList.clear();
                                        noticBeanList.addAll(parsedNotices);
                                        adapter2.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar2.setVisibility(View.GONE);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Accept", "application/json");
                String token = MyApplication.sharedPreferences.getKey(SPCsnstants.api_token);
                if (token != null) {
                    params.put("Authorization", token);
                }
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.mRequestQue.add(request);
    }
}