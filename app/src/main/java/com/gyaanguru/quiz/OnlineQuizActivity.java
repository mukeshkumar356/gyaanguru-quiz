package com.gyaanguru.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.gyaanguru.quiz.data.QuestionBank;
import com.gyaanguru.quiz.model.Question;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OnlineQuizActivity extends AppCompatActivity {

    // Open Trivia DB category IDs
    private static final int[] CAT_IDS  = {9,  17, 23, 21, 22, 18, 11, 20, 15};
    private static final String[] CATS  = {
        "General Knowledge", "Science & Nature", "History",
        "Sports", "Geography", "Computers", "Entertainment: Film",
        "Mythology", "Video Games"
    };
    private static final String[] DIFFS = {"easy", "medium", "hard"};

    private Spinner spinnerCat, spinnerDiff;
    private Button btnStart;
    private ProgressBar loader;
    private TextView tvStatus;

    // Static holder so QuizActivity can read them
    public static List<Question> pendingOnlineQuestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_quiz);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Online Quiz");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        spinnerCat  = findViewById(R.id.spinner_category);
        spinnerDiff = findViewById(R.id.spinner_difficulty);
        btnStart    = findViewById(R.id.btn_start);
        loader      = findViewById(R.id.loader);
        tvStatus    = findViewById(R.id.tv_status);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, CATS);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCat.setAdapter(catAdapter);

        ArrayAdapter<String> diffAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, DIFFS);
        diffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDiff.setAdapter(diffAdapter);

        btnStart.setOnClickListener(v -> fetchAndStart());
    }

    private void fetchAndStart() {
        int catPos  = spinnerCat.getSelectedItemPosition();
        int diffPos = spinnerDiff.getSelectedItemPosition();
        int catId   = CAT_IDS[catPos];
        String diff = DIFFS[diffPos];
        String catName = CATS[catPos];

        btnStart.setEnabled(false);
        loader.setVisibility(View.VISIBLE);
        tvStatus.setText("Questions fetch ho rahi hain...");

        String apiUrl = "https://opentdb.com/api.php?amount=15&category="
                + catId + "&difficulty=" + diff + "&type=multiple";

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<Question> fetched = fetchQuestions(apiUrl, catName);
            handler.post(() -> {
                loader.setVisibility(View.GONE);
                btnStart.setEnabled(true);

                if (fetched == null || fetched.isEmpty()) {
                    tvStatus.setText("❌ Internet nahi hai ya koi error hui. Offline quiz khelein.");
                    return;
                }

                pendingOnlineQuestions = fetched;
                Intent intent = new Intent(this, QuizActivity.class);
                intent.putExtra("category", "ONLINE:" + catName);
                startActivity(intent);
            });
        });
    }

    private List<Question> fetchQuestions(String apiUrl, String catName) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();

            JSONObject root = new JSONObject(sb.toString());
            if (root.getInt("response_code") != 0) return null;

            JSONArray results = root.getJSONArray("results");
            List<Question> list = new ArrayList<>();

            for (int i = 0; i < results.length(); i++) {
                JSONObject item = results.getJSONObject(i);
                String qText = decode(item.getString("question"));
                String correct = decode(item.getString("correct_answer"));

                JSONArray wrongArr = item.getJSONArray("incorrect_answers");
                List<String> opts = new ArrayList<>();
                opts.add(correct);
                for (int j = 0; j < wrongArr.length(); j++)
                    opts.add(decode(wrongArr.getString(j)));
                Collections.shuffle(opts);

                int correctIdx = opts.indexOf(correct);
                list.add(new Question(qText, opts.toArray(new String[0]), correctIdx, catName));
            }
            return list;

        } catch (Exception e) {
            return null;
        }
    }

    private String decode(String html) {
        return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
