package hoatran.st.ueh.edu.uehnews.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import hoatran.st.ueh.edu.uehnews.data.local.DatabaseHelper;
import hoatran.st.ueh.edu.uehnews.data.model.Article;

public class ArticleRepository {

    private final CollectionReference articleCollection;
    private final DatabaseHelper dbHelper;

    public ArticleRepository(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.articleCollection = db.collection("articles");
        this.dbHelper = new DatabaseHelper(context);
    }

    // --- Chức năng Firebase ---

    // Lấy bài viết mới nhất đã duyệt
    public Task<QuerySnapshot> getLatestApprovedArticles(int limit) {
        return articleCollection
                .whereEqualTo("status", "approved")
                .orderBy("approvedAt", Query.Direction.DESCENDING)
                .limit(limit)
                .get();
    }

    // Lấy bài viết theo ID từ Firebase
    public Task<Article> getArticleById(String articleId) {
        return articleCollection.document(articleId).get().continueWith(task ->
                task.getResult().toObject(Article.class));
    }

    // --- Chức năng SQLite ---

    // Lưu một bài viết vào danh sách Yêu thích
    public void addArticleToFavorites(Article article) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID, article.getId());
        values.put(DatabaseHelper.COLUMN_TITLE, article.getTitle());
        values.put(DatabaseHelper.COLUMN_THUMBNAIL_URL, article.getThumbnailUrl());
        values.put(DatabaseHelper.COLUMN_AUTHOR_NAME, article.getAuthorName());
        values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, article.getCategoryName());
        values.put(DatabaseHelper.COLUMN_SAVED_AT, System.currentTimeMillis());

        db.insertWithOnConflict(DatabaseHelper.TABLE_FAVORITES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // Lấy tất cả bài viết Yêu thích
    public List<Article> getFavoriteArticles() {
        List<Article> articles = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_FAVORITES, null, null, null, null, null, DatabaseHelper.COLUMN_SAVED_AT + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Article article = new Article();
                article.setId(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
                article.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)));
                // Lấy các cột khác tương tự...
                articles.add(article);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return articles;
    }

    // Xóa một bài viết khỏi danh sách Yêu thích
    public void removeArticleFromFavorites(String articleId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_FAVORITES, DatabaseHelper.COLUMN_ID + " = ?", new String[]{articleId});
        db.close();
    }

    // Tương tự, bạn có thể tạo các hàm cho Lịch sử xem (addArticleToHistory, getHistoryArticles...)
}

