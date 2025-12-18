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

import de.hdodenhof.circleimageview.CircleImageView;
import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.data.model.Article;
// DỌN DẸP: Xóa bỏ hoàn toàn import không cần thiết

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
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
        TextView tvCategory, tvTitle, tvAuthorName, tvPublishDate;
        CircleImageView civAuthorAvatar;

        public HomeArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.image_view_article);
            tvCategory = itemView.findViewById(R.id.tv_article_category);
            tvTitle = itemView.findViewById(R.id.text_view_article_title);
            tvAuthorName = itemView.findViewById(R.id.tv_author_name);
            tvPublishDate = itemView.findViewById(R.id.tv_publish_date);
            civAuthorAvatar = itemView.findViewById(R.id.img_author_avatar);
        }

        public void bind(final Article article, final OnArticleClickListener listener) {
            tvTitle.setText(article.getTitle());
            tvAuthorName.setText(article.getAuthorName() != null ? article.getAuthorName() : "N/A");
            tvCategory.setText(article.getCategoryName() != null ? article.getCategoryName() : "Tin tức");

            if (article.getCreatedAt() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                tvPublishDate.setText(sdf.format(new Date(article.getCreatedAt())));
            } else {
                tvPublishDate.setText("");
            }

            Glide.with(itemView.getContext()).load(article.getImageUrl()).centerCrop().placeholder(R.drawable.ueh_placeholder).into(ivImage);
            Glide.with(itemView.getContext()).load(article.getAuthorAvatarUrl()).placeholder(R.drawable.ic_default_avatar).error(R.drawable.ic_default_avatar).into(civAuthorAvatar);

            // SỬA LỖI: Khôi phục lại cách xử lý sự kiện click đúng
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onArticleClick(article);
                }
            });
        }
    }
}
