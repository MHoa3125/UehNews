// ĐƯỜNG DẪN: app/src/main/java/hoatran/st/ueh/edu/uehnews/ui/profile/ProfileFragment.java
package hoatran.st.ueh.edu.uehnews.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import hoatran.st.ueh.edu.uehnews.MainActivity;
import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.ui.activities.FavoritesActivity;
import hoatran.st.ueh.edu.uehnews.ui.activities.HistoryActivity;
import hoatran.st.ueh.edu.uehnews.ui.admin.AdminDashboardActivity;
import hoatran.st.ueh.edu.uehnews.ui.author.AuthorDashboardActivity;
import hoatran.st.ueh.edu.uehnews.ui.author.MyArticlesActivity;
import hoatran.st.ueh.edu.uehnews.ui.auth.AuthManager;
import hoatran.st.ueh.edu.uehnews.ui.auth.LoginActivity;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContext() == null) {
            return null;
        }

        // 1. Kiểm tra vai trò người dùng từ AuthManager
        String userRole = AuthManager.getCurrentRole(getContext());

        View view;
        if (AuthManager.ROLE_GUEST.equals(userRole)) {
            // --- GIAO DIỆN CHO KHÁCH (GUEST) ---
            view = inflater.inflate(R.layout.main_profile_guest, container, false);
            setupGuestView(view);
        } else {
            // --- GIAO DIỆN CHO NGƯỜI DÙNG ĐÃ ĐĂNG NHẬP (ADMIN/AUTHOR) ---
            view = inflater.inflate(R.layout.main_profile_user, container, false);
            setupUserView(view, userRole);
        }

        return view;
    }

    /**
     * Cài đặt các sự kiện cho giao diện Guest
     */
    private void setupGuestView(View view) {
        TextView btnSavedArticles = view.findViewById(R.id.btn_saved_articles);
        TextView btnRecentArticles = view.findViewById(R.id.btn_recent_articles);
        Button btnGoToLogin = view.findViewById(R.id.btn_go_to_login);

        btnSavedArticles.setOnClickListener(v -> startActivity(new Intent(getActivity(), FavoritesActivity.class)));
        btnRecentArticles.setOnClickListener(v -> startActivity(new Intent(getActivity(), HistoryActivity.class)));
        btnGoToLogin.setOnClickListener(v -> startActivity(new Intent(getActivity(), LoginActivity.class)));
    }

    /**
     * Cài đặt các sự kiện và hiển thị thông tin cho giao diện User (Admin/Author)
     */
    private void setupUserView(View view, String userRole) {
        // Ánh xạ các view từ layout main_profile_user.xml
        ImageView userAvatar = view.findViewById(R.id.iv_user_avatar);
        TextView userName = view.findViewById(R.id.tv_user_name);
        TextView userEmail = view.findViewById(R.id.tv_user_email);

        Button btnAdminDashboard = view.findViewById(R.id.btn_admin_dashboard);
        Button btnAuthorDashboard = view.findViewById(R.id.btn_author_dashboard);
        Button btnMyArticles = view.findViewById(R.id.btn_my_articles);
        Button btnSavedArticles = view.findViewById(R.id.btn_saved_articles_user);
        Button btnRecentArticles = view.findViewById(R.id.btn_recent_articles_user);
        Button btnLogout = view.findViewById(R.id.btn_logout);

        // 1. Lấy thông tin người dùng từ Firebase Auth để hiển thị
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userName.setText(currentUser.getDisplayName());
            userEmail.setText(currentUser.getEmail());
            if (currentUser.getPhotoUrl() != null && getContext() != null) {
                Glide.with(getContext()).load(currentUser.getPhotoUrl()).into(userAvatar);
            }
        }

        // 2. Phân quyền hiển thị các nút chức năng
        if (AuthManager.ROLE_ADMIN.equals(userRole)) {
            btnAdminDashboard.setVisibility(View.VISIBLE);
            btnAuthorDashboard.setVisibility(View.GONE);
            btnMyArticles.setVisibility(View.GONE);
        } else if (AuthManager.ROLE_AUTHOR.equals(userRole)) {
            btnAdminDashboard.setVisibility(View.GONE);
            btnAuthorDashboard.setVisibility(View.VISIBLE);
            btnMyArticles.setVisibility(View.VISIBLE);
        }

        // 3. Cài đặt OnClickListener cho tất cả các nút
        btnAdminDashboard.setOnClickListener(v -> startActivity(new Intent(getActivity(), AdminDashboardActivity.class)));
        btnAuthorDashboard.setOnClickListener(v -> startActivity(new Intent(getActivity(), AuthorDashboardActivity.class)));
        btnMyArticles.setOnClickListener(v -> startActivity(new Intent(getActivity(), MyArticlesActivity.class)));
        btnSavedArticles.setOnClickListener(v -> startActivity(new Intent(getActivity(), FavoritesActivity.class)));
        btnRecentArticles.setOnClickListener(v -> startActivity(new Intent(getActivity(), HistoryActivity.class)));

        btnLogout.setOnClickListener(v -> {
            if (getContext() != null) {
                AuthManager.signOut(getContext());
                // Quay về trang chủ và làm mới lại stack activity
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                if (getActivity() != null) {
                    getActivity().finishAffinity();
                }
            }
        });
    }
}