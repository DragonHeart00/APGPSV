package com.acodigo.godkantsv.activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acodigo.godkantsv.R;
import com.acodigo.godkantsv.common.constants;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

public class activity_done extends AppCompatActivity {

    RelativeLayout relativeLayoutWait;
    LinearLayout linearLayoutDone;
    TextView textViewResult;
    Button buttonShowMyAnswers, buttonClose;
    ListView listView;

    final String[] stringArray = new String[]{};

    GifImageView gifImageViewWait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        setContents();
        checkIfFullTest();

        showTestResult();
    }
    @Override
    public void onBackPressed() {
        try {
            if (constants.sharedPreferencesGetTestMode(activity_done.this).equals("exam")) {
                startActivity(new Intent(this, activity_choose_mode.class));
            } else {
                startActivity(new Intent(this, activity_choose_test.class));
            }
        } finally {
            finish();
        }
    }

    private void setContents() {
        relativeLayoutWait = findViewById(R.id.relativeLayoutWait);
        linearLayoutDone = findViewById(R.id.linearLayoutDone);
        textViewResult = findViewById(R.id.textViewResult);
        buttonShowMyAnswers = findViewById(R.id.buttonShowMyAnswers);
        buttonClose = findViewById(R.id.buttonClose);
        listView = findViewById(R.id.listView);
        gifImageViewWait = findViewById(R.id.gifImageViewWait);

        buttonShowMyAnswers.setOnClickListener(view -> startActivity(new Intent(this, activity_answers.class)));
        buttonClose.setOnClickListener(v -> onBackPressed());

        Glide.with(this).load(R.drawable.wait_primary_background_white).centerInside().into(gifImageViewWait);
    }
    private void checkIfFullTest() {
        if (!constants.sharedPreferencesGetIsFullTestMode(activity_done.this)) {
            messageShowSubscribe();
        }
    }

    private void messageShowSubscribe() {
        try {
            final Dialog dialog = new Dialog(activity_done.this, R.style.FullHeightDialog);
            dialog.setContentView(R.layout.dialog_subscribe);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);

            Button button1 = dialog.findViewById(R.id.buttonCall);
            Button button2 = dialog.findViewById(R.id.buttonEmail);
            Button button3 = dialog.findViewById(R.id.buttonClose);

            button1.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + getString(R.string.support_mobile_phone)));
                startActivity(intent);
            });
            button2.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.support_email_address), null));
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_language));
                startActivity(Intent.createChooser(intent, getString(R.string.choose_alternative_colon)));
            });
            button3.setOnClickListener(v -> dialog.dismiss());

            dialog.show();
        } catch (Exception ex) {
            Log.e(constants.logDeveloperError, ex.toString());
        }
    }

    private void showTestResult() {
        try {
            final List<String> ListStrings = new ArrayList<>(Arrays.asList(stringArray));
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ListStrings);
            listView.setAdapter(arrayAdapter);

            String stringTest = getIntent().getStringExtra("TEST_ID");
            String stringTotal = getString(R.string.done_list_total_questions) + " " + getIntent().getStringExtra("TOTAL_QUESTIONS");
            String stringCorrect = getString(R.string.done_list_right_questions) + " " + getIntent().getStringExtra("CORRECT_COUNTER") + "\n(" + getString(R.string.done_list_limit) + " 57)";
            String stringWrong = getString(R.string.done_list_wrong_questions) + " " + getIntent().getStringExtra("WRONG_COUNTER");
            String stringLeft = getString(R.string.done_list_left_questions) + " " + getIntent().getStringExtra("UNANSWERED_COUNTER");

            String stringTime = "";
            if (!constants.sharedPreferencesGetTestMode(activity_done.this).equals("study")) {
                stringTime = getString(R.string.done_list_total_time) + " " + getIntent().getStringExtra("TIME");
            }

            ListStrings.add(stringTest);
            ListStrings.add(stringTotal);
            ListStrings.add(stringCorrect);
            ListStrings.add(stringWrong);
            ListStrings.add(stringLeft);

            if (!constants.sharedPreferencesGetTestMode(activity_done.this).equals("study")) {
                ListStrings.add(stringTime);
            }

            if (Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("CORRECT_COUNTER"))) < 57) {
                textViewResult.setText(getString(R.string.failed));
                textViewResult.setTextColor(ContextCompat.getColor(activity_done.this, R.color.colorRedDark));
                textViewResult.setBackgroundResource(R.drawable.shape_static_square_white_border_red_dark);
            } else {
                textViewResult.setText(getString(R.string.succeed));
                textViewResult.setTextColor(ContextCompat.getColor(activity_done.this, R.color.colorGreenDark));
                textViewResult.setBackgroundResource(R.drawable.shape_static_square_white_border_green_dark);
            }

            arrayAdapter.notifyDataSetChanged();
        } finally {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.e(constants.logDeveloperError, e.toString());
                    }
                    runOnUiThread(() -> {
                        relativeLayoutWait.setVisibility(View.GONE);
                        linearLayoutDone.setVisibility(View.VISIBLE);
                    });
                }
            };
            thread.start();
        }
    }
}