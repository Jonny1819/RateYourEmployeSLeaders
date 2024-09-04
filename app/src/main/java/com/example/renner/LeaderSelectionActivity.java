package com.example.renner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class LeaderSelectionActivity extends AppCompatActivity {
    private LinearLayout leaderLayout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_selection);

        leaderLayout = findViewById(R.id.leader_layout);
        sharedPreferences = getSharedPreferences("LeaderAssessment", Context.MODE_PRIVATE);

        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadAndDisplayLeaders();
    }

    private void loadAndDisplayLeaders() {
        Set<String> leaderSet = sharedPreferences.getStringSet("employees", new HashSet<>());
        ArrayList<String> leaders = new ArrayList<>(leaderSet);

        leaderLayout.removeAllViews();
        for (final String leader : leaders) {
            Button leaderButton = new Button(this);
            leaderButton.setText(leader);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(600, 120); // width: 330, height: 120
            leaderButton.setLayoutParams(params);
            params.gravity = Gravity.CENTER;
            leaderButton.setGravity(Gravity.CENTER_HORIZONTAL);
            leaderButton.setBackgroundResource(R.drawable.button3);
            leaderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LeaderSelectionActivity.this, RateLeaderActivity.class);
                    intent.putExtra("leader_name", leader);
                    startActivity(intent);
                }
            });
            leaderLayout.addView(leaderButton);
        }
    }
}
