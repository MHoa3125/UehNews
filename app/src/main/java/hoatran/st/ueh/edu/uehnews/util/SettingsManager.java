package hoatran.st.ueh.edu.uehnews.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Lớp quản lý cài đặt của ứng dụng, sử dụng SharedPreferences.
 * Giúp lưu trữ và truy xuất các lựa chọn của người dùng một cách nhất quán.
 */
public class SettingsManager {

    private static final String PREFERENCES_NAME = "UehNewsSettings";
    private static final String KEY_FONT_SIZE = "fontSize";
    private static final String KEY_DARK_MODE = "darkMode";

    // Giá trị mặc định
    private static final int DEFAULT_FONT_SIZE_PROGRESS = 1; // 0: Nhỏ, 1: Vừa, 2: Lớn
    private static final boolean DEFAULT_DARK_MODE = false;

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Lưu lựa chọn cỡ chữ (dưới dạng progress của SeekBar).
     * @param context Context của ứng dụng.
     * @param progress Giá trị progress từ 0 đến 2.
     */
    public static void saveFontSize(Context context, int progress) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putInt(KEY_FONT_SIZE, progress);
        editor.apply();
    }

    /**
     * Lấy ra lựa chọn cỡ chữ đã lưu.
     * @param context Context của ứng dụng.
     * @return Giá trị progress đã lưu, hoặc giá trị mặc định (1) nếu chưa có.
     */
    public static int getFontSize(Context context) {
        return getPreferences(context).getInt(KEY_FONT_SIZE, DEFAULT_FONT_SIZE_PROGRESS);
    }

    /**
     * Lưu trạng thái Chế độ tối.
     * @param context Context của ứng dụng.
     * @param isEnabled True nếu chế độ tối được bật.
     */
    public static void saveDarkMode(Context context, boolean isEnabled) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(KEY_DARK_MODE, isEnabled);
        editor.apply();
    }

    /**
     * Lấy ra trạng thái Chế độ tối đã lưu.
     * @param context Context của ứng dụng.
     * @return True nếu chế độ tối được bật, ngược lại là False.
     */
    public static boolean isDarkMode(Context context) {
        return getPreferences(context).getBoolean(KEY_DARK_MODE, DEFAULT_DARK_MODE);
    }
}
