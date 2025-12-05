package hoatran.st.ueh.edu.uehnews.data.model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Article {
    // Thuộc tính chung cho cả Firebase & SQLite
    private String id; // Document ID của Firebase, cũng là Primary Key cho SQLite
    private String title;
    private String content;
    private String thumbnailUrl;
    private String authorName;
    private String categoryName;
    private int viewCount;

    // Thuộc tính chỉ có trên Firebase
    private String authorId;
    private String categoryId;
    private String status; // "pending", "approved", "rejected"
    private String reasonReject;

    @ServerTimestamp
    private Date createdAt;
    @ServerTimestamp
    private Date approvedAt;

    // Constructor rỗng cho Firebase
    public Article() {}

    // Getters and Setters...
    // (Bao gồm getter và setter cho tất cả các thuộc tính trên)

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReasonReject() { return reasonReject; }
    public void setReasonReject(String reasonReject) { this.reasonReject = reasonReject; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Date approvedAt) { this.approvedAt = approvedAt; }
}
