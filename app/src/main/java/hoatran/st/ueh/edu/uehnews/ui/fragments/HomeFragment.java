package hoatran.st.ueh.edu.uehnews.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.data.model.Article;
import hoatran.st.ueh.edu.uehnews.data.repositories.ArticleRepository;
import hoatran.st.ueh.edu.uehnews.ui.activities.ArticleDetailActivity;
import hoatran.st.ueh.edu.uehnews.ui.adapter.ArticleAdapter;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArticleAdapter articleAdapter;
    private ArticleRepository articleRepository;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        articleRepository = new ArticleRepository();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view_articles);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setupAdapter();
        loadLatestArticles();
    }

    private void setupAdapter() {
        articleAdapter = new ArticleAdapter(new ArrayList<>());
        articleAdapter.setOnItemClickListener(article -> {
            Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
            intent.putExtra(ArticleDetailActivity.EXTRA_ARTICLE, article);
            startActivity(intent);
        });
        recyclerView.setAdapter(articleAdapter);
    }

    private void loadLatestArticles() {
        articleRepository.getLatestApprovedArticles(articles -> {
            if (getActivity() != null) { // Ensure fragment is still attached
                getActivity().runOnUiThread(() -> {
                    articleAdapter.setArticles(articles);
                });
            }
        });
    }
}
