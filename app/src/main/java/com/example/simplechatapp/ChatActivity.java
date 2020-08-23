package com.example.simplechatapp;

import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.simplechatapp.App.CHANNEL_1_ID;
import static com.example.simplechatapp.App.CHANNEL_2_ID;
import static com.example.simplechatapp.R.mipmap.logo;

public class ChatActivity extends AppCompatActivity {

    private ListView messageListView;
    private MessageAdapter adapter;
    private ProgressBar progressBar;
    private ImageButton sendImageButton;
    private Button sendMessageButton;
    private EditText messageEditText;
    private String recipentUserId;

    private static final int RC_IMAGE_PICKER = 123;
    private FirebaseAuth auth;
    private String recipentUsername;
    private String recipientUserGender;
    private String recipientUserAge;
    private String userName;
    FirebaseDatabase database;
    DatabaseReference messagesDatabaseReference;
    ChildEventListener messagesChildEventListener;
    DatabaseReference usersDatabaseReference;
    ChildEventListener usersChildEventListener;

    FirebaseStorage storage;
    StorageReference chatImagesStorageReference;

    private Toolbar customToolbar;
    private static boolean isFirst = true;

    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        customToolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(customToolbar);
        customToolbar.findViewById(R.id.profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ChatActivity.this, ProfileActivity.class);
                i.putExtra("userName", recipentUsername);
                i.putExtra("userGender", recipientUserGender);
                i.putExtra("userAge", recipientUserAge);
                startActivity(i);
            }
        });
        customToolbar.findViewById(R.id.left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        if (intent != null) {
            userName = intent.getStringExtra("userName");
            recipentUserId = intent.getStringExtra("recipentUserId");
            recipentUsername = intent.getStringExtra("recipentUserName");
            recipientUserGender = intent.getStringExtra("recipientUserGender");
            recipientUserAge = intent.getStringExtra("recipientUserAge");
        }

        TextView recipientName = customToolbar.findViewById(R.id.name);
        recipientName.setText(recipentUsername);

        setTitle(recipentUsername);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        messagesDatabaseReference = database.getReference().child("messages");
        usersDatabaseReference = database.getReference().child("users");
        chatImagesStorageReference = storage.getReference().child("chat_images");

        progressBar = findViewById(R.id.progressBar);
        sendImageButton = findViewById(R.id.sendPhotoButton);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        messageEditText = findViewById(R.id.messageEditText);

        messageListView = findViewById(R.id.messageListView);
        List<Message> messages = new ArrayList<>();
        adapter = new MessageAdapter(this, R.layout.message_item,
                messages);
        messageListView.setAdapter(adapter);

        progressBar.setVisibility(ProgressBar.INVISIBLE);

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    sendMessageButton.setEnabled(true);
                } else {
                    sendMessageButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        messageEditText.setFilters(new InputFilter[]
                {new InputFilter.LengthFilter(500)});


        notificationManager = NotificationManagerCompat.from(this);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Message message = new Message();
                message.setText(messageEditText.getText().toString());
                message.setName(userName);
                message.setSender(auth.getCurrentUser().getUid());
                message.setRecipent(recipentUserId);
                message.setImageUrl(null);

                String date = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
                message.setTime(date);

                messagesDatabaseReference.push().setValue(message);

                messageEditText.setText("");
            }
        });


        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                intent1.setType("image/*");
                intent1.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent1, "Choose an image"), RC_IMAGE_PICKER);
            }
        });

        usersChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    userName = user.getName();
                }
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

        usersDatabaseReference.addChildEventListener(usersChildEventListener);

        messagesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);

                if (message.getSender().equals(auth.getCurrentUser().getUid())
                        && message.getRecipent().equals(recipentUserId)) {
                    message.setMine(true);
                    adapter.add(message);
                } else if (message.getRecipent().equals(auth.getCurrentUser().getUid())
                        && message.getSender().equals(recipentUserId)) {

                    message.setMine(false);
                    sendOnChannel1(message.getName(), message.getText());
                    adapter.add(message);
                }
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

        messagesDatabaseReference.addChildEventListener(messagesChildEventListener);
    }

    private void sendOnChannel1(String title, String message) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_baseline_person_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);
    }

    private void sendOnChannel2() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_baseline_person_24)
                .setContentTitle("This is channel 2")
                .setContentText("This is a very long message")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(2, notification);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ChatActivity.this, SignUpSignInActivity.class));
                return true;
            case R.id.profile:
                startActivity(new Intent(ChatActivity.this, ProfileActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_IMAGE_PICKER && resultCode == RESULT_OK) {
            Uri selectedImgUri = data.getData();

            final StorageReference imageReference = chatImagesStorageReference
                    .child(selectedImgUri.getLastPathSegment());

            UploadTask uploadTask = imageReference.putFile(selectedImgUri);

            uploadTask = imageReference.putFile(selectedImgUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {


                        Uri downloadUri = task.getResult();
                        Message message = new Message();
                        message.setImageUrl(downloadUri.toString());
                        message.setName(userName);
                        message.setSender(auth.getCurrentUser().getUid());
                        message.setRecipent(recipentUserId);
                        messagesDatabaseReference.push().setValue(message);

                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

        }
    }


}