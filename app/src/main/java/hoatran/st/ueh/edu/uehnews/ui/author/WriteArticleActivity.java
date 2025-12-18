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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        btnPreview = findViewById(R.id.btn_submit);
        findViewById(R.id.btn_save_draft).setVisibility(View.GONE);
        btnPreview.setText("Xem trước");

        tvSelectImagePrompt = findViewById(R.id.tv_select_image_prompt);

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

        previewLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        boolean isArticlePosted = result.getData().getBooleanExtra("isArticlePosted", false);
                        if (isArticlePosted) {
                            finish();
                        }
                    }
                });

        btnSelectImage.setOnClickListener(v -> openImagePicker());
        findViewById(R.id.card_thumbnail).setOnClickListener(v -> openImagePicker());

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
        // ... (giữ nguyên validateInput)
        return true;
    }

    private void openPreview() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String authorName = (user != null && user.getDisplayName() != null && !user.getDisplayName().isEmpty()) 
                            ? user.getDisplayName() 
                            : "UEH Author";

        Intent intent = new Intent(this, PreviewArticleActivity.class);
        intent.putExtra("title", etTitle.getText().toString().trim());
        intent.putExtra("content", etContent.getText().toString().trim());
        intent.putExtra("imageUri", selectedImageUri.toString());
        // SỬA LỖI: Gửi cả tên tác giả sang màn hình Preview
        intent.putExtra("authorName", authorName);
        previewLauncher.launch(intent);
    }

    // ... (giữ nguyên onSaveInstanceState và onRestoreInstanceState)
}
