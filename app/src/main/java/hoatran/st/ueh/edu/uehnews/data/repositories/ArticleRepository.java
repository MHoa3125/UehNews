package hoatran.st.ueh.edu.uehnews.data.repositories;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import hoatran.st.ueh.edu.uehnews.data.model.Article;

public class ArticleRepository {

    private final CollectionReference articleCollection;

    public ArticleRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.articleCollection = db.collection("articles");
    }

    public interface ArticlesCallback {
        void onCallback(List<Article> articles);
    }

    public void getLatestApprovedArticles(ArticlesCallback callback) {
        articleCollection
                .whereEqualTo("status", "approved")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Article> articles = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Article article = document.toObject(Article.class);
                            article.setId(document.getId());
                            articles.add(article);
                        }
                        callback.onCallback(articles);
                    } else {
                        callback.onCallback(new ArrayList<>());
                    }
                });
    }

    public void searchArticles(String keyword, ArticlesCallback callback) {
        if (keyword == null || keyword.trim().isEmpty()) {
            callback.onCallback(new ArrayList<>());
            return;
        }

        String finalKeyword = keyword.toLowerCase().trim();

        articleCollection
                .whereEqualTo("status", "approved")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Article> allApprovedArticles = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Article article = document.toObject(Article.class);
                            article.setId(document.getId());
                            allApprovedArticles.add(article);
                        }

                        // Filter locally for case-insensitive contains
                        List<Article> filteredList = allApprovedArticles.stream()
                                .filter(article -> article.getTitle().toLowerCase().contains(finalKeyword))
                                .collect(Collectors.toList());

                        callback.onCallback(filteredList);
                    } else {
                        callback.onCallback(new ArrayList<>());
                    }
                });
    }
}
