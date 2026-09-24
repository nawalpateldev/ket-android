package com.ketgrouponline.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ketgrouponline.Network.MyApplication;
import com.ketgrouponline.R;
import com.ketgrouponline.Storage.SPCsnstants;
import com.ketgrouponline.Utils.Endpoints;
import com.ketgrouponline.Utils.MyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    EditText oldEt, newEt, confirmEt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        oldEt = view.findViewById(R.id.oldEt);
        newEt = view.findViewById(R.id.newEt);
        confirmEt = view.findViewById(R.id.confirmEt);
        view.findViewById(R.id.changeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oldEt.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "Enter Old Password", Toast.LENGTH_SHORT).show();
                } else if (newEt.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "Enter New Password", Toast.LENGTH_SHORT).show();
                } else if (confirmEt.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "Enter Confirm Password", Toast.LENGTH_SHORT).show();
                } else if (!confirmEt.getText().toString().trim().equals(newEt.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "New Password and Confirm Password should be same.", Toast.LENGTH_SHORT).show();
                } else {
                    changePassword(oldEt.getText().toString().trim(),
                            confirmEt.getText().toString().trim(),
                            newEt.getText().toString().trim());
                }
            }
        });
        return view;
    }

    public void changePassword(String old_password, String new_confirm_password, String new_password) {
        MyUtils.showProgressDialog(getActivity(), false);
        StringRequest request = new StringRequest(Request.Method.POST, Endpoints.change_password, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MyUtils.dismisProgressDialog();
                Log.i(TAG, "getContactData " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("return")) {
                        oldEt.setText("");
                        confirmEt.setText("");
                        newEt.setText("");
                        Toast.makeText(getActivity(), "Password Changed Successfully.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.dismisProgressDialog();
                MyUtils.showVolleyError(error, TAG, getActivity());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", MyApplication.sharedPreferences.getKey(SPCsnstants.api_token));
                return params;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("old_password", old_password);
                params.put("new_confirm_password", new_confirm_password);
                params.put("new_password", new_password);
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