package hoatran.st.ueh.edu.uehnews.ui.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.Locale;

import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.data.local.DatabaseHelper;
import hoatran.st.ueh.edu.uehnews.data.model.Article;

public class ArticleDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ARTICLE = "extra_article";

    private ImageView imageViewDetailArticle, imageViewShare, imageViewFavorite;
    private TextView textViewDetailArticleTitle, textViewDetailArticleAuthor, textViewDetailArticleDate, textViewDetailArticleContent;

    private Article article;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_activity_article_detail);

        dbHelper = new DatabaseHelper(this);

        imageViewDetailArticle = findViewById(R.id.image_view_detail_article);
        textViewDetailArticleTitle = findViewById(R.id.text_view_detail_article_title);
        textViewDetailArticleAuthor = findViewById(R.id.text_view_detail_article_author);
        textViewDetailArticleDate = findViewById(R.id.text_view_detail_article_date);
        textViewDetailArticleContent = findViewById(R.id.text_view_detail_article_content);
        imageViewShare = findViewById(R.id.image_view_share);
        imageViewFavorite = findViewById(R.id.image_view_favorite);

        article = getIntent().getParcelableExtra(EXTRA_ARTICLE);

        if (article != null) {
            populateUI();
            saveToHistory();
            setupShareButton();
            setupFavoriteButton();
        }
    }

    private void populateUI() {
        textViewDetailArticleTitle.setText(article.getTitle());
        textViewDetailArticleAuthor.setText(article.getAuthorName());
        textViewDetailArticleContent.setText(article.getContent());

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(article.getCreatedAt());
        String date = DateFormat.format("dd/MM/yyyy", cal).toString();
        textViewDetailArticleDate.setText(date);

        Glide.with(this)
                .load(article.getImageUrl())
                .into(imageViewDetailArticle);
    }

    private void saveToHistory() {
        dbHelper.addHistory(article);
    }

    private void setupShareButton() {
        imageViewShare.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("article_link", "https://ueh.edu.vn/news/article/" + article.getId());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Link đã được sao chép!", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupFavoriteButton() {
        updateFavoriteIcon(); // Set initial state

        imageViewFavorite.setOnClickListener(v -> {
            if (dbHelper.isFavorite(article.getId())) {
                dbHelper.removeFavorite(article.getId());
                Toast.makeText(this, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.addFavorite(article);
                Toast.makeText(this, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
            }
            updateFavoriteIcon(); // Update icon after click
        });
    }

    private void updateFavoriteIcon() {
        if (dbHelper.isFavorite(article.getId())) {
            imageViewFavorite.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            imageViewFavorite.setImageResource(R.drawable.ic_favorite_border);
        }
    }
}
