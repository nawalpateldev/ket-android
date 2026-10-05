package com.ketgrouponline.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ketgrouponline.Network.MyApplication;
import com.ketgrouponline.R;
import com.ketgrouponline.Storage.SPCsnstants;
import com.ketgrouponline.Utils.Endpoints;
import com.ketgrouponline.Utils.MyUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    Dialog dialog;
    EditText mobile_edit_text, password_edit_text;
    String fmobile, fpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dialog = new Dialog(this);

        mobile_edit_text = findViewById(R.id.mobile_edit_text);
        password_edit_text = findViewById(R.id.password_edit_text);

        findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        findViewById(R.id.login_otp_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mobile_edit_text.getText().toString().trim().equals("")) {
                    if (mobile_edit_text.getText().toString().trim().length() == 10) {
                        loginWithOtp(mobile_edit_text.getText().toString().trim());
                    } else {
                        Toast.makeText(LoginActivity.this, "Enter your 10 digit mobile number", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Enter your 10 digit mobile number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fmobile = mobile_edit_text.getText().toString();
                fpassword = password_edit_text.getText().toString();
                if (fmobile.length() == 0) {
                    mobile_edit_text.setError("Enter Mobile");
                } else if (fpassword.length() == 0) {
                    password_edit_text.setError("Enter Password");
                } else {
                    login(fmobile, fpassword);
                }
            }
        });
    }

    private void login(String username, String password) {
        MyUtils.showProgressDialog(LoginActivity.this, false);
        StringRequest request = new StringRequest(Request.Method.POST, Endpoints.login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MyUtils.dismisProgressDialog();
                Log.i(TAG, "login " + response);
                try {
                    JSONObject rootObject = new JSONObject(response);
                    if (rootObject.getBoolean("return")) {
                        JSONObject object = rootObject.getJSONObject("data");
                        MyApplication.sharedPreferences.setKey(SPCsnstants.api_token, getJsonString(object, "api_token"));
                        MyApplication.sharedPreferences.setKey(SPCsnstants.ID_KEY, getJsonString(object, "id"));
                        MyApplication.sharedPreferences.setKey(SPCsnstants.NAME_KEY, getJsonString(object, "name"));
                        MyApplication.sharedPreferences.setKey(SPCsnstants.EMAIL_KEY, getJsonString(object, "email"));
                        MyApplication.sharedPreferences.setKey(SPCsnstants.MOBILE_KEY, getJsonString(object, "mobile"));
                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.dismisProgressDialog();
                Toast.makeText(LoginActivity.this, "Invalid Mobile Or Password", Toast.LENGTH_SHORT).show();
                //MyUtils.showVolleyError(error, TAG, LoginActivity.this);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("password", password);
                params.put("username", username);

                Log.e("password", password);
                Log.e("username", username);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.mRequestQue.add(request);
    }

    public void showOtpDialog(String mobile) {
        EditText otpEt;
        dialog.setContentView(R.layout.enter_otp_dialog);
        otpEt = dialog.findViewById(R.id.otpEt);
        dialog.findViewById(R.id.regisfabcancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.resend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOtp(mobile);
            }
        });

        dialog.findViewById(R.id.submitbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!otpEt.getText().toString().trim().equals("")) {
                    if (otpEt.getText().toString().trim().length() == 6) {
                        verifyOtp(mobile, otpEt.getText().toString().trim());
                    } else {
                        Toast.makeText(LoginActivity.this, "Enter 6 Digit OTP", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Enter 6 Digit OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.getWindow().setBackgroundDrawableResource(R.color.blacktrans);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    }

    private void loginWithOtp(String mobile) {
        MyUtils.showProgressDialog(LoginActivity.this, false);
        StringRequest request = new StringRequest(Request.Method.POST, Endpoints.login_with_otp, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MyUtils.dismisProgressDialog();
                Log.i(TAG, "loginWithOtp " + response);
                try {
                    JSONObject rootObject = new JSONObject(response);
                    if (rootObject.getBoolean("return")) {
                        showOtpDialog(mobile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.dismisProgressDialog();
                Toast.makeText(LoginActivity.this, "Invalid Mobile Or Password", Toast.LENGTH_SHORT).show();
                //MyUtils.showVolleyError(error, TAG, LoginActivity.this);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", mobile);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.mRequestQue.add(request);
    }

    private void resendOtp(String mobile) {
        MyUtils.showProgressDialog(LoginActivity.this, false);
        StringRequest request = new StringRequest(Request.Method.POST, Endpoints.login_with_otp, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MyUtils.dismisProgressDialog();
                Log.i(TAG, "resendOtp " + response);
                try {
                    JSONObject rootObject = new JSONObject(response);
                    if (rootObject.getBoolean("return")) {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.dismisProgressDialog();
                Toast.makeText(LoginActivity.this, "Invalid Mobile Or Password", Toast.LENGTH_SHORT).show();
                //MyUtils.showVolleyError(error, TAG, LoginActivity.this);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", mobile);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.mRequestQue.add(request);
    }

    private void verifyOtp(String mobile, String otp) {
        MyUtils.showProgressDialog(LoginActivity.this, false);
        StringRequest request = new StringRequest(Request.Method.POST, Endpoints.verify_otp, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MyUtils.dismisProgressDialog();
                Log.i(TAG, "verifyOtp " + response);
                try {
                    JSONObject rootObject = new JSONObject(response);
                    if (rootObject.getBoolean("return")) {
                        JSONObject object = rootObject.getJSONObject("data");
                        MyApplication.sharedPreferences.setKey(SPCsnstants.api_token, getJsonString(object, "api_token"));
                        MyApplication.sharedPreferences.setKey(SPCsnstants.ID_KEY, getJsonString(object, "id"));
                        MyApplication.sharedPreferences.setKey(SPCsnstants.NAME_KEY, getJsonString(object, "name"));
                        MyApplication.sharedPreferences.setKey(SPCsnstants.EMAIL_KEY, getJsonString(object, "email"));
                        MyApplication.sharedPreferences.setKey(SPCsnstants.MOBILE_KEY, getJsonString(object, "mobile"));
                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.dismisProgressDialog();
                Toast.makeText(LoginActivity.this, "Invalid Otp", Toast.LENGTH_SHORT).show();
                //MyUtils.showVolleyError(error, TAG, LoginActivity.this);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", mobile);
                params.put("otp", otp);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.mRequestQue.add(request);
    }

    private String getJsonString(JSONObject object, String key) {
        if (object != null && object.has(key) && !object.isNull(key)) {
            String val = object.optString(key, "");
            return val.equalsIgnoreCase("null") ? "" : val;
        }
        return "";
    }
}