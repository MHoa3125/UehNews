package hoatran.st.ueh.edu.uehnews.ui.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.data.model.Article;

// ĐÃ ĐỔI TÊN FILE
public class AdminPreviewActivity extends AppCompatActivity {

    public static final String EXTRA_ARTICLE = "extra_article";

    private Article article;
    private FirebaseFirestore db;

    private Button btnApprove;
    private Button btnReject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // SỬA LỖI: Trỏ đến layout mới
        setContentView(R.layout.admin_activity_preview);

        db = FirebaseFirestore.getInstance();
        article = getIntent().getParcelableExtra(EXTRA_ARTICLE);

        if (article != null) {
            initViews();
            populateUI();
            setupActionButtons();
        } else {
            Toast.makeText(this, "Không thể tải bài viết để xem trước", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        // SỬA LỖI: Sử dụng các ID mới của layout admin
        MaterialToolbar toolbar = findViewById(R.id.admin_toolbar_preview);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setTitle("Xem trước bài duyệt");

        btnApprove = findViewById(R.id.admin_btn_approve);
        btnReject = findViewById(R.id.admin_btn_reject);
    }

    private void populateUI() {
        // SỬA LỖI: Sử dụng các ID mới của layout admin
        ImageView imageViewDetailArticle = findViewById(R.id.admin_preview_image);
        TextView tvDetailCategory = findViewById(R.id.admin_preview_category);
        TextView textViewDetailArticleTitle = findViewById(R.id.admin_preview_title);
        CircleImageView imgDetailAuthorAvatar = findViewById(R.id.admin_preview_author_avatar);
        TextView textViewDetailArticleAuthor = findViewById(R.id.admin_preview_author_name);
        TextView textViewDetailArticleDate = findViewById(R.id.admin_preview_date);
        TextView textViewDetailArticleContent = findViewById(R.id.admin_preview_content);

        tvDetailCategory.setText(article.getCategoryName() != null ? article.getCategoryName() : "Chưa phân loại");
        textViewDetailArticleTitle.setText(article.getTitle());
        textViewDetailArticleAuthor.setText(article.getAuthorName() != null ? article.getAuthorName() : "N/A");
        textViewDetailArticleContent.setText(article.getContent());

        if (article.getCreatedAt() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            textViewDetailArticleDate.setText(sdf.format(new Date(article.getCreatedAt())));
        }

        Glide.with(this).load(article.getImageUrl()).placeholder(R.drawable.ueh_placeholder).into(imageViewDetailArticle);
        Glide.with(this).load(article.getAuthorAvatarUrl()).placeholder(R.drawable.ic_default_avatar).error(R.drawable.ic_default_avatar).into(imgDetailAuthorAvatar);
    }

    private void setupActionButtons() {
        btnApprove.setOnClickListener(v -> updateArticleStatus("approved"));
        btnReject.setOnClickListener(v -> updateArticleStatus("rejected"));
    }

    private void updateArticleStatus(String status) {
        if (article == null || article.getId() == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID bài viết.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("articles").document(article.getId())
                .update("status", status)
                .addOnSuccessListener(aVoid -> {
                    String message = "approved".equals(status) ? "Đã duyệt bài viết thành công!" : "Đã từ chối bài viết.";
                    Toast.makeText(AdminPreviewActivity.this, message, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminPreviewActivity.this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
