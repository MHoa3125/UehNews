package hoatran.st.ueh.edu.uehnews.ui.author;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import hoatran.st.ueh.edu.uehnews.R;

public class WriteArticleActivity extends AppCompatActivity {

    private ImageView ivThumbnail;
    private EditText etTitle, etContent;
    private Button btnSelectImage, btnPreview;
    private TextView tvSelectImagePrompt;

    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> previewLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.author_activity_write_article);

        ivThumbnail = findViewById(R.id.iv_thumbnail);
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        btnSelectImage = findViewById(R.id.btn_select_image);
        // Đổi btnSubmit thành btnPreview và bỏ btnSaveDraft
        btnPreview = findViewById(R.id.btn_submit);
        findViewById(R.id.btn_save_draft).setVisibility(View.GONE);
        btnPreview.setText("Xem trước");

        tvSelectImagePrompt = findViewById(R.id.tv_select_image_prompt);

        // Launcher để chọn ảnh
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            Glide.with(this).load(selectedImageUri).centerCrop().into(ivThumbnail);
                            tvSelectImagePrompt.setVisibility(View.GONE);
                        }
                    }
                });

        // Launcher để xử lý kết quả từ màn hình Preview
        previewLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        boolean isArticlePosted = result.getData().getBooleanExtra("isArticlePosted", false);
                        if (isArticlePosted) {
                            finish(); // Đóng màn hình viết bài khi đã đăng thành công
                        }
                    }
                });

        // Sự kiện click để mở thư viện ảnh
        btnSelectImage.setOnClickListener(v -> openImagePicker());
        findViewById(R.id.card_thumbnail).setOnClickListener(v -> openImagePicker());

        // Sự kiện click để xem trước
        btnPreview.setOnClickListener(v -> {
            if (validateInput()) {
                openPreview();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Chọn ảnh từ"));
    }

    private boolean validateInput() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (selectedImageUri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh đại diện", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (content.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void openPreview() {
        Intent intent = new Intent(this, PreviewArticleActivity.class);
        intent.putExtra("title", etTitle.getText().toString().trim());
        intent.putExtra("content", etContent.getText().toString().trim());
        intent.putExtra("imageUri", selectedImageUri.toString());
        previewLauncher.launch(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selectedImageUri != null) {
            outState.putString("imageUri", selectedImageUri.toString());
        }
        outState.putString("title", etTitle.getText().toString());
        outState.putString("content", etContent.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String imageUriString = savedInstanceState.getString("imageUri");
        if (imageUriString != null) {
            selectedImageUri = Uri.parse(imageUriString);
            Glide.with(this).load(selectedImageUri).centerCrop().into(ivThumbnail);
            tvSelectImagePrompt.setVisibility(View.GONE);
        }
        etTitle.setText(savedInstanceState.getString("title"));
        etContent.setText(savedInstanceState.getString("content"));
    }
}
