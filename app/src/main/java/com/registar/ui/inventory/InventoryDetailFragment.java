package com.registar.ui.inventory;

import static com.registar.util.Constants.OPTION_DELETE;
import static com.registar.util.Constants.OPTION_DETAILS;
import static com.registar.util.Constants.OPTION_UPDATE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import com.registar.adapter.AssetRecordAdapter;
import com.registar.data.model.AssetRecordList;
import com.registar.data.model.AssetRecordWithDetails;
import com.registar.data.model.AssetRecordWithDetails;
import com.registar.data.model.FixedAsset;

public class InventoryDetailFragment extends Fragment implements AssetRecordAdapter.OnAssetRecordWithDetailsItemClick {

    private InventoryViewModel inventoryViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AssetRecordAdapter assetRecordRecordAdapter;
    private FloatingActionButton addAssetRecordWithDetails;
    private AssetRecordList assetRecordList = null;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inventoryViewModel =
                new ViewModelProvider(this).get(InventoryViewModel.class);

        View root = inflater.inflate(R.layout.fragment_inventory_detail, container, false);

        addAssetRecordWithDetails = root.findViewById(R.id.button_inventory_record_add);

        recyclerView = root.findViewById(R.id.recycler_inventory_records);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        assetRecordRecordAdapter = new AssetRecordAdapter(this);
        recyclerView.setAdapter(assetRecordRecordAdapter);

        addAssetRecordWithDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigate(null, true);
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.inventory_detail));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);
        if (getArguments() != null && getArguments().containsKey("assetRecordList")) {
            assetRecordList = (AssetRecordList) getArguments().getSerializable("assetRecordList");
            inventoryViewModel.setAssetRecordList(assetRecordList);
        }

        if (inventoryViewModel.getAssetRecordList().getValue() != null) {
            inventoryViewModel.getAllAssetRecordWithDetails(assetRecordList.getListId()).observe(getViewLifecycleOwner(), assetRecordRecordList -> {
                assetRecordRecordAdapter.submitList(assetRecordRecordList);
            });
        }
    }

    @Override
    public void onAssetRecordWithDetailsClick(int pos) {
        final AssetRecordWithDetails assetRecord = assetRecordRecordAdapter.getItemAt(pos);
        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.select))
                .setItems(new String[]{ getString(R.string.details), getString(R.string.delete) }, (dialogInterface, i) -> {
                    switch (i) {
                        case OPTION_DETAILS:
                            navigate(assetRecord, false);
                            break;
                        case OPTION_DELETE - 1:
                            inventoryViewModel.deleteAssetRecord(assetRecord.getAssetRecord());
                            break;
                    }
                }).show();
    }

    private void navigate(AssetRecordWithDetails assetRecord, boolean isUpdate) {
        if(assetRecordList != null) {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            Bundle bundle = new Bundle();
            bundle.putBoolean("isUpdateMode", isUpdate);
            bundle.putSerializable("assetRecordList", assetRecordList);
            bundle.putSerializable("assetRecordWithDetails", assetRecord);
            navController.navigate(R.id.action_inventoryDetailFragment_to_inventoryRecordDetailFragment, bundle);
        }
    }
}