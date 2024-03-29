package com.anorlddroid.parctr.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.anorlddroid.parctr.R;
import com.anorlddroid.parctr.domain.BasicUtils;
import com.anorlddroid.parctr.model.TrackingItems;
import com.anorlddroid.parctr.model.User;
import com.anorlddroid.parctr.ui.home.client.items.ItemsActivity;
import com.anorlddroid.parctr.ui.home.driver.HomeActivity;
import com.anorlddroid.parctr.ui.registration.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

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
    private boolean isClient;

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
            getUserCredentials(currentUser.getUid());
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
                        getUserCredentials(Objects.requireNonNull(task.getResult().getUser()).getUid());
                        Toast.makeText(LoginActivity.this, "Log in success.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
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
    private  void getUserCredentials(String userID){
        mDatabase.collection("users").document(userID).collection("userDetails").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()){
                                    User user = document.toObject(User.class);
                                    Intent intent;
                                    if (user.getType().equals("Client")){
                                        intent = new Intent(LoginActivity.this, ItemsActivity.class);
                                    }else{
                                        intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    }
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(LoginActivity.this, "Log in success.", Toast.LENGTH_SHORT).show();
                                }
                            }
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