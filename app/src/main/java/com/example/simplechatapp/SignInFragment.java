package com.example.simplechatapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInFragment extends Fragment {
    private static final String TAG = "SignInFragment";
    private FirebaseAuth auth;
    private EditText usernameEditText;
    private EditText passwordEditText;
//    private EditText nameEditText;
    private Button signInButton;
    private TextView signUpTextView;
    private SignInFragmentListener signInFragmentListener;
    private SignUpFragment.SignUpFragmentListener signUpFragmentListener;
    private String username;
    private String email;
    private ProgressBar progressBar;

    FirebaseDatabase database;
    DatabaseReference usersDatabaseReference;

    public SignInFragment() {
    }

    public SignInFragment(SignInFragmentListener signInFragmentListener, SignUpFragment.SignUpFragmentListener signUpFragmentListener) {
        this.signInFragmentListener = signInFragmentListener;
        this.signUpFragmentListener = signUpFragmentListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sign_in, container, false);

        progressBar = v.findViewById(R.id.signInProgressBar);
        progressBar.setVisibility(View.GONE);
        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        usersDatabaseReference = database.getReference().child("users");

        usernameEditText = v.findViewById(R.id.editTextSignInUsername);
        passwordEditText = v.findViewById(R.id.editTextSignInPassword);
//        usernameEditText = v.findViewById(R.id.editTextSignInUsername);
        signUpTextView = v.findViewById(R.id.sign_up_text_view);
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpFragment signUpFragment = new SignUpFragment(signUpFragmentListener);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.signin_signup_container, signUpFragment).commit();
            }
        });

        if (auth.getCurrentUser() != null) {
            signInFragmentListener.onSignIn();
        }

//        signIn(emailEditText.getText().toString().trim(), passwordEditText.getText().toString());

        signInButton = v.findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                username = usernameEditText.getText().toString().trim();
                SharedPreferences sharedPreferences = getActivity().
                        getSharedPreferences("myUserNameFile", Context.MODE_PRIVATE);
                sharedPreferences.edit().putString("username", username).apply();
                email = username + "@gmail.com";
                signIn(email, passwordEditText.getText().toString());
            }
        });
        return v;
    }

    private void signIn(String email, String password) {
        if (passwordEditText.getText().toString().trim().length() < 6) {
            Toast.makeText(getActivity(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
        } else if (passwordEditText.getText().toString().trim().equals("")) {
            Toast.makeText(getActivity(), "Please enter your password", Toast.LENGTH_SHORT).show();
        } else if (usernameEditText.getText().toString().trim().equals("")) {
            Toast.makeText(getActivity(), "Please enter your username", Toast.LENGTH_SHORT).show();
        } else {
            signInFragmentListener.signInWithEmail(auth, email, password, username, progressBar);
        }
    }

    public interface SignInFragmentListener {
        void onSignIn();
        void signInWithEmail(FirebaseAuth auth, String email, String password, String username, ProgressBar progressBar);
    }
}
