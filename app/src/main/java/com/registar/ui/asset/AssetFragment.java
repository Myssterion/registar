package com.registar.ui.asset;

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
import com.registar.adapter.FixedAssetAdapter;
import com.registar.adapter.FixedAssetAdapter;
import com.registar.data.model.FixedAsset;
import com.registar.ui.recyclerview.Adapter;
import com.registar.ui.recyclerview.Item;

import java.util.ArrayList;
import java.util.List;

public class AssetFragment extends Fragment implements FixedAssetAdapter.OnFixedAssetItemClick{

    private AssetViewModel assetViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private FixedAssetAdapter fixedAssetAdapter;
    private EditText filterByName;
    private EditText filterByBarcode;
    private FloatingActionButton addFixedAsset;
    private List<Item> array = new ArrayList<Item>();
    private long locationId = -1;
    private boolean hasFiltered = false;

    public static AssetFragment newInstance() {
        return new AssetFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        assetViewModel =
                new ViewModelProvider(this).get(AssetViewModel.class);

        View root = inflater.inflate(R.layout.fragment_asset, container, false);

        filterByName = root.findViewById(R.id.text_filter_asset_name);
        filterByBarcode = root.findViewById(R.id.text_filter_asset_barcode);
        addFixedAsset = root.findViewById(R.id.button_asset_add);

        recyclerView = root.findViewById(R.id.recycler_asset);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        fixedAssetAdapter = new FixedAssetAdapter(this);
        recyclerView.setAdapter(fixedAssetAdapter);

        addFixedAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigate(null, true);
            }
        });

        filterByName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filter(filterByName, filterByBarcode, locationId);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        filterByBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filter(filterByName, filterByBarcode, locationId);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);
        if(getArguments() != null && getArguments().containsKey("locationId")) {
            locationId = getArguments().getLong("locationId");
            assetViewModel.setLocationId(locationId);
        }

        assetViewModel.getAllFixedAssets().observe(getViewLifecycleOwner(), items -> {
            //filter(filterByName, filterByBarcode, locationId);
            fixedAssetAdapter.submitList(items);
            if(locationId != -1 && !hasFiltered) {
                filter(filterByName, filterByBarcode, locationId);
                hasFiltered = true;
            }
        });
    }

    @Override
    public void onFixedAssetClick(int pos) {
        final FixedAsset asset = fixedAssetAdapter.getItemAt(pos);
        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.select))
                .setItems(new String[]{ getString(R.string.details), getString(R.string.update), getString(R.string.delete) }, (dialogInterface, i) -> {
                    switch (i) {
                        case OPTION_DETAILS:
                            navigate(asset, false);
                            break;
                        case OPTION_UPDATE:
                            navigate(asset, true);
                            break;
                        case OPTION_DELETE:
                            assetViewModel.deleteItem(asset);
                            break;
                    }
                }).show();
    }

    private void filter(EditText filterByName, EditText filterByBarcode, long locationId) {
        String nameQuery = filterByName.getText().toString();
        String barcodeQuery = filterByBarcode.getText().toString();

        assetViewModel.filter(nameQuery, barcodeQuery, locationId);
    }

    private void navigate(FixedAsset asset, boolean isUpdate) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isUpdateMode", isUpdate);
        bundle.putSerializable("asset", asset);
        navController.navigate(R.id.action_nav_asset_to_assetDetailFragment, bundle);
    }
}