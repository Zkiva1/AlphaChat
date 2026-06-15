package com.example.alphachat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alphachat.adapter.RecentChatRecyclerAdapter;
import com.example.alphachat.model.ChatroomModel;
import com.example.alphachat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;


/**
 * Fragment that displays the list of recent chat conversations for the current user.
 *
 * This fragment manages a RecyclerView populated with active chat rooms where the
 * user is a participant. It utilizes the {@link RecentChatRecyclerAdapter} to
 * provide real-time updates of conversation previews.
 *
 * Cloud Firestore {@code chatrooms} collection.
 */
public class ChatFragment extends Fragment {

    /** The RecyclerView displaying the list of recent chats. Binds to {@code recent_chats_recycler_view}. */
    RecyclerView recyclerView;
    /** The adapter used to bind chat room data to the RecyclerView. */
    RecentChatRecyclerAdapter adapter;

    /**
     * Required empty public constructor for fragment instantiation.
     */
    public ChatFragment() {

    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * Initializes the RecyclerView and triggers the data setup.
     *
     * @param inflater The {@link LayoutInflater} object to inflate views in the fragment.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recent_chats_recycler_view);
        setupRecyclerView();

        return view;
    }

    /**
     * Configures the RecyclerView with a Firestore query and adapter.
     *
     * Sets up a query for chat rooms containing the current user's ID, ordered by
     * the most recent message timestamp.
     */
    void setupRecyclerView(){

        Query query = FirebaseUtil.allChatroomCollectionReference()
                .whereArrayContains("userIds", FirebaseUtil.currentUserId())
                .orderBy("lastMessageTimestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query,ChatroomModel.class).build();

        adapter = new RecentChatRecyclerAdapter(options,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    /**
     * Called when the Fragment is visible to the user.
     *
     * Starts the Firestore adapter listening for real-time updates.
     */
    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    /**
     * Called when the Fragment is no longer started.
     *
     * Stops the Firestore adapter from listening to conserve resources.
     */
    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    /**
     * Called when the fragment is visible and actively running.
     *
     * Refreshes the adapter data to ensure the UI is in sync.
     */
    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }
}