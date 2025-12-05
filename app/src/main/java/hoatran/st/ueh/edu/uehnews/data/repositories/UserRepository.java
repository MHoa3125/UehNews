package hoatran.st.ueh.edu.uehnews.data.repositories;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import hoatran.st.ueh.edu.uehnews.data.model.User;

public class UserRepository {

    private final CollectionReference userCollection;

    public UserRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.userCollection = db.collection("users");
    }

    // Tạo hoặc cập nhật thông tin người dùng khi họ đăng nhập
    public Task<Void> createOrUpdateUser(User user) {
        return userCollection.document(user.getUid()).set(user);
    }

    // Lấy thông tin người dùng bằng UID
    public Task<DocumentSnapshot> getUser(String uid) {
        return userCollection.document(uid).get();
    }
}
