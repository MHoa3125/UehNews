package hoatran.st.ueh.edu.uehnews.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import hoatran.st.ueh.edu.uehnews.data.model.Article;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ueh_news_offline.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_FAVORITES = "favorites";
    public static final String TABLE_HISTORY = "history";

    // Common Columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_IMAGE_URL = "image_url";
    public static final String COLUMN_AUTHOR_NAME = "author_name";
    public static final String COLUMN_CATEGORY_NAME = "category_name";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_CONTENT = "content"; // For offline reading


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_TEMPLATE = "CREATE TABLE %s (%s TEXT PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER, %s TEXT)";
        db.execSQL(String.format(CREATE_TABLE_TEMPLATE, TABLE_FAVORITES, COLUMN_ID, COLUMN_TITLE, COLUMN_IMAGE_URL, COLUMN_AUTHOR_NAME, COLUMN_CATEGORY_NAME, COLUMN_CREATED_AT, COLUMN_CONTENT));
        db.execSQL(String.format(CREATE_TABLE_TEMPLATE, TABLE_HISTORY, COLUMN_ID, COLUMN_TITLE, COLUMN_IMAGE_URL, COLUMN_AUTHOR_NAME, COLUMN_CATEGORY_NAME, COLUMN_CREATED_AT, COLUMN_CONTENT));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    // --- Favorites Methods ---

    public void addFavorite(Article article) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = createArticleContentValues(article);
        db.insertWithOnConflict(TABLE_FAVORITES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void removeFavorite(String articleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITES, COLUMN_ID + " = ?", new String[]{articleId});
    }

    public boolean isFavorite(String articleId) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_FAVORITES, new String[]{COLUMN_ID}, COLUMN_ID + " = ?",
                new String[]{articleId}, null, null, null, null)) {
            return cursor.getCount() > 0;
        }
    }

    public List<Article> getAllFavorites() {
        return getAllArticlesFromTable(TABLE_FAVORITES);
    }

    // --- History Methods ---

    public void addHistory(Article article) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = createArticleContentValues(article);
        db.insertWithOnConflict(TABLE_HISTORY, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<Article> getAllHistory() {
        return getAllArticlesFromTable(TABLE_HISTORY);
    }

    // --- Helper Methods ---

    private ContentValues createArticleContentValues(Article article) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, article.getId());
        values.put(COLUMN_TITLE, article.getTitle());
        values.put(COLUMN_IMAGE_URL, article.getImageUrl());
        values.put(COLUMN_AUTHOR_NAME, article.getAuthorName());
        values.put(COLUMN_CATEGORY_NAME, article.getCategoryName());
        values.put(COLUMN_CREATED_AT, article.getCreatedAt());
        values.put(COLUMN_CONTENT, article.getContent());
        return values;
    }

    private List<Article> getAllArticlesFromTable(String tableName) {
        List<Article> articleList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + tableName + " ORDER BY " + COLUMN_CREATED_AT + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery(selectQuery, null)) {
            if (cursor.moveToFirst()) {
                do {
                    Article article = new Article();
                    article.setId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                    article.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                    article.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)));
                    article.setAuthorName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR_NAME)));
                    article.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME)));
                    article.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
                    article.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
                    articleList.add(article);
                } while (cursor.moveToNext());
            }
        }
        return articleList;
    }
}
