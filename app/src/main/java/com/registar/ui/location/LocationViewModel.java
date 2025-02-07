package com.registar.ui.location;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.registar.data.RegistarDatabase;
import com.registar.data.dao.LocationDao;
import com.registar.data.model.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class LocationViewModel extends AndroidViewModel {

    private MutableLiveData<List<Location>> locations = new MutableLiveData<>(new ArrayList<>());
    private List<Location> originalList = new ArrayList<>();

    private MutableLiveData<Location> location = new MutableLiveData<>();
    private LocationDao locationDao;
    private final MutableLiveData<Boolean> isUpdateMode = new MutableLiveData<>(false);
    private final MutableLiveData<String> title = new MutableLiveData<>();
    public LocationViewModel(@NonNull Application application) {
        super(application);
        RegistarDatabase db = RegistarDatabase.getInstance(application);
        locationDao = db.getLocationDao();
        locationDao.getLocations().observeForever(new Observer<List<Location>>() {
            @Override
            public void onChanged(List<Location> locationList) {
                originalList = new ArrayList<>();
                originalList.addAll(locationList);
                locations.setValue(locationList);
                locationDao.getLocations().removeObserver(this);
            }
        });
    }

    public LiveData<List<Location>> getAllLocations() {
        return this.locations;
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

    public void deleteItem(Location location) {
        // Perform delete in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            locationDao.deleteLocation(location);
            List<Location> currentList = locations.getValue();
            if (currentList != null) {
                originalList.remove(location);
                currentList.remove(location); // Remove from the current list
                locations.postValue(new ArrayList<>(currentList)); // Post the updated list
            }
        });
    }

    public void filter(String nameQuery, String addressQuery) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Location> filteredList = originalList.stream()
                    .filter(location ->
                            location.getLocationName().toLowerCase().startsWith(nameQuery.toLowerCase())
                            && location.getAddress().toLowerCase().startsWith(addressQuery.toLowerCase()))
                    .collect(Collectors.toList());
            locations.postValue(filteredList);
        });
    }

    public LiveData<Location> getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location.setValue(location);
    }

    public void updateLocation(Location location) {
        Executors.newSingleThreadExecutor().execute(() -> {
            locationDao.updateLocation(location);
            this.location.postValue(location);
        });
    }

    public void addLocation(Location newLocation) {
        Executors.newSingleThreadExecutor().execute(() -> {
            locationDao.insertLocation(newLocation);
            //setLocation(newLocation);
            this.location.postValue(newLocation);
        });
    }
}