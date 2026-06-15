package com.example.alphachat.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alphachat.ChatActivity;
import com.example.alphachat.R;
import com.example.alphachat.model.UserModel;
import com.example.alphachat.utils.AndroidUtil;
import com.example.alphachat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

/**
 * Adapter for displaying user search results.
 *
 * This class uses {@link FirestoreRecyclerAdapter} to display a list of users matching
 * a search query from the {@code users} collection. It binds user profile data and
 * handles navigation to the {@link ChatActivity}.
 *
 * Cloud Firestore {@code users} collection.
 */
public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModelViewHolder> {

    /** The context of the activity where the search results are displayed. */
    Context context;

    /**
     * Constructs a new SearchUserRecyclerAdapter.
     *
     * @param options The {@link FirestoreRecyclerOptions} for {@link UserModel}.
     * @param context The {@link Context} of the parent Activity.
     */
    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * Binds the user's name, email, and profile picture to the UI components.
     * Sets up a click listener to initiate a chat with the selected user.
     *
     * @param holder The {@link UserModelViewHolder} to update.
     * @param position The position of the item within the adapter.
     * @param model The {@link UserModel} containing the user's data.
     */
    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        holder.usernameText.setText(model.getUsername());

        holder.emailText.setText(model.getEmail());

        if(model.getUserId().equals(FirebaseUtil.currentUserId())) {
            holder.usernameText.setText(model.getUsername() + " (Me)");
        }

        String profilePicUrl = model.getProfilePicUrl();

        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            Uri imageUri = Uri.parse(profilePicUrl);
            AndroidUtil.setProfilePic(context, imageUri, holder.profilePic);
        }else {
            holder.profilePic.setImageResource(R.drawable.person_icon);
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent, model);
            context.startActivity(intent);
        });
    }

    /**
     * Called by RecyclerView to create a new ViewHolder.
     *
     * @param parent The {@link ViewGroup} into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new {@link UserModelViewHolder} that holds the {@code search_user_recycler_row} view.
     */
    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false);
        return new UserModelViewHolder(view);
    }

    /**
     * ViewHolder class for user search result items.
     *
     * Holds references to views within the {@code search_user_recycler_row.xml} layout.
     */
    class UserModelViewHolder extends RecyclerView.ViewHolder {
        /** Displays the user's name. Binds to {@code user_name_text}. */
        TextView usernameText;
        /** Displays the user's email. Binds to {@code email_text}. */
        TextView emailText;
        /** Displays the user's profile picture. Binds to {@code profile_pic_image_view}. */
        ImageView profilePic;

        /**
         * Constructs a new UserModelViewHolder.
         *
         * @param itemView The root view of the search result row layout.
         */
        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            emailText = itemView.findViewById(R.id.email_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }

}
