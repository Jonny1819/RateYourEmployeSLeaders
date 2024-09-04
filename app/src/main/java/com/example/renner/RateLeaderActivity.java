package com.example.renner;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RateLeaderActivity extends AppCompatActivity {
    private LinearLayout criteriaLayout;
    private SharedPreferences sharedPreferences;
    private String leaderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_leader);

        criteriaLayout = findViewById(R.id.criteria_layout);
        sharedPreferences = getSharedPreferences("LeaderAssessment", Context.MODE_PRIVATE);

        leaderName = getIntent().getStringExtra("leader_name");
        TextView leaderNameText = findViewById(R.id.leader_name_text);
        leaderNameText.setText("Rating: " + leaderName);

        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadAndDisplayCriteria();
    }

    private void loadAndDisplayCriteria() {
        Set<String> criteriaSet = sharedPreferences.getStringSet("criteria", new HashSet<>());
        final Map<String, Integer> ratings = new HashMap<>();

        criteriaLayout.removeAllViews();
        for (final String criterion : criteriaSet) {
            TextView criterionText = new TextView(this);
            criterionText.setText(criterion);
            criterionText.setTextColor(Color.BLACK);

            RadioGroup ratingGroup = new RadioGroup(this);
            for (int i = 1; i <= 5; i++) {
                RadioButton ratingButton = new RadioButton(this);
                ratingGroup.setOrientation(RadioGroup.HORIZONTAL);
                ratingButton.setText(String.valueOf(i));
                ratingButton.setTextColor(Color.BLACK);
                final int rating = i;
                ratingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ratings.put(criterion, rating);
                    }
                });
                ratingGroup.addView(ratingButton);
            }

            criteriaLayout.addView(criterionText);
            criteriaLayout.addView(ratingGroup);
        }

        Button submitButton = new Button(this);
        submitButton.setText("Submit Rating");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(600, 120);
        submitButton.setLayoutParams(params);
        params.gravity = Gravity.CENTER;
        submitButton.setGravity(Gravity.CENTER_HORIZONTAL);
        submitButton.setBackgroundResource(R.drawable.button3);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ratings.size() == criteriaSet.size()) {
                    saveRatings(ratings);
                    Toast.makeText(RateLeaderActivity.this, getString(R.string.ratings_submitted), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RateLeaderActivity.this, getString(R.string.please_rate_all_criteria), Toast.LENGTH_SHORT).show();
                }
            }
        });
        criteriaLayout.addView(submitButton);
    }

    private void saveRatings(Map<String, Integer> ratings) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (Map.Entry<String, Integer> entry : ratings.entrySet()) {
            String key = leaderName + "_" + entry.getKey();
            int existingTotal = sharedPreferences.getInt(key + "_total", 0);
            int existingCount = sharedPreferences.getInt(key + "_count", 0);

            int newTotal = existingTotal + entry.getValue();
            int newCount = existingCount + 1;

            editor.putInt(key + "_total", newTotal);
            editor.putInt(key + "_count", newCount);
        }

        editor.apply();
    }


}
