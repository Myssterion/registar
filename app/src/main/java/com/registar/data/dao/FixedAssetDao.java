package com.registar.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.registar.data.model.FixedAsset;
import com.registar.util.Constants;

import java.util.List;
import java.util.Map;

@Dao
public interface FixedAssetDao {
    @Query("SELECT * FROM " + Constants.TABLE_NAME_FIXED_ASSET)
    LiveData<List<FixedAsset>> getFixedAssets();

   /* @Query("SELECT asset_id, asset_name FROM " + Constants.TABLE_NAME_FIXED_ASSET)
    LiveData<Map<Long, String>> getAssetIdNameMap();
*/
    @Query("SELECT * FROM " + Constants.TABLE_NAME_FIXED_ASSET + " WHERE asset_id = :id")
    FixedAsset getFixedAsset(long id);
    /*
     * Insert the object in database
     * @param fixedAsset, object to be inserted
     */
    @Insert
    long insertFixedAsset(FixedAsset fixedAsset);

    /*
     * update the object in database
     * @param fixedAsset, object to be updated
     */
    @Update
    void updateFixedAsset(FixedAsset repos);

    /*
     * delete the object from database
     * @param fixedAsset, object to be deleted
     */
    @Delete
    void deleteFixedAsset(FixedAsset fixedAsset);

    // FixedAsset... is varargs, here fixedAsset is an array
    /*
     * delete list of objects from database
     * @param fixedAsset, array of oject to be deleted
     */
    @Delete
    void deleteFixedAssets(FixedAsset... fixedAsset);
}
