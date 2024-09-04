package com.example.renner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class TrashSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash_selection);

        Button employeeTrashButton = findViewById(R.id.employee_trash_button);
        Button criteriaTrashButton = findViewById(R.id.criteria_trash_button);
        Button backButton = findViewById(R.id.btn_back);

        employeeTrashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrashSelectionActivity.this, EmployeeTrashActivity.class);
                startActivity(intent);
            }
        });

        criteriaTrashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrashSelectionActivity.this, CriteriaTrashActivity.class);
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
