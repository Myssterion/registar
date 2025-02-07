package com.registar.ui.inventory;

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

import com.registar.R;
import com.registar.data.model.AssetRecordList;

public class InventoryUpdateFragment extends Fragment {

    private InventoryViewModel inventoryViewModel;
    private EditText inventoryName;
    private Button saveButton;
    private TextWatcher textWatcher;
    private boolean isAddingMode = false;
    private String title;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inventoryViewModel = new ViewModelProvider(this).get(InventoryViewModel.class);

        View root = inflater.inflate(R.layout.fragment_inventory_update, container, false);

        inventoryName = root.findViewById(R.id.text_inventory_name);
        saveButton = root.findViewById(R.id.button_inventory_save);
        saveButton.setEnabled(false);
        createTextWatcher();
        setTextWatcherToFields();

        inventoryViewModel.getAssetRecordList().observe(getViewLifecycleOwner(), inventory -> {
            if (inventory != null) {
                inventoryName.setText(inventory.getListName());
            }
        });

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
            activity.getSupportActionBar().setTitle(inventoryViewModel.getTitleOfUpdate().getValue());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);
        if (getArguments() != null && getArguments().containsKey("assetRecordList")) {
            AssetRecordList assetRecordList = (AssetRecordList) getArguments().getSerializable("assetRecordList");
            inventoryViewModel.setAssetRecordList(assetRecordList);
            if (assetRecordList != null) {
               inventoryViewModel.setTitleOfUpdate(getString(R.string.inventory_update_update));
            } else {
                inventoryViewModel.setTitleOfUpdate(getString(R.string.inventory_update_add));
            }
        }
    }

    private void saveData() {
        String newName = inventoryName.getText().toString();
        if (inventoryViewModel.getAssetRecordList().getValue() == null) {
            // Add new inventory logic
            AssetRecordList newAssetRecordList = new AssetRecordList(newName);
            inventoryViewModel.addAssetRecordList(newAssetRecordList);
        } else {
            // Update inventory data in the ViewModel
            long id = inventoryViewModel.getAssetRecordList().getValue().getListId();
            AssetRecordList inventory = new AssetRecordList(id, newName);
            inventoryViewModel.updateAssetRecordList(inventory);

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
        inventoryName.addTextChangedListener(textWatcher);
    }

    private void checkFields() {
        String inventoryNameValue = inventoryName.getText().toString().trim();

        boolean isNameValid = !inventoryNameValue.isEmpty(); // Add more checks if needed

        // Enable or disable the button
        saveButton.setEnabled(isNameValid);
    }
}