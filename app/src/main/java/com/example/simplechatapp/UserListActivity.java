package com.example.simplechatapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserListActivity extends AppCompatActivity {

    private String userName;

    private FirebaseAuth auth;
    private DatabaseReference usersDatabaseReference;
    private ChildEventListener usersChildEventListener;

    private ArrayList<User> userArrayList;
    private RecyclerView userRecyclerView;
    private UserAdapter userAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Toolbar customToolbar;
    private TextView view;
    private String otherName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        view = findViewById(R.id.goToMainChat);

        customToolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(customToolbar);
        TextView toolbarName = customToolbar.findViewById(R.id.name);
        toolbarName.setText("AlbaChat");
        customToolbar.findViewById(R.id.online_state).setVisibility(View.GONE);
        customToolbar.findViewById(R.id.left_arrow).setVisibility(View.GONE);

        Intent intent = getIntent();
        if (intent != null) {
            userName = intent.getStringExtra("userName");
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserListActivity.this, MainChatActivity.class);
                intent.putExtra("user", userName);

                startActivity(intent);
            }
        });

        auth = FirebaseAuth.getInstance();

        userArrayList = new ArrayList<>();
        attachUserDatabaseReferenceListener();
        buildRecyclerView();

    }

    private void attachUserDatabaseReferenceListener() {
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        if (usersChildEventListener == null) {
            usersChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    User user = snapshot.getValue(User.class);

                    if (user.getId() != null && !user.getId().equals(auth.getCurrentUser().getUid()) && user.isOnline()) {
                        user.setAvatarMockUpResource(R.drawable.ic_baseline_person_24);
                        userArrayList.add(user);

                        userAdapter.notifyDataSetChanged();
                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    User user = snapshot.getValue(User.class);

                    if (user.isOnline()) {
                        user.setAvatarMockUpResource(R.drawable.ic_baseline_person_24);
                        userArrayList.add(user);

                        userAdapter.notifyDataSetChanged();
                    } else if(!user.isOnline()) {
                        User offlineUser = null;
                        for (User user1 : userArrayList) {
                            if(user1.getId() == user.getId()) {
                                offlineUser = user1;
                                break;
                            }
                        }
                        userArrayList.remove(offlineUser);
                        userAdapter.notifyDataSetChanged();
                    }
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
        }
    }

    private void buildRecyclerView() {

        userRecyclerView = findViewById(R.id.userListRecyclerView);
        userRecyclerView.setHasFixedSize(true);
        userRecyclerView.addItemDecoration(new DividerItemDecoration(userRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        layoutManager = new LinearLayoutManager(this);
        userAdapter = new UserAdapter(userArrayList);

        userRecyclerView.setLayoutManager(layoutManager);
        userRecyclerView.setAdapter(userAdapter);

        userAdapter.setOnUserClickListener(new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(int position) {
                goToChat(position);
            }
        });
    }

    private void goToChat(int position) {
        Intent intent = new Intent(UserListActivity.this, ChatActivity.class);
        intent.putExtra("recipentUserId", userArrayList.get(position).getId());
        intent.putExtra("recipentUserName", userArrayList.get(position).getName());
        intent.putExtra("recipientUserGender", userArrayList.get(position).getGender());
        intent.putExtra("recipientUserAge", userArrayList.get(position).getAge());
        intent.putExtra("userName", userName);

        startActivity(intent);
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
                FirebaseUser currentUser = auth.getCurrentUser();
                usersDatabaseReference.child(currentUser.getUid()).child("online").setValue(false);
//                attachUserDatabaseReferenceListener();
                FirebaseAuth.getInstance().signOut();
//                FirebaseUser currentUser = auth.getCurrentUser();
//                usersDatabaseReference = FirebaseDatabase.getInstance().getReference();
//                usersDatabaseReference.child("users").child(currentUser.getUid()).child("online").setValue(false);
//                startActivity(new Intent(UserListActivity.this, SignUpSignInActivity.class));
                Intent i = new Intent(UserListActivity.this, SignUpSignInActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(UserListActivity.this)
                .setTitle("Exiting the App")
                .setMessage("Are you sure?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // The user wants to leave - so dismiss the dialog and exit
                        FirebaseUser currentUser = auth.getCurrentUser();
                        usersDatabaseReference.child(currentUser.getUid()).child("online").setValue(false);
                        for (User user : userArrayList) {
                            if(user.getId() == currentUser.getUid()) {
                                userArrayList.remove(user);
                                userAdapter.notifyDataSetChanged();
                            }
                        }
                        finishAffinity();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // The user is not sure, so you can exit or just stay
                        dialog.dismiss();
                    }
                }).show();
    }
}