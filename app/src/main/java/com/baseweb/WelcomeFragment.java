package com.baseweb;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class WelcomeFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnGetStarted = view.findViewById(R.id.btnGetStarted);

        btnGetStarted.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnGetStarted) {
            toKeyFragment();
        }
    }

    private void toKeyFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragmentManager != null)
            fragmentTransaction.setCustomAnimations(
                    R.animator.fade_in_up,
                    R.animator.fade_out_down,
                    R.animator.fade_in_up,
                    R.animator.fade_out_down);

            fragmentTransaction.replace(R.id.fragment_spot, new KeyFragment(), null)
                    .disallowAddToBackStack()
                    .commit();
    }
}