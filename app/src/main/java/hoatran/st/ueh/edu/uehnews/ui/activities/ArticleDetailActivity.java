package hoatran.st.ueh.edu.uehnews.ui.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.data.local.DatabaseHelper;
import hoatran.st.ueh.edu.uehnews.data.model.Article;
import hoatran.st.ueh.edu.uehnews.util.SettingsManager;

public class ArticleDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ARTICLE = "extra_article";

    private Article article;
    private DatabaseHelper dbHelper;
    private ImageView imageViewFavorite;
    private ImageView imageViewShare;
    private TextView textViewDetailArticleContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_activity_article_detail);

        dbHelper = new DatabaseHelper(this);
        article = getIntent().getParcelableExtra(EXTRA_ARTICLE);

        if (article != null) {
            initViews();
            populateUI();
            saveToHistory();
            applyFontSize();
        } else {
            Toast.makeText(this, "Không thể tải bài viết", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_article_detail);
        toolbar.setNavigationOnClickListener(v -> finish());

        imageViewFavorite = findViewById(R.id.image_view_favorite);
        imageViewShare = findViewById(R.id.image_view_share);
        textViewDetailArticleContent = findViewById(R.id.text_view_detail_article_content);

        setupFavoriteButton();
        setupShareButton();
    }

    private void populateUI() {
        ImageView imageViewDetailArticle = findViewById(R.id.image_view_detail_article);
        TextView tvDetailCategory = findViewById(R.id.tv_detail_category);
        TextView textViewDetailArticleTitle = findViewById(R.id.text_view_detail_article_title);
        CircleImageView imgDetailAuthorAvatar = findViewById(R.id.img_detail_author_avatar);
        TextView textViewDetailArticleAuthor = findViewById(R.id.text_view_detail_article_author);
        TextView textViewDetailArticleDate = findViewById(R.id.text_view_detail_article_date);

        tvDetailCategory.setText(article.getCategoryName() != null ? article.getCategoryName() : "Tin tức");
        textViewDetailArticleTitle.setText(article.getTitle());
        textViewDetailArticleAuthor.setText(article.getAuthorName() != null ? article.getAuthorName() : "N/A");
        textViewDetailArticleContent.setText(article.getContent());

        if (article.getCreatedAt() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            textViewDetailArticleDate.setText(sdf.format(new Date(article.getCreatedAt())));
        }

        Glide.with(this).load(article.getImageUrl()).placeholder(R.drawable.ueh_placeholder).into(imageViewDetailArticle);
        Glide.with(this).load(article.getAuthorAvatarUrl()).placeholder(R.drawable.ic_default_avatar).error(R.drawable.ic_default_avatar).into(imgDetailAuthorAvatar);
    }

    /**
     * CẬP NHẬT CỠ CHỮ:
     * Đọc lựa chọn của người dùng và áp dụng các mức cỡ chữ mới.
     */
    private void applyFontSize() {
        int progress = SettingsManager.getFontSize(this);
        float fontSize;
        switch (progress) {
            case 0: // Nhỏ
                fontSize = 16f; // Tăng từ 14f
                break;
            case 2: // Lớn
                fontSize = 22f; // Tăng từ 18f
                break;
            case 1: // Vừa (Mặc định)
            default:
                fontSize = 19f; // Tăng từ 16f
                break;
        }
        textViewDetailArticleContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
    }

    private void saveToHistory() {
        dbHelper.addHistory(article);
    }

    private void setupShareButton() {
        // ... (code không đổi)
    }

    private void setupFavoriteButton() {
        // ... (code không đổi)
    }

    private void updateFavoriteIcon() {
        // ... (code không đổi)
    }
}
