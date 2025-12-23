package hoatran.st.ueh.edu.uehnews.ui.author;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.ui.adapter.MyArticleAdapter;
import hoatran.st.ueh.edu.uehnews.data.model.Article;

public class MyArticlesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private MyArticleAdapter adapter;
    private List<Article> articleList;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.author_activity_my_articles);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        recyclerView = findViewById(R.id.rv_my_articles);
        progressBar = findViewById(R.id.progress_bar);

        articleList = new ArrayList<>();
        // Adapter được khởi tạo với 2 hành động: xem chi tiết và xóa
        adapter = new MyArticleAdapter(articleList, this::onArticleClick, this::onDeleteClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadMyArticles();
    }

    private void loadMyArticles() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Bạn cần đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        String userId = auth.getCurrentUser().getUid();

        db.collection("articles")
                .whereEqualTo("authorId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    articleList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Article article = document.toObject(Article.class);
                        article.setId(document.getId());
                        articleList.add(article);
                    }

                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    if (articleList.isEmpty()) {
                        Toast.makeText(this, "Bạn chưa có bài viết nào.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // SỬA LỖI: Thêm lý do từ chối vào dialog
    private void onArticleClick(Article article) {
        StringBuilder message = new StringBuilder();
        message.append("Tiêu đề: ").append(article.getTitle());
        message.append("\nTrạng thái: ").append(getStatusText(article.getStatus()));

        // Nếu bài viết bị từ chối và có lý do, hiển thị lý do đó
        if ("rejected".equals(article.getStatus()) && article.getRejectionReason() != null && !article.getRejectionReason().isEmpty()) {
            message.append("\n\nLý do: ").append(article.getRejectionReason());
        }

        new AlertDialog.Builder(this)
                .setTitle("Chi tiết bài viết")
                .setMessage(message.toString())
                .setPositiveButton("Đóng", null)
                .show();
    }

    /**
     * Được gọi từ Adapter khi người dùng nhấn nút xóa.
     */
    private void onDeleteClick(Article article) {
        // Hiển thị dialog xác nhận trước khi xóa
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa bài viết '" + article.getTitle() + "'?\nHành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> deleteArticle(article)) // Gọi hàm xóa nếu người dùng đồng ý
                .setNegativeButton("Hủy", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Bắt đầu quá trình xóa: trước tiên xóa ảnh, sau đó xóa dữ liệu.
     */
    private void deleteArticle(Article article) {
        progressBar.setVisibility(View.VISIBLE);

        // 1. Xóa ảnh từ Firebase Storage nếu có
        if (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) {
            StorageReference imageRef = storage.getReferenceFromUrl(article.getImageUrl());
            imageRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        // 2. Nếu xóa ảnh thành công, tiếp tục xóa dữ liệu từ Firestore
                        deleteArticleFromFirestore(article);
                    })
                    .addOnFailureListener(e -> {
                        // Nếu xóa ảnh thất bại, vẫn thử xóa dữ liệu
                        Toast.makeText(this, "Lỗi xóa ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        deleteArticleFromFirestore(article);
                    });
        } else {
            // Nếu không có ảnh, chỉ cần xóa dữ liệu từ Firestore
            deleteArticleFromFirestore(article);
        }
    }

    /**
     * Xóa document của bài viết khỏi Firestore và cập nhật UI.
     */
    private void deleteArticleFromFirestore(Article article) {
        db.collection("articles").document(article.getId()).delete()
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Đã xóa bài viết", Toast.LENGTH_SHORT).show();

                    // 3. Xóa bài viết khỏi danh sách và cập nhật RecyclerView
                    int position = articleList.indexOf(article);
                    if (position != -1) {
                        articleList.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, articleList.size());
                    }

                    if (articleList.isEmpty()) {
                         Toast.makeText(this, "Bạn không còn bài viết nào.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi xóa bài viết: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String getStatusText(String status) {
        if (status == null) return "Không xác định";
        switch (status) {
            case "draft": return "Bản nháp";
            case "pending": return "Chờ duyệt";
            case "approved": return "Đã duyệt";
            case "rejected": return "Bị từ chối";
            default: return status;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại dữ liệu khi quay lại màn hình để đảm bảo danh sách luôn được cập nhật
        if (auth.getCurrentUser() != null) {
            loadMyArticles();
        }
    }
}
