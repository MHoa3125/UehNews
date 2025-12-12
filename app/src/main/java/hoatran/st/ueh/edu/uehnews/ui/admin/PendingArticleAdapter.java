package hoatran.st.ueh.edu.uehnews.ui.admin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.data.model.Article;

public class PendingArticleAdapter extends RecyclerView.Adapter<PendingArticleAdapter.ArticleViewHolder> {

    private final Context context;
    private final ArrayList<Article> articleList;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "PendingArticleAdapter";

    // Sửa lại constructor để nhận Context
    public PendingArticleAdapter(Context context, ArrayList<Article> articleList) {
        this.context = context;
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng đúng tên file item layout của bạn
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
            Log.e(TAG, "Lỗi định dạng ngày tháng: ", e);
        }

        holder.btnApprove.setOnClickListener(v -> updateArticleStatus(article, "approved", holder.getAdapterPosition()));
        holder.btnReject.setOnClickListener(v -> updateArticleStatus(article, "rejected", holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    private void updateArticleStatus(Article article, String newStatus, int position) {
        if (position == RecyclerView.NO_POSITION) {
            Log.w(TAG, "Không thể cập nhật vì vị trí không hợp lệ.");
            return;
        }

        db.collection("articles").document(article.getId())
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    String actionText = newStatus.equals("approved") ? "duyệt" : "từ chối";
                    Toast.makeText(context, "Đã " + actionText + " bài viết.", Toast.LENGTH_SHORT).show();

                    // Xóa item khỏi danh sách và cập nhật RecyclerView một cách an toàn
                    articleList.remove(position);
                    notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Lỗi khi cập nhật status cho bài viết " + article.getId(), e);
                });
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvDate;
        Button btnApprove, btnReject;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các view từ file item layout của bạn
            tvTitle = itemView.findViewById(R.id.tv_article_title);
            tvAuthor = itemView.findViewById(R.id.tv_article_author);
            tvDate = itemView.findViewById(R.id.tv_article_date);
            btnApprove = itemView.findViewById(R.id.btn_approve);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }
    }
}
