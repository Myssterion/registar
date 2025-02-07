package com.registar.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.registar.data.model.AssetListRecordsCrossRef;
import com.registar.util.Constants;

import java.util.List;
@Dao
public interface AssetListRecordsCrossRefDao {
    @Query("SELECT * FROM " + Constants.TABLE_NAME_ASSET_LIST_RECORDS)
    List<AssetListRecordsCrossRef> getAssetListRecordsCrossRefs();

    @Query("SELECT * FROM " + Constants.TABLE_NAME_ASSET_LIST_RECORDS + " WHERE asset_list_id = :id")
    List<AssetListRecordsCrossRef> getAssetListRecordsCrossRefsByListId(long id);

    /*
     * Insert the object in database
     * @param assetListRecordsCrossRef, object to be inserted
     */
    @Insert
    long insertAssetListRecordsCrossRef(AssetListRecordsCrossRef assetListRecordsCrossRef);

    /*
     * update the object in database
     * @param assetListRecordsCrossRef, object to be updated
     */
    @Update
    void updateAssetListRecordsCrossRef(AssetListRecordsCrossRef repos);

    /*
     * delete the object from database
     * @param assetListRecordsCrossRef, object to be deleted
     */
    @Delete
    void deleteAssetListRecordsCrossRef(AssetListRecordsCrossRef assetListRecordsCrossRef);

    // AssetListRecordsCrossRef... is varargs, here assetListRecordsCrossRef is an array
    /*
     * delete list of objects from database
     * @param assetListRecordsCrossRef, array of oject to be deleted
     */
    @Delete
    void deleteAssetListRecordsCrossRefs(AssetListRecordsCrossRef... assetListRecordsCrossRef);
}
