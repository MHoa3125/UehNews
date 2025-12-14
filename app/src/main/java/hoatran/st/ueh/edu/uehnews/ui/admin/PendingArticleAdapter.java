package hoatran.st.ueh.edu.uehnews.ui.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "PendingArticleAdapter";

    public PendingArticleAdapter(Context context, ArrayList<Article> articleList) {
        this.context = context;
        this.articleList = articleList;
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
            Log.e(TAG, "Lỗi định dạng ngày tháng: ", e);
        }

        // Xử lý sự kiện Duyệt
        holder.btnApprove.setOnClickListener(v -> showApproveConfirmation(article, holder.getAdapterPosition()));

        // Xử lý sự kiện Từ chối (nhập lý do)
        holder.btnReject.setOnClickListener(v -> showRejectDialog(article, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    private void showApproveConfirmation(Article article, int position) {
        // Tải danh sách danh mục từ Firestore
        Toast.makeText(context, "Đang tải danh sách danh mục...", Toast.LENGTH_SHORT).show();

        db.collection("categories").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Category> categories = new ArrayList<>();
            List<String> categoryNames = new ArrayList<>();

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Category cat = document.toObject(Category.class);
                cat.setId(document.getId());
                categories.add(cat);
                categoryNames.add(cat.getName());
            }

            if (categories.isEmpty()) {
                Toast.makeText(context, "Chưa có danh mục nào. Vui lòng tạo danh mục trước.", Toast.LENGTH_LONG).show();
                return;
            }

            // Tạo dialog chọn danh mục
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Chọn danh mục & Duyệt");

            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(50, 40, 50, 10);

            final TextView tvMessage = new TextView(context);
            tvMessage.setText("Chọn danh mục cho bài viết này:");
            tvMessage.setPadding(0, 0, 0, 20);
            layout.addView(tvMessage);

            final Spinner spinner = new Spinner(context);
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categoryNames);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerAdapter);
            layout.addView(spinner);

            builder.setView(layout);

            builder.setPositiveButton("Duyệt", (dialog, which) -> {
                int selectedPosition = spinner.getSelectedItemPosition();
                if (selectedPosition >= 0) {
                    Category selectedCategory = categories.get(selectedPosition);
                    updateArticleStatus(article, "approved", position, null, selectedCategory);
                }
            });
            builder.setNegativeButton("Hủy", null);
            builder.show();

        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Lỗi tải danh mục: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Lỗi tải danh mục", e);
        });
    }

    private void showRejectDialog(Article article, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Từ chối bài viết");
        builder.setMessage("Vui lòng nhập lý do từ chối (bắt buộc):");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setHint("Nhập lý do...");
        input.setMinLines(2);
        builder.setView(input);

        builder.setPositiveButton("Từ chối", (dialog, which) -> {
            // Sẽ được override ở dưới để validation không đóng dialog
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Override nút Positive để kiểm tra input
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String reason = input.getText().toString().trim();
            if (reason.isEmpty()) {
                input.setError("Lý do không được để trống");
                Toast.makeText(context, "Vui lòng nhập lý do từ chối!", Toast.LENGTH_SHORT).show();
            } else {
                updateArticleStatus(article, "rejected", position, reason, null);
                dialog.dismiss();
            }
        });
    }

    private void updateArticleStatus(Article article, String newStatus, int position, String rejectionReason, Category category) {
        if (position == RecyclerView.NO_POSITION) {
            Log.w(TAG, "Không thể cập nhật vì vị trí không hợp lệ.");
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", newStatus);
        updates.put("updatedAt", System.currentTimeMillis());
        if (rejectionReason != null) {
            updates.put("rejectionReason", rejectionReason);
        }
        
        // Thêm thông tin danh mục nếu có
        if (category != null) {
            updates.put("categoryId", category.getId());
            updates.put("categoryName", category.getName());
        }

        db.collection("articles").document(article.getId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    String actionText = newStatus.equals("approved") ? "duyệt" : "từ chối";
                    Toast.makeText(context, "Đã " + actionText + " bài viết.", Toast.LENGTH_SHORT).show();

                    // Xóa item khỏi danh sách và cập nhật RecyclerView
                    if (position >= 0 && position < articleList.size()) {
                        articleList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, articleList.size());
                    }
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
            tvTitle = itemView.findViewById(R.id.tv_article_title);
            tvAuthor = itemView.findViewById(R.id.tv_article_author);
            tvDate = itemView.findViewById(R.id.tv_article_date);
            btnApprove = itemView.findViewById(R.id.btn_approve);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }
    }
}
