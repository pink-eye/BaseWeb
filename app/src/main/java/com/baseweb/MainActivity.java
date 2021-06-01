package com.baseweb;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {

    private static final String APP_STORAGE = "storage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SharedPreferences storage = getSharedPreferences(APP_STORAGE, Context.MODE_PRIVATE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (storage.getBoolean("firstrun", true)) {
            fragmentTransaction.add(R.id.fragment_spot, new WelcomeFragment(), null)
                    .disallowAddToBackStack()
                    .commit();
        } else {
            fragmentTransaction.add(R.id.fragment_spot, new HomeFragment(), null)
                    .disallowAddToBackStack()
                    .commit();
        }


    }
}