package com.example.simplechatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpSignInActivity extends AppCompatActivity implements SignInFragment.SignInFragmentListener, SignUpFragment.SignUpFragmentListener {
    private static final String TAG = "SignUpSignInActivity";
//    private FirebaseAuth auth;
//    FirebaseDatabase database;
//    DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_sign_in);

        SignInFragment signInFragment = new SignInFragment(this, this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.signin_signup_container, signInFragment).commit();
//        auth = FirebaseAuth.getInstance();
//        database = FirebaseDatabase.getInstance();
//        usersDatabaseReference = database.getReference();
    }

    @Override
    public void onSignIn() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid()).child("online").setValue(true);
        startActivity(new Intent(SignUpSignInActivity.this, UserListActivity.class));
    }

    @Override
    public void signInWithEmail(final FirebaseAuth auth, String email, String password, final String username, final ProgressBar progressBar) {
        final FirebaseAuth auth1 = auth;
//        final EditText nameEditText1 = nameEditText;
        auth1.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = auth1.getCurrentUser();
                            //updateUI(user);

                            progressBar.setVisibility(View.GONE);
//                            FirebaseUser firebaseUser = auth.getCurrentUser();
//                            usersDatabaseReference.child("users").child(firebaseUser.getUid()).child("online").setValue(true);
                            FirebaseUser currentUser = auth.getCurrentUser();
                            FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid()).child("online").setValue(true);
                            Intent intent = new Intent(SignUpSignInActivity.this, UserListActivity.class);
                            intent.putExtra("userName", username);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignUpSignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                            // ...
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onSignUp(FirebaseAuth auth, String email, String password, final String username, final Spinner genderSpinner, final String userAge, final DatabaseReference usersDatabaseReference, final ProgressBar progressBar) {
        final FirebaseAuth auth1 = auth;
        auth1.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = auth1.getCurrentUser();
                            createUser(user, username, genderSpinner, userAge, usersDatabaseReference);

                            //updateUI(user);
                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(SignUpSignInActivity.this, UserListActivity.class);
                            intent.putExtra("userName", username);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpSignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                        // ...
                    }
                });
    }

    private void createUser(FirebaseUser firebaseUser, String username, Spinner genderSpinner, String userAge, DatabaseReference usersDatabaseReference) {
        User user = new User();
        user.setId(firebaseUser.getUid());
        user.setEmail(firebaseUser.getEmail());
        user.setName(username);
        user.setGender(genderSpinner.getSelectedItem().toString());
        user.setAge(userAge);
        user.setOnline(true);

        usersDatabaseReference.child(user.getId()).setValue(user);

    }
}