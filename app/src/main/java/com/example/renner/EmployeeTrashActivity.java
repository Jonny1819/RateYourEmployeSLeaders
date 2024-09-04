package com.example.renner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class EmployeeTrashActivity extends AppCompatActivity {
    private LinearLayout trashLayout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_trash);

        trashLayout = findViewById(R.id.trash_layout);
        sharedPreferences = getSharedPreferences("LeaderAssessment", Context.MODE_PRIVATE);

        loadAndDisplayTrash();

        Button emptyTrashButton = findViewById(R.id.btn_empty_trash);
        emptyTrashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyTrash();
            }
        });
        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadAndDisplayTrash() {
        Set<String> trashSet = sharedPreferences.getStringSet("employee_trash", new HashSet<>());
        ArrayList<String> trashItems = new ArrayList<>(trashSet);

        trashLayout.removeAllViews();
        for (final String employee : trashItems) {
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
                    confirmAction(employee);
                }
            });
            trashLayout.addView(employeeButton);
        }
    }

    private void confirmAction(final String employee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.restore_or_delete_employee)
                .setMessage(String.format(getString(R.string.restore_delete_employee_question), employee))
                .setPositiveButton(R.string.restore, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restoreFromTrash(employee);
                        loadAndDisplayTrash();
                    }
                })
                .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePermanently(employee);
                        loadAndDisplayTrash();
                    }
                })
                .setNeutralButton(R.string.cancel, null)
                .show();
    }

    private void restoreFromTrash(String employee) {
        moveItem(employee, "employee_trash", "employees");
        Toast.makeText(this, employee + R.string.restored, Toast.LENGTH_SHORT).show();
    }

    private void deletePermanently(String employee) {
        removeItemFromTrash(employee, "employee_trash");
        Toast.makeText(this, employee + " " + getString(R.string.deleted_permanently), Toast.LENGTH_SHORT).show();
    }

    private void moveItem(String item, String fromSetName, String toSetName) {
        Set<String> fromSet = sharedPreferences.getStringSet(fromSetName, new HashSet<>());
        Set<String> toSet = sharedPreferences.getStringSet(toSetName, new HashSet<>());

        fromSet.remove(item);
        toSet.add(item);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(fromSetName, fromSet);
        editor.putStringSet(toSetName, toSet);
        editor.commit();
    }

    private void removeItemFromTrash(String item, String trashSetName) {
        Set<String> trashSet = sharedPreferences.getStringSet(trashSetName, new HashSet<>());
        trashSet.remove(item);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(trashSetName, trashSet);
        editor.commit();
    }

    private void emptyTrash() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("employee_trash", new HashSet<String>());
        editor.commit();
        loadAndDisplayTrash();
        Toast.makeText(this, getString(R.string.employee_trash_emptied), Toast.LENGTH_SHORT).show();
    }
}
