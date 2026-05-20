package com.example.alphachat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alphachat.adapter.MechinaAdapter;
import com.example.alphachat.model.Mechina;
import java.util.ArrayList;
import java.util.List;

public class MechinotFragment extends Fragment {

    private SearchView searchInput;
    private RecyclerView recyclerView;
    private Spinner spinnerRegion, spinnerGender, spinnerType;

    private List<Mechina> allMechinot = new ArrayList<>();
    private final List<Mechina> filteredList = new ArrayList<>();
    private MechinaAdapter adapter;

    public MechinotFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mechinot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Instantiate components using the root layout 'view'
        searchInput = view.findViewById(R.id.search_input);
        recyclerView = view.findViewById(R.id.search_recycler_view);
        spinnerRegion = view.findViewById(R.id.spinner_region);
        spinnerGender = view.findViewById(R.id.spinner_gender);
        spinnerType = view.findViewById(R.id.spinner_type);

        // Initialize UI display parameters
        setupSpinners();

        // Execute the file deserialization utility passing context safely
        allMechinot = JsonReader.convertJsonToObject(requireContext());
        filteredList.addAll(allMechinot);

        // Bind the adapter structure
        adapter = new MechinaAdapter(filteredList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Query input mutations listener
        searchInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyFilters();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilters();
                return true;
            }
        });

    }

    private void setupSpinners() {
        String[] regions = {"כל האזורים", "צפון", "דרום", "מרכז/שפלה", "עמקים", "ערבה"};
        String[] genders = {"כל המגדרים", "בנים", "בנות", "מעורבת"};
        String[] types = {"כל הסוגים", "תורנית", "חילונית", "כללית/מעורבת"};

        bindSpinnerAdapter(spinnerRegion, regions);
        bindSpinnerAdapter(spinnerGender, genders);
        bindSpinnerAdapter(spinnerType, types);

        AdapterView.OnItemSelectedListener selectionListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerRegion.setOnItemSelectedListener(selectionListener);
        spinnerGender.setOnItemSelectedListener(selectionListener);
        spinnerType.setOnItemSelectedListener(selectionListener);
    }

    private void bindSpinnerAdapter(Spinner spinner, String[] options) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, options);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
    }

    private void applyFilters() {
        String query = searchInput.getQuery().toString().toLowerCase().trim();
        String selectedRegion = spinnerRegion.getSelectedItem().toString();
        String selectedGender = spinnerGender.getSelectedItem().toString();
        String selectedType = spinnerType.getSelectedItem().toString();

        filteredList.clear();

        for (Mechina item : allMechinot) {
            boolean matchesSearch = query.isEmpty() || item.getName().toLowerCase().contains(query);
            boolean matchesRegion = selectedRegion.equals("כל האזורים") || item.getRegion().contains(selectedRegion);

            // Contains handles multi-value string subsets within the text attributes safely
            boolean matchesGender = selectedGender.equals("כל המגדרים") || item.getGender().contains(selectedGender);
            boolean matchesType = selectedType.equals("כל הסוגים") || item.getType().contains(selectedType);

            if (matchesSearch && matchesRegion && matchesGender && matchesType) {
                filteredList.add(item);
            }
        }

        // Push dataset updates safely to structural layout frames
        adapter.notifyDataSetChanged();
    }
}