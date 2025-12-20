package hoatran.st.ueh.edu.uehnews.ui.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

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

// SỬA LỖI: Implement interface để lắng nghe sự kiện từ Adapter
public class ArticleApprovalActivity extends AppCompatActivity implements PendingArticleAdapter.OnArticleUpdateListener {

    private static final String TAG = "ArticleApprovalActivity";
    private RecyclerView recyclerView;
    private PendingArticleAdapter adapter;
    private ArrayList<Article> articleList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_article_approval);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recycler_view_pending_articles);
        progressBar = findViewById(R.id.progress_bar_loading);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        articleList = new ArrayList<>();
        
        // SỬA LỖI: Cung cấp tham số thứ 3 (this) cho constructor của Adapter
        adapter = new PendingArticleAdapter(this, articleList, this);
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

    // SỬA LỖI: Override lại phương thức onArticleUpdated từ interface
    @Override
    public void onArticleUpdated() {
        Log.d(TAG, "Nhận được tín hiệu cập nhật từ Adapter. Tải lại danh sách...");
        // Khi Adapter báo đã cập nhật xong (duyệt/từ chối), chúng ta sẽ tải lại danh sách
        fetchPendingArticles();
    }
}
