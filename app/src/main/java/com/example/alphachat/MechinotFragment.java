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

/**
 * Fragment for browsing and filtering Mechinot (academies).
 *
 * This fragment provides a search interface with text input and multiple spinners for
 * filtering academies by region, gender orientation, and religious/secular type.
 * Data is loaded locally via {@link JsonReader} and managed within a RecyclerView.
 */
public class MechinotFragment extends Fragment {

    /** The search bar for text-based filtering by academy name. Binds to {@code search_input}. */
    private SearchView searchInput;
    /** The list displaying the filtered results. Binds to {@code search_recycler_view}. */
    private RecyclerView recyclerView;
    /** Filter for geographic location. Binds to {@code spinner_region}. */
    private Spinner spinnerRegion;
    /** Filter for gender audience. Binds to {@code spinner_gender}. */
    private Spinner spinnerGender;
    /** Filter for academy type. Binds to {@code spinner_type}. */
    private Spinner spinnerType;

    /** The master list of all academies loaded from the local JSON. */
    private List<Mechina> allMechinot = new ArrayList<>();
    /** The subset of academies that match current search and filter criteria. */
    private final List<Mechina> filteredList = new ArrayList<>();
    /** The adapter for binding the filtered list to the RecyclerView. */
    private MechinaAdapter adapter;

    /**
     * Required empty public constructor for fragment instantiation.
     */
    public MechinotFragment() {
        // Required empty public constructor
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The {@link LayoutInflater} object to inflate views.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mechinot, container, false);
    }

    /**
     * Called immediately after {@code onCreateView} has returned.
     *
     * Initializes UI components, sets up spinners, loads academy data from local JSON,
     * and configures the search listeners.
     *
     * @param view The View returned by {@code onCreateView}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed.
     */
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

    /**
     * Configures the spinner filters with predefined Hebrew options.
     *
     * Sets up selection listeners on all spinners to trigger {@link #applyFilters()}
     * whenever a value is changed.
     */
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

    /**
     * Helper method to bind a simple array adapter to a spinner.
     *
     * @param spinner The {@link Spinner} view to configure.
     * @param options The array of string options to display.
     */
    private void bindSpinnerAdapter(Spinner spinner, String[] options) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, options);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
    }

    /**
     * Filters the {@code allMechinot} list based on the current state of UI inputs.
     *
     * Updates {@code filteredList} and notifies the adapter of the data change.
     * Criteria include name search (contains, case-insensitive), region, gender, and type.
     */
    private void applyFilters() {
        String query = searchInput.getQuery().toString().toLowerCase().trim();
        String selectedRegion = spinnerRegion.getSelectedItem().toString();
        String selectedGender = spinnerGender.getSelectedItem().toString();
        String selectedType = spinnerType.getSelectedItem().toString();

        filteredList.clear();

        for (Mechina item : allMechinot) {
            boolean matchesSearch = query.isEmpty() || item.getName().toLowerCase().contains(query);
            boolean matchesRegion = selectedRegion.equals("כל האזורים") || item.getRegion().contains(selectedRegion);
            boolean matchesGender = selectedGender.equals("כל המגדרים") || item.getGender().contains(selectedGender);
            boolean matchesType = selectedType.equals("כל הסוגים") || item.getType().contains(selectedType);

            if (matchesSearch && matchesRegion && matchesGender && matchesType) {
                filteredList.add(item);
            }
        }

        adapter.notifyDataSetChanged();
    }
}