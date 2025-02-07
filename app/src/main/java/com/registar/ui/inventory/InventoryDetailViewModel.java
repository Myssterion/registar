package com.registar.ui.inventory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.registar.data.RegistarDatabase;
import com.registar.data.dao.EmployeeDao;
import com.registar.data.dao.AssetRecordDao;
import com.registar.data.dao.FixedAssetDao;
import com.registar.data.dao.LocationDao;
import com.registar.data.model.AssetRecordWithDetails;
import com.registar.data.model.Employee;
import com.registar.data.model.AssetRecord;
import com.registar.data.model.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class InventoryDetailViewModel extends AndroidViewModel {

    private MutableLiveData<List<AssetRecordWithDetails>> assetRecords = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<AssetRecord> assetRecord = new MutableLiveData<>();
    private List<AssetRecordWithDetails> originalListOfRecordsOfRecords = new ArrayList<>();
    private AssetRecordDao assetRecordDao;
    private FixedAssetDao assetDao;

    public InventoryDetailViewModel(@NonNull Application application) {
        super(application);
        RegistarDatabase db = RegistarDatabase.getInstance(application);
        assetRecordDao = db.getAssetRecordDao();
        assetDao = db.getFixedAssetDao();

       /* assetRecordDao.getAssetRecordsWithDetails().observeForever(new Observer<List<AssetRecordWithDetails>>() {

            @Override
            public void onChanged(List<AssetRecordWithDetails> assetRecordList) {
                originalListOfRecords = new ArrayList<>(); System.out.println(assetRecordList);
                originalListOfRecords.addAll(assetRecordList);
                assetRecords.setValue(assetRecordList);
                assetRecordDao.getAssetRecords().removeObserver(this);
            }
        });*/

    }

    public LiveData<List<AssetRecordWithDetails>> getAllAssetRecordWithDetails() {
        return this.assetRecords;
    }


   /* public void deleteItem(AssetRecord assetRecord) {
        // Perform delete in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            assetRecordDao.deleteAssetRecord(assetRecord);
            List<AssetRecord> currentList = assetRecords.getValue();
            if (currentList != null) {
                originalListOfRecords.remove(assetRecord);
                currentList.remove(assetRecord); // Remove from the current list
                assetRecords.postValue(new ArrayList<>(currentList)); // Post the updated list
            }
        });
    }*/

   /* public void filter(String nameQuery) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<AssetRecord> filteredList = originalListOfRecords.stream()
                    .filter(assetRecord ->
                            assetRecord.getAssetName().toLowerCase().startsWith(nameQuery.toLowerCase()))
                    .collect(Collectors.toList());
            assetRecords.postValue(filteredList);
        });
    }
*/
    public LiveData<AssetRecord> getAssetRecord() {
        return assetRecord;
    }

    public void setAssetRecord(AssetRecord assetRecord) {
        this.assetRecord.setValue(assetRecord);
    }

    public void updateAssetRecord(AssetRecord assetRecord) {
        Executors.newSingleThreadExecutor().execute(() -> {
            assetRecordDao.updateAssetRecord(assetRecord);
            this.assetRecord.postValue(assetRecord);
        });
    }

    public void addAssetRecord(AssetRecord newAssetRecord) {
        Executors.newSingleThreadExecutor().execute(() -> {
            assetRecordDao.insertAssetRecord(newAssetRecord);
            //setAssetRecord(newAssetRecord);
            this.assetRecord.postValue(newAssetRecord);
        });
    }
}
