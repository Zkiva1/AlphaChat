package com.example.alphachat;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alphachat.adapter.SearchUserRecyclerAdapter;
import com.example.alphachat.model.Mechina;
import com.example.alphachat.model.UserModel;
import com.example.alphachat.utils.AndroidUtil;
import com.example.alphachat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class StudantsFragment extends Fragment {

    private SearchUserRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private Mechina mechinaModel;

    public StudantsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_studants, container, false);
        if (getArguments() != null) {
            mechinaModel = (Mechina) getArguments().getSerializable("mechina_model");
        }
        return view;
    }

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

    private void setupSearchRecyclerView() {
        // 3. Use whereEqualTo for exact matches (Firestore rules block multiple range filters)
        Query query = FirebaseUtil.allUserCollectionReference()
                .whereEqualTo("occupation", "מכיניסט")
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

    // 6. Lifecycle methods must be public
    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

}