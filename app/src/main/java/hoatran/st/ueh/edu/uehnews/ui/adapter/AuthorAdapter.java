// ĐƯỜNG DẪN: app/src/main/java/hoatran/st/ueh/edu/uehnews/ui/adapter/AuthorAdapter.java
package hoatran.st.ueh.edu.uehnews.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.List;
import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.data.model.User;

public class AuthorAdapter extends RecyclerView.Adapter<AuthorAdapter.AuthorViewHolder> {

    private List<User> authorList;
    private OnAuthorStatusChangedListener listener;

    public interface OnAuthorStatusChangedListener {
        void onStatusChange(User author, boolean isActive);
    }

    public AuthorAdapter(List<User> authorList, OnAuthorStatusChangedListener listener) {
        this.authorList = authorList;
        this.listener = listener;
    }

    public void setAuthors(List<User> authorList) {
        this.authorList = authorList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AuthorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_author, parent, false);
        return new AuthorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuthorViewHolder holder, int position) {
        User author = authorList.get(position);
        holder.bind(author);
    }

    @Override
    public int getItemCount() {
        return authorList.size();
    }

    class AuthorViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthorName, tvAuthorEmail;
        SwitchMaterial switchStatus;

        public AuthorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthorName = itemView.findViewById(R.id.tv_author_name);
            tvAuthorEmail = itemView.findViewById(R.id.tv_author_email);
            switchStatus = itemView.findViewById(R.id.switch_author_status);
        }

        public void bind(User author) {
            tvAuthorName.setText(author.getDisplayName() != null ? author.getDisplayName() : "Chưa có tên");
            tvAuthorEmail.setText(author.getEmail());

            // SỬA LỖI GỐC: Gọi đúng phương thức isActive() đã được chú thích
            switchStatus.setOnCheckedChangeListener(null);
            switchStatus.setChecked(author.isActive());

            // Gắn lại listener
            switchStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    author.setActive(isChecked);
                    listener.onStatusChange(author, isChecked);
                }
            });
        }
    }
}
