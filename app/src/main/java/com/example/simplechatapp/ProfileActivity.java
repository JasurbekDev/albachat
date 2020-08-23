package com.example.simplechatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {
    private String userName;
    private TextView userNameTextView;
    private String userGender;
    private String userAge;
    private TextView userGenderTextView;
    private TextView userAgeTextView;
    private ImageView leftArrow;
    private TextView goBackText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        if (intent != null) {
            userName = intent.getStringExtra("userName");
            userGender = intent.getStringExtra("userGender");
            userAge = intent.getStringExtra("userAge");
        }

        userNameTextView = findViewById(R.id.profileUserNameTextView);
        userGenderTextView = findViewById(R.id.profileGenderTextView);
        userAgeTextView = findViewById(R.id.profileAgeTextView);
        userNameTextView.setText(userName);
        userGenderTextView.setText(userGender);
        userAgeTextView.setText(userAge);

        leftArrow = findViewById(R.id.profile_left_arrow);
        goBackText = findViewById(R.id.profile_go_back_text);
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        goBackText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}