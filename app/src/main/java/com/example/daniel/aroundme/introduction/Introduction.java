package com.example.daniel.aroundme.introduction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.daniel.aroundme.R;
import com.example.daniel.aroundme.activities_and_fragments.MainActivity;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

/**
 * Created by Daniel on 2/16/2016.
 */

//this class introduce user to the app
    //using view pager and tab layout
public class Introduction extends AppCompatActivity {
    private ViewPager pager;
    private SmartTabLayout indicator;
    private Button skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.intro);

        pager = (ViewPager)findViewById(R.id.container);
        skip = (Button)findViewById(R.id.intro_btn_skip);
        indicator = (SmartTabLayout)findViewById(R.id.indicator);


        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0 : return new OnboardingFragment1();
                    case 1 : return new OnboardingFragment2();
                    case 2 : return new OnboardingFragment3();
                    case 3 : return new OnboardingFragment4();
                    default: return null;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };

        pager.setAdapter(adapter);

        indicator.setViewPager(pager);

        indicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if(position == 3){
                    skip.setVisibility(View.VISIBLE);
                    skip.setText("Done");
                }
            }

        });



        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishOnboarding();
            }
        });


    }

    private void finishOnboarding() {
        SharedPreferences preferences = getSharedPreferences("my_preferences", MODE_PRIVATE);

        preferences.edit().putBoolean("onboarding_complete", true).apply();

        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);

        finish();

    }
}
