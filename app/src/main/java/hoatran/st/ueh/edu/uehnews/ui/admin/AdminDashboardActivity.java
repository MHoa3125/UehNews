package hoatran.st.ueh.edu.uehnews.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import hoatran.st.ueh.edu.uehnews.MainActivity;
import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.ui.auth.AuthManager;

public class AdminDashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        // --- Xử lý cho nút Duyệt Bài Viết ---
        // Lấy đúng ID từ file admin_dashboard.xml
        Button btnReviewArticles = findViewById(R.id.btn_review_articles);

        btnReviewArticles.setOnClickListener(v -> {
            // Mở màn hình duyệt bài
            Intent intent = new Intent(AdminDashboardActivity.this, ArticleApprovalActivity.class);
            startActivity(intent);
        });

        // --- Xử lý cho nút Đăng Xuất ---
        // Lấy đúng ID từ file admin_dashboard.xml
        Button btnLogout = findViewById(R.id.btn_admin_logout);
        btnLogout.setOnClickListener(v -> {
            AuthManager.signOut(this);
            // Quay về màn hình Guest
            Intent intent = new Intent(AdminDashboardActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Bạn có thể thêm code xử lý cho các nút "Quản lý Danh mục" và "Quản lý Tài khoản" ở đây nếu cần
        // Button btnManageCategories = findViewById(R.id.btn_manage_categories);
        // Button btnManageAccounts = findViewById(R.id.btn_manage_accounts);
    }
}
