package hoatran.st.ueh.edu.uehnews.ui.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.data.model.Article;
import hoatran.st.ueh.edu.uehnews.data.model.Category;

public class PendingArticleAdapter extends RecyclerView.Adapter<PendingArticleAdapter.ArticleViewHolder> {

    private final Context context;
    private final ArrayList<Article> articleList;
    private final FirebaseFirestore db;
    private static final String TAG = "PendingArticleAdapter";

    public interface OnArticleUpdateListener {
        void onArticleUpdated();
    }
    private final OnArticleUpdateListener updateListener;

    public PendingArticleAdapter(Context context, ArrayList<Article> articleList, OnArticleUpdateListener listener) {
        this.context = context;
        this.articleList = articleList;
        this.updateListener = listener;
        this.db = FirebaseFirestore.getInstance();
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
        holder.tvAuthor.setText("Tác giả: " + (article.getAuthorName() != null ? article.getAuthorName() : article.getAuthorEmail()));

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.tvDate.setText("Ngày gửi: " + sdf.format(new Date(article.getCreatedAt())));
        } catch (Exception e) {
            holder.tvDate.setText("Ngày gửi: N/A");
        }

        // --- GIỮ NGUYÊN LOGIC CŨ CỦA BẠN ---
        holder.btnApprove.setOnClickListener(v -> showCategorySelectionDialog(article));
        holder.btnReject.setOnClickListener(v -> {
            updateArticleStatus(article, "rejected", null);
        });
        
        // --- SỬA LỖI: Gán sự kiện chỉ cho vùng thông tin ---
        holder.infoContainer.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminArticleDetailActivity.class);
            intent.putExtra(AdminArticleDetailActivity.EXTRA_ARTICLE, article);
            context.startActivity(intent);
        });
    }

    private void showCategorySelectionDialog(Article article) {
        db.collection("categories").get().addOnSuccessListener(queryDocumentSnapshots -> {
            final List<Category> categories = new ArrayList<>();
            List<String> categoryNames = new ArrayList<>();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Category cat = doc.toObject(Category.class);
                cat.setId(doc.getId());
                categories.add(cat);
                categoryNames.add(cat.getName());
            }

            if (categories.isEmpty()) {
                Toast.makeText(context, "Chưa có danh mục!", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Chọn danh mục & Duyệt");

            final Spinner spinner = new Spinner(context);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, categoryNames);
            spinner.setAdapter(adapter);

            LinearLayout container = new LinearLayout(context);
            container.setPadding(60, 20, 60, 20);
            container.addView(spinner);
            builder.setView(container);

            builder.setPositiveButton("Duyệt", (dialog, which) -> {
                int pos = spinner.getSelectedItemPosition();
                if (pos >= 0) {
                    updateArticleStatus(article, "approved", categories.get(pos));
                }
            });
            builder.setNegativeButton("Hủy", null);
            builder.show();
        }).addOnFailureListener(e -> Log.e(TAG, "Lỗi tải danh mục", e));
    }

    private void updateArticleStatus(Article article, String status, @Nullable Category category) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        updates.put("updatedAt", System.currentTimeMillis());

        if ("approved".equals(status) && category != null) {
            updates.put("categoryId", category.getId());
            updates.put("categoryName", category.getName());
        }

        db.collection("articles").document(article.getId()).update(updates)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                if (updateListener != null) {
                    updateListener.onArticleUpdated();
                }
            })
            .addOnFailureListener(e -> Toast.makeText(context, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvDate;
        View btnApprove, btnReject;
        // SỬA LỖI: Thêm vùng chứa thông tin
        View infoContainer;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            infoContainer = itemView.findViewById(R.id.info_container);
            tvTitle = itemView.findViewById(R.id.tv_article_title);
            tvAuthor = itemView.findViewById(R.id.tv_article_author);
            tvDate = itemView.findViewById(R.id.tv_article_date);
            btnApprove = itemView.findViewById(R.id.btn_approve);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }
    }
}
