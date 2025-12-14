package hoatran.st.ueh.edu.uehnews.ui.categories;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.data.model.Article;
import hoatran.st.ueh.edu.uehnews.ui.adapter.HomeArticleAdapter;

public class CategoryArticlesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HomeArticleAdapter adapter;
    private List<Article> articleList;
    private ProgressBar progressBar;
    private TextView tvNoData;
    private FirebaseFirestore db;
    private String categoryId;
    private String categoryName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_articles);

        // Lấy dữ liệu từ Intent
        categoryId = getIntent().getStringExtra("categoryId");
        categoryName = getIntent().getStringExtra("categoryName");

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_category_articles);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(categoryName != null ? categoryName : "Danh mục");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.rv_category_articles);
        progressBar = findViewById(R.id.progress_bar_category_articles);
        tvNoData = findViewById(R.id.tv_no_data);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        articleList = new ArrayList<>();
        // Sử dụng lại HomeArticleAdapter vì cấu trúc hiển thị giống nhau
        adapter = new HomeArticleAdapter(articleList, this::onArticleClick);
        recyclerView.setAdapter(adapter);

        if (categoryId != null) {
            loadArticlesByCategory();
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin danh mục", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadArticlesByCategory() {
        progressBar.setVisibility(View.VISIBLE);
        tvNoData.setVisibility(View.GONE);

        // Truy vấn bài viết theo categoryId và status = 'approved'
        db.collection("articles")
                .whereEqualTo("categoryId", categoryId)
                .whereEqualTo("status", "approved")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    articleList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Article article = document.toObject(Article.class);
                            article.setId(document.getId());
                            articleList.add(article);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    if (articleList.isEmpty()) {
                        tvNoData.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    // Log lỗi để lấy link tạo Index
                    Log.e("CategoryArticles", "Lỗi tải bài viết (Kiểm tra link Index bên dưới): ", e);
                    Toast.makeText(this, "Lỗi tải bài viết: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void onArticleClick(Article article) {
        // Tạm thời hiển thị Dialog nội dung, sau này có thể mở DetailActivity
        new AlertDialog.Builder(this)
                .setTitle(article.getTitle())
                .setMessage(article.getContent())
                .setPositiveButton("Đóng", null)
                .show();
    }
}
