package com.grintaux.example.id;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ImageView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.EmailAuthProvider;
import com.huawei.agconnect.auth.EmailUser;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.agconnect.auth.VerifyCodeResult;
import com.huawei.agconnect.auth.VerifyCodeSettings;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hmf.tasks.TaskExecutors;

import java.util.Locale;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView backButton;

    private EditText emailEditText, passwordEditText, verifyCodeEditText;
    private Button verifyEmailButton, registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initClicks();

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });






        verifyCodeEditText.addTextChangedListener(emailVerTextWatcher);

    }

    private TextWatcher emailVerTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String verificationStr = verifyCodeEditText.getText().toString().trim();
            if(verificationStr.isEmpty())
            {
                //EditText is empty
                // Verify Email Button Visible
                verifyEmailButton.setVisibility(View.VISIBLE);
                registerButton.setVisibility(View.GONE);
            }
            else
            {
                //EditText is not empty
                // Verify Email Button Gone
                verifyEmailButton.setVisibility(View.GONE);
                registerButton.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void initClicks() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        verifyCodeEditText = findViewById(R.id.verifyCodeEditText);
        verifyEmailButton = findViewById(R.id.verifyEmailButton);
        registerButton = findViewById(R.id.registerButton);

        verifyEmailButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }


    private void verifyEmail() {
        VerifyCodeSettings verifyCodeSettings = VerifyCodeSettings.newBuilder()
                .action(VerifyCodeSettings.ACTION_REGISTER_LOGIN)
                .sendInterval(30)
                .locale(Locale.getDefault())
                .build();

        Task<VerifyCodeResult> task = EmailAuthProvider.requestVerifyCode(emailEditText.getText().toString(), verifyCodeSettings);
        task.addOnSuccessListener(TaskExecutors.uiThread(), new OnSuccessListener<VerifyCodeResult>() {
            @Override
            public void onSuccess(VerifyCodeResult verifyCodeResult) {
                Toast.makeText(getApplicationContext(), "Please check your e-mail", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(TaskExecutors.uiThread(), new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerWithEmail() {

        EmailUser emailUser = new EmailUser.Builder()
                .setEmail(emailEditText.getText().toString())
                .setPassword(passwordEditText.getText().toString())
                .setVerifyCode(verifyCodeEditText.getText().toString())
                .build();

        AGConnectAuth.getInstance().createUser(emailUser)
                .addOnSuccessListener(new OnSuccessListener<SignInResult>() {
                    @Override
                    public void onSuccess(SignInResult signInResult) {
                        Toast.makeText(getApplicationContext(), "User successfully created.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.verifyEmailButton:
                verifyEmail();
                break;
            case R.id.registerButton:
                registerWithEmail();
                break;
        }
    }
}