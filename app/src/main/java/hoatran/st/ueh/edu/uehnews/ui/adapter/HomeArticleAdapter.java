package hoatran.st.ueh.edu.uehnews.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.data.model.Article;

public class HomeArticleAdapter extends RecyclerView.Adapter<HomeArticleAdapter.HomeArticleViewHolder> {

    private final List<Article> articleList;
    private final OnArticleClickListener onArticleClickListener;

    public interface OnArticleClickListener {
        void onArticleClick(Article article);
    }

    public HomeArticleAdapter(List<Article> articleList, OnArticleClickListener onArticleClickListener) {
        this.articleList = articleList;
        this.onArticleClickListener = onArticleClickListener;
    }

    @NonNull
    @Override
    public HomeArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_article, parent, false);
        return new HomeArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeArticleViewHolder holder, int position) {
        Article article = articleList.get(position);
        holder.bind(article, onArticleClickListener);
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    static class HomeArticleViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvAuthor, tvDate, tvSummary;

        public HomeArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_home_article_image);
            tvTitle = itemView.findViewById(R.id.tv_home_article_title);
            tvAuthor = itemView.findViewById(R.id.tv_home_article_author);
            tvDate = itemView.findViewById(R.id.tv_home_article_date);
            tvSummary = itemView.findViewById(R.id.tv_home_article_summary);
        }

        public void bind(final Article article, final OnArticleClickListener listener) {
            tvTitle.setText(article.getTitle());
            tvSummary.setText(article.getContent()); // Tạm thời hiển thị content làm summary

            // Hiển thị tên tác giả hoặc email nếu không có tên
            String author = article.getAuthorName();
            if (author == null || author.isEmpty()) {
                author = article.getAuthorEmail();
            }
            tvAuthor.setText(author);

            // Hiển thị ngày
            if (article.getCreatedAt() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                tvDate.setText("• " + sdf.format(new Date(article.getCreatedAt())));
            }

            // Hiển thị ảnh
            if (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(article.getImageUrl())
                        .centerCrop()
                        .placeholder(R.drawable.ic_image_placeholder)
                        .into(ivImage);
            } else {
                ivImage.setImageResource(R.drawable.ic_image_placeholder);
            }

            itemView.setOnClickListener(v -> listener.onArticleClick(article));
        }
    }
}
