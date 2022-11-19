package com.anorlddroid.parctr.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anorlddroid.parctr.R;
import com.anorlddroid.parctr.domain.BasicUtils;
import com.anorlddroid.parctr.ui.home.HomeActivity;
import com.anorlddroid.parctr.ui.registration.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "LOGIN/ACT";
    ProgressDialog progressDialog;
    BasicUtils utils = new BasicUtils();
    private EditText email;
    private EditText password;
    private Button loginBtn;
    private TextView registerSwitchText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.emailField);
        password = findViewById(R.id.passwordField);
        loginBtn = findViewById(R.id.loginBtn);
        registerSwitchText = findViewById(R.id.registerSwitchText);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();
        signIn();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(LoginActivity.this, "Log in success.", Toast.LENGTH_SHORT).show();
        }
    }

    private void signIn() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                if (TextUtils.isEmpty(txt_email)) {
                    Toast.makeText(LoginActivity.this, "Email can't be blank!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(txt_password)) {
                    Toast.makeText(LoginActivity.this, "Password can't be blank!", Toast.LENGTH_SHORT).show();
                } else if (utils.isNetworkAvailable(getApplication())) {
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Signing-in...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    verifyCredentials(txt_email, txt_password);
                } else {
                    Toast.makeText(LoginActivity.this, "No Network Available!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerSwitchText.setOnClickListener(view -> {
            String passEmail = email.getText().toString();
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            if (!passEmail.isEmpty()) {
                intent.putExtra("EMAIL", passEmail);
            }
            startActivity(intent);
            finish();
        });

    }

    private void verifyCredentials(String txt_email, String txt_password) {
        mAuth.signInWithEmailAndPassword(txt_email, txt_password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(LoginActivity.this, "Log in success.", Toast.LENGTH_SHORT).show();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        if (progressDialog != null) {
                            progressDialog.dismiss();

                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}