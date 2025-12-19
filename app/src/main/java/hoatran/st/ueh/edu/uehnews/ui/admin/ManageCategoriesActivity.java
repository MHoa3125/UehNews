package hoatran.st.ueh.edu.uehnews.ui.admin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.data.model.Category;
import hoatran.st.ueh.edu.uehnews.ui.adapter.CategoryAdapter;

public class ManageCategoriesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<Category> categoryList;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_manage_categories);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.rv_categories);
        progressBar = findViewById(R.id.progress_bar_categories);
        Button btnAddCategory = findViewById(R.id.btn_add_category);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(categoryList, new CategoryAdapter.OnCategoryActionListener() {
            @Override
            public void onEdit(Category category) {
                showAddEditDialog(category);
            }

            @Override
            public void onDelete(Category category) {
                showDeleteConfirmation(category);
            }
        });
        recyclerView.setAdapter(adapter);

        btnAddCategory.setOnClickListener(v -> showAddEditDialog(null));

        loadCategories();
    }

    private void loadCategories() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    categoryList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Category category = document.toObject(Category.class);
                        category.setId(document.getId());
                        categoryList.add(category);
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi tải danh mục: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showAddEditDialog(@Nullable Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(category == null ? "Thêm Danh Mục Mới" : "Sửa Danh Mục");

        View view = LayoutInflater.from(this).inflate(R.layout.admin_dialog_add_edit_category, null);
        EditText etName = view.findViewById(R.id.et_category_name);

        if (category != null) {
            etName.setText(category.getName());
        }

        builder.setView(view);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            // Sẽ được xử lý trong OnClickListener của nút Positive để ngăn đóng dialog khi lỗi
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                etName.setError("Vui lòng nhập tên danh mục");
                return;
            }

            if (category == null) {
                addNewCategory(name, dialog);
            } else {
                updateCategory(category, name, dialog);
            }
        });
    }

    private void addNewCategory(String name, AlertDialog dialog) {
        Map<String, Object> newCategory = new HashMap<>();
        newCategory.put("name", name);
        // Có thể thêm imageUrl sau

        progressBar.setVisibility(View.VISIBLE);
        db.collection("categories")
                .add(newCategory)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Đã thêm danh mục thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadCategories(); // Tải lại danh sách
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi thêm danh mục: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateCategory(Category category, String newName, AlertDialog dialog) {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("categories").document(category.getId())
                .update("name", newName)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Đã cập nhật danh mục", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadCategories(); // Tải lại danh sách
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showDeleteConfirmation(Category category) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa Danh Mục")
                .setMessage("Bạn có chắc muốn xóa danh mục '" + category.getName() + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteCategory(category))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteCategory(Category category) {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("categories").document(category.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Đã xóa danh mục", Toast.LENGTH_SHORT).show();
                    loadCategories();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi xóa danh mục: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}