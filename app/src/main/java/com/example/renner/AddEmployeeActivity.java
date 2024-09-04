package com.example.renner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AddEmployeeActivity extends AppCompatActivity {
    private LinearLayout employeeLayout;
    private SharedPreferences sharedPreferences;
    private EditText employeeNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        employeeLayout = findViewById(R.id.employee_layout);
        sharedPreferences = getSharedPreferences("LeaderAssessment", Context.MODE_PRIVATE);
        employeeNameInput = findViewById(R.id.input_employee_name);
        TextView infoText = findViewById(R.id.info_text);
        infoText.setText(R.string.tap_to_delete_employee);
        Button addButton = findViewById(R.id.btn_add_employee);
        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadAndDisplayEmployees();
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String employeeName = employeeNameInput.getText().toString().trim();
                if (!employeeName.isEmpty()) {
                    addEmployee(employeeName);
                    employeeNameInput.setText("");
                } else {
                    Toast.makeText(AddEmployeeActivity.this, R.string.employee_hint, Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadAndDisplayEmployees() {
        Set<String> employeeSet = sharedPreferences.getStringSet("employees", new HashSet<>());
        ArrayList<String> employees = new ArrayList<>(employeeSet);

        employeeLayout.removeAllViews();
        for (final String employee : employees) {
            Button employeeButton = new Button(this);
            employeeButton.setText(employee);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(600, 120);
            employeeButton.setLayoutParams(params);
            params.gravity = Gravity.CENTER;
            employeeButton.setGravity(Gravity.CENTER_HORIZONTAL);
            employeeButton.setBackgroundResource(R.drawable.button3);
            employeeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDeletion(employee);
                }
            });
            employeeLayout.addView(employeeButton);
        }
    }
    private void addEmployee(String employeeName) {
        Set<String> employeeSet = sharedPreferences.getStringSet("employees", new HashSet<>());
        if (employeeSet.add(employeeName)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet("employees", employeeSet);
            editor.apply();
            loadAndDisplayEmployees();
            Toast.makeText(this, employeeName + R.string.added, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, employeeName + R.string.already_exist, Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDeletion(final String employee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_employee)
                .setMessage(String.format(getString(R.string.confirm_delete), employee))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveToTrash(employee, "employees", "employee_trash");
                        Toast.makeText(AddEmployeeActivity.this, employee + R.string.deleted, Toast.LENGTH_SHORT).show();
                        loadAndDisplayEmployees();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void moveToTrash(String item, String originalSetName, String trashSetName) {
        Set<String> originalSet = sharedPreferences.getStringSet(originalSetName, new HashSet<>());
        Set<String> trashSet = sharedPreferences.getStringSet(trashSetName, new HashSet<>());

        originalSet.remove(item);
        trashSet.add(item);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(originalSetName, originalSet);
        editor.putStringSet(trashSetName, trashSet);
        editor.commit();
    }
}
