package hoatran.st.ueh.edu.uehnews.ui.home;

import android.app.AlertDialog;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.data.model.Article;
import hoatran.st.ueh.edu.uehnews.ui.adapter.HomeArticleAdapter;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private HomeArticleAdapter adapter;
    private List<Article> articleList;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_home, container, false);

        db = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.rv_home_articles);
        progressBar = view.findViewById(R.id.progress_bar_home);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        articleList = new ArrayList<>();
        adapter = new HomeArticleAdapter(articleList, this::onArticleClick);
        recyclerView.setAdapter(adapter);

        loadApprovedArticles();

        return view;
    }

    private void loadApprovedArticles() {
        progressBar.setVisibility(View.VISIBLE);
        
        // Chỉ lấy những bài viết có status là "approved"
        db.collection("articles")
                .whereEqualTo("status", "approved")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    articleList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Article article = document.toObject(Article.class);
                            article.setId(document.getId());
                            articleList.add(article);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Lỗi tải tin tức: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onArticleClick(Article article) {
        // Xử lý khi click vào bài viết (ví dụ: mở màn hình chi tiết bài viết)
        // Hiện tại chỉ hiển thị thông báo
        new AlertDialog.Builder(getContext())
                .setTitle(article.getTitle())
                .setMessage(article.getContent())
                .setPositiveButton("Đóng", null)
                .show();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Tải lại dữ liệu khi quay lại màn hình
        loadApprovedArticles();
    }
}
