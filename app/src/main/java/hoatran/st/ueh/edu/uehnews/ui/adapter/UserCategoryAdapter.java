package hoatran.st.ueh.edu.uehnews.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.data.model.Category;

public class UserCategoryAdapter extends RecyclerView.Adapter<UserCategoryAdapter.CategoryViewHolder> {

    private final List<Category> categoryList;
    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public UserCategoryAdapter(List<Category> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_user, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.tvName.setText(category.getName());
        // Thao tác với ImageView đã được xóa bỏ một cách an toàn

        holder.itemView.setOnClickListener(v -> listener.onCategoryClick(category));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    // ViewHolder đã được cập nhật để không còn chứa ImageView
    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            // Đã xóa bỏ findViewById cho ImageView
            tvName = itemView.findViewById(R.id.tv_category_name_user);
        }
    }
}
