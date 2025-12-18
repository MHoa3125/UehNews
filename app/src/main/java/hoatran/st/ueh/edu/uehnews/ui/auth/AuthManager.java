package hoatran.st.ueh.edu.uehnews.ui.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import hoatran.st.ueh.edu.uehnews.MainActivity;
import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.data.model.User;
import hoatran.st.ueh.edu.uehnews.ui.admin.AdminDashboardActivity;
import hoatran.st.ueh.edu.uehnews.ui.author.AuthorDashboardActivity;

public class AuthManager {

    private static final String TAG = "AuthManager";
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_USER_ROLE = "user_role";

    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_AUTHOR = "author";
    public static final String ROLE_GUEST = "guest";

    private static final Set<String> ADMIN_EMAILS = new HashSet<>(Arrays.asList(
            "admin1@ueh.edu.vn",
            "hoatran.31231023175@st.ueh.edu.vn",
            "nguyennguyen.31231027057@st.ueh.edu.vn",
            "quynhle.31231027234@st.ueh.edu.vn",
            "nhungnguyen.31231026625@st.ueh.edu.vn"
    ));

    public static void handleLoginSuccess(Activity activity, FirebaseUser user) {
        if (user == null || user.getEmail() == null) {
            handleLoginFailure(activity, "Lỗi: Không lấy được thông tin người dùng.");
            return;
        }

        String email = user.getEmail();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (ADMIN_EMAILS.contains(email)) {
            Log.d(TAG, "Đăng nhập với quyền ADMIN (từ danh sách cố định): " + email);
            saveRoleAndRedirect(activity, ROLE_ADMIN);
            return;
        }

        db.collection("users").document(email).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            User dbUser = document.toObject(User.class);
                            if (dbUser == null) {
                                handleLoginFailure(activity, "Lỗi đọc dữ liệu người dùng.");
                                signOut(activity);
                                return;
                            }

                            // SỬA LỖI GỐC: Gọi đúng phương thức isActive() đã được chú thích
                            if (!dbUser.isActive()) {
                                handleLoginFailure(activity, "Tài khoản của bạn đã bị vô hiệu hóa.");
                                signOut(activity);
                                return;
                            }

                            String role = dbUser.getRole();
                            if (ROLE_ADMIN.equals(role)) {
                                role = ROLE_GUEST;
                                Log.w(TAG, "Phát hiện email không thuộc danh sách admin nhưng có vai trò admin trong DB. Đã hạ quyền: " + email);
                            }
                            saveRoleAndRedirect(activity, role);
                        } else {
                            createNewUser(activity, user, db);
                        }
                    } else {
                        Log.e(TAG, "Lỗi khi kiểm tra vai trò: ", task.getException());
                        handleLoginFailure(activity, "Không thể xác thực vai trò. Vui lòng thử lại.");
                        signOut(activity);
                    }
                });
    }

    private static void createNewUser(Activity activity, FirebaseUser user, FirebaseFirestore db) {
        String email = user.getEmail();
        String name = user.getDisplayName();
        String photoUrl = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : "";

        Map<String, Object> newUser = new HashMap<>();
        newUser.put("email", email);
        newUser.put("displayName", name);
        newUser.put("photoUrl", photoUrl);
        newUser.put("role", ROLE_GUEST);
        newUser.put("isActive", true);
        newUser.put("createdAt", System.currentTimeMillis());

        db.collection("users").document(email).set(newUser)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Tạo người dùng mới thành công: " + email);
                    saveRoleAndRedirect(activity, ROLE_GUEST);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi tạo người dùng mới: ", e);
                    handleLoginFailure(activity, "Không thể tạo tài khoản mới. Vui lòng thử lại.");
                    signOut(activity);
                });
    }

    private static void saveRoleAndRedirect(Activity activity, String role) {
        SharedPreferences prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_USER_ROLE, role).apply();

        Intent intent;
        if (ROLE_ADMIN.equals(role)) {
            intent = new Intent(activity, AdminDashboardActivity.class);
        } else if (ROLE_AUTHOR.equals(role)) {
            intent = new Intent(activity, AuthorDashboardActivity.class);
        } else {
            intent = new Intent(activity, MainActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void handleLoginFailure(Context context, String errorMessage) {
        Toast.makeText(context, "Đăng nhập thất bại: " + errorMessage, Toast.LENGTH_LONG).show();
    }

    public static void signOut(Context context) {
        FirebaseAuth.getInstance().signOut();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(context, gso);
        googleSignInClient.signOut();

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_USER_ROLE).apply();
    }

    public static String getCurrentRole(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER_ROLE, ROLE_GUEST);
    }
}
