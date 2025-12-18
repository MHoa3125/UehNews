package hoatran.st.ueh.edu.uehnews.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.data.model.Article;
import hoatran.st.ueh.edu.uehnews.data.repositories.ArticleRepository;
import hoatran.st.ueh.edu.uehnews.ui.adapter.ArticleAdapter;
// DỌN DẸP: Xóa import trỏ tới package "guest"
// import hoatran.st.ueh.edu.uehnews.ui.guest.ArticleDetailActivity;

public class ArticleSearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recyclerViewSearchResults;
    private ArticleAdapter articleAdapter;
    private ArticleRepository articleRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_activity_article_search);

        articleRepository = new ArticleRepository();

        searchView = findViewById(R.id.search_view_articles);
        recyclerViewSearchResults = findViewById(R.id.recycler_view_search_results);
        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(this));

        setupAdapter();
        setupSearchView();
    }

    private void setupAdapter() {
        articleAdapter = new ArticleAdapter(new ArrayList<>());
        articleAdapter.setOnItemClickListener(article -> {
            // Sắp tới, ArticleDetailActivity sẽ nằm cùng package này, nên không cần import
            Intent intent = new Intent(this, ArticleDetailActivity.class);
            intent.putExtra(ArticleDetailActivity.EXTRA_ARTICLE, article);
            startActivity(intent);
        });
        recyclerViewSearchResults.setAdapter(articleAdapter);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchArticles(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Live search can be implemented here if desired
                return false;
            }
        });
    }

    private void searchArticles(String keyword) {
        articleRepository.searchArticles(keyword, articles -> {
            runOnUiThread(() -> {
                articleAdapter.setArticles(articles);
            });
        });
    }
}
