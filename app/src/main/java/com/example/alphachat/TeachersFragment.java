package com.example.alphachat;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alphachat.adapter.SearchUserRecyclerAdapter;
import com.example.alphachat.model.Mechina;
import com.example.alphachat.model.UserModel;
import com.example.alphachat.utils.AndroidUtil;
import com.example.alphachat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

/**
 * Fragment that displays a list of teachers (Staff) for a specific academy.
 *
 * This fragment filters the {@code users} collection by occupation ("ר\"מ או מדריך")
 * and academy name. It uses {@link SearchUserRecyclerAdapter} to display the results
 * in real-time.
 *
 * Cloud Firestore {@code users} collection.
 */
public class TeachersFragment extends Fragment {

    /** The adapter for displaying teachers in the RecyclerView. */
    private SearchUserRecyclerAdapter adapter;
    /** The RecyclerView displaying the teacher list. Binds to {@code teachers_recycler_view}. */
    private RecyclerView recyclerView;
    /** The academy model used to filter staff. */
    private Mechina mechinaModel;

    /**
     * Required empty public constructor for fragment instantiation.
     */
    public TeachersFragment() {
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
        View view = inflater.inflate(R.layout.fragment_teachers, container, false);
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
        recyclerView = view.findViewById(R.id.teachers_recycler_view);

        // 2. Only run the query if we successfully received the Mechina object
        if (mechinaModel != null) {
            setupSearchRecyclerView();
        }
    }

    /**
     * Configures the RecyclerView with a Firestore query for staff of the academy.
     *
     * Sets up a query for users with occupation "ר\"מ או מדריך" belonging to the
     * specific Mechina.
     */
    private void setupSearchRecyclerView() {
        // 3. Use whereEqualTo for exact matches (Firestore rules block multiple range filters)
        Query query = FirebaseUtil.allUserCollectionReference()
                .whereEqualTo("occupation", "ר\"מ או מדריך")
                .whereEqualTo("mechina", mechinaModel.getName());

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .build();

        if (adapter == null) {
            // 4. Pass requireContext() to the adapter
            adapter = new SearchUserRecyclerAdapter(options, requireContext());
            adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);

            // 5. Correctly instantiate the LayoutManager
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