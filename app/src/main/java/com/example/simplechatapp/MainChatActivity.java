package com.example.simplechatapp;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainChatActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private String userName;
    private ListView messageListView;
    private MessageAdapter adapter;
    private ProgressBar mainChatProgressBar;
    private Button mainChatButton;
    private EditText mainChatEditText;
    private Toolbar customToolbar;

    FirebaseDatabase database;
    DatabaseReference messagesDatabaseReferences;
    ChildEventListener messagesChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        customToolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(customToolbar);
        customToolbar.findViewById(R.id.left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SharedPreferences sharedPreferences = this.
                getSharedPreferences("myUserNameFile", Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("username", "");

        database = FirebaseDatabase.getInstance();
        messagesDatabaseReferences = database.getReference().child("main_chat_messages");

        mainChatProgressBar = findViewById(R.id.mainChatProgressBar);
        mainChatButton = findViewById(R.id.mainChatButton);
        mainChatEditText = findViewById(R.id.mainChatEditText);

        messageListView = findViewById(R.id.mainChatListView);
        List<Message> messages = new ArrayList<>();
        adapter = new MessageAdapter(this, R.layout.message_item,
                messages);
        messageListView.setAdapter(adapter);

        mainChatProgressBar.setVisibility(ProgressBar.INVISIBLE);

        mainChatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mainChatButton.setEnabled(true);
                } else {
                    mainChatButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mainChatEditText.setFilters(new InputFilter[]
                {new InputFilter.LengthFilter(500)});

        mainChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Message message = new Message();
                message.setText(mainChatEditText.getText().toString());
                message.setName(userName);

                String date = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
                message.setTime(date);

                messagesDatabaseReferences.push().setValue(message);

                mainChatEditText.setText("");
            }
        });

        messagesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);

                    adapter.add(message);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        messagesDatabaseReferences.addChildEventListener(messagesChildEventListener);

    }
}