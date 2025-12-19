package hoatran.st.ueh.edu.uehnews.ui.admin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.data.model.Article;

public class ArticleApprovalActivity extends AppCompatActivity {

    private static final String TAG = "ArticleApprovalActivity";
    private RecyclerView recyclerView;
    private PendingArticleAdapter adapter;
    private ArrayList<Article> articleList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;

    private final ActivityResultLauncher<Intent> previewLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.d(TAG, "Nhận được kết quả thành công, đang tải lại danh sách...");
                    fetchPendingArticles();
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_article_approval);

        db = FirebaseFirestore.getInstance();

        // SỬA LỖI: Đã bỏ đi phần xử lý Toolbar không tồn tại

        recyclerView = findViewById(R.id.recycler_view_pending_articles);
        progressBar = findViewById(R.id.progress_bar_loading);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        articleList = new ArrayList<>();
        adapter = new PendingArticleAdapter(this, articleList, previewLauncher);
        recyclerView.setAdapter(adapter);

        fetchPendingArticles();
    }

    private void fetchPendingArticles() {
        Log.d(TAG, "Bắt đầu tải các bài viết chờ duyệt...");
        progressBar.setVisibility(View.VISIBLE);

        db.collection("articles")
                .whereEqualTo("status", "pending")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful() && task.getResult() != null) {
                        articleList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Article article = document.toObject(Article.class);
                                article.setId(document.getId());
                                articleList.add(article);
                            } catch (Exception e) {
                                Log.e(TAG, "Lỗi khi chuyển đổi document: " + document.getId(), e);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "Tải thành công " + articleList.size() + " bài viết.");
                        if (articleList.isEmpty()) {
                            Toast.makeText(this, "Không có bài viết nào đang chờ duyệt.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w(TAG, "Lỗi khi lấy tài liệu: ", task.getException());
                        Toast.makeText(this, "Không thể tải danh sách.", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
