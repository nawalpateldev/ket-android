package com.ketgrouponline.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ketgrouponline.Adapter.TransactionAdapter;
import com.ketgrouponline.Model.TransactionBean;
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

public class WalletFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    TextView walletTv;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerview;
    ProgressBar progressBar;
    List<TransactionBean> list;
    TransactionAdapter adapter;
    int requestCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        walletTv = view.findViewById(R.id.walletTv);
        //swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerview = view.findViewById(R.id.recyclerview);
        progressBar = view.findViewById(R.id.progressBar);
        list = new ArrayList<>();
        wallet();
        adapter = new TransactionAdapter(list, getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setAdapter(adapter);
        transactionHistory(requestCount);

        recyclerview.setOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                        if (MyUtils.isLastItemDisplaying(recyclerView)) {
                            requestCount++;
                            transactionHistory(requestCount);
                        }
                        super.onScrolled(recyclerView, dx, dy);
                    }
                });
        return view;
    }

    private void wallet() {
        StringRequest request = new StringRequest(Request.Method.GET, Endpoints.wallet, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "wallet " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("return")) {
                        walletTv.setText(object.getString("wallet") + " Points");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.showVolleyError(error, TAG, getActivity());
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

    private void transactionHistory(int requestCount) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.GET, Endpoints.transaction_histories+"/"+requestCount, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                Log.i(TAG, "transactionHistory " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray data = object.getJSONArray("data");
                    if (data.length() > 0) {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object1 = data.getJSONObject(i);
                            TransactionBean bean = new TransactionBean();
                            bean.setId(object1.getString("id"));
                            bean.setUser_id(object1.getString("user_id"));
                            bean.setType(object1.getString("type"));
                            bean.setPoints(object1.getString("points"));
                            bean.setReason(object1.getString("reason"));
                            bean.setTime(object1.getString("time"));
                            list.add(bean);
                        }
                        adapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                MyUtils.showVolleyError(error, TAG, getActivity());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", MyApplication.sharedPreferences.getKey(SPCsnstants.api_token));
                Log.e("Authorization", MyApplication.sharedPreferences.getKey(SPCsnstants.api_token));
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