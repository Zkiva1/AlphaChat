package com.example.alphachat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alphachat.adapter.SearchUserRecyclerAdapter;
import com.example.alphachat.model.Mechina;
import com.example.alphachat.model.UserModel;
import com.example.alphachat.utils.AndroidUtil;
import com.example.alphachat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

/**
 * Fragment that displays a list of students (Mechinists) for a specific academy.
 *
 * This fragment filters the {@code users} collection by occupation ("מכיניסט") and
 * academy name. It uses {@link SearchUserRecyclerAdapter} to display the results
 * in real-time.
 *
 * Cloud Firestore {@code users} collection.
 */
public class StudantsFragment extends Fragment {

    /** The adapter for displaying students in the RecyclerView. */
    private SearchUserRecyclerAdapter adapter;
    /** The RecyclerView displaying the student list. Binds to {@code studants_recycler_view}. */
    private RecyclerView recyclerView;
    /** The academy model used to filter students. */
    private Mechina mechinaModel;

    /**
     * Required empty public constructor for fragment instantiation.
     */
    public StudantsFragment() {
        // Required empty public constructor
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * Retrieves the {@link Mechina} model from the fragment arguments.
     *
     * @param inflater The {@link LayoutInflater} to inflate views.
     * @param container The parent view to attach the UI to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_studants, container, false);
        if (getArguments() != null) {
            mechinaModel = (Mechina) getArguments().getSerializable("mechina_model");
        }
        return view;
    }

    /**
     * Called immediately after {@code onCreateView} has returned.
     *
     * Initializes the RecyclerView and triggers the data setup if the academy model
     * is available.
     *
     * @param view The View returned by {@code onCreateView}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Initialize the RecyclerView
        // Note: Check your XML to make sure the ID matches
        recyclerView = view.findViewById(R.id.studants_recycler_view);

        // 2. Only run the query if we successfully received the Mechina object
        if (mechinaModel != null) {
            setupSearchRecyclerView();
        }
    }

    /**
     * Configures the RecyclerView with a Firestore query for students of the academy.
     *
     * Sets up a query for users with occupation "מכיניסט" belonging to the specific
     * Mechina.
     */
    private void setupSearchRecyclerView() {
        Query query = FirebaseUtil.allUserCollectionReference()
                .whereEqualTo("occupation", "מכיניסט")
                .whereEqualTo("mechina", mechinaModel.getName());

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .build();

        if (adapter == null) {
            adapter = new SearchUserRecyclerAdapter(options, requireContext());
            adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);

            recyclerView.setLayoutManager(new AndroidUtil.SafeLinearLayoutManager(requireContext()));

            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateOptions(options);
        }
    }

    /**
     * Called when the Fragment is visible to the user.
     *
     * Starts the Firestore adapter listening for real-time updates.
     */
    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    /**
     * Called when the Fragment is no longer started.
     *
     * Stops the Firestore adapter from listening.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

}