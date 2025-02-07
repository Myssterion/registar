package com.registar.ui.location;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.registar.R;
import com.registar.data.model.Location;
import com.registar.ui.location.LocationViewModel;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class LocationDetailFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private Marker selectedMarker;
    private LocationViewModel locationViewModel;
    private EditText locationName;
    private EditText locationAddress;
    private EditText latitude;
    private EditText longitude;
    private Button saveButton;
    private boolean isAddingMode = false;
    private TextWatcher textWatcher;
    private Location currLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        View root = inflater.inflate(R.layout.fragment_location_detail, container, false);

        locationName = root.findViewById(R.id.text_location_name);
        locationAddress = root.findViewById(R.id.text_location_address);
        latitude = root.findViewById(R.id.text_location_latitude);
        longitude = root.findViewById(R.id.text_location_longitude);
        saveButton = root.findViewById(R.id.button_location_save);
        saveButton.setEnabled(false);
        createTextWatcher();
        setTextWatcherToFields();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        saveButton.setOnClickListener(listener -> {
            saveData();
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(locationViewModel.getTitle().getValue());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);
        if (getArguments() != null && getArguments().containsKey("isUpdateMode") && getArguments().containsKey("location")) {
            boolean isUpdateMode = getArguments().getBoolean("isUpdateMode");
            locationViewModel.setUpdateMode(isUpdateMode);
            Location loc = (Location) getArguments().getSerializable("location");
            locationViewModel.setLocation(loc);

            if(loc != null) {
                if(isUpdateMode)
                    locationViewModel.setTitle(getString(R.string.location_detail_update));
                else
                    locationViewModel.setTitle(getString(R.string.location_detail_info));
            }
            else {
                locationViewModel.setTitle(getString(R.string.location_detail_add));
            }
        }

        checkAndChangeIfViewMode(Boolean.TRUE.equals(locationViewModel.getUpdateMode().getValue()));

        locationViewModel.getLocation().observe(getViewLifecycleOwner(), location -> {
            if (location != null) {
                currLocation = location;
                locationName.setText(location.getLocationName());
                locationAddress.setText(location.getAddress());
                latitude.setText(String.valueOf(location.getLatitude()));
                longitude.setText(String.valueOf(location.getLongitude()));
                updateMapWithCoordinates(currLocation.getLatitude(), currLocation.getLongitude());
            }
        });
    }

    public void checkAndChangeIfViewMode(boolean isUpdateMode) {
        if(!isUpdateMode)  {
            locationName.setEnabled(false);
            latitude.setEnabled(false);
            longitude.setEnabled(false);
            locationAddress.setEnabled(false);
            saveButton.setVisibility(View.GONE);
        }
    }

    private void saveData() {
        String newName = locationName.getText().toString();
        String newAddress = locationAddress.getText().toString();
        double newLatitude = latitude.getText().toString().isBlank() ? 0 : Double.parseDouble(latitude.getText().toString());
        double newLongitude = longitude.getText().toString().isBlank() ? 0 : Double.parseDouble(longitude.getText().toString());
        if (locationViewModel.getLocation().getValue() == null) {
            // Add new location logic
            Location newLocation = new Location(newName, newAddress, newLatitude, newLongitude);
            locationViewModel.addLocation(newLocation);
        } else {
            // Update location data in the ViewModel
            long id = locationViewModel.getLocation().getValue().getLocationId();
            Location location = new Location(id, newName, newAddress, newLatitude, newLongitude);
            locationViewModel.updateLocation(location);

        }
    }

    private void createTextWatcher() {
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Check if all fields are valid
               checkFields();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private void setTextWatcherToFields() {
        locationName.addTextChangedListener(textWatcher);
        locationAddress.addTextChangedListener(textWatcher);
        latitude.addTextChangedListener(textWatcher);
        longitude.addTextChangedListener(textWatcher);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
         googleMap = map;

        // Set a default location (e.g., New York City)
        LatLng defaultLocation = new LatLng(40.7128, -74.0060);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));

        // Set a click listener to select a location
        googleMap.setOnMapClickListener(latLng -> {
            if(Boolean.TRUE.equals(locationViewModel.getUpdateMode().getValue())) {
                if (selectedMarker != null) {
                    selectedMarker.remove();
                }

                // Add a marker at the clicked location
                selectedMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));

                // Get location details
                setLocationDetails(latLng);
            }
        });

        if (currLocation != null) {
            updateMapWithCoordinates(currLocation.getLatitude(), currLocation.getLongitude());
        }
    }

    private void setLocationDetails(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String locality = address.getLocality();
                String addressLine = address.getAddressLine(0);

                locationName.setText(locality);
                locationAddress.setText(addressLine);
                longitude.setText(String.valueOf(latLng.longitude));
                latitude.setText(String.valueOf(latLng.latitude));
               // checkFields();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkFields() {
        String locationNameValue = locationName.getText().toString().trim();
        String locationAddressValue = locationAddress.getText().toString().trim();
        String latitudeValue = latitude.getText().toString().trim();
        String longitudeValue = longitude.getText().toString().trim();

        boolean isNameValid = !locationNameValue.isEmpty(); // Add more checks if needed
        boolean isLocationValid = !locationAddressValue.isEmpty();
        boolean isLongitudeValid = !longitudeValue.isEmpty() && longitudeValue.matches("^-?\\d+(\\.\\d+)?$"); // Check for a valid number
        boolean isLatitudeValid = !latitudeValue.isEmpty() && latitudeValue.matches("^-?\\d+(\\.\\d+)?$");
        // Enable or disable the button
        saveButton.setEnabled(isNameValid && isLongitudeValid && isLatitudeValid && isLocationValid);
    }

    public void updateMapWithCoordinates(double lat, double lng) {
        if (googleMap != null) {
            // Move the camera to the new position
            LatLng location = new LatLng(lat, lng);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));  // Zoom level 15 for example

            // Optionally, add a marker at the new location
            selectedMarker = googleMap.addMarker(new MarkerOptions().position(location).title("New Location"));
        }
    }
}