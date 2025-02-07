package com.registar.ui.inventory;

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
import com.registar.adapter.AssetRecordListAdapter;
import com.registar.data.model.AssetRecordList;

public class InventoryFragment extends Fragment implements AssetRecordListAdapter.OnAssetRecordListItemClick {

    private InventoryViewModel inventoryViewModel;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AssetRecordListAdapter assetRecordListRecordListAdapter;
    private EditText filterByName;
    private FloatingActionButton addAssetRecordList;

    public static InventoryFragment newInstance() {
        return new InventoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        inventoryViewModel =
                new ViewModelProvider(this).get(InventoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_inventory, container, false);

        filterByName = root.findViewById(R.id.text_filter_inventory_name);
        addAssetRecordList = root.findViewById(R.id.button_inventory_add);

        recyclerView = root.findViewById(R.id.recycler_inventory);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        assetRecordListRecordListAdapter = new AssetRecordListAdapter(this);
        recyclerView.setAdapter(assetRecordListRecordListAdapter);

        addAssetRecordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigate(null, true);
            }
        });

        inventoryViewModel.getAllAssetRecordLists().observe(getViewLifecycleOwner(), items -> {
            assetRecordListRecordListAdapter.submitList(items);
        });

        filterByName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filter(filterByName);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        return root;
    }

    @Override
    public void onAssetRecordListClick(int pos) {
        final AssetRecordList assetRecordList = assetRecordListRecordListAdapter.getItemAt(pos);
        new AlertDialog.Builder(requireContext())
                .setTitle( getString(R.string.select) )
                .setItems(new String[]{ getString(R.string.details), getString(R.string.update), getString(R.string.delete) }, (dialogInterface, i) -> {
                    switch (i) {
                        case OPTION_DETAILS:
                            navigate(assetRecordList, false);
                            break;
                        case OPTION_UPDATE:
                            navigate(assetRecordList, true);
                            break;
                        case OPTION_DELETE:
                            inventoryViewModel.deleteItem(assetRecordList);
                            break;
                    }
                }).show();
    }

    private void filter(EditText filterByName) {
        String nameQuery = filterByName.getText().toString();

        inventoryViewModel.filter(nameQuery);
    }

    private void navigate(AssetRecordList assetRecordList, boolean isUpdate) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        Bundle bundle = new Bundle();
        bundle.putSerializable("assetRecordList", assetRecordList);
        if (isUpdate) {
            navController.navigate(R.id.action_nav_inventory_to_inventoryUpdateFragment, bundle);
        } else {
            navController.navigate(R.id.action_nav_inventory_to_inventoryDetailFragment, bundle);
        }
    }
}