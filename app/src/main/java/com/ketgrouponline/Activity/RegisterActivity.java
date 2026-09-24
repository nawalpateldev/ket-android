package com.ketgrouponline.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ketgrouponline.Network.MyApplication;
import com.ketgrouponline.R;
import com.ketgrouponline.Utils.Endpoints;
import com.ketgrouponline.Utils.MyUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    EditText name, mobile, password;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = findViewById(R.id.name);
        mobile = findViewById(R.id.mobile);
        password = findViewById(R.id.password);

        findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: ");
                if (name.getText().toString().trim().length() == 0) {
                    name.setError("Enter Name");
                } else if (mobile.getText().toString().trim().length() == 0) {
                    mobile.setError("Enter Mobile");
                } else if (password.getText().toString().trim().length() == 0) {
                    password.setError("Enter Password");
                } else {
                    registerUser(name.getText().toString().trim(), password.getText().toString().trim(), mobile.getText().toString().trim());
                }
            }
        });

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void registerUser(String name1, String password1, String mobile1) {
        MyUtils.showProgressDialog(RegisterActivity.this, false);
        StringRequest request = new StringRequest(Request.Method.POST, Endpoints.register, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MyUtils.dismisProgressDialog();
                Log.i(TAG, "registerUser " + response);
                try {
                    JSONObject rootObject = new JSONObject(response);
                    if (rootObject.getBoolean("return")) {
                        Toast.makeText(RegisterActivity.this, rootObject.getString("message"), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.dismisProgressDialog();
                MyUtils.showVolleyError(error, TAG, RegisterActivity.this);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name1);
                params.put("password", password1);
                params.put("mobile", mobile1);
                Log.e("name", name1);
                Log.e("password", password1);
                Log.e("mobile", mobile1);
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