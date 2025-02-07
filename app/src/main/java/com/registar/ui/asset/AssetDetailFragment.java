package com.registar.ui.asset;

import static com.registar.util.Constants.OPTION_DELETE;
import static com.registar.util.Constants.OPTION_DETAILS;
import static com.registar.util.Constants.OPTION_GALLERY;
import static com.registar.util.Constants.OPTION_TAKE_PHOTO;
import static com.registar.util.Constants.OPTION_UPDATE;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.registar.R;
import com.registar.data.model.AssetRecordWithDetails;
import com.registar.data.model.Employee;
import com.registar.data.model.FixedAsset;
import com.registar.data.model.Location;
import com.registar.ui.asset.AssetViewModel;
import com.registar.util.BarcodeUtil;
import com.registar.util.ImageUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssetDetailFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private Marker selectedMarker;
    private AssetViewModel fixedAssetViewModel;
    private EditText assetName;
    private EditText assetDescription;
    private EditText assetBarcode;
    private EditText assetPrice;
    private EditText assetDate;
    private Spinner assetEmployee;
    private Spinner assetLocation;
    private Button saveButton;
    private ImageView assetImage;
    private TextWatcher textWatcher;
    private Employee selectedEmployee;
    private Location selectedLocation;
    private FixedAsset asset;
    private List<Employee> employeeList;
    private List<Location> locationList;
    private LinearLayout mapContainer;
    private Employee currEmployee;
    private Location currLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fixedAssetViewModel = new ViewModelProvider(this).get(AssetViewModel.class);

        View root = inflater.inflate(R.layout.fragment_asset_detail, container, false);
        assetName = root.findViewById(R.id.text_asset_name);
        assetDescription = root.findViewById(R.id.text_asset_description);
        assetBarcode = root.findViewById(R.id.text_asset_barcode);
        assetPrice = root.findViewById(R.id.text_asset_price);
        assetDate = root.findViewById(R.id.text_asset_date);
        assetEmployee = root.findViewById(R.id.spinner_asset_employee);
        assetLocation = root.findViewById(R.id.spinner_asset_location);
        assetImage = root.findViewById(R.id.image_view_asset_image);
        mapContainer = root.findViewById(R.id.map_container);
        mapContainer.setVisibility(View.GONE);
        saveButton = root.findViewById(R.id.button_asset_save);
        saveButton.setEnabled(false);
        createTextWatcher();
        setTextWatcherToFields();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        assetDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
                // Format the selected date
                String formattedDate = String.format(Locale.UK, "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                assetDate.setText(formattedDate);
            }, year, month, day);
            datePickerDialog.show();
        });

        assetImage.setOnClickListener(listener -> {
            ImageUtil.showImageDialog(requireContext());
        });

        assetBarcode.setOnClickListener(listener -> {
            if(!assetBarcode.isFocusable())
                BarcodeUtil.showBarcodeDialog(assetBarcode);
        });

        initLaunchers();

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
            activity.getSupportActionBar().setTitle(fixedAssetViewModel.getTitle().getValue());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);
        if (getArguments() != null && getArguments().containsKey("isUpdateMode") && getArguments().containsKey("asset")) {
            boolean isUpdateMode = getArguments().getBoolean("isUpdateMode");
            fixedAssetViewModel.setUpdateMode(isUpdateMode);
            asset = (FixedAsset) getArguments().getSerializable("asset");
            fixedAssetViewModel.setFixedAsset(asset);
            if(asset != null) {
                if(isUpdateMode)
                     fixedAssetViewModel.setTitle(getString(R.string.asset_detail_update));
                else
                    fixedAssetViewModel.setTitle(getString(R.string.asset_detail_info));
            }
            else {
                fixedAssetViewModel.setTitle(getString(R.string.asset_detail_add));
            }
        }

        checkAndChangeIfViewMode(Boolean.TRUE.equals(fixedAssetViewModel.getUpdateMode().getValue()));

        fixedAssetViewModel.getFixedAsset().observe(getViewLifecycleOwner(), fixedAsset -> {
            if (fixedAsset != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                assetName.setText(fixedAsset.getAssetName());
                assetDescription.setText(fixedAsset.getDescription());
                assetBarcode.setText(String.valueOf(fixedAsset.getBarcode()));
                assetPrice.setText(String.valueOf(fixedAsset.getPrice()));
                assetDate.setText(fixedAsset.getCreatedAt().format(formatter));
                new Thread(() -> {
                    ImageUtil.imagePath = fixedAsset.getImagePath();
                    Uri imageUri = Uri.parse(fixedAsset.getImagePath());
                    requireActivity().runOnUiThread(() -> assetImage.setImageURI(imageUri));
                }).start();
                preselectItems(asset);
                //dodaj iz dropdown list i uri
            }
        });

        fixedAssetViewModel.getAllEmployees().observe(getViewLifecycleOwner(), items -> {
            employeeList = new ArrayList<>();
            employeeList.add(new Employee(getString(R.string.select_employee), "", 0));  // Assuming Employee has a constructor like this

            // Add the rest of the employees
            employeeList.addAll(items);

            ArrayAdapter<Employee> adapter = new ArrayAdapter<Employee>(requireContext(), android.R.layout.simple_spinner_item, employeeList) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    // Customize how items are displayed in the spinner
                    TextView textView = (TextView) super.getView(position, convertView, parent);
                    textView.setText(getItem(position).getName() + " " + getItem(position).getSurname());
                    return textView;
                }

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    // Customize dropdown view
                    TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                    textView.setText(getItem(position).getName() + " " + getItem(position).getSurname());
                    return textView;
                }
            };
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            assetEmployee.setAdapter(adapter);

            assetEmployee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    selectedEmployee = employeeList.get(position);  // This gets the selected Employee object
                    checkFields();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Handle the case when nothing is selected (optional)
                }
            });
            preselectItems(asset);
        });

        fixedAssetViewModel.getAllLocations().observe(getViewLifecycleOwner(), items -> {
            locationList = new ArrayList<>();
            locationList.add(new Location(getString(R.string.select_location), "", 0.0, 0.0));

            locationList.addAll(items);

            ArrayAdapter<Location> adapter = new ArrayAdapter<Location>(requireContext(), android.R.layout.simple_spinner_item, locationList) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    // Customize how items are displayed in the spinner
                    TextView textView = (TextView) super.getView(position, convertView, parent);
                    textView.setText(getItem(position).getLocationName());
                    return textView;
                }

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    // Customize dropdown view
                    TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                    textView.setText(getItem(position).getLocationName());
                    return textView;
                }
            };
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            assetLocation.setAdapter(adapter);

            assetLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    selectedLocation = locationList.get(position);  // This gets the selected Employee object
                    checkFields();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Handle the case when nothing is selected (optional)
                }
            });
            preselectItems(asset);
        });
    }

    public void checkAndChangeIfViewMode(boolean isUpdateMode) {
        if(!isUpdateMode)  {
            assetName.setEnabled(false);
            assetDescription.setEnabled(false);
            assetBarcode.setEnabled(false);
            assetPrice.setEnabled(false);
            assetLocation.setEnabled(false);
            assetEmployee.setEnabled(false);
            assetDate.setEnabled(false);
            assetImage.setEnabled(false);
            mapContainer.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.GONE);
        }
    }

    private void saveData() {
        String newName = assetName.getText().toString();//promjeni u double
        String newDescription = assetDescription.getText().toString();
        long newBarcode = assetBarcode.getText().toString().isBlank() ? 0 : Long.parseLong(assetBarcode.getText().toString());
        double newPrice = assetPrice.getText().toString().isBlank() ? 0 : Double.parseDouble(assetPrice.getText().toString());
        String imagePath = ImageUtil.imagePath;
        long employeeId = selectedEmployee.getEmployeeId();
        long locationId = selectedLocation.getLocationId();
        String dateString = assetDate.getText().toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate createdAt = null;
        try {
            createdAt = LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }

        if (fixedAssetViewModel.getFixedAsset().getValue() == null && createdAt != null) {
            // Add new fixedAsset logic
            FixedAsset newFixedAsset = new FixedAsset(newName, newDescription, newBarcode, newPrice,
                    createdAt, employeeId, locationId, imagePath);
            fixedAssetViewModel.addFixedAsset(newFixedAsset);
        } else {
            // Update fixedAsset data in the ViewModel
            long id = fixedAssetViewModel.getFixedAsset().getValue().getAssetId();
            FixedAsset fixedAsset = new FixedAsset(id, newName, newDescription, newBarcode, newPrice,
                                createdAt, employeeId, locationId, imagePath);
            fixedAssetViewModel.updateFixedAsset(fixedAsset);

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
        assetName.addTextChangedListener(textWatcher);
        assetDescription.addTextChangedListener(textWatcher);
        assetBarcode.addTextChangedListener(textWatcher);
        assetPrice.addTextChangedListener(textWatcher);
        assetDate.addTextChangedListener(textWatcher);
    }

    private ImageUtil.ImageSaveCallback imageSaveCallback = new ImageUtil.ImageSaveCallback() {
        @Override
        public void onImageSaved(File imageFile) {
            // Update the image in the fragment on the main thread
            if (imageFile != null) {
                assetImage.setImageURI(Uri.fromFile(imageFile));
            }
        }
    };

    private void initLaunchers() {
       ImageUtil.galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        ImageUtil.createImageFileFromUri(requireContext(), selectedImageUri, imageSaveCallback);
                       // assetImage.setImageURI(ImageUtil.imageUri);
                    }
                }
        );

       ImageUtil.cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                       assetImage.setImageURI(ImageUtil.imageUri);
                    }
                }
        );

        BarcodeUtil.barcodeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        String scannedBarcode = result.getData().getStringExtra("SCAN_RESULT");
                        assetBarcode.setText(scannedBarcode);
                    }
                }
        );
    }


    private void preselectItems(FixedAsset fixedAsset) {
        if (employeeList != null && employeeList.size() > 1
                && locationList != null && locationList.size() > 1 && fixedAsset != null) {
            // Preselect items in the UI
            currEmployee = employeeList.stream().filter(emp -> emp.getEmployeeId() == fixedAsset.getEmployeeInChargeId()).findFirst().orElse(null);
            currLocation = locationList.stream().filter(loc -> loc.getLocationId() == fixedAsset.getOnLocationId()).findFirst().orElse(null);
            if(currEmployee != null && currLocation != null) {
                int employeePos = employeeList.indexOf(currEmployee);
                int locationPos = locationList.indexOf(currLocation);

                assetEmployee.setSelection(employeePos);
                assetLocation.setSelection(locationPos);

                updateMapWithCoordinates(currLocation.getLatitude(), currLocation.getLongitude());

            }

        }
    }

    private void checkFields() {
        String assetNameValue = assetName.getText().toString().trim();
        String assetDescriptionValue = assetDescription.getText().toString().trim();
        String assetBarcodeValue = assetBarcode.getText().toString().trim();
        String assetPriceValue = assetPrice.getText().toString().trim();
        String imagePath = ImageUtil.imagePath;
        String dateString = assetDate.getText().toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        boolean isNameValid = !assetNameValue.isEmpty(); // Add more checks if needed
        boolean isDescriptionValid = !assetDescriptionValue.isEmpty();
        boolean isPriceValid = !assetPriceValue.isEmpty() && assetPriceValue.matches("^-?\\d+(\\.\\d+)?$"); // Check for a valid number
        boolean isBarcodeValid = !assetBarcodeValue.isEmpty() && assetBarcodeValue.matches("\\d+");
        boolean isEmployeeValid = assetEmployee.getSelectedItemPosition() != 0;
        boolean isLocationValid = assetLocation.getSelectedItemPosition() != 0;
        boolean isImageValid = imagePath != null;
        boolean isDateValid = false;
        try {
            LocalDate localDate = LocalDate.parse(dateString, formatter);
            isDateValid = true;
        } catch (DateTimeParseException e) {
            isDateValid = false;
            e.printStackTrace();
        }

        saveButton.setEnabled(isNameValid && isDescriptionValid && isPriceValid && isBarcodeValid && isDateValid
        && isEmployeeValid && isLocationValid && isImageValid);
    }

    public void updateMapWithCoordinates(double lat, double lng) {
        if (googleMap != null) {
            // Move the camera to the new position
            LatLng location = new LatLng(lat, lng);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));  // Zoom level 15 for example

            // Optionally, add a marker at the new location
            googleMap.addMarker(new MarkerOptions().position(location).title("New Location"));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ImageUtil.imagePath = null;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        // Set a default location (e.g., New York City)
        LatLng defaultLocation = new LatLng(40.7128, -74.0060);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));

        if (currLocation != null) {
            updateMapWithCoordinates(currLocation.getLatitude(), currLocation.getLongitude());
        }

        googleMap.setOnMarkerClickListener(marker -> {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            Bundle bundle = new Bundle();
            bundle.putLong("locationId", currLocation.getLocationId());
            navController.navigate(R.id.action_assetDetailFragment_to_nav_asset, bundle);

            // Optionally, return true to consume the event and prevent the default behavior (e.g., opening an info window)
            return true;
        });
    }
}