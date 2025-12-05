package hoatran.st.ueh.edu.uehnews.ui.auth;

import android.app.Activity;
import android.content.Intent;import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import hoatran.st.ueh.edu.uehnews.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ActivityResultLauncher<Intent> googleLauncher;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_login);

        // --- SỬA LỖI 1: Khởi tạo các đối tượng cần thiết ---
        mAuth = FirebaseAuth.getInstance();

        // Cấu hình Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // --- SỬA LỖI 2: Xử lý kết quả trả về từ Google ---
        googleLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            // Google Sign In thành công, bây giờ xác thực với Firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                            firebaseAuthWithGoogle(account.getIdToken());
                        } catch (ApiException e) {
                            // Google Sign In thất bại
                            Log.w(TAG, "Google sign in failed", e);
                            AuthManager.handleLoginFailure(this, e.getMessage());
                        }
                    } else {
                        // Người dùng hủy đăng nhập
                        AuthManager.handleLoginFailure(this, "Đăng nhập Google đã bị hủy.");
                    }
                });

        // Thiết lập sự kiện click cho nút đăng nhập
        findViewById(R.id.btnGoogleLogin).setOnClickListener(v -> {
            // --- SỬA LỖI 3: Gọi trực tiếp Intent đăng nhập ---
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleLauncher.launch(signInIntent);
        });

        // Lưu ý: Không cần kiểm tra người dùng đã đăng nhập ở đây,
        // vì luồng mới của chúng ta là người dùng phải chủ động bấm vào nút đăng nhập.
    }

    // --- SỬA LỖI 4: Tạo lại phương thức xác thực với Firebase ---
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng nhập thành công, gọi AuthManager để xử lý phân quyền và chuyển hướng
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        AuthManager.handleLoginSuccess(this, user);
                    } else {
                        // Đăng nhập thất bại
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        AuthManager.handleLoginFailure(this, "Xác thực Firebase thất bại.");
                    }
                });
    }
}
