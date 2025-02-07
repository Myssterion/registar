package com.registar.ui.inventory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.registar.data.RegistarDatabase;
import com.registar.data.dao.AssetRecordDao;
import com.registar.data.dao.EmployeeDao;
import com.registar.data.dao.AssetRecordListDao;
import com.registar.data.dao.FixedAssetDao;
import com.registar.data.dao.LocationDao;
import com.registar.data.model.AssetRecord;
import com.registar.data.model.AssetRecordWithDetails;
import com.registar.data.model.Employee;
import com.registar.data.model.AssetRecordList;
import com.registar.data.model.FixedAsset;
import com.registar.data.model.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class InventoryViewModel extends AndroidViewModel {

    private MutableLiveData<List<AssetRecordList>> assetRecordLists = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<AssetRecordList> assetRecordList = new MutableLiveData<>();
    private MutableLiveData<List<AssetRecordWithDetails>> assetRecordWithDetails = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<AssetRecordWithDetails> assetRecord = new MutableLiveData<>();
    private MutableLiveData<List<Employee>> employees = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<Location>> locations = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<FixedAsset>> fixedAssets = new MutableLiveData<>(new ArrayList<>());
    private List<AssetRecordWithDetails> originalListOfRecords = new ArrayList<>();
    private List<AssetRecordList> originalList = new ArrayList<>();
    private AssetRecordListDao assetRecordListDao;
    private AssetRecordDao assetRecordDao;
    private LocationDao locationDao;
    private EmployeeDao employeeDao;
    private FixedAssetDao fixedAssetDao;
    private final MutableLiveData<String> titleOfUpdate = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isUpdateMode = new MutableLiveData<>(false);
    private final MutableLiveData<String> titleOfRecord = new MutableLiveData<>();
    public InventoryViewModel(@NonNull Application application) {
        super(application);
        RegistarDatabase db = RegistarDatabase.getInstance(application);
        assetRecordListDao = db.getAssetRecordListDao();
        assetRecordDao = db.getAssetRecordDao();
        fixedAssetDao = db.getFixedAssetDao();
        locationDao = db.getLocationDao();
        employeeDao = db.getEmployeeDao();

        assetRecordListDao.getAssetRecordLists().observeForever(new Observer<List<AssetRecordList>>() {

            @Override
            public void onChanged(List<AssetRecordList> assetRecordListList) {
                originalList = new ArrayList<>();
                originalList.addAll(assetRecordListList);
                assetRecordLists.setValue(assetRecordListList);
                assetRecordListDao.getAssetRecordLists().removeObserver(this);
            }
        });

        locationDao.getLocations().observeForever(new Observer<List<Location>>() {
            @Override
            public void onChanged(List<Location> locationList) {
                locations.setValue(locationList);
            }
        });

        employeeDao.getEmployees().observeForever(new Observer<List<Employee>>() {
            @Override
            public void onChanged(List<Employee> employeeList) {
                employees.setValue(employeeList);
            }
        });

        fixedAssetDao.getFixedAssets().observeForever(new Observer<List<FixedAsset>>() {
            @Override
            public void onChanged(List<FixedAsset> fixedAssetList) {
                fixedAssets.setValue(fixedAssetList);
            }
        });
    }

    public LiveData<List<AssetRecordList>> getAllAssetRecordLists() {
        return this.assetRecordLists;
    }

    public void setTitleOfUpdate(String value) {
        titleOfUpdate.setValue(value);
    }

    public LiveData<String> getTitleOfUpdate() {
        return titleOfUpdate;
    }

    public void setUpdateMode(boolean value) {
        isUpdateMode.setValue(value);
    }

    public LiveData<Boolean> getUpdateMode() {
        return isUpdateMode;
    }

    public void setTitleOfRecord(String value) {
        titleOfRecord.setValue(value);
    }

    public LiveData<String> getTitleOfRecord() {
        return titleOfRecord;
    }

    public LiveData<List<Location>> getAllLocations() {
        return this.locations;
    }

    public LiveData<List<Employee>> getAllEmployees() {
        return this.employees;
    }

    public LiveData<List<FixedAsset>> getAllFixedAssets() { return this.fixedAssets; }

    public void deleteItem(AssetRecordList assetRecordList) {
        // Perform delete in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            assetRecordListDao.deleteAssetRecordList(assetRecordList);
            List<AssetRecordList> currentList = assetRecordLists.getValue();
            if (currentList != null) {
                originalList.remove(assetRecordList);
                currentList.remove(assetRecordList); // Remove from the current list
                assetRecordLists.postValue(new ArrayList<>(currentList)); // Post the updated list
            }
        });
    }

    public void filter(String nameQuery) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<AssetRecordList> filteredList = originalList.stream()
                    .filter(assetRecordList ->
                            assetRecordList.getListName().toLowerCase().startsWith(nameQuery.toLowerCase()))
                    .collect(Collectors.toList());
            assetRecordLists.postValue(filteredList);
        });
    }

    public LiveData<AssetRecordList> getAssetRecordList() {
        return assetRecordList;
    }

    public void setAssetRecordList(AssetRecordList assetRecordList) {
        this.assetRecordList.setValue(assetRecordList);
    }

    public void updateAssetRecordList(AssetRecordList assetRecordList) {
        Executors.newSingleThreadExecutor().execute(() -> {
            assetRecordListDao.updateAssetRecordList(assetRecordList);
            this.assetRecordList.postValue(assetRecordList);
        });
    }

    public void addAssetRecordList(AssetRecordList newAssetRecordList) {
        Executors.newSingleThreadExecutor().execute(() -> {
            assetRecordListDao.insertAssetRecordList(newAssetRecordList);
            //setAssetRecordList(newAssetRecordList);
            this.assetRecordList.postValue(newAssetRecordList);
        });
    }

    //AssetRecord methods 

    public LiveData<List<AssetRecordWithDetails>> getAllAssetRecordWithDetails(long listId) {
            return assetRecordDao.getAssetRecordsWithDetails(listId);
    }
