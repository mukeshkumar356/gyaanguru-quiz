package com.gyaanguru.quiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gyaanguru.quiz.adapter.CategoryAdapter;
import com.gyaanguru.quiz.data.QuestionBank;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Hindi categories
        List<String> hindiCats = QuestionBank.getHindiCategories();
        RecyclerView rvHindi = findViewById(R.id.rv_hindi);
        rvHindi.setLayoutManager(new GridLayoutManager(this, 2));
        rvHindi.setNestedScrollingEnabled(false);
        rvHindi.setAdapter(new CategoryAdapter(hindiCats, cat -> startQuiz(cat)));

        // English categories
        List<String> engCats = QuestionBank.getEnglishCategories();
        RecyclerView rvEnglish = findViewById(R.id.rv_english);
        rvEnglish.setLayoutManager(new GridLayoutManager(this, 2));
        rvEnglish.setNestedScrollingEnabled(false);
        rvEnglish.setAdapter(new CategoryAdapter(engCats, cat -> startQuiz(cat)));

        // Online Quiz button
        findViewById(R.id.btn_online_quiz).setOnClickListener(v -> {
            Intent i = new Intent(this, OnlineQuizActivity.class);
            startActivity(i);
        });

        loadStats();
    }

    private void startQuiz(String category) {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    private void loadStats() {
        SharedPreferences prefs = getSharedPreferences("GKQuiz", MODE_PRIVATE);
        ((TextView) findViewById(R.id.tv_score)).setText(String.valueOf(prefs.getInt("totalScore", 0)));
        ((TextView) findViewById(R.id.tv_played)).setText(String.valueOf(prefs.getInt("totalPlayed", 0)));
        int totalQ = QuestionBank.getHindiCategories().size() + QuestionBank.getEnglishCategories().size();
        ((TextView) findViewById(R.id.tv_cats)).setText(String.valueOf(totalQ));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStats();
    }
}
