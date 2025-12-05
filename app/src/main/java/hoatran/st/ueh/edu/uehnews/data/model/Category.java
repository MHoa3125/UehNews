package hoatran.st.ueh.edu.uehnews.data.model;

public class Category {
    private String id;          // Document ID của danh mục trong Firestore
    private String name;        // Tên của danh mục (ví dụ: "Công nghệ", "Kinh tế")
    private String imageUrl;    // URL hình ảnh đại diện cho danh mục

    // Constructor rỗng bắt buộc cho việc chuyển đổi dữ liệu từ Firestore
    public Category() {
    }

    // Constructor đầy đủ để dễ dàng tạo đối tượng mới
    public Category(String id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    // ----- Getters và Setters -----

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
