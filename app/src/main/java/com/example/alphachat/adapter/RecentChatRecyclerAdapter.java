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

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }

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

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText, lastMessageText, lastMessageTime;
        ImageView profilePic;
        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }

}
