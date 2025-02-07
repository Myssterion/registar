package com.registar.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.registar.data.model.Location;
import com.registar.util.Constants;

@Dao
public interface LocationDao {
    @Query("SELECT * FROM " + Constants.TABLE_NAME_LOCATION)
    LiveData<List<Location>> getLocations();

    @Query("SELECT * FROM " + Constants.TABLE_NAME_LOCATION + " WHERE location_id = :id")
    Location getLocation(long id);

    /*
     * Insert the object in database
     * @param location, object to be inserted
     */
    @Insert
    long insertLocation(Location location);

    /*
     * update the object in database
     * @param location, object to be updated
     */
    @Update
    void updateLocation(Location repos);

    /*
     * delete the object from database
     * @param location, object to be deleted
     */
    @Delete
    void deleteLocation(Location location);

    // Location... is varargs, here location is an array
    /*
     * delete list of objects from database
     * @param location, array of oject to be deleted
     */
    @Delete
    void deleteLocations(Location... location);
}
