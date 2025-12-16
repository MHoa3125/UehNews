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

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFavorites;
    private ArticleAdapter articleAdapter;
    private DatabaseHelper dbHelper;
    private TextView textViewEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_activity_favorites);

        dbHelper = new DatabaseHelper(this);

        recyclerViewFavorites = findViewById(R.id.recycler_view_favorites);
        textViewEmpty = findViewById(R.id.text_view_empty);
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));

        setupAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void setupAdapter() {
        articleAdapter = new ArticleAdapter(new ArrayList<>());
        articleAdapter.setOnItemClickListener(article -> {
            Intent intent = new Intent(this, ArticleDetailActivity.class);
            intent.putExtra(ArticleDetailActivity.EXTRA_ARTICLE, article);
            startActivity(intent);
        });
        recyclerViewFavorites.setAdapter(articleAdapter);
    }

    private void loadFavorites() {
        List<Article> favoriteArticles = dbHelper.getAllFavorites();
        articleAdapter.setArticles(favoriteArticles);

        if (favoriteArticles.isEmpty()) {
            recyclerViewFavorites.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerViewFavorites.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        }
    }
}
