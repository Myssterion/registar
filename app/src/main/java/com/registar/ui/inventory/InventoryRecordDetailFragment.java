package com.registar.ui.inventory;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.registar.R;
import com.registar.data.model.AssetRecord;
import com.registar.data.model.AssetRecordList;
import com.registar.data.model.AssetRecordWithDetails;
import com.registar.data.model.Employee;
import com.registar.data.model.FixedAsset;
import com.registar.data.model.Location;
import com.registar.ui.asset.AssetViewModel;
import com.registar.util.BarcodeUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class InventoryRecordDetailFragment extends Fragment {

    private InventoryViewModel inventoryViewModel;
    private Spinner assetName;
    private Spinner assetPrevLocation;
    private Spinner assetPrevEmployee;
    private Spinner assetNextLocation;
    private Spinner assetNextEmployee;

    private Button scanButton;
    private Button saveButton;
    private boolean isAddingMode = false;
    private Employee selectedPrevEmployee;
    private Employee selectedNextEmployee;
    private Location selectedPrevLocation;
    private Location selectedNextLocation;
    private FixedAsset selectedAsset;
    private List<Location> locationList;
    private List<Employee> employeeList;
    private List<FixedAsset> assetList;
    private AssetRecordWithDetails assetRecordWithDetails;
    private String title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inventoryViewModel = new ViewModelProvider(this).get(InventoryViewModel.class);

        View root = inflater.inflate(R.layout.fragment_inventory_record_detail, container, false);
        assetName = root.findViewById(R.id.spinner_asset_record_asset);
        assetPrevEmployee = root.findViewById(R.id.spinner_asset_record_prev_employee);
        assetPrevLocation = root.findViewById(R.id.spinner_asset_record_prev_location);
        assetNextEmployee = root.findViewById(R.id.spinner_asset_record_next_employee);
        assetNextLocation = root.findViewById(R.id.spinner_asset_record_next_location);

        scanButton = root.findViewById(R.id.button_asset_record_scan);
        saveButton = root.findViewById(R.id.button_asset_record_save);
        saveButton.setEnabled(false);

        loadAssetSpinner();
        loadEmployeeSpinner();
        loadLocationSpinner();

        inventoryViewModel.getAssetRecord().observe(getViewLifecycleOwner(), assetRecord -> {
            if (assetRecord != null) {
                assetRecordWithDetails = assetRecord;
                preselectItems(assetRecordWithDetails);
            }
        });

        initLaunchers();

        saveButton.setOnClickListener(listener -> {
            saveData();
            resetData();
        });

        scanButton.setOnClickListener(listener -> {
            BarcodeUtil.scanBarcode(requireContext());
        });
        //scanButton

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(inventoryViewModel.getTitleOfRecord().getValue());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);
        if (getArguments() != null && getArguments().containsKey("isUpdateMode") && getArguments().containsKey("assetRecordWithDetails")
        && getArguments().containsKey("assetRecordList")) {
            boolean isUpdateMode = getArguments().getBoolean("isUpdateMode");
            inventoryViewModel.setUpdateMode(isUpdateMode);
            AssetRecordWithDetails assetRecordWithDetails = (AssetRecordWithDetails) getArguments().getSerializable("assetRecordWithDetails");
            AssetRecordList assetRecordList = (AssetRecordList) getArguments().getSerializable("assetRecordList");
            if (assetRecordList != null)
                inventoryViewModel.setAssetRecordList(assetRecordList);
            if (assetRecordWithDetails != null) {
                inventoryViewModel.setAssetRecord(assetRecordWithDetails);
                inventoryViewModel.setTitleOfRecord(getString(R.string.inventory_record_detail));
            } else {
                isAddingMode = true;
                inventoryViewModel.setTitleOfRecord(getString(R.string.inventory_record_add));
            }
        }

        checkAndChangeIfViewMode(Boolean.TRUE.equals(inventoryViewModel.getUpdateMode().getValue()));
    }

    public void checkAndChangeIfViewMode(boolean isUpdateMode) {
        if(!isUpdateMode)  {
            assetName.setEnabled(false);
            assetPrevLocation.setEnabled(false);
            assetPrevEmployee.setEnabled(false);
            assetNextLocation.setEnabled(false);
            assetNextEmployee.setEnabled(false);
            scanButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
        } else if(!isAddingMode) {
            assetName.setEnabled(false);
            assetPrevLocation.setEnabled(false);
            assetPrevEmployee.setEnabled(false);
            scanButton.setVisibility(View.GONE);
        }
    }

    private void saveData() {
        long assetId = selectedAsset.getAssetId();
        long assetRecordList = inventoryViewModel.getAssetRecordList().getValue().getListId();
        long prevEmployeeId = selectedPrevEmployee.getEmployeeId();
        long nextEmployeeId = selectedNextEmployee.getEmployeeId();
        long prevLocationId = selectedPrevLocation.getLocationId();
        long nextLocationId = selectedNextLocation.getLocationId();

        if (isAddingMode) {
            // Add new fixedAsset logic
            AssetRecord assetRecord = new AssetRecord(assetId, assetRecordList, prevEmployeeId, nextEmployeeId, prevLocationId, nextLocationId);
            inventoryViewModel.addAssetRecord(assetRecord);
        } else {
            // Update fixedAsset data in the ViewModel
            long id = inventoryViewModel.getAssetRecord().getValue().getAssetRecord().getRecordId();
            AssetRecord assetRecord = new AssetRecord(id, assetId, assetRecordList, prevEmployeeId, nextEmployeeId, prevLocationId, nextLocationId);
            inventoryViewModel.updateAssetRecord(assetRecord);

        }
    }

    private void resetData() {
        assetName.setSelection(0);
        assetPrevEmployee.setSelection(0);
        assetNextEmployee.setSelection(0);
        assetPrevLocation.setSelection(0);
        assetNextLocation.setSelection(0);
    }


    private void initLaunchers() {
        BarcodeUtil.barcodeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        String scannedBarcode = result.getData().getStringExtra("SCAN_RESULT");
                        long barcode = Long.parseLong(scannedBarcode);
                        FixedAsset asset = inventoryViewModel.getAsset(barcode);
                        setFields(asset);
                    }
                }
        );
    }

    private void checkFields() {

        boolean isNameValid = assetName.getSelectedItemPosition() != 0;
        boolean isPrevLocationValid = assetPrevLocation.getSelectedItemPosition() != 0;
        boolean isNextLocationValid = assetNextLocation.getSelectedItemPosition() != 0;
        boolean isNextEmployeeValid = assetNextEmployee.getSelectedItemPosition() != 0;
        boolean isPrevEmployeeValid = assetPrevEmployee.getSelectedItemPosition() != 0;

        // Enable or disable the button
        saveButton.setEnabled(isNameValid && isPrevLocationValid && isNextLocationValid && isNextEmployeeValid && isPrevEmployeeValid);
    }

    private void setFields(FixedAsset asset) {
        if(asset != null) {
            Employee prevEmployee = employeeList.stream().filter(employee -> employee.getEmployeeId() == asset.getEmployeeInChargeId()).findFirst().orElse(null);
            Location prevLocation = locationList.stream().filter(location -> location.getLocationId() == asset.getOnLocationId()).findFirst().orElse(null);
            if (prevEmployee != null && prevLocation != null) {
                this.assetName.setSelection(assetList.indexOf(asset));
                this.assetPrevEmployee.setSelection(employeeList.indexOf(prevEmployee));
                this.assetPrevLocation.setSelection(locationList.indexOf(prevLocation));
            }
        }//dodaj da ispise na nema asset sa datim barcodom
    }

    private void preselectItems(AssetRecordWithDetails assetRecordWithDetails) {
        if (assetList != null && assetList.size() > 1 && employeeList != null && employeeList.size() > 1
                && locationList != null && locationList.size() > 1 && assetRecordWithDetails != null) {
            // Preselect items in the UI
            assetName.setSelection(assetList.indexOf(assetRecordWithDetails.getAsset()));
            assetPrevEmployee.setSelection(employeeList.indexOf(assetRecordWithDetails.getPrevEmployee()));
            assetNextEmployee.setSelection(employeeList.indexOf(assetRecordWithDetails.getNewEmployee()));
            assetPrevLocation.setSelection(locationList.indexOf(assetRecordWithDetails.getPrevLocation()));
            assetNextLocation.setSelection(locationList.indexOf(assetRecordWithDetails.getNewLocation()));
        }
    }

    private void loadAssetSpinner() {
        inventoryViewModel.getAllFixedAssets().observe(getViewLifecycleOwner(), items -> {
            assetList = new ArrayList<>();
            assetList.add(new FixedAsset(getString(R.string.select_asset), "", 0, 0, null,0,0,""));  // Assuming Employee has a constructor like this

            // Add the rest of the employees
            assetList.addAll(items);

            ArrayAdapter<FixedAsset> adapter1 = new ArrayAdapter<FixedAsset>(requireContext(), android.R.layout.simple_spinner_item, assetList) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    // Customize how items are displayed in the spinner
                    TextView textView = (TextView) super.getView(position, convertView, parent);
                    textView.setText(getItem(position).getAssetName());
                    return textView;
                }

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    // Customize dropdown view
                    TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                    textView.setText(getItem(position).getAssetName());
                    return textView;
                }
            };
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            assetName.setAdapter(adapter1);

            assetName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    selectedAsset = assetList.get(position);
                    checkFields();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {}
            });
            preselectItems(assetRecordWithDetails);
        });
    }

    private void loadEmployeeSpinner() {
        inventoryViewModel.getAllEmployees().observe(getViewLifecycleOwner(), items -> {
            employeeList = new ArrayList<>();
            employeeList.add(new Employee(getString(R.string.select_employee), "", 0));  // Assuming Employee has a constructor like this

            // Add the rest of the employees
            employeeList.addAll(items);

            ArrayAdapter<Employee> adapter1 = new ArrayAdapter<Employee>(requireContext(), android.R.layout.simple_spinner_item, employeeList) {
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
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            assetPrevEmployee.setAdapter(adapter1);

            assetPrevEmployee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    selectedPrevEmployee = employeeList.get(position);  // This gets the selected Employee object
                    checkFields();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Handle the case when nothing is selected (optional)
                }
            });

            ArrayAdapter<Employee> adapter2 = new ArrayAdapter<Employee>(requireContext(), android.R.layout.simple_spinner_item, employeeList) {
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
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            assetNextEmployee.setAdapter(adapter2);

            assetNextEmployee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    selectedNextEmployee = employeeList.get(position);  // This gets the selected Employee object
                    checkFields();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Handle the case when nothing is selected (optional)
                }
            });
            preselectItems(assetRecordWithDetails);
        });

    }

    private void loadLocationSpinner() {
        inventoryViewModel.getAllLocations().observe(getViewLifecycleOwner(), items -> {
            locationList = new ArrayList<>();
            locationList.add(new Location(getString(R.string.select_location), "", 0.0, 0.0));

            locationList.addAll(items);
            preselectItems(assetRecordWithDetails);

            ArrayAdapter<Location> adapter1 = new ArrayAdapter<Location>(requireContext(), android.R.layout.simple_spinner_item, locationList) {
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
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            assetPrevLocation.setAdapter(adapter1);

            assetPrevLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    selectedPrevLocation = locationList.get(position);  // This gets the selected Employee object
                    checkFields();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Handle the case when nothing is selected (optional)
                }
            });

            ArrayAdapter<Location> adapter2 = new ArrayAdapter<Location>(requireContext(), android.R.layout.simple_spinner_item, locationList) {
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
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            assetNextLocation.setAdapter(adapter2);

            assetNextLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    selectedNextLocation = locationList.get(position);  // This gets the selected Employee object
                    checkFields();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Handle the case when nothing is selected (optional)
                }
            });
            preselectItems(assetRecordWithDetails);
        });
    }
}