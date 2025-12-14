package hoatran.st.ueh.edu.uehnews.ui.categories;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.data.model.Category;
import hoatran.st.ueh.edu.uehnews.ui.adapter.UserCategoryAdapter;

public class CategoriesFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserCategoryAdapter adapter;
    private List<Category> categoryList;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_categories, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.rv_categories_user);
        progressBar = view.findViewById(R.id.progress_bar_categories_user);

        // Sử dụng GridLayoutManager với 2 cột
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        
        categoryList = new ArrayList<>();
        adapter = new UserCategoryAdapter(categoryList, this::onCategoryClick);
        recyclerView.setAdapter(adapter);

        loadCategories();

        return view;
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
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Lỗi tải danh mục: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onCategoryClick(Category category) {
        // Chuyển sang màn hình danh sách bài viết
        Intent intent = new Intent(getContext(), CategoryArticlesActivity.class);
        intent.putExtra("categoryId", category.getId());
        intent.putExtra("categoryName", category.getName());
        startActivity(intent);
    }
}
