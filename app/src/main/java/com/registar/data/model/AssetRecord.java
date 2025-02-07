package com.registar.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.registar.util.Constants;

import java.io.Serializable;

@Entity(tableName = Constants.TABLE_NAME_ASSET_RECORD,
        foreignKeys = {
                @ForeignKey(entity = FixedAsset.class,
                        parentColumns = "asset_id",
                        childColumns = "asset_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = AssetRecordList.class,
                        parentColumns = "list_id",
                        childColumns = "asset_record_list_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Employee.class,
                        parentColumns = "employee_id",
                        childColumns = "prev_employee_in_charge_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Location.class,
                        parentColumns = "location_id",
                        childColumns = "prev_on_location_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Employee.class,
                        parentColumns = "employee_id",
                        childColumns = "new_employee_in_charge_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Location.class,
                        parentColumns = "location_id",
                        childColumns = "new_on_location_id",
                        onDelete = ForeignKey.CASCADE)
        })
public class AssetRecord implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "record_id")
    private long recordId;

    @ColumnInfo(name = "asset_id")
    private long asset;

    @ColumnInfo(name = "asset_record_list_id")
    private long assetRecordListId;
    @ColumnInfo(name = "prev_employee_in_charge_id")
    private long prevEmployeeInChargeId;
    @ColumnInfo(name = "prev_on_location_id")
    private long prevOnLocationId;
    @ColumnInfo(name = "new_employee_in_charge_id")
    private long newEmployeeInChargeId;
    @ColumnInfo(name = "new_on_location_id")
    private long newOnLocationId;

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public long getAsset() {
        return asset;
    }

    public void setAsset(long asset) {
        this.asset = asset;
    }

    public long getPrevEmployeeInChargeId() {
        return prevEmployeeInChargeId;
    }

    public void setPrevEmployeeInChargeId(long prevEmployeeInChargeId) {
        this.prevEmployeeInChargeId = prevEmployeeInChargeId;
    }

    public long getPrevOnLocationId() {
        return prevOnLocationId;
    }

    public void setPrevOnLocationId(long prevOnLocationId) {
        this.prevOnLocationId = prevOnLocationId;
    }

    public long getNewEmployeeInChargeId() {
        return newEmployeeInChargeId;
    }

    public void setNewEmployeeInChargeId(long newEmployeeInChargeId) {
        this.newEmployeeInChargeId = newEmployeeInChargeId;
    }

    public long getNewOnLocationId() {
        return newOnLocationId;
    }

    public void setNewOnLocationId(long newOnLocationId) {
        this.newOnLocationId = newOnLocationId;
    }

    public long getAssetRecordListId() {
        return assetRecordListId;
    }

    public void setAssetRecordListId(long assetRecordListId) {
        this.assetRecordListId = assetRecordListId;
    }

    public AssetRecord() {
    }

    @Ignore
    public AssetRecord(long asset, long assetRecordListId, long prevEmployeeInChargeId, long newEmployeeInChargeId, long prevOnLocationId, long newOnLocationId) {
        this.asset = asset;
        this.assetRecordListId = assetRecordListId;
        this.prevEmployeeInChargeId = prevEmployeeInChargeId;
        this.newEmployeeInChargeId = newEmployeeInChargeId;
        this.prevOnLocationId = prevOnLocationId;
        this.newOnLocationId = newOnLocationId;
    }
    @Ignore
    public AssetRecord(long recordId, long assetRecordListId, long asset, long prevEmployeeInChargeId, long prevOnLocationId, long newEmployeeInChargeId, long newOnLocationId) {
        this.recordId = recordId;
        this.asset = asset;
        this.assetRecordListId = assetRecordListId;
        this.prevEmployeeInChargeId = prevEmployeeInChargeId;
        this.prevOnLocationId = prevOnLocationId;
        this.newEmployeeInChargeId = newEmployeeInChargeId;
        this.newOnLocationId = newOnLocationId;
    }
}
