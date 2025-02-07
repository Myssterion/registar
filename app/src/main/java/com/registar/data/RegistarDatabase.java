package com.registar.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.registar.data.dao.AssetListRecordsCrossRefDao;
import com.registar.data.dao.AssetRecordDao;
import com.registar.data.dao.AssetRecordListDao;
import com.registar.data.dao.EmployeeDao;
import com.registar.data.dao.FixedAssetDao;
import com.registar.data.dao.LocationDao;
import com.registar.data.model.AssetListRecordsCrossRef;
import com.registar.data.model.AssetRecord;
import com.registar.data.model.AssetRecordList;
import com.registar.data.model.Employee;
import com.registar.data.model.FixedAsset;
import com.registar.data.model.Location;
import com.registar.util.Constants;
import com.registar.util.DateRoomConverter;

@Database(entities = { FixedAsset.class, Location.class, Employee.class,
        AssetRecord.class, AssetRecordList.class, AssetListRecordsCrossRef.class}, version = 3)
@TypeConverters({DateRoomConverter.class})
public abstract class RegistarDatabase extends RoomDatabase {

    public abstract LocationDao getLocationDao();
    public abstract EmployeeDao getEmployeeDao();
    public abstract FixedAssetDao getFixedAssetDao();
    public abstract AssetRecordListDao getAssetRecordListDao();
    public abstract AssetListRecordsCrossRefDao getAssetListRecordsCrossRefDao();
    public abstract AssetRecordDao getAssetRecordDao();

    private static RegistarDatabase registarDatabase;

    public static RegistarDatabase getInstance(Context context) {
        if(registarDatabase == null)
            registarDatabase = buildDatabaseInstance(context);
        return registarDatabase;
    }

    private static RegistarDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                RegistarDatabase.class,
                Constants.DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public void cleanUp(){
        registarDatabase = null;
    }
}
