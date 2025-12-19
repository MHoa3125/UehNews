package hoatran.st.ueh.edu.uehnews.ui.admin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.data.model.Article;

public class PendingArticleAdapter extends RecyclerView.Adapter<PendingArticleAdapter.ArticleViewHolder> {

    private final Context context;
    private final ArrayList<Article> articleList;
    private final ActivityResultLauncher<Intent> previewLauncher;

    public PendingArticleAdapter(Context context, ArrayList<Article> articleList, ActivityResultLauncher<Intent> launcher) {
        this.context = context;
        this.articleList = articleList;
        this.previewLauncher = launcher;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_item_pending_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articleList.get(position);

        holder.tvTitle.setText(article.getTitle());

        if (article.getAuthorName() != null && !article.getAuthorName().isEmpty()) {
            holder.tvAuthor.setText("Tác giả: " + article.getAuthorName());
        } else {
            holder.tvAuthor.setText("Tác giả: " + article.getAuthorEmail());
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String formattedDate = sdf.format(new Date(article.getCreatedAt()));
            holder.tvDate.setText("Ngày gửi: " + formattedDate);
        } catch (Exception e) {
            holder.tvDate.setText("Ngày gửi: N/A");
            Log.e("PendingArticleAdapter", "Lỗi định dạng ngày tháng: ", e);
        }

        // SỬA LỖI CUỐI CÙNG: Trỏ đến đúng Activity của Admin
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminPreviewActivity.class);
            intent.putExtra(AdminPreviewActivity.EXTRA_ARTICLE, article);
            previewLauncher.launch(intent);
        });
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvDate;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_article_title);
            tvAuthor = itemView.findViewById(R.id.tv_article_author);
            tvDate = itemView.findViewById(R.id.tv_article_date);
        }
    }
}
