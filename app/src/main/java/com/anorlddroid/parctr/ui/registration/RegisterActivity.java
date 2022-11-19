package com.anorlddroid.parctr.ui.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anorlddroid.parctr.R;
import com.anorlddroid.parctr.domain.BasicUtils;
import com.anorlddroid.parctr.model.User;
import com.anorlddroid.parctr.ui.home.HomeActivity;
import com.anorlddroid.parctr.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


final
public class RegisterActivity extends AppCompatActivity {
    private final String TAG = "REG/ACT";
    private EditText email, name, contactNo, password;
    private Button registerBtn;
    private TextView loginSwitchText;
    private ProgressDialog progressDialog;
    private BasicUtils utils = new BasicUtils();
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = findViewById(R.id.nameField);
        contactNo = findViewById(R.id.contactNoField);
        email = findViewById(R.id.emailField);
        password = findViewById(R.id.passwordField);
        registerBtn = findViewById(R.id.registerBtn);
        loginSwitchText = findViewById(R.id.loginSwitchText);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();

        sanitizeInput();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }

    private void sanitizeInput() {
        registerBtn.setOnClickListener(view -> {
            String txt_email = email.getText().toString();
            String txt_password = password.getText().toString();
            String txt_name = name.getText().toString();
            String txt_contact_no = contactNo.getText().toString();
            if (!utils.isNameValid(txt_name)) {
                Toast.makeText(RegisterActivity.this, "Name is Invalid!", Toast.LENGTH_SHORT).show();
            } else if (!utils.isPhoneNoValid(txt_contact_no)) {
                Toast.makeText(RegisterActivity.this, "Phone Number is Invalid!", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(txt_email)) {
                Toast.makeText(RegisterActivity.this, "Email can't be blank!", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(txt_password)) {
                Toast.makeText(RegisterActivity.this, "Password can't be blank!", Toast.LENGTH_SHORT).show();
            } else if (txt_password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Password too short!", Toast.LENGTH_SHORT).show();
            } else if (utils.isNetworkAvailable(getApplication())) {
                progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setMessage("Signing-up...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
                signUpUser(txt_email, txt_password, txt_name, txt_contact_no);
                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "No Network Available!", Toast.LENGTH_SHORT).show();
            }
        });
        loginSwitchText.setOnClickListener(view -> {
            String passEmail = email.getText().toString();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            if (!passEmail.isEmpty()) {
                intent.putExtra("EMAIL", passEmail);
            }
            startActivity(intent);
            finish();
        });
    }

    public void signUpUser(String txt_email, String txt_password, String txt_name, String txt_contact_no) {
        mAuth.createUserWithEmailAndPassword(txt_email, txt_password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        User user = new User();
                        user.setEmail(txt_email);
                        user.setName(txt_name);
                        user.setPhone(txt_contact_no);
                        addUserToDB(user, Objects.requireNonNull(task.getResult().getUser()).getUid());
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegisterActivity.this, "Authentication failed. Check your internet connection then try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addUserToDB(User user, String userID) {
        mDatabase.collection("users").document(userID).collection("userDetails").add(user)
                .addOnSuccessListener(documentReference -> Log.d("TAG", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}