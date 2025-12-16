package hoatran.st.ueh.edu.uehnews.ui.GUEST;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.ui.adapter.ArticleAdapter;
// Import your ArticleRepository here
// import hoatran.st.ueh.edu.uehnews.data.repositories.ArticleRepository;

public class ArticleSearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recyclerViewSearchResults;
    private ArticleAdapter articleAdapter;
    // private ArticleRepository articleRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_activity_article_search);

        // articleRepository = new ArticleRepository();

        searchView = findViewById(R.id.search_view_articles);
        recyclerViewSearchResults = findViewById(R.id.recycler_view_search_results);
        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(this));

        articleAdapter = new ArticleAdapter(new ArrayList<>());
        recyclerViewSearchResults.setAdapter(articleAdapter);

        setupSearchView();
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
                // You can implement live search here if you want
                // searchArticles(newText);
                return true;
            }
        });
    }

    private void searchArticles(String keyword) {
        // TODO: Implement the search logic using ArticleRepository
        /*
        articleRepository.searchArticles(keyword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Article> articles = task.getResult().toObjects(Article.class);
                articleAdapter.setArticles(articles);
            } else {
                // Handle error
            }
        });
        */
    }
}
