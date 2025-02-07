package com.registar.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.registar.data.model.AssetRecordList;
import com.registar.util.Constants;

import java.util.List;
@Dao
public interface AssetRecordListDao {
    @Query("SELECT * FROM " + Constants.TABLE_NAME_ASSET_RECORD_LIST)
    LiveData<List<AssetRecordList>> getAssetRecordLists();

    @Query("SELECT * FROM " + Constants.TABLE_NAME_ASSET_RECORD_LIST + " WHERE list_id = :id")
    AssetRecordList getAssetRecordList(long id);

    /*
     * Insert the object in database
     * @param assetRecordList, object to be inserted
     */
    @Insert
    long insertAssetRecordList(AssetRecordList assetRecordList);

    /*
     * update the object in database
     * @param assetRecordList, object to be updated
     */
    @Update
    void updateAssetRecordList(AssetRecordList repos);

    /*
     * delete the object from database
     * @param assetRecordList, object to be deleted
     */
    @Delete
    void deleteAssetRecordList(AssetRecordList assetRecordList);

    // AssetRecordList... is varargs, here assetRecordList is an array
    /*
     * delete list of objects from database
     * @param assetRecordList, array of oject to be deleted
     */
    @Delete
    void deleteAssetRecordLists(AssetRecordList... assetRecordList);
}
