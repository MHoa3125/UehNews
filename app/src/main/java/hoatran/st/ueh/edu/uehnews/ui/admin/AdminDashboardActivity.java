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

        Button btnLogout = findViewById(R.id.btn_admin_logout);
        btnLogout.setOnClickListener(v -> {
            AuthManager.signOut(this);
            // Quay về màn hình Guest
            Intent intent = new Intent(AdminDashboardActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
