package hoatran.st.ueh.edu.uehnews;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import hoatran.st.ueh.edu.uehnews.ui.activities.ArticleSearchActivity;
import hoatran.st.ueh.edu.uehnews.ui.admin.AdminDashboardActivity;
import hoatran.st.ueh.edu.uehnews.ui.auth.AuthManager;
import hoatran.st.ueh.edu.uehnews.ui.author.AuthorDashboardActivity;
import hoatran.st.ueh.edu.uehnews.ui.categories.CategoriesFragment;
import hoatran.st.ueh.edu.uehnews.ui.home.HomeFragment;
import hoatran.st.ueh.edu.uehnews.ui.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- SỬA LỖI: Kiểm tra vai trò và chuyển hướng NGAY LẬP TỨC ---
        String currentUserRole = AuthManager.getCurrentRole(this);

        // Nếu là Admin, chuyển đến trang Admin và đóng MainActivity
        if (AuthManager.ROLE_ADMIN.equals(currentUserRole)) {
            startActivity(new Intent(this, AdminDashboardActivity.class));
            finish();
            return; // Dừng việc thực thi onCreate của MainActivity ở đây
        }

        // Nếu là Author, chuyển đến trang Author và đóng MainActivity
        if (AuthManager.ROLE_AUTHOR.equals(currentUserRole)) {
            startActivity(new Intent(this, AuthorDashboardActivity.class));
            finish();
            return; // Dừng việc thực thi onCreate của MainActivity ở đây
        }

        // --- Nếu là GUEST, tiếp tục hiển thị giao diện của MainActivity ---
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            Intent intent = new Intent(this, ArticleSearchActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final BottomNavigationView.OnItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.nav_categories) {
                    selectedFragment = new CategoriesFragment();
                } else if (itemId == R.id.nav_profile) {
                    selectedFragment = new ProfileFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                }
                return true;
            };
}
