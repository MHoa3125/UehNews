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
    private boolean isFavorite = false; // Biến để theo dõi trạng thái yêu thích

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

    private void applyFontSize() {
        int progress = SettingsManager.getFontSize(this);
        float fontSize;
        switch (progress) {
            case 0: fontSize = 16f; break;
            case 2: fontSize = 22f; break;
            case 1: default: fontSize = 19f; break;
        }
        textViewDetailArticleContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
    }

    private void saveToHistory() {
        dbHelper.addHistory(article);
    }

    private void setupShareButton() {
        imageViewShare.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("article_title", article.getTitle());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Đã sao chép tiêu đề!", Toast.LENGTH_SHORT).show();
        });
    }

    // --- KHÔI PHỤC LOGIC CHO NÚT YÊU THÍCH ---
    private void setupFavoriteButton() {
        // 1. Kiểm tra trạng thái yêu thích ban đầu
        if (article != null) {
            isFavorite = dbHelper.isFavorite(article.getId());
            updateFavoriteIcon();
        }

        // 2. Gán sự kiện click
        imageViewFavorite.setOnClickListener(v -> {
            if (article == null) return;

            if (isFavorite) {
                // Nếu đang là yêu thích -> Bỏ yêu thích
                dbHelper.removeFavorite(article.getId());
                Toast.makeText(this, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                // Nếu chưa yêu thích -> Thêm vào yêu thích
                dbHelper.addFavorite(article);
                Toast.makeText(this, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
            }

            // 3. Cập nhật lại trạng thái và icon
            isFavorite = !isFavorite;
            updateFavoriteIcon();
        });
    }

    private void updateFavoriteIcon() {
        if (isFavorite) {
            imageViewFavorite.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            imageViewFavorite.setImageResource(R.drawable.ic_favorite_border);
        }
    }
}
