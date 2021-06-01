package com.baseweb;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements View.OnClickListener {


    private static final String APP_STORAGE = "storage";
    private static final String TITLE_KEY = "title";
    private static final String DESCR_KEY = "descr";
    private static final String URL_KEY = "url";
    private static final String LOGO_KEY = "logo";
    private static final String BASE_URL_LOGO = "https://logo.clearbit.com/";
    private static final String API_KEY = "key";

    private EditText etSearch;
    private ImageView imageView;
    private TextView tvTitle, tvDescription, tvURL;
    private LinearLayout card, searchBox;
    private ImageView imageViewHome;

    private Animation cardShow, cardHide, searchBoxAnim, fadeOut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearch = view.findViewById(R.id.etSearch);

        imageView = view.findViewById(R.id.img);
        imageViewHome = view.findViewById(R.id.imageViewHome);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvURL = view.findViewById(R.id.tvURL);
        card = view.findViewById(R.id.card);
        searchBox = view.findViewById(R.id.searchBox);

        cardShow = AnimationUtils.loadAnimation(getActivity(), R.anim.card_show);
        cardHide = AnimationUtils.loadAnimation(getActivity(), R.anim.card_hide);
        searchBoxAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.search_box_anim);
        fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
        cardShow.setAnimationListener(cardShowListener);
        cardHide.setAnimationListener(cardHideListener);
        fadeOut.setAnimationListener(fadeOutListener);
        searchBox.startAnimation(searchBoxAnim);

        LinkPreviewObject savedLpo = getLastData();

        if (isExist()) {
            hideView(imageViewHome);
            fillCard(savedLpo);
            card.startAnimation(cardShow);
        }

        ImageButton btnSearch = view.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        imageView.setOnClickListener(this);
        tvTitle.setOnClickListener(this);
        tvDescription.setOnClickListener(this);
    }

    Animation.AnimationListener fadeOutListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
            if (isVisible(imageViewHome)) hideView(imageViewHome);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    };

    Animation.AnimationListener cardHideListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
            clearCard();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    };

    Animation.AnimationListener cardShowListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSearch) {
            if (!etSearch.getText().toString().equals("")) {

                if(isVisible(imageViewHome)) imageViewHome.startAnimation(fadeOut);

                hideKeyboard();
                card.startAnimation(cardHide);

                LinkPreviewService.getInstance()
                        .getAPI()
                        .getJSON("?key=" + getAPIKey() + "&q=" + etSearch.getText().toString())
                        .enqueue(new Callback<LinkPreviewObject>() {
                            @Override
                            public void onResponse(@NonNull Call<LinkPreviewObject> call, @NonNull Response<LinkPreviewObject> response) {

                                if(response.isSuccessful()){
                                    assert response.body() != null;
                                    response.body().setLogo(BASE_URL_LOGO + etSearch.getText().toString() + "?size=800");
                                    fillCard(response.body());
                                    saveLastData(response.body());
                                } else setError();

                                card.startAnimation(cardShow);
                            }

                            @Override
                            public void onFailure(@NonNull Call<LinkPreviewObject> call, @NonNull Throwable t) {


                                if (t instanceof IOException){
                                    setNoNetwork();
                                    card.startAnimation(cardShow);
                                }
                            }

                        });

            } else
                showToast("The Search field is empty, yo");
        }

        if (v.getId() == R.id.img) saveImage();
        if (v.getId() == R.id.tvTitle) {
            setClipboard(getContext(), tvTitle.getText().toString());
            showToast("Title was copied");
        }
        if (v.getId() == R.id.tvDescription) {
            setClipboard(getContext(), tvDescription.getText().toString());
            showToast("Description was copied");
        }
    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private void fillCard(LinkPreviewObject lpo) {
        tvTitle.setText(lpo.getTitle());
        tvDescription.setText(lpo.getDescription());
        tvURL.setText(lpo.getUrl());
        Picasso.get()
                .load(lpo.getLogo())
                .placeholder(R.drawable.ic_launcher)
                .transform(new CircularTransformation())
                .into(imageView);
    }

    private void clearCard() {
        imageView.setImageDrawable(null);
        tvURL.setText("");
        tvTitle.setText("");
        tvDescription.setText("");
    }

    private void saveLastData(LinkPreviewObject lpo) {
        SharedPreferences storage = getContext().getSharedPreferences(APP_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = storage.edit();

        editor.putString(TITLE_KEY, lpo.getTitle());
        editor.putString(DESCR_KEY, lpo.getDescription());
        editor.putString(URL_KEY, lpo.getUrl());
        editor.putString(LOGO_KEY, lpo.getLogo());

        editor.apply();
    }

    private LinkPreviewObject getLastData() {
        LinkPreviewObject lpo = new LinkPreviewObject();
        SharedPreferences storage = getContext().getSharedPreferences(APP_STORAGE, Context.MODE_PRIVATE);

        lpo.setTitle(storage.getString(TITLE_KEY, null));
        lpo.setDescription(storage.getString(DESCR_KEY, null));
        lpo.setUrl(storage.getString(URL_KEY, null));
        lpo.setLogo(storage.getString(LOGO_KEY, null));

        return lpo;
    }

    private boolean isExist() {
        SharedPreferences storage = getContext().getSharedPreferences(APP_STORAGE, Context.MODE_PRIVATE);
        return storage.getString(TITLE_KEY, null) != null;
    }

    private void saveImage() {
        imageView.buildDrawingCache();
        Bitmap bm = imageView.getDrawingCache();
        MediaStore.Images.Media.insertImage(
                getActivity().getContentResolver(),
                bm,
                tvTitle.getText().toString(),
                tvDescription.getText().toString());

        showToast("Image was saved");
    }

    private void setClipboard(Context context, String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
    }

    private void setError() {
        Picasso.get()
                .load(R.drawable.error)
                .into(imageView);
        tvTitle.setText("Error :(");
        tvDescription.setText("\t- Maybe this domain doesn't exist;\n\n" +
                "\t- The requested website does not allow to access this page;\n\n" +
                "\t- Too many requests per second on a single domain;\n\n" +
                "\t- Too many requests / rate limit exceeded.");

        tvURL.setText(null);
    }

    private void setNoNetwork() {
        Picasso.get()
                .load(R.drawable.nonetwork)
                .into(imageView);
        tvTitle.setText("Hmm...");
        tvDescription.setText("Check your network connection.");
        tvURL.setText(null);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    private String getAPIKey() {
        SharedPreferences storage = getContext().getSharedPreferences(APP_STORAGE, Context.MODE_PRIVATE);
        return storage.getString(API_KEY, null);
    }

    private void hideView (View view) {
        view.setVisibility(View.GONE);
    }

    private boolean isVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }
}