package hoatran.st.ueh.edu.uehnews.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.data.local.DatabaseHelper;
import hoatran.st.ueh.edu.uehnews.data.model.Article;
import hoatran.st.ueh.edu.uehnews.ui.adapter.ArticleAdapter;
// DỌN DẸP: Xóa import trỏ tới package "guest"

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewHistory;
    private ArticleAdapter articleAdapter;
    private DatabaseHelper dbHelper;
    private TextView textViewEmptyHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_activity_history);

        dbHelper = new DatabaseHelper(this);

        recyclerViewHistory = findViewById(R.id.recycler_view_history);
        textViewEmptyHistory = findViewById(R.id.text_view_empty_history);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));

        setupAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory();
    }

    private void setupAdapter() {
        articleAdapter = new ArticleAdapter(new ArrayList<>());
        articleAdapter.setOnItemClickListener(article -> {
            Intent intent = new Intent(this, ArticleDetailActivity.class);
            intent.putExtra(ArticleDetailActivity.EXTRA_ARTICLE, article);
            startActivity(intent);
        });
        recyclerViewHistory.setAdapter(articleAdapter);
    }

    private void loadHistory() {
        List<Article> historyArticles = dbHelper.getAllHistory();
        articleAdapter.setArticles(historyArticles);

        if (historyArticles.isEmpty()) {
            recyclerViewHistory.setVisibility(View.GONE);
            textViewEmptyHistory.setVisibility(View.VISIBLE);
        } else {
            recyclerViewHistory.setVisibility(View.VISIBLE);
            textViewEmptyHistory.setVisibility(View.GONE);
        }
    }
}
