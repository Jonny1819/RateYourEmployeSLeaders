package com.example.renner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    private EditText passwordEditText;
    private Button loginButton;
    private Button languageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        passwordEditText = findViewById(R.id.et_password);
        loginButton = findViewById(R.id.btn_login);
        languageButton = findViewById(R.id.btn_language);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordEditText.getText().toString();
                if ("1".equals(password)) {
                    navigateToMainActivity("admin");
                } else if ("2".equals(password)) {
                    navigateToMainActivity("user");
                } else {
                    Toast.makeText(LoginActivity.this, R.string.incorrect_password, Toast.LENGTH_SHORT).show();
                }
            }
        });

        languageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, LanguageSelectionActivity.class);
                startActivity(intent);
            }
        });
    }

    private void navigateToMainActivity(String userType) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("user_type", userType);
        startActivity(intent);
        finish();
    }
}
