package hoatran.st.ueh.edu.uehnews.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import hoatran.st.ueh.edu.uehnews.R;
import hoatran.st.ueh.edu.uehnews.ui.activities.FavoritesActivity;
import hoatran.st.ueh.edu.uehnews.ui.activities.HistoryActivity;
import hoatran.st.ueh.edu.uehnews.ui.auth.LoginActivity;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_profile_guest, container, false);

        // Find the buttons
        TextView btnSavedArticles = view.findViewById(R.id.btn_saved_articles);
        TextView btnRecentArticles = view.findViewById(R.id.btn_recent_articles);
        Button btnGoToLogin = view.findViewById(R.id.btn_go_to_login);

        // Set click listeners
        btnSavedArticles.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FavoritesActivity.class);
            startActivity(intent);
        });

        btnRecentArticles.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HistoryActivity.class);
            startActivity(intent);
        });

        btnGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
