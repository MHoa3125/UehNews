package hoatran.st.ueh.edu.uehnews.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ueh_news_offline.db";
    private static final int DATABASE_VERSION = 1;

    // Tên bảng
    public static final String TABLE_FAVORITES = "favorites";
    public static final String TABLE_HISTORY = "history";

    // Các cột chung
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_THUMBNAIL_URL = "thumbnail_url";
    public static final String COLUMN_AUTHOR_NAME = "author_name";
    public static final String COLUMN_CATEGORY_NAME = "category_name";
    public static final String COLUMN_SAVED_AT = "saved_at";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Câu lệnh tạo bảng Tin yêu thích (favorites)
        String CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "("
                + COLUMN_ID + " TEXT PRIMARY KEY,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_THUMBNAIL_URL + " TEXT,"
                + COLUMN_AUTHOR_NAME + " TEXT,"
                + COLUMN_CATEGORY_NAME + " TEXT,"
                + COLUMN_SAVED_AT + " INTEGER" + ")";
        db.execSQL(CREATE_FAVORITES_TABLE);

        // Câu lệnh tạo bảng Lịch sử xem (history)
        String CREATE_HISTORY_TABLE = "CREATE TABLE " + TABLE_HISTORY + "("
                + COLUMN_ID + " TEXT PRIMARY KEY,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_THUMBNAIL_URL + " TEXT,"
                + COLUMN_AUTHOR_NAME + " TEXT,"
                + COLUMN_CATEGORY_NAME + " TEXT,"
                + COLUMN_SAVED_AT + " INTEGER" + ")"; // saved_at ở đây mang ý nghĩa là "viewed_at"
        db.execSQL(CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng cũ nếu tồn tại và tạo lại
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }
}
