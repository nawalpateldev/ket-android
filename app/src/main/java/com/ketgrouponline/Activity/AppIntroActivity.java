package com.ketgrouponline.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.ketgrouponline.Adapter.IntroAdapter;
import com.ketgrouponline.Network.MyApplication;
import com.ketgrouponline.R;
import com.ketgrouponline.Storage.SPCsnstants;

public class AppIntroActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private Button btnNext;
    private IntroAdapter introAdapter;
    private TextView btnSkip;
    private int currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_intro);
        transparentStatusBar();

        findViews();

        setClickListener();

        setupViewPager();

        addDotsIndicator(0);
    }

    private void launchMainScreen() {
        if (MyApplication.sharedPreferences.getKey(SPCsnstants.api_token) != null) {
            Intent intent = new Intent(AppIntroActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(AppIntroActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void transparentStatusBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    private void setupViewPager() {
        introAdapter = new IntroAdapter(this);
        viewPager.setAdapter(introAdapter);
        viewPager.addOnPageChangeListener(pageChangeListener);
    }

    private void findViews() {
        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);
    }

    private void setClickListener() {
        btnSkip.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    public void addDotsIndicator(int position) {
        TextView[] mDots = new TextView[introAdapter.getCount()];

        dotsLayout.removeAllViews();

        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("•"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.dot_inactive_color));

            dotsLayout.addView(mDots[i]);
        }

        if (mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.dot_active_color));
        }
    }

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {

            currentItem = position;
            addDotsIndicator(position);

            if (position == introAdapter.getCount() - 1) {
                btnNext.setText(getString(R.string.finish));
                btnSkip.setVisibility(View.INVISIBLE);
            } else {
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnNext) {
            if (currentItem < introAdapter.getCount() - 1) {
                ++currentItem;
                viewPager.setCurrentItem(currentItem);
            } else
                launchMainScreen();
        } else if (id == R.id.btnSkip) {
            launchMainScreen();
        }
    }
}