package com.acodigo.godkantsv.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.acodigo.godkantsv.R;
import com.acodigo.godkantsv.common.constants;

public class activity_code_info extends AppCompatActivity {

    TextView textViewInfo2, textViewInfo4;
    Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_info);

        setContents();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setContents() {
        textViewInfo2 = findViewById(R.id.textViewInfo2);
        textViewInfo4 = findViewById(R.id.textViewInfo4);
        buttonBack = findViewById(R.id.buttonBack);

        textViewInfo2.setText(constants.sharedPreferencesGetUserTime(activity_code_info.this));
        textViewInfo4.setText(constants.sharedPreferencesGetDeviceId(activity_code_info.this));

        buttonBack.setOnClickListener(v -> onBackPressed());
    }
}