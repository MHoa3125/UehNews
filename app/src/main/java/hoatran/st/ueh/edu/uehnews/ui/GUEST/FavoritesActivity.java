package hoatran.st.ueh.edu.uehnews.ui.GUEST;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.data.local.DatabaseHelper;
import hoatran.st.ueh.edu.uehnews.data.model.Article;
import hoatran.st.ueh.edu.uehnews.ui.adapter.ArticleAdapter;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFavorites;
    private ArticleAdapter articleAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_activity_favorites);

        dbHelper = new DatabaseHelper(this);

        recyclerViewFavorites = findViewById(R.id.recycler_view_favorites);
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));

        List<Article> favoriteArticles = dbHelper.getAllFavorites();
        articleAdapter = new ArticleAdapter(favoriteArticles);
        recyclerViewFavorites.setAdapter(articleAdapter);
    }
}
