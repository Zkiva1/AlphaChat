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
import com.example.alphachat.model.ChatroomModel;
import com.example.alphachat.model.UserModel;
import com.example.alphachat.utils.AndroidUtil;
import com.example.alphachat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

/**
 * Adapter for displaying the list of recent chat conversations.
 *
 * This class uses {@link FirestoreRecyclerAdapter} to show active chat rooms from the
 * {@code chatrooms} collection in real-time. It fetches the other participant's
 * profile information asynchronously for each row.
 *
 * Cloud Firestore {@code chatrooms} and {@code users} collections.
 */
public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    /** The context of the activity where the chat list is displayed. */
    Context context;

    /**
     * Constructs a new RecentChatRecyclerAdapter.
     *
     * @param options The {@link FirestoreRecyclerOptions} for {@link ChatroomModel}.
     * @param context The {@link Context} of the parent Activity or Fragment.
     */
    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * This method initiates an asynchronous fetch of the other user's profile details.
     * It then binds the room's last message, sender info, and profile picture to the UI.
     *
     * @param holder The {@link ChatroomModelViewHolder} to update.
     * @param position The position of the item within the adapter.
     * @param model The {@link ChatroomModel} containing the room metadata.
     *
     * @implNote This method initiates an asynchronous Firestore operation; the UI is updated
     * via the supplied callback on the main thread.
     */
    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                   if(task.isSuccessful()) {
                       boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());

                       UserModel otherUserModel = task.getResult().toObject(UserModel.class);

                       String profilePicUrl = otherUserModel.getProfilePicUrl();

                       if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                           Uri imageUri = Uri.parse(profilePicUrl);
                           AndroidUtil.setProfilePic(context, imageUri, holder.profilePic);
                       } else {
                           holder.profilePic.setImageResource(R.drawable.person_icon);
                       }

                       if(otherUserModel.getUserId().equals(FirebaseUtil.currentUserId())) {
                           holder.usernameText.setText(otherUserModel.getUsername() + " (Me)");
                       }else {
                           holder.usernameText.setText(otherUserModel.getUsername());
                       }

                       if(lastMessageSentByMe) {
                           holder.lastMessageText.setText("You: " + model.getLastMessage());
                       }else {
                           holder.lastMessageText.setText(model.getLastMessage());
                       }

                       holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                       holder.itemView.setOnClickListener(view -> {
                           Intent intent = new Intent(context, ChatActivity.class);
                           AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                           context.startActivity(intent);
                       });
                   }
                });
    }

    /**
     * Called by RecyclerView to create a new ViewHolder.
     *
     * @param parent The {@link ViewGroup} into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new {@link ChatroomModelViewHolder} that holds the {@code recent_chat_recycler_row} view.
     */
    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    /**
     * ViewHolder class for recent chat room items.
     *
     * Holds references to views within the {@code recent_chat_recycler_row.xml} layout.
     */
    class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
        /** Displays the other user's name. Binds to {@code user_name_text}. */
        TextView usernameText;
        /** Displays the content of the last message. Binds to {@code last_message_text}. */
        TextView lastMessageText;
        /** Displays the time of the last message. Binds to {@code last_message_time_text}. */
        TextView lastMessageTime;
        /** Displays the other user's profile picture. Binds to {@code profile_pic_image_view}. */
        ImageView profilePic;

        /**
         * Constructs a new ChatroomModelViewHolder.
         *
         * @param itemView The root view of the recent chat row layout.
         */
        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }

}
