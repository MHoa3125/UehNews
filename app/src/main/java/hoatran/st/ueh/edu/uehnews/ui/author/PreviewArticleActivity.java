package hoatran.st.ueh.edu.uehnews.ui.author;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import hoatran.st.ueh.edu.uehnews.R;

public class PreviewArticleActivity extends AppCompatActivity {

    private ImageView ivThumbnail;
    private TextView tvTitle, tvContent;
    private Button btnSaveDraft, btnSubmit;
    private ProgressBar progressBar;
    private Toolbar toolbar;

    private Uri imageUri;
    private String title, content, authorName;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_article);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        // SỬA LỖI: Thêm lại đầy đủ các findViewById đã bị thiếu
        toolbar = findViewById(R.id.toolbar_preview);
        ivThumbnail = findViewById(R.id.iv_preview_thumbnail);
        tvTitle = findViewById(R.id.tv_preview_title);
        tvContent = findViewById(R.id.tv_preview_content);
        btnSaveDraft = findViewById(R.id.btn_preview_save_draft);
        btnSubmit = findViewById(R.id.btn_preview_submit);
        progressBar = findViewById(R.id.progress_bar_preview);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        content = intent.getStringExtra("content");
        authorName = intent.getStringExtra("authorName");
        String imageUriString = intent.getStringExtra("imageUri");
        if (imageUriString != null) {
            imageUri = Uri.parse(imageUriString);
        }

        // Bây giờ tvTitle và tvContent sẽ không còn null nữa
        tvTitle.setText(title);
        tvContent.setText(content);
        if (imageUri != null) {
            Glide.with(this).load(imageUri).centerCrop().into(ivThumbnail);
        }

        btnSaveDraft.setOnClickListener(v -> saveArticle("draft"));
        btnSubmit.setOnClickListener(v -> saveArticle("pending"));
    }

    private void saveArticle(String status) {
        if (imageUri == null) {
            Toast.makeText(this, "Thiếu ảnh đại diện", Toast.LENGTH_SHORT).show();
            return;
        }
        setLoading(true);
        uploadImageAndSaveArticle(title, content, authorName, status);
    }

    private void uploadImageAndSaveArticle(String title, String content, String authorName, String status) {
        String fileName = "articles/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = storage.getReference().child(fileName);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    saveArticleToFirestore(title, content, imageUrl, authorName, status);
                }))
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Lỗi upload ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveArticleToFirestore(String title, String content, String imageUrl, String authorName, String status) {
        if (auth.getCurrentUser() == null) {
            setLoading(false);
            Toast.makeText(this, "Bạn cần đăng nhập để thực hiện thao tác này", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        String userEmail = auth.getCurrentUser().getEmail();
        long currentTime = System.currentTimeMillis();

        Map<String, Object> articleData = new HashMap<>();
        articleData.put("title", title);
        articleData.put("content", content);
        articleData.put("imageUrl", imageUrl);
        articleData.put("authorId", userId);
        articleData.put("authorEmail", userEmail);
        articleData.put("authorName", authorName);
        articleData.put("status", status);
        articleData.put("createdAt", currentTime);
        articleData.put("updatedAt", currentTime);

        db.collection("articles")
                .add(articleData)
                .addOnSuccessListener(documentReference -> {
                    setLoading(false);
                    String message = status.equals("draft") ? "Lưu nháp thành công!" : "Gửi bài viết thành công! Đang chờ duyệt.";
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("isArticlePosted", true);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Lỗi lưu bài viết: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSubmit.setEnabled(!isLoading);
        btnSaveDraft.setEnabled(!isLoading);
    }
}
