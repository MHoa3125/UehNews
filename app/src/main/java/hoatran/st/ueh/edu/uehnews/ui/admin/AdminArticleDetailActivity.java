package hoatran.st.ueh.edu.uehnews.ui.admin;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.data.model.Article;

public class AdminArticleDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ARTICLE = "extra_article_detail";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sử dụng layout mới mà chúng ta đã tạo
        setContentView(R.layout.admin_activity_article_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_detail);
        toolbar.setNavigationOnClickListener(v -> finish());

        Article article = getIntent().getParcelableExtra(EXTRA_ARTICLE);

        if (article != null) {
            populateUi(article);
        } else {
            Toast.makeText(this, "Không thể tải chi tiết bài viết", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void populateUi(Article article) {
        ImageView ivImage = findViewById(R.id.iv_detail_image);
        TextView tvTitle = findViewById(R.id.tv_detail_title);
        CircleImageView ivAuthorAvatar = findViewById(R.id.iv_detail_author_avatar);
        TextView tvAuthorName = findViewById(R.id.tv_detail_author_name);
        TextView tvDate = findViewById(R.id.tv_detail_date);
        TextView tvContent = findViewById(R.id.tv_detail_content);

        tvTitle.setText(article.getTitle());
        tvAuthorName.setText(article.getAuthorName() != null ? article.getAuthorName() : "Chưa có tên");
        tvContent.setText(article.getContent());

        if (article.getCreatedAt() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            tvDate.setText(sdf.format(new Date(article.getCreatedAt())));
        }

        // Tải ảnh bài viết
        Glide.with(this)
                .load(article.getImageUrl())
                .placeholder(R.drawable.ueh_placeholder)
                .error(R.drawable.ueh_placeholder)
                .into(ivImage);

        // Tải ảnh đại diện tác giả
        Glide.with(this)
                .load(article.getAuthorAvatarUrl())
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.ic_default_avatar)
                .into(ivAuthorAvatar);
    }
}
