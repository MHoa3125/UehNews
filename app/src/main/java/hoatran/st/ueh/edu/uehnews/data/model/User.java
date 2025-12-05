package hoatran.st.ueh.edu.uehnews.data.model;

public class User {
    private String uid;
    private String email;
    private String displayName;
    private String photoUrl;
    private String role;

    // Constructor rỗng bắt buộc cho Firestore
    public User() {}

    public User(String uid, String email, String displayName, String photoUrl, String role) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.role = role;
    }

    // Getters và Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}