/*
    public void refreshAssetRecords(int listId) {
        // Explicitly reload the data (e.g., after changes)
        loadAssetRecordsWithDetails(listId);
    }

    private void loadAssetRecordsWithDetails(int listId) {
        // Use a background thread (e.g., Room, Coroutine, or Executor)
        Executors.newSingleThreadExecutor().execute(() -> {
            List<AssetRecordWithDetails> records = assetRecordDao.getAssetRecordsWithDetails(listId);
            assetRecordWithDetails.postValue(records); // Post to LiveData
        });
    }*/

    public void deleteAssetRecord(AssetRecord assetRecord) {
        Executors.newSingleThreadExecutor().execute(() -> {
            assetRecordDao.deleteAssetRecord(assetRecord);
        });
    }
    
    public LiveData<AssetRecordWithDetails> getAssetRecord() {
        return assetRecord;
    }

    public void setAssetRecord(AssetRecordWithDetails assetRecord) {
        this.assetRecord.setValue(assetRecord);
    }

    public void updateAssetRecord(AssetRecord assetRecord) {
        Executors.newSingleThreadExecutor().execute(() -> {
            assetRecordDao.updateAssetRecord(assetRecord);
            AssetRecordWithDetails assetRecordWithDetails = assetRecordDao.getAssetRecordWithDetails(assetRecord.getRecordId());
            this.assetRecord.postValue(assetRecordWithDetails);
        });
    }

    public void addAssetRecord(AssetRecord newAssetRecord) {
        Executors.newSingleThreadExecutor().execute(() -> {
            FixedAsset asset = fixedAssetDao.getFixedAsset(newAssetRecord.getAsset());
            asset.setEmployeeInChargeId(newAssetRecord.getNewEmployeeInChargeId());
            asset.setOnLocationId(newAssetRecord.getNewOnLocationId());
            fixedAssetDao.updateFixedAsset(asset);
            assetRecordDao.insertAssetRecord(newAssetRecord);
            AssetRecordWithDetails assetRecordWithDetails = assetRecordDao.getAssetRecordWithDetails(newAssetRecord.getRecordId());
            this.assetRecord.postValue(assetRecordWithDetails);
        });
    }

    public FixedAsset getAsset(long barcode) {
        Optional<FixedAsset> fixedAssetOptional = fixedAssets.getValue().stream()
                                            .filter(asset -> asset.getBarcode() == barcode).findFirst();
        return fixedAssetOptional.orElse(null);
    }
}