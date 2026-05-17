package com.example.alphachat;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.alphachat.model.Mechina;
import java.util.ArrayList;
import java.util.List;

public class SearchUserActivity extends AppCompatActivity {

    private SearchView searchInput;
    private ImageButton backButton;
    private RecyclerView recyclerView;
    private Spinner spinnerRegion, spinnerGender, spinnerType;

    private List<Mechina> allMechinot = new ArrayList<>();
    private final List<Mechina> filteredList = new ArrayList<>();
    private MechinaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_user);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Instantiate components
        searchInput = findViewById(R.id.search_input);
        backButton = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.search_recycler_view);
        spinnerRegion = findViewById(R.id.spinner_region);
        spinnerGender = findViewById(R.id.spinner_gender);
        spinnerType = findViewById(R.id.spinner_type);

        // Initialize UI display parameters
        setupSpinners();

        // Execute the file deserialization utility
        allMechinot = JsonReader.convertJsonToObject(this);
        filteredList.addAll(allMechinot);

        // Bind the adapter structure
        adapter = new MechinaAdapter(filteredList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

        backButton.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
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
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
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
            boolean matchesSearch = query.isEmpty() || item.name.toLowerCase().contains(query);
            boolean matchesRegion = selectedRegion.equals("כל האזורים") || item.region.contains(selectedRegion);

            // Contains handles multi-value string subsets within the text attributes safely
            boolean matchesGender = selectedGender.equals("כל המגדרים") || item.gender.contains(selectedGender);
            boolean matchesType = selectedType.equals("כל הסוגים") || item.type.contains(selectedType);

            if (matchesSearch && matchesRegion && matchesGender && matchesType) {
                filteredList.add(item);
            }
        }

        // Push dataset updates safely to structural layout frames
        adapter.notifyDataSetChanged();
    }
}