package com.example.simplechatapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SignUpFragment extends Fragment {
    private TextView signInTextView;
    private EditText emailEditText;
    private EditText passwordEditText;
//    private EditText repeatPasswordEditText;
    private EditText editTextSignUpAge;
    private Button signUpButton;
    private SignUpFragmentListener signUpFragmentListener;
    private Spinner genderSpinner;
//    private ArrayAdapter adapter;
    private FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference usersDatabaseReference;
    private String username;
    private String userAge;
    private ProgressBar progressBar;

    public SignUpFragment() {
    }

    public SignUpFragment(SignUpFragmentListener signUpFragmentListener) {
        this.signUpFragmentListener = signUpFragmentListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sign_up, container, false);

        progressBar = v.findViewById(R.id.signUpProgressBar);
        progressBar.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersDatabaseReference = database.getReference().child("users");

        emailEditText = v.findViewById(R.id.editTextSignUpEmail);
        passwordEditText = v.findViewById(R.id.editTextSignUpPassword);
//        repeatPasswordEditText = v.findViewById(R.id.editTextPasswordRepeat);
        editTextSignUpAge = v.findViewById(R.id.editTextSignUpAge);
        signUpButton = v.findViewById(R.id.signUpButton);
        genderSpinner = v.findViewById(R.id.genderSpinner);

        List<String> genders = new ArrayList<>();
        genders.add("Male");
        genders.add("Female");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.gender_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                username = emailEditText.getText().toString().trim();

                userAge = editTextSignUpAge.getText().toString().trim();
                String username = emailEditText.getText().toString().trim();
                String email = username + "@gmail.com";
                signUpUser(email,
                        passwordEditText.getText().toString());

                SharedPreferences sharedPreferences = getActivity().
                        getSharedPreferences("myUserNameFile", Context.MODE_PRIVATE);
                sharedPreferences.edit().putString("username", username).apply();
            }
        });

        signInTextView = v.findViewById(R.id.sign_up_text_view);
        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignInFragment signInFragment = new SignInFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.signin_signup_container, signInFragment).commit();
            }
        });



        return v;
    }

    private void signUpUser(String email, String password) {
        if (passwordEditText.getText().toString().trim().length() < 6) {
            Toast.makeText(getActivity(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
        } else if (passwordEditText.getText().toString().trim().equals("")) {
            Toast.makeText(getActivity(), "Please enter your password", Toast.LENGTH_SHORT).show();
        } else if(emailEditText.getText().toString().trim().equals("")
        || editTextSignUpAge.getText().toString().trim().equals("")) {
            Toast.makeText(getActivity(), "Please fill empty forms", Toast.LENGTH_SHORT).show();
        } else {
            signUpFragmentListener.onSignUp(auth, email, password, username, genderSpinner, userAge, usersDatabaseReference, progressBar);
        }
    }

    public interface SignUpFragmentListener {
        void onSignUp(FirebaseAuth auth, String email, String password, String username, Spinner genderSpinner, String userAge, DatabaseReference usersDatabaseReference, ProgressBar progressBar);
    }
}
