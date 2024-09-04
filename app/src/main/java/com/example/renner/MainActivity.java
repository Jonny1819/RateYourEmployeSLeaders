package com.example.renner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView userRoleText;
    private Button addEmployeeButton;
    private Button addCriteriaButton;
    private Button rateLeaderButton;
    private Button viewResultsButton;
    private Button trashButton;
    private Button logoutButton;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userRoleText = findViewById(R.id.user_role_text);
        addEmployeeButton = findViewById(R.id.btn_add_employee);
        addCriteriaButton = findViewById(R.id.btn_add_criteria);
        rateLeaderButton = findViewById(R.id.btn_rate_leader);
        viewResultsButton = findViewById(R.id.btn_view_results);
        trashButton = findViewById(R.id.btn_trash);
        logoutButton = findViewById(R.id.btn_logout);
        Button backupButton = findViewById(R.id.btn_backup);

        SharedPreferences sharedPreferences = getSharedPreferences("LeaderAssessment", MODE_PRIVATE);
        userType = getIntent().getStringExtra("user_type");

        userRoleText.setText(userType.equals("admin") ? "Admin" : "User");



        if ("admin".equals(userType)) {
            addEmployeeButton.setVisibility(View.VISIBLE);
            addCriteriaButton.setVisibility(View.VISIBLE);
            viewResultsButton.setVisibility(View.VISIBLE);
            trashButton.setVisibility(View.VISIBLE);
            backupButton.setVisibility(View.VISIBLE);
        } else {
            addEmployeeButton.setVisibility(View.GONE);
            addCriteriaButton.setVisibility(View.GONE);
            viewResultsButton.setVisibility(View.GONE);
            trashButton.setVisibility(View.GONE);
            backupButton.setVisibility(View.GONE);
        }

        addEmployeeButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddEmployeeActivity.class)));
        addCriteriaButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddCriteriaActivity.class)));
        rateLeaderButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LeaderSelectionActivity.class)));
        viewResultsButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ResultActivity.class)));
        trashButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TrashSelectionActivity.class)));

        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("user_role");
            editor.apply();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        backupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BackupActivity.class));
            }
        });
    }
}
