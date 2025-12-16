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

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewHistory;
    private ArticleAdapter articleAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_activity_history);

        dbHelper = new DatabaseHelper(this);

        recyclerViewHistory = findViewById(R.id.recycler_view_history);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));

        List<Article> historyArticles = dbHelper.getAllHistory();
        articleAdapter = new ArticleAdapter(historyArticles);
        recyclerViewHistory.setAdapter(articleAdapter);
    }
}
