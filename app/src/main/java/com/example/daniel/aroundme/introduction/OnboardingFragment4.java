package com.example.daniel.aroundme.introduction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.daniel.aroundme.R;

/**
 * Created by Daniel on 2/16/2016.
 */
public class OnboardingFragment4 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle s) {

        return inflater.inflate(R.layout.fourth_onboarding_screen, container, false
        );

    }
}