package hoatran.st.ueh.edu.uehnews.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Article implements Parcelable {
    private String id;
    private String title;
    private String content;
    private String imageUrl;
    private String status; // draft, pending, approved, rejected
    private String authorId;
    private String authorEmail;
    private String categoryId;
    private long createdAt;
    private long updatedAt;
    private String rejectionReason;
    private String authorName;
    private String categoryName;
    private String authorAvatarUrl; // <-- THÊM TRƯỜNG MỚI

    public Article() {
        // Constructor rỗng cần thiết cho Firestore
    }

    // Constructor cũ hơn có thể được giữ lại hoặc xóa đi nếu không dùng
    public Article(String id, String title, String content, String imageUrl,
                   String status, String authorId, String authorEmail) {
        // ...
    }

    // Constructor đầy đủ
    public Article(String id, String title, String content, String imageUrl, String status, String authorId, String authorEmail, String categoryId, long createdAt, long updatedAt, String rejectionReason, String authorName, String categoryName, String authorAvatarUrl) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.status = status;
        this.authorId = authorId;
        this.authorEmail = authorEmail;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.rejectionReason = rejectionReason;
        this.authorName = authorName;
        this.categoryName = categoryName;
        this.authorAvatarUrl = authorAvatarUrl;
    }

    // Parcelable (đọc dữ liệu theo thứ tự ghi vào)
    protected Article(Parcel in) {
        id = in.readString();
        title = in.readString();
        content = in.readString();
        imageUrl = in.readString();
        status = in.readString();
        authorId = in.readString();
        authorEmail = in.readString();
        categoryId = in.readString();
        createdAt = in.readLong();
        updatedAt = in.readLong();
        rejectionReason = in.readString();
        authorName = in.readString();
        categoryName = in.readString();
        authorAvatarUrl = in.readString(); // <-- Đọc trường mới
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(imageUrl);
        dest.writeString(status);
        dest.writeString(authorId);
        dest.writeString(authorEmail);
        dest.writeString(categoryId);
        dest.writeLong(createdAt);
        dest.writeLong(updatedAt);
        dest.writeString(rejectionReason);
        dest.writeString(authorName);
        dest.writeString(categoryName);
        dest.writeString(authorAvatarUrl); // <-- Ghi trường mới
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getImageUrl() { return imageUrl; }
    public String getStatus() { return status; }
    public String getAuthorId() { return authorId; }
    public String getAuthorEmail() { return authorEmail; }
    public String getCategoryId() { return categoryId; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public String getRejectionReason() { return rejectionReason; }
    public String getAuthorName() { return authorName; }
    public String getCategoryName() { return categoryName; }
    public String getAuthorAvatarUrl() { return authorAvatarUrl; } // <-- Thêm Getter

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setStatus(String status) { this.status = status; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public void setAuthorEmail(String authorEmail) { this.authorEmail = authorEmail; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public void setAuthorAvatarUrl(String authorAvatarUrl) { this.authorAvatarUrl = authorAvatarUrl; } // <-- Thêm Setter
}
