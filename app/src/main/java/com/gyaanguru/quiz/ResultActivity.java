package com.gyaanguru.quiz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        int score = getIntent().getIntExtra("score", 0);
        int total = getIntent().getIntExtra("total", 1);
        String category = getIntent().getStringExtra("category");

        Toolbar toolbar = findViewById(R.id.toolbar);

        TextView tvGrade = findViewById(R.id.tv_grade);
        TextView tvScore = findViewById(R.id.tv_score_big);
        TextView tvCorrect = findViewById(R.id.tv_correct);
        TextView tvWrong = findViewById(R.id.tv_wrong);
        TextView tvTotal = findViewById(R.id.tv_total);
        ProgressBar circleBar = findViewById(R.id.circle_progress);
        LinearLayout reviewContainer = findViewById(R.id.review_container);
        Button btnHome = findViewById(R.id.btn_home);
        Button btnRetry = findViewById(R.id.btn_retry);

        int pct = (score * 100) / total;
        String grade;
        int color;
        if (pct >= 90) { grade = "शानदार!"; color = Color.parseColor("#4CAF50"); }
        else if (pct >= 70) { grade = "बहुत अच्छा!"; color = Color.parseColor("#1E88E5"); }
        else if (pct >= 50) { grade = "अच्छा!"; color = Color.parseColor("#FF9800"); }
        else { grade = "और पढ़ें!"; color = Color.parseColor("#F44336"); }

        tvGrade.setText(grade);
        tvGrade.setTextColor(color);
        tvScore.setText(pct + "%");
        tvScore.setTextColor(color);
        tvCorrect.setText(String.valueOf(score));
        tvWrong.setText(String.valueOf(total - score));
        tvTotal.setText(String.valueOf(total));
        circleBar.setMax(total);
        circleBar.setProgress(score);

        ArrayList<Integer> userAnswers = getIntent().getIntegerArrayListExtra("userAnswers");
        ArrayList<Integer> correctIdx = getIntent().getIntegerArrayListExtra("correctIdx");
        ArrayList<String> qTexts = getIntent().getStringArrayListExtra("qTexts");
        ArrayList<String> optA = getIntent().getStringArrayListExtra("optA");
        ArrayList<String> optB = getIntent().getStringArrayListExtra("optB");
        ArrayList<String> optC = getIntent().getStringArrayListExtra("optC");
        ArrayList<String> optD = getIntent().getStringArrayListExtra("optD");

        if (qTexts != null) {
            String[][] opts = {
                optA.toArray(new String[0]),
                optB.toArray(new String[0]),
                optC.toArray(new String[0]),
                optD.toArray(new String[0])
            };
            for (int i = 0; i < qTexts.size(); i++) {
                int ua = (userAnswers != null && i < userAnswers.size()) ? userAnswers.get(i) : -1;
                int ci = correctIdx.get(i);
                boolean correct = ua == ci;

                TextView tv = new TextView(this);
                String[] row = {opts[0][i], opts[1][i], opts[2][i], opts[3][i]};
                StringBuilder sb = new StringBuilder();
                sb.append((correct ? "✅ " : "❌ "))
                  .append("प्र.").append(i + 1).append(": ").append(qTexts.get(i)).append("\n")
                  .append("✔ सही: ").append(row[ci]).append("\n");
                if (!correct && ua >= 0) sb.append("✘ आपका: ").append(row[ua]);
                else if (ua < 0) sb.append("⏰ समय समाप्त");
                tv.setText(sb.toString());
                tv.setTextSize(13f);
                tv.setPadding(24, 20, 24, 20);
                tv.setTextColor(Color.parseColor("#212121"));
                tv.setBackgroundColor(correct ? Color.parseColor("#E8F5E9") : Color.parseColor("#FFEBEE"));

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 0, 12);
                tv.setLayoutParams(lp);
                reviewContainer.addView(tv);
            }
        }

        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });

        btnRetry.setOnClickListener(v -> {
            startActivity(new Intent(this, QuizActivity.class)
                .putExtra("category", category));
            finish();
        });
    }
}
