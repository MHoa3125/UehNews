// ĐƯỜNG DẪN: app/src/main/java/hoatran/st/ueh/edu/uehnews/data/model/User.java
package hoatran.st.ueh.edu.uehnews.data.model;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

public class User implements Serializable {
    private String uid;
    private String displayName;
    private String email;
    private String photoUrl;
    private String role;
    private boolean isActive;
    private long createdAt;

    public User() {}

    // Getters
    public String getUid() { return uid; }
    public String getDisplayName() { return displayName; }
    public String getEmail() { return email; }
    public String getPhotoUrl() { return photoUrl; }
    public String getRole() { return role; }
    // SỬA LỖI: Sửa lỗi cú pháp nhỏ
    public long getCreatedAt() { return createdAt; }

    // Sử dụng @PropertyName để khớp chính xác với tên trường trên Firestore
    @PropertyName("isActive")
    public boolean isActive() { return isActive; }

    // Setters
    public void setUid(String uid) { this.uid = uid; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public void setRole(String role) { this.role = role; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    @PropertyName("isActive")
    public void setActive(boolean active) { isActive = active; }
}
