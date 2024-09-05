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

public class AddCriteriaActivity extends AppCompatActivity {
    private LinearLayout criteriaLayout;
    private SharedPreferences sharedPreferences;
    private EditText criteriaInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_criteria);

        criteriaLayout = findViewById(R.id.criteria_layout);
        sharedPreferences = getSharedPreferences("LeaderAssessment", Context.MODE_PRIVATE);
        criteriaInput = findViewById(R.id.input_criterion_name);
        TextView infoText = findViewById(R.id.info_text);
        infoText.setText(R.string.tap_to_delete_criterion);
        Button addButton = findViewById(R.id.btn_add_criteria);
        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadAndDisplayCriteria();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String criterion = criteriaInput.getText().toString().trim();
                if (!criterion.isEmpty()) {
                    addCriterion(criterion);
                    criteriaInput.setText("");
                } else {
                    Toast.makeText(AddCriteriaActivity.this, getString(R.string.please_enter_criterion), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void addCriterion(String criterion) {
        Set<String> criteriaSet = sharedPreferences.getStringSet("criteria", new HashSet<>());
        if (criteriaSet.add(criterion)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet("criteria", criteriaSet);
            editor.apply();
            loadAndDisplayCriteria();
            Toast.makeText(this, criterion + getString(R.string.added), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, criterion + getString(R.string.already_exist), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAndDisplayCriteria() {
        Set<String> criteriaSet = sharedPreferences.getStringSet("criteria", new HashSet<>());
        ArrayList<String> criteria = new ArrayList<>(criteriaSet);

        criteriaLayout.removeAllViews();
        for (final String criterion : criteria) {
            Button criterionButton = new Button(this);
            criterionButton.setText(criterion);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(600, 120);
            criterionButton.setLayoutParams(params);
            params.gravity = Gravity.CENTER;
            criterionButton.setGravity(Gravity.CENTER_HORIZONTAL);
            criterionButton.setBackgroundResource(R.drawable.button3);
            criterionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDeletion(criterion);
                }
            });
            criteriaLayout.addView(criterionButton);
        }
    }

    private void confirmDeletion(final String criterion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_criterion)
                .setMessage(String.format(getString(R.string.confirm_delete),criterion))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveToTrash(criterion, "criteria", "criteria_trash");
                        Toast.makeText(AddCriteriaActivity.this, criterion + getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                        loadAndDisplayCriteria();
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
