package com.registar.ui.asset;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.registar.data.RegistarDatabase;
import com.registar.data.dao.EmployeeDao;
import com.registar.data.dao.FixedAssetDao;
import com.registar.data.dao.LocationDao;
import com.registar.data.model.Employee;
import com.registar.data.model.FixedAsset;
import com.registar.data.model.FixedAsset;
import com.registar.data.model.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AssetViewModel extends AndroidViewModel {
   private MutableLiveData<List<FixedAsset>> fixedAssets = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<Employee>> employees = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<Location>> locations = new MutableLiveData<>(new ArrayList<>());
   private MutableLiveData<FixedAsset> fixedAsset = new MutableLiveData<>();
   private List<FixedAsset> originalList = new ArrayList<>();
   private FixedAssetDao fixedAssetDao;
    private LocationDao locationDao;
    private EmployeeDao employeeDao;
    private long locationId = -1;
    private final MutableLiveData<Boolean> isUpdateMode = new MutableLiveData<>(false);
    private final MutableLiveData<String> title = new MutableLiveData<>();
    public AssetViewModel(@NonNull Application application) {
        super(application);
        RegistarDatabase db = RegistarDatabase.getInstance(application);
        fixedAssetDao = db.getFixedAssetDao();
        locationDao = db.getLocationDao();
        employeeDao = db.getEmployeeDao();

        fixedAssetDao.getFixedAssets().observeForever(new Observer<List<FixedAsset>>() {

            @Override
            public void onChanged(List<FixedAsset> fixedAssetList) {
                originalList = new ArrayList<>();
                originalList.addAll(fixedAssetList);
                fixedAssets.setValue(fixedAssetList);
                fixedAssetDao.getFixedAssets().removeObserver(this);
                System.out.println("list " + fixedAssetList.size() + " LOC " + locationId);
                if(locationId != -1)
                    filter("", "", locationId);
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
    }

    public LiveData<List<FixedAsset>> getAllFixedAssets() {
        return this.fixedAssets;
    }

    public LiveData<List<Location>> getAllLocations() {
        return this.locations;
    }

    public LiveData<List<Employee>> getAllEmployees() {
        return this.employees;
    }

    public void setUpdateMode(boolean value) {
        isUpdateMode.setValue(value);
    }

    public LiveData<Boolean> getUpdateMode() {
        return isUpdateMode;
    }

    public void setTitle(String value) {
        title.setValue(value);
    }

    public LiveData<String> getTitle() {
        return title;
    }

    public void deleteItem(FixedAsset fixedAsset) {
        // Perform delete in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            fixedAssetDao.deleteFixedAsset(fixedAsset);
            List<FixedAsset> currentList = fixedAssets.getValue();
            if (currentList != null) {
                originalList.remove(fixedAsset);
                currentList.remove(fixedAsset); // Remove from the current list
                fixedAssets.postValue(new ArrayList<>(currentList)); // Post the updated list

            }
        });
    }

    public void filter(String nameQuery, String barcodeQuery, long locationId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            if(!originalList.isEmpty()) {
                List<FixedAsset> filteredList = originalList.stream()
                        .filter(fixedAsset ->
                                fixedAsset.getAssetName().toLowerCase().startsWith(nameQuery.toLowerCase())
                                && String.valueOf(fixedAsset.getBarcode()).startsWith(barcodeQuery))
                        .collect(Collectors.toList());
                List<FixedAsset> filteredByLocation;
                if (locationId != -1)
                    filteredByLocation = filteredList.stream()
                            .filter(asset -> asset.getOnLocationId() == locationId)
                            .collect(Collectors.toList());
                else
                    filteredByLocation = filteredList;
                fixedAssets.postValue(filteredByLocation);
            }
        });
    }

    public LiveData<FixedAsset> getFixedAsset() {
        return fixedAsset;
    }

    public void setFixedAsset(FixedAsset fixedAsset) {
        this.fixedAsset.setValue(fixedAsset);
    }

    public void updateFixedAsset(FixedAsset fixedAsset) {
        Executors.newSingleThreadExecutor().execute(() -> {
            fixedAssetDao.updateFixedAsset(fixedAsset);
            this.fixedAsset.postValue(fixedAsset);
        });
    }

    public void addFixedAsset(FixedAsset newFixedAsset) {
        Executors.newSingleThreadExecutor().execute(() -> {
            fixedAssetDao.insertFixedAsset(newFixedAsset);
            //setFixedAsset(newFixedAsset);
            this.fixedAsset.postValue(newFixedAsset);
        });
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }
}