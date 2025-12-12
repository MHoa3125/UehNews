package hoatran.st.ueh.edu.uehnews.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hoatran.st.ueh.edu.uehnews.data.local.DatabaseHelper;
import hoatran.st.ueh.edu.uehnews.data.model.Article;

public class ArticleRepository {

    private final CollectionReference articleCollection;
    private final DatabaseHelper dbHelper;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    // --- Định nghĩa Callbacks để trả kết quả về Main Thread ---
    public interface ArticlesCallback {
        void onSuccess(List<Article> articles);
        void onError(Exception e);
    }

    public interface OperationCallback {
        void onSuccess();
        void onError(Exception e);
    }

    public ArticleRepository(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.articleCollection = db.collection("articles");
        this.dbHelper = new DatabaseHelper(context);
    }

    // --- Chức năng Firebase (vẫn giữ nguyên vì đã là bất đồng bộ) ---

    public Task<QuerySnapshot> getLatestApprovedArticles(int limit) {
        return articleCollection
                .whereEqualTo("status", "approved")
                .orderBy("approvedAt", Query.Direction.DESCENDING)
                .limit(limit)
                .get();
    }

    public Task<Article> getArticleById(String articleId) {
        return articleCollection.document(articleId).get().continueWith(task ->
                task.getResult().toObject(Article.class));
    }

    // --- Chức năng SQLite (Sửa lại để chạy bất đồng bộ) ---

    /**
     * Lưu một bài viết vào danh sách Yêu thích trên một luồng nền.
     * @param article Bài viết để lưu
     * @param callback Callback để nhận kết quả thành công hoặc thất bại trên luồng chính.
     */
    public void addArticleToFavorites(Article article, OperationCallback callback) {
        executor.execute(() -> {
            try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_ID, article.getId());
                values.put(DatabaseHelper.COLUMN_TITLE, article.getTitle());
                values.put(DatabaseHelper.COLUMN_THUMBNAIL_URL, article.getImageUrl());
                values.put(DatabaseHelper.COLUMN_AUTHOR_NAME, article.getAuthorName());
                values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, article.getCategoryName());
                values.put(DatabaseHelper.COLUMN_SAVED_AT, System.currentTimeMillis());

                db.insertWithOnConflict(DatabaseHelper.TABLE_FAVORITES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                mainThreadHandler.post(callback::onSuccess);
            } catch (Exception e) {
                mainThreadHandler.post(() -> callback.onError(e));
            }
        });
    }

    /**
     * Lấy tất cả bài viết Yêu thích từ SQLite trên luồng nền.
     * @param callback Callback để nhận danh sách bài viết hoặc lỗi trên luồng chính.
     */
    public void getFavoriteArticles(ArticlesCallback callback) {
        executor.execute(() -> {
            List<Article> articles = new ArrayList<>();
            try (SQLiteDatabase db = dbHelper.getReadableDatabase();
                 Cursor cursor = db.query(DatabaseHelper.TABLE_FAVORITES, null, null, null, null, null, DatabaseHelper.COLUMN_SAVED_AT + " DESC")) {

                if (cursor.moveToFirst()) {
                    do {
                        Article article = new Article();
                        article.setId(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
                        article.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)));
                        article.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_THUMBNAIL_URL)));
                        article.setAuthorName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AUTHOR_NAME)));
                        article.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME)));
                        articles.add(article);
                    } while (cursor.moveToNext());
                }
                mainThreadHandler.post(() -> callback.onSuccess(articles));
            } catch (Exception e) {
                mainThreadHandler.post(() -> callback.onError(e));
            }
        });
    }

    /**
     * Xóa một bài viết khỏi danh sách Yêu thích trên luồng nền.
     * @param articleId ID của bài viết cần xóa
     * @param callback Callback để nhận kết quả thành công hoặc thất bại trên luồng chính.
     */
    public void removeArticleFromFavorites(String articleId, OperationCallback callback) {
        executor.execute(() -> {
            try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
                db.delete(DatabaseHelper.TABLE_FAVORITES, DatabaseHelper.COLUMN_ID + " = ?", new String[]{articleId});
                mainThreadHandler.post(callback::onSuccess);
            } catch (Exception e) {
                mainThreadHandler.post(() -> callback.onError(e));
            }
        });
    }
}
