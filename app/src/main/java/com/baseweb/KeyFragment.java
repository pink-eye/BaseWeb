package com.baseweb;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KeyFragment extends Fragment implements View.OnClickListener {

    private static final String APP_STORAGE = "storage";
    private static final String API_KEY = "key";

    private EditText etKey;
    private ConstraintLayout fragmentKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_key, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etKey = view.findViewById(R.id.etKey);
        fragmentKey = view.findViewById(R.id.fragmentKey);
        ImageButton btnKey = view.findViewById(R.id.btnKey);

        btnKey.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnKey) {
            if (!etKey.getText().toString().equals("")) {

                LinkPreviewService.getInstance()
                        .getAPI()
                        .getJSON("?key=" + etKey.getText().toString() + "&q=google.com")
                        .enqueue(new Callback<LinkPreviewObject>() {
                            @Override
                            public void onResponse(@NonNull Call<LinkPreviewObject> call, @NonNull Response<LinkPreviewObject> response) {

                                if(response.isSuccessful()){
                                    showToast("Successfull!");
                                    saveAPI(etKey.getText().toString());
                                    toHomeFragment();
                                } else showToast("Fail... API key is invalid");

                            }

                            @Override
                            public void onFailure(@NonNull Call<LinkPreviewObject> call, @NonNull Throwable t) {
                                if (t instanceof IOException){
                                    showToast("Check network connection");
                                }
                            }

                        });
            } else {
                showToast("Print own API key");
            }
        }

    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private void toHomeFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(fragmentManager != null)
            fragmentTransaction.setCustomAnimations(
                    R.animator.fade_in_up,
                    R.animator.fade_out_down,
                    R.animator.fade_in_up,
                    R.animator.fade_out_down);

        fragmentTransaction.replace(R.id.fragment_spot, new HomeFragment(), null)
                .disallowAddToBackStack()
                .commit();
    }

    private void saveAPI(String key) {
        SharedPreferences storage = getActivity().getSharedPreferences(APP_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = storage.edit();

        editor.putString(API_KEY, key);
        storage.edit().putBoolean("firstrun", false).apply();
        editor.apply();
    }
}