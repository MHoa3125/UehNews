package hoatran.st.ueh.edu.uehnews.ui.author;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import hoatran.st.ueh.edu.uehnews.MainActivity;
import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.ui.auth.AuthManager;

public class AuthorDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, tvTotalArticles, tvPendingArticles, tvApprovedArticles;
    private Button btnWriteArticle, btnManageMyArticles, btnLogout;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.author_dashboard);

        // Khởi tạo Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Kiểm tra đăng nhập
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ views
        tvWelcome = findViewById(R.id.tv_author_welcome);
        tvTotalArticles = findViewById(R.id.tv_total_articles);
        tvPendingArticles = findViewById(R.id.tv_pending_articles);
        tvApprovedArticles = findViewById(R.id.tv_approved_articles);

        btnWriteArticle = findViewById(R.id.btn_write_article);
        btnManageMyArticles = findViewById(R.id.btn_manage_my_articles);
        btnLogout = findViewById(R.id.btn_author_logout);

        // Hiển thị thông tin user
        displayUserInfo();

        // Load thống kê
        loadStatistics();

        // Viết bài mới
        btnWriteArticle.setOnClickListener(v -> {
            Intent intent = new Intent(AuthorDashboardActivity.this, WriteArticleActivity.class);
            startActivity(intent);
        });

        // Quản lý bài viết của tôi
        btnManageMyArticles.setOnClickListener(v -> {
            Intent intent = new Intent(AuthorDashboardActivity.this, MyArticlesActivity.class);
            startActivity(intent);
        });

        // Đăng xuất
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void displayUserInfo() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            String displayName = email != null ? email.split("@")[0] : "Tác giả";
            tvWelcome.setText("Xin chào, " + displayName + "!");
        }
    }

    private void loadStatistics() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();
        CollectionReference articlesRef = db.collection("articles");

        // Đếm tổng số bài viết
        articlesRef.whereEqualTo("authorId", userId)
                .count()
                .get(AggregateSource.SERVER)
                .addOnSuccessListener(aggregateQuerySnapshot -> {
                    long total = aggregateQuerySnapshot.getCount();
                    if (tvTotalArticles != null) {
                        tvTotalArticles.setText("Tổng bài viết: " + total);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AuthorDashboardActivity.this, "Lỗi đếm tổng bài viết.", Toast.LENGTH_SHORT).show();
                });

        // Đếm bài viết chờ duyệt
        articlesRef.whereEqualTo("authorId", userId)
                .whereEqualTo("status", "pending")
                .count()
                .get(AggregateSource.SERVER)
                .addOnSuccessListener(aggregateQuerySnapshot -> {
                    long pending = aggregateQuerySnapshot.getCount();
                    if (tvPendingArticles != null) {
                        tvPendingArticles.setText("Chờ duyệt: " + pending);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AuthorDashboardActivity.this, "Lỗi đếm bài viết chờ duyệt.", Toast.LENGTH_SHORT).show();
                });

        // Đếm bài viết đã duyệt
        articlesRef.whereEqualTo("authorId", userId)
                .whereEqualTo("status", "approved")
                .count()
                .get(AggregateSource.SERVER)
                .addOnSuccessListener(aggregateQuerySnapshot -> {
                    long approved = aggregateQuerySnapshot.getCount();
                    if (tvApprovedArticles != null) {
                        tvApprovedArticles.setText("Đã duyệt: " + approved);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AuthorDashboardActivity.this, "Lỗi đếm bài viết đã duyệt.", Toast.LENGTH_SHORT).show();
                });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    AuthManager.signOut(this);
                    Intent intent = new Intent(AuthorDashboardActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    Toast.makeText(this, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload thống kê khi quay lại activity
        loadStatistics();
    }
}
