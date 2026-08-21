package com.gyaanguru.quiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.gyaanguru.quiz.data.QuestionBank;
import com.gyaanguru.quiz.model.Question;
import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private static final int MAX_Q = 15;
    private List<Question> questions;
    private int currentIndex = 0, score = 0;
    private boolean answered = false;
    private CountDownTimer timer;
    private final ArrayList<Integer> userAnswers = new ArrayList<>();

    private TextView tvQuestion, tvTimer, tvProgress;
    private final CardView[] optCards  = new CardView[4];
    private final TextView[] optTexts  = new TextView[4];
    private final TextView[] optBadges = new TextView[4];
    private ProgressBar progressBar, timerBar;
    private String category;

    private static final int C_WHITE   = 0xFFFFFFFF;
    private static final int C_CORRECT = 0xFF4CAF50;
    private static final int C_WRONG   = 0xFFF44336;
    private static final int C_BADGE   = 0xFF1A237E;
    private static final int C_BADGE_OK  = 0xFF388E3C;
    private static final int C_BADGE_ERR = 0xFFD32F2F;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        category = getIntent().getStringExtra("category");

        TextView tvCategoryTitle = findViewById(R.id.tv_category_title);
        tvCategoryTitle.setText(category);

        tvQuestion  = findViewById(R.id.tv_question);
        tvTimer     = findViewById(R.id.tv_timer);
        tvProgress  = findViewById(R.id.tv_progress);
        progressBar = findViewById(R.id.progress_bar);
        timerBar    = findViewById(R.id.timer_bar);

        int[] cardIds  = {R.id.card_a,  R.id.card_b,  R.id.card_c,  R.id.card_d};
        int[] textIds  = {R.id.txt_a,   R.id.txt_b,   R.id.txt_c,   R.id.txt_d};
        int[] badgeIds = {R.id.badge_a, R.id.badge_b, R.id.badge_c, R.id.badge_d};

        for (int i = 0; i < 4; i++) {
            optCards[i]  = findViewById(cardIds[i]);
            optTexts[i]  = findViewById(textIds[i]);
            optBadges[i] = findViewById(badgeIds[i]);
            final int idx = i;
            optCards[i].setOnClickListener(v -> handleAnswer(idx));
        }

        if (category != null && category.startsWith("ONLINE:")) {
            questions = new ArrayList<>(OnlineQuizActivity.pendingOnlineQuestions);
            tvCategoryTitle.setText(category.replace("ONLINE:", ""));
        } else {
            List<Question> all = QuestionBank.getByCategory(category);
            questions = new ArrayList<>(all.subList(0, Math.min(MAX_Q, all.size())));
        }
        loadQuestion();
    }

    private void loadQuestion() {
        if (currentIndex >= questions.size()) { endQuiz(); return; }
        answered = false;
        Question q = questions.get(currentIndex);

        tvQuestion.setText(q.getQuestion());
        tvProgress.setText((currentIndex + 1) + " / " + questions.size());
        progressBar.setMax(questions.size());
        progressBar.setProgress(currentIndex + 1);

        String[] opts = q.getOptions();
        for (int i = 0; i < 4; i++) {
            String text = (i < opts.length) ? opts[i].trim() : "";
            optTexts[i].setText(text);
            optCards[i].setCardBackgroundColor(C_WHITE);
            optTexts[i].setTextColor(Color.parseColor("#212121"));
            optBadges[i].setBackgroundTintList(ColorStateList.valueOf(C_BADGE));
            optBadges[i].setTextColor(Color.WHITE);
            if (text.isEmpty()) {
                optCards[i].setVisibility(View.GONE);
            } else {
                optCards[i].setVisibility(View.VISIBLE);
                optCards[i].setClickable(true);
            }
        }

        startTimer();
    }

    private void startTimer() {
        if (timer != null) timer.cancel();
        timerBar.setMax(30);
        timerBar.setProgress(30);
        timerBar.setProgressTintList(ColorStateList.valueOf(C_CORRECT));

        timer = new CountDownTimer(30000, 1000) {
            @Override public void onTick(long ms) {
                int sec = (int) (ms / 1000);
                tvTimer.setText(sec + "s");
                timerBar.setProgress(sec);
                int color;
                if (sec <= 7)       color = Color.parseColor("#F44336");
                else if (sec <= 15) color = Color.parseColor("#FF9800");
                else                color = Color.parseColor("#4CAF50");
                tvTimer.setTextColor(color);
                timerBar.setProgressTintList(ColorStateList.valueOf(color));
            }
            @Override public void onFinish() { handleAnswer(-1); }
        }.start();
    }

    private void handleAnswer(int selected) {
        if (answered) return;
        answered = true;
        if (timer != null) timer.cancel();

        for (int i = 0; i < 4; i++) optCards[i].setClickable(false);

        userAnswers.add(selected);
        int correct = questions.get(currentIndex).getCorrectIndex();

        if (selected >= 0) {
            boolean right = (selected == correct);
            optCards[selected].setCardBackgroundColor(right ? C_CORRECT : C_WRONG);
            optTexts[selected].setTextColor(Color.WHITE);
            optBadges[selected].setBackgroundTintList(
                ColorStateList.valueOf(right ? C_BADGE_OK : C_BADGE_ERR));
        }

        if (selected != correct) {
            optCards[correct].setCardBackgroundColor(C_CORRECT);
            optTexts[correct].setTextColor(Color.WHITE);
            optBadges[correct].setBackgroundTintList(ColorStateList.valueOf(C_BADGE_OK));
        }

        if (selected == correct) score++;

        tvTimer.postDelayed(() -> {
            currentIndex++;
            loadQuestion();
        }, 1200);
    }

    private void endQuiz() {
        SharedPreferences prefs = getSharedPreferences("GKQuiz", MODE_PRIVATE);
        prefs.edit()
            .putInt("totalScore",  prefs.getInt("totalScore",  0) + score)
            .putInt("totalPlayed", prefs.getInt("totalPlayed", 0) + questions.size())
            .apply();

        ArrayList<String> qTexts = new ArrayList<>();
        ArrayList<Integer> correctIdx = new ArrayList<>();
        ArrayList<String> optA = new ArrayList<>(), optB = new ArrayList<>(),
                optC = new ArrayList<>(), optD = new ArrayList<>();

        for (Question q : questions) {
            qTexts.add(q.getQuestion());
            correctIdx.add(q.getCorrectIndex());
            String[] o = q.getOptions();
            optA.add(o.length > 0 ? o[0] : "");
            optB.add(o.length > 1 ? o[1] : "");
            optC.add(o.length > 2 ? o[2] : "");
            optD.add(o.length > 3 ? o[3] : "");
        }

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("total", questions.size());
        intent.putExtra("category", category);
        intent.putIntegerArrayListExtra("userAnswers", userAnswers);
        intent.putStringArrayListExtra("qTexts", qTexts);
        intent.putIntegerArrayListExtra("correctIdx", correctIdx);
        intent.putStringArrayListExtra("optA", optA);
        intent.putStringArrayListExtra("optB", optB);
        intent.putStringArrayListExtra("optC", optC);
        intent.putStringArrayListExtra("optD", optD);
        startActivity(intent);
        finish();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}
