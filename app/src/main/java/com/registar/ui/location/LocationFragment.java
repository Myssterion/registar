package com.registar.ui.location;

import static com.registar.util.Constants.OPTION_DELETE;
import static com.registar.util.Constants.OPTION_DETAILS;
import static com.registar.util.Constants.OPTION_UPDATE;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.registar.R;
import com.registar.adapter.LocationAdapter;
import com.registar.data.model.Employee;
import com.registar.data.model.Location;

public class LocationFragment extends Fragment implements LocationAdapter.OnLocationItemClick {

    private LocationViewModel locationViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private LocationAdapter locationAdapter;
    private EditText filterByName;
    private EditText filterByAddress;
    private FloatingActionButton addLocation;
    public static LocationFragment newInstance() {
        return new LocationFragment();
    }
    //treba filterlayout za scroll down da sakrije filter ako budem dodavao
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        locationViewModel =
                new ViewModelProvider(this).get(LocationViewModel.class);

        View root = inflater.inflate(R.layout.fragment_location, container, false);
        filterByName = root.findViewById(R.id.text_filter_location_name);
        filterByAddress = root.findViewById(R.id.text_filter_location_address);
        addLocation = root.findViewById(R.id.button_location_add);

        recyclerView = root.findViewById(R.id.recycler_location);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        locationAdapter = new LocationAdapter(this);
        recyclerView.setAdapter(locationAdapter);

        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigate(null, true);
            }
        });

        locationViewModel.getAllLocations().observe(getViewLifecycleOwner(), items -> {
            locationAdapter.submitList(items);
        });

        filterByName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filter(filterByName, filterByAddress);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        filterByAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filter(filterByName, filterByAddress);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        return root;
    }

    @Override
    public void onLocationClick(int pos) {
        final Location location = locationAdapter.getItemAt(pos);
        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.select))
                .setItems(new String[]{getString(R.string.details), getString(R.string.update), getString(R.string.delete)}, (dialogInterface, i) -> {
                    switch (i) {
                        case OPTION_DETAILS:
                            navigate(location, false);
                            break;
                        case OPTION_UPDATE:
                            navigate(location, true);
                            break;
                        case OPTION_DELETE:
                            locationViewModel.deleteItem(location);
                            break;
                    }
                }).show();
    }

    private void filter(EditText filterByName, EditText filterByAddress) {
        String nameQuery = filterByName.getText().toString();
        String adressQuery = filterByAddress.getText().toString();

        locationViewModel.filter(nameQuery, adressQuery);
    }

    private void navigate(Location location, boolean isUpdate) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isUpdateMode", isUpdate);
        bundle.putSerializable("location", location);
        navController.navigate(R.id.action_nav_locations_to_locationDetailFragment, bundle);
    }
}