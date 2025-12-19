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
import java.util.List;

import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.data.model.Article;

public class ArticleApprovalActivity extends AppCompatActivity {

    private static final String TAG = "ArticleApprovalActivity";
    private RecyclerView recyclerView;
    private PendingArticleAdapter adapter; // Giữ nguyên tên adapter gốc của bạn
    private List<Article> articleList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ======================= SỬA LỖI Ở ĐÂY =======================
        // Trỏ đến tên file layout đã được đổi tên và đồng bộ
        setContentView(R.layout.admin_activity_article_approval);
        // =============================================================

        // Khởi tạo Firebase
        db = FirebaseFirestore.getInstance();

        // Ánh xạ View
        recyclerView = findViewById(R.id.recycler_view_pending_articles);
        progressBar = findViewById(R.id.progress_bar_loading);

        // Cài đặt RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        articleList = new ArrayList<>();
        adapter = new PendingArticleAdapter(this, (ArrayList<Article>) articleList);
        recyclerView.setAdapter(adapter);

        // Bắt đầu lấy dữ liệu
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
                        Log.w(TAG, "Lỗi khi lấy tài liệu (HÃY KIỂM TRA INDEX TRÊN FIREBASE): ", task.getException());
                        Toast.makeText(this, "Không thể tải danh sách. Vui lòng kiểm tra Logcat.", Toast.LENGTH_LONG).show();
                    }
                });
    }
}