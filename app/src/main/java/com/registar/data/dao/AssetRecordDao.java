package com.registar.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.registar.data.model.AssetRecord;
import com.registar.data.model.AssetRecordWithDetails;
import com.registar.util.Constants;

import java.util.List;
@Dao
public interface AssetRecordDao {
    @Query("SELECT * FROM " + Constants.TABLE_NAME_ASSET_RECORD)
    LiveData<List<AssetRecord>> getAssetRecords();

    @Query("SELECT * FROM " + Constants.TABLE_NAME_ASSET_RECORD + " WHERE asset_record_list_id = :listId")
    LiveData<List<AssetRecordWithDetails>> getAssetRecordsWithDetails(long listId);

    @Query("SELECT * FROM " + Constants.TABLE_NAME_ASSET_RECORD + " WHERE record_id = :id")
    AssetRecord getAssetRecord(long id);

    @Query("SELECT * FROM " + Constants.TABLE_NAME_ASSET_RECORD + " WHERE record_id = :id")
    AssetRecordWithDetails getAssetRecordWithDetails(long id);


    /*
     * Insert the object in database
     * @param assetRecord, object to be inserted
     */
    @Insert
    long insertAssetRecord(AssetRecord assetRecord);

    /*
     * update the object in database
     * @param assetRecord, object to be updated
     */
    @Update
    void updateAssetRecord(AssetRecord repos);

    /*
     * delete the object from database
     * @param assetRecord, object to be deleted
     */
    @Delete
    void deleteAssetRecord(AssetRecord assetRecord);

    // AssetRecord... is varargs, here assetRecord is an array
    /*
     * delete list of objects from database
     * @param assetRecord, array of oject to be deleted
     */
    @Delete
    void deleteAssetRecords(AssetRecord... assetRecord);
}
