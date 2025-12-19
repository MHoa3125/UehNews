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
        Button btnReviewArticles = findViewById(R.id.btn_review_articles);
        btnReviewArticles.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ArticleApprovalActivity.class);
            startActivity(intent);
        });

        // --- Xử lý cho nút Quản lý Danh mục ---
        Button btnManageCategories = findViewById(R.id.btn_manage_categories);
        btnManageCategories.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageCategoriesActivity.class);
            startActivity(intent);
        });

        // --- Xử lý cho nút Quản lý Tài khoản (Tác giả) ---
        Button btnManageAccounts = findViewById(R.id.btn_manage_accounts);
        btnManageAccounts.setOnClickListener(v -> {
            // Mở màn hình quản lý tác giả mà chúng ta vừa tạo
            Intent intent = new Intent(AdminDashboardActivity.this, ManageAuthorsActivity.class);
            startActivity(intent);
        });

        // --- Xử lý cho nút Đăng Xuất ---
        Button btnLogout = findViewById(R.id.btn_admin_logout);
        btnLogout.setOnClickListener(v -> {
            AuthManager.signOut(this);
            Intent intent = new Intent(AdminDashboardActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}