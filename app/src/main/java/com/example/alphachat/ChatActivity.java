package com.example.alphachat;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alphachat.adapter.ChatRecyclerAdapter;
import com.example.alphachat.adapter.SearchUserRecyclerAdapter;
import com.example.alphachat.model.ChatMessageModel;
import com.example.alphachat.model.ChatroomModel;
import com.example.alphachat.model.UserModel;
import com.example.alphachat.utils.AndroidUtil;
import com.example.alphachat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Firebase;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Activity for direct messaging between two users.
 *
 * This activity facilitates a real-time chat interface. It manages chat room creation,
 * message persistence in Firestore, and UI updates for incoming and outgoing messages.
 * It also handles keyboard-aware padding for the message input area.
 *
 * Cloud Firestore {@code chatrooms} collection and {@code chats} sub-collection.
 */
public class ChatActivity extends AppCompatActivity {

    /** The model of the user being chatted with. */
    UserModel otherUser;
    /** The deterministic ID of the chat room between the two users. */
    String chatroomId;
    /** The model representing the chat room metadata. */
    ChatroomModel chatroomModel;
    /** Adapter for displaying chat messages. */
    ChatRecyclerAdapter adapter;

    /** Input field for typing new messages. Binds to {@code chat_message_input}. */
    EditText messageInput;
    /** Button to send the typed message. Binds to {@code message_send_btn}. */
    ImageButton sendMessageBtn;
    /** Button to navigate back. Binds to {@code back_btn}. */
    ImageButton backBtn;
    /** Displays the name of the other participant. Binds to {@code other_username}. */
    TextView otherUsername;
    /** Displays the other participant's profile picture. Binds to {@code profile_pic_image_view}. */
    ImageView profilePic;
    /** RecyclerView for the conversation history. Binds to {@code chat_recycler_view}. */
    RecyclerView recyclerView;

    /**
     * Called when the activity is first created.
     *
     * Initializes UI components, sets up keyboard-responsive padding, extracts the target
     * user from the intent, and initiates chat room and message listeners.
     *
     * @param savedInstanceState If non-null, this activity is being re-constructed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            int bottomPadding = Math.max(systemBars.bottom, imeInsets.bottom);
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding);
            return insets;
        });

        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), otherUser.getUserId());
        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        profilePic = findViewById(R.id.profile_pic_image_view);

        // 2. NEW RECYCLERVIEW SETUP: Forces messages to the bottom and scrolls up with keyboard
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        backBtn.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        otherUsername.setText(otherUser.getUsername());

        String profilePicUrl = otherUser.getProfilePicUrl();

        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            Uri imageUri = Uri.parse(profilePicUrl);
            AndroidUtil.setProfilePic(this, imageUri, profilePic);
        }

        sendMessageBtn.setOnClickListener(view -> {
            String message = messageInput.getText().toString().trim();
            messageInput.setText("");
            if(message.isEmpty()) return;
            sendMessageToUser(message);
        });

        getOrCreateChatroomModel();
        setupChatRecyclerView();
    }

    /**
     * Configures the RecyclerView with a real-time Firestore query for messages.
     *
     * Uses {@link ChatRecyclerAdapter} and a reversed {@link LinearLayoutManager} to
     * show messages in reverse chronological order, scrolling to the bottom on new inserts.
     */
    void setupChatRecyclerView(){
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    /**
     * Sends a chat message to the target user.
     *
     * Updates the parent chat room document with the last message metadata and
     * adds a new document to the {@code chats} sub-collection.
     *
     * @param message The text content of the message to send.
     *
     * @implNote This method initiates an asynchronous Firestore operation; the UI is updated
     * via the supplied callback on the main thread.
     */
    void sendMessageToUser(String message) {
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroomModel.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(task -> {
                   if(task.isSuccessful()) {
                       messageInput.setText("");
                   }
                });
    }

    /**
     * Retrieves existing chat room metadata or creates a new one if it doesn't exist.
     *
     * @implNote This method initiates an asynchronous Firestore operation; the UI is updated
     * via the supplied callback on the main thread.
     */
    void getOrCreateChatroomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
           if(task.isSuccessful()) {
               chatroomModel = task.getResult().toObject(ChatroomModel.class);
               if(chatroomModel == null) {
                   chatroomModel = new ChatroomModel(
                           chatroomId,
                           Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserId()),
                           Timestamp.now(),
                           ""
                   );
                   FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
               }
           }
        });
    }

}