package hoatran.st.ueh.edu.uehnews.ui.auth;

import android.app.Activity;
import android.content.Context;import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import hoatran.st.ueh.edu.uehnews.MainActivity;
import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.ui.admin.AdminDashboardActivity;
import hoatran.st.ueh.edu.uehnews.ui.author.AuthorDashboardActivity;

public class AuthManager {

    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String ROLE_ADMIN = "admin";
    private static final String ROLE_AUTHOR = "author";
    private static final String ROLE_GUEST = "guest";

    // --- DANH SÁCH EMAIL PHÂN QUYỀN ---
    // Thay thế bằng email của bạn để kiểm thử
    private static final Set<String> ADMIN_EMAILS = new HashSet<>(Arrays.asList(
            "admin1@ueh.edu.vn",
            "hoatran.31231023175@st.ueh.edu.vn" // Ví dụ
    ));

    // Logic hiện tại: Bất kỳ ai đăng nhập không phải Admin đều là Author
    // Nếu bạn muốn có danh sách Author cụ thể, hãy tạo một Set tương tự ADMIN_EMAILS

    /**
     * Xử lý logic sau khi đăng nhập Google thành công.
     * Hàm này sẽ phân quyền và chuyển hướng đến màn hình phù hợp.
     * @param activity Activity hiện tại (thường là LoginActivity)
     * @param user Đối tượng FirebaseUser vừa đăng nhập thành công
     */
    public static void handleLoginSuccess(Activity activity, FirebaseUser user) {
        if (user == null) {
            handleLoginFailure(activity, "Lỗi: Không lấy được thông tin người dùng.");
            return;
        }

        String email = user.getEmail();
        SharedPreferences prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Lưu email người dùng
        editor.putString(KEY_USER_EMAIL, email);

        // Phân quyền dựa trên email
        String role = ROLE_AUTHOR; // Mặc định tất cả người dùng là Author
        if (email != null && ADMIN_EMAILS.contains(email.toLowerCase())) {
            role = ROLE_ADMIN;
        }

        // Lưu vai trò vào SharedPreferences
        editor.putString(KEY_USER_ROLE, role);
        editor.apply();

        // Chuyển hướng đến màn hình tương ứng với vai trò
        Intent intent;
        if (role.equals(ROLE_ADMIN)) {
            // Nếu là Admin, chuyển đến Admin Dashboard
            intent = new Intent(activity, AdminDashboardActivity.class);
        } else {
            // Nếu là Author, chuyển đến Author Dashboard
            intent = new Intent(activity, AuthorDashboardActivity.class);
        }

        // Xóa tất cả các activity cũ khỏi stack và mở màn hình mới
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish(); // Đóng LoginActivity
    }

    /**
     * Xử lý khi đăng nhập thất bại.
     * @param context Context để hiển thị Toast
     * @param errorMessage Thông báo lỗi
     */
    public static void handleLoginFailure(Context context, String errorMessage) {
        Toast.makeText(context, "Đăng nhập thất bại: " + errorMessage, Toast.LENGTH_LONG).show();
    }

    /**
     * Thực hiện đăng xuất người dùng khỏi Firebase và Google.
     * @param context Context để thực hiện các hành động
     */
    public static void signOut(Context context) {
        // Đăng xuất khỏi Firebase
        FirebaseAuth.getInstance().signOut();

        // Đăng xuất khỏi Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(context, gso);
        googleSignInClient.signOut();

        // Xóa thông tin đã lưu trong SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_USER_ROLE);
        editor.remove(KEY_USER_EMAIL);
        editor.apply();
    }

    /**
     * Lấy vai trò của người dùng hiện tại từ SharedPreferences.
     * @param context Context để truy cập SharedPreferences
     * @return Chuỗi vai trò ("admin", "author", hoặc "guest" nếu chưa đăng nhập)
     */
    public static String getCurrentRole(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER_ROLE, ROLE_GUEST);
    }
}
