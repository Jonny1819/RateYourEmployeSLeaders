package com.example.renner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class LanguageSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        Button englishButton = findViewById(R.id.btn_english);
        Button hungarianButton = findViewById(R.id.btn_hungarian);
        Button germanButton = findViewById(R.id.btn_german);
        Button backButton = findViewById(R.id.btn_back);

        englishButton.setOnClickListener(v -> setLocale("en"));
        hungarianButton.setOnClickListener(v -> setLocale("hu"));
        germanButton.setOnClickListener(v -> setLocale("de"));
        backButton.setOnClickListener(v -> finish());
    }

    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());


        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("selected_language", langCode);
        editor.apply();


        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
