// ĐƯỜNG DẪN: app/src/main/java/hoatran/st/ueh/edu/uehnews/ui/admin/ManageAuthorsActivity.java
package hoatran.st.ueh.edu.uehnews.ui.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.data.model.User;
import hoatran.st.ueh.edu.uehnews.ui.adapter.AuthorAdapter;

public class ManageAuthorsActivity extends AppCompatActivity {

    private static final String TAG = "ManageAuthorsActivity";
    private RecyclerView recyclerView;
    private AuthorAdapter adapter;
    private List<User> authorList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_manage_authors);

        db = FirebaseFirestore.getInstance();
        authorList = new ArrayList<>();

        MaterialToolbar toolbar = findViewById(R.id.toolbar_manage_authors);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.rv_authors);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AuthorAdapter(authorList, this::updateAuthorStatus);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add_author);
        fab.setOnClickListener(v -> showAddAuthorDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAuthors();
    }

    private void loadAuthors() {
        db.collection("users").whereEqualTo("role", "author")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    authorList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        User user = document.toObject(User.class);
                        user.setUid(document.getId());
                        authorList.add(user);
                    }
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Tải thành công " + authorList.size() + " tác giả.");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Lỗi khi tải tác giả.", e);
                    Toast.makeText(this, "Lỗi tải danh sách tác giả.", Toast.LENGTH_LONG).show();
                });
    }

    private void updateAuthorStatus(User author, boolean isActive) {
        db.collection("users").document(author.getUid())
                .update("isActive", isActive)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    author.setActive(isActive);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    loadAuthors();
                });
    }

    // SỬA ĐỔI: Đọc thêm tên tác giả từ dialog
    private void showAddAuthorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.admin_dialog_add_author, null);

        final EditText etName = dialogView.findViewById(R.id.et_author_name);
        final EditText etEmail = dialogView.findViewById(R.id.et_author_email);

        builder.setView(dialogView)
                .setPositiveButton("Thêm", (dialog, id) -> {
                    String name = etName.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();

                    if (name.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập tên tác giả", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(this, "Vui lòng nhập email hợp lệ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    addAuthor(name, email);
                })
                .setNegativeButton("Hủy", (dialog, id) -> dialog.cancel());
        builder.create().show();
    }

    // SỬA ĐỔI: Nhận thêm `displayName` để lưu
    private void addAuthor(String displayName, String email) {
        Map<String, Object> newAuthor = new HashMap<>();
        newAuthor.put("email", email);
        newAuthor.put("displayName", displayName); // Sử dụng tên được nhập
        newAuthor.put("role", "author");
        newAuthor.put("isActive", true);
        newAuthor.put("photoUrl", "");
        newAuthor.put("createdAt", System.currentTimeMillis());

        db.collection("users").document(email).set(newAuthor)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Thêm tác giả thành công", Toast.LENGTH_SHORT).show();
                    loadAuthors();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Thêm tác giả thất bại", Toast.LENGTH_SHORT).show();
                });
    }
}
