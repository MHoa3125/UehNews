package hoatran.st.ueh.edu.uehnews.data.model;

public class Article {
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
    private String rejectionReason; // Lý do từ chối (nếu có)

    // Thêm 2 trường còn thiếu
    private String authorName;
    private String categoryName;

    public Article() {
        // Constructor rỗng cần thiết cho Firestore
    }

    public Article(String id, String title, String content, String imageUrl,
                   String status, String authorId, String authorEmail) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.status = status;
        this.authorId = authorId;
        this.authorEmail = authorEmail;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public Article(String id, String title, String content, String imageUrl, String status, String authorId, String authorEmail, String categoryId, long createdAt, long updatedAt, String rejectionReason, String authorName, String categoryName) {
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
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    // Thêm getters cho 2 trường mới
    public String getAuthorName() {
        return authorName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    // Thêm setters cho 2 trường mới
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
