package hoatran.st.ueh.edu.uehnews.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class MyArticleAdapter extends RecyclerView.Adapter<MyArticleAdapter.ArticleViewHolder> {

    private final List<Article> articleList;
    private final OnArticleClickListener onArticleClickListener;
    private final OnDeleteClickListener onDeleteClickListener;

    public interface OnArticleClickListener {
        void onArticleClick(Article article);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Article article);
    }

    public MyArticleAdapter(List<Article> articleList, OnArticleClickListener onArticleClickListener, OnDeleteClickListener onDeleteClickListener) {
        this.articleList = articleList;
        this.onArticleClickListener = onArticleClickListener;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articleList.get(position);
        holder.bind(article, onArticleClickListener, onDeleteClickListener);
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        ImageView ivArticleImage;
        TextView tvArticleTitle;
        TextView tvArticleStatus;
        TextView tvArticleDate; // Thêm TextView cho ngày tháng
        Button btnDelete;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            ivArticleImage = itemView.findViewById(R.id.iv_article_thumbnail);
            tvArticleTitle = itemView.findViewById(R.id.tv_article_title);
            tvArticleStatus = itemView.findViewById(R.id.tv_article_status);
            tvArticleDate = itemView.findViewById(R.id.tv_article_date); // Ánh xạ View
            btnDelete = itemView.findViewById(R.id.btn_delete_article);
        }

        public void bind(final Article article, final OnArticleClickListener articleClickListener, final OnDeleteClickListener deleteClickListener) {
            tvArticleTitle.setText(article.getTitle());

            String status = article.getStatus();
            tvArticleStatus.setText(getStatusText(status));
            tvArticleStatus.setBackgroundResource(getStatusBackground(status));

            // Cập nhật ngày giờ
            if (article.getCreatedAt() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                tvArticleDate.setText(sdf.format(new Date(article.getCreatedAt())));
            }

            if (ivArticleImage != null) {
                Glide.with(itemView.getContext())
                        .load(article.getImageUrl())
                        .centerCrop()
                        .placeholder(R.drawable.ic_image_placeholder)
                        .into(ivArticleImage);
            }

            itemView.setOnClickListener(v -> articleClickListener.onArticleClick(article));

            if (btnDelete != null) {
                if ("draft".equals(status) || "rejected".equals(status)) {
                    btnDelete.setVisibility(View.VISIBLE);
                    btnDelete.setOnClickListener(v -> deleteClickListener.onDeleteClick(article));
                } else {
                    btnDelete.setVisibility(View.GONE);
                }
            }
        }

        private String getStatusText(String status) {
            if (status == null) return "Không xác định";
            switch (status) {
                case "draft": return "Bản nháp";
                case "pending": return "Chờ duyệt";
                case "approved": return "Đã duyệt";
                case "rejected": return "Bị từ chối";
                default: return status;
            }
        }

        private int getStatusBackground(String status) {
            if (status == null) return R.drawable.bg_status_badge;
            switch (status) {
                case "approved": return R.drawable.bg_status_approved;
                case "pending": return R.drawable.bg_status_pending;
                case "rejected": return R.drawable.bg_status_rejected;
                default: return R.drawable.bg_status_badge;
            }
        }
    }
}
