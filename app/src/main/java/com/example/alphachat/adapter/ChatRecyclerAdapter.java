package com.example.alphachat.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alphachat.ChatActivity;
import com.example.alphachat.R;
import com.example.alphachat.model.ChatMessageModel;
import com.example.alphachat.utils.AndroidUtil;
import com.example.alphachat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import org.w3c.dom.Text;

/**
 * Adapter for displaying chat messages in a conversation.
 *
 * This class uses {@link FirestoreRecyclerAdapter} to provide real-time updates from
 * the {@code messages} sub-collection in Firestore. It alternates between left and
 * right chat bubbles based on the sender's identity.
 *
 * Cloud Firestore {@code chatrooms/messages} sub-collection.
 */
public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {

    /** The context of the activity where the chat is displayed. */
    Context context;

    /**
     * Constructs a new ChatRecyclerAdapter.
     *
     * @param options The {@link FirestoreRecyclerOptions} for {@link ChatMessageModel}.
     * @param context The {@link Context} of the parent Activity.
     */
    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * Toggles visibility of left (incoming) and right (outgoing) chat layouts based
     * on whether the message was sent by the current user.
     *
     * @param holder The {@link ChatModelViewHolder} to update.
     * @param position The position of the item within the adapter.
     * @param model The {@link ChatMessageModel} containing the message data.
     */
    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        if(model.getSenderId().equals(FirebaseUtil.currentUserId())) {
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextView.setText(model.getMessage());
        }else {
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatTextView.setText(model.getMessage());
        }
    }

    /**
     * Called by RecyclerView to create a new ViewHolder.
     *
     * @param parent The {@link ViewGroup} into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new {@link ChatModelViewHolder} that holds the {@code chat_message_recycler_row} view.
     */
    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row, parent, false);
        return new ChatModelViewHolder(view);
    }

    /**
     * ViewHolder class for chat message items.
     *
     * Holds references to both incoming and outgoing chat bubble layouts within
     * {@code chat_message_recycler_row.xml}.
     */
    class ChatModelViewHolder extends RecyclerView.ViewHolder {

        /** The layout container for incoming messages. Binds to {@code left_chat_layout}. */
        LinearLayout leftChatLayout;
        /** The layout container for outgoing messages. Binds to {@code right_chat_layout}. */
        LinearLayout rightChatLayout;
        /** The TextView for incoming message text. Binds to {@code left_chat_textview}. */
        TextView leftChatTextView;
        /** The TextView for outgoing message text. Binds to {@code right_chat_textview}. */
        TextView rightChatTextView;

        /**
         * Constructs a new ChatModelViewHolder.
         *
         * @param itemView The root view of the message row layout.
         */
        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextView = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextView = itemView.findViewById(R.id.right_chat_textview);
        }
    }

}
