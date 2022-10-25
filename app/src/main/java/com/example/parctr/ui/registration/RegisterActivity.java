package com.example.parctr.ui.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parctr.R;
import com.example.parctr.model.User;
import com.example.parctr.domain.BasicUtils;
import com.example.parctr.ui.home.HomeActivity;
import com.example.parctr.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


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
            //TODO: Go to home page
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
                Intent intent=new Intent(RegisterActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "No Network Available!", Toast.LENGTH_SHORT).show();
            }
        });
        loginSwitchText.setOnClickListener(view -> {
            String passEmail = email.getText().toString();

            Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
            if(!passEmail.isEmpty()){
                intent.putExtra("EMAIL",passEmail);
            }
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);  //slide from left to right
        });
    }

    public void signUpUser(String txt_email, String txt_password, String txt_name, String txt_contact_no) {
        mAuth.createUserWithEmailAndPassword(txt_email, txt_password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");

                        FirebaseUser mAuthCurrentUser = mAuth.getCurrentUser();
                        User user = new User();
                        user.setEmail(txt_email);
                        user.setName(txt_name);
                        user.setPhone(txt_contact_no);

                        assert mAuthCurrentUser != null;

                        addUserToDB(user, mAuthCurrentUser.getUid());
                        //TODO: Go to home page

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


}