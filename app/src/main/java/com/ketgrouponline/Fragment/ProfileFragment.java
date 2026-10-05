package com.ketgrouponline.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ketgrouponline.R;
import com.ketgrouponline.Storage.MySharedPreferences;
import com.ketgrouponline.Storage.SPCsnstants;

public class ProfileFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    TextView name,mobile,email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        name = view.findViewById(R.id.name);
        mobile = view.findViewById(R.id.mobile);
        email = view.findViewById(R.id.email);

        String nameStr = MySharedPreferences.getInstance(getContext()).getKey(SPCsnstants.NAME_KEY);
        String mobileStr = MySharedPreferences.getInstance(getContext()).getKey(SPCsnstants.MOBILE_KEY);
        String emailStr = MySharedPreferences.getInstance(getContext()).getKey(SPCsnstants.EMAIL_KEY);

        name.setText(nameStr != null && !nameStr.equalsIgnoreCase("null") ? nameStr : "");
        mobile.setText(mobileStr != null && !mobileStr.equalsIgnoreCase("null") ? mobileStr : "");
        email.setText(emailStr != null && !emailStr.equalsIgnoreCase("null") ? emailStr : "");

        return view;
    }


}