package com.example.renner;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ResultActivity extends AppCompatActivity {
    private LinearLayout resultLayout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultLayout = findViewById(R.id.result_layout);
        sharedPreferences = getSharedPreferences("LeaderAssessment", Context.MODE_PRIVATE);

        loadAndDisplayResults();
    }

    private void loadAndDisplayResults() {
        Set<String> employeeSet = sharedPreferences.getStringSet("employees", new HashSet<>());
        Set<String> criteriaSet = sharedPreferences.getStringSet("criteria", new HashSet<>());

        resultLayout.removeAllViews();

        for (String employee : employeeSet) {
            TextView employeeNameText = new TextView(this);
            employeeNameText.setText(employee);
            employeeNameText.setTextSize(18);
            employeeNameText.setTextColor(0xFF000000);
            employeeNameText.setTypeface(null, Typeface.BOLD);
            resultLayout.addView(employeeNameText);

            for (String criterion : criteriaSet) {
                String key = employee + "_" + criterion;
                int totalScore = sharedPreferences.getInt(key + "_total", 0);
                int count = sharedPreferences.getInt(key + "_count", 0);

                if (count > 0) {
                    double average = (double) totalScore / count;
                    TextView criterionResult = new TextView(this);
                    criterionResult.setText(criterion + ": " + String.format("%.1f", average));
                    criterionResult.setTextColor(0xFF000000);
                    resultLayout.addView(criterionResult);
                } else {
                    TextView criterionResult = new TextView(this);
                    criterionResult.setText(criterion + ": No ratings");
                    criterionResult.setTextColor(0xFF000000);
                    resultLayout.addView(criterionResult);
                }
            }


            TextView spacer = new TextView(this);
            spacer.setText("\n");
            resultLayout.addView(spacer);
        }
        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
